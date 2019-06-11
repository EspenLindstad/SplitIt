package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

public class DeleteExpenseActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String groupKey;
    private String baseCurrency;
    ArrayAdapter arrayAdapter;
    ListView expenseListView;
    private ArrayList<String> expenseNames = new ArrayList<>();
    private ArrayList<String> userWhoPayed = new ArrayList<>();
    private ArrayList<String> expenses = new ArrayList<>();
    private ArrayList<ArrayList<String>> participants = new ArrayList<>();
    private ArrayList<String> uniqueKeyArray = new ArrayList<>();
    private Button backBtn;
    private Map<String, String> expenseNameMap;
    private Map<String, Double> expenseMap;
    private Map<String, ArrayList<String>> participantsMap;
    private Map<String, String> userWhoPayedMap;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String currentUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_expense);


        //get the groupkey
        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

        //initialize listview and backbutton
        expenseListView = (ListView) findViewById(R.id.expensesListView);
        backBtn = (Button) findViewById(R.id.addExpenseBtn);

        //get current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String email = user.getEmail();
        currentUser = usernameFromEmail(email);

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //get the information from firebase
                expenseNameMap = documentSnapshot.toObject(Group.class).getExpenseNameMap();
                userWhoPayedMap = documentSnapshot.toObject(Group.class).getUserWhoPayedMap();
                expenseMap = documentSnapshot.toObject(Group.class).getExpenseMap();
                participantsMap = documentSnapshot.toObject(Group.class).getParticipantsMap();
                baseCurrency = documentSnapshot.toObject(Group.class).getBaseCurrency();

                //fetch out the relevant values to display them in the next intent
                for (String value : expenseNameMap.values()) {
                    expenseNames.add(value);
                }

                for(String key : expenseNameMap.keySet()){
                    uniqueKeyArray.add(key);
                }

                for (String value : userWhoPayedMap.values()) {
                    userWhoPayed.add(value);
                }

                for (Double value : expenseMap.values()) {
                    expenses.add(value.toString());
                }

                for (ArrayList<String> value : participantsMap.values()){
                    participants.add(value);
                }


                //Listview to show all the expensenames
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, expenseNames);
                expenseListView.setAdapter(arrayAdapter);


                //set the expenses on clicklistener -> if you click them you get information about the expense.
                expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent viewExpenseIntent = new Intent(getApplicationContext(), ViewExpense.class);
                        //pass the information
                        viewExpenseIntent.putExtra("groupKey", groupKey);
                        viewExpenseIntent.putExtra("expenseName", expenseNames.get(position));
                        viewExpenseIntent.putExtra("userWhoPayed", userWhoPayed.get(position));
                        viewExpenseIntent.putExtra("expense", expenses.get(position));
                        viewExpenseIntent.putExtra("currentUser", currentUser);
                        viewExpenseIntent.putExtra("uniqueExpenseKey", uniqueKeyArray.get(position));
                        viewExpenseIntent.putExtra("baseCurrency", baseCurrency);

                        viewExpenseIntent.putStringArrayListExtra("participants", participants.get(position));
                        startActivity(viewExpenseIntent);

                    }

                });

            }
        });

        //back to settlement homepage
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newSetIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                newSetIntent.putExtra("groupKey", groupKey);
                startActivity(newSetIntent);
            }
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
