package com.zhongsou.souyue.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by lvqiang on 14-12-19.
 */
public class DynamicViewPager extends ViewPager {
    boolean mIsInEdit;
    public DynamicViewPager(Context context) {
        super(context);
    }

    public DynamicViewPager(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public void setmIsInEdit(boolean isInEdit){
        mIsInEdit=isInEdit;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if(mIsInEdit){
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mIsInEdit){
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
