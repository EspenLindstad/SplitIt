package com.example.splitit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//A class which provides some basic functions to deal with expenses

public class Expense {

    public ArrayList<String> expenseMembers; //members of the Expense
    private String memberPayed; //who added the expense
    private Double expense;
    String expenseName;

    Map<String, Map<String, Integer>> map = new HashMap<>();

    public Expense(ArrayList<String> expenseMembers, String memberPayed, Double expense, String expenseName){
        this.expenseMembers = expenseMembers;
        this.memberPayed = memberPayed;
        this.expense = expense;
        this.expenseName = expenseName;
    }

    public double getCostPerPerson(){
        return expense/(expenseMembers.size()); //plus one to account for the person who payed
    }

}
