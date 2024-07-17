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
 * 
 * This software is the confidential and proprietary information
 *  of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * 
 * project	:	Karaoke.PLAY4
 * filename	:	PlayActivity.java
 * author	:	isyoon
 *
 * <pre>
 * com.example.soundtouch_test
 *    |_ PlayActivity.java
 * </pre>
 * 
 */

package kr.kymedia.karaoke.play.apps.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lamerman.isyoon.SelectionMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kr.kymedia.karaoke.play.app.BuildConfig;
import kr.kymedia.karaoke.play.app.R;
import kr.kymedia.karaoke.play.app.view.PlayView;
import kr.kymedia.karaoke.play.app.view._PlayView;
import kr.kymedia.karaoke.play.impl.ISongPlay.TYPE;
import kr.kymedia.karaoke.util.BuildUtils;
import kr.kymedia.karaoke.util._Log;
import kr.kymedia.karaoke.util.TextUtil;
import kr.kymedia.karaoke.widget.util.WidgetUtils;

public class MainActivity extends _Activity implements OnClickListener, OnLongClickListener {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	@Override
	public String toString() {

		// return super.toString();
		return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
	}

	@Override
	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		_Log.i(__CLASSNAME__, getMethodName());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		((EditText) findViewById(R.id.editSong)).setText("8888");

		((Button) findViewById(R.id.open)).setOnLongClickListener(this);

		Intent intent = getIntent();

		_Log.i(__CLASSNAME__, getMethodName() + PlayView.type + ":" + intent);

		openIntent(intent);

		if (PlayView.type == TYPE.AUDIOTRACKPLAY) {
			((Checkable) findViewById(R.id.radioPlayTypeA)).setChecked(true);
		} else if (_PlayView.type == TYPE.MEDIAPLAYERPLAY) {
			((Checkable) findViewById(R.id.radioPlayTypeM)).setChecked(true);
		} else if (_PlayView.type == TYPE.SOUNDTOUCHPLAY) {
			((Checkable) findViewById(R.id.radioPlayTypeS)).setChecked(true);
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			(findViewById(R.id.radioPlayTypeA)).setEnabled(false);
			((Checkable) findViewById(R.id.radioPlayTypeM)).setChecked(true);
			(findViewById(R.id.radioPlayTypeS)).setEnabled(false);
		}

		startVersion();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// _Log.i(__CLASSNAME__, getMethodName());


		super.onNewIntent(intent);
		_Log.i(__CLASSNAME__, getMethodName() + intent);

		openIntent(intent);
	}

	@Override
	public void onClick(View v) {

		_Log.e(__CLASSNAME__, getMethodName() + v);
		if (v.getId() == R.id.open) {
			open(null);
		}
	}

	private void open(@Nullable String path) {
		Bundle extras = new Bundle();

		if (!TextUtil.isEmpty(path)) {
			Uri uri = Uri.parse(path);
			extras.putParcelable("pathPlay", uri);
			_Log.e(__CLASSNAME__, getMethodName() + extras + " - Uri[" + uri + "]");
		}
		extras.putString("editSong", ((EditText) findViewById(R.id.editSong)).getText().toString());
		extras.putInt("radioServer",
				((RadioGroup) findViewById(R.id.radioServer)).getCheckedRadioButtonId());
		extras.putInt("radioPlayType",
				((RadioGroup) findViewById(R.id.radioPlayType)).getCheckedRadioButtonId());

		openPlay(extras, null);
	}

	@Override
	public boolean onLongClick(View v) {

		// _Log.e(__CLASSNAME__, getMethodName() + v);
		if (v.getId() == R.id.open) {
			// openPlay(null, null);
			openTest();
		}
		return false;
	}

	// String paths[] = {
	// "http://resource.kymedia.kr/record/kpop/20120518/53/120518T5K42KH53.m4a",
	// "http://resource.kymedia.kr/record/kpop/20130508/26/130508328CG3326.m4a",
	// Environment.getExternalStorageDirectory() + "/Music/Let it Go.mp3",
	// };
	@SuppressLint({ "InlinedApi", "NewApi" })
	void openTest() {
		_Log.e(__CLASSNAME__, getMethodName());

		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("Sing a Happy Song");
		builder.setItems(R.array.songs, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// the user clicked on colors[which]
				int index = which;
				if (index == 0) {
					String start = Environment.getExternalStorageDirectory() + "/Music";
					openFileDialog(SelectionMode.MODE_OPEN_SINGLE, MainActivity.this, start);
				} else {
					String paths[] = getResources().getStringArray(R.array.paths);
					if (index < paths.length) {
						path = paths[index];
					}
					if (!TextUtil.isNetworkUrl(path)) {
						String root = Environment.getExternalStorageDirectory() + "/Music";
						path = getFilePath(root, path);
					}
					open(path);
				}
			}
		});
		builder.show();
	}

	@Override
	public void onFileDialogResult(int requestCode, int resultCode, Intent data) {

		super.onFileDialogResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			open(path);
		}

	}

	/**
	 * 버전정보
	 */
	protected void showVersion() {
		// if (BuildConfig.DEBUG) _Log.d(__CLASSNAME__, getMethodName());
		findViewById(R.id.version).setVisibility(View.VISIBLE);
	}

	/**
	 * 버전정보
	 */
	protected void hideVersion() {
		// if (BuildConfig.DEBUG) _Log.d(__CLASSNAME__, getMethodName());
		findViewById(R.id.version).setVisibility(View.INVISIBLE);
	}

	/**
	 * 버전정보
	 */
	protected void startVersion() {
		// if (BuildConfig.DEBUG) _Log.d(__CLASSNAME__, getMethodName());

		try {
			post(new Runnable() {

				@Override
				public void run() {

					try {
						showVersion();

						// showCBT();

						PackageInfo pkgInfo = BuildUtils.getPackageInfo(getApplicationContext());
						if (pkgInfo != null) {
							int versionNumber = pkgInfo.versionCode;
							String versionName = pkgInfo.versionName;
							String version = getString(R.string.app_version);
							version += ":" + versionName + "(" + versionNumber + ")";
							if (BuildConfig.DEBUG) {
								version += ":" + "DEBUG";
							}
							// String installDate = DateUtils.getDate("yyyy/MM/dd hh:mm:ss", pkgInfo.lastUpdateTime);
							//String buildDate = BuildUtils.getDate("yyyy/MM/dd HH:mm", BuildUtils.getBuildDate(getApplicationContext()));
							String buildDate = (new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)).format(new Date(BuildConfig.TIMESTAMP/* + TimeZone.getTimeZone("Asia/Seoul").getRawOffset()*/));
							version += "-" + buildDate;
							TextView tv = (TextView) findViewById(R.id.version);
							if (tv != null) {
								tv.setText(version);
								tv.setSingleLine();
								tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
								WidgetUtils.setTextViewMarquee(tv);
							}
						}
					} catch (Exception e) {

						e.printStackTrace();
					}
				}
			});

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
