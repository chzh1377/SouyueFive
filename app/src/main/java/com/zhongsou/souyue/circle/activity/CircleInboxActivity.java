package com.zhongsou.souyue.circle.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.fragment.InboxContentPagerFragment;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlong on 14-4-28.
 */
public class CircleInboxActivity extends BaseActivity {

    public static final int RECOMMEND_TYPE_USER = 1;
    public static final int RECOMMEND_TYPE_SYS = 2;

    private ViewPager mViewPage;
    private TextView userRecommend, sysRecommend;

    private InboxPagerAdapter adapter;

    private long interest_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity_inbox);
        interest_id = getIntent().getLongExtra("interest_id", 0);
        initViews();
    }

    private void initViews(){
        mViewPage = (ViewPager) findViewById(R.id.vp_circle_inbox_content);
        userRecommend = (TextView) findViewById(R.id.tv_circle_inbox_title_user);
        sysRecommend = (TextView) findViewById(R.id.tv_circle_inbox_title_sys);

        adapter = new InboxPagerAdapter(getSupportFragmentManager());
        mViewPage.setAdapter(adapter);
        mViewPage.setOnPageChangeListener(new OnInboxPageChangeListener());
        userRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPage.setCurrentItem(0);
            }
        });
        sysRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPage.setCurrentItem(1);
            }
        });
    }

    /**
     * 返回按钮的点击事件
     * @param view
     */
    public void onBackPressClick(View view) {
        this.finish();
    }

    /**
     * 设置按钮的点击事件
     * @param view
     */
    public void onSettingClick(View view) {
        UIHelper.showInboxSettingPage(this, interest_id);
    }

    class InboxPagerAdapter extends FragmentPagerAdapter {

        private List<InboxContentPagerFragment> items = new ArrayList<InboxContentPagerFragment>(PAGER_DEFAULT_SIZE);

        public static final int PAGER_DEFAULT_SIZE = 2;

        public InboxPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i=0; i< PAGER_DEFAULT_SIZE; i++){
                InboxContentPagerFragment pagerFragment = new InboxContentPagerFragment(i==0 ? RECOMMEND_TYPE_USER :RECOMMEND_TYPE_SYS, interest_id);
                items.add(pagerFragment);
            }
        }

        @Override
        public Fragment getItem(int i) {
            return items.get(i);
        }

        @Override
        public int getCount() {
            return PAGER_DEFAULT_SIZE;
        }
    }


    class OnInboxPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            if (i == 0) {
                userRecommend.setBackgroundResource(R.drawable.vpi__tab_selected_holo);
                userRecommend.setTextColor(Color.parseColor("#94deff"));
            	
//                userRecommend.setTextColor(getResources().getColor(R.color.vpi__font_color_unselected));
//                userRecommend.setGravity(Gravity.CENTER);
//                userRecommend.setTextSize(7 * getResources().getDisplayMetrics().density);
//                userRecommend.setPadding(20, 20, 20, 25);
                userRecommend.setPadding(
                		DeviceUtil.dip2px(CircleInboxActivity.this, 15),
                		DeviceUtil.dip2px(CircleInboxActivity.this, 12),
                		DeviceUtil.dip2px(CircleInboxActivity.this, 15),
                		DeviceUtil.dip2px(CircleInboxActivity.this, 12));
                sysRecommend.setTextColor(Color.parseColor("#ffffff"));
                sysRecommend.setBackgroundColor(Color.parseColor("#00ffffff"));
        		
            }
            if (i==1) {
                sysRecommend.setBackgroundResource(R.drawable.vpi__tab_selected_holo);
                sysRecommend.setTextColor(Color.parseColor("#94deff"));
            	
//                userRecommend.setTextColor(getResources().getColor(R.color.vpi__font_color_unselected));
//                userRecommend.setGravity(Gravity.CENTER);
//                userRecommend.setTextSize(7 * getResources().getDisplayMetrics().density);
//                userRecommend.setPadding(20, 20, 20, 25);
                sysRecommend.setPadding(
                		DeviceUtil.dip2px(CircleInboxActivity.this, 15),
                		DeviceUtil.dip2px(CircleInboxActivity.this, 12),
                		DeviceUtil.dip2px(CircleInboxActivity.this, 15),
                		DeviceUtil.dip2px(CircleInboxActivity.this, 12));
                userRecommend.setTextColor(Color.parseColor("#ffffff"));
                userRecommend.setBackgroundColor(Color.parseColor("#00ffffff"));
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK)
        {
            int rTpye = data.getIntExtra("recommendType", -1);
            if (rTpye == RECOMMEND_TYPE_USER)
                ((InboxContentPagerFragment)adapter.getItem(0)).onStateResult(data);
            else if (rTpye == RECOMMEND_TYPE_SYS)
                ((InboxContentPagerFragment)adapter.getItem(1)).onStateResult(data);
            else {
                ((InboxContentPagerFragment)adapter.getItem(0)).onStateResult(data);
                ((InboxContentPagerFragment)adapter.getItem(1)).onStateResult(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public int getCurrentType(){
        return mViewPage.getCurrentItem() ==0 ? RECOMMEND_TYPE_USER:RECOMMEND_TYPE_SYS;
    }

}
