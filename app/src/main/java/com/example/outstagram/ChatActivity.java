package com.example.outstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.adapters.MessageRVAdapter;
import com.example.outstagram.models.Message;
import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

/*
    TODO: Get the Intent Extra.
        Get ChatId of This chat
        Then Setup the Recycler View Of Messages
        Add method to send messages

*/


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private CircleImageView imageView;
    private ImageView sendButton;
    private TextView userName;
    private EditText message_ev;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private UserDetails details;
    private FirebaseFirestore firebaseFirestore;
    private List<Message> listMessages = new ArrayList<>();
    private MessageRVAdapter adapter;
    private ListenerRegistration mChatEventListener;
    private Set<String> mMessage_id = new HashSet<>();
    private UserDetails currentUser;
    private byte[] encryptionKey = {9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53};
    private Cipher cipher, decipher;
    private SecretKeySpec secretKeySpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseFirestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progress_bar_chat_activity);
        recyclerView = findViewById(R.id.recyclerView_chat_activity);
        sendButton = findViewById(R.id.send_message);
        imageView = findViewById(R.id.image_view_profile_chat_activity);
        userName = findViewById(R.id.user_name_chat_activity);
        message_ev = findViewById(R.id.edit_message_chat_activity);
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        details =  intent.getParcelableExtra(MessageFragment.INTENT_KEY_CHAT_USER);

        userName.setText(details.getUserName());




        try {
            cipher = Cipher.getInstance("AES");
            decipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(encryptionKey, "AES");


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        Glide.with(getApplicationContext())
                .load(details.getProfileImageUrl())
                .apply(options)
                .into(imageView);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ChatActivity.this, "Send Message", Toast.LENGTH_SHORT).show();
                if(message_ev.getText().toString().equals("")){
                    Toast.makeText(ChatActivity.this, "Add messages first", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessages(message_ev.getText().toString());
            }
        });

        initRecyclerView();

        firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            currentUser = task.getResult().toObject(UserDetails.class);

                        }
                    }
                });



    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessages();
    }

    private void getMessages(){
        Log.d(TAG, "getMessages: Called..");

        Log.d(TAG, "getMessages: ChatId : " + details.getChatId());
        if(details.getChatId() == null){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "no ChatId found", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference collectionReference = firebaseFirestore.collection(RegistrationActivity.CHAT_ROOM)
                .document(details.getChatId())
                .collection("Messages");

        mChatEventListener = collectionReference
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        // check exception
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        // get all the messages.
                        if(queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                Message message = doc.toObject(Message.class);
                                if(!mMessage_id.contains(message.getMessage_id())){
                                    mMessage_id.add(message.getMessage_id());
                                    listMessages.add(message);
                                    recyclerView.smoothScrollToPosition(listMessages.size() - 1);
                                }

                            }
                            adapter.notifyDataSetChanged();

                        }
                    }
                });


       /* // code to get data from firebase
         firebaseFirestore.collection(RegistrationActivity.CHAT_ROOM)
                .document(details.getChatId())
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                            Log.d(TAG, "onComplete: snapshots Size : " + snapshots.size());
                            for(DocumentSnapshot d : snapshots){
                                Message message;
                                message = d.toObject(Message.class);
                                listMessages.add(message);
                            }

                            Log.d(TAG, "onComplete: size of Message List : " +listMessages.size());

                          //  initRecyclerView();

                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChatActivity.this, "Unable to get Messages", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/


    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: Called");
        adapter = new MessageRVAdapter(listMessages, secretKeySpec);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //recyclerView.smoothScrollToPosition(listMessages.size()-1);
        progressBar.setVisibility(View.GONE);
    }

    private void sendMessages(String message ){
        Log.d(TAG, "sendMessages: Called");

        Message message1 = new Message();

        String encryptedMessage = encryptMessage(message);

        message1.setMessage(encryptedMessage);
        message1.setUserName(currentUser.getUserName());

        DocumentReference newMessageDoc = firebaseFirestore
                .collection(RegistrationActivity.CHAT_ROOM)
                .document(details.getChatId())
                .collection("Messages")
                .document();

        message1.setMessage_id(newMessageDoc.getId());

        newMessageDoc.set(message1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            message_ev.setText("");
                        }
                        else {
                            Toast.makeText(ChatActivity.this, "message not send", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private String encryptMessage(String message){
        Log.d(TAG, "encryptMessage: Called..");
        byte[] stringByte = message.getBytes();
        byte[] encryptedByte = new byte[stringByte.length];

        Log.d(TAG, "encryptMessage: Trying encryption...");

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedByte = cipher.doFinal(stringByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "encryptMessage: Encryption Complete.. converting byte");
        String cipherMessage = null;
        Log.d(TAG, "encryptMessage: converting Encrypted Byte to string..");

        try {
            cipherMessage = new String(encryptedByte, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "encryptMessage: Encryption Complete.. showing result..");

        return cipherMessage;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChatEventListener != null){
            mChatEventListener.remove();
        }
       /* if(mUserEventListener != null){
            mUserEventListener.remove();
        }*/
    }
}