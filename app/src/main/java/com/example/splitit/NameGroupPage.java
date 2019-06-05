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
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameGroupPage extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView title;
    private TextView groupName;
    private TextView participants;
    private TextView groupnameInput;

    private ArrayList<String> memberlist;
    private ArrayList<String> userKeys;
    private ArrayList<String> memberKeys;

    private Button backButton;
    private Button doneButton;

    private String baseCurrency;


    private String uniqueKey;
    private String groupKey;
    private String name;
    private String userkey;

    private Map<String, Integer> userMap;
    Map<String, String> expenseNameMap = new HashMap<>();
    Map<String, Double> expenseMap = new HashMap<>();
    Map<String, ArrayList<String>> participantsMap = new HashMap<>();
    Map<String, String> userWhoPayedMap = new HashMap<>();

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
        doneButton = (Button) findViewById(R.id.doneBtn);

        participantsView = (ListView) findViewById(R.id.usersList);

        final Spinner fromSpinner = (Spinner) findViewById(R.id.baseCurrencySpinner);


        Intent intent = getIntent();

        memberlist = intent.getStringArrayListExtra("grouplist");
        userKeys = intent.getStringArrayListExtra("userKeys");

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, memberlist);
        participantsView.setAdapter(arrayAdapter);
        userMap = initializeMap(memberlist);



        doneButton.setOnClickListener(view -> {
                    name = ((TextView) findViewById(R.id.editText)).getText().toString();

                    baseCurrency = fromSpinner.getSelectedItem().toString();
                    writeNewGroup(name, memberlist, userKeys, userMap, expenseNameMap, expenseMap, participantsMap, userWhoPayedMap, baseCurrency);

                });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), AddGroupMember.class);
                startActivity(backIntent);
            }
        });
            }


    private Map<String, Integer> initializeMap(ArrayList groupList){
        Map<String, Integer> userMap = new HashMap<>();
        for (int i = 0; i < groupList.size(); i++) {
            userMap.put(groupList.get(i).toString(), i);
        }
        return userMap;
    }

    private void writeNewGroup(String gName, ArrayList<String> members, ArrayList<String> memberKeys, Map<String, Integer> userMap, Map<String, String> expenseNameMap, Map<String, Double> expenseMap, Map<String, ArrayList<String>> participantsMap, Map<String, String> userWhoPayedMap, String baseCurrency) {
        //gName = ((TextView) findViewById(R.id.editText)).getText().toString();
        System.out.println("This is the groupname: " + gName);
        Group group = new Group(gName, members, memberKeys, userMap, expenseNameMap, expenseMap, participantsMap, userWhoPayedMap, baseCurrency);
        db.collection("groups")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        uniqueKey = documentReference.getId();
                        System.out.println("Dette er nøkkelen i writenewgroup: " + uniqueKey);

                        System.out.println("These are the memberkeys: " + memberKeys);
                        System.out.println("These are the membernames: " + memberlist);

                        if (memberKeys.contains(null)) {
                            memberKeys.remove(null);
                        }

                        for (String member : memberKeys) {
                            addUserToSettlement(uniqueKey, member, ((TextView) findViewById(R.id.editText)).getText().toString());
                        }

                        Intent nextIntent = new Intent(getApplicationContext(), SettlementHomepage.class);

                        nextIntent.putExtra("groupKey", uniqueKey);

                        System.out.println("intentkey: " + uniqueKey);

                        startActivity(nextIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failure", "Error adding document", e);
                    }
                });


    }


    public void addUserToSettlement(String groupKey, String userKey, String gname) {

        DocumentReference docRef = db.collection("users").document(userKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //System.out.println("Class of usersting: " + documentSnapshot.get("usersSettlements").getClass());


                if (documentSnapshot.get("usersSettlements") == null) {
                    Map<String, ArrayList<String>> settlementMap = new HashMap<>();
                    ArrayList<String> partOf = new ArrayList<>();
                    partOf.add(gname);
                    partOf.add(groupKey);
                    settlementMap.put("usersSettlements", partOf);
                    db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());
                }
                else {
                    //Funker ikke når brukeren er ny
                    Map<String, ArrayList<String>> settlementMap = new HashMap<>();
                    ArrayList<String> memberOf = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                    System.out.println("Test: " + memberOf.isEmpty());
                    memberOf.add(gname);
                    memberOf.add(groupKey);
                    settlementMap.put("usersSettlements", memberOf);
                    db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());

                    System.out.println("Ting funker ja");
                }



            }
        });

    }


}
