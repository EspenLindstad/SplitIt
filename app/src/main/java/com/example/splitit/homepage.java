package com.example.splitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class homepage extends AppCompatActivity {

    private TextView mTextMessage;

    private Button signOutBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");
    private FirebaseUser user;
    public FirebaseAuth Auth = FirebaseAuth.getInstance();

    private ArrayList<String> grouplist;
    private ArrayList<String> partOf;
    ArrayAdapter arrayAdapter;

    private String uid;
    private String userkey;

    public ListView GroupListView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_settlements);
                    GroupListView.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashbboard:
                    mTextMessage.setText(R.string.title_profile);
                    GroupListView.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_userlist:
                    mTextMessage.setText(R.string.title_userlist);
                    Intent intent = new Intent(homepage.this, UserList.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onResume(){
        super.onResume();

        readData(new PartOfInterface() {
            @Override
            public void onCallback(ArrayList<String> groups) {
                if (partOf != null) {
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, partOf);
                    GroupListView.setAdapter(arrayAdapter);
                }
            }

        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        user = Auth.getCurrentUser();

        final Button addSettlement = (Button) findViewById(R.id.addSettlement);

        signOutBtn = (Button) findViewById(R.id.logoutBtn);

        GroupListView = (ListView) findViewById(R.id.GroupListView);
        GroupListView.setVisibility(View.INVISIBLE);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    mAuth.getInstance().signOut();
                    Intent intent = new Intent(homepage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        addSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent, from the MainActivity to AnotherActivity
                Intent intent = new Intent(homepage.this, AddGroupMember.class);

                // Invoking the intent, start AnotherActivity
                startActivity(intent);
            }
        });

        readData(new PartOfInterface() {
            @Override
            public void onCallback(ArrayList<String> groups) {
                if (partOf != null) {
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, partOf);
                    GroupListView.setAdapter(arrayAdapter);
                }
            }

        });
    }

    public void readData(PartOfInterface partOfInterface) {
        // Create a reference to the cities collection
        CollectionReference userRef = db.collection("users");
        // Create a query against the collection.
        Query query = userRef.whereEqualTo("Uid", user.getUid());
        // retrieve  query results asynchronously using query.get()
        Task<QuerySnapshot> querySnapshotTask = query.get();

        querySnapshotTask.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : querySnapshotTask.getResult().getDocuments()) {
                    userkey = document.getId();

                    DocumentReference docRef = db.collection("users").document(userkey); //Denne må fikses
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            partOf = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                            System.out.println("Part of: " + partOf);

                            partOfInterface.onCallback(partOf);

                        }
                    });
                }
            }
        });

    }




}
