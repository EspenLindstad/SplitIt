package com.example.splitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {

    private DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final static String TAG = "MAIN";
    private FirebaseFirestore db;

    private String key;
    private String uid;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private Button backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        Button newUserBtn = (Button) findViewById(R.id.newUserBtn);
        backBtn = (Button) findViewById(R.id.signupBackBtn);



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(backIntent);
            }
        });

      newUserBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = ((EditText) findViewById(R.id.edit_email)).getText().toString();
                String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();
                String password_auth = ((EditText) findViewById(R.id.edit_password_auth)).getText().toString();

                firstName = ((EditText) findViewById(R.id.userFirstName)).getText().toString();
                lastName = ((EditText) findViewById(R.id.userLastName)).getText().toString();
                phoneNumber = ((EditText) findViewById(R.id.userPhoneNumber)).getText().toString();

                if (password.equals(password_auth)) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

//Ifsigninfails,displayamessagetotheuser.Ifsigninsucceeds
//theauthstatelistenerwillbenotifiedandlogictohandlethe
//signedinusercanbehandledinthelistener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(signUp.this, "AuthenticationFailed",
                                                Toast.LENGTH_SHORT).show();
                                        Toast.makeText(signUp.this, "Password needs to consist of Numbers and digits",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        onAuthSuccess(task.getResult().getUser());
                                        showUserList();
                                    }
                                }


                            });
                }
                else{
                    Toast.makeText(signUp.this, "Please type in same password",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public void showUserList(){
        //Her skal jeg sende med brukernøkkelen

    }

    private void onAuthSuccess(FirebaseUser user) {

        String username = usernameFromEmail(user.getEmail());

        uid = user.getUid();

        // Write new user
        writeNewUser(username, user.getEmail());

        // Go to MainActivity
    }


    public String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String name, String email) {
        User user = new User(name, email);


        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        key = documentReference.getId();

                        user.setUserID(key);
                        user.setUserUid(mAuth.getUid());

                        uid = mAuth.getUid();

                        Map<String, Object> userMap = new HashMap<>();

                        userMap.put("userID", key);
                        userMap.put("Uid", uid);


                        db.collection("users").document(key).set(userMap, SetOptions.merge());

                        Intent intent = new Intent(getApplicationContext(), homepage.class);
                        intent.putExtra("userKey", key);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("email", email);



                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });







    }


}
