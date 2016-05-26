package com.zhongsou.souyue.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.zhongsou.souyue.fragment.RssFragment;
import com.zhongsou.souyue.module.GroupKeywordItem;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年5月27日
 * 上午10:42:57 
 * 类说明 :首页Rss新闻源Adapter
 */
public class SouYueRssAdapter extends FragmentPagerAdapter{
	private List<GroupKeywordItem> title = new ArrayList<GroupKeywordItem>();
	private boolean imgAble;
	private List<Fragment> listf;
	public SouYueRssAdapter(FragmentManager fm , List<GroupKeywordItem> title , boolean imgAble) {
		super(fm);
		this.title = title;
		this.imgAble = imgAble;
		if(title!=null&&title.size()>0){
            listf=new ArrayList<Fragment>();
            initFragment(title);
        }
	}
    private void initFragment(List<GroupKeywordItem> items){
        Fragment frag = null;
        for(int i=0;i<items.size();i++){
            Bundle args = new Bundle();
            args.putString(RssFragment.URL, title.get(i).url());
            args.putBoolean(RssFragment.IMGABLE, imgAble);
            frag = new RssFragment();
            frag.setArguments(args);
            listf.add(frag);
        }
    }
	@Override
	public Fragment getItem(int arg0) {
	    if(listf!=null){
            return listf.get(arg0);
        }
        return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (title == null || title.size() <= 0)
			return 0;
		return title.size();
	}

}
