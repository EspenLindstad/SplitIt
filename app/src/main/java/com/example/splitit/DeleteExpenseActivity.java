package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DeleteExpenseActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String groupKey;
    ArrayList<Expense> expenses;
    ArrayAdapter arrayAdapter;
    ListView expenseListView;
    ArrayList<String> expenseNames = new ArrayList<>();
    Button backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_expense);

        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

        expenseListView = (ListView) findViewById(R.id.expensesListView);
        backBtn = (Button) findViewById(R.id.addExpenseBtn);
        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                expenses = documentSnapshot.toObject(Group.class).getExpenses();
                for(Expense expense : expenses){
                    expenseNames.add(expense.getExpenseName());
                }

                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, expenseNames);
                expenseListView.setAdapter(arrayAdapter);
/*
                expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //expenseMembers.add(groupMembers.get(position));

                        documentSnapshot.toObject(Group.class).removeExpense(expenses.get(position));

                    }

                });
*/
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newSetIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                newSetIntent.putExtra("groupKey", groupKey);
                startActivity(newSetIntent);
            }
        });


    }
}
