package com.tokopedia.testproject.problems.algorithm.maxrectangle;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class Solution {
    private static ArrayList<Integer> arrResult = new ArrayList<>();
    private static int countLength = 0;
    private static int countRect = 0;
    private static int[][] matrix;
    private static final String TAG = "Solution";
    public static int maxRect(int[][] matrix) {
        // TODO, return the largest area containing 1's, given the 2D array of 0s and 1s
        // below is stub
        Solution.matrix = matrix;
        return searchMaxRect();
    }

    private static int searchMaxRect(){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                ++countLength;
                getSquareLength(i,j);
                if(countLength > 1){
                    expandSquare(i,j);
                    arrResult.add(countRect);
                } else arrResult.add(0);
                countLength = 0;
                countRect = 0;
            }
        }
        int bestResult = Collections.max(arrResult);
        Log.i(TAG, "searchMaxContinuousArea: value="+bestResult);
        return bestResult;
    }

    private static void getSquareLength(int i,int j){
        if(i+1 <= matrix.length-1 && j+1 < matrix[i].length){
            if(matrix[i][j] == 1 && matrix[i][j] == matrix[i][j+1] && matrix[i][j] == matrix[i+1][j] && matrix[i][j] == matrix[i+1][j+1]){
                countLength++;
                getSquareLength(i,j+1);
            }
        }
    }

    private static void expandSquare(int i, int j){
        if(i < matrix.length){
            ArrayList<Integer> rowTemp = new ArrayList<>();
            for(int k = j; k < countLength+j; k++)
                rowTemp.add(matrix[i][k]);

            if(!rowTemp.contains(0)){
                countRect+=rowTemp.size();
                expandSquare(i+1,j);
            }
        }
    }
}
