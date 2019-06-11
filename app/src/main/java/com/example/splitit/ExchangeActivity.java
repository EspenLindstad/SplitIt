package com.example.splitit;


import android.content.Intent;
import android.graphics.Color;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExchangeActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    static Double resultVal;
    private String groupKey;
    private String basecurrencyPosition;
    private String currentUser;
    private String expenseName;
    private String baseCurrency;


    private ArrayList<String> groupMembers = new ArrayList<>();
    private ArrayList<String> expenseMembers = new ArrayList<>();
    private ArrayList<String> tempMembers = new ArrayList<>();

    ListView userListView;
    ArrayAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Map<String, String> expenseNameMapTemp;
    private Map<String, Double> expenseMapTemp;
    private Map<String, ArrayList<String>> participantsMapTemp;
    private Map<String, String> userWhoPayedMapTemp;
    private Button backBtn;
    private TextView toptext;
    private Spinner fromSpinner;
    private Button addExpenseBtn;
    private TextView moneyText;
    private Button addAllBtn;
    private EditText name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        //Initialize all buttons, textviews, spinners etc
        addExpenseBtn = (Button) findViewById(R.id.addExpenseBtn);
        moneyText = (TextView) findViewById(R.id.moneyText);
        toptext = (TextView) findViewById(R.id.addExpenseTextView);
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        addAllBtn = (Button) findViewById(R.id.addAllBtn);
        name = ((EditText) findViewById(R.id.settlementName));
        userListView = (ListView) findViewById(R.id.userListView);
        backBtn = (Button) findViewById(R.id.addExpenseBackBtn);


        //get groupkey
        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

    }

    @Override
    protected void onResume() {
        super.onResume();


        //final Spinner toSpinner = (Spinner) findViewById(R.id.toSpinner);
        resultVal = 0.0;

        //get current user from firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String email = user.getEmail();
        currentUser = usernameFromEmail(email);


        //back to settlement homepage
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                backIntent.putExtra("groupKey", groupKey);
                backIntent.putExtra("baseCurrencyPos", basecurrencyPosition);
                startActivity(backIntent);
            }
        });


        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //get information from firebase, to display.
                groupMembers = documentSnapshot.toObject(Group.class).getGroupList();
                baseCurrency =  documentSnapshot.toObject(Group.class).getBaseCurrency();
                basecurrencyPosition = documentSnapshot.toObject(Group.class).getBaseCurrencyPos();

                int startAt = Integer.parseInt(basecurrencyPosition);
                fromSpinner.setSelection(startAt);

                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, groupMembers);

                //Add everybody in the expense
                addAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expenseMembers.size() != groupMembers.size()) {
                            expenseMembers = groupMembers;
                            Toast.makeText(getApplicationContext(),"All members will be added to the expense",
                                    Toast.LENGTH_SHORT).show();
                            addAllBtn.setText("REMOVE ALL");
                            userListView.setVisibility(View.GONE);
                        }
                        else {
                            expenseMembers = tempMembers;
                            Toast.makeText(getApplicationContext(),"All members removed from the expense",
                                    Toast.LENGTH_SHORT).show();
                            addAllBtn.setText("SELECT ALL");
                            userListView.setVisibility(View.VISIBLE);
                        }
                    }
                });

                //If we don't want to add everybody to the expense, we can press the members we want to add in the listview
                userListView.setAdapter(arrayAdapter);
                userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!expenseMembers.contains(groupMembers.get(position))) {
                            view.setBackgroundColor(Color.LTGRAY);
                            view.invalidate();
                            expenseMembers.add(groupMembers.get(position));
                            if (!tempMembers.contains(groupMembers.get(position))) {
                                tempMembers.add(groupMembers.get(position));
                            }
                        }
                        else {
                            expenseMembers.remove(groupMembers.get(position));
                            view.setBackgroundColor(0x00000000);
                            view.invalidate();
                            tempMembers.remove(groupMembers.get(position));


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

                        //Url from API
                        String mainUrl = "http://data.fixer.io/api/latest?access_key=be99fccf6933a51407eb597a21f7dcb3&symbols=";
                        String updatedUrl = mainUrl; //+ fromSpinner.getSelectedItem().toString();

                        URL url = new URL(updatedUrl);

                        urlConnection = (HttpURLConnection) url.openConnection();

                        //Read inn al the currencies from the webpage with all currencies
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
                        String inputLine = "";
                        String fullStr = "";
                        while ((inputLine = inReader.readLine()) != null) {
                            fullStr += inputLine;
                        }


                        //Create text a json object
                        JSONObject jsonObj = new JSONObject(fullStr);
                        JSONObject result = jsonObj.getJSONObject("rates");

                        //pick out the rates for the basecurrency and the currency which has been selected
                        double rateValue = result.getDouble(fromSpinner.getSelectedItem().toString());
                        double rateValueBaseCurrency = result.getDouble(baseCurrency);

                        Double moneyValue = Double.valueOf(moneyText.getText().toString());

                        //If the basecurrency and selected currency is the same -> no need to change
                        if (fromSpinner.getSelectedItem().equals(baseCurrency)) {
                            resultVal = moneyValue;

                        } else {
                            //Converting the result to the basecurrency
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

        //adding the expense to the settlement
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

                            //get userMap(So that we can keep track of where which position to add expense in the settlement matrix)
                            //get settlement matrix from firebase
                            Group group = documentSnapshot.toObject(Group.class);

                            Map<String, Integer> userMap = documentSnapshot.toObject(Group.class).getUserMap();

                            ArrayList<Double> settlementArr = documentSnapshot.toObject(Group.class).getSettlementArr();


                            //if the settlementarray is empty: initialize as zeros
                            if(settlementArr.isEmpty()){
                                for(int i = 0; i < group.getUserMap().size()*group.getUserMap().size(); i++){
                                    settlementArr.add(0.0);
                                }
                            }


                            //adding the actual expense
                            settlementArr = group.addExpense(resultVal, expenseMembers, currentUser, expenseName, settlementArr, userMap); // current user = member payed

                            //Now: Adding all the updates maps back into firebase.

                            Map<String, Object> settlementMap = new HashMap<String, Object>();
                            settlementMap.put("settlementArr", settlementArr);
                            db.collection("groups").document(groupKey).set(settlementMap, SetOptions.merge());

                            String uniqueExpenseID = UUID.randomUUID().toString();
                            System.out.println(uniqueExpenseID);

                            expenseNameMapTemp = documentSnapshot.toObject(Group.class).getExpenseNameMap();
                            expenseMapTemp = documentSnapshot.toObject(Group.class).getExpenseMap();
                            participantsMapTemp = documentSnapshot.toObject(Group.class).getParticipantsMap();
                            userWhoPayedMapTemp = documentSnapshot.toObject(Group.class).getUserWhoPayedMap();

                            expenseNameMapTemp.put(uniqueExpenseID, expenseName);
                            expenseMapTemp.put(uniqueExpenseID, resultVal);
                            participantsMapTemp.put(uniqueExpenseID, expenseMembers);
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

                            //notify the user that the expense has been added
                            Toast.makeText(getApplicationContext(),"Expense added",
                                    Toast.LENGTH_SHORT).show();

                            //go back to settlement homepage
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
