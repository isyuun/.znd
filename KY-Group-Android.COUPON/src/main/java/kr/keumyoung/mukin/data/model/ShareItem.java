package kr.keumyoung.mukin.data.model;

import android.content.Intent;

import kr.keumyoung.mukin.activity.ShareActivity;

/**
 *  on 20/01/18.
 */

public class ShareItem {
    private int name, icon;
    private Intent intent;
    private ShareActivity.ShareOptions option;

    public ShareItem(int name, int icon, Intent intent, ShareActivity.ShareOptions option) {
        this.name = name;
        this.icon = icon;
        this.intent = intent;
        this.option = option;
    }

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public Intent getIntent() {
        return intent;
    }

    public ShareActivity.ShareOptions getOption() {
        return option;
    }
}
