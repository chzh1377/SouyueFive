package com.zhongsou.souyue.ui.dynamicgrid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by lvqiang on 14-12-24.
 */
public class GrapeGridview extends GridView {

    boolean mInEdit;

    public GrapeGridview(Context context) {
        super(context);
    }
    public GrapeGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrapeGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEdited(boolean _isEdit){
        mInEdit=_isEdit;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mInEdit){
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public int computeVerticalScrollRange(){
        return super.computeVerticalScrollRange();
    }

    public int computeVerticalScrollExtent(){
        return super.computeVerticalScrollExtent();
    }

    public int computeVerticalScrollOffset(){
        return super.computeVerticalScrollOffset();
    }
}
