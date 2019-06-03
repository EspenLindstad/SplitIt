package com.example.splitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    private ArrayList<String> testNames;
    private ArrayList<String> testIds;
    ArrayAdapter arrayAdapter;

    private String uid;
    private String userkey;

    public ListView GroupListView;

    private Map<String, String> testMap;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashbboard:
                    mTextMessage.setText(R.string.title_profile);
                    GroupListView.setVisibility(View.GONE);
                    signOutBtn.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_settlements);
                    GroupListView.setVisibility(View.VISIBLE);
                    signOutBtn.setVisibility(View.GONE);
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
            public void onCallback(ArrayList<String> names, ArrayList<String> ids) {
                if (names != null) {
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, names);
                    GroupListView.setAdapter(arrayAdapter);
                    GroupListView.setVisibility(View.VISIBLE);



                    GroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Intent nextIntent = new Intent(getApplicationContext(), SettlementHomepage.class);

                            nextIntent.putExtra("groupKey", ids.get(position));

                            startActivity(nextIntent);
                        }
                    });
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
        signOutBtn.setVisibility(View.GONE);

        GroupListView = (ListView) findViewById(R.id.GroupListView);
        GroupListView.setVisibility(View.VISIBLE);

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

                intent.putExtra("userkey", userkey);

                // Invoking the intent, start AnotherActivity
                startActivity(intent);
            }
        });

        /*
        readData(new PartOfInterface() {
            @Override
            public void onCallback(ArrayList<String> names, ArrayList<String> ids) {
                if (names != null) {
                    arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, names);
                    GroupListView.setAdapter(arrayAdapter);
                }
            }

        });
        */
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
                            ArrayList<String> tull = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                            //partOf = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                            System.out.println("Part of: " + tull);

                            testNames = new ArrayList<>();
                            testIds = new ArrayList<>();

                            if (tull != null) {
                                //Funker ikke med ny bruker
                                for (int a = 0; a<tull.size();a++) {
                                    if (a % 2 == 0) {
                                        testNames.add(tull.get(a));
                                    }
                                    else {
                                        testIds.add(tull.get(a));
                                    }
                                }

                                System.out.println("names: " + testNames);
                                System.out.println("ids: " + testIds);
                            }


                            partOfInterface.onCallback(testNames, testIds);


                        }
                    });
                }
            }
        });

    }




}