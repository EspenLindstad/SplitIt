package com.example.splitit;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExchangeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    static Double resultVal;
    String groupKey;
    private ArrayList<String> groupMembers;
    ArrayAdapter arrayAdapter;
    ListView userListView;
    String baseCurrency = "USD";
    private ArrayList<String> expenseMembers = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String currentUser;
    String expenseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        final Button addExpenseBtn = (Button) findViewById(R.id.addExpenseBtn);
        final TextView moneyText = (TextView) findViewById(R.id.moneyText);
        final Spinner fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        final Button addAllBtn = (Button) findViewById(R.id.addAllBtn);
        final EditText name = ((EditText) findViewById(R.id.settlementName));



        userListView = (ListView) findViewById(R.id.userListView);

        //final Spinner toSpinner = (Spinner) findViewById(R.id.toSpinner);
        resultVal = 0.0;

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String email = user.getEmail();
        currentUser = usernameFromEmail(email);


        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupMembers = documentSnapshot.toObject(Group.class).getGroupList();
                //set base currency må komme før her
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);

                addAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expenseMembers = groupMembers;
                        Toast.makeText(getApplicationContext(),"All members will be added to expense",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                userListView.setAdapter(arrayAdapter);
                userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (!expenseMembers.contains(groupMembers.get(position))) {
                            expenseMembers.add(groupMembers.get(position));

                        }
                    }
                });

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
                        String updatedUrl = mainUrl; //+ fromSpinner.getSelectedItem().toString();

                        URL url = new URL(updatedUrl);

                        urlConnection = (HttpURLConnection) url.openConnection();

                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
                        String inputLine = "";
                        String fullStr = "";
                        while ((inputLine = inReader.readLine()) != null) {
                            fullStr += inputLine;
                        }


                        JSONObject jsonObj = new JSONObject(fullStr);
                        JSONObject result = jsonObj.getJSONObject("rates");

                        double rateValue = result.getDouble(fromSpinner.getSelectedItem().toString());
                        double rateValueBaseCurrency = result.getDouble(baseCurrency);

                        Double moneyValue = Double.valueOf(moneyText.getText().toString());

                        if (fromSpinner.getSelectedItem().equals(baseCurrency)) {
                            resultVal = moneyValue;

                        } else {
                            Double resultValue = moneyValue * rateValueBaseCurrency/rateValue;
                            resultVal = resultValue;
                        }
                    } finally {
                        if (urlConnection != null)
                            urlConnection.disconnect();
                    }


                } catch (NumberFormatException e) {
                    e.getCause();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        addExpenseBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                thread.start();


                try {
                    thread.join();

                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            expenseName  = name.getText().toString();

                            Group group = documentSnapshot.toObject(Group.class);

                            Map<String, Integer> userMap = documentSnapshot.toObject(Group.class).getUserMap();

                            ArrayList<Double> settlementArr = documentSnapshot.toObject(Group.class).getSettlementArr();

                            if(settlementArr.isEmpty()){
                                for(int i = 0; i < group.getUserMap().size()*group.getUserMap().size(); i++){
                                    settlementArr.add(0.0);
                                }
                            }

                            settlementArr = group.addExpense(resultVal, expenseMembers, currentUser, expenseName, settlementArr, userMap); // current user = member payed


                            Map<String, Object> settlementMap = new HashMap<String, Object>();
                            settlementMap.put("settlementArr", settlementArr);
                            db.collection("groups").document(groupKey).set(settlementMap, SetOptions.merge());


                            Toast.makeText(getApplicationContext(),"Expense added",
                                    Toast.LENGTH_SHORT).show();

                            //Now: Adding the expenses into firebase.

                            String uniqueExpenseID = UUID.randomUUID().toString();
                            System.out.println(uniqueExpenseID);

                            Map<String, String> expenseNameMapTemp;
                            Map<String, Double> expenseMapTemp;
                            Map<String, ArrayList<String>> participantsMapTemp;
                            Map<String, String> userWhoPayedMapTemp;

                            expenseNameMapTemp = documentSnapshot.toObject(Group.class).getExpenseNameMap();
                            expenseMapTemp = documentSnapshot.toObject(Group.class).getExpenseMap();
                            participantsMapTemp = documentSnapshot.toObject(Group.class).getParticipantsMap();
                            userWhoPayedMapTemp = documentSnapshot.toObject(Group.class).getUserWhoPayedMap();

                            expenseNameMapTemp.put(uniqueExpenseID, expenseName);
                            expenseMapTemp.put(uniqueExpenseID, resultVal);
                            participantsMapTemp.put(uniqueExpenseID, groupMembers);
                            userWhoPayedMapTemp.put(uniqueExpenseID, currentUser);

                            Map<String, Object> expenseNameMap = new HashMap<String, Object>();
                            expenseNameMap.put("expenseNameMap", expenseNameMapTemp);

                            Map<String, Object> expenseMap = new HashMap<String, Object>();
                            expenseMap.put("expenseMap", expenseMapTemp);

                            Map<String, Object> participantsMap = new HashMap<String, Object>();
                            participantsMap.put("participantsMap", participantsMapTemp);

                            Map<String, Object> userWhoPayedMap = new HashMap<String, Object>();
                            userWhoPayedMap.put("userWhoPayedMap", userWhoPayedMapTemp);


                            db.collection("groups").document(groupKey).set(expenseNameMap, SetOptions.merge());
                            db.collection("groups").document(groupKey).set(expenseMap, SetOptions.merge());
                            db.collection("groups").document(groupKey).set(participantsMap, SetOptions.merge());
                            db.collection("groups").document(groupKey).set(userWhoPayedMap, SetOptions.merge());


                            Intent newIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                            newIntent.putExtra("groupKey", groupKey);
                            startActivity(newIntent);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
