package is.yuun.com.loopj.android.http.api;

import com.loopj.android.http.ResponseHandlerInterface;

public interface ProgressListener extends ResponseHandlerInterface {
	void onProgress(long size, long total);
}
