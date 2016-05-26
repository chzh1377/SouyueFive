package com.zhongsou.souyue.im.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zoulu
 * on 2015/1/9
 * Description:用于显示viewpager的gifadapter
 */
public class GifPagerAdapter extends PagerAdapter{

    private List<View> pageViews;

    public GifPagerAdapter(List<View> pageViews) {
        super();
        this.pageViews=pageViews;
    }

    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(pageViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(pageViews.get(position));
        return pageViews.get(position);
    }
}
