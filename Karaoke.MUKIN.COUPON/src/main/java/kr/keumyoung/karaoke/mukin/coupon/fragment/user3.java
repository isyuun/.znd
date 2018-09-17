package kr.keumyoung.karaoke.mukin.coupon.fragment;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import kr.keumyoung.karaoke.mukin.coupon.app._Application;

public class user3 extends user2 {

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return getActivity().getSharedPreferences(name, mode);
    }

    public String getPackageName() {
        return getActivity().getPackageName();
    }

    public _Application getApplication() {
        return (_Application) getActivity().getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        String sdate = getApplication().sdate;
        String edate = getApplication().edate;
        SimpleDateFormat f1 = getApplication().f1;
        //SimpleDateFormat f2 = new SimpleDateFormat("yyyy년MM월dd일 hh시mm분");
        SimpleDateFormat f2 = getApplication().f2;
        try {
            String date = "기간:" + f2.format(f1.parse(sdate)) + "~" + f2.format(f1.parse(edate));
            Toast.makeText(getActivity(), date, Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
