package kr.keumyoung.mukin.helper;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;

/**
 *  on 11/01/18.
 */

public class ToastHelper {

    Context context;

    public enum MessageType {
        SUCCESS, ERROR, WARNING
    }

    @Inject
    public ToastHelper(Context context) {
        this.context = context;
    }

    public void showError(String message) {
        showToast(message, MessageType.ERROR);
    }

    public void showError(int message) {
        showToast(context.getResources().getString(message), MessageType.ERROR);
    }

    public void showToast(String message, MessageType type) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void showGenericToast(int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
