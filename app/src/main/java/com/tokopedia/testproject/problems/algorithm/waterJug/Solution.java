package com.tokopedia.testproject.problems.algorithm.waterJug;

import android.util.Log;

public class Solution {

    private static int jug1 = 0;
    private static int jug1Cap = 0;
    private static int jug2 = 0;
    private static int jug2Cap = 0;
    private static int target = 0;
    private static int pour = 0;
    private static final String TAG = "Solution";

    public static int minimalPourWaterJug(int jug1, int jug2, int target) {
        // TODO, return the smallest number of POUR action to do the water jug problem
        // below is stub, replace with your implementation!
        jug1Cap = jug1;
        jug2Cap = jug2;
        Solution.target = target;
        int pourJug1 = getPour(1);
        int pourJug2 = getPour(2);
        Log.d(TAG, "getMinimalPour: from jug1:"+pourJug1+" | from jug2:"+pourJug2);
        if(pourJug1 > pourJug2){
            return pourJug2;
        } else {
            return pourJug1;
        }
    }

    private static int getPour(int isJug){
        pour = 0;
        fillJug(isJug);
        return pour;
    }

    private static void fillJug(int isJug){
        switch (isJug){
            case 1:
                jug1 = jug1Cap;
                break;
            case 2:
                jug2 = jug2Cap;
                break;
        }
        pourJug(isJug);
    }

    private static void pourJug(int isJug){
        int jumlah = jug1+jug2;
        pour++;
        switch (isJug){
            case 1:
                if(jumlah < jug2Cap){
                    jug2 =  jumlah;
                    jug1 = 0;
                } else {
                    jug2 = jug2Cap;
                    jug1 =  jumlah -jug2;
                }
                if(jug2 != target){
                    if(jug2 == jug2Cap){
                        emptyJug(isJug);
                    } else{
                        fillJug(isJug);
                    }
                }
                break;
            case 2:
                if(jumlah < jug1Cap){
                    jug1 = jumlah;
                    jug2 = jumlah - jug1;
                } else {
                    jug1 = jug1Cap;
                    jug2 = jumlah - jug1;
                }
                if(jug2 != target){
                    if(jug1 == jug1Cap){
                        emptyJug(isJug);
                    } else {
                        fillJug(isJug);
                    }
                }
                break;
        }
    }

    private static void emptyJug(int target){
        switch (target){
            case 1:
                jug2 = 0;
                break;
            case 2:
                jug1 = 0;
                break;
        }
        pourJug(target);
    }
}
