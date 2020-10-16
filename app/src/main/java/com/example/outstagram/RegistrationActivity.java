package com.example.outstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.outstagram.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    public static final String USERS_DATABASE = "User Database";
    public static final String CHAT_ROOM = "Chat Rooms";
    //widget
    private EditText ev_email, ev_password;
    private Button b_register, b_sign_in;
    private ProgressBar progressBar;

    //variables
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firebaseFirestore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ev_email = findViewById(R.id.ev_email_registration);
        ev_password = findViewById(R.id.ev_password_register);
        b_register = findViewById(R.id.b_register_register);
        b_sign_in = findViewById(R.id.b_sign_in_registration);
        progressBar = findViewById(R.id.progressBar_registration);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        b_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = ev_email.getText().toString();
                String password = ev_password.getText().toString();
                if(email.isEmpty()){
                    ev_email.setError("Please Enter Email Id!");
                    ev_email.requestFocus();
                    progressBar.setVisibility(View.GONE);

                }else if(password.isEmpty()){
                    ev_password.setError("Please Enter Password!");
                    ev_password.requestFocus();
                    progressBar.setVisibility(View.GONE);


                }
                else if(email.isEmpty() || password.isEmpty()){
                    toastMessage("All Fields are Empty.");
                    progressBar.setVisibility(View.GONE);

                }
                else if(!email.isEmpty() && !password.isEmpty()){
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);

                            if(!task.isSuccessful()){
                                toastMessage("SignUp Unsuccessful, Please try again.");
                            }else{

                                String uId = firebaseAuth.getCurrentUser().getUid().toString();
                                saveUserDataInFirestore(uId);

                                toastMessage("SignUp Successful.");
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);


                            }
                        }
                    });

                }else{
                    toastMessage("Error occurred!");
                }
            }
        });

        b_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);

                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveUserDataInFirestore(String uId){
        String email  = ev_email.getText().toString().trim();

       UserDetails userDetails = new UserDetails();
       userDetails.setEmail(email);
       userDetails.setUserId(uId);
       userDetails.setName(email.substring(0, email.indexOf("@")));
       userDetails.setUserName(email.substring(0, email.indexOf("@")));
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(USERS_DATABASE)
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                .set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully entered the data.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Error: " + e.getMessage() );
            }
        });

    }
}