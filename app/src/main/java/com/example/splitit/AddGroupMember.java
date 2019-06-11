package com.example.splitit;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddGroupMember extends AppCompatActivity {

    /*
    This page is the first page of creating a new group.
    Here is a listview of all members in the system, where each member is cliclable and hence selected for the group.
    We have not implemented the search function, as we only worked with small groups and few members, but as the project
    would have grown, the search function would hence be needed.
     */


    private Button nextButton;
    private Button backButton;

    private TextView topText;

    private static final String TAG = "UuerList";
    private String displayName;
    private String userkey;

    public ArrayList<String> usernamelist = new ArrayList<>();
    private ArrayList<String> userkeylist = new ArrayList<>();
    private ArrayList<String> userKeys = new ArrayList<>();
    private ArrayList<String> memberlist = new ArrayList<>();
    ArrayAdapter arrayAdapter;


    public ListView userListView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    public FirebaseAuth Auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        user = Auth.getCurrentUser();

        Intent intent = getIntent();

        userkey = intent.getExtras().getString("userkey");

        memberlist.add(usernameFromEmail(user.getEmail()));
        userKeys.add(userkey);

        userListView = (ListView) findViewById(R.id.userlistview);

        nextButton = (Button) findViewById(R.id.nextButton);
        backButton = (Button) findViewById(R.id.backButton);

        nextButton.setEnabled(false);
        nextButton.setVisibility(View.INVISIBLE);

        topText = (TextView) findViewById(R.id.textView4);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), homepage.class);
                startActivity(backIntent);
            }
        });


        readData(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> names, ArrayList<String> keys) {
                System.out.println(usernamelist);
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

        });


        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!memberlist.contains(usernamelist.get(position))) {
                    view.setBackgroundColor(Color.LTGRAY);
                    view.invalidate();
                    memberlist.add(usernamelist.get(position));
                    userKeys.add(userkeylist.get(position));

                }
                else {
                    memberlist.remove(usernamelist.get(position));
                    userKeys.remove(userkeylist.get(position));
                    view.setBackgroundColor(0x00000000);
                    view.invalidate();

                }
                changeClickability();
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent, from the MainActivity to AnotherActivity
                Intent intent = new Intent(AddGroupMember.this, NameGroupPage.class);

                intent.putStringArrayListExtra("grouplist", memberlist);
                intent.putStringArrayListExtra("userKeys", userKeys);

                // Invoking the intent, start AnotherActivity
                startActivity(intent);
            }
        });


    }


    private void changeClickability() {
        if (memberlist.size() > 1) {
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
        }
        else {
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.INVISIBLE);
        }

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
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                    myCallback.onCallback(usernamelist, userKeys);
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
