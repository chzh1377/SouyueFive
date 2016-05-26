package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.fragment.SysPushHistoryFragment;
import com.zhongsou.souyue.fragment.UserPushHistoryFragment;
import com.zhongsou.souyue.ui.indicator.IcommonTabPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class FragmentMsgPushHistoryActivity extends RightSwipeActivity implements OnClickListener {

//	private boolean isSysRecState;
	private SysPushHistoryFragment sysFragment;
	private UserPushHistoryFragment userFragment;
	
    private IcommonTabPageIndicator indicator;
	private ViewPager pager;
	private FragmentPagerAdapter adapter;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_msgpush_history_new);
		initView();
		adapter = new PagerAdapter(getSupportFragmentManager());
		pager = findView(R.id.viewpager);
		pager.setOffscreenPageLimit(5);
		indicator = findView(R.id.indicator);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
	}
	
	
	private void initView() {
		sysFragment = new SysPushHistoryFragment();
		userFragment = new UserPushHistoryFragment();
		fragments.add(sysFragment);
		fragments.add(userFragment);
	}

	class PagerAdapter extends FragmentPagerAdapter {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return position == 0 ? getResources().getString(R.string.push_system_recommend) : getResources().getString(R.string.push_user_recommend);
		}

		@Override
		public int getCount() {
			if (fragments != null)
				return fragments.size();
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		
	}
	
}
