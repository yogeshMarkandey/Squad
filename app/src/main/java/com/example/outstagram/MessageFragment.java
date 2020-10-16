package com.example.outstagram;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.outstagram.adapters.ChatListRVAdapter;
import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment  implements ChatListRVAdapter.ChatItemTapListener {
    private static final String TAG = "MessageFragment";

    public static final String INTENT_KEY_CHAT_USER = "chat_user";

    private RecyclerView recyclerView;
    private ChatListRVAdapter adapter;
    private ProgressBar progressBar;

    private List<UserDetails> listFull = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private  List<Map<String, String>> mapList;
    private Map<String, String> mapValues;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_messages_friends);
        progressBar = view.findViewById(R.id.progress_bar_message_frag);
        firebaseFirestore = FirebaseFirestore.getInstance();

        /*
        * TODO: Get list of friends Here.
        *     The call initRecyclerView in OnComplete.
        *      This will show the list of friends in this Fragment
        *      On Tap on the item open ChatActivity
        *      also send user Details via intent Extra.
        * */
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.isSuccessful()){
                    return;
                }

                List<DocumentSnapshot> snapshots = task.getResult().getDocuments();

                for (DocumentSnapshot d : snapshots){
                    UserDetails details = new UserDetails();
                    details = d.toObject(UserDetails.class);

                    if(details != null){
                        listFull.add(details);
                    }

                }

                Log.d(TAG, "onComplete: size of listFull : " + listFull.size());

                initRecyclerView();
            }
        });


        return view;
    }



    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: called.");


        adapter = new ChatListRVAdapter(listFull , getContext(), this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onTap(int position, UserDetails details) {
        Toast.makeText(getContext(), "position : " + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(INTENT_KEY_CHAT_USER, details);
        startActivity(intent);
    }
}
