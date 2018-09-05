package kr.keumyoung.karaoke.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import kr.keumyoung.karaoke.play.BuildConfig;

class Download extends Thread implements _Const {
	private static final String CLASSNAME = "DONW";

	String mUrl, mFileName;
	int mType;
	Handler mHandler;
	public String newPath;

	public void setType(int type) {
		mType = type;
	}

	public void setUrl(String url) {
		mUrl = url;
	}

	public void setFileName(String fileName) {
		mFileName = fileName;
	}

	public Download(Handler h) {
		mHandler = h;
	}

	public void sendMessage(int state) {
		Bundle b = new Bundle();
		b.putInt("state", state);

		Message msg = mHandler.obtainMessage();
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	@Override
	public void run() {
		int fileType = mType;

		String sdpath = newPath;

		HttpClient downClient = new DefaultHttpClient();
		HttpGet testHttpGet = new HttpGet(mUrl);
		if (BuildConfig.DEBUG) Log.i(CLASSNAME, "httpget mUrl = " + mUrl);

		try {
			HttpResponse testResponse = downClient.execute(testHttpGet);
			if (BuildConfig.DEBUG) Log.i(CLASSNAME, "excute");

			HttpEntity downEntity = testResponse.getEntity();
			if (downEntity != null) {
				if (BuildConfig.DEBUG) Log.i(CLASSNAME, "entity not null");

				int BUFFER_SIZE = 1024 * 10;
				byte[] buffer = new byte[BUFFER_SIZE];

				InputStream testInputStream = null;
				testInputStream = downEntity.getContent();
				if (BuildConfig.DEBUG) Log.i(CLASSNAME, "getcontent");
				BufferedInputStream testInputBuf = new BufferedInputStream(testInputStream, BUFFER_SIZE);

				File file = null;
				file = new File(sdpath + File.separator + mFileName);
				file.createNewFile();
				if (BuildConfig.DEBUG) Log.i(CLASSNAME, "createnewfile");
				FileOutputStream testFileOutputStream = new FileOutputStream(file, false);
				BufferedOutputStream testOutputBuf = new BufferedOutputStream(testFileOutputStream, BUFFER_SIZE);

				int readSize = -1;

				while ((readSize = testInputBuf.read(buffer)) != -1) {
					if (BuildConfig.DEBUG) Log.i(CLASSNAME, "readSize = " + String.valueOf(readSize));
					testOutputBuf.write(buffer, 0, readSize);
				}

				switch (fileType) {
				case REQUEST_FILE_ARTIST_IMAGE:
					if (BuildConfig.DEBUG) Log.i(CLASSNAME, "_COMPLETE_ARTIST_IMAGE");
					sendMessage(COMPLETE_DOWN_ARTIST_IMAGE);
					break;
				case REQUEST_FILE_SONG:
					if (BuildConfig.DEBUG) Log.i(CLASSNAME, "_COMPLETE_SONG");
					sendMessage(COMPLETE_DOWN_SONG);
					break;
				case REQUEST_FILE_LISTEN:
					if (BuildConfig.DEBUG) Log.i(CLASSNAME, "_COMPLETE_LISTEN");
					sendMessage(COMPLETE_DOWN_LISTEN);
					break;
				case REQUEST_FILE_LISTEN_OTHER:
					if (BuildConfig.DEBUG) Log.i(CLASSNAME, "_COMPLETE_LISTEN_OTHER_DOWN");
					sendMessage(COMPLETE_DOWN_LISTEN_OTHER);
					break;
				}

				if (BuildConfig.DEBUG) Log.i(CLASSNAME, "write end");
				testOutputBuf.flush();

				testInputBuf.close();
				testFileOutputStream.close();
				testOutputBuf.close();
			}
		} catch (Exception e) {
			testHttpGet.abort();
			if (BuildConfig.DEBUG) Log.i(CLASSNAME, "execute fail");
		}
	}

}