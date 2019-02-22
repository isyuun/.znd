package kr.keumyoung.mukin.fragment;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.Songs;

public class BaseFragment2 extends BaseFragment {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    Handler handler = new Handler();
    /**
     * <a href="https://stackoverflow.com/questions/13560243/how-to-stop-runnable-when-the-app-goes-to-background">
     * How to stop runnable when the app goes to background?
     * </a>
     */
    private Runnable running = null;
    private void clearRunning() {
        this.running = null;
    }

    public final void removeCallbacks(Runnable r)
    {
        handler.removeCallbacks(r);
    }

    /**
     * 일단 {@link #postDelayed(Runnable, long)}에서만
     */
    public final boolean post(Runnable r) {
        //this.running = r;
        removeCallbacks(r);
        return handler.post(r);
    }

    /**
     * 일단 {@link #postDelayed(Runnable, long)}에서만
     */
    public final boolean postDelayed(Runnable r, long delayMillis) {
        this.running = r;
        removeCallbacks(r);
        return handler.postDelayed(r, delayMillis);
    }

    public View findViewById(int id) {
        return getActivity().findViewById(id);
    }

    _BaseListFragment currentListFragment;

    public _BaseListFragment getChildCurrentFragment() {
        return currentListFragment;
    }

    public void onBackPressed() {
    }

    public void refresh() {
    }
}
