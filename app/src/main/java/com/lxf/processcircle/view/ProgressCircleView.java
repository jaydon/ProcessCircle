package com.lxf.processcircle.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.lxf.processcircle.R;
import com.lxf.processcircle.utils.UiUtils;

/**
 * Created by luoxf on 2016/1/7.
 */
public class ProgressCircleView extends View{
    private Context mContext;
    private int mTotalWidth, mTotalHeight; //控件宽度，高度
    private int centerX, centerY; //圆心
    private int radius; //半径
    private Paint mLinePaint, mCirclePaint;
    private int mCricleMargin = 10;
    private int	mLineLength = 20; //刻度线长度
    private int mLineWidth = 5; //刻度的宽度
    private int mLineCount = 62; //刻度线的个数
    private int mLineColor; //原始线颜色
    private int mCircleColor; //圆颜色
    private int mChangeLineColor; //线改变的颜色
    private int mProgress; //进度
    private final int MAX_PROGRESS = 100; //最大进度
    private ObjectAnimator mAnimator; //属性动画
    public ProgressCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(attrs);
        initPaint();
        initData();
    }

    /**
     * 初始化属性
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.ProgressCircleView);
        mLineColor = a.getColor(R.styleable.ProgressCircleView_lineColor, Color.BLACK);
        mCircleColor = a.getColor(R.styleable.ProgressCircleView_circleColor, Color.BLACK);
        mChangeLineColor = a.getColor(R.styleable.ProgressCircleView_lineChangeColor, Color.GREEN);
        mLineLength = a.getDimensionPixelOffset(R.styleable.ProgressCircleView_lineHeight, mLineLength);
        a.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mLinePaint.setAntiAlias(true);// 去除画笔锯齿
        mLinePaint.setStyle(Paint.Style.STROKE);// 设置风格为实线
        mLinePaint.setColor(mLineColor);// 设置画笔颜色
        mLinePaint.setStrokeWidth(mLineWidth);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(1);
    }

    public int getMProgress() {
        return mProgress;
    }

    public void setMProgress(int progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    /**

     * 将dp转化为px,为了适配
     */
    private void initData() {
        mCricleMargin = UiUtils.dipToPx(mContext, mCricleMargin);
        mLineLength = UiUtils.dipToPx(mContext, mLineLength);
        mCricleMargin = getPaddingLeft() == 0 ? mCricleMargin : getPaddingLeft();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawLine(canvas);
    }

    /**
     * 画圆
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, radius, mCirclePaint);
    }

    /**
     * 画线
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        mProgress = mProgress > MAX_PROGRESS ? MAX_PROGRESS : mProgress;
        int changeLineCount = (int) (((float)mProgress / MAX_PROGRESS) * mLineCount);
        canvas.save();
        canvas.translate(mTotalWidth >> 1, mCricleMargin);
        float rotateDegree = (float) (360.0 / mLineCount);
        for(int i = 0; i < mLineCount; i++) {
            if(i < changeLineCount) {
                mLinePaint.setColor(mChangeLineColor);
            } else {
                mLinePaint.setColor(mLineColor);
            }
            canvas.drawLine(0, 0, 0, mLineLength, mLinePaint);
            canvas.rotate(rotateDegree, 0, radius);
        }
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //因为是圆，所以取最小，长宽相等
        mTotalWidth = Math.min(w, h);
        mTotalHeight = mTotalWidth;
        int tempRadius = mTotalWidth >> 1;
        radius = tempRadius - mCricleMargin;
        centerX = tempRadius;
        centerY = tempRadius;
    }

    /**
     * 设置进度
     * @param progress
     */
    public void setProgress(int progress) {
        mProgress = progress;
        startAnimate(progress);
    }

    /**
     * 进度变化
     */
    private void startAnimate(int progress) {
        if(null == mAnimator) {
            mAnimator = ObjectAnimator.ofInt(this, "mProgress", 0, progress);
        }
        if(!mAnimator.isRunning()) {
            mAnimator.setDuration(5000);
            mAnimator.start();
        }
    }
}
