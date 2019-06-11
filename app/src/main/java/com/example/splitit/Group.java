package com.example.splitit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Group {

    private String baseCurrency; // Base currency: The currency the final settlement will be in
    private String name; //name of the group
    private String key;
    private String baseCurrencyPos; //The position of the base currency in the array -> so that when..
    // a user adds an expense, the spinner will start at the basecurrency

    private ArrayList<String> groupList; //list of all the groupmembers(name of the groupmembers)
    private ArrayList<String> groupKeys; //list of the groupkeys
    private ArrayList<Double> settlementArr = new ArrayList<Double>(); //Settlement array

    private ArrayList<Expense> expenses = new ArrayList<Expense>();

    private Map<String, Integer> userMap = new HashMap<String, Integer>(); //<username, index in matrix> -> to keep track of ..
    // which user corresponds to which index in the settlement matrix
    private Map<String, String> expenseNameMap = new HashMap<>(); // Map<Unique Expense Key, Name of the expense>
    private Map<String, Double> expenseMap = new HashMap<>(); // Map<Unique Expense Key, Value of the expense>
    private Map<String, ArrayList<String>> participantsMap = new HashMap<>();// Map<Unique Expense Key, List of the participants in that expense>
    private Map<String, String> userWhoPayedMap = new HashMap<>(); // Map<Unique Expense Key, name of the user which payed for the expense>

    private double[][] settlement;

    int members; //keeps track of number of members

    //constructor
    public Group(String name, ArrayList groupList, ArrayList groupKeys, Map<String, Integer> userMap, Map<String, String> expenseNameMap, Map<String, Double> expenseMap,Map<String, ArrayList<String>> participantsMap, Map<String, String> userWhoPayedMap, String baseCurrency, String baseCurrencyPos) {
        this.name = name;
        this.groupList = groupList;
        this.groupKeys = groupKeys;
        this.members = groupList.size();
        settlement = new double[groupList.size()][groupList.size()];
        this.userMap = userMap;
        this.expenseNameMap = expenseNameMap;
        this.expenseMap = expenseMap;
        this.participantsMap = participantsMap;
        this.userWhoPayedMap = userWhoPayedMap;
        this.baseCurrency = baseCurrency;
        this.baseCurrencyPos = baseCurrencyPos;
    }


    public Group() {

    }


    //Setters and getters

    public String getBaseCurrencyPos() {
        return baseCurrencyPos;
    }

    public void setBaseCurrencyPos(String baseCurrencyPos) {
        this.baseCurrencyPos = baseCurrencyPos;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public void setUserMap(Map<String, Integer> userMap) {
        this.userMap = userMap;
    }

    public Map<String, Integer> getUserMap() {
        return this.userMap;
    }

    public void setName(String name) {this.name = name;}

    public String getBaseCurrency(){
        return baseCurrency;
    }

    public ArrayList<Expense> getExpenses(){
        return expenses;
    }

    public ArrayList<Double> getSettlementArray(){
        return settlementArr;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getExpenseNameMap() {
        return expenseNameMap;
    }

    public void setExpenseNameMap(Map<String, String> expenseNameMap) {
        this.expenseNameMap = expenseNameMap;
    }

    public Map<String, Double> getExpenseMap() {
        return expenseMap;
    }

    public void setExpenseMap(Map<String, Double> expenseMap) {
        this.expenseMap = expenseMap;
    }

    public Map<String, ArrayList<String>> getParticipantsMap() {
        return participantsMap;
    }

    public void setParticipantsMap(Map<String, ArrayList<String>> participantsMap) {
        this.participantsMap = participantsMap;
    }

    public Map<String, String> getUserWhoPayedMap() {
        return userWhoPayedMap;
    }


    public void setUserWhoPayedMap(Map<String, String> userWhoPayedMap) {
        this.userWhoPayedMap = userWhoPayedMap;
    }

    public void setBaseCurrency(String baseCurrency){
        this.baseCurrency = baseCurrency;
    }

    public ArrayList<String> getGroupList() {
        System.out.println("Siste stedet vi prover å kjore?");
        return groupList;
    }

    public ArrayList<String> getGroupKeys() {
        return groupKeys;
    }

    public ArrayList<Double> getSettlementArr(){
        return settlementArr;
    }


    //converting arrays to matrix

    public double[][] arrayToMat(ArrayList<Double> settlementArr){
        int size = settlementArr.size()/groupList.size();
        int counter = -1;
        double[][] settlement = new double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                counter++;
                settlement[i][j] = settlementArr.get(counter);
            }
        }
        return settlement;

    }

    //for some reason viewMember couldnt find groupList, so we just use this instead.
    public double[][] arrayToMatrix(ArrayList<Double> settlementArr, ArrayList<String> groupList){
        int size = settlementArr.size()/groupList.size();
        int counter = -1;
        double[][] settlement = new double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                counter++;
                settlement[i][j] = settlementArr.get(counter);
            }
        }
        return settlement;
    }


    //converting matrix to array

    public ArrayList<Double> matToArray(double[][] mat){

        ArrayList<Double> arr = new ArrayList<>();
        int counter = -1;
        for(int i = 0; i < mat.length; i++){
            for(int j = 0; j < mat.length; j++){
                counter++;
                arr.add(mat[i][j]);
            }
        }
        return arr;
    }



    //add group member
    public void addGroupMember(String user, String key, ArrayList<Double> settlementArr) {

        double[][] settlement = arrayToMat(settlementArr);

        groupList.add(user);
        groupKeys.add(key);
        userMap.put(user, members);
        setMembers(getMembers()+1);

        double[][] temp = new double[getMembers()][getMembers()];

        for (int i = 0; i < settlement.length; i++) {
            for (int j = 0; j < settlement.length; j++) {
                temp[i][j] = settlement[i][j];
            }
        }

        this.settlementArr = matToArray(temp);
    }


    //add expense to the group
    public ArrayList<Double> addExpense(double expense, ArrayList<String> participants, String user_who_payed, String name, ArrayList<Double> prevSettlement, Map<String, Integer> userMap) {

        settlement = arrayToMat(prevSettlement);

        Expense expenseGroup = new Expense(participants, user_who_payed, expense, name);

        double dividedExpense = expenseGroup.getCostPerPerson();

        System.out.println("This is the usermap: " + getUserMap());

        int user_who_payed_index = userMap.get(user_who_payed);

        if (participants.size() == groupList.size()) { // hvis det skal deles på alle
            for (int i = 0; i < settlement.length; i++) {
                settlement[i][user_who_payed_index] = settlement[i][user_who_payed_index] + dividedExpense;
            }
        } else {
            settlement[user_who_payed_index][user_who_payed_index] = settlement[user_who_payed_index][user_who_payed_index] + dividedExpense;
            for (int i = 0; i < participants.size(); i++) {
                settlement[userMap.get(participants.get(i))][user_who_payed_index] = settlement[userMap.get(participants.get(i))][user_who_payed_index]+ dividedExpense;
            }
        }

        settlementArr = matToArray(settlement);

        System.out.println("settlement array in addExpense");
        System.out.println(settlementArr);

        return settlementArr;

    }

    //removing an expense

    public ArrayList<Double> removeExpense(ArrayList<Double> settlementArr,ArrayList<String> members, String user_who_payed, double expense){

        double[][] settlement = arrayToMat(settlementArr);

        int user_who_payed_index = userMap.get(user_who_payed);
        for(String member : members){
            int memberIndex = userMap.get(member);
            settlement[memberIndex][user_who_payed_index] = settlement[memberIndex][user_who_payed_index] - getCostPerPerson(expense, members);
        }
        settlementArr = matToArray(settlement);

        return settlementArr;
    }

    public double getCostPerPerson(double expense, ArrayList<String> members){
        return expense/(members.size()); //plus one to account for the person who payed
    }


    //find out who should pay next
    public String whoShouldPayNext(ArrayList<Double> settlementArr, Map<String, Integer> userMap){


        double[][] settlement = arrayToMat(settlementArr);

        for (double[] row : settlement) {

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        }


        Map<String, Integer> debtMap = new HashMap<>(); // <user, total debt>

        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
            int sumDebt = 0;
            //for(int i = 0; i < settlement.length; i++) {
                sumDebt += settlement[entry.getValue()][entry.getValue()];
            //}
            debtMap.put(entry.getKey(), sumDebt);
        }


        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(debtMap.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }


        return temp.keySet().toArray()[0].toString(); //get the first person in the list, the one with the largest debt
    }

}
