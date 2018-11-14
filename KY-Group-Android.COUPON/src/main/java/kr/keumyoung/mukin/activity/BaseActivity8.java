package kr.keumyoung.mukin.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.fragment._BaseFragment;

public class BaseActivity8 extends BaseActivity7 {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected void showAlertDialog(String message, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        Log.e(__CLASSNAME__, getMethodName() + "[DLG]" + message + ":" + positive + ":" + negative);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), positive);

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.no), negative);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void showAlertDialog(String message, DialogInterface.OnClickListener listener) {
        Log.e(__CLASSNAME__, getMethodName() + "[DLG]" + message + ":" + listener);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setNeutralButton(getResources().getString(R.string.confirm), listener);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void refresh() {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + getCurrentFragment() + ":" + getChildCurrentFragment());
        if (getChildCurrentFragment() != null) {
            getChildCurrentFragment().refresh();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getCurrentFragment() != null) {
            getCurrentFragment().onBackPressed();
        }
    }

    @Override
    protected void onLoginSuccess(String email, String nickName) {
        super.onLoginSuccess(email, nickName);
        refresh();
        setResult(KARAOKE_RESULT_REFRESH);
    }

    @Override
    protected void onLogoutSuccess() {
        super.onLogoutSuccess();
        refresh();
        setResult(KARAOKE_RESULT_REFRESH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + ":" + (requestCode == KARAOKE_INTENT_ACTION_LOGIN)  + ":" + (resultCode == KARAOKE_RESULT_REFRESH) + ":" + requestCode + ":" + resultCode + ":" + data);
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
    }
}
