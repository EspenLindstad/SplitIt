package com.example.splitit;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNewGroupMember extends AppCompatActivity {

    private TextView toptext;

    private Button doneBtn;
    private Button backBtn;

    private ListView userlist;

    private ArrayList<String> memberlist;
    private ArrayList<String> memberKeys;
    private ArrayList<String> newMembersList = new ArrayList<>();
    private ArrayList<String> newMembersKeys = new ArrayList<>();

    private ArrayList<String> usernamelist = new ArrayList<>();
    private ArrayList<String> userkeylist = new ArrayList<>();

    private String groupKey;
    private String displayName;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    public FirebaseAuth Auth = FirebaseAuth.getInstance();

    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_group_member);

        user = Auth.getCurrentUser();

        toptext = (TextView) findViewById(R.id.addnewmembertextview);

        doneBtn = (Button) findViewById(R.id.finishBtn);
        doneBtn.setEnabled(false);
        backBtn = (Button) findViewById(R.id.backBtn);

        userlist = (ListView) findViewById(R.id.userlistListView);

        Intent intent = getIntent();

        groupKey = intent.getExtras().getString("groupKey");
        memberlist = intent.getExtras().getStringArrayList("groupMembers");
        memberKeys = intent.getExtras().getStringArrayList("groupkeys");


        readData(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> names, ArrayList<String> keys) {
                for (String name : memberlist) {
                    if (usernamelist.contains(name)) {
                        usernamelist.remove(name);
                    }
                }
                for (String key : memberKeys) {
                    if (userkeylist.contains(key)) {
                        userkeylist.remove(key);
                    }
                }
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, usernamelist);
                userlist.setAdapter(arrayAdapter);

            }

        });

        userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!memberlist.contains(usernamelist.get(position))) {
                    view.setBackgroundColor(Color.LTGRAY);
                    view.invalidate();
                    memberlist.add(usernamelist.get(position));
                    memberKeys.add(userkeylist.get(position));
                    newMembersList.add((usernamelist.get(position)));
                    newMembersKeys.add(userkeylist.get(position));

                }
                else {
                    memberlist.remove(usernamelist.get(position));
                    memberKeys.remove(userkeylist.get(position));
                    newMembersList.remove(usernamelist.get(position));
                    newMembersKeys.remove(userkeylist.get(position));
                    view.setBackgroundColor(0x00000000);
                    view.invalidate();

                }
                changeClickability();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                backIntent.putExtra("groupKey", groupKey);
                startActivity(backIntent);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroup(newMembersList, newMembersKeys);

                Intent doneIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                doneIntent.putExtra("groupKey", groupKey);
                doneIntent.putStringArrayListExtra("groupmembers", memberlist);
                startActivity(doneIntent);

            }
        });

    }

    public void readData(MyCallback myCallback) {


        final Task<QuerySnapshot> querySnapshotTask = db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (user != null) {
                                // User is Login
                                displayName = usernameFromEmail(user.getEmail());

                            }

                            String name = document.get("name").toString();
                            String uid = document.get("userID").toString();

                            if (!name.equals(displayName)) {
                                usernamelist.add(name);
                                userkeylist.add(uid);
                            }
                        }
                    } else {
                    }
                    myCallback.onCallback(usernamelist, userkeylist);
                });
    }

    private void updateGroup(ArrayList<String> newMembersList, ArrayList<String> newMembersKeys) {
        //gName = ((TextView) findViewById(R.id.editText)).getText().toString();
        DocumentReference docRef = db.collection("groups").document(groupKey);
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group group = documentSnapshot.toObject(Group.class);
                Map<String, Integer> groupMap = group.getUserMap();
                ArrayList<String> members = group.getGroupList();
                ArrayList<String> keys = group.getGroupKeys();
                ArrayList<Double> settlementArr = group.getSettlementArr();
                String groupName = group.getName();

                for (int i = 0; i < newMembersKeys.size(); i++) {
                    group.addGroupMember(newMembersList.get(i), newMembersKeys.get(i), settlementArr);
                    addUserToSettlement(groupKey, newMembersKeys.get(i), groupName);
                }

                Map<String, ArrayList<String>> newGroupMap = new HashMap<>();
                newGroupMap.put("groupList", group.getGroupList());
                newGroupMap.put("groupKeys", group.getGroupKeys());

                db.collection("groups").document(groupKey).set(newGroupMap, SetOptions.merge());
                db.collection("groups").document(groupKey).update("members", group.getMembers());
                db.collection("groups").document(groupKey).update("userMap", group.getUserMap());
                db.collection("groups").document(groupKey).update("settlementArr", group.getSettlementArr());
            }
        });
    }


    public void addUserToSettlement(String groupKey, String userKey, String gname) {

        DocumentReference docRef = db.collection("users").document(userKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.get("usersSettlements") == null) {
                    Map<String, ArrayList<String>> settlementMap = new HashMap<>();
                    ArrayList<String> partOf = new ArrayList<>();
                    partOf.add(gname);
                    partOf.add(groupKey);
                    settlementMap.put("usersSettlements", partOf);
                    db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());
                }
                else {
                    Map<String, ArrayList<String>> settlementMap = new HashMap<>();
                    ArrayList<String> memberOf = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                    memberOf.add(gname);
                    memberOf.add(groupKey);
                    settlementMap.put("usersSettlements", memberOf);
                    db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());
                }



            }
        });

    }

    private void changeClickability() {
        if (newMembersList.size() >= 1) {
            doneBtn.setEnabled(true);
        }
        else {
            doneBtn.setEnabled(false);
        }

    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
