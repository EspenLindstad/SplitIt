package com.example.splitit;

import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class User {
    private String name;
    private String email;
    private String ID;
    private String Uid;

    private ArrayList<String> partOf;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    public String getUserID() {
        return this.ID;
    }

    public void setUserID(String ID) {
        this.ID = ID;
    }

    public void setUserUid(String Uid) {this.Uid = Uid;}

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }


    public void addUserToSettlement(String groupKey, String userKey) {

        Map<String, ArrayList<String>> settlementMap = new HashMap<>();
        ArrayList<String> memberOf = new ArrayList<>();
        memberOf.add(groupKey);
        settlementMap.put("usersSettlements", memberOf);
        db.collection("users").document(userKey).set(settlementMap, SetOptions.merge());

        System.out.println("Ting funker ja");
    }


    public ArrayList<String> getUsersSettlements(String userkey) {
        DocumentReference docRef = db.collection("users").document(userkey);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> partOf = new ArrayList<>();
                System.out.println("Class of usersting: " + documentSnapshot.get("usersSettlements").getClass());

            }
        });
        return partOf;
    }
}
