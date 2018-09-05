package is.yuun.com.loopj.android.http.api;

public interface ProgressListener {
	// public abstract void onPercent(int val);
	public abstract void onProgress(long size, long total);
}
