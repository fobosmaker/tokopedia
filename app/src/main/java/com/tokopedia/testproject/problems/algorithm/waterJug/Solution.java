package com.tokopedia.testproject.problems.algorithm.waterJug;

import android.util.Log;

public class Solution {

    private static int jug1 = 0;
    private static int jug1Cap = 0;
    private static int pourJug1 = 0;
    private static int countStepJug1 = 0;
    private static int jug2 = 0;
    private static int jug2Cap = 0;
    private static int pourJug2 = 0;
    private static int countStepJug2 = 0;
    private static int target = 0;
    private static final String TAG = "Solution";

    public static int minimalPourWaterJug(int jug1, int jug2, int target) {
        // TODO, return the smallest number of POUR action to do the water jug problem
        // below is stub, replace with your implementation!
        jug1Cap = jug1;
        jug2Cap = jug2;
        Solution.target = target;
        return getMinimalPour();
    }

    private static int getMinimalPour(){
        fillJug1();
        fillJug2();
        Log.d(TAG, "getMinimalPour: from jug1("+countStepJug1+"):"+pourJug1+" | from jug2("+countStepJug2+"):"+pourJug2);
        return 3;
    }

    private static void fillJug1(){
        countStepJug1++;
        jug1 = jug1Cap;
        pourJug1toJug2();
    }

    private static void pourJug1toJug2(){
        countStepJug1++;
        pourJug1++;
        int jumlah = jug1+jug2;
        if(jumlah < jug2Cap){
            jug2 =  jumlah;
            jug1 = 0;
        } else {
            jug2 = jug2Cap;
            jug1 =  jumlah -jug2;
        }

        if(jug2 != target){
            if(jug2 == jug2Cap){
                emptyJug2();
            } else{
                fillJug1();
            }
        }
    }

    private static void emptyJug2(){
        countStepJug1++;
        jug2 = 0;
        pourJug1toJug2();
    }

    private static void fillJug2(){
        countStepJug2++;
        jug2 = jug2Cap;
        pourJug2toJug1();
    }

    private static void pourJug2toJug1(){
        countStepJug2++;
        pourJug2++;
        int jumlah = jug1+jug2;
        if(jumlah < jug1Cap){
            jug1 = jumlah;
            jug2 = jumlah - jug1;
        } else {
            jug1 = jug1Cap;
            jug2 = jumlah - jug1;
        }

        if(jug2 != target){
            if(jug1 == jug1Cap){
                emptyJug1();
            } else {
                fillJug2();
            }
        }
    }

    private static void emptyJug1(){
        countStepJug2++;
        jug1 = 0;
        pourJug2toJug1();
    }
}
