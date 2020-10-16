package com.example.outstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.models.Message;
import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnotherUserProfile extends AppCompatActivity {
    private static final String TAG = "AnotherUserProfile";

    private CircleImageView imageView;
    private TextView textView;
    private Button addFriend;

    private UserDetails details;
    private UserDetails currentUser;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user_profile);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        details = intent.getParcelableExtra(SearchActivity.INTENT_KEY_ANOTHER_USER);
        Log.d(TAG, "onCreate: user : " + details.toString() );

        imageView = findViewById(R.id.image_view_profile_another_user);
        textView = findViewById(R.id.text_user_name_another);
        addFriend = findViewById(R.id.button_add_friend);

        textView.setText(details.getUserName());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(getApplicationContext())
                .load(details.getProfileImageUrl())
                .apply(options)
                .into(imageView);


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFriendList();
            }
        });

        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    currentUser = task.getResult().toObject(UserDetails.class);
                }
            }
        });
    }

    private void addNewFriend(){
        Log.d(TAG, "addNewFriend: called");

        String chatId = "chat_"+ System.currentTimeMillis();

        final Map<String , String> data = new HashMap<>();
        data.put("ChatId", chatId);


        Map<String, String> item =new HashMap<>();
        item.put(details.getUserId(), chatId);



        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("friends_list", FieldValue.arrayUnion(item))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           // Toast.makeText(AnotherUserProfile.this, "done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        currentUser = new UserDetails();

        Map<String, String> chatId2 = new HashMap<>();
        chatId2.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), chatId);

        // Saving in Another users profile
        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(details.getUserId())
                .update("friends_list", FieldValue.arrayUnion(chatId2))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           // Toast.makeText(AnotherUserProfile.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        Map<String, String> map = new HashMap<>();
        map.put("Room Id", chatId);

        // Creating a chat room for them
        firebaseFirestore.collection(RegistrationActivity.CHAT_ROOM)
                .document(chatId)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AnotherUserProfile.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void addToFriendList(){
        Log.d(TAG, "addToFriendList: called");

        String chat_id = "chatId_" + System.currentTimeMillis();

        Map<String, String> map = new HashMap<>();
        map.put("UserId", details.getUserId());
        details.setChatId(chat_id);

        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Friends")
                .document(details.getUserId())
                .set(details)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                          //  Toast.makeText(AnotherUserProfile.this, "Complete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        currentUser.setChatId(chat_id);
        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(details.getUserId())
                .collection("Friends")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(currentUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(AnotherUserProfile.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Message message = new Message();
        message.setUserName("Milligram");
        message.setMessage("You are Friends");
        message.setMessage_id("First_Message");
        firebaseFirestore.collection(RegistrationActivity.CHAT_ROOM)
                .document(chat_id)
                .collection("Messages")
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AnotherUserProfile.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}