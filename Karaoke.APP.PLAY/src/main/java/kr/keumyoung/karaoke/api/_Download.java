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
 * 2015 All rights (c)KYGroup Co.,Ltd. reserved.
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.TV
 * filename	:	DownLoad2.java
 * author	:	isyoon
 *
 * <pre>
 * kr.kymedia.kykaraoke.tv
 *    |_ DownLoad2.java
 * </pre>
 * 
 */

package kr.keumyoung.karaoke.api;

import android.os.Handler;

import kr.kymedia.karaoke.api.LyricsUtil;
import kr.kymedia.karaoke.play.impl.ISongPlay;

/**
 *
 *
 * <pre>
 * 가사다운로드기능추가
 * 
 * </pre>
 * 
 * Copy of {@link Download}
 * 
 * @see LyricsUtil
 * @author isyoon
 * @since 2015. 1. 27.
 * @version 1.0
 */
public class _Download extends Download2 {

	public _Download(Handler h) {
		super(h);
	}

	/**
	 * <pre>
	 * 다운로드리스너
	 * </pre>
	 *
	 */
	public interface onDownloadListener {
		// public abstract void onError(Exception e);
		public abstract void onDownError(final ISongPlay.ERROR t, final Exception e);

		public abstract void onDownRetry(final int count);

		public abstract void onDownTimeout(final long timeout);

	}

}
