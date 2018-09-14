package kr.keumyoung.karaoke.play;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnTimedTextListener;
import android.util.Log;

import kr.keumyoung.karaoke.api._Const;

/**
 * <p/>
 * <pre></pre>
 * <p/>
 *
 * @author isyoon
 * @version 1.0
 * @since 2015. 1. 27.
 */
class Listen2 implements _Const {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

	private String _toString() {

		return (BuildConfig.DEBUG ? __CLASSNAME__ : getClass().getSimpleName()) + '@' + Integer.toHexString(hashCode());
	}

	protected String getMethodName() {
		String name = Thread.currentThread().getStackTrace()[3].getMethodName();
		// int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
		// name = String.format("line:%d - %s() ", line, name);
		name += "() ";
		return name;
	}

	Context context;

	public Context getContext() {
		return context;
	}

	public Listen2(Context context) {
		super();
		this.context = context;
	}

	protected MediaPlayer m_mp = null;

	OnBufferingUpdateListener mOnBufferingUpdateListener;

	public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
		mOnBufferingUpdateListener = listener;
	}

	OnPreparedListener mOnPreparedListener;

	public void setOnPreparedListener(OnPreparedListener listener) {
		mOnPreparedListener = listener;
	}

	OnTimedTextListener mOnTimedTextListener;

	public void setOnTimedTextListener(OnTimedTextListener listener) {
		mOnTimedTextListener = listener;
	}

	OnCompletionListener mOnCompletionListener;

	public void setOnCompletionListener(OnCompletionListener listener) {
		mOnCompletionListener = listener;
	}

	OnInfoListener mOnInfoListener;

	public void setOnInfoListener(OnInfoListener listener) {
		mOnInfoListener = listener;
	}

	OnErrorListener mOnErrorListener;

	public void setOnErrorListener(OnErrorListener listener) {
		mOnErrorListener = listener;
	}

	String url;

	public void setPath(String url) {
		this.url = url;
	}

	@Deprecated
	protected void setFile(String path) {
		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName());
		// m_mp = new MediaPlayer();
		//
		// try {
		// File sourceFile = new File(load);
		// if (sourceFile.exists()) {
		// FileInputStream fs = new FileInputStream(sourceFile);
		// FileDescriptor fd = fs.getFD();
		// m_mp.setDataSource(fd);
		// fs.close();
		// m_mp.prepareAsync();
		// m_mp.setOnPreparedListener(new OnPreparedListener() {
		//
		// @Override
		// public void onPrepared(MediaPlayer mp) {
		//
		// if (BuildConfig.DEBUG) _LOG.d(_toString(), getMethodName() + mp);
		// m_mp.start();
		// if (mOnPreparedListener != null) {
		// mOnPreparedListener.onPrepared(mp);
		// }
		// }
		// });
		// // m_mp.setOnCompletionListener(onListenComplete);
		// m_mp.setOnCompletionListener(new OnCompletionListener() {
		//
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		//
		// if (BuildConfig.DEBUG) _LOG.d(_toString(), getMethodName() + mp);
		// if (mOnCompletionListener != null) {
		// mOnCompletionListener.onCompletion(mp);
		// }
		// }
		// });
		// m_mp.setOnErrorListener(new OnErrorListener() {
		//
		// @Override
		// public boolean onError(MediaPlayer mp, int what, int extra) {
		//
		// if (BuildConfig.DEBUG) _LOG.d(_toString(), getMethodName() + mp + "(" + what + ", " + extra + ")");
		// if (mOnErrorListener != null) {
		// mOnErrorListener.onError(mp, what, extra);
		// }
		// return false;
		// }
		// });
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	protected boolean load(String path) throws Exception {
		return false;
	}


	public void stop() {
		if (m_mp != null/* && isPlaying()*/) {
			m_mp.stop();
		}
	}

	public boolean isPlaying() {
		//if (BuildConfig.DEBUG) Log.i(_toString() + "MediaPlayer", getMethodName() + m_mp);
		if (m_mp != null && m_mp.isPlaying()) {
			return true;
		}
		return false;
	}

	public boolean isPausing() {
		//if (BuildConfig.DEBUG) Log.i(_toString() + "MediaPlayer", getMethodName() + m_mp);
		if (m_mp != null && !m_mp.isPlaying()) {
			return true;
		}
		return false;
	}

	protected void reset() {
		if (m_mp != null) {
			m_mp.setOnPreparedListener(null);
			m_mp.setOnCompletionListener(null);
			m_mp.setOnErrorListener(null);
			m_mp.reset();
		}
	}

	public void release() {
		if (m_mp != null) {
			m_mp.release();
			m_mp = null;
		}
	}

}