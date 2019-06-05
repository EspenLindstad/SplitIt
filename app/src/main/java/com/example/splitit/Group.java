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

    String baseCurrency;
    private String name;
    private String key;
    private String baseCurrencyPos;

    private ArrayList<String> groupList;
    private ArrayList<String> groupKeys;

    ArrayList<Expense> expenses = new ArrayList<Expense>();

    private Map<String, Integer> userMap = new HashMap<String, Integer>(); //<username, index in matrix>

    private ArrayList<Double> settlementArr = new ArrayList<Double>();

    private double[][] settlement;

    Map<String, String> expenseNameMap = new HashMap<>();
    Map<String, Double> expenseMap = new HashMap<>();
    Map<String, ArrayList<String>> participantsMap = new HashMap<>();
    Map<String, String> userWhoPayedMap = new HashMap<>();

    int members;

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
        // IKKK SLETT DENNE
    }

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

    public void deleteGroupMember(String user) {

        double[][] settlement = arrayToMat(settlementArr);

        for (int i = 0; i < expenses.size(); i++) {

            ArrayList<String> expenseMembers = expenses.get(i).getExpenseMembers();
            String user_who_payed = expenses.get(i).getMemberPayed();
            int user_who_payed_index = userMap.get(user_who_payed);

            if (user_who_payed == user){
                expenses.remove(i);
            }

            if (expenseMembers.contains(user)) {// check if user to be deleted is a part of the expense

                for (String member : expenseMembers) {

                    int participant_index = userMap.get(member); // get the index of all members in the group

                    settlement[participant_index][user_who_payed_index] = settlement[participant_index][user_who_payed_index] + expenses.get(i).getNewCostIfPersonDeleted();
                    // delete the expense

                    //System.out.println("This is the expenseMembers size: " + expenseMembers.size());
                }

                expenses.get(i).deletePerson(user);
            }

        }

        boolean found_user = false;

        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
            if (entry.getKey() == user) {
                found_user = true;
            }
            if (found_user == true) {
                entry.setValue(entry.getValue() - 1);
            }
        }
        userMap.remove(user);

        double [][] temp = new double[settlement.length-1][settlement.length-1];

        for (Expense expense : expenses){
            ArrayList<String> expenseMembers = expense.getExpenseMembers(); //"p1", "p2"
            String person_who_payed = expense.getMemberPayed();

            int person_who_payed_index = userMap.get(person_who_payed);
            temp[person_who_payed_index][person_who_payed_index] = expense.getCostPerPerson();

            for(String member : expenseMembers){
                int index = userMap.get(member);
                temp[index][person_who_payed_index] = expense.getCostPerPerson();
            }
        }

        members--;

        settlementArr = matToArray(temp);

        groupList.remove(user);


    }

    public ArrayList<Double> addExpense(double expense, ArrayList<String> participants, String user_who_payed, String name, ArrayList<Double> prevSettlement, Map<String, Integer> userMap) {

        settlement = arrayToMat(prevSettlement);

        Expense expenseGroup = new Expense(participants, user_who_payed, expense, name);

        expenses.add(expenseGroup);

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
        return expense/(members.size()+1); //plus one to account for the person who payed
    }

    public String whoShouldPayNext(ArrayList<Double> settlementArr, Map<String, Integer> userMap){

        System.out.println("USERMAP INPUT TO WHO SHOULD PAY NEXT");
        System.out.println(userMap);
        System.out.println("THIS IS THE INPUT SETTLEMENT MAT");

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

        System.out.println("THIS IS THE STORTED USERMAP");
        System.out.println(temp);

        return temp.keySet().toArray()[0].toString(); //get the first person in the list
    }

    public static void main(String[] args) {
/*

        ArrayList<String> groupList = new ArrayList<>();
        groupList.add("p1");
        groupList.add("p2");
        groupList.add("p3");
        groupList.add("p4");
        groupList.add("p5");



        ArrayList<Double> sa;
        sa = group.getSettlementArr();
        System.out.println(sa);

        for (int i = 0; i < sa.size(); i++) {
            sa.set(i, i * 1.0);
        }

        System.out.println(sa);
        System.out.println("group.arrayToMat(sa)");

        double[][] s = group.arrayToMat(sa);
        for (double[] row : s) {

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        }

        System.out.println("HER");
        ArrayList<Double> arry = group.matToArray(s);
        System.out.println(arry);
*/
        /////////////////////////

/*
        group.addExpense(124, expenseMembers2, "p2", "skjer");


        ArrayList<String> expenseMembers3 = new ArrayList<>();
        expenseMembers3.add("p3");
        expenseMembers3.add("p1");
        expenseMembers3.add("p2");

        group.addExpense(432, expenseMembers2, "p4", "sap");
/*

        // graph[i][j] indicates the amount
        // that person i needs to pay person j

  /*      System.out.println("BEFORE DELETING");

        System.out.println(group.userMap);

        for (double[] row : group.settlement) {

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        }

        group.deleteGroupMember("p1");

        System.out.println("AFTER DELETING p3");

        System.out.println(group.userMap);

        for (double[] row : group.settlement) {

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        }

        //System.out.println(group.whoShouldPayNext());

        //SplitAlgorithm split = new SplitAlgorithm(new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
        //split.minCashFlow(group.settlement, group.groupList.size());


        // Group group_test = new Group(groupList);


        // Print the solution
        //minCashFlow(graph, N);
    */
    }
}
