package com.projects.shrungbhatt.filetransfer.adpaters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.projects.shrungbhatt.filetransfer.R;
import com.projects.shrungbhatt.filetransfer.utils.Utility;

import java.util.List;

public class Adapter_HistoryListItem extends RecyclerView.Adapter<Adapter_HistoryListItem.HistoryViewHolder> {


    private Context mContext;
    private List<String> mFiles;

    public Adapter_HistoryListItem(Context context, List<String> files) {
        mContext = context;
        mFiles = files;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new HistoryViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder,final int position) {
        holder.mThumbnail.setImageBitmap(Utility.getThumbnail(mFiles.get(position)));

        holder.mThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Utility.getUri(mFiles.get(position)), "video/mp4");
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mFiles != null)
            return mFiles.size();
        else
            return 0;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private ImageView mThumbnail;

        HistoryViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_history, parent, false));

            mThumbnail = itemView.findViewById(R.id.list_item_history_image_view);


        }

    }


}
