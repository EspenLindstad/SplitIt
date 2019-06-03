package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SeeSettlement extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayAdapter adapter;
    private CustomAdapter customAdapter;
    private ListView settlements;

    private ArrayList<String> debitUsers = new ArrayList<>();
    private ArrayList<String> creditUsers = new ArrayList<>();
    private ArrayList<String> sums = new ArrayList<>();
    private ArrayList<Double> groupMembers = new ArrayList<>();

    private String groupKey;

    private Button backBtn;

    private TextView topText;
    private TextView noListTextView;

    private Integer count;

    private double[][] settlement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_settlement);



        settlements = findViewById(R.id.settlementsListView);
        backBtn = findViewById(R.id.arrowBackBtn);
        topText = findViewById(R.id.topTextView);
        noListTextView = (TextView) findViewById(R.id.noListTextView);
        noListTextView.setVisibility(View.GONE);


        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

        DocumentReference docRef = db.collection("groups").document(groupKey);
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Group group = documentSnapshot.toObject(Group.class);

                Map<String, Integer> temporaryMap;

                temporaryMap = group.getUserMap();

                Long temp = (Long) documentSnapshot.get("members");
                count = temp.intValue();
                System.out.println("members count: " + count);

                ArrayList<Double> array = (ArrayList<Double>) documentSnapshot.get("settlement");

                if (array != null) {
                    for (Double i : (ArrayList<Double>) documentSnapshot.get("settlement")) {
                        groupMembers.add(i);
                    }
                    System.out.println("Groupsmembers: " + groupMembers);

                    settlement = arrayToMat(groupMembers, count);

                    SplitAlgorithm splitter = new SplitAlgorithm();

                    splitter.minCashFlow(settlement, count);


                    debitUsers = splitter.getDebitors();
                    creditUsers = splitter.getCreditors();
                    sums = splitter.getSums();

                    // Dette må testes mer
                    for (String i : debitUsers) {
                        for (Map.Entry<String, Integer> entry : temporaryMap.entrySet()) {
                            if (entry.getValue().equals(i)) {
                                System.out.println(entry.getKey());
                            }
                        }
                    }


                    System.out.println("Size og debitUsers");
                    System.out.println(debitUsers.size());

                    customAdapter = new CustomAdapter();

                    settlements.setAdapter(customAdapter);
                }
                else {
                    noListTextView.setVisibility(View.VISIBLE);
                }



            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                backIntent.putExtra("groupKey", groupKey);
                startActivity(backIntent);

            }
        });

    }

    class CustomAdapter extends BaseAdapter {
        public int getCount() {
            return debitUsers.size();
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout, null);

            TextView creditUser = (TextView)view.findViewById(R.id.creditUserTextView);
            //TextView arrow = (TextView)view.findViewById(R.id.textView10);

            creditUser.setText(creditUsers.get(i) + " ows " + debitUsers.get(i) + " " + sums.get(i) );

            return view;
        }
    }

    public double[][] arrayToMat(ArrayList<Double> settlementArr, Integer membercount){
        int counter = -1;
        double[][] settlement = new double[membercount][membercount];
        for(int i = 0; i < membercount; i++){
            for(int j = 0; j < membercount; j++){
                counter++;
                settlement[i][j] = settlementArr.get(counter).doubleValue();
            }
        }

        return settlement;

    }



}


