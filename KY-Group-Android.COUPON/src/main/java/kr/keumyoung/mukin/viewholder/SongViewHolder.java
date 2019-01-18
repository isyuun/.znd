package kr.keumyoung.mukin.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.BuildConfig;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.data.model.Song;
import kr.keumyoung.mukin.data.model.SongView;
import kr.keumyoung.mukin.helper.DateHelper;
import kr.keumyoung.mukin.helper.MediaManager;

/**
 * on 12/01/18.
 */

public class SongViewHolder extends RecyclerView.ViewHolder {
    private final String __CLASSNAME__ = (new Exception()).getStackTrace()[0].getFileName();

    protected String getMethodName() {
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();
        String text = String.format("%s()", name);
        // int line = Thread.currentThread().getStackTrace()[3].getLineNumber();
        // text = String.format("line:%d - %s() ", line, name);
        return text;
    }

    @Inject
    MediaManager mediaManager;

    @Inject
    Bus bus;

    @Inject
    DateHelper dateHelper;

    @Inject
    Context context;

    Song song;

    int viewType;

    @BindView(R.id.album_image)
    RoundedImageView albumImage;
    @BindView(R.id.song_name)
    TextView songName;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.hits_count_text)
    TextView hitsCountText;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.song_anchor)
    RelativeLayout songAnchor;
    @BindView(R.id.song_item)
    LinearLayout songItem;
    @BindView(R.id.favorite_button)
    CheckBox favoriteButton;
    @BindView(R.id.free_button)
    TextView freeButton;
    @BindView(R.id.reserve_button)
    TextView reserveButton;

    GestureDetector gestureDetector;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onContextClick(MotionEvent e) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + e);
            return super.onContextClick(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + e);
            onSongClick(songAnchor);
            super.onLongPress(e);
        }

        //@Override
        //public boolean onDoubleTap(MotionEvent e) {
        //    if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + e);
        //    onSongClick(songAnchor);
        //    return super.onDoubleTap(e);
        //}

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + e);
            onReserveClick(songAnchor);
            return super.onSingleTapConfirmed(e);
        }
    }

    public SongViewHolder(View itemView, int viewType) {
        super(itemView);
        gestureDetector = new GestureDetector(context, new GestureListener());

        this.viewType = viewType;
        //R.layout.item_song
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this, itemView);
        //songAnchor.setOnClickListener(v -> songAnchor.setOnRippleCompleteListener(rippleView -> onSongClick(v.findViewById(R.id.song_item))));
        songAnchor.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        favoriteButton.setOnClickListener(v -> {
            if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, "favoriteButton.onClick(...)" + v);
            onFavoriteClick(v);
        });
        if (viewType == SongAdapter.DATA) {
            songAnchor.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        } else {
            loading.setVisibility(View.VISIBLE);
            songAnchor.setVisibility(View.GONE);
        }
    }

    public void onSongClick(View view) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + view);
        bus.post(new SongView(song, view, 1));
    }

    public void onFavoriteClick(View view) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + view);
        bus.post(new SongView(song, view, 1));
    }

    public void onReserveClick(View view) {
        if (BuildConfig.DEBUG) Log.e(__CLASSNAME__, getMethodName() + view);
        bus.post(new SongView(song, view, 2));
    }

    public void setData(Song song) {
        boolean reload = false;
        if (this.song != song) {
            reload = true;
        }
        this.song = song;

        if (reload) {
            mediaManager.loadImageIntoView(song.getAlbumImage(), albumImage);
        }
        //songName.setText(String.format("%s (%s)", song.getSongTitle(), song.getIdentifier()));
        songName.setText(String.format("[%s] %s", song.getIdentifier(), song.getSongTitle()));
        artistName.setText(song.getArtistName());

        String durationText = dateHelper.getDuration(song.getDuration());
        String hitsText = String.format("%s %s", Integer.toString(song.getHits()),
                song.getHits() > 1 ? context.getResources().getString(R.string.hits) : context.getResources().getString(R.string.hit));

        hitsCountText.setText(String.format("%s", hitsText));

        favoriteButton.setChecked(song.isFavorite());

        if (song.isReserve()) {
            reserveButton.setVisibility(View.VISIBLE);
            freeButton.setVisibility(View.GONE);
        } else {
            reserveButton.setVisibility(View.GONE);
            if (song.isFree()) {
                freeButton.setVisibility(View.VISIBLE);
            } else {
                freeButton.setVisibility(View.GONE);
            }
        }
    }
}
