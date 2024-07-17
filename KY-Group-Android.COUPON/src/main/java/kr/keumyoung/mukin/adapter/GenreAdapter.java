package kr.keumyoung.mukin.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Genres;
import kr.keumyoung.mukin.viewholder.GenreViewHolder;

import javax.inject.Inject;

import static kr.keumyoung.mukin.adapter.SongAdapter.DATA;
import static kr.keumyoung.mukin.adapter.SongAdapter.LOADING;

/**
 *  on 13/01/18.
 */

public class GenreAdapter extends RecyclerView.Adapter<GenreViewHolder> {

    private Genres genres;

    @Inject
    Context context;

    private boolean isLoading = false;

    public GenreAdapter(Genres genres) {
        this.genres = genres;
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading && position == getItemCount() - 1) return LOADING;
        return DATA;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenreViewHolder(LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        if (!(isLoading && position == getItemCount() - 1))
            holder.setData(genres.get(position));
    }

    @Override
    public int getItemCount() {
        return isLoading() ? genres.size() + 1 : genres.size();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
