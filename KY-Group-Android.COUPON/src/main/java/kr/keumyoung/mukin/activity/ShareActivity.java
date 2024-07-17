package kr.keumyoung.mukin.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.ShareAdapter;
import kr.keumyoung.mukin.data.model.ShareItem;
import kr.keumyoung.mukin.data.model.ShareItems;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.helper.AnimationHelper;
import kr.keumyoung.mukin.helper.NavigationHelper;
import kr.keumyoung.mukin.helper.ToastHelper;
import kr.keumyoung.mukin.util.Constants;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *  on 15/01/18.
 */

public class ShareActivity extends BaseActivity {

    @Inject
    NavigationHelper navigationHelper;

    @Inject
    AnimationHelper animationHelper;

    @Inject
    ToastHelper toastHelper;

    @BindView(R.id.back_button)
    ImageView backButton;
    @BindView(R.id.header_text)
    TextView headerText;
    @BindView(R.id.toolbar)
    LinearLayout toolbar;
    @BindView(R.id.share_on_label)
    TextView shareOnLabel;
    @BindView(R.id.music_link)
    TextView musicLink;
    @BindView(R.id.link_label)
    TextView linkLabel;
    @BindView(R.id.share_recycler)
    RecyclerView shareRecycler;
    @BindView(R.id.music_link_ripple)
    RippleView musicLinkRipple;
    @BindView(R.id.back_button_ripple)
    RippleView backButtonRipple;
    @BindView(R.id.badge_section)
    LinearLayout badgeSection;

    ShareAdapter shareAdapter;
    ShareItems shareItems = new ShareItems();

    Song song;
    String shareLink;

    public enum ShareOptions {
        KAKAO_TALK,
        FACEBOOK,
        TWITTER,
        INSTAGRAM,
        WHATSAPP,
        MESSENGER,
        COPY_LINK,
        MORE
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getMainComponent().inject(this);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_share, null, false);
        inflateContainerView(view);

        ButterKnife.bind(this, view);

        musicLinkRipple.setOnLongClickListener(v -> {
            musicLinkRipple.setOnRippleCompleteListener(rippleView -> copyLinkToClipboard());
            return true;
        });

        Intent intent = getIntent();
        if (intent.hasExtra(Constants.DATA)) {
            Bundle bundle = intent.getBundleExtra(Constants.DATA);

            if (bundle.containsKey(Constants.SONG))
                song = (Song) bundle.getSerializable(Constants.SONG);

            if (bundle.containsKey(Constants.SHARE_LINK))
                shareLink = bundle.getString(Constants.SHARE_LINK);
        }

        if (shareLink != null) musicLink.setText(shareLink);
    }

    private void copyLinkToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", shareLink);
        clipboard.setPrimaryClip(clip);
        toastHelper.showError(R.string.link_copied_to_clipboard);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bus.register(this);

        populateShareItems();

        animationHelper.showHeaderText(headerText, false);
        animationHelper.showWithZoomAnim(badgeSection);

        setupRecycler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    private void setupRecycler() {
        shareAdapter = new ShareAdapter(shareItems);
        shareRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        shareRecycler.setAdapter(shareAdapter);
    }

    private void populateShareItems() {
        shareItems.clear();
        shareItems.add(new ShareItem(R.string.kakao_talk, R.drawable.kako_talk_icon, getIntentForOption(ShareOptions.KAKAO_TALK), ShareOptions.KAKAO_TALK));
        shareItems.add(new ShareItem(R.string.facebook, R.drawable.fb_icon, getIntentForOption(ShareOptions.FACEBOOK), ShareOptions.FACEBOOK));
        shareItems.add(new ShareItem(R.string.twitter, R.drawable.twitter_icon, getIntentForOption(ShareOptions.TWITTER), ShareOptions.TWITTER));
        shareItems.add(new ShareItem(R.string.instagram, R.drawable.instagram_icon, getIntentForOption(ShareOptions.INSTAGRAM), ShareOptions.INSTAGRAM));
        shareItems.add(new ShareItem(R.string.whatsapp, R.drawable.whatsapp_icon, getIntentForOption(ShareOptions.WHATSAPP), ShareOptions.WHATSAPP));
        shareItems.add(new ShareItem(R.string.messenger, R.drawable.messenger_icon, getIntentForOption(ShareOptions.MESSENGER), ShareOptions.MESSENGER));
        shareItems.add(new ShareItem(R.string.copy_link, R.drawable.copy_link_icon, getIntentForOption(ShareOptions.COPY_LINK), ShareOptions.COPY_LINK));
        shareItems.add(new ShareItem(R.string.more, R.drawable.more_icon, getIntentForOption(ShareOptions.MORE), ShareOptions.MORE));
    }

    private Intent getIntentForOption(ShareOptions option) {
        switch (option) {
            case KAKAO_TALK:
                return getShareIntent("com.kakao.talk");
            case TWITTER:
                return getShareIntent("com.twitter.android");
            case FACEBOOK:
                return getShareIntent("com.facebook.katana");
            case WHATSAPP:
                return getShareIntent("com.whatsapp");
            case COPY_LINK:
                return null;
            case INSTAGRAM:
                return getShareIntent("com.instagram.android");
            case MESSENGER:
                return getShareIntent("com.facebook.orca");
            case MORE:
            default:
                return getShareIntent("");
        }
    }

    private Intent getShareIntent(String packageName) {
        Intent intent;
        PackageManager pm = getPackageManager();
        try {
            if (!packageName.isEmpty()) {
                PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            }

            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String text = shareLink;

            if (!packageName.isEmpty()) {
                intent.setPackage(packageName);
            }

            intent.putExtra(Intent.EXTRA_TEXT, text);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return intent;
    }

    @Subscribe
    public void onShareOption(ShareItem item) {
        if (item.getOption() == ShareOptions.COPY_LINK) {
            copyLinkToClipboard();
            return;
        }

        Intent intent = item.getIntent();
        if (intent != null) startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_with)));
        else toastHelper.showError(R.string.the_app_not_found);
    }

    @OnClick(R.id.back_button_ripple)
    public void onViewClicked() {
        backButtonRipple.setOnRippleCompleteListener(rippleView -> navigationHelper.finish(this));
    }

    @Override
    public void onBackPressed() {
        navigationHelper.finish(this);
    }
}
