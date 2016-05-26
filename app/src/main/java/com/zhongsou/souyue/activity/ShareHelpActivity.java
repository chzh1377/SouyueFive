package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.fragment.GuideFragment;
import com.zhongsou.souyue.ui.indicator.PageIndicator;

public class ShareHelpActivity extends FragmentActivity {
	private int currentItem;
	private GestureDetector detector;
	private int flaggingWidth;

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	GuideFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;
	protected final int[] CONTENT = new int[] { R.drawable.help_1, R.drawable.help_2, R.drawable.help_3, R.drawable.help_4 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		setContentView(R.layout.user_guide);
		flaggingWidth = getDM();
		detector = new GestureDetector(new GuideViewTouch());
		mAdapter = new GuideFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentItem = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mPager.setAdapter(mAdapter);

		// CirclePageIndicator indicator = (CirclePageIndicator)
		// findViewById(R.id.indicator);
		// mIndicator = indicator;
		// indicator.setViewPager(mPager);
		// indicator.setSnap(true);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (detector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * view adapter
	 * 
	 * @author eye_
	 * 
	 */
	class GuideFragmentAdapter extends FragmentPagerAdapter {
		private int mCount = CONTENT.length;
		private int position;

		public GuideFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			this.position = position;
			return GuideFragment.newInstance(CONTENT[position % CONTENT.length], CONTENT.length, position);
		}

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public int getItemPosition(Object object) {
			return this.position;
		}
	}

	/**
	 * 手势识别
	 * 
	 * @author eye_
	 * 
	 */
	private class GuideViewTouch extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (currentItem == 3) {
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY()) && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1.getX() - e2.getX() >= flaggingWidth)) {
					if (e1.getX() - e2.getX() >= flaggingWidth) {
						GoTo();
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * 获得屏幕分辨率
	 * 
	 * @return
	 */
	private int getDM() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels / 3;
	}

	/**
	 * 关闭
	 */
	private void GoTo() {
		finish();
	}

	protected void onSaveInstanceState(Bundle outState) {
	}

}