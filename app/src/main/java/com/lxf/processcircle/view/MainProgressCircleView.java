package com.lxf.processcircle.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.lxf.processcircle.R;
import com.lxf.processcircle.utils.UiUtils;

/**
 * Created by Jaydon on 2016/1/10.
 */
public class MainProgressCircleView extends View{
    private Context mContext;
    private int mTotalWidth, mTotalHeight; //控件宽度，高度
    private int mInCircleColor = Color.WHITE; //里面圆颜色
    private int mOutCircleColor = Color.WHITE; //外面圆颜色
    private Paint mInCirclePaint; //里面圆画笔
    private Paint mOutCirclePaint; //外面圆画笔
    private Paint mCirclePointPaint; //结束点
    private RectF mOutCircleRect; //外面圆所绘制区域
    private int mInCircleWidth = 2, mOutCircleWidth = 5;
    private int mCircleDiameter = 200; //圆的直径 单位是dp
    private int mCircleRadius; //圆的半径
    private int centerX, centerY;
    private final int TOTAL_PROGRESS = 1;
    private final int TOTAL_DEGREE = 360;
    private float mTargetNum, mNowNum;
    private float progress; //目前进度
    private ObjectAnimator mAnimator; //属性动画

    public MainProgressCircleView(Context context, AttributeSet attrs) {
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
        mCircleDiameter = UiUtils.dipToPx(mContext, mCircleDiameter); //转化为px
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.MainProgressCircleView);
        mInCircleColor = a.getColor(R.styleable.MainProgressCircleView_InCircleColor, mInCircleColor);
        mOutCircleColor = a.getColor(R.styleable.MainProgressCircleView_OutCircleColor, mOutCircleColor);
        mCircleDiameter = a.getDimensionPixelOffset(R.styleable.MainProgressCircleView_CircleDiameter, mCircleDiameter);
        a.recycle();
        mCircleRadius = mCircleDiameter >> 1;
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //设置里面圆画笔
        mInCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mInCirclePaint.setAntiAlias(true);// 去除画笔锯齿
        mInCirclePaint.setStyle(Paint.Style.STROKE);// 设置风格为实线
        mInCirclePaint.setColor(mInCircleColor);// 设置画笔颜色
        mInCirclePaint.setStrokeWidth(mInCircleWidth);
        //设置外面圆画笔
        mOutCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mOutCirclePaint.setAntiAlias(true);// 去除画笔锯齿
        mOutCirclePaint.setStyle(Paint.Style.STROKE);// 设置风格为实线
        mOutCirclePaint.setColor(mOutCircleColor);// 设置画笔颜色
        mOutCirclePaint.setStrokeWidth(mOutCircleWidth);
        //设置结束点画笔
        mCirclePointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mCirclePointPaint.setAntiAlias(true);// 去除画笔锯齿
        mCirclePointPaint.setStyle(Paint.Style.STROKE);// 设置风格为实线
        mCirclePointPaint.setColor(Color.BLACK);// 设置画笔颜色
        mCirclePointPaint.setStrokeWidth(mOutCircleWidth);
    }

    /**
     * 初始化数据
     */
    private void initData() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawCircle(canvas);
        drawCirclePaint(canvas);
    }

    /**
     * 绘制背景
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        if(progress < 0.5) {
            canvas.drawColor(Color.BLUE);
        } else if(progress < 0.8) {
            canvas.drawColor(Color.GREEN);
        }
    }

    /**
     * 绘圆
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        //绘制里圆
        canvas.drawCircle(centerX, centerY, mCircleRadius, mInCirclePaint);
        //绘制外圆
        canvas.drawArc(mOutCircleRect, 270, progress * TOTAL_DEGREE, false, mOutCirclePaint);
    }

    /**
     * 绘制圆上的结束点
     * @param canvas
     */
    private void drawCirclePaint(Canvas canvas) {
        canvas.save();
        canvas.translate(centerX, centerY - mCircleRadius);
        float degree = progress * TOTAL_DEGREE;
        canvas.rotate(degree, 0, mCircleRadius);
        canvas.drawPoint(0, 0, mCirclePointPaint);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        centerX = w >> 1;
        centerY = (int) (getY() + getPaddingTop() + mCircleRadius);
        mOutCircleRect = new RectF(centerX - mCircleRadius, centerY - mCircleRadius, centerX + mCircleRadius, centerY + mCircleRadius);
    }

    /**
     * 取得进度属性
     * @return
     */
    public float getProgress() {
        return progress;
    }

    /**
     * 设置进度属性
     * @param progress
     */
    public void setProgress(float progress) {
        this.progress = progress;
        postInvalidate();
    }

    /**
     * 设置目标值和当前值，并更新进度，开始动画
     * @param targerNum
     * @param nowNum
     */
    public void setTargetAndNowNum(float targerNum, float nowNum) {
        this.mTargetNum = targerNum;
        this.mNowNum = nowNum;
        beginCircleAniminate();
    }

    /**
     * 圆的进度动画
     */
    private void beginCircleAniminate() {
        float temp = mNowNum / mTargetNum;
        progress = Math.min(temp, TOTAL_PROGRESS);
        if(null == mAnimator) {
            mAnimator = ObjectAnimator.ofFloat(this, "progress", 0, progress);
        }
        if(!mAnimator.isRunning()) {
            mAnimator.setDuration(2000);
            mAnimator.start();
        }
    }
}
