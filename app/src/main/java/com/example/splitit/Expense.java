package com.example.splitit;

import java.util.ArrayList;

public class Expense {

    public ArrayList<String> expenseMembers; //members of the Expense
    public String memberPayed; //who added the expense
    private int expense;

    public Expense(ArrayList<String> expenseMembers, String memberPayed, int expense){
        this.expenseMembers = expenseMembers;
        this.memberPayed = memberPayed;
        this.expense = expense;
    }

    public int getCostPerPerson(){
        return expense/(expenseMembers.size() +1); //plus one to account for the person who payed
    }

    public void deletePerson(String user){
        expenseMembers.remove(user);
    }



}
