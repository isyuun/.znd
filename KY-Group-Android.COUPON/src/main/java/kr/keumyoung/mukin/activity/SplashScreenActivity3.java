package kr.keumyoung.mukin.activity;

public class SplashScreenActivity3 extends SplashScreenActivity2 {
    @Override
    protected void proceedToNextActivity() {
        super.proceedToNextActivity();
        getApp().sendUser();
    }
}
