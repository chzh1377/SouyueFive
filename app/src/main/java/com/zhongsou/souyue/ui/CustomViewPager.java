package com.zhongsou.souyue.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomViewPager extends ViewPagerWithTips {
    float topEdge = 0;
    private boolean enabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTopEdge(float top) {
        topEdge = top;
    }

    /**
     * 控制嵌套viewpager
     */
    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
//		LogDebugUtil.v("fan", "dx="+dx);
        if (v != this && v instanceof ViewPager) {
            int currentItem = ((ViewPager) v).getCurrentItem();
            int countItem = ((ViewPager) v).getAdapter().getCount();
            if ((currentItem == (countItem - 1) && dx < 0) || (currentItem == 0 && dx > 0)) {
                return false;
            }
            return true;
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!enabled) {
            return false;
        }
        try {
            float ey = ev.getY();
            if (ey < topEdge) {
                return false;
            }
        } catch (IllegalArgumentException e) {

        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        if (!enabled) {
            return false;
        }
        try {
            float ey = ev.getY();
            if (ey < topEdge) {
                return false;
            }
        } catch (IllegalArgumentException e) {

        }
        return super.onTouchEvent(ev);
    }

    /**
     * 控制viewpager是否允许滑动
     *
     * @param enabled false 不允许
     */
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
