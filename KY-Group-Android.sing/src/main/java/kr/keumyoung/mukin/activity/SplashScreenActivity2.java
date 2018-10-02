package kr.keumyoung.mukin.activity;

import kr.keumyoung.KEUMYOUNG;

public class SplashScreenActivity2 extends SplashScreenActivity {

    @Override
    protected void proceedToNextActivity() {
        super.proceedToNextActivity();

        this.email = getGoogleAccount();
        this.pass = KEUMYOUNG.DEFAULT_PASS;

        handler.postDelayed(() -> {
            //registerUserToDF(this.email, this.email, this.pass, "");
            navigationHelper.navigate(this, _HomeActivity.class);
        }, 2000);
    }

}
