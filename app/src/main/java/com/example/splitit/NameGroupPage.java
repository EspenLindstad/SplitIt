package com.example.splitit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameGroupPage extends AppCompatActivity {
    /*
    The page where the the name of the group is added and the basecurrency.
    The basecurrency chosen, will be the default for when adding an expense.
    From here the groupKey is created, and passed on around the other pages.
     */

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private TextView title;
    private TextView groupName;
    private TextView participants;
    private TextView groupnameInput;

    private ArrayList<String> memberlist;
    private ArrayList<String> userKeys;

    private Button backButton;
    private Button doneButton;
    private Button infoBtn;

    private String baseCurrency;


    private String uniqueKey;
    private String name;

    private Map<String, Integer> userMap;
    Map<String, String> expenseNameMap = new HashMap<>();
    Map<String, Double> expenseMap = new HashMap<>();
    Map<String, ArrayList<String>> participantsMap = new HashMap<>();
    Map<String, String> userWhoPayedMap = new HashMap<>();

    public ListView participantsView;

    ArrayAdapter arrayAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_group_page);

        title = (TextView) findViewById(R.id.titleTextView);
        groupName = (TextView) findViewById(R.id.groupnameTextView);
        participants = (TextView) findViewById(R.id.participantsTextView);


        backButton = (Button) findViewById(R.id.button2);
        doneButton = (Button) findViewById(R.id.doneBtn);
        infoBtn = (Button) findViewById(R.id.infoBtn);

        participantsView = (ListView) findViewById(R.id.usersList);

        final Spinner fromSpinner = (Spinner) findViewById(R.id.baseCurrencySpinner);

        Intent intent = getIntent();

        memberlist = intent.getStringArrayListExtra("grouplist");
        userKeys = intent.getStringArrayListExtra("userKeys");

        arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, memberlist);
        participantsView.setAdapter(arrayAdapter);
        userMap = initializeMap(memberlist);




        doneButton.setOnClickListener(view -> {
                    name = ((TextView) findViewById(R.id.editText)).getText().toString();

                    baseCurrency = fromSpinner.getSelectedItem().toString();
                    List<String> spinnerItems = Arrays.asList(getResources().getStringArray(R.array.spinnerItems));
                    String basecurrencyPosition = Integer.toString(spinnerItems.indexOf(baseCurrency));

                    writeNewGroup(name, memberlist, userKeys, userMap, expenseNameMap, expenseMap, participantsMap, userWhoPayedMap, baseCurrency, basecurrencyPosition);

                });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(getApplicationContext(), AddGroupMember.class);
                startActivity(backIntent);
            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonShowPopupWindowClick(findViewById(android.R.id.content));
            }
        });
            }


    private Map<String, Integer> initializeMap(ArrayList groupList){
        Map<String, Integer> userMap = new HashMap<>();
        for (int i = 0; i < groupList.size(); i++) {
            userMap.put(groupList.get(i).toString(), i);
        }
        return userMap;
    }

    private void writeNewGroup(String gName, ArrayList<String> members, ArrayList<String> memberKeys, Map<String, Integer> userMap, Map<String, String> expenseNameMap, Map<String, Double> expenseMap, Map<String, ArrayList<String>> participantsMap, Map<String, String> userWhoPayedMap, String baseCurrency, String baseCurrencyPos) {
        //gName = ((TextView) findViewById(R.id.editText)).getText().toString();
        Group group = new Group(gName, members, memberKeys, userMap, expenseNameMap, expenseMap, participantsMap, userWhoPayedMap, baseCurrency, baseCurrencyPos);
        db.collection("groups")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        uniqueKey = documentReference.getId();

                        if (memberKeys.contains(null)) {
                            memberKeys.remove(null);
                        }

                        for (String member : memberKeys) {
                            addUserToSettlement(uniqueKey, member, ((TextView) findViewById(R.id.editText)).getText().toString());
                        }

                        Intent nextIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                        nextIntent.putExtra("groupKey", uniqueKey);
                        nextIntent.putExtra("baseCurrencyPos", baseCurrencyPos);
                        startActivity(nextIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failure", "Error adding document", e);
                    }
                });


    }


    public void addUserToSettlement(String groupKey, String userKey, String gname) {

        DocumentReference docRef = db.collection("users").document(userKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.get("usersSettlements") == null) {
                    Map<String, ArrayList<String>> settlementMap = new HashMap<>();
                    ArrayList<String> partOf = new ArrayList<>();
                    partOf.add(gname);
                    partOf.add(groupKey);
                    settlementMap.put("usersSettlements", partOf);
                    db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());
                }
                else {
                    //Funker ikke når brukeren er ny
                    Map<String, ArrayList<String>> settlementMap = new HashMap<>();
                    ArrayList<String> memberOf = (ArrayList<String>) documentSnapshot.get("usersSettlements");
                    memberOf.add(gname);
                    memberOf.add(groupKey);
                    settlementMap.put("usersSettlements", memberOf);
                    db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());
                }



            }
        });

    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


}
