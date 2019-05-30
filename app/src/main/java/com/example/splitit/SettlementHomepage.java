package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SettlementHomepage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseAuth Auth = FirebaseAuth.getInstance();
    public FirebaseUser user;

    private String groupKey;
    private String memberkey;

    private ArrayList<String> groupMembers;
    private ArrayList<String> memberKeys;
    private ArrayList<String> partOf;

    private Group group;


    ArrayAdapter arrayAdapter;


    public ListView userListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_homepage);

        userListView = (ListView) findViewById(R.id.groupmembersListView);

        Intent intent = getIntent();

        groupKey = intent.getExtras().getString("groupKey");

        user = Auth.getCurrentUser();



        System.out.println("this is the groupKey: " + groupKey);


        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupMembers = documentSnapshot.toObject(Group.class).getGroupList();
                memberKeys = documentSnapshot.toObject(Group.class).getGroupKeys();

                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);
                userListView.setAdapter(arrayAdapter);

                for (String member : memberKeys) {
                    readData(new GroupCallback() {
                        @Override
                        public void onCallback(String key) {
                            addUserToSettlement(groupKey, member);
                        }
                    });

                }
            }
        });



    }


    public void readData(GroupCallback groupCallback) {

        groupCallback.onCallback(groupKey);
    }


    private void addUserToSettlement(String groupKey, String userKey) {
        DocumentReference docRef = db.collection("users").document(userKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                partOf = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                System.out.println("Part of: " + partOf);

                partOf.add(groupKey);

                Map<String, Object> settlementMap = new HashMap<>();
                settlementMap.put("usersSettlements", partOf);
                db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());

                System.out.println("Ting funker ja");

            }
        });
    }




}
