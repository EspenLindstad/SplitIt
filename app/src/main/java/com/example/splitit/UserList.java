package com.example.splitit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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

public class UserList extends AppCompatActivity {

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
        setContentView(R.layout.activity_user_list);
        userlistReference = FirebaseDatabase.getInstance().getReference().child("users");
        onStart();
        userListView = (ListView) findViewById(R.id.userlistview);
        titleTextView = (TextView) findViewById(R.id.textView6);



    }

    @Override
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
                arrayAdapter = new ArrayAdapter(UserList.this, android.R.layout.simple_list_item_1, usernamelist);
                userListView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled: ", databaseError.toException());
                Toast.makeText(UserList.this, "Failed to load User list.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        userlistReference.addValueEventListener(userListener);

        mUserListListener = userListener;
    }

    public String usernameOfCurrentUser() {
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mUserListListener != null) {
            userlistReference.removeEventListener(mUserListListener);
        }

    }
}

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    */

    /*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    */
