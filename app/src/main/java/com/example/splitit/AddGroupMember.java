package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class AddGroupMember extends AppCompatActivity {

    private Set<Integer> memberList;


    private Button nextButton;
    private Button backButton;
    private Button testButton;

    private TextView topText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_member);

        final Button nextButton = (Button) findViewById(R.id.nextButton);
        Button backButton = (Button) findViewById(R.id.backButton);
        Button testButton = (Button) findViewById(R.id.testButton); // Dette skal egentlig være en person

        nextButton.setEnabled(false);

        TextView topText = (TextView) findViewById(R.id.textView4);

        final Set<Integer> memberList = new HashSet<Integer>();


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent, from the MainActivity to AnotherActivity
                Intent intent = new Intent(AddGroupMember.this, AddGroupMember.class);

                // Invoking the intent, start AnotherActivity
                startActivity(intent);
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberList.add(1);
                changeClickability(memberList, nextButton);
            }
        });



    }

    private void changeClickability(Set set, Button btn) {
        if (!set.isEmpty()) {
            btn.setEnabled(true);
        }

    }

    //Må fikse sharedPreferences


}
