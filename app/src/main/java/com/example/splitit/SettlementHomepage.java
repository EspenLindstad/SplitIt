package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettlementHomepage extends AppCompatActivity {
    /*
    This activity is the homepage for each group.
    Here one can add expenses, delete expenses, see the settlement, add users to the group and
    delete users from the group(By pressing at yourself in the listview)
     */

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String groupKey;

    private ArrayList<String> groupMembers;

    private Button addBtn;
    private Button goToSettlementBtn;
    private Button deleteBtn;
    private Button homeBtn;
    private Button plusBtn;

    private TextView payNextPerson;
    private TextView toptext;

    private Map<String, Integer> userMap = new HashMap<>();

    private ArrayAdapter arrayAdapter;

    public ListView userListView;

    public void onResume() {
        super.onResume();

        System.out.println("Kjorer vi her etter add member?");

        Intent intent = getIntent();

        groupKey = intent.getExtras().getString("groupKey");

        groupMembers = intent.getStringArrayListExtra("groupmembers");

        userListView = (ListView) findViewById(R.id.groupmembersListView);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference docRef = db.collection("groups").document(groupKey);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> groupkeys = (ArrayList<String>) documentSnapshot.get("groupKeys");

                        Intent addIntent = new Intent(getApplicationContext(), AddNewGroupMember.class);
                        addIntent.putStringArrayListExtra("groupMembers", groupMembers);
                        addIntent.putStringArrayListExtra("groupkeys", groupkeys);
                        addIntent.putExtra("groupKey", groupKey);
                        startActivity(addIntent);
                    }
                });

            }
        });

        goToSettlementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settlementIntent = new Intent(getApplicationContext(), SeeSettlement.class);
                settlementIntent.putExtra("groupKey", groupKey);
                startActivity(settlementIntent);
            }
        });

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (groupMembers == null) {
                    groupMembers = (ArrayList<String>) documentSnapshot.get("groupList");
                }
                toptext = (TextView) findViewById(R.id.textView22);
                toptext.setText(documentSnapshot.toObject(Group.class).getName());
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);
                userListView.setAdapter(arrayAdapter);

                ArrayList<Double> settlementArr = documentSnapshot.toObject(Group.class).getSettlementArr();

                userMap = documentSnapshot.toObject(Group.class).getUserMap();

                if(settlementArr.isEmpty()){
                    for(int i = 0; i < userMap.size()*userMap.size(); i++){
                        settlementArr.add(0.0);
                    }
                }
                String payNext = documentSnapshot.toObject(Group.class).whoShouldPayNext(settlementArr, userMap);
                payNextPerson.setText(payNext);

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(getApplicationContext(), ExchangeActivity.class);
                newIntent.putExtra("groupKey", groupKey);
                startActivity(newIntent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newDIntent = new Intent(getApplicationContext(), DeleteExpenseActivity.class);
                newDIntent.putExtra("groupKey", groupKey);
                startActivity(newDIntent);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement_homepage);

        addBtn = (Button) findViewById(R.id.addBtn);
        deleteBtn = (Button) findViewById(R.id.deleteBtn);
        plusBtn = (Button) findViewById(R.id.plusBtn);
        payNextPerson = (TextView) findViewById(R.id.userTextView);

        goToSettlementBtn = (Button) findViewById(R.id.goToSettlementBtn);

        homeBtn = (Button) findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(getApplicationContext(), homepage.class);
                startActivity(homeIntent);
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
