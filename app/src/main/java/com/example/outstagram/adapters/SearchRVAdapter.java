package com.example.outstagram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.R;
import com.example.outstagram.SearchActivity;
import com.example.outstagram.models.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.SearchViewHolder> implements Filterable {
    private static final String TAG = "SearchRVAdapter";
    
    private List<UserDetails> list = new ArrayList<>();
    private List<UserDetails> listFull = new ArrayList<>();

    private Context context;
    private OnRVItemTapListener listener;

    public SearchRVAdapter(List<UserDetails> details, Context context, OnRVItemTapListener listener) {
        this.list = details;
        this.context = context;
        this.listFull = new ArrayList<>(list);
        Log.d(TAG, "SearchRVAdapter: listfull size = " + listFull.size());
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_rv, parent, false);
        SearchViewHolder holder = new SearchViewHolder(v, listener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        UserDetails userDetails = list.get(position);
        holder.userId.setText(userDetails.getName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        Glide.with(context)
                .load(userDetails.getProfileImageUrl())
                .apply(options)
                .into(holder.profileImage);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class SearchViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView userId;
        CircleImageView profileImage;
        private OnRVItemTapListener listener;

        public SearchViewHolder(@NonNull View itemView, OnRVItemTapListener listener) {
            super(itemView);

            userId = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.circle_image_view_profile_item);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: Called..");
            listener.onTap(getAdapterPosition(), list.get(getAdapterPosition()));
        }
    }


    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter: called");
        return filter;
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            Log.d(TAG, "performFiltering: performing..");
            List<UserDetails> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                Log.d(TAG, "performFiltering: search is empty");
                filteredList = listFull;
            }else {

                Log.d(TAG, "performFiltering: search contains pattern");
                String filterPattern = charSequence.toString().toLowerCase().trim();
                Log.d(TAG, "performFiltering: list full size= " + listFull.size());
                for(UserDetails d : listFull){
                    Log.d(TAG, "performFiltering: looping for checking results..");
                    if(d.getUserName().toLowerCase().startsWith(filterPattern)){
                        filteredList.add(d);
                    }
                }

                Log.d(TAG, "performFiltering: size of filtered list... = " + filteredList.size());
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            Log.d(TAG, "performFiltering: complete");
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            Log.d(TAG, "publishResults: called");
            list.clear();
            list.addAll((List) filterResults.values);
            notifyDataSetChanged();
            Log.d(TAG, "publishResults: complete");
        }
    };


    public interface OnRVItemTapListener{
        void onTap(int position, UserDetails details);

    }

}
