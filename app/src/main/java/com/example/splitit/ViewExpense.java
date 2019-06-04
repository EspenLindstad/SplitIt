package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private TextView ExpenseNameTV;
    private TextView UserWhoPayedTV;
    private TextView ExpenseTV;
    private Button deleteThisExpenseBtn;
    private Button backToViewExpenseBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> participants;


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

        System.out.println("HER");
        System.out.println(expenseName);
        System.out.println(userWhoPayed);
        System.out.println(expense);
        System.out.println(currentUser);
        /*
        UserWhoPayedTV.setText(userWhoPayed);
        ExpenseNameTV.setText(expenseName);
        ExpenseTV.setText(expense);*/

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

                            Group group = documentSnapshot.toObject(Group.class);
                            Map<String, Integer> userMap = documentSnapshot.toObject(Group.class).getUserMap();
                            ArrayList<Double> settlementArr = documentSnapshot.toObject(Group.class).getSettlementArr();

                            settlementArr =documentSnapshot.toObject(Group.class).removeExpense(settlementArr, participants, userWhoPayed, Double.parseDouble(expense));

                            Map<String, Object> settlementMap = new HashMap<String, Object>();
                            settlementMap.put("settlementArr", settlementArr);
                            db.collection("groups").document(groupKey).set(settlementMap, SetOptions.merge());




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
