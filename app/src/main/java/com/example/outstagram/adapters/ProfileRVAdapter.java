package com.example.outstagram.adapters;

import android.content.Context;
import android.media.Image;
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
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ProfileRVAdapter extends RecyclerView.Adapter<ProfileRVAdapter.PostViewHolder> {

    private static final String TAG = "ProfileRVAdapter";

    private List<PostData> postDataList = new ArrayList<>();
    private Context context;

    public ProfileRVAdapter(List<PostData> postDataList, Context context) {
        this.postDataList = postDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_posts_small,
                parent, false);

        PostViewHolder holder = new PostViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        PostData data = postDataList.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher);

        Glide.with(context)
                .load(data.getPostImageUrl())
                .apply(options)
                .into(holder.postImage);

    }

    @Override
    public int getItemCount() {
        return postDataList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

       private ImageView postImage;
       public PostViewHolder(@NonNull View itemView) {
           super(itemView);
           postImage = itemView.findViewById(R.id.image_view_posts_small);
       }
   }
}
