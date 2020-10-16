package com.example.outstagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.R;
import com.example.outstagram.models.PostData;
import com.google.android.gms.common.ConnectionResult;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedRVAdapter extends RecyclerView.Adapter<FeedRVAdapter.FeedViewHolder> {

    private List<PostData> postDataList = new ArrayList<>();
    private Context context;
    private OnFeedTapListener listener;
    public FeedRVAdapter(List<PostData> postDataList, Context context, OnFeedTapListener listener) {
        this.listener = listener;
        this.context = context;
        this.postDataList = postDataList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        FeedViewHolder holder = new FeedViewHolder(v, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        PostData data = postDataList.get(position);
        holder.username.setText(data.getUsername());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(context)
                .load(data.getImageUrl())
                .apply(options)
                .into(holder.circleImageView);
        Glide.with(context)
                .load(data.getPostImageUrl())
                .apply(options)
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CircleImageView circleImageView;
        private TextView username;
        private ImageView postImage;
        private OnFeedTapListener listener;
        private Button like_button, comment_button;

        public FeedViewHolder(@NonNull View itemView, OnFeedTapListener listener) {
            super(itemView);
            this.listener = listener;
            circleImageView = itemView.findViewById(R.id.circle_image_view_profile_post);
            postImage = itemView.findViewById(R.id.feed_image_view_post);
            username = itemView.findViewById(R.id.username_post_item);
            like_button = itemView.findViewById(R.id.button_like_post);
            comment_button = itemView.findViewById(R.id.button_comment_post);

            like_button.setOnClickListener(this);
            comment_button.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_like_post:
                    listener.onLikeTap();
                    break;
                case  R.id.button_comment_post:
                    listener.onCommentTapped();
                    break;
            }
        }
    }


    public interface OnFeedTapListener{
        void onLikeTap();
        void onCommentTapped();
    }
}
