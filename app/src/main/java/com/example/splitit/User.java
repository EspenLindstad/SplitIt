package com.example.splitit;

import java.util.ArrayList;

class User {
    private String name;
    private String email;
    private String userID;

    private ArrayList<String> partOf;


    public User(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
    }

    public User() {
    }

    public String getUserID() {
        return this.userID;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public void addUserToSettlement(Group group) {
        partOf.add(group.getGroupName());
    }

    public ArrayList<String> getUsersSettlements() {
        return partOf;
    }
}
