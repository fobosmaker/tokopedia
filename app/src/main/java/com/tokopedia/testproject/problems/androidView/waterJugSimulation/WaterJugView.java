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
import android.view.View;

public class WaterJugView extends View {

    private int maxWater = 0;
    private int waterFill = 0;
    private Rect mRect = new Rect();
    private Paint mPaint = new Paint();

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

    @Override
    protected void onDraw(Canvas canvas) {
        // border
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0,0,getWidth(), getHeight(), mPaint);
        // fill
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(mRect, mPaint);
    }

    public void drawJug(){
        int selisih = 0;
        if(maxWater != 0 && waterFill != 0) selisih = waterFill * (getHeight()/maxWater);
        mRect.left = 0;
        mRect.top = getHeight()-selisih; //change for the height
        mRect.right = getWidth();
        mRect.bottom = getHeight();
    }
}
