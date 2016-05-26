package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.fragment.InterestListFragment;
import com.zhongsou.souyue.fragment.RssSubscribeListFragment;
import com.zhongsou.souyue.fragment.SubscribeListBaseFragment;
import com.zhongsou.souyue.fragment.SubscribeListFragment;
import com.zhongsou.souyue.ui.indicator.SuberPageIndicator;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class SubscribeListActivity extends RightSwipeActivity implements
        OnClickListener {
    public static final String INTENT_INDEX = "nav_index";
    private int index;

    private SuberPageIndicator indicator;
    private ViewPager pager;
    private FragmentPagerAdapter adapter;
    private List<SubscribeListBaseFragment> fragments = new ArrayList<SubscribeListBaseFragment>();
    private ImageButton img_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_base_view);
        initView();
        adapter = new TabPagerAdapter(getSupportFragmentManager());
        pager = findView(R.id.viewpager);
        pager.setOffscreenPageLimit(5);
        indicator = findView(R.id.indicator);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        doFromIntent();
        doConversionIndex();
    }

    private void doConversionIndex() {
        // doSetCurrentItem(index);
        switch (index) {
            case R.string.manager_grid_rss:
                doSetCurrentItem(2);
                break;
            case R.string.manager_grid_insterest:
                doSetCurrentItem(0);
                break;
            case R.string.manager_grid_subject:
                doSetCurrentItem(1);
                break;
            default:
                doSetCurrentItem(0);
                break;

        }
    }

    private void doSetCurrentItem(int index) {
        if (pager != null) {
            pager.setCurrentItem(index);
        }
    }

    private void doFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            index = intent.getIntExtra(INTENT_INDEX, 0);
        }
    }

    private void initView() {
        img_search = findView(R.id.img_btn_title_activity_bar_search);
        img_search.setVisibility(View.VISIBLE);
        img_search.setOnClickListener(this);
//        fragments.add(new InterestListFragment());
//        fragments.add(new SubscribeListFragment(this));
//        fragments.add(new RssSubscribeListFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_title_activity_bar_search:// "搜索"
                IntentUtil.openSearchActivity(this);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                sysp.putBoolean(SYSharedPreferences.KEY_UPDATE, true);
                break;
            default:
                break;
        }
    }

    private static final int[] ICONS = new int[]{
            R.drawable.suber_group_insteres,
            R.drawable.suber_group_subject,
            R.drawable.suber_group_rss
    };
    private static final String[] TITLES = new String[]{
            "兴趣", "主题", "报刊"
    };

    public class TabPagerAdapter extends FragmentPagerAdapter {
        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }


        public int getIconResId(int index) {
            return ICONS[index];
        }

        @Override
        public int getCount() {
            if (fragments != null)
                return fragments.size();
            return 0;
        }
    }

}