package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SettlementHomepage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String groupKey;

    private ArrayList<String> groupMembers;

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


        System.out.println("this is the groupKey: " + groupKey);

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupMembers = documentSnapshot.toObject(Group.class).getGroupList();
                System.out.println("These are my mfuckin gmember: " + groupMembers);

                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);
                userListView.setAdapter(arrayAdapter);
            }
        });


    }


    public void readGroup(GroupCallback groupCallback) {
        final Task<QuerySnapshot> querySnapshotTask = db.collection("groups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            groupKey = document.getId();
                        }
                    } else {
                        Log.w("WTF", "Error getting documents.", task.getException());
                    }
                    groupCallback.onCallback(groupKey);
                });
    }


}
