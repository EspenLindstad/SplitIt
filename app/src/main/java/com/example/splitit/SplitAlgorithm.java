package com.example.splitit;


import android.content.Intent;

import java.util.ArrayList;

public class SplitAlgorithm {
    // Number of persons (or vertices in the graph)

    private ArrayList<String> debitors;
    private ArrayList<String> creditors;
    private ArrayList<String> sums;


    public SplitAlgorithm(){
        this.debitors = new ArrayList<>();
        this.creditors = new ArrayList<>();
        this.sums = new ArrayList<>();
    }


    public void addToDebitors(String debit) {
        debitors.add(debit);
    }

    public void addToCreditors(String credit) {
        creditors.add(credit);
    }

    public void addToSums(String sum) {
        sums.add(sum);
    }

    public ArrayList<String> getDebitors() {
        return this.debitors;
    }

    public ArrayList<String> getCreditors() {
        return this.creditors;
    }

    public ArrayList<String> getSums() {
        return this.sums;
    }

    // A utility function that returns
    // index of minimum value in arr[]
    static int getMin(int arr[], int N)
    {
        int minInd = 0;
        for (int i = 1; i < N; i++)
            if (arr[i] < arr[minInd])
                minInd = i;
        return minInd;
    }

    // A utility function that returns
    // index of maximum value in arr[]
    static int getMax(int arr[],int N)
    {
        int maxInd = 0;
        for (int i = 1; i < N; i++)
            if (arr[i] > arr[maxInd])
                maxInd = i;
        return maxInd;
    }

    // A utility function to return minimum of 2 values
    static int minOf2(int x, int y)
    {
        return (x < y) ? x: y;
    }

    // amount[p] indicates the net amount
    // to be credited/debited to/from person 'p'
    // If amount[p] is positive, then
    // i'th person will amount[i]
    // If amount[p] is negative, then
    // i'th person will give -amount[i]
    private void minCashFlowRec(int amount[], int N)
    {
        // Find the indexes of minimum and
        // maximum values in amount[]
        // amount[mxCredit] indicates the maximum amount
        // to be given (or credited) to any person .
        // And amount[mxDebit] indicates the maximum amount
        // to be taken(or debited) from any person.
        // So if there is a positive value in amount[],
        // then there must be a negative value
        int mxCredit = getMax(amount, N), mxDebit = getMin(amount, N);

        // If both amounts are 0, then
        // all amounts are settled
        if (amount[mxCredit] == 0 && amount[mxDebit] == 0)
            return;

        // Find the minimum of two amounts
        int min = minOf2(-amount[mxDebit], amount[mxCredit]);
        amount[mxCredit] -= min;
        amount[mxDebit] += min;


        addToCreditors(Integer.toString(mxDebit));
        addToDebitors(Integer.toString(mxCredit));
        addToSums(Integer.toString(min));

        // If minimum is the maximum amount to be
        System.out.println("Person " + mxDebit + " pays " + min
                + " to " + "Person " + mxCredit);

        // Recur for the amount array.
        // Note that it is guaranteed that
        // the recursion would terminate
        // as either amount[mxCredit]  or
        // amount[mxDebit] becomes 0
        minCashFlowRec(amount, N);
    }

    // Given a set of persons as graph[]
    // where graph[i][j] indicates
    // the amount that person i needs to
    // pay person j, this function
    // finds and prints the minimum
    // cash flow to settle all debts.
    public void minCashFlow(double graph[][], int N)
    {
        System.out.println("Startet split algorithm");
        // Create an array amount[],
        // initialize all value in it as 0.
        int amount[]=new int[N];

        // Calculate the net amount to
        // be paid to person 'p', and
        // stores it in amount[p]. The
        // value of amount[p] can be
        // calculated by subtracting
        // debts of 'p' from credits of 'p'
        for (int p = 0; p < N; p++)
            for (int i = 0; i < N; i++)
                amount[p] += (graph[i][p] - graph[p][i]);

        minCashFlowRec(amount, N);
    }


    // Driver code
    public void main (String[] args)
    {
        // graph[i][j] indicates the amount
        // that person i needs to pay person j
        double graph[][] = { {0, 1000, 2000},
                        {0, 0, 5000},
                        {0, 0, 0},};

        int N = 3;
        // Print the solution
        minCashFlow(graph, N);


    }
}

