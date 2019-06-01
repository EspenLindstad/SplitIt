package com.example.splitit;

import java.util.ArrayList;

public class Expense {

    public ArrayList<String> expenseMembers; //members of the Expense
    private String memberPayed; //who added the expense
    private Double expense;
    String expenseName;

    public Expense(ArrayList<String> expenseMembers, String memberPayed, Double expense, String expenseName){
        this.expenseMembers = expenseMembers;
        this.memberPayed = memberPayed;
        this.expense = expense;
        this.expenseName = expenseName;
    }

    public String getExpenseName(){
        return expenseName;
    }

    public ArrayList<String> getExpenseMembers(){
        return expenseMembers;
    }

    public String getMemberPayed(){
        return memberPayed;
    }

    public double getCostPerPerson(){
        return expense/(expenseMembers.size()+1); //plus one to account for the person who payed
    }

    public double getNewCostIfPersonDeleted(){
        double diff = expense/expenseMembers.size() - getCostPerPerson();
        return diff;
    }
    public void deletePerson(String user){
        expenseMembers.remove(user);
    }

}
