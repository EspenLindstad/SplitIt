package com.example.splitit;

import android.content.ClipData;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddGroupMember extends AppCompatActivity {


    private Button nextButton;
    private Button backButton;

    private TextView topText;

    public FirebaseAuth mAuth;


    private static final String TAG = "UserList";
    private DatabaseReference userlistReference;
    private ValueEventListener mUserListListener;
    private ArrayList<String> usernamelist = new ArrayList<>();
    private ArrayList<String> userKeys = new ArrayList<>();
    private ArrayList<String> memberlist = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    ;

    public ListView userListView;

    public TextView titleTextView;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);


        userlistReference = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userListView = (ListView) findViewById(R.id.userlistview);

        nextButton = (Button) findViewById(R.id.nextButton);
        backButton = (Button) findViewById(R.id.backButton);

        nextButton.setEnabled(false);

        TextView topText = (TextView) findViewById(R.id.textView4);


        readData(new MyCallback() {
            @Override
            public void onCallback(ArrayList<String> names, ArrayList<String> keys) {
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

        });



        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!memberlist.contains(usernamelist.get(position))) {
                    memberlist.add(usernamelist.get(position));

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

        /*

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberList.add(1);
                changeClickability(memberList, nextButton);
            }
        });
        */


    }


    private void changeClickability() {
        if (!memberlist.isEmpty()) {
            nextButton.setEnabled(true);
        }

    }

    public void readData(MyCallback myCallback) {
        final Task<QuerySnapshot> querySnapshotTask = db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String temp = document.get("name").toString();
                            usernamelist.add(temp);
                            userKeys.add(document.getId());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                    myCallback.onCallback(usernamelist, userKeys);
                });
    }
    /*
    private void loadUsers(MyCallback myCallback) {
        final Task<QuerySnapshot> querySnapshotTask = userRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onDataChange(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<User> types = documentSnapshots.toObjects(User.class);

                            // Add all to your list
                            userlist.addAll(types);
                            Log.d(TAG, "onSuccess: " + userlist);
                            MyCallback.onCallback(userlist);

                        }
                    }

                });
    }
    */
}