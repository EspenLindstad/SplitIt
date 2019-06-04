package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddNewGroupMember extends AppCompatActivity {

    private TextView toptext;

    private Button doneBtn;
    private Button backBtn;

    private ListView userlist;

    private ArrayList<String> memberlist = new ArrayList<>();
    private ArrayList<String> memberKeys = new ArrayList<>();

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
        backBtn = (Button) findViewById(R.id.backBtn);

        userlist = (ListView) findViewById(R.id.userlistListView);

        Intent intent = getIntent();

        groupKey = intent.getExtras().getString("groupKey");
        memberlist = intent.getExtras().getStringArrayList("groupMembers");

        System.out.println(memberlist);

        readData(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> names, ArrayList<String> keys) {
                System.out.println(usernamelist);
                for (String name : memberlist) {
                    if (usernamelist.contains(name)) {
                        usernamelist.remove(name);
                    }
                }
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, usernamelist);
                userlist.setAdapter(arrayAdapter);
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
                        Log.w("Bosj", "Error getting documents.", task.getException());
                    }
                    myCallback.onCallback(usernamelist, userkeylist);
                });
    }


    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
