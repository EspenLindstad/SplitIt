package com.example.splitit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NameGroupPage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView title;
    private TextView groupName;
    private TextView participants;
    private TextView groupnameInput;

    private ArrayList<String> memberlist;
    private ArrayList<String> userKeys;

    private Button backButton;
    private Button doneButton;

    private String uniqueKey;
    private String groupKey;

    public ListView participantsView;

    ArrayAdapter arrayAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference groupsRef = database.getReference("groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_group_page);

        title = (TextView) findViewById(R.id.titleTextView);
        groupName = (TextView) findViewById(R.id.groupnameTextView);
        participants = (TextView) findViewById(R.id.participantsTextView);


        backButton = (Button) findViewById(R.id.button2);
        doneButton = (Button) findViewById(R.id.button);

        participantsView = (ListView) findViewById(R.id.usersList);


        Intent intent = getIntent();

        memberlist = intent.getStringArrayListExtra("grouplist");
        System.out.println("this is the memberlist: " + memberlist);
        userKeys = intent.getStringArrayListExtra("userKeys");
        System.out.println("this is the userkeys: " + userKeys);

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, memberlist);
        participantsView.setAdapter(arrayAdapter);

        readGroup(key -> {
            doneButton.setOnClickListener(view -> {
                String name = ((TextView) findViewById(R.id.editText)).getText().toString();
                writeNewGroup(name, memberlist, userKeys);

                Intent nextIntent = new Intent(getApplicationContext(), SettlementHomepage.class);

                nextIntent.putExtra("groupKey", key);

                startActivity(nextIntent);
                finish();
            });
        });



    }


    public void readGroup(GroupCallback groupCallback) {
        final Task<QuerySnapshot> querySnapshotTask = db.collection("groups")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            groupKey = document.getId();
                            System.out.println("Groupkey: " + groupKey);
                        }
                    } else {
                        Log.w("WTF", "Error getting documents.", task.getException());
                    }
                    groupCallback.onCallback(groupKey);
                });
    }


    private void writeNewGroup(String gName, ArrayList<String> members, ArrayList<String> userKeys) {

        uniqueKey = groupsRef.push().getKey();
        Group group = new Group(uniqueKey, gName, members);
        System.out.println("This is the groupKey: " + group.getKey());
        db.collection("groups")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        uniqueKey = documentReference.getId();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failure", "Error adding document", e);
                    }
                });

    }

}
