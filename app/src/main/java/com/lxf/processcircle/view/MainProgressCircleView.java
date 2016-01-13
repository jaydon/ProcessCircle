package com.lxf.processcircle.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import com.lxf.processcircle.R;
import com.lxf.processcircle.utils.UiUtils;
import java.math.BigDecimal;

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
    private Paint mBigTextPaint, mSmallTextPaint; //文本的的画笔
    private int mBigTextSize = 40, mSmallTextSize = 20;
    private int mTextPadding = 30;
    private RectF mOutCircleRect; //外面圆所绘制区域
    private int mInCircleWidth = 4, mOutCircleWidth = 8;
    private int mCircleDiameter = 200; //圆的直径 单位是dp
    private int mCircleRadius; //圆的半径
    private int centerX, centerY;
    private final int TOTAL_PROGRESS = 1;
    private final int TOTAL_DEGREE = 360;
    private float mTargetNum, mNowNum, mKilometre, mKcal;
    private float progress; //目前进度
    private ObjectAnimator mCircleAnimator, mNowNumAnimator, mTargetNumAnimator, mKilometreAnimator, mKcalAnimator; //属性动画
    private Bitmap icon;
    private int iconWidth, iconHeight;
    private TextView mainNumTV;
    private UpLinearLayout upLinearLayout;

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
        mBigTextSize = UiUtils.sp2px(mContext, mBigTextSize); //转化为px
        mSmallTextSize = UiUtils.sp2px(mContext, mSmallTextSize);//转化为px
        mTextPadding = UiUtils.dipToPx(mContext, mTextPadding);
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.MainProgressCircleView);
        mInCircleColor = a.getColor(R.styleable.MainProgressCircleView_InCircleColor, mInCircleColor);
        mOutCircleColor = a.getColor(R.styleable.MainProgressCircleView_OutCircleColor, mOutCircleColor);
        mCircleDiameter = a.getDimensionPixelOffset(R.styleable.MainProgressCircleView_CircleDiameter, mCircleDiameter);
        mBigTextSize = a.getDimensionPixelSize(R.styleable.MainProgressCircleView_BigText, mBigTextSize);
        mSmallTextSize = a.getDimensionPixelSize(R.styleable.MainProgressCircleView_SmallText, mSmallTextSize);
        mTextPadding = a.getDimensionPixelSize(R.styleable.MainProgressCircleView_TextPadding, mTextPadding);
        a.recycle();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mOutCircleWidth = UiUtils.dipToPx(mContext, mOutCircleWidth);
        mInCircleWidth = UiUtils.dipToPx(mContext, mInCircleWidth);
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
        //设置大文本画笔
        mBigTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mBigTextPaint.setAntiAlias(true);// 去除画笔锯齿
        mBigTextPaint.setColor(Color.WHITE);// 设置画笔颜色
        mBigTextPaint.setTextSize(mBigTextSize);
        //设置小文本画笔
        mSmallTextPaint= new Paint(Paint.ANTI_ALIAS_FLAG);// 初始绘制线的画笔
        mSmallTextPaint.setAntiAlias(true);// 去除画笔锯齿
        mSmallTextPaint.setColor(Color.WHITE);// 设置画笔颜色
        mSmallTextPaint.setTextSize(mSmallTextSize);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mCircleRadius = mCircleDiameter >> 1;
        icon = ((BitmapDrawable)mContext.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        iconWidth = icon.getWidth();
        iconHeight = icon.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawCircle(canvas);
        drawCirclePaint(canvas);
        drawBitMap(canvas);
        drawText(canvas);
    }

    /**
     * 绘制背景
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        int color = Color.WHITE;
        if(progress < 0.5) {
            color = Color.BLUE;
        } else if(progress < 0.8) {
            color = Color.GREEN;
        }
        canvas.drawColor(color);
        if(null != upLinearLayout) {
            upLinearLayout.setBackgroundColor(color);
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

    /**
     * 绘制图标
     * @param canvas
     */
    private void drawBitMap(Canvas canvas) {
        Rect src = new Rect(0, 0, iconWidth, iconHeight);
        Rect dest = new Rect(centerX - (iconWidth >> 1), mTextPadding * 2 - (iconHeight >> 1), centerX + (iconWidth >> 1), mTextPadding * 2 + (iconHeight >> 1));
        canvas.drawBitmap(icon, src, dest, mSmallTextPaint);
    }

    /**
     * 绘制文本
     */
    private void drawText(Canvas canvas) {
        //绘制现有步数
        String bigStr = (int) (mNowNum) + "";
        Paint.FontMetrics bigFontMetrics = mBigTextPaint.getFontMetrics();
        //计算大文字高度
        float fontTotalHeight = bigFontMetrics.bottom - bigFontMetrics.top;
        float bigY = fontTotalHeight / 2 - bigFontMetrics.bottom;
        float bigTextY = centerY + bigY / 2;
        //计算大文字宽度
        int bigX = getTextWidth(mBigTextPaint, bigStr);
        int bigTextX = centerX - bigX / 2;
//        canvas.drawText(bigStr, bigTextX, bigTextY, mBigTextPaint);
        if(null != mainNumTV) {
            mainNumTV.setText(bigStr);
        }

        //绘制目标步数
        String smallStr = "目标" + (int) mTargetNum + "步";
        //计算小文字高度
        Paint.FontMetrics smallFontMetrics = mSmallTextPaint.getFontMetrics();
        fontTotalHeight = smallFontMetrics.bottom - smallFontMetrics.top;
        float smallY = fontTotalHeight / 2 - smallFontMetrics.bottom;
        float smallTextY = centerY + bigY / 2 + mTextPadding ;
        //计算小文字宽度
        int smallX = getTextWidth(mSmallTextPaint, smallStr);
        int smallTextX = centerX - smallX / 2;
        canvas.drawText(smallStr, smallTextX, smallTextY, mSmallTextPaint);

        //绘制公里
        String kilometreStr = round(mKilometre, 2,BigDecimal.ROUND_UP) + "";
        smallTextY = centerY + mCircleRadius + mTextPadding;
        smallTextX = centerX - mCircleRadius * 4 / 5;
        canvas.drawText(kilometreStr, smallTextX, smallTextY, mSmallTextPaint);
        //绘制大卡
        String kcalStr = (int)mKcal + "";
        smallTextX = centerX + mCircleRadius * 4 / 5 - getTextWidth(mSmallTextPaint, kcalStr);
        canvas.drawText(kcalStr, smallTextX, smallTextY, mSmallTextPaint);

        //绘制公里单位
        smallTextY = centerY + mCircleRadius + mTextPadding + fontTotalHeight;
        smallTextX = centerX - mCircleRadius * 5 / 6;
        canvas.drawText("公里", smallTextX, smallTextY, mSmallTextPaint);
        //绘制大卡单位
        smallTextX = centerX + mCircleRadius * 5 / 6 - getTextWidth(mSmallTextPaint, "大卡");
        canvas.drawText("大卡", smallTextX, smallTextY, mSmallTextPaint);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mCircleDiameter = (int) (Math.min(w, h) * 0.7);
        mCircleRadius = mCircleDiameter >> 1;
        centerX = w >> 1;
        centerY = (int) (getY() + getPaddingTop() + mCircleRadius);
        mOutCircleRect = new RectF(centerX - mCircleRadius, centerY - mCircleRadius, centerX + mCircleRadius, centerY + mCircleRadius);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenHeight = UiUtils.getScreenHeightPixels(mContext);
        int screenWight = UiUtils.getScreenWidthPixels(mContext);
        setMeasuredDimension(widthMeasureSpec, (int) (Math.max(screenHeight, screenWight) * 0.5));
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

    public float getMNowNum() {
        return mNowNum;
    }

    public void setMNowNum(float mNowNum) {
        this.mNowNum = mNowNum;
    }

    public float getMTargetNum() {
        return mTargetNum;
    }

    public void setMTargetNum(float mTargetNum) {
        this.mTargetNum = mTargetNum;
    }

    public float getMKilometre() {
        return mKilometre;
    }

    public void setMKilometre(float mKilometre) {
        this.mKilometre = mKilometre;
    }

    public float getMKcal() {
        return mKcal;
    }

    public void setMKcal(float mKcal) {
        this.mKcal = mKcal;
    }

    /**
     * 设置目标值和当前值，并更新进度，开始动画
     * @param targerNum
     * @param nowNum
     */
    public void setTargetAndNowNum(float targerNum, float nowNum, float kilometre, float kcal, TextView mainNumTV) {
        this.mTargetNum = targerNum;
        this.mNowNum = nowNum;
        this.mKilometre = kilometre;
        this.mKcal = kcal;
        this.mainNumTV = mainNumTV;
        beginCircleAniminate();
    }

    /**
     * 圆的进度动画
     */
    private void beginCircleAniminate() {
        float temp = mNowNum / mTargetNum;
        progress = Math.min(temp, TOTAL_PROGRESS);
        if(null == mCircleAnimator) {
            mCircleAnimator = ObjectAnimator.ofFloat(this, "progress", 0, progress);
            mNowNumAnimator = ObjectAnimator.ofFloat(this, "mNowNum", 0, mNowNum);
            mTargetNumAnimator = ObjectAnimator.ofFloat(this, "mTargetNum", 0, mTargetNum);
            mKilometreAnimator = ObjectAnimator.ofFloat(this, "mKilometre", 0, mKilometre);
            mKcalAnimator = ObjectAnimator.ofFloat(this, "mKcal", 0, mKcal);

        }
        if(!mCircleAnimator.isRunning()) {
            AccelerateInterpolator accelerateInterpolator =  new AccelerateInterpolator();
            AnimatorSet animSet = new AnimatorSet();
            animSet.setInterpolator(accelerateInterpolator);
            animSet.play(mCircleAnimator).with(mNowNumAnimator).with(mTargetNumAnimator).with(mKilometreAnimator).with(mKcalAnimator);
            animSet.setDuration(1000);
            animSet.start();
        }
    }

    /**
     * 获取文字宽度
     * @param paint
     * @param str
     * @return
     */
    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 对double数据进行取精度.
     * <p>
     * For example: <br>
     * double value = 100.345678; <br>
     * double ret = round(value,4,BigDecimal.ROUND_HALF_UP); <br>
     * ret为100.3457 <br>
     *
     * @param value
     *            float.
     * @param scale
     *            精度位数(保留的小数位数).
     * @param roundingMode
     *            精度取值方式.
     * @return 精度计算后的数据.
     */
    private float round(float value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        float d = bd.floatValue();
        bd = null;
        return d;
    }

    public void setUPRelativeLayout(UpLinearLayout upLinearLayout) {
        this.upLinearLayout = upLinearLayout;
    }
}
