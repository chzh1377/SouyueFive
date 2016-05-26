package com.zhongsou.souyue.ui.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 *
 */
public class DiscrollvableLayout extends LinearLayout implements Discrollvable {

    private static final String TAG = "DiscrollvableLayout";

    
    private float mRatio;
    private Paint mPaint;
    private ViewGroup mPathView;
    private ImageView mTimeIcon,mTimeIcon1;
    private TextView mTime;
    private ImageView mTimeLine;
    
    public DiscrollvableLayout(Context context) {
        super(context);
    }

    public DiscrollvableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
	public DiscrollvableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPathView = (ViewGroup) findViewById(R.id.rl_guide_item);
    }

    @SuppressLint("NewApi")
	@Override
    public void onDiscrollve(float ratio) {
		mPathView.setAlpha(ratio);
		invalidate();
    }

    @SuppressLint("NewApi")
	@Override
    public void onResetDiscrollve() {
        mRatio = 0.0f;
        mPathView.setAlpha(0);
        invalidate();
    }
    
}
