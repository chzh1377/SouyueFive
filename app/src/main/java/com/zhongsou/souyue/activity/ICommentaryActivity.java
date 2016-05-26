package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.fragment.ICommentarysFragment;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.ui.indicator.IcommonTabPageIndicator;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @description: 添加的注释，我的评论 和 回复我的
 *
 *
 * @auther: qubian 加注释
 * @data: 2015/12/12.
 */

public class ICommentaryActivity extends BaseActivity {
	public static final String[] CONTENT = new String[] { "我的评论", "回复我的" };
	private IcommonTabPageIndicator indicator;
	private ViewPager pager;
	private FragmentPagerAdapter adapter;
	private User user = SYUserManager.getInstance().getUser();
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.sy_icomments_layout);
		pager = findView(R.id.viewpager);
		indicator = findView(R.id.indicator);
		adapter = new PagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
		super.onCreate(arg0);
	}
	
	class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ICommentarysFragment.newInstance(CONTENT[position % CONTENT.length], user != null ? user.token() : "");
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
          return CONTENT.length;
        }
    }
}
