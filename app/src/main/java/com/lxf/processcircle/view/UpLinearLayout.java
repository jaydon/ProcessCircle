package com.lxf.processcircle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.lxf.processcircle.R;


/**
 * Created by luoxf on 2016/1/11.
 */
public class UpLinearLayout extends LinearLayout{
    private Context mContext;
    private Scroller mScroller;
    private MainProgressCircleView mainCircle;
    private TextView mainNum;
    private View mainData;
    private View mainWave;
    private int mainCircleHeight;
    private int mainNumHeight;
    private int mLastY;
    private int touchSlop; //最小滑动距离
    private int mainWaveHeight;
    private int mainNumMarginTop;

    public UpLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    /**
     * 初始化属性
     */
    private void initView() {
        mScroller = new Scroller(mContext, new AccelerateInterpolator());
        touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int y = (int) ev.getY();
        final int action = ev.getActionMasked();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                intercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastY;
                if(mScroller.getFinalY() >= mainCircleHeight && deltaY < 0) {
                    intercepted = false;
                }  else if(Math.abs(deltaY) > touchSlop) {
                    intercepted = true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                intercepted = false;
                break;
        }

        mLastY = y;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                int deltaY = 0;
                if(event.getPointerCount() == 1) {
                    deltaY = y - mLastY;
                } else {
                    int pointerId = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)>>>
                            MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    if(pointerId == 0) {
                        deltaY = y - mLastY;
                    }
                }

                if(mScroller.getFinalY() >= mainCircleHeight) {
                   if(deltaY > 0 && mScroller.getFinalY() - deltaY > 0) {
                       smoothScrollBy(0, -deltaY);
                   } else {
                       smoothScrollTo(0, mainCircleHeight);
                   }
                } else if(mScroller.getFinalY() < 0) {
                    smoothScrollTo(0, 0);
                } else {
                    if(mScroller.getFinalY() - deltaY < 0) {
                        smoothScrollTo(0, 0);

                    } else {
                        smoothScrollBy(0, -deltaY);
                    }
                }

                mLastY = y;
                break;
            //多指时只在最后一弹起时触发,现在只解决两指，在这里要更新mLastY，不然在会小的移动<touchslop的时候，会因为y-mLastY过大，而导致滑动
            case MotionEvent.ACTION_POINTER_UP:
                mScroller.abortAnimation();
                if (event.getPointerCount() >= 1) {
                    mLastY = (int) event.getY(event.findPointerIndex(event.getPointerCount() - 1));
                }
                return true;
            case MotionEvent.ACTION_UP:
                if(mScroller.getFinalY() > (mainCircleHeight >> 1)) {
                    smoothScrollTo(0, mainCircleHeight);
                } else {
                    smoothScrollTo(0, 0);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mainCircle = (MainProgressCircleView) findViewById(R.id.mainCircle);
        mainNum = (TextView) findViewById(R.id.mainNum);
        mainWave = findViewById(R.id.mainWave);
        mainData = findViewById(R.id.mainData);
        mainCircle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainCircle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mainCircle.setUPRelativeLayout(UpLinearLayout.this);
                mainCircleHeight = mainCircle.getHeight();
                mainNumHeight = mainNum.getHeight();
                mainCircleHeight -= mainNumHeight;
                LayoutParams layoutParams = (LayoutParams) mainData.getLayoutParams();
                layoutParams.width = LayoutParams.MATCH_PARENT;
                layoutParams.height = getHeight() - mainNumHeight;
                mainData.setLayoutParams(layoutParams);
                mainNumMarginTop = mainNum.getTop();
                mainWaveHeight = mainWave.getHeight();
            }
        });
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 0);
        postInvalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }
    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            float alpha = (mScroller.getCurrY() / (float)mainCircleHeight);
            mainCircle.setAlpha(1 - alpha);
            mainNum.setY(mainNumMarginTop + (mainCircleHeight - mainNumMarginTop) * alpha);
            mainWave.setAlpha(1 - alpha);
            if (alpha < 1) {
                mainWave.setVisibility(View.VISIBLE);
            } else {
                mainWave.setVisibility(View.GONE);
            }
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 设置目标值和当前值，并更新进度，开始动画
     * @param targerNum
     * @param nowNum
     */
    public void setTargetAndNowNum(float targerNum, float nowNum, float kilometre, float kcal) {
        mainCircle.setTargetAndNowNum(targerNum, nowNum, kilometre, kcal, mainNum);
    }
}
