package com.zhongsou.souyue.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.fragment.DownloadContentPagerFragmentV2;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.circle.view.PagerSlidingTabStrip;

public class DownloadActivity extends RightSwipeActivity {

    private static String[] TITLES = {"缓存中", "已缓存"};
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager mViewPage;
    private TextView sdcard_space;
    private InboxPagerAdapter adapter;
    
    private String useSize;			//占用空间
    private String availableSize;	//可用空间
    private int fileType;
    private int from;
    private int downloadState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity_download);
        fileType = getIntent().getIntExtra("fileType", -1);
        from = getIntent().getIntExtra("from", -1);
        downloadState = getIntent().getIntExtra("downloadstate", -1);
        ((TextView)findViewById(R.id.tv_statement)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.setClass(DownloadActivity.this, DownLoadStatementActivity.class);
				startActivity(i);
			}
		});
        initViews();
        initdata();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if(downloadState == 1){
    		mViewPage.setCurrentItem(1);
    	}
    }

	private void initViews() {
		mViewPage = (ViewPager) findViewById(R.id.pager);
		sdcard_space = (TextView) findViewById(R.id.tv_circle_download_sdcard_space);

        adapter = new InboxPagerAdapter(getSupportFragmentManager());
        mViewPage.setAdapter(adapter);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.indicator);
        pagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.space_14));
        pagerSlidingTabStrip.setTextColorResource(R.color.pstrip_text__normal_color);
        pagerSlidingTabStrip.setDividerColor(0xF2F2F2);
        pagerSlidingTabStrip.setViewPager(mViewPage);
	}
	
	
	private void initdata() {
		//可用空间
		availableSize = CircleUtils.getSDAvailableSize(DownloadActivity.this);
		//占用空间
		useSize = CircleUtils.getSDUseSize(DownloadActivity.this);
		sdcard_space.setText("\t\t占用空间："+ useSize + "\t\t可用空间：" + availableSize );
	}
	
	
	class InboxPagerAdapter extends FragmentPagerAdapter {

        public final int[] PAGER = {DownloadContentPagerFragmentV2.TYPE_DOWNLOADING, DownloadContentPagerFragmentV2.TYPE_DOWNLOADED};

        public InboxPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return new DownloadContentPagerFragmentV2(PAGER[i], fileType,from);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

		@Override
        public int getCount() {
            return PAGER.length;
        }
    }

}
