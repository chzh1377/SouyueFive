package com.zhongsou.souyue.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ImageViewPager extends ViewPager{

    private OnTouchListener onTouchListener;

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return super.onInterceptTouchEvent(arg0);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouchListener.onTouch(this, ev);
        return super.dispatchTouchEvent(ev);
    }
    
    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.onTouchListener = l;
    }
}
