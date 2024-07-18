package kr.keumyoung.mukin.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        if (artists.get(position).getArtistName().length() > 0) {
            currentCharacter = artists.get(position).getArtistName().substring(0, 1).toUpperCase();
            if (((int) currentCharacter.charAt(0)) > 127) {
                String decomposed = getCharacter(currentCharacter);
                currentCharacter = decomposed.substring(0, 1);
            }
        }

        String prevCharacter = "";
        if (artists.get(position - 1).getArtistName().length() > 0) {
            prevCharacter = artists.get(position - 1).getArtistName().substring(0, 1).toUpperCase();
            if (((int) currentCharacter.charAt(0)) > 127) {
                String decomposed = getCharacter(prevCharacter);
                prevCharacter = decomposed.substring(0, 1);
            }
        }

        if (prevCharacter.equalsIgnoreCase(currentCharacter)) return " ";
        else {
            if (isCharacterInList(currentCharacter)) return currentCharacter;
            else return " ";
        }
    }

    private final static String getCharacter(final String character) {
        // the following characters are in the correct (i.e. Unicode) order
        final String initials = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";// list of initials
        final String vowels = "ᅡᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵ";// list of vowels
        final String finals = "ᆨᆩᆪᆫᆬᆭᆮᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸᆹᆺᆻᆼᆽᆾᆿᇀᇁᇂ";// list of tail characters
        final int characterValue = character.codePointAt(0); // Unicode value
        final int hangulUnicodeStartValue = 44032;
        if (characterValue < hangulUnicodeStartValue)
            return character; // for instance for 32 (space)

        final int tailIndex = Math.round((characterValue - hangulUnicodeStartValue) % 28) - 1;
        final int vowelIndex = Math.round(((characterValue - hangulUnicodeStartValue - tailIndex) % 588) / 28);
        final int initialIndex = (characterValue - hangulUnicodeStartValue) / 588;
        final String leadString = initials.substring(initialIndex, initialIndex + 1);
        final String vowelString = vowels.substring(vowelIndex, vowelIndex + 1);
        final String tailString = tailIndex == -1 ? "" : finals.substring(tailIndex, tailIndex + 1);// may be -1 when there is no tail character
        return leadString + vowelString + tailString;
    }

    private boolean isCharacterInList(String character) {
        try {
            char charac = character.charAt(0);
            if (((int) charac) <= 127) {
                for (char permittedCharacter : Constants.PERMITTED_CHARACTERS) {
                    if (((int) permittedCharacter) == ((int) charac)) return true;
                }
            } else {
                String decomposedCharacters = getCharacter(character);
                char firstChar = decomposedCharacters.charAt(0);

                for (char permittedCharacter : Constants.PERMITTED_CHARACTERS) {
                    if (permittedCharacter == firstChar) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }


}
