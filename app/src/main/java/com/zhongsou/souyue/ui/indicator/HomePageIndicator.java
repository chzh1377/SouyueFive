package com.zhongsou.souyue.ui.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.zhongsou.souyue.adapter.SearchNavigationAdapter;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author zhangliang01@zhongsou.com
 */
public class HomePageIndicator extends HorizontalScrollView {
    /**
     * Title text used when no title is provided by the adapter.
     */
    protected BaseAdapter adapter;
    private int layoutwith;

    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }

    private Runnable mTabSelector;

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            final int newSelected = (Integer) view.getTag();
            if (mTabReselectedListener != null) {
                mTabReselectedListener.onTabReselected(newSelected);
            }
            setCurrentItem(newSelected);
        }
    };

    private final LinearLayout mTabLayout;
    //	private int mMaxTabWidth;
    private int mSelectedTabIndex;

    private OnTabReselectedListener mTabReselectedListener;

    public HomePageIndicator(Context context) {
        this(context, null);
    }

    public HomePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new LinearLayout(getContext());
        mTabLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(mTabLayout, new ViewGroup.LayoutParams(FILL_PARENT, FILL_PARENT));
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        boolean isRightEdg = false;
        if (mTabLayout.getMeasuredWidth() <= getScrollX() + getWidth()) {
            isRightEdg = true;
        }
        if (rightEdgeListener != null)
            rightEdgeListener.onRightEdge(isRightEdg);
    }

    public RightEdgeListener rightEdgeListener;

    public interface RightEdgeListener {
        void onRightEdge(boolean isRightEdg);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos + (getWidth() - tabView.getWidth()) / 2, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            post(mTabSelector);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private void addTab(View text, int index) {
        text.setTag(index);
        text.setOnClickListener(mTabClickListener);
        if (adapter instanceof SearchNavigationAdapter) {
            mTabLayout.setPadding(15, 0, 10, 0);
            mTabLayout.addView(text);
        } else {
            mTabLayout.addView(text, new LinearLayout.LayoutParams(WRAP_CONTENT, FILL_PARENT, 1));
        }
        layoutwith = mTabLayout.getWidth();
    }

    public int getLayoutwith() {
        return layoutwith;
    }

    public void setViewAdapter(BaseAdapter adapter) {
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        this.adapter = adapter;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View title = adapter.getView(i, null, this);
            addTab(title, i);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    public void setCurrentItem(int item) {
        mSelectedTabIndex = item;
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
    }

    public int getCurrentItem() {
        return mSelectedTabIndex;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }
}
