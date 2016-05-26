package com.zhongsou.souyue.im.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zcz on 2015/6/16.
 */
public class CustomerViewPager extends ViewPager {

    public static boolean isCanScroll = true;

    public CustomerViewPager(Context context) {
        super(context);
    }

    public CustomerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
            super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (isCanScroll){
//            System.out.println("onTouchEvent"+true);
            return super.onTouchEvent(arg0);
        }else{
//            System.out.println("onTouchEvent"+false);
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanScroll){
//            System.out.println("onInterceptTouchEvent"+true);
            return super.onInterceptTouchEvent(arg0);
        }else{
//            System.out.println("onInterceptTouchEvent"+false);
            return false;
        }
    }
}