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
import kr.keumyoung.mukin.util.RandromAlbumImage;

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

        //db를 바꿔야지 구지 왜 이걸...
        if(genre.getName().compareTo("TV음악") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_tv);
            //genreIcon.setImageResource(RandromAlbumImage.getInstance().getAlbumResourceID());
        }else if(genre.getName().compareTo("7080") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_7080);
        }else if(genre.getName().compareTo("레전드팝가수") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_legendpop);
        }else if(genre.getName().compareTo("댄스") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_dance);
        }else if(genre.getName().compareTo("동요") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_child);
        }else if(genre.getName().compareTo("듀엣곡") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_duet);
        }else if(genre.getName().compareTo("락") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_rock);
        }else if(genre.getName().compareTo("2018 대한민국 6대 보컬") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_6vocal);
        }else if(genre.getName().compareTo("발라드") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_balad);
        }else if(genre.getName().compareTo("뮤지컬 인기곡") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_musical);
        }else if(genre.getName().compareTo("성인") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_adult);
        }else if(genre.getName().compareTo("축가모음") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_congratulation);
        }else if(genre.getName().compareTo("힙합") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_hiphop);
        }else if(genre.getName().compareTo("아이돌 인기곡") == 0)
        {
            genreIcon.setImageResource(R.drawable.genre_idolhit);
        }else {
            mediaManager.loadImageIntoView(genre.getIcon(), genreIcon);
        }


    }

    @Override
    public void onClick(View view) {
        bus.post(genre);
    }
}
