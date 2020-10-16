package com.example.outstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.outstagram.adapters.FeedRVAdapter;
import com.example.outstagram.models.PostData;
import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment implements FeedRVAdapter.OnFeedTapListener {

    private static final String TAG = "FeedFragment";

    private ImageView search_image;
    private ImageView postImageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private FeedRVAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private List<PostData> postDataList = new ArrayList<>();
    private List<String> listOfFriendsId = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_feed, container, false);

        search_image = view.findViewById(R.id.image_search);
        postImageView = view.findViewById(R.id.add_post_image_feed);
        recyclerView = view.findViewById(R.id.recycler_view_feed);
        progressBar = view.findViewById(R.id.progress_bar_feed);
        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open search Activity
                Log.d(TAG, "onClick: Starting Search Activity..");
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPostActivity.class);
                startActivity(intent);
            }
        });

        listOfFriendsId.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

        initRecyclerView();

        getFeeds();

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getFeeds(){

        Log.d(TAG, "onCreateView: getting friends List");
        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG, "onComplete: No Friends list found");
                            return;
                        }
                        List<DocumentSnapshot> snapshots = task.getResult().getDocuments();

                        for(DocumentSnapshot snapshot : snapshots){
                            UserDetails details = new UserDetails();
                            details = snapshot.toObject(UserDetails.class);

                            listOfFriendsId.add(details.getUserId());
                        }
                        Log.d(TAG, "onComplete: Size of friends list : " + listOfFriendsId.size());
                        Log.d(TAG, "onComplete: Now Iterating through each friend's id..");
                        for(String id : listOfFriendsId ){
                            firebaseFirestore.collection(NewPostActivity.POST_DATABASE)
                                    .document(id)
                                    .collection("Posts")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(!task.isSuccessful()) {
                                                return;
                                            }
                                            List<DocumentSnapshot> snapshots1 = task.getResult().getDocuments();

                                            for(DocumentSnapshot s : snapshots1){
                                                PostData data = s.toObject(PostData.class);
                                                postDataList.add(data);
                                            }
                                            Collections.reverse(postDataList);
                                            adapter.notifyDataSetChanged();
                                            progressBar.setVisibility(View.GONE);
                                            Log.d(TAG, "onComplete: complete");
                                        }
                                    });
                        }
                        Log.d(TAG, "onComplete: size of post List : " + postDataList.size());

                        //Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: called..");

        adapter = new FeedRVAdapter(postDataList, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

    }

    @Override
    public void onLikeTap() {
        Toast.makeText(getContext(), "Like", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCommentTapped() {
        Toast.makeText(getContext(), "comment", Toast.LENGTH_SHORT).show();

    }
}
