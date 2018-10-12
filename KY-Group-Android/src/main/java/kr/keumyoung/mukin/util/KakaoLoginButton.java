package kr.keumyoung.mukin.util;

import android.content.Context;
import android.util.AttributeSet;

import com.kakao.widget.LoginButton;

import kr.keumyoung.mukin.R;

/**
 *  on 05/02/18.
 * Project: KyGroup
 */

public class KakaoLoginButton extends LoginButton {
    public KakaoLoginButton(Context context) {
        super(context);
    }

    public KakaoLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KakaoLoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflate(getContext(), R.layout.kakao_login_button, this);
    }
}
