package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.tuita.sdk.PushService;
import com.tuita.sdk.TuitaIMManager;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.firstleader.FirstInGuideAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.fragment.firstleader.FirstLeaderPage;
import com.zhongsou.souyue.presenter.FirstInActPresenter;
import com.zhongsou.souyue.ui.UnScrollViewPager;
import com.zhongsou.souyue.utils.PushUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.ArrayList;


/**
 * Created by zyw on 2016/1/12.
 * 第一次启动的界面
 * first_in_activity_layout.xml
 * home_first_leader_page_two.xml
 * home_first_leader_page_three.xml
 */
public class FirstInActivity extends BaseActivity {

    FirstInActPresenter mPresenter;
    private FrameLayout            loadingLayout;
    private UnScrollViewPager      pager;
    private View                   btnBack;
    private FirstInGuideAdapter    mAdapter;

    private boolean isLoading;
    private boolean defaultPushState = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TuitaIMManager.mIsShowNfBar = true;
        defaultPushState = sysp.getBoolean(SYSharedPreferences.KEY_PUSHSWITCH,true);
        sysp.putBoolean(SYSharedPreferences.KEY_PUSHSWITCH, true);
        MobclickAgent.onError(this); //初始化友盟
        setContentView(R.layout.first_in_activity_layout);
        init();
    }
    public void restoreDefaultPushState(){
        sysp.putBoolean(SYSharedPreferences.KEY_PUSHSWITCH, defaultPushState);
    }

    private void init() {
        initView();
        initFragmentsAdapter();
        mPresenter = new FirstInActPresenter(this);
        pager = findView(R.id.viewpager);
        pager.setOffscreenPageLimit(0);
        pager.setAdapter(mAdapter);
        pager.setOnPageChangeListener(mPresenter.onPageChangeListener);
        btnBack.setOnClickListener(mPresenter.btnBackListener);
        mPresenter.init();
    }

    private void initFragmentsAdapter() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        //fragment
        FirstLeaderPage page1 = new FirstLeaderPage(R.layout.home_first_leader_page_one);
        FirstLeaderPage page2 = new FirstLeaderPage(R.layout.home_first_leader_page_two);
        FirstLeaderPage page3 = new FirstLeaderPage(R.layout.home_first_leader_page_three);
        fragments.add(page1);
        fragments.add(page2);
        fragments.add(page3);
        mAdapter = new FirstInGuideAdapter(getSupportFragmentManager(), fragments);
    }
    public FirstLeaderPage getCurrentPageFrag(){
        return (FirstLeaderPage) mAdapter.getItem(pager.getCurrentItem());
    }

    /**
     * 初始化加载中视图
     */
    private void initView() {
        btnBack = findView(R.id.imageButton_back);
        loadingLayout = findView(R.id.first_in_loading);
    }

    public int getCurrentPage() {
        return pager.getCurrentItem();
    }

    public void setLoading() {
        isLoading = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadingLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }
            }
        });
    }

    public void removeLoading() {
        isLoading = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadingLayout.setVisibility(View.GONE);
                } catch (Exception e) {

                }
            }
        });
    }

    public void setCurrentPage(final int page) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (page > -1 || page < 3) {
                    if (page == 0) {
                        btnBack.setVisibility(View.GONE);
                    } else {
                        btnBack.setVisibility(View.VISIBLE);
                    }
                    pager.setCurrentItem(page);
                }
            }
        });
    }

    public void setPageOne() {
        setCurrentPage(0);
    }

    public void setPageTwo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCurrentPage(1);
            }
        });
    }

    public void setPageThree() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setCurrentPage(2);
            }
        });
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !isLoading) {
            if (pager.getCurrentItem() == 0) {
                return super.onKeyDown(keyCode, event);
            }
            mPresenter.goBackPage();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        isFront = true;
        super.onResume();
        MobclickAgent.onResume(this);
        //再次启动
        mPresenter.onRestore();
//        JPushInterface.onResume(this);  //JPush

    }

    public boolean isFront() {
        return isFront;
    }

    private boolean isFront = true;

    @Override
    protected void onPause() {
        isFront = false;
        super.onPause();
        MobclickAgent.onPause(this);
        //记录刚才的状态
        mPresenter.onSaveState();
//        JPushInterface.onPause(this);  //JPush
    }
}