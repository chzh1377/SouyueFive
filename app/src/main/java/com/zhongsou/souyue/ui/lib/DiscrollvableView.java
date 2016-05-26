package com.zhongsou.souyue.ui.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 *
 */
public class DiscrollvableView extends FrameLayout implements Discrollvable {


    private float mDiscrollveThreshold;
    private boolean mDiscrollveAlpha;

    private int mWidth;
    private int mHeight;

    public DiscrollvableView(Context context) {
        super(context);
    }

    public DiscrollvableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscrollvableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        onResetDiscrollve();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public void setDiscrollveThreshold(float discrollveThreshold) {
        if(discrollveThreshold < 0.0f || discrollveThreshold > 1.0f) {
            throw new IllegalArgumentException("threshold must be >= 0.0f and <= 1.0f");
        }
        mDiscrollveThreshold = discrollveThreshold;
    }

    public void setDiscrollveAlpha(boolean discrollveAlpha) {
        mDiscrollveAlpha = discrollveAlpha;
    }

    @SuppressLint("NewApi")
	@Override
    public void onDiscrollve(float ratio) {
        if(ratio >= mDiscrollveThreshold) {
            ratio = withThreshold(ratio);
            float ratioInverse = 1 - ratio;

            if(mDiscrollveAlpha) {
                setAlpha(ratio);
            }
        }
    }

    private float withThreshold(float ratio) {
        return (ratio - mDiscrollveThreshold) / (1.0f - mDiscrollveThreshold);
    }

    @SuppressLint("NewApi")
	@Override
    public void onResetDiscrollve() {
        if(mDiscrollveAlpha) {
            setAlpha(1.0f);
        }
    }

}
