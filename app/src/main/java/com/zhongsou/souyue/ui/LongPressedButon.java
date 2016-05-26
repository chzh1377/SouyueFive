package com.zhongsou.souyue.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * @author zhangliang01@zhongsou.com
 */
public class LongPressedButon extends Button {

    long startTime;
    onRepeatListener listener;
    RepeatedTask task;
    int count = 0;
    boolean isup;
    int width;
    int height;
    boolean mIsRuning;

    public LongPressedButon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LongPressedButon(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public LongPressedButon(Context context) {
        this(context, null);
    }

    public void setOnRepeatListener(onRepeatListener listener) {
        this.listener = listener;
    }


    public void stop() {
        removeCallbacks(task);
        count = 0;
        isup = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isup) {
                removeCallbacks(task);
                listener.onRepeat(true, count);
                count = 0;
                mIsRuning = false;
                isup = true;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//			if(event.getY()<-15){
//				stop();
//				listener.cancelRecord();
//			}
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startTime = System.currentTimeMillis();
            isup = false;
            task = new RepeatedTask();
            mIsRuning = true;
            post(task);
        }
        return super.onTouchEvent(event);
    }

    class RepeatedTask implements Runnable {

        @Override
        public void run() {
            if(mIsRuning) {
                count++;
                if (count > 60) {
                    isup = !isup;
                    listener.onRepeat(isup, 60);
                    count = 0;
                    removeCallbacks(this);
                } else {
                    listener.onRepeat(isup, count);
                    postDelayed(this, 1000);
                }
            }
        }

    }

    public void setCancelPos(int width, int height) {
        this.width = width / 2;
        this.height = height / 2;
    }

    public interface onRepeatListener {
        public void onRepeat(boolean isEnd, int count);
//		public void cancelRecord();
    }

}
