package com.example.outstagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.outstagram.models.PostData;
import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import static com.example.outstagram.ProfileFragment.REQUEST_CODE_PICK_IMAGE;

public class NewPostActivity extends AppCompatActivity {
    private static final String TAG = "NewPostActivity";

    public static final String POST_DATABASE = "Post Database";
    public static final int REQUEST_CODE_PICK_POST_IMAGE = 9002;
    public static final String  KEY_POST_URL = "postImageUrl";

    private ImageView postImage;
    private EditText caption_ev;
    private Button post_button;
    private ProgressBar progressBar;

    private UserDetails currentUser;
    private Uri postImageUri;
    private StorageReference mStorageRef;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        postImage = findViewById(R.id.imageView_post);
        caption_ev = findViewById(R.id.description_post);
        post_button = findViewById(R.id.button_post_post_act);
        progressBar = findViewById(R.id.progress_bar_post_activity);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

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

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_POST_IMAGE);
    }

    private void uploadData(){


        if (postImageUri == null) {
            Toast.makeText(getApplicationContext(), "Select an image to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //When post is clicked
        final StorageReference profile = mStorageRef.child("posts/" + System.currentTimeMillis());
        profile.putFile(postImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: image save was successful");
                        // save the profile link in user data base
                        profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: Setting the image url to the database");

                                HashMap<String, String> profileMap = new HashMap<>();
                               profileMap.put(KEY_POST_URL, uri.toString());

                                PostData postData = new PostData();
                                postData.setDescription(caption_ev.getText().toString());
                                postData.setUsername(currentUser.getUserName());
                                postData.setImageUrl(currentUser.getProfileImageUrl());
                                postData.setPostImageUrl(uri.toString());
                                DocumentReference document = firebaseFirestore.collection(POST_DATABASE)
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Posts")
                                        .document();

                                postData.setPostId(document.getId());

                                document.set(postData);

                                Map<String , String> postId = new HashMap<>();
                                postId.put("PostId", document.getId());

                                firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("Posts")
                                        .document(document.getId())
                                        .set(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(NewPostActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                    }
                                });

                                Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.GONE);

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_POST_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Log.d(TAG, "onActivityResult: Pick image successful");
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: crop request OK : Setting image in imageView");
                postImageUri = result.getUri();
                Glide.with(NewPostActivity.this)
                        .load(postImageUri)
                        .into(postImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Log.d(TAG, "onActivityResult: error : " + error.getMessage());
            }
        }
    }

}