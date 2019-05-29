package com.example.splitit;

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

    ArrayList<String> groupList;

    ArrayList<Expense> expenses = new ArrayList<Expense>();

    private Map<String, Integer> userMap = new HashMap<String, Integer>(); //<username, index in matrix>

    private int[][] settlement;

    int members;

    public Group(String key, String name, ArrayList groupList) {
        this.key = key;
        this.name = name;
        this.groupList = groupList;
        this.members = groupList.size();

        settlement = new int[groupList.size()][groupList.size()];

        for (int i = 0; i < groupList.size(); i++) {
            userMap.put(groupList.get(i).toString(), i);
        }
    }


    public void addGroupMember(String user) {

        //List<String> groupList = new ArrayList<>();

        groupList.add(user);
        userMap.put(user, members);
        members++;

        int[][] temp = new int[groupList.size() + 1][groupList.size() + 1];

        for (int i = 0; i < settlement.length; i++) {
            for (int j = 0; j < settlement.length; j++) {
                temp[i][j] = settlement[i][j];
            }
        }
        settlement = temp;
    }

    public void deleteGroupMember(String user) {


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

        int [][] temp = new int[settlement.length-1][settlement.length-1];

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

        settlement = temp;

        groupList.remove(user);


    }

    public void addExpense(int expense, ArrayList<String> participants, String user_who_payed) {

        Expense expenseGroup = new Expense(participants, user_who_payed, expense);

        expenses.add(expenseGroup);

        int dividedExpense = expenseGroup.getCostPerPerson();

        int user_who_payed_index = userMap.get(user_who_payed);

        if (participants.size() == groupList.size()) { // hvis det skal deles på alle
            for (int i = 0; i < settlement.length; i++) {
                settlement[i][user_who_payed_index] = dividedExpense;
            }
        } else {
            settlement[user_who_payed_index][user_who_payed_index] = dividedExpense;
            for (int i = 0; i < participants.size(); i++) {
                settlement[userMap.get(participants.get(i))][user_who_payed_index] = dividedExpense;
            }
        }

    }

    public void removeExpense(Expense expense){

        ArrayList<String> members = expense.getExpenseMembers();
        String user_who_payed = expense.getMemberPayed();
        int user_who_payed_index = userMap.get(user_who_payed);
        for(String member : members){
            int memberIndex = userMap.get(member);
            settlement[memberIndex][user_who_payed_index] = settlement[memberIndex][user_who_payed_index] - expense.getCostPerPerson();
        }

        expenses.remove(expense);
    }

    public String whoShouldPayNext(){

        Map<String, Integer> debtMap = new HashMap<>(); // <user, total debt>

        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
            int sumDebt = 0;
            for(int i = 0; i < settlement.length; i++) {
                sumDebt += settlement[entry.getValue()][i];
            }
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


        return temp.keySet().toArray()[0].toString(); //get the first person in the list

    }

    public static void main(String[] args) {


        ArrayList<String> groupList = new ArrayList<>();
        groupList.add("p1");
        groupList.add("p2");
        groupList.add("p3");
        groupList.add("p4");
        groupList.add("p5");

        Group group = new Group("dmcwdcw", "test", groupList);

        ArrayList<String> expenseMembers1 = new ArrayList<>();
        expenseMembers1.add("p1");
        expenseMembers1.add("p2");
        expenseMembers1.add("p5");

        group.addExpense(100, expenseMembers1, "p4");
/*
        ArrayList<String> expenseMembers2 = new ArrayList<>();
        expenseMembers2.add("p3");
        expenseMembers2.add("p1");
        expenseMembers2.add("p4");

        group.addExpense(124, expenseMembers2, "p2");


        ArrayList<String> expenseMembers3 = new ArrayList<>();
        expenseMembers3.add("p3");
        expenseMembers3.add("p1");
        expenseMembers3.add("p2");

        group.addExpense(432, expenseMembers2, "p4");
*/
        // graph[i][j] indicates the amount
        // that person i needs to pay person j

        System.out.println("BEFORE DELETING");

        System.out.println(group.userMap);

        for (int[] row : group.settlement) {

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        }

        group.deleteGroupMember("p1");

        System.out.println("AFTER DELETING p3");

        System.out.println(group.userMap);

        for (int[] row : group.settlement) {

            // converting each row as string
            // and then printing in a separate line
            System.out.println(Arrays.toString(row));
        }

        System.out.println(group.whoShouldPayNext());

        SplitAlgorithm split = new SplitAlgorithm();

        split.minCashFlow(group.settlement, group.groupList.size());


        // Group group_test = new Group(groupList);


        // Print the solution
        //minCashFlow(graph, N);
    }
}
