package kr.keumyoung.karaoke.data;

import android.util.Log;

import kr.keumyoung.karaoke.play.BuildConfig;

public class _SongData extends SongData {
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

	public _SongData() {
		super();
	}

	@Override
	public boolean load(String name) {

		boolean ret = false;
		Log.w(_toString(), "load() " + "[ST]" + ret);
		if (BuildConfig.DEBUG) Log.i(_toString(), getMethodName() + name);
		ret = super.load(name);
		Log.w(_toString(), "load() " + "[ED]" + ret);
		return ret;
	}

}
