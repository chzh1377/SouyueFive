package com.zhongsou.souyue.ui.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SearchNavigationAdapter;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.ui.TabPageIndicator.OnTabSelectedItemListener;

import static android.view.ViewGroup.LayoutParams.*;

/**
 * @author zhangliang01@zhongsou.com
 */
public class TabPageIndicator extends HorizontalScrollView {
    /**
     * Title text used when no title is provided by the adapter.
     */
    protected BaseAdapter adapter;
    private boolean mClickable;
    private int layoutwith;
    // add by trade
	private OnTabSelectedItemListener mOnTabSelectedItemListener;

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

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new LinearLayout(getContext());
        mTabLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(mTabLayout, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
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

//		final int childCount = mTabLayout.getChildCount();
//		if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
//			if (childCount > 2) {
//				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
//			} else {
//				mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
//			}
//		} else {
//			mMaxTabWidth = -1;
//		}

//		final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		final int newWidth = getMeasuredWidth();

//		if (lockedExpanded && oldWidth != newWidth) {
//			// Recenter the tab display if we're at a new (scrollable) size.
//			setCurrentItem(mSelectedTabIndex);
//		}
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
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
//		 final TabView tabView = new TabView(getContext());
//		 tabView.mIndex = index;
//		 tabView.setFocusable(true);
//		 tabView.setOnClickListener(mTabClickListener);
//		 tabView.setText(text);
//		 tabView.setGravity(Gravity.CENTER);
//		 tabView.setBackgroundDrawable(getResources().getDrawable(R.drawable.vpi__tab_indicator));
//		 tabView.setTextAppearance(getContext(),
//		 R.style.TextAppearance_TabPageIndicator);
//		 tabView.setTextSize(14);
//		 tabView.setPadding(2, 22, 22, 2);
//		 tabView.setMaxLines(1);
//		text.setMinimumWidth(mMaxTabWidth);
        text.setTag(index);
        text.setOnClickListener(mTabClickListener);
        if (adapter instanceof SearchNavigationAdapter) {
            mTabLayout.setPadding(15, 0, 10, 0);
            mTabLayout.addView(text);
        } else {
            if(mIsWeightAble){
                mTabLayout.addView(text, new LinearLayout.LayoutParams(WRAP_CONTENT, FILL_PARENT, 1));
            }else{
                mTabLayout.addView(text, new LinearLayout.LayoutParams(WRAP_CONTENT, FILL_PARENT));
            }
        }
        layoutwith = mTabLayout.getWidth();
    }

    public void setCanClick(boolean _enable){
        mClickable = _enable;
        int count = mTabLayout.getChildCount();
        for(int i = 0;i<count;i++){
            View v = mTabLayout.getChildAt(i);
            v.setClickable(_enable);
        }
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
            final ViewGroup child = (ViewGroup)mTabLayout.getChildAt(i);
            final TextView tvTmp = (TextView) child.findViewById(R.id.navi_title);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
                tvTmp.setTextColor(getResources().getColor(R.color.color_srp_title));
            }else{
                tvTmp.setTextColor(getResources().getColor(R.color.actions_bg));
            }
            if(!ConfigApi.isSouyue()){
			    if(mOnTabSelectedItemListener!=null&&isSelected)
	                mOnTabSelectedItemListener.onTabSelectedItem(child, i);
			}
        }
    }

    public int getCurrentItem() {
        return mSelectedTabIndex;
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

//	private class TabView extends TextView {
//		private int mIndex;
//
//		public TabView(Context context) {
//			super(context);
//		}
//
//		@Override
//		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//			// Re-measure if we went beyond our maximum size.
//			if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
//				super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
//			}
//		}
//
//		public int getIndex() {
//			return mIndex;
//		}
//	}

	// add by trade
	public void setViewAdapter(BaseAdapter adapter,boolean isWeightAble) {
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        this.mIsWeightAble = isWeightAble;
        this.adapter = adapter;
        notifyDataSetChanged();
    }
	
	/**
     * 设置顶部导航条当前选择View的回调
     * @param onTabSelectedItemListener {link OnTabSelectedItemListener}
     */
    public void setOnTabSelectedItemListener(OnTabSelectedItemListener onTabSelectedItemListener){
        this.mOnTabSelectedItemListener = onTabSelectedItemListener;    
    }
    /**
     * 设置是否开启比重属性，控制每个Item是否平分布局
     */
    public boolean mIsWeightAble = false;
    /**
     * 设置 是否每个子Item是否平分布局.
     * @param isWeightAble 默认为true，设置比重属性，每个Item都平分布局(如当只有两项时，平分屏幕显示)；
     *                     false，不设置比重，每个Item都顺序从左侧添加(如当只有两项时，每一项都在左侧排列)
     */
    public void setIsWeightAble(boolean isWeightAble) {
        this.mIsWeightAble = isWeightAble;
        notifyDataSetChanged();
    }
}
