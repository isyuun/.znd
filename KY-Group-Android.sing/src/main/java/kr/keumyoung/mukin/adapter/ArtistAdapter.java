package kr.keumyoung.mukin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import javax.inject.Inject;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.Artists;
import kr.keumyoung.mukin.util.Constants;
import kr.keumyoung.mukin.viewholder.ArtistViewHolder;

/**
 * on 13/01/18.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistViewHolder> {

    @Inject
    Context context;
    private Artists artists;

    public ArtistAdapter(Artists artists) {
        this.artists = artists;
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistViewHolder(LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false));
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        String nameTag = getNameTag(position);
        holder.setData(artists.get(position), position, nameTag);
    }

    private String getNameTag(int position) {
        int count = getItemCount();
        if (count == 0) return " ";
        if (position == 0) {
            if (artists.get(position).getArtistName().length() > 0)
                return artists.get(position).getArtistName().substring(0, 1).toUpperCase();
            else return " ";
        }

        String currentCharacter = "";
        if (artists.get(position).getArtistName().length() > 0)
            currentCharacter = artists.get(position).getArtistName().substring(0, 1).toUpperCase();

        String prevCharacter = "";
        if (artists.get(position - 1).getArtistName().length() > 0)
            prevCharacter = artists.get(position - 1).getArtistName().substring(0, 1).toUpperCase();

        if (prevCharacter.equalsIgnoreCase(currentCharacter)) return " ";
        else {
            if (isCharacterInList(currentCharacter)) return currentCharacter;
            else return " ";
        }
    }

    private boolean isCharacterInList(String character) {
        try {
            return Constants.PERMITTED_CHARACTERS.contains(character);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }


}
