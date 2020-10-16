package com.example.outstagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.R;
import com.example.outstagram.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class ChatListRVAdapter extends RecyclerView.Adapter<ChatListRVAdapter.ChatViewHolder> {

    private List<UserDetails> list = new ArrayList<>();
    private Context context;
    private ChatItemTapListener listener;

    public ChatListRVAdapter(List<UserDetails> list, Context context, ChatItemTapListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_rv, parent, false);

        ChatViewHolder holder = new ChatViewHolder(v, listener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        UserDetails details = list.get(position);

        holder.userName.setText(details.getUserName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(context)
                .load(details.getProfileImageUrl())
                .apply(options)
                .into(holder.imageView);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView userName;
        private CircleImageView imageView;
        private ChatItemTapListener listener;
        public ChatViewHolder(@NonNull View itemView, ChatItemTapListener listener) {
            super(itemView);
            this.listener = listener;
            userName = itemView.findViewById(R.id.user_name );
            imageView = itemView.findViewById(R.id.circle_image_view_profile_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onTap(getAdapterPosition(), list.get(getAdapterPosition()));
        }
    }

    public interface ChatItemTapListener{
        void onTap(int position, UserDetails details);
    }
}
