package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DeleteMember extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String groupKey;
    private ArrayList<String> members;
    ArrayAdapter arrayAdapter;
    private ListView memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_member);
        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

        memberList = (ListView) findViewById(R.id.memberList);

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                members = documentSnapshot.toObject(Group.class).getGroupList();

                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, members);

                memberList.setAdapter(arrayAdapter);

                memberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
/*
                        Intent viewExpenseIntent = new Intent(getApplicationContext(), ViewExpense.class);
                        viewExpenseIntent.putExtra("groupKey", groupKey);
                        viewExpenseIntent.putExtra("expenseName", expenseNames.get(position));
                        viewExpenseIntent.putExtra("userWhoPayed", userWhoPayed.get(position));
                        viewExpenseIntent.putExtra("expense", expenses.get(position));
                        viewExpenseIntent.putExtra("currentUser", currentUser);
                        viewExpenseIntent.putExtra("uniqueExpenseKey", uniqueKeyArray.get(position));
                        viewExpenseIntent.putExtra("baseCurrency", baseCurrency);

                        viewExpenseIntent.putStringArrayListExtra("participants", participants.get(position));


                        startActivity(viewExpenseIntent);
*/
                    }

                });

            }
        });

    }
}
