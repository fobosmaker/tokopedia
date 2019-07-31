package com.tokopedia.testproject.problems.androidView.waterJugSimulation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    private static int jug1Cap = 0;
    private static int jug2Cap = 0;
    private static final String TAG = "Solution";

    public static List<WaterJugAction> simulateWaterJug(int jug1, int jug2, int target) {
        // TODO, simulate the smallest number of action to do the water jug problem
        // below is stub, replace with your implementation!
        jug1Cap = jug1;
        jug2Cap = jug2;
        return getMinimalAction(target);
    }

    private static List<WaterJugAction> getMinimalAction(int target){
        List<WaterJugAction> list = new ArrayList<>();
        fillJug(list, 0, 0, target,1);
        List<WaterJugAction> list2 = new ArrayList<>();
        fillJug(list2, 0, 0, target,2);

        Log.d(TAG, "size: from jug1("+list.size()+") | from jug2("+list2.size()+"):");
        if(list.size() == 0 && list2.size() == 0){
            return new ArrayList<>();
        } else {
            if(list.size() > list2.size()){
                return list2;
            } else {
                return list;
            }
        }
    }

    private static void fillJug(List<WaterJugAction> list, int jug1, int jug2, int target, int isJug){
        switch (isJug){
            case 1:
                jug1 = jug1Cap;
                list.add(new WaterJugAction(WaterJugActionEnum.FILL,1));
                break;
            case 2:
                jug2 = jug2Cap;
                list.add(new WaterJugAction(WaterJugActionEnum.FILL,2));
                break;
        }
        pourJug(list,jug1,jug2,target,isJug);
    }

    private static void pourJug(List<WaterJugAction> list, int jug1, int jug2, int target, int isJug){
        int jumlah = jug1+jug2;
        switch (isJug){
            case 1:
                list.add(new WaterJugAction(WaterJugActionEnum.POUR, 2));
                if(jumlah < jug2Cap){
                    jug2 =  jumlah;
                    jug1 = 0;
                } else {
                    jug2 = jug2Cap;
                    jug1 =  jumlah-jug2;
                }
                if(jug2 != target){
                    if(jug2 == jug2Cap){
                        emptyJug(list,jug1,jug2,target,isJug);
                    } else{
                        fillJug(list,jug1,jug2,target,isJug);
                    }
                }
                break;
            case 2:
                list.add(new WaterJugAction(WaterJugActionEnum.POUR, 1));
                if(jumlah < jug1Cap){
                    jug1 = jumlah;
                    jug2 = jumlah - jug1;
                } else {
                    jug1 = jug1Cap;
                    jug2 = jumlah - jug1;
                }

                if(jug2 != target){
                    if(jug1 == jug1Cap){
                        emptyJug(list,jug1,jug2,target,isJug);
                    } else {
                        fillJug(list,jug1,jug2,target,isJug);
                    }
                }
                break;
        }
    }

    private static void emptyJug(List<WaterJugAction> list, int jug1, int jug2, int target, int isJug){
        switch (isJug){
            case 1:
                jug2 = 0;
                list.add(new WaterJugAction(WaterJugActionEnum.EMPTY, 2));
                break;
            case 2:
                jug1 = 0;
                list.add(new WaterJugAction(WaterJugActionEnum.EMPTY, 1));
                break;
        }
        pourJug(list,jug1,jug2,target,isJug);
    }
}
