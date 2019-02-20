package kr.keumyoung.mukin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import dev.niekirk.com.instagram4android.Instagram4Android;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoggedUser;
import dev.niekirk.com.instagram4android.requests.payload.InstagramLoginResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.util.Constants;

@Deprecated
public class LoginChoiceActivity3 extends LoginChoiceActivity {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loginToInstagram(final String usr, final String pwd) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName());

        Instagram4Android instagram = Instagram4Android.builder().username(usr).password(pwd).build();

        attemptLogin(instagram)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(instagramLoginResult -> {
                    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + instagramLoginResult.getLogged_in_user());
                    String email = "";
                    String password = "";
                    String name = "";
                    String profileImage = "";
                    String sociallogin = Constants.INSTAGRAM;
                    String socialid = "";
                    if (instagramLoginResult.getLogged_in_user() != null) {
                        InstagramLoggedUser user = instagramLoginResult.getLogged_in_user();
                        email = user.username;
                        password = socialid = "" + user.pk;
                        name = user.username;
                        profileImage = user.profile_pic_url;
                        if (BuildConfig.DEBUG) Log.i(__CLASSNAME__, "\n" + email + "\n" + password + "\n" + socialid + "\n" + name + "\n" + profileImage);
                        //registerUserToDF(email, name, pwd, profileImage, sociallogin, socialid);
                    }
                });

    }

    private Observable<InstagramLoginResult> attemptLogin(final Instagram4Android instagram) {

        Observable<InstagramLoginResult> observable = Observable.create(observableEmitter -> {

            instagram.setup();
            observableEmitter.onNext(instagram.login());

        });

        return observable;

    }
}
