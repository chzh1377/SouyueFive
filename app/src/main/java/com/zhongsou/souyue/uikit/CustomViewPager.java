package com.zhongsou.souyue.uikit;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CustomViewPager extends ViewPager {
    private int childId;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
/*     @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (childId > 0) {          
            ViewPager pager = (ViewPager)findViewById(childId);

            if (pager != null) {           
                pager.requestDisallowInterceptTouchEvent(true);
            }

        }

        return super.onInterceptTouchEvent(event);
    }*/
     @Override
     protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
         if(v instanceof ViewPager) {
             return true;
         }
         return super.canScroll(v, checkV, dx, x, y);
     }
    public void setChildId(int id) {
        this.childId = id;
    }
}