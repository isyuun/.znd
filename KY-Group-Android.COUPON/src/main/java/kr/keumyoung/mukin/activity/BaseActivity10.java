package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class BaseActivity10 extends BaseActivity9 {
    //private void forceCrash() {
    //    throw new RuntimeException("This is a crash");
    //}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        //forceCrash();   //test
    }
}
