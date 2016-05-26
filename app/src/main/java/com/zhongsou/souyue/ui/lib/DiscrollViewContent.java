package com.zhongsou.souyue.ui.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;

/**
 *
 */
public class DiscrollViewContent extends LinearLayout {

    public DiscrollViewContent(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public DiscrollViewContent(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public DiscrollViewContent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(asDiscrollvable(child, (LayoutParams) params), index, params);
    }

    private View asDiscrollvable(View child, LayoutParams lp) {
        if(! isDiscrollvable(lp)) {
            return child;
        }
        DiscrollvableView discrollvableChild = new DiscrollvableView(getContext());
        discrollvableChild.setDiscrollveAlpha(lp.mDiscrollveAlpha);
        discrollvableChild.addView(child);
        return discrollvableChild;
    }

    private boolean isDiscrollvable(LayoutParams lp) {
        return lp.mDiscrollveAlpha;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LinearLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LinearLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        public boolean mDiscrollveAlpha;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DiscrollView_LayoutParams);
            try {
                mDiscrollveAlpha = a.getBoolean(R.styleable.DiscrollView_LayoutParams_discrollve_alpha, false);
            } finally {
                a.recycle();
            }
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }
}
