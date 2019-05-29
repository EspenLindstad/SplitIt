package com.example.splitit;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ExchangeActivity extends AppCompatActivity {
    static Double resultVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        final Button button = (Button) findViewById(R.id.button);
        final TextView text = (TextView) findViewById(R.id.resultView);
        final TextView moneyText = (TextView) findViewById(R.id.moneyText);
        final Spinner fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        //final Spinner toSpinner = (Spinner) findViewById(R.id.toSpinner);
        resultVal = 0.0;
        text.setText(resultVal.toString());

        final Thread thread = new Thread(new Runnable() {


            @Override
            public void run() {


                ArrayList<String> groupList = new ArrayList<>();
                groupList.add("p1");
                groupList.add("p2");
                groupList.add("p3");
                groupList.add("p4");
                groupList.add("p5");
                Group group = new Group("hkj", groupList);


                HttpURLConnection urlConnection = null;
                try {
                    try {
                        String mainUrl = "http://api.fixer.io/latest";
                        String updatedUrl = mainUrl + "?base=" + fromSpinner.getSelectedItem();
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


                        Double moneyValue = Double.valueOf(moneyText.getText().toString());

                        if (fromSpinner.getSelectedItem().equals(group.baseCurrency)) {
                            resultVal = moneyValue;
                            //nytt

                        } else {
                            Double rateValue = Double.valueOf(result.getString((String) group.baseCurrency));
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
                    text.setText(resultVal.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });


    }


}
