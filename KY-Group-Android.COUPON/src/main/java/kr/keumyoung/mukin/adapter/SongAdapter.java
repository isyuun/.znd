package kr.keumyoung.mukin.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Songs;
import kr.keumyoung.mukin.viewholder.SongViewHolder;

import javax.inject.Inject;

/**
 *  on 12/01/18.
 */

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    @Inject
    Context context;

    private Songs songs;

    private boolean isLoading = false;

    public static final int LOADING = 1;
    public static final int DATA = 2;

    public SongAdapter(Songs songs) {
        this.songs = songs;
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading && position == getItemCount() - 1) return LOADING;
        return DATA;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(context).inflate(R.layout.item_song, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (!(isLoading && position == getItemCount() - 1))
            holder.setData(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return isLoading ? songs.size() + 1 : songs.size();
    }

    public boolean isLoading() {
        return isLoading;
    }
}
