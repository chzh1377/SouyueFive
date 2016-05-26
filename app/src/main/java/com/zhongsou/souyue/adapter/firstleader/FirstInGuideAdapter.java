package com.zhongsou.souyue.adapter.firstleader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyw on 2016/3/25.
 */
public class FirstInGuideAdapter extends FragmentPagerAdapter {
    public static final String TAG = FirstInGuideAdapter.class.getSimpleName();

    private List<Fragment> fragmentList = new ArrayList<>();

    public FirstInGuideAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        fragmentList.addAll(fragments);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
