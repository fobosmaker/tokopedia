package com.tokopedia.testproject.problems.androidView.waterJugSimulation;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class WaterJugView extends View {

    private int maxWater = 0;
    private int waterFill = 0;
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();
    private static int JUG_1_DIMENS = 100;
    private static int JUG_2_DIMENS = 140;
    private static final String TAG = "WaterJugView";

    public WaterJugView(Context context) {
        super(context);
    }

    public WaterJugView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterJugView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WaterJugView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMaxWater(int maxWater) {
        this.maxWater = maxWater;
    }

    public void setWaterFill(int waterFill) {
        this.waterFill = waterFill;
    }


    public void drawJug(int jug){
        switch (jug){
            case 1:
                if(waterFill != 0){
                    //Log.d(TAG, "drawJug"+jug+": max="+maxWater+" fill"+waterFill+" percentage:" + maxWater/waterFill);
                    mRect.left = JUG_1_DIMENS * (2/maxWater);
                    mRect.top = JUG_1_DIMENS;
                    mRect.right = mRect.left + JUG_1_DIMENS;
                    mRect.bottom = mRect.top + JUG_1_DIMENS;
                    mPaint.setColor(Color.BLUE);
                }
                break;
            case 2:
                if(waterFill != 0) {
                    Log.d(TAG, "drawJug"+jug+": max="+maxWater+" fill"+waterFill+" percentage:" + maxWater/waterFill);
                    mRect.left = JUG_2_DIMENS * (waterFill/maxWater*100);
                    mRect.top = JUG_2_DIMENS;
                    mRect.right = mRect.left + JUG_2_DIMENS;
                    mRect.bottom = mRect.top + JUG_2_DIMENS;
                    mPaint.setColor(Color.GREEN);
                }
                break;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mRect,mPaint);
    }

    //TODO
    /*
    Based on these variables: maxWater and waterFill, draw the jug with the water

    Example a:
    maxWater = 10
    waterFill = 0

    Result,
    View will draw like below
    |        |
    |        |
    |        |
    |        |
    `--------'

    Example b:
    maxWater = 10
    waterFill = 5

    Result,
    View will draw like below
    |        |
    |        |
    |--------|
    |        |
    `--------'

    Example c:
    maxWater = 10
    waterFill = 8

    Result,
    View will draw like below
    |        |
    |--------|
    |        |
    |        |
    `--------'

    Example d:
    maxWater = 10
    waterFill = 10

    Result,
    View will draw like below
     ________
    |        |
    |        |
    |        |
    |        |
    `--------'
    */

}
