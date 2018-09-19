package kr.keumyoung.mukin.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.adapter.SongAdapter;
import kr.keumyoung.mukin.data.model.Genre;
import kr.keumyoung.mukin.helper.MediaManager;

/**
 *  on 13/01/18.
 */

public class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @Inject
    Bus bus;

    @Inject
    MediaManager mediaManager;

    @Inject
    Context context;

    Genre genre;

    @BindView(R.id.genre_icon)
    ImageView genreIcon;
    @BindView(R.id.genre_name)
    TextView genreName;
    @BindView(R.id.genre_count)
    TextView genreCount;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.next_button)
    ImageView nextButton;
    @BindView(R.id.parent_item)
    LinearLayout parentItem;

    public GenreViewHolder(View itemView, int viewType) {
        super(itemView);

//        R.layout.item_genre
        ButterKnife.bind(this, itemView);
        MainApplication.getInstance().getMainComponent().inject(this);
        itemView.setOnClickListener(this);

        if (viewType == SongAdapter.LOADING) {
            loading.setVisibility(View.VISIBLE);
            parentItem.setVisibility(View.GONE);
        } else {
            parentItem.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    public void setData(Genre genre) {
        this.genre = genre;
        genreName.setText(genre.getName());
        genreCount.setText(String.format("%s %s", String.valueOf(genre.getCount()),
                genre.getCount() > 1 ? context.getResources().getString(R.string.songs) : context.getResources().getString(R.string.song)));
        mediaManager.loadImageIntoView(genre.getIcon(), genreIcon);
    }

    @Override
    public void onClick(View view) {
        bus.post(genre);
    }
}
