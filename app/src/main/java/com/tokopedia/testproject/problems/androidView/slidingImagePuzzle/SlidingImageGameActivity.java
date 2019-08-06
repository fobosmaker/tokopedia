package com.tokopedia.testproject.problems.androidView.slidingImagePuzzle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tokopedia.testproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.View.GONE;

public class SlidingImageGameActivity extends AppCompatActivity {
    public static final String X_IMAGE_URL = "link example";
    public static final int GRID_NO = 4;
    private String imageUrl;
    ImageView[][] imageViews = new ImageView[4][4];
    private GridLayout gridLayout;
    int[][] defaultArray = new int[4][4];
    List<Bitmap> bitmap = new ArrayList<>();
    ImageView fullImage;
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
        fullImage = findViewById(R.id.fullImage);
        fullImage.setVisibility(GONE);
        gridLayout = findViewById(R.id.gridLayout);

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
                    }
                });
            }
        }

        Solution.sliceTo4x4(this, new Solution.onSuccessLoadBitmap() {
            @Override
            public void onSliceSuccess(List<Bitmap> bitmapList, Bitmap bitmapView) {
                //TODO will randomize placement to grid. Note: the game must be solvable.
                //replace below implementation to your implementation.
                bitmap = bitmapList;
                fullImage.setImageBitmap(bitmapView);
                newGame();
            }

            @Override
            public void onSliceFailed(String message) {
                Toast.makeText(SlidingImageGameActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }, imageUrl);

        // TODO add implementation of the game.
        // There is image adjacent to blank space (either horizontal or vertical).
        // If that image is clicked, it will swap to the blank space
        // if the puzzle is solved (the image in the view is aligned with the original image), then show a "success" dialog

        // TODO add handling for rotation to save the user input.
        // If the device is rotated, it should retain user's input, so user can continue the game.
        //
        //
        // I add some configChanges in this activity on AndroidManifest.xml
    }

    private void newGame(){
        do{
            resetPuzzle();
            shufflePuzzle();
        } while(!isSolvable());
        drawPuzzle();
    }

    private void resetPuzzle(){
        int counter = 0;
        int limit = 15;
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                if (counter >= limit) break;
                defaultArray[i][j] = counter;
                counter++;
            }
            if (counter >= limit) break;
        }
        defaultArray[3][3] = BLANK;
    }

    private void shufflePuzzle(){
        int counter = 0;
        int limit = 15;
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                Random r = new Random();
                int row = r.nextInt(GRID_NO);
                int col = r.nextInt(GRID_NO);
                int tmp = defaultArray[i][j];
                defaultArray[i][j] = defaultArray[row][col];
                defaultArray[row][col] = tmp;
                counter++;
            }
            if (counter >= limit) break;
        }
    }

    private boolean isSolvable(){
        int count = 0;
        for (int i = 0; i < GRID_NO - 1; i++){
            for (int j = i + 1; j < GRID_NO; j++){
                if (defaultArray[j][i] > BLANK && defaultArray[j][i] > BLANK &&
                        defaultArray[j][i] > defaultArray[i][j])
                    count++;
            }
        }
        return (count % 2 == 0);
    }

    private void drawPuzzle(){
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                if(defaultArray[i][j] == BLANK) imageViews[i][j].setImageDrawable(null);
                else imageViews[i][j].setImageBitmap(bitmap.get(defaultArray[i][j]));
            }
        }
    }

    private void movePuzzle(int row, int col){
        //left
        if(!move(row,col,row,col-1)){
            //right
            if(!move(row,col,row,col+1)){
                //top
                if(!move(row,col,row-1,col)){
                    //bottom
                    if(move(row,col,row+1,col)) isFinished();
                } else isFinished();
            } else isFinished();
        } else isFinished();
    }

    private Boolean move(int x, int y, int newX, int newY){
        if(newX >= 0 && newX <= defaultArray.length-1 && newY >= 0 && newY < defaultArray[x].length){
            if(defaultArray[newX][newY] == BLANK){
                //swap value
                defaultArray[newX][newY] = defaultArray[x][y];
                defaultArray[x][y] = BLANK;
                imageViews[newX][newY].setImageBitmap(bitmap.get(defaultArray[newX][newY]));
                imageViews[x][y].setImageDrawable(null);
                return true;
            } else  return false;
        } else  return false;
    }

    private void isFinished(){
        boolean isDone = true;
        int counter = 0;
        int limit = 15;
        for (int i = 0; i < GRID_NO; i++) {
            for (int j = 0; j < GRID_NO; j++) {
                if (counter >= limit) break;
                if(defaultArray[i][j] != counter){
                    isDone = false;
                    break;
                }
                counter++;
            }
            if (counter >= limit) break;
            if(!isDone) break;
        }

        if(isDone){
            Toast.makeText(this, "Congrats!!!", Toast.LENGTH_LONG).show();
            gridLayout.setVisibility(GONE);
            fullImage.setVisibility(View.VISIBLE);
        }
    }
}
