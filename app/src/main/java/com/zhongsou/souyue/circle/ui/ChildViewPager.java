package com.zhongsou.souyue.circle.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wangqiang on 15/10/27.
 */
public class ChildViewPager extends ViewPager {

    private boolean scrollable = true;

    public ChildViewPager(Context context) {
        super(context);
    }

    public ChildViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置viewpager是否可以滚动
     *
     * @param enable
     */
    public void setScrollable(boolean enable) {
        scrollable = enable;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
////        if (scrollable) {
////            return super.onInterceptTouchEvent(event);
////        } else {
////            return true;
////        }
//        return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:

                break;

        }
        return super.onTouchEvent(ev);
    }
}
