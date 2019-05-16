package com.example.splitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class homepage extends AppCompatActivity {

    private TextView mTextMessage;

    private Button signOutBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_settlements);
                    return true;
                case R.id.navigation_dashbboard:
                    mTextMessage.setText(R.string.title_profile);
                    return true;
                case R.id.navigation_userlist:
                    mTextMessage.setText(R.string.title_userlist);
                    Intent intent = new Intent(homepage.this, UserList.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        final Button addSettlement = (Button) findViewById(R.id.addSettlement);

        signOutBtn = (Button) findViewById(R.id.logoutBtn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    mAuth.getInstance().signOut();
                    Intent intent = new Intent(homepage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        addSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent, from the MainActivity to AnotherActivity
                Intent intent = new Intent(homepage.this, AddGroupMember.class);

                // Invoking the intent, start AnotherActivity
                startActivity(intent);
            }
        });
    }




}
