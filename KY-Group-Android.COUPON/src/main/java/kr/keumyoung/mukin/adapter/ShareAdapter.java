package kr.keumyoung.mukin.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import kr.keumyoung.mukin.MainApplication;
import kr.keumyoung.mukin.R;
import kr.keumyoung.mukin.data.model.ShareItems;
import kr.keumyoung.mukin.viewholder.ShareViewHolder;

import javax.inject.Inject;

/**
 *  on 20/01/18.
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareViewHolder> {

    private ShareItems shareItems;

    @Inject
    Context context;

    public ShareAdapter(ShareItems shareItems) {
        this.shareItems = shareItems;
        MainApplication.getInstance().getMainComponent().inject(this);
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ShareViewHolder(LayoutInflater.from(context).inflate(R.layout.item_share, parent, false));
    }

    @Override
    public void onBindViewHolder(ShareViewHolder holder, int position) {
        holder.setData(shareItems.get(position));
    }

    @Override
    public int getItemCount() {
        return shareItems.size();
    }
}
