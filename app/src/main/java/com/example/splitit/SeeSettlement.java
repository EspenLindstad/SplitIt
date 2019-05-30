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

import java.util.ArrayList;

public class SeeSettlement extends AppCompatActivity {

    private ArrayAdapter adapter;
    private CustomAdapter customAdapter;
    private ListView settlements;

    private ArrayList<String> debitUsers = new ArrayList<>();
    private ArrayList<String> creditUsers = new ArrayList<>();
    private ArrayList<String> sums = new ArrayList<>();

    private String groupKey;

    private Button backBtn;

    private TextView topText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_settlement);

        settlements = findViewById(R.id.settlementsListView);
        backBtn = findViewById(R.id.arrowBackBtn);
        topText = findViewById(R.id.topTextView);

        debitUsers.add("Trond");
        debitUsers.add("Per");
        debitUsers.add("Pål");

        creditUsers.add("Espen");
        creditUsers.add("Frøya");
        creditUsers.add("Grunnhild");

        sums.add("100");
        sums.add("27");
        sums.add("30");

        customAdapter = new CustomAdapter();

        settlements.setAdapter(customAdapter);

        Intent intent = getIntent();
        groupKey = intent.getExtras().getString("groupKey");

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

            TextView debitUser = (TextView)view.findViewById(R.id.debitUserTextView);
            TextView creditUser = (TextView)view.findViewById(R.id.creditUserTextView);
            TextView arrow = (TextView)view.findViewById(R.id.textView10);
            TextView sum = (TextView)view.findViewById(R.id.sumTextView);

            debitUser.setText(debitUsers.get(i));
            creditUser.setText(creditUsers.get(i));
            sum.setText(sums.get(i));

            return view;
        }
    }



}


