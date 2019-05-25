package com.example.splitit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group {

    private ArrayList<String> groupList;
    private String name;

    ArrayList<Expense> expenses = new ArrayList<Expense>();

    private Map<String, Integer> userMap= new HashMap<String, Integer>(); //<username, index in matrix>

    private int [ ][ ] settlement;

    int members;

    public Group(String name, ArrayList groupList){
        this.name = name;
        this.groupList = groupList;

        this.members = groupList.size();

        settlement = new int [groupList.size()][groupList.size()];

        for (int i = 0; i < groupList.size(); i++){
            userMap.put(groupList.get(i).toString(), i);
        }
    }

    public String getGroupName() {
        return this.name;
    }

    public ArrayList<String> getGroupList() {
        return this.groupList;
    }


    public void addGroupMember(String user){

        //List<String> groupList = new ArrayList<>();

        groupList.add(user);
        userMap.put(user,members);
        members++;

        int [][] temp = new int[groupList.size()+1][groupList.size()+1];

        for(int i = 0; i < settlement.length; i++) {
            for (int j = 0; j < settlement.length; j++) {
                temp[i][j] = settlement[i][j];
            }
        }
        settlement = temp;
    }

    public void deleteGroupMember(String user){

        groupList.remove(user);
        boolean found_user = false;

        for (Map.Entry<String, Integer> entry : userMap.entrySet()) {
            if (entry.getKey() == user){
                found_user = true;
            }
            if (found_user == true){
                entry.setValue(entry.getValue()-1);
            }
        }

        userMap.remove(user);
    }

    public void addExpense(int expense, ArrayList<String> participants, String user_who_payed){
        int dividedExpense = expense/participants.size();
        int user_who_payed_index = userMap.get(user_who_payed);
        if (participants.size() == groupList.size()){ // hvis det skal deles på alle
            for (int i = 0; i < settlement.length; i++){
                settlement[i][user_who_payed_index] = dividedExpense;
            }
        }


    }

    public static void main (String[] args)
    {
        // graph[i][j] indicates the amount
        // that person i needs to pay person j

        ArrayList<String> groupList = new ArrayList<>();
        groupList.add("p1");
        groupList.add("p2");
        groupList.add("p3");

        Group group = new Group("test", groupList);

        group.addGroupMember("p4");

        group.addExpense(100, group.groupList, "p1");

        group.deleteGroupMember("p1");

        System.out.println(group.userMap);

        for (int i = 0; i < group.members; i++) {
            for (int j = 0; j < group.members; j++) {
                System.out.println(group.settlement[i][j] + " ");
            }
            //System.out.println();
        }

        //SplitAlgorithm alg = new SplitAlgorithm();
        //alg.minCashFlow(group.settlement, group.members);





        int graph[][] = { {0, 1000, 2000},
                {0, 0, 5000},
                {0, 0, 0},};

        int N = 3;


       // Group group_test = new Group(groupList);


        // Print the solution
        //minCashFlow(graph, N);
    }

}
