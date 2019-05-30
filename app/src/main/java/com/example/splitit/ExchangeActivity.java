package com.example.splitit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExchangeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    static Double resultVal;
    String groupKey;
    private ArrayList<String> groupMembers;
    ArrayAdapter arrayAdapter;
    ListView userListView;
    String baseCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        final Button button = (Button) findViewById(R.id.button);
        final TextView moneyText = (TextView) findViewById(R.id.moneyText);
        final Spinner fromSpinner = (Spinner) findViewById(R.id.fromSpinner);

        userListView = (ListView) findViewById(R.id.userListView);

        //final Spinner toSpinner = (Spinner) findViewById(R.id.toSpinner);
        resultVal = 0.0;


        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupMembers = documentSnapshot.toObject(Group.class).getGroupList();
                //set base currency må komme før her
                documentSnapshot.toObject(Group.class).setBaseCurrency("USD");
                //
                baseCurrency = documentSnapshot.toObject(Group.class).getBaseCurrency();
                System.out.println("These are my mfuckin gmember: " + groupMembers);
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);
                userListView.setAdapter(arrayAdapter);


            }
        });

        final Thread thread = new Thread(new Runnable() {


            @Override
            public void run() {


                HttpURLConnection urlConnection = null;
                try {
                    try {
                        String mainUrl = "http://data.fixer.io/api/latest?access_key=be99fccf6933a51407eb597a21f7dcb3&symbols=";
                        System.out.println(fromSpinner.getSelectedItem());
                        String updatedUrl = mainUrl + fromSpinner.getSelectedItem().toString();

                        URL url = new URL(updatedUrl);

                        urlConnection = (HttpURLConnection) url.openConnection();

                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
                        String inputLine = "";
                        String fullStr = "";
                        while ((inputLine = inReader.readLine()) != null) {
                            fullStr += inputLine;
                        }

                        System.out.println("fullStr");
                        System.out.println(fullStr);

                        JSONObject jsonObj = new JSONObject(fullStr);
                        JSONObject result = jsonObj.getJSONObject("rates");

                        double rateValue = result.getDouble("AUD");


                        Double moneyValue = Double.valueOf(moneyText.getText().toString());

                        if (fromSpinner.getSelectedItem().equals(baseCurrency)) {
                            resultVal = moneyValue;
                            //nytt

                        } else {
                            Double resultValue = moneyValue * rateValue;
                            resultVal = resultValue;
                        }
                    } finally {
                        if (urlConnection != null)
                            urlConnection.disconnect();
                    }


                } catch (NumberFormatException e) {
                    //TODO: Alertbox ekle

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                thread.start();
                try {
                    thread.join();

                    ArrayList<String> expenseMembers = new ArrayList<>();
                    expenseMembers.add("hei");
                    String memberPayed = "iuh";

                    Map<String, Object> expenseMap = new HashMap<>();
                    Expense expense = new Expense(expenseMembers, memberPayed, resultVal.intValue());
                    expenseMap.put("expenses", expense);
                    //Expense(ArrayList<String> expenseMembers, String memberPayed, int expense)
                    System.out.println("this is the resulting value");
                    System.out.println(resultVal);
                    //db.collection("groups").document(groupKey).set(expenseMap, SetOptions.merge());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
/*
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        ArrayList<String> expenseMembers = new ArrayList<>();
                        expenseMembers.add("hei");
                        String memberPayed = "iuh";
                        documentSnapshot.toObject(Group.class).addExpense(resultVal.intValue(),expenseMembers, memberPayed);
                        String p = documentSnapshot.toObject(Group.class).whoShouldPayNext();
                        System.out.println(p);
                    }
                });*/
            }



        });


    }


        //addExpense(int expense, ArrayList<String> participants, String user_who_payed)


}
