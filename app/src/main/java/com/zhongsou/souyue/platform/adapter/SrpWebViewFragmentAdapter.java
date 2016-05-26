package com.zhongsou.souyue.platform.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SrpWebViewFragmentAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> fragments;

	public SrpWebViewFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public SrpWebViewFragmentAdapter(FragmentManager fm,ArrayList<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;

	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}
	
	

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
	

}
