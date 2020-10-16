package com.example.outstagram;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.outstagram.adapters.ProfileRVAdapter;
import com.example.outstagram.models.PostData;
import com.example.outstagram.models.UserDetails;
import com.example.outstagram.util.UserClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    public static final int REQUEST_CODE_PICK_IMAGE = 9001;
    public static final String KEY_PROFILE_URL = "profileImageUrl";

    private Button button;
    private ImageView imageView;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;


    private List<PostData> postDataList = new ArrayList<>();
    private ProfileRVAdapter adapter;
    private StorageReference mStorageRef;
    Uri imageUri;
    private FirebaseFirestore firebaseFirestore;
    String picUrl;
    boolean isUploaded = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.image_view_profile);
        button = view.findViewById(R.id.button_post_profile);
        progressBar = view.findViewById(R.id.progress_bar_profile);
        progressBar.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recycler_view_profile_frag);


        adapter = new ProfileRVAdapter(postDataList, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        initWidgets();
        getProfileFromFireStore();


        UserClient client = UserClient.getInstance();
        UserDetails details = client.getCurrentUser();
        Log.d(TAG, "onCreateView: getting post details");
        firebaseFirestore.collection(NewPostActivity.POST_DATABASE)
                .document(details.getUserId())
                .collection("Posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<DocumentSnapshot> snapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot s : snapshots){
                                PostData data = new PostData();
                                data = s.toObject(PostData.class);

                                postDataList.add(data);
                            }
                            Log.d(TAG, "onComplete: size of postDataList  : " + postDataList.size());
                            Collections.reverse(postDataList);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

        return view;
    }

    private void getProfileFromFireStore(){
        progressBar.setVisibility(View.VISIBLE);
        DocumentReference picRef =  firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        picRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserDetails details = new UserDetails();
                    details = task.getResult().toObject(UserDetails.class);
                    if(details.getProfileImageUrl() != null){
                        Log.d(TAG, "onComplete: profile pic is not empty...");
                        picUrl = details.getProfileImageUrl();
                        Log.d(TAG, "onComplete: pic Url: " + picUrl);

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round);

                        Log.d(TAG, "onComplete: loading image from url..");
                        Glide.with(getContext())
                                .load(picUrl)
                                .apply(options)
                                .into(imageView);

                    }else {
                        Log.d(TAG, "onComplete: No profile pic to show..");
                        Toast.makeText(getContext(), "No Profile Pic", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }

                //progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initWidgets(){
        Log.d(TAG, "initWidgets: Init listeners");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: starting image selection..");
                pickImage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isUploaded){
                    isUploaded = true;
                    uploadData();

                }else {
                    Toast.makeText(getContext(), "Already uploaded..", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        isUploaded = false;
    }

    private void uploadData(){


        if (imageUri == null) {
            Toast.makeText(getContext(), "Select an image for profile", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //When post is clicked
        final StorageReference profile = mStorageRef.child("profile/" + System.currentTimeMillis()
                + "." + getFileExtension(imageUri));
        profile.putFile(imageUri)
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
                                profileMap.put(KEY_PROFILE_URL, uri.toString());

                                DocumentReference document = firebaseFirestore.collection(RegistrationActivity.USERS_DATABASE)
                                        .document(FirebaseAuth.getInstance().getUid());
                                document.set(profileMap, SetOptions.merge());
                                Toast.makeText(getContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
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
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        //On progress
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Log.d(TAG, "onActivityResult: Pick image successful");
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: crop request OK : Setting image in imageView");
                imageUri = result.getUri();
                Glide.with(getActivity())
                        .load(imageUri)
                        .into(imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Log.d(TAG, "onActivityResult: error : " + error.getMessage());
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
