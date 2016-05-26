package com.zhongsou.souyue.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by zhou on 2015/11/4.
 * 向右侧滑即退出activity
 * 需要设置 mDetector  和 mListener；
 * 并且设置 isCanRightSwipe 来确定 是否可以侧滑关闭  默认不支持侧滑关闭
 *
 */
public class RightSwipeView extends LinearLayout {


    private GestureDetector mDetector;
    private ActivityFinishListener mListener;

    private boolean isCanRightSwipe = false;

    public RightSwipeView(Context context) {
        super(context);
    }

    public RightSwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightSwipeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDetector(GestureDetector detector){
        this.mDetector = detector;
    }

    public void setFinishListener(ActivityFinishListener listener){
        this.mListener = listener;
    }

    public static interface ActivityFinishListener{
        void finishActivity();
    }

    private float mLastMotionX;
    private boolean isFinish = false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(this.mDetector ==null){
            throw new NullPointerException("RightSwipeView 的 this.mDetector 不可以为空，情调用setFinishListener(ActivityFinishListener listener) 设置该值");
        }
        if (this.mDetector.onTouchEvent(ev)) {
            return true;
        }
        final int action = ev.getAction();
        final float x = ev.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                final float delay = x - mLastMotionX;
                if (delay > getMeasuredWidth() / 2 && isCanRightSwipe()) {
                    isFinish = true;
                    if(mListener == null){
                        throw new NullPointerException("RightSwipeView 的 this.mListener 不可以为空，情调用setDetector(GestureDetector detector) 设置该值");
                    }else{
                        mListener.finishActivity();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isFinish) {
                    isFinish = false;
                    return true;
                }
                break;
            default:
                break;
        }
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    public boolean isCanRightSwipe() {
        return isCanRightSwipe;
    }

    public void setIsCanRightSwipe(boolean isCanRightSwipe) {
        this.isCanRightSwipe = isCanRightSwipe;
    }

}
