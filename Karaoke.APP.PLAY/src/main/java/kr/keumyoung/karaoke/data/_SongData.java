package kr.keumyoung.karaoke.data;

import android.util.Log;

import kr.keumyoung.karaoke.play.BuildConfig;

public class _SongData extends SongData {
	private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

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
		Log.w(__CLASSNAME__, "load() " + "[ST]" + ret);
		if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, getMethodName() + name);
		ret = super.load(name);
		Log.w(__CLASSNAME__, "load() " + "[ED]" + ret);
		return ret;
	}

}
