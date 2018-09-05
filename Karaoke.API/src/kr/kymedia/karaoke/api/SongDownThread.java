package kr.kymedia.karaoke.api;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import kr.kymedia.karaoke._IKaraoke;
import kr.kymedia.karaoke.data.SongItem;

@Deprecated
public class SongDownThread extends Thread implements _IKaraoke {
	final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private SongItem mSongItem;

	private Handler mHandler;
	private Message mMsg = null;

	private int mState;
	private long mByteTotal = 0;
	private long mByteRead = 0;

	private int mErrorCode = 0;
	private String mErrorMessage = "";

	public SongDownThread(Handler h, SongItem songItem) {
		Log.d(__CLASSNAME__, "Handler : " + h.toString() + ", songItem" + songItem.toString());
		mHandler = h;
		mSongItem = songItem;
	}

	/**
	 * @return the mSongItem.url
	 */
	public String getSongDownURL() {
		return mSongItem.url;
	}

	/**
	 * @param url
	 *          the mSongItem.url to set
	 */
	public void setSongDownURL(String url) {
		this.mSongItem.url = url;
	}

	/**
	 * 
	 */
	public String getSongDownDst() {
		return mSongItem.dst;
	}

	/**
	 * 
	 * @param dst
	 */
	public void setSongDownDst(String dst) {
		this.mSongItem.dst = dst;
	}

	/**
	 * 
	 */
	public String getSongNumber() {
		return mSongItem.number;
	}

	/**
	 * 
	 * @param number
	 */
	// public void setSongNumber(String number) {
	// this.mSongItem.number = number;
	// }

	/**
	 * @return the mSongItem.title
	 */
	public String getSongTitle() {
		return mSongItem.title;
	}

	public int getRunState() {
		return mState;
	}

	protected void setRunState(int State) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {

			// InterruptedException은 오류로 전달하지 않는다.
			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
		}
		mState = State;
	}

	/**
	 * @see java.lang.Thread#interrupt()
	 */
	@Override
	public void interrupt() {

		boolean ret = isAlive();
		while (ret) {
			setRunState(STATE_SONG_DOWNLOAD_CANCEL);
			super.interrupt();
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			//
			// //Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			// }
			ret = isAlive();
		}
	}

	/**
	 * 정상인경우 state: STATE_SONG_DOWNLOAD_START = 0; STATE_SONG_DOWNLOAD_RUNNING =
	 * 1; STATE_SONG_DOWNLOAD_COMPLETE = 2; STATE_SONG_DOWNLOAD_CANCEL = 3;
	 * STATE_SONG_DOWNLOAD_ERROR = 4;
	 * 
	 * @param code
	 * @param message
	 * 
	 *          오류인경우 state: code != 0 message != ""
	 */
	public int sendMessage(int what, int code, String message) {

		mState = what;
		mErrorCode = code;
		mErrorMessage = message;

		Bundle b = new Bundle();
		b.putInt("State", mState);
		b.putLong("ByteRead", mByteRead);
		b.putLong("ByteTotal", mByteTotal);
		b.putInt("ErrorCode", mErrorCode);
		b.putString("ErrorMesage", mErrorMessage);

		mMsg = mHandler.obtainMessage();
		mMsg.what = mState;
		mMsg.setData(b);
		mHandler.sendMessage(mMsg);

		switch (what) {
		case STATE_SONG_DOWNLOAD_START:
			// Log.d("STATE_SONG_DOWNLOAD_START", "" +
			// mByteRead+File.separator+mByteTotal + ", "+mMsg);
			break;

		case STATE_SONG_DOWNLOAD_RUNNING:
			// Log.d("STATE_SONG_DOWNLOAD_RUNNING", "" +
			// mByteRead+File.separator+mByteTotal + ", "+mMsg);
			break;

		case STATE_SONG_DOWNLOAD_COMPLETE:
			// Log.d("STATE_SONG_DOWNLOAD_COMPLETE", "" +
			// mByteRead+File.separator+mByteTotal + ", "+mMsg);
			break;

		case STATE_SONG_DOWNLOAD_CANCEL:
			// Log.d("STATE_SONG_DOWNLOAD_CANCEL", "" +
			// mByteRead+File.separator+mByteTotal + ", "+mMsg);
			break;

		case STATE_SONG_DOWNLOAD_ERROR:
			// Log.d("STATE_SONG_DOWNLOAD_ERROR", ""+message+", "+mMsg);
			break;

		default:
			break;
		}

		return code;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	@Override
	public void run() {
		sendMessage(STATE_SONG_DOWNLOAD_START, 0, "");

		long byteTotal = 0;

		mByteRead = 0;
		mByteTotal = 0;

		if (mSongItem.url == null || mSongItem.url.equals("")) {
			mSongItem.url = SKYMDOWN_URL + mSongItem.number;
		}

		mSongItem.dst = SKYM_PATH + File.separator + mSongItem.number + ".skym";

		// Log.d(__CLASSNAME__, "HttpGet [" + mSongItem.url + ", " + mSongItem.dst);

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(mSongItem.url);
		HttpResponse res = null;
		HttpEntity entity = null;

		try {
			sendMessage(STATE_SONG_DOWNLOAD_RUNNING, 0, "");

			res = client.execute(get);
			Log.e(__CLASSNAME__, res.getStatusLine().toString());

			Header[] headers = res.getAllHeaders();
			for (Header h : headers) {
				Log.e(__CLASSNAME__, h.getName() + " : " + h.getValue());
				if (("Content-Length").equalsIgnoreCase(h.getName())) {
					try {
						mByteTotal = Long.parseLong(h.getValue());
					} catch (NumberFormatException e) {

						// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
						mByteRead = 4 * 1024 * 1024;
					}
				}
			}

			// HTTP-CODE
			if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				sendMessage(STATE_SONG_DOWNLOAD_ERROR, res.getStatusLine().getStatusCode(), res
						.getStatusLine().toString());
				return;
			}

			entity = res.getEntity();

			byteTotal = entity.getContentLength();
			// CONTENT_LENGHT정보가 없는경우
			if (mByteTotal == 0 && byteTotal < 0) {
				mByteTotal = 4 * 1024 * 1024;
				Log.e(__CLASSNAME__, "Content-Length : " + byteTotal);
			}

		} catch (ClientProtocolException e) {

			sendMessage(STATE_SONG_DOWNLOAD_ERROR, e.hashCode(), Log.getStackTraceString(e));
			return;
		} catch (IOException e) {

			sendMessage(STATE_SONG_DOWNLOAD_ERROR, e.hashCode(), Log.getStackTraceString(e));
			return;
		}

		InputStream in = null;
		try {
			in = entity.getContent();
		} catch (IllegalStateException e) {

			sendMessage(STATE_SONG_DOWNLOAD_ERROR, e.hashCode(), Log.getStackTraceString(e));
			return;
		} catch (IOException e) {

			sendMessage(STATE_SONG_DOWNLOAD_ERROR, e.hashCode(), Log.getStackTraceString(e));
			return;
		}

		int BLOCK_SIZE = 1024;
		byte[] buf = new byte[BLOCK_SIZE];

		int count = 0;
		int UPDATE_COUNT = 20;

		File dir = null;
		File f = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			dir = new File(SKYM_PATH);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			f = new File(mSongItem.dst);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			fos = new FileOutputStream(f);
			bos = new BufferedOutputStream(fos, 8192);
		} catch (IOException e) {

			sendMessage(STATE_SONG_DOWNLOAD_ERROR, e.hashCode(), Log.getStackTraceString(e));
			return;
		}

		try {
			int l = 0;
			while (mState == STATE_SONG_DOWNLOAD_RUNNING && (l = in.read(buf)) != -1) {
				mByteRead += l;

				// CONTENT_LENGHT정보가 없는경우
				if (byteTotal < 0 && mByteRead > mByteTotal) {
					mByteTotal = mByteRead + 1024;
				}

				bos.write(buf, 0, l);

				if (count % UPDATE_COUNT == 0) {
					Thread.sleep(100);
					sendMessage(STATE_SONG_DOWNLOAD_RUNNING, 0, "");
				}

				count++;
			}

			bos.flush();

			if (fos != null)
				fos.close();
			if (bos != null)
				bos.close();

		} catch (IOException e) {

			sendMessage(STATE_SONG_DOWNLOAD_ERROR, e.hashCode(), Log.getStackTraceString(e));
			return;
		} catch (InterruptedException e) {

			// InterruptedException은 오류로 전달하지 않는다.
			// Log.e(__CLASSNAME__, Log.getStackTraceString(e));
			return;
		}

		// CONTENT_LENGHT정보가 없는경우
		if (mState != STATE_SONG_DOWNLOAD_CANCEL && byteTotal < 0) {
			mByteTotal = mByteRead;
			sendMessage(STATE_SONG_DOWNLOAD_RUNNING, 0, "");
		}

		switch (mState) {
		case STATE_SONG_DOWNLOAD_CANCEL:
			sendMessage(STATE_SONG_DOWNLOAD_CANCEL, 0, "");
			break;

		case STATE_SONG_DOWNLOAD_ERROR:
			sendMessage(STATE_SONG_DOWNLOAD_ERROR, 0, "UNKNOWN");
			break;

		default:
			if (mByteTotal > 0) {
				if (mByteTotal == mByteRead) {
					sendMessage(STATE_SONG_DOWNLOAD_COMPLETE, 0, "");
				} else {
					sendMessage(STATE_SONG_DOWNLOAD_ERROR, -1, "FILEERROR(" + mByteRead + "/" + mByteTotal
							+ ")");
				}
			} else {
				mByteTotal = mByteRead;
				sendMessage(STATE_SONG_DOWNLOAD_COMPLETE, 0, "");
			}
			break;
		}

	}
}
