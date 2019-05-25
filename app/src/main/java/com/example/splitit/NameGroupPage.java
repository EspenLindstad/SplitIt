package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NameGroupPage extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private TextView title;
    private TextView groupName;
    private TextView participants;
    private TextView groupnameInput;

    private ArrayList<String> memberlist;

    private Button backButton;
    private Button doneButton;

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

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, memberlist);
        participantsView.setAdapter(arrayAdapter);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupnameInput = ((TextView) findViewById(R.id.editText)).getText().toString();
                writeNewGroup(groupnameInput, memberlist);

                startActivity(new Intent(getApplicationContext(), homepage.class));
                finish();
            }
        });

    }

    private void writeNewGroup(String gName, ArrayList members) {

        HashMap<String, Object> result = new HashMap<>();

        Group group = new Group(gName, members);
        String uniqueKey = groupsRef.push().getKey();
        groupsRef.child(uniqueKey).setValue("Name: " + group.getGroupName());
        groupsRef.child(uniqueKey).child("Members");
        //groupsRef.child(uniqueKey).child("Members").setValue(group.getGroupList());

    }
}
