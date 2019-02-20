package kr.keumyoung.mukin.activity;

public class SplashScreenActivity3 extends SplashScreenActivity2 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    protected void proceedToNextActivity() {
        super.proceedToNextActivity();
        getApp().sendUser();
    }

    @Override
    protected void onCompleteCopyFilesToLocal() {
        super.onCompleteCopyFilesToLocal();
    }

    @Override
    protected void openHomeActivity() {
        super.openHomeActivity();
    }

    @Override
    public void openPreferenceLoginChoice() {
        super.openPreferenceLoginChoice();
    }
}
