package com.tokopedia.testproject.problems.androidView.slidingImagePuzzle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tokopedia.testproject.R;

import java.util.ArrayList;
import java.util.List;

public class SlidingImageGameActivity extends AppCompatActivity {
    public static final String X_IMAGE_URL = "x_image_url";
    public static final int GRID_NO = 4;
    private String imageUrl;
    ImageView[][] imageViews = new ImageView[4][4];
    private GridLayout gridLayout;
    private static final String TAG = "SlidingImageGameActivit";
    int[][] defaultArray = new int[4][4];
    int[][] goalArray = new int[4][4];
    List<Bitmap> bitmap = new ArrayList<>();
    private static final int BLANK = 99;

    public static Intent getIntent(Context context, String imageUrl) {
        Intent intent = new Intent(context, SlidingImageGameActivity.class);
        intent.putExtra(X_IMAGE_URL, imageUrl);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getIntent().getStringExtra(X_IMAGE_URL);
        setContentView(R.layout.activity_sliding_image_game);
        gridLayout = findViewById(R.id.gridLayout);

        goalArray[0][0] = 0;
        goalArray[0][1] = 1;
        goalArray[0][2] = 2;
        goalArray[0][3] = 3;

        goalArray[1][0] = 4;
        goalArray[1][1] = 5;
        goalArray[1][2] = 6;
        goalArray[1][3] = 7;

        goalArray[2][0] = 8;
        goalArray[2][1] = 9;
        goalArray[2][2] = 10;
        goalArray[2][3] = 11;

        goalArray[3][0] = 12;
        goalArray[3][1] = 13;
        goalArray[3][2] = 14;
        goalArray[3][3] = BLANK;


        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                ImageView view = (ImageView) inflater.inflate(R.layout.item_image_sliding_image,
                        gridLayout, false);
                gridLayout.addView(view);
                imageViews[i][j] = view;
                final int row = i;
                final int column = j;
                imageViews[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        movePuzzle(row,column);
                        //if(imageViews[row][column].)
                        //Toast.makeText(SlidingImageGameActivity.this, "image "+row+" "+column+"clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        Solution.sliceTo4x4(this, new Solution.onSuccessLoadBitmap() {
            @Override
            public void onSliceSuccess(List<Bitmap> bitmapList) {
                //TODO will randomize placement to grid. Note: the game must be solvable.
                //replace below implementation to your implementation.

                int counter = 0;
                int bitmapSize = bitmapList.size();
                for (int i = 0; i < GRID_NO; i++) {
                    for (int j = 0; j < GRID_NO; j++) {
                        if (counter >= bitmapSize) break;
                        imageViews[i][j].setImageBitmap(bitmapList.get(counter));
                        defaultArray[i][j] = counter;
                        counter++;
                    }
                    if (counter >= bitmapSize) break;
                }
                defaultArray[3][3] = BLANK;
                bitmap = bitmapList;
            }

            @Override
            public void onSliceFailed(Throwable throwable) {
                Toast.makeText(SlidingImageGameActivity.this,
                        throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, imageUrl);

        // TODO add implementation of the game.
        // There is image adjacent to blank space (either horizontal or vertical).
        // If that image is clicked, it will swap to the blank space
        // if the puzzle is solved (the image in the view is aligned with the original image), then show a "success" dialog

        // TODO add handling for rotation to save the user input.
        // If the device is rotated, it should retain user's input, so user can continue the game.

    }

    private void movePuzzle(int row, int col){
        //Log.d(TAG, "movePuzzle: resume movement for array "+row+" "+col);
        //left
        if(!move(row,col,row,col-1)){
            //right
            if(!move(row,col,row,col+1)){
                //top
                if(!move(row,col,row-1,col)){
                    //bottom
                    if(!move(row,col,row+1,col)){
                        Log.d(TAG, "movePuzzle: cant move");
                    } else{
                        isFinished();
                    }
                } else {
                    isFinished();
                }
            } else {
                isFinished();
            }
        } else {
            isFinished();
        }
    }

    private Boolean move(int x, int y, int newX, int newY){
        if(newX >= 0 && newX <= defaultArray.length-1 && newY >= 0 && newY < defaultArray[x].length){
            if(defaultArray[newX][newY] == BLANK){
                //Log.d(TAG, "move: yeay there is blank space so you can move");
                swap(x,y,newX,newY);
                return true;
            } else {
                //Log.d(TAG, "move: Can't move caused no blank space");
                return false;
            }
        } else {
            //Log.d(TAG, "move: out of boundary");
            return false;
        }
    }

    private void swap(int x, int y, int newX, int newY){
       // Log.d(TAG, "swap: from "+x+" "+y+" to "+newX+" "+newY);
       // Log.d(TAG, "swap before: "+defaultArray.toString());
        defaultArray[newX][newY] = defaultArray[x][y];
        defaultArray[x][y] = BLANK;
        imageViews[newX][newY].setImageBitmap(bitmap.get(defaultArray[newX][newY]));
        imageViews[x][y].setImageDrawable(null);
        //Log.d(TAG, "swap after: "+defaultArray.toString());
    }

    private void isFinished(){
        boolean isDone = true;
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                if (defaultArray[i][j] != goalArray[i][j]){
                    isDone = false;
                    break;
                }
            }
            if (!isDone) break;
        }

        if(isDone){
            Toast.makeText(this, "Congrats!!!", Toast.LENGTH_LONG).show();
        }
    }
}
