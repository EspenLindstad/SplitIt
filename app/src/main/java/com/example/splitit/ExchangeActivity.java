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
import java.util.List;
import java.util.Map;

public class ExchangeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    static Double resultVal;
    String groupKey;
    private ArrayList<String> groupMembers;
    ArrayAdapter arrayAdapter;
    ListView userListView;
    String baseCurrency = "USD";
    private ArrayList<String> expenseMembers = new ArrayList<>();
    String expenseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        final Button addExpenseBtn = (Button) findViewById(R.id.addExpenseBtn);
        final TextView moneyText = (TextView) findViewById(R.id.moneyText);
        final Spinner fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        expenseName = ((EditText) findViewById(R.id.settlementName)).getText().toString();

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
                System.out.println("These are my mfuckin gmember: " + groupMembers);
                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);
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

                    String memberPayed = "sucks";

                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            //vi må intizialisere en tom array i starten slik at vi kan hente den ut, adde settlement, og sende den inn igjen.

                            //C: First add the expense locally

                            Group group = documentSnapshot.toObject(Group.class);

                            group.setUserMap(group.getGroupList());

                            System.out.println("group.getUserMap()");

                            System.out.println(group.getUserMap());

                            System.out.println("settlementArr");

                            ArrayList<Double> settlementArr = group.getSettlementArr();

                            if(settlementArr.isEmpty()){
                                for(int i = 0; i < group.getUserMap().size()*group.getUserMap().size(); i++){
                                    settlementArr.add(0.0);
                                }
                            }

                            group.addExpense(resultVal, expenseMembers, memberPayed, expenseName);

                            System.out.println(group.getSettlementArr());

                            settlementArr = group.getSettlementArr();

                            //documentSnapshot.toObject(Group.class).addExpense(resultVal ,expenseMembers, memberPayed, expenseName);


                            /*ArrayList<Double> settlement = new ArrayList<>();
                            settlement.add(1.0);
                            settlement.add(2.0);
                            settlement.add(3.0);*/

                            Map<String, Object> settlementMap = new HashMap<String, Object>();
                            settlementMap.put("settlement", settlementArr);
                            db.collection("groups").document(groupKey).set(settlementMap, SetOptions.merge());
/*


                            Map<String, Object> expenseMap = new HashMap<>();
                            //expenseMap.put("expenseValue", resultVal);
                            //expenseMap.put("groupMembers", groupMembers);
                            //expenseMap.put("memberWhoPayed", memberPayed);
                   */       //db.collection("groups").document(groupKey).set(expenseMap, SetOptions.merge());

                            //documentSnapshot.toObject(Group.class).addExpense(5 ,expenseMembers, memberPayed);
                            //String p = documentSnapshot.toObject(Group.class).whoShouldPayNext();
                            //System.out.println(p);

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


}
