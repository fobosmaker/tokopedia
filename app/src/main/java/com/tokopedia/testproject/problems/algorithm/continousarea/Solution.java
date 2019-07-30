package com.tokopedia.testproject.problems.algorithm.continousarea;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hendry on 18/01/19.
 */
public class Solution {
    private static ArrayList<String> arrTemp = new ArrayList<>();
    private static ArrayList<Integer> arrResult = new ArrayList<>();
    private static int[][] matrix;
    private static final String TAG = "Solution";

    public static int maxContinuousArea(int[][] matrix) {
        // TODO, return the largest continuous area containing the same integer, given the 2D array with integers
        // below is stub
        Solution.matrix = matrix;
        return searchMaxContinuousArea();
    }

    private static Integer searchMaxContinuousArea(){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                arrTemp.add(generateString(i,j));
                directions(i,j);
                arrResult.add(arrTemp.size());
                arrTemp.clear();
            }
        }
        int bestResult = Collections.max(arrResult);
        Log.i(TAG, "searchMaxContinuousArea: value="+bestResult);
        return bestResult;
    }

    private static void directions(int i, int j){
        move(i,j,i,j-1);
        move(i,j,i,j+1);
        move(i,j,i-1,j);
        move(i,j,i+1,j);
    }

    private static void move(int x, int y, int newX, int newY){
        if(newX >= 0 && newX <= matrix.length-1 && newY >= 0 && newY < matrix[x].length){
            if(matrix[x][y] == matrix[newX][newY]){
                String val = generateString(newX,newY);
                if(!arrTemp.contains(val)){
                    arrTemp.add(val);
                    directions(newX,newY);
                }
            }
        }
    }

    private static String generateString(Integer x, Integer y){
        return x.toString()+y.toString();
    }
}
