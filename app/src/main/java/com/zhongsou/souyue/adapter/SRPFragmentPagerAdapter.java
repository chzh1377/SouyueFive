package com.zhongsou.souyue.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.zhongsou.souyue.fragment.SRPFragment;
import com.zhongsou.souyue.module.NavigationBar;

import java.util.ArrayList;
import java.util.List;

public class SRPFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<SRPFragment> fragments=new ArrayList<SRPFragment>();
    private List<NavigationBar> titles;

    public SRPFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		return getFragments().get(arg0);
	}
	
	

	@Override
	public int getCount() {
		return getFragments().size();
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

    public void setTitles(List<NavigationBar> titles) {
        this.titles = titles;
    }
	public ArrayList<SRPFragment> getFragments() {
		return fragments;
	}

	public void setFragments(ArrayList<SRPFragment> fragments) {
		this.fragments = fragments;
	}

    @Override
    public CharSequence getPageTitle(int position) {
        if(titles!=null){
            return titles.get(position).title();
        }else{
            return "";
        }
    }
}
