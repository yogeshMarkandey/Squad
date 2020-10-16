package com.example.outstagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.R;
import com.example.outstagram.models.PostData;

import java.util.ArrayList;
import java.util.List;

public class DiscoverRVAdapter extends RecyclerView.Adapter<DiscoverRVAdapter.DiscoverViewHolder> {

    private static final String TAG = "DiscoverRVAdapter";

    private List<PostData> list = new ArrayList<>();
    private Context context;

    public DiscoverRVAdapter(List<PostData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_posts_small, parent, false);
        DiscoverViewHolder holder = new DiscoverViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverViewHolder holder, int position) {
        PostData data = list.get(position);

        RequestOptions options = new RequestOptions()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop();

        Glide.with(context)
                .load(data.getPostImageUrl())
                .apply(options)
                .into(holder.postImageView);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DiscoverViewHolder extends RecyclerView.ViewHolder{

        private ImageView postImageView;

        public DiscoverViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.image_view_posts_small);

        }
    }
}
