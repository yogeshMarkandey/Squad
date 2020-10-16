package com.example.outstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemWrapperICS;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.outstagram.adapters.SearchRVAdapter;
import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchRVAdapter.OnRVItemTapListener {
    private static final String TAG = "SearchActivity";

    public static final String INTENT_KEY_ANOTHER_USER = "another_user";

    private RecyclerView recyclerViewSearch;
    private SearchRVAdapter adapter;
    private List<UserDetails> list  = new ArrayList<>();

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Log.d(TAG, "onCreate: Called...");


        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG, "onComplete: getting documents complete..");
                        if(!task.isSuccessful()){
                            Log.d(TAG, "onComplete: Task not successful.. " + task.getException());
                            return;
                        }

                        for(DocumentSnapshot documentSnapshot : task.getResult()){
                            UserDetails details = new UserDetails();
                            details = documentSnapshot.toObject(UserDetails.class);

                            list.add(details);
                        }

                        initRecyclerView();
                        Log.d(TAG, "onComplete: list size = "+ list.size());

                    }
                });

        Log.d(TAG, "onCreate: complete");
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: called");
        recyclerViewSearch = findViewById(R.id.recycler_view_search);
        adapter = new SearchRVAdapter(list, getApplicationContext(), this);

        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setAdapter(adapter);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        Log.d(TAG, "initRecyclerView: complete");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView  = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "onQueryTextSubmit: called..");
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "onQueryTextChange: called..");
                adapter.getFilter().filter(s);
                return true;
            }
        });


        return true;
    }

    @Override
    public void onTap(int position, UserDetails details) {
        Toast.makeText(this, "Position: " + position, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SearchActivity.this, AnotherUserProfile.class);
        intent.putExtra(INTENT_KEY_ANOTHER_USER, details );
        startActivity(intent);
    }
}