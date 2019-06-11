package com.example.splitit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ViewMember extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String member;
    private String groupKey;
    private ArrayList<String> partOfExpenses;
    private ArrayList<String> payedForExpenses;
    private ArrayList<String> userKeys;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String currentUser;
    private String admin = "hei";

    ArrayList<String> expenseNames = new ArrayList<>();
    ArrayList<String> userWhoPayed = new ArrayList<>();
    ArrayList<String> expenses = new ArrayList<>();
    ArrayList<ArrayList<String>> participants = new ArrayList<>();
    ArrayList<String> uniqueKeyArray = new ArrayList<>();
    ArrayList<Double> settlementArr;
    ArrayList<String> groupList;
    ArrayList<String> userSettlements;

    private Map<String, String> expenseNameMap;
    private Map<String, Double> expenseMap;
    private Map<String, ArrayList<String>> participantsMap;
    private Map<String, String> userWhoPayedMap;
    private Map<String, Integer> userMap;

    private TextView userPayedForTV;
    private TextView userPartOfTV;


    ListView partOfListView;
    ListView payedForListView;

    ArrayAdapter arrayAdapter1;
    ArrayAdapter arrayAdapter2;

    private Button delMemberBtn;
    private Button backToSettlementHomepage;

    private int numMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_member);


        //First get information about which member was clicked
        Intent intent = getIntent();
        member = intent.getStringExtra("member");
        groupKey = intent.getStringExtra("groupKey");

        //get the current user to see if the person is authorised to delete this person
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String email = user.getEmail();
        currentUser = usernameFromEmail(email);

        //ListViews
        partOfListView = (ListView) findViewById(R.id.partOfLV);
        payedForListView = (ListView) findViewById(R.id.payedForLV);

        //TextViews
        userPartOfTV = (TextView) findViewById(R.id.userPartOfTV);
        userPayedForTV = (TextView) findViewById(R.id.userPayedForTV);
        userPartOfTV.setText("Expenses " + member + " is a part of");
        userPayedForTV.setText("Expenses " + member + " payed for");

        //Buttons
        delMemberBtn = (Button) findViewById(R.id.deleteMemBtn);
        backToSettlementHomepage = (Button) findViewById(R.id.backToSettlementHomepage);

        //fetch out data from firebase
        DocumentReference docRef = db.collection("groups").document(groupKey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                expenseNameMap = documentSnapshot.toObject(Group.class).getExpenseNameMap(); //All the name of the expenses
                userWhoPayedMap = documentSnapshot.toObject(Group.class).getUserWhoPayedMap(); //User who payed for all the expenses
                expenseMap = documentSnapshot.toObject(Group.class).getExpenseMap(); // All the expenses
                participantsMap = documentSnapshot.toObject(Group.class).getParticipantsMap(); //All the participants in all expenses
                userMap = documentSnapshot.toObject(Group.class).getUserMap();
                groupList = documentSnapshot.toObject(Group.class).getGroupList();
                numMembers = documentSnapshot.toObject(Group.class).getMembers();
                settlementArr = documentSnapshot.toObject(Group.class).getSettlementArr();
                userKeys = documentSnapshot.toObject(Group.class).getGroupKeys();


                System.out.println("Member clicked on");
                System.out.println(member);
                System.out.println("userWhoPayedMap");
                System.out.println(userWhoPayedMap);
                System.out.println("expenseMap");
                System.out.println(expenseMap);
                System.out.println("expenseNameMap");
                System.out.println(expenseNameMap);
                System.out.println("participantsMap");
                System.out.println(participantsMap);


                //Want to give the user an overview of the expenses this member is a part of, and which expenses they payed for.
                ArrayList<String> userPartOf= new ArrayList<>(); //All the expenses the member was a part of
                ArrayList<String> userPayedFor = new ArrayList<>(); //All the expenses the member payed for


                //Find out which expenses the user payed for
                for (Map.Entry<String, String> entry : userWhoPayedMap.entrySet()) {
                    if (entry.getValue().equals(member)){
                        System.out.println("HALO");
                        userPayedFor.add(expenseNameMap.get(entry.getKey()));
                    }
                }

                //find out which expenses the user is a part of
                for (Map.Entry<String, ArrayList<String>> entry : participantsMap.entrySet()) {
                    for(String participants : entry.getValue()){
                        if (participants.equals(member) && userWhoPayedMap.get(entry.getKey())!=member){ //dont want duplicate lists
                            userPartOf.add(expenseNameMap.get(entry.getKey()));
                        }
                    }
                }

                System.out.println("userPayedFor" + userPayedFor);
                System.out.println("userPartOf" + userPartOf);


                //Display the listviews
                arrayAdapter1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, userPartOf);
                partOfListView.setAdapter(arrayAdapter1);
                arrayAdapter2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, userPayedFor);
                payedForListView.setAdapter(arrayAdapter2);


                //Deleting the member
                delMemberBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //We need to update: All the expenses, recalculating the expenses to update settlement matrix, usermap
                        //If the user to be deleted payed for some of the expenses, we need to remove the expense from the expensemap, expensenamemap and userwhopayedmap.
                        ArrayList<String> keysToBeDeleted = new ArrayList<>(); //Because you cant delete while iterating.
                        for (Map.Entry<String, String> entry : userWhoPayedMap.entrySet()) {
                            if (entry.getValue().equals(member)) {
                                keysToBeDeleted.add(entry.getKey());
                            }
                        }

                        for (String key : keysToBeDeleted) {
                            expenseMap.remove(key);
                            expenseNameMap.remove(key);
                            userWhoPayedMap.remove(key);
                        }


                        //If the person is partiticipating in any expenses, we need to delete them from there as well.
                        for (Map.Entry<String, ArrayList<String>> entry : participantsMap.entrySet()) {
                            for (int i = entry.getValue().size() - 1; i >= 0; i--) {
                                if (entry.getValue().get(i).equals(member)) {
                                    entry.getValue().remove(entry.getValue().get(i));
                                }
                            }
                        }

                        //We need to update the userMap, so that we still have the right correspondance in the settlement matrix

                        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
                            if (userMap.get(member) < entry.getValue()) {
                                entry.setValue(entry.getValue() - 1);
                            }
                        }

                        //Then we remove the user from the usermap
                        String thisUserKey = userKeys.get(userMap.get(member));
                        System.out.println("USERKEY FOR PERSON TO BE DELETED" + thisUserKey);
                        userKeys.remove((userMap.get(member)));
                        userMap.remove(member);


                        //Updating the settlement matrix
                        double[][] settlement = documentSnapshot.toObject(Group.class).arrayToMatrix(settlementArr, groupList);
                        settlement = new double[settlement.length - 1][settlement.length - 1];

                        for (Map.Entry<String, Double> entry : expenseMap.entrySet()) {
                            ArrayList<String> expenseMembers = participantsMap.get(entry.getKey()); //get all the participants in that expense
                            String person_who_payed = userWhoPayedMap.get(entry.getKey()); // get the user who payed in that expense
                            int person_who_payed_index = userMap.get(person_who_payed);

                            //just to keep track of how much each person has payed, it looks nicer in the settlement matrix
                            settlement[person_who_payed_index][person_who_payed_index] = entry.getValue() / expenseMembers.size();
                            for (String member : expenseMembers) {
                                int index = userMap.get(member);
                                settlement[index][person_who_payed_index] = entry.getValue() / expenseMembers.size();
                            }

                        }

                        //then converting it into an array
                        settlementArr = documentSnapshot.toObject(Group.class).matToArray(settlement);


                        //remove user form groupList

                        groupList.remove(member);

                        //Updating how many members there is in the group
                        numMembers--;

                        //sending the new information the firebase
                        db.collection("groups").document(groupKey).update("members", numMembers);
                        db.collection("groups").document(groupKey).update("userMap", userMap);
                        db.collection("groups").document(groupKey).update("settlementArr", settlementArr);
                        db.collection("groups").document(groupKey).update("groupList", groupList);
                        db.collection("groups").document(groupKey).update("expenseMap", expenseMap);
                        db.collection("groups").document(groupKey).update("expenseNameMap", expenseNameMap);
                        db.collection("groups").document(groupKey).update("participantsMap", participantsMap);



                        System.out.println("thisUserKey" + thisUserKey);


                        DocumentReference docRef = db.collection("users").document(thisUserKey);
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                userSettlements = (ArrayList<String>) documentSnapshot.get("usersSettlements");

                                System.out.println("USER SETTLEMENTS");
                                System.out.println(userSettlements);

                                for(int i = 0; i < userSettlements.size(); i++){
                                    if(userSettlements.get(i) == thisUserKey){
                                        userSettlements.remove(i-1);
                                        userSettlements.remove(i);
                                    }
                                }
                                db.collection("users").document(thisUserKey).update("usersSettlements", userSettlements);

                            }
                        });


                        if (currentUser == member) {
                            Intent backToHomeIntent = new Intent(getApplicationContext(), homepage.class);
                            startActivity(backToHomeIntent);
                        }
                        else {
                            //go back to settlement homepage
                            Intent backToSettlementIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                            backToSettlementIntent.putExtra("groupKey", groupKey);
                            startActivity(backToSettlementIntent);
                        }
                    }
                });

            }
        });

        backToSettlementHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToSettlementHPIntent = new Intent(getApplicationContext(), SettlementHomepage.class);
                backToSettlementHPIntent.putExtra("groupKey", groupKey);
                startActivity(backToSettlementHPIntent);
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
