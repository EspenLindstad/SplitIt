package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewExpense extends AppCompatActivity {

    private String groupKey;
    private String expenseName;
    private String userWhoPayed;
    private String expense;
    private String currentUser;
    private String uniqueKey;
    private Button deleteThisExpenseBtn;
    private Button backToViewExpenseBtn;
    private String baseCurrency;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> participants;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        Intent viewExpenseIntent = getIntent();
        groupKey = viewExpenseIntent.getExtras().getString("groupKey");
        expenseName = viewExpenseIntent.getExtras().getString("expenseName");
        userWhoPayed = viewExpenseIntent.getExtras().getString("userWhoPayed");
        expense = viewExpenseIntent.getExtras().getString("expense");
        currentUser = viewExpenseIntent.getExtras().getString("currentUser");
        participants =  viewExpenseIntent.getExtras().getStringArrayList("participants");
        uniqueKey = viewExpenseIntent.getExtras().getString("uniqueExpenseKey");
        baseCurrency = viewExpenseIntent.getExtras().getString("baseCurrency");

        double expenseDouble = Double.parseDouble(expense);
        expenseDouble = Math.round(expenseDouble*100)/100;
        expense = Double.toString(expenseDouble);

        TextView ExpenseNameTV = (TextView) findViewById(R.id.ExpenseNameTV);
        TextView UserWhoPayedTV = (TextView) findViewById(R.id.UserWhoPayedTV);
        TextView ExpenseTV = (TextView) findViewById(R.id.ExpenseTV);
        ListView participantsLV = (ListView) findViewById(R.id.participantsLV);

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, participants);
        participantsLV.setAdapter(arrayAdapter);


        UserWhoPayedTV.setText("User Who Payed: " + userWhoPayed);
        ExpenseNameTV.setText("Expense Name: " + expenseName);
        ExpenseTV.setText("Total: " + expense + " " + baseCurrency);



        deleteThisExpenseBtn = findViewById(R.id.deleteThisExpenseBtn);
        backToViewExpenseBtn = findViewById(R.id.backToViewExpenseBtn);

        backToViewExpenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToDeleteExpenseActivity = new Intent(getApplicationContext(), DeleteExpenseActivity.class);
                backToDeleteExpenseActivity.putExtra("groupKey", groupKey);
                startActivity(backToDeleteExpenseActivity);
            }
        });

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                deleteThisExpenseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUser.equals(userWhoPayed)){
                            Toast.makeText(getApplicationContext(),"Ok det funker",
                                    Toast.LENGTH_SHORT).show();

                            Map<String, String> userWhoPayedMapTemp = documentSnapshot.toObject(Group.class).getUserWhoPayedMap();
                            Map<String, Double> expenseMapTemp = documentSnapshot.toObject(Group.class).getExpenseMap();
                            Map<String, ArrayList<String>> participantsMapTemp = documentSnapshot.toObject(Group.class).getParticipantsMap();
                            Map<String, String> expenseNameMapTemp = documentSnapshot.toObject(Group.class).getExpenseNameMap();

                            System.out.println("MAPS BEFORE ELEMENT IS REMOVED");
                            System.out.println("userWhoPayedMap");
                            System.out.println(userWhoPayedMapTemp);
                            System.out.println("expenseMap");
                            System.out.println(expenseMapTemp);
                            System.out.println("expenseNameMap");
                            System.out.println(expenseNameMapTemp);
                            System.out.println("participantsMap");
                            System.out.println(participantsMapTemp);

                            expenseMapTemp.remove(uniqueKey);
                            participantsMapTemp.remove(uniqueKey);
                            expenseNameMapTemp.remove(uniqueKey);
                            userWhoPayedMapTemp.remove(uniqueKey);


                            System.out.println("MAPS AFTER ELEMENT IS REMOVED");
                            System.out.println("userWhoPayedMap");
                            System.out.println(userWhoPayedMapTemp);
                            System.out.println("expenseMap");
                            System.out.println(expenseMapTemp);
                            System.out.println("expenseNameMap");
                            System.out.println(expenseNameMapTemp);
                            System.out.println("participantsMap");
                            System.out.println(participantsMapTemp);


                            Map<String, Object> expenseNameMap = new HashMap<String, Object>();
                            expenseNameMap.put("expenseNameMap", expenseNameMapTemp);

                            Map<String, Object> expenseMap = new HashMap<String, Object>();
                            expenseMap.put("expenseMap", expenseMapTemp);

                            Map<String, Object> participantsMap = new HashMap<String, Object>();
                            participantsMap.put("participantsMap", participantsMapTemp);

                            Map<String, Object> userWhoPayedMap = new HashMap<String, Object>();
                            userWhoPayedMap.put("userWhoPayedMap", userWhoPayedMapTemp);

                            db.collection("groups").document(groupKey).update(expenseNameMap);
                            db.collection("groups").document(groupKey).update(expenseMap);
                            db.collection("groups").document(groupKey).update(participantsMap);
                            db.collection("groups").document(groupKey).update(userWhoPayedMap);



                            ArrayList<Double> settlementArr = documentSnapshot.toObject(Group.class).getSettlementArr();
                            settlementArr =documentSnapshot.toObject(Group.class).removeExpense(settlementArr, participants, userWhoPayed, Double.parseDouble(expense));
                            Map<String, Object> settlementMap = new HashMap<String, Object>();
                            settlementMap.put("settlementArr", settlementArr);
                            db.collection("groups").document(groupKey).set(settlementMap, SetOptions.merge());

                            Intent backToDeleteExpenseActivity = new Intent(getApplicationContext(), DeleteExpenseActivity.class);
                            backToDeleteExpenseActivity.putExtra("groupKey", groupKey);
                            startActivity(backToDeleteExpenseActivity);


                        }
                        else{
                            Toast.makeText(getApplicationContext(),"You can only delete your expenses",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });










    }
}
