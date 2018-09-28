/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2014 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	Karaoke.PLAY4
 * filename	:	SongPlayCast.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke.play4
 *    |_ SongPlayCast.java
 * </pre>
 */

package kr.kymedia.karaoke.play;

import android.util.Log;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import fi.iki.elonen.isyoon.LocalMp3Server;
import kr.kymedia.karaoke.play.impl.ISongPlayCast;
import kr.kymedia.karaoke.util.EnvironmentUtils;
import kr.kymedia.karaoke.util.TextUtil;

/**
 *
 *
 * <pre></pre>
 *
 * @author isyoon
 * @since 2014. 10. 6.
 * @version 1.0
 */
public class SongPlayCast extends SongPlay implements ISongPlayCast {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	protected static String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	// public SongPlayCast(String name) {
	// super(name);
	// // TODO Auto-generated constructor stub
	// }

	private String url;
	private final String ip = EnvironmentUtils.getIpAddress();
	// private final String port = EnvironmentUtils.getLocalPort();
	private final String port = ":8089";

	protected Tag tag;

	Tag parseMP3Tag(String path) {
		if (TextUtil.isNetworkUrl(path)) {
			return null;
		}
		// Log.e(__CLASSNAME__, getMethodName() + "[MP3Info]");
		try {
			File file = new File(path);
			TagOptionSingleton.getInstance().setAndroid(true);
			AudioFile f = AudioFileIO.read(file);
			Tag tag = f.getTag();

			Log.e(__CLASSNAME__, getMethodName() + "[MP3Info]");
			Log.i("[MP3Info]ARTIST:", tag.getFirst(FieldKey.ARTIST));
			Log.i("[MP3Info]ALBUM:", tag.getFirst(FieldKey.ALBUM));
			Log.i("[MP3Info]TITLE:", tag.getFirst(FieldKey.TITLE));
			Log.i("[MP3Info]COMMENT:", tag.getFirst(FieldKey.COMMENT));
			Log.i("[MP3Info]YEAR:", tag.getFirst(FieldKey.YEAR));
			Log.i("[MP3Info]TRACK:", tag.getFirst(FieldKey.TRACK));
			Log.i("[MP3Info]DISC_NO:", tag.getFirst(FieldKey.DISC_NO));
			Log.i("[MP3Info]COMPOSER:", tag.getFirst(FieldKey.COMPOSER));
			Log.i("[MP3Info]ARTIST_SORT:", tag.getFirst(FieldKey.ARTIST_SORT));

			Iterator<TagField> itr = tag.getFields();
			while (itr.hasNext()) {
				Log.w("[MP3Field]", itr.next().toString());
			}

			// f.setTag(new ID3v23Tag());
			// tag.setField(FieldKey.ARTIST, artist);
			// tag.setField(FieldKey.ALBUM, album);
			// Artwork cover = ArtworkFactory.createArtworkFromFile(cover_file);
			// tag.setField(cover);
			// f.commit();

			return tag;
		} catch (Exception e) {
			Log.e(__CLASSNAME__, getMethodName() + "\n" + Log.getStackTraceString(e));
			return null;
		}
	}

	private LocalMp3Server mLocalMp3Server;

	@Override
	public void setPath(String path) {
		Log.e(__CLASSNAME__, getMethodName() + "path:" + path);
		super.setPath(path);

		if (TextUtil.isNetworkUrl(path)) {
			this.url = path;
		} else {
			tag = parseMP3Tag(path);

			if (mLocalMp3Server == null) {
				mLocalMp3Server = new LocalMp3Server(path);
			} else {
				mLocalMp3Server.setPath(path);
			}

			try {
				if (mLocalMp3Server != null && !mLocalMp3Server.wasStarted()) {
					mLocalMp3Server.start();
				}
			} catch (IOException e) {

				e.printStackTrace();
				return;
			}

			this.url = "http://" + ip + port + "/nnnn.mp3";
		}
		Log.e(__CLASSNAME__, getMethodName() + "url:" + url);

	}

	@Override
	public String getUrl() {

		String path = getPath();

		// if (mLocalMp3Server != null && mLocalMp3Server.wasStarted()) {
		// if (!TextUtil.isNetworkUrl(path)) {
		// path = url;
		// }
		// } else {
		// path = url;
		// }

		path = url;

		Log.e(__CLASSNAME__, getMethodName() + path);
		return path;
	}

	public void onStop() {
		Log.e(__CLASSNAME__, getMethodName() + mLocalMp3Server);


		try {
			stop();
			if (mLocalMp3Server != null) {
				if (mLocalMp3Server.wasStarted()) {
					mLocalMp3Server.stop();
				}
				mLocalMp3Server = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
