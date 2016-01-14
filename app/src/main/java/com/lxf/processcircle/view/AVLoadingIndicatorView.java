package com.lxf.processcircle.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.lxf.processcircle.R;
import com.lxf.processcircle.utils.UiUtils;

/**
 * Created by luoxf on 2016/1/14.
 */
public class AVLoadingIndicatorView extends View {
    private Context mContext;
    //Sizes (with defaults in DP)
    public static final int DEFAULT_SIZE = 30;

    int mIndicatorColor;

    Paint mPaint;

    BaseIndicatorController mIndicatorController;

    private boolean mHasAnimation;


    public AVLoadingIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }



    private void init(AttributeSet attrs) {
        mIndicatorColor = mContext.getResources().getColor(R.color.sport_day);
        mPaint=new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        applyIndicator();
    }

    private void applyIndicator(){
        mIndicatorController=new BallPulseIndicator();
        mIndicatorController.setTarget(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width  = measureDimension(UiUtils.dipToPx(mContext, DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(UiUtils.dipToPx(mContext, DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize,int measureSpec){
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicator(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!mHasAnimation){
            mHasAnimation=true;
            applyAnimation();
        }
    }

    void drawIndicator(Canvas canvas){
        mIndicatorController.draw(canvas, mPaint);
    }

    void applyAnimation(){
        mIndicatorController.createAnimation();
    }

}
