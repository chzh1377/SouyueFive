package com.zhongsou.souyue.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AutoLinefeedView extends ViewGroup {

    public AutoLinefeedView(Context context) {
        super(context);
    }

    public AutoLinefeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    LayoutParams layoutParams = getLayoutParams();
                    layoutParams.height = getChildAt(getChildCount() - 1).getBottom();
                    setLayoutParams(layoutParams);
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } catch (Exception ex) {
                }
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {

        final int count = getChildCount();
        int row = 0;
        int lengthX = 0;
        int lengthY = 0;
        for (int i = 0; i < count; i++) {

            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            lengthX += width;
            lengthY = row * (height) + height;
            if (lengthX > right) {
                lengthX = width;
                row++;
                lengthY = row * (height) + height;

            }

            child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        for (int index = 0; index < getChildCount(); index++) {
            final LinearLayout child = (LinearLayout) getChildAt(index);
            TextView tv = (TextView) child.getChildAt(0);
            tv.setMaxWidth(getMeasuredWidth() - 30);
//			child.measure(widthMeasureSpec,heightMeasureSpec);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }

    }

    public static int resolveSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.max(size, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }
}
