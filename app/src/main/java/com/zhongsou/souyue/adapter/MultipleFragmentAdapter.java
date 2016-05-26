//package com.zhongsou.souyue.adapter;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import com.zhongsou.souyue.fragment.SRPFragment;
//
//import java.util.ArrayList;
//
///**
// * @author : zoulu
// * 2014年5月15日
// * 下午5:31:38
// * 类说明 :
// */
//public class MultipleFragmentAdapter extends FragmentPagerAdapter{
//
//	private ArrayList<SRPFragment> fragments=new ArrayList<SRPFragment>();
//
//	public MultipleFragmentAdapter(FragmentManager fm) {
//		super(fm);
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	public Fragment getItem(int arg0) {
//		return getFragments().get(arg0);
//	}
//
//	@Override
//	public int getCount() {
//		return getFragments().size();
//	}
//
//	@Override
//	public int getItemPosition(Object object) {
//		return super.getItemPosition(object);
//	}
//
//	public ArrayList<SRPFragment> getFragments() {
//		return fragments;
//	}
//
//	public void setFragments(ArrayList<SRPFragment> fragments) {
//		this.fragments = fragments;
//	}
//
//}
