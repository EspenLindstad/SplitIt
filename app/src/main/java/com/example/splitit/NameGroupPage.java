package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_group_page);

        title = (TextView) findViewById(R.id.titleTextView);
        groupName = (TextView) findViewById(R.id.groupnameTextView);
        participants = (TextView) findViewById(R.id.participantsTextView);
        String groupnameInput = ((TextView) findViewById(R.id.editText)).getText().toString();

        backButton = (Button) findViewById(R.id.button);
        doneButton = (Button) findViewById(R.id.button2);

        participantsView = (ListView) findViewById(R.id.usersList);


        Intent intent = getIntent();

        memberlist = intent.getStringArrayListExtra("grouplist");

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, memberlist);
        participantsView.setAdapter(arrayAdapter);



    }

    private void writeNewGroup(String userId, String gName, ArrayList members) {

        //mDatabase.child("group").child(userId).setValue(user);
        ArrayList<String> userNames = new ArrayList<>();
        //userNames.add(name);
        mDatabase.child("usernamelist").setValue(userNames);

    }
}
