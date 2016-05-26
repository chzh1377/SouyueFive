package android.support.v4.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SearchNavigationAdapter;
import com.zhongsou.souyue.ui.indicator.HomePageIndicator;
import com.zhongsou.souyue.ui.indicator.HomePageIndicator.OnTabReselectedListener;
import com.zhongsou.souyue.ui.indicator.HomePageIndicator.RightEdgeListener;

import java.lang.ref.WeakReference;

public class HomeOuterTabPageIndicator extends LinearLayout implements 
	OnTabReselectedListener, RightEdgeListener{
	ViewPager mPager;
	private WeakReference<PagerAdapter> mWatchingAdapter;
	private PageListener mPageListener = new PageListener();
	private HomePageIndicator indicator;
	private View moreView;
	private boolean searchflag;
	public HomeOuterTabPageIndicator(Context context) {
		this(context, null);
	}
	public HomeOuterTabPageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		indicator = new HomePageIndicator(context);
		LayoutParams indicator_params = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 1);
		addView(indicator, indicator_params);
		indicator.rightEdgeListener = this;
		moreView = new View(context);
		moreView.setBackgroundResource(R.drawable.indicator_hasmore);
		moreView.setVisibility(View.GONE);
		LayoutParams moreView_params = new LinearLayout.LayoutParams(20, LayoutParams.FILL_PARENT);
		moreView_params.setMargins(0, 0, 0, 5);
		addView(moreView, moreView_params);
	}
	
    @Override
	public void onRightEdge(boolean isRightEdg) {
		if(moreView.getVisibility() != View.GONE){
			if(isRightEdg) {
				moreView.setVisibility(View.INVISIBLE);
			}else {
				moreView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	@Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (!(parent instanceof ViewPager)) {
            return;
        }

        final ViewPager pager = (ViewPager) parent;
        final PagerAdapter adapter = pager.getAdapter();

        pager.setInternalPageChangeListener(mPageListener);
        pager.setOnAdapterChangeListener(mPageListener);
        mPager = pager;
        updateAdapter(mWatchingAdapter != null ? mWatchingAdapter.get() : null, adapter);
    }
	
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPager != null) {
            updateAdapter(mPager.getAdapter(), null);
            mPager.setInternalPageChangeListener(null);
            mPager.setOnAdapterChangeListener(null);
            mPager = null;
        }
    }
    
    void updateAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener);
            mWatchingAdapter = null;
        }
        
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(mPageListener);
            mWatchingAdapter = new WeakReference<PagerAdapter>(newAdapter);
        }
        
        if (mPager != null && mWatchingAdapter!=null && mWatchingAdapter.get()!=null) {
            requestLayout();
        }
    }
    
    private class PageListener extends DataSetObserver implements ViewPager.OnPageChangeListener,
		    ViewPager.OnAdapterChangeListener {
		private int mScrollState;
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		    if (positionOffset > 0.5f) {
		        // Consider ourselves to be on the next page when we're 50% of the way there.
		        position++;
		    }
//		    updateTextPositions(position, positionOffset, false);
		}
		
		@Override
		public void onPageSelected(int position) {
		    if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
		    	int index = mPager.getCurrentItem();
		    	indicator.setCurrentItem(index);
		    }
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		    mScrollState = state;
		}
		
		@Override
		public void onAdapterChanged(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
		    updateAdapter(oldAdapter, newAdapter);
		}
		
		@Override
		public void onChanged() {
		}
    }
    
    public void setViewPager(ViewPager vp) {
		mPager = vp;
	}
    
    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
		indicator.setOnTabReselectedListener(listener);
	}
    
	@Override
	public void onTabReselected(int position) {
		if(mPager != null){
			mPager.setCurrentItem(position);
		}
	}
	public void setViewAdapter(BaseAdapter navigationAdapter) {
		if(navigationAdapter == null){
			setVisibility(View.GONE);
			return;
		}else if(navigationAdapter.getCount()<=0){
			indicator.setViewAdapter(navigationAdapter);
			setVisibility(View.GONE);
			return;
		}
		
		if(getVisibility() != View.VISIBLE)
			setVisibility(View.VISIBLE);
		
		
		boolean show = setListViewHeightBasedOnChildren(navigationAdapter, getWidth());
		if(navigationAdapter instanceof SearchNavigationAdapter){
		    searchflag=true;
		}else{
		    moreView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
		indicator.setViewAdapter(navigationAdapter);
		indicator.setCurrentItem(0);
	}
	
	public static boolean setListViewHeightBasedOnChildren(BaseAdapter listAdapter, int width) {
        if (listAdapter == null) {
            return false;
        }
        int totalWidth = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, null);
            listItem.measure(0, 0);
            totalWidth += listItem.getMeasuredWidth();
        }
        
        if(totalWidth > width){
        	return true;
        }
        return false;
    }
	
	public void setCurrentItem(int i) {
		indicator.setCurrentItem(i);
	}
	
	public void notifyDataSetChanged() {
		if(indicator.getAdapter().getCount()<=0){//设置adapter（setViewAdapter（）方法）的时候可能隐藏了
			setVisibility(View.GONE);
			return;
		}
		
		if(getVisibility() != View.VISIBLE)
			setVisibility(View.VISIBLE);
	
		indicator.notifyDataSetChanged();
		
		boolean show = setListViewHeightBasedOnChildren(indicator.getAdapter(), getWidth());
		if(searchflag==true){
		    moreView.setVisibility(indicator.getLayoutwith()>getWidth()||show? View.VISIBLE : View.INVISIBLE);
		}else{
		    moreView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
		
	}
	public int getCurrentItem() {
		return indicator.getCurrentItem();
	}
}
