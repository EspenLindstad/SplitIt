package com.example.splitit;

import android.content.ClipData;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AddGroupMember extends AppCompatActivity {

    private ArrayList<String> memberList = new ArrayList<>();


    private Button nextButton;
    private Button backButton;

    private TextView topText;

    public FirebaseAuth mAuth;


    private static final String TAG = "UserList";
    private DatabaseReference userlistReference;
    private ValueEventListener mUserListListener;
    ArrayList<String> usernamelist = new ArrayList<>();
    ArrayAdapter arrayAdapter;
    ;

    public ListView userListView;

    public TextView titleTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        userlistReference = FirebaseDatabase.getInstance().getReference().child("users");
        userListView = (ListView) findViewById(R.id.userlistview);
        onStart();

        nextButton = (Button) findViewById(R.id.nextButton);
        backButton = (Button) findViewById(R.id.backButton);

        nextButton.setEnabled(false);

        TextView topText = (TextView) findViewById(R.id.textView4);



        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent, from the MainActivity to AnotherActivity
                Intent intent = new Intent(AddGroupMember.this, NameGroupPage.class);

                intent.putStringArrayListExtra("grouplist", memberList);

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

    protected void onStart() {
        super.onStart();
        final ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Log.i(TAG, "user " + snapshot);
                    Log.d("item id ",snapshot.child("name").getValue().toString());
                    usernamelist.add(snapshot.child("name").getValue().toString());
                    //System.out.println(snapshot);
                }
                Log.i(TAG, "onDataChange: " + usernamelist.toString());
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to load User list.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        userlistReference.addValueEventListener(userListener);

        mUserListListener = userListener;

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int posID = (int)id;

                if (!memberList.contains(usernamelist.get(position))) {
                    memberList.add(usernamelist.get(position));
                }
                changeClickability();
                System.out.println(memberList);

            }
        });
    }

    private void changeClickability() {
        if (!memberList.isEmpty()) {
            nextButton.setEnabled(true);
        }

    }



}
