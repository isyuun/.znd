package kr.keumyoung.mukin.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Artist;
import kr.keumyoung.mukin.helper.MediaManager;

/**
 *  on 13/01/18.
 */

public class ArtistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @Inject
    Bus bus;

    @Inject
    MediaManager mediaManager;

    @Inject
    Context context;

    Artist artist;

    int position;

    @BindView(R.id.name_tag)
    TextView nameTag;
    @BindView(R.id.artist_image)
    CircleImageView artistImage;
    @BindView(R.id.artist_name)
    TextView artistName;
    @BindView(R.id.song_count)
    TextView songCount;

    public ArtistViewHolder(View itemView) {
        super(itemView);
        MainApplication.getInstance().getMainComponent().inject(this);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    public void setData(Artist artist, int position, String tag) {
        this.position = position;
        this.artist = artist;

        artistName.setText(artist.getArtistName());
        songCount.setText(String.format("%s %s", Integer.toString(artist.getSongCount()),
                artist.getSongCount() > 1 ? context.getResources().getString(R.string.songs) : context.getResources().getString(R.string.song)));
        mediaManager.loadImageIntoView(artist.getArtistImage(), artistImage);

        if (tag.isEmpty()) {
            nameTag.setVisibility(View.INVISIBLE);
        } else {
            nameTag.setText(tag);
            nameTag.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        bus.post(artist);
    }
}
