package com.example.outstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.example.outstagram.models.UserDetails;
import com.example.outstagram.util.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BottomNavigationView navigationBar;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationBar = findViewById(R.id.bottom_nav_bar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new HomeFragment() ).commit();

        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new HomeFragment() ).commit();
                        break;
                    case R.id.discover:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new DiscoverFragment() ).commit();
                        break;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_container, new ProfileFragment() ).commit();
                        break;

                }
                return true;
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            UserClient currentClient = UserClient.getInstance();
                            UserDetails details = task.getResult().toObject(UserDetails.class);
                            currentClient.setCurrentUser(details);
                            Log.d(TAG, "onComplete: current details is updated..");
                        }
                    }
                });
    }
}