package com.lxf.processcircle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

import com.lxf.processcircle.utils.UiUtils;

/**
 * Created by luoxf on 2016/1/11.
 */
public class DynamicWave extends View {
    // 波纹颜色
    private final int WAVE_PAINT_COLOR = 0x880000aa;
    // y = Asin(wx+b)+h
    private final float STRETCH_FACTOR_A = 20;
    private final int OFFSET_Y = 5;
    // 第一条水波移动速度
    private final int TRANSLATE_X_SPEED_ONE = 7;
    // 第二条水波移动速度
    private final int TRANSLATE_X_SPEED_TWO = 5;
    // 第三条水波移动速度
    private final int TRANSLATE_X_SPEED_THREE = 2;
    private float mCycleFactorW;

    private int mTotalWidth, mTotalHeight;
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private float[] mResetThreeYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOffsetSpeedThree;
    private int mXOneOffset;
    private int mXTwoOffset;
    private int mXThreeOffset;

    private Paint mWavePaint;
    private DrawFilter mDrawFilter;

    public DynamicWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_TWO);
        mXOffsetSpeedThree = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_THREE);

        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Style.FILL);
        // 设置画笔颜色
        mWavePaint.setColor(WAVE_PAINT_COLOR);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);
        resetPositonY();
        for (int i = 0; i < mTotalWidth; i++) {

            // 绘制第一条水波纹
            canvas.drawLine(i, mResetOneYPositions[i] + 20, i, mTotalHeight, mWavePaint);

            // 绘制第二条水波纹
            canvas.drawLine(i, mResetTwoYPositions[i] + 20, i, mTotalHeight, mWavePaint);

            //绘制第三条水波纹
            canvas.drawLine(i, mResetThreeYPositions[i] + 20, i, mTotalHeight, mWavePaint);
        }

        // 改变三条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;
        mXThreeOffset += mXOffsetSpeedThree;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }

        if (mXThreeOffset > mTotalWidth) {
            mXThreeOffset = 0;
        }

        // 引发view重绘，一般可以考虑延迟20-30ms重绘，空出时间片
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                postInvalidate();
            }
        };
        postDelayed(runnable, 30);
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = mYPositions.length - mXOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);

        int yThreeInterval = mYPositions.length - mXThreeOffset;
        System.arraycopy(mYPositions, mXThreeOffset, mResetThreeYPositions, 0,
                yThreeInterval);
        System.arraycopy(mYPositions, 0, mResetThreeYPositions, yThreeInterval, mXThreeOffset);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        mTotalWidth = w;
        mTotalHeight = h;
        // 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];
        // 用于保存波纹一的y值
        mResetOneYPositions = new float[mTotalWidth];
        // 用于保存波纹二的y值
        mResetTwoYPositions = new float[mTotalWidth];
        // 用于保存波纹三的y值
        mResetThreeYPositions = new float[mTotalWidth];

        // 将周期定为view总宽度
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }

}

