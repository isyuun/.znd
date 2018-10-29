package kr.keumyoung.mukin.fragment;

import android.os.Handler;

import kr.keumyoung.mukin.api.RequestModel;
import kr.keumyoung.mukin.data.request.SongHitRequest;
import kr.keumyoung.mukin.util.PreferenceKeys;

public class BaseFragment2 extends BaseFragment {
    Handler handler = new Handler();

    public final boolean post(Runnable r) {
        handler.removeCallbacks(r);
        return handler.post(r);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        handler.removeCallbacks(r);
        return handler.postDelayed(r, delayMillis);
    }

    protected  void addFavoriteSong(String userid, String songid) {
        RequestModel<SongHitRequest> model = new RequestModel<>(new SongHitRequest(userid, songid));
    }

    protected  void delFavoriteSong(String userid, String songid) {
        RequestModel<SongHitRequest> model = new RequestModel<>(new SongHitRequest(userid, songid));
    }
}
