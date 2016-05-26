package com.zhongsou.souyue.circle.activity;

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
import com.zhongsou.souyue.circle.fragment.RecommendInfoFragment;
import com.zhongsou.souyue.circle.model.RecommendInfo;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlong on 14-4-30.
 */
public class CircleRecommendInfoActivity extends BaseActivity implements View.OnClickListener
    , RecommendInfoFragment.OnRecommendStateChangedListener{

    private TextView refuse, interest, enssence;

    // 当前推荐类型
    private int recommendType = 0;

    //推荐ID列表
    private long[] recommendIdList;
    private int showPosition = 0;
    private RecommendInfoAdapter adapter;
    private ViewPager pager;

    private Bundle stateResult = new Bundle();

    protected ProgressBarHelper progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity_recommend_info);
        initView();

        Intent intent = getIntent();
        if (intent != null) {
            recommendIdList = intent.getLongArrayExtra("recommendIdList");
            showPosition = intent.getIntExtra("showPosition", 0);
            recommendType = intent.getIntExtra("recommendType", CircleInboxActivity.RECOMMEND_TYPE_USER);
        }
        //加载界面
        View loading = findViewById(R.id.ll_data_loading);
        if(loading != null) {
            progress = new ProgressBarHelper(this, loading);
            progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                @Override
                public void clickRefresh() {
                    RecommendInfoFragment fragment = (RecommendInfoFragment) adapter.getItem(pager.getCurrentItem());
                    fragment.reLoad();
                }
            });

            if(!CMainHttp.getInstance().isNetworkAvailable(this)) {
                progress.showNetError();
            }
        }

        adapter = new RecommendInfoAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                RecommendInfoFragment fragment = (RecommendInfoFragment)adapter.getItem(i);
                fragment.initState();

                if (fragment.needLoading()){
                    showLoading();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        pager.setCurrentItem(showPosition);
        if (showPosition == 0) {
            RecommendInfoFragment fragment = (RecommendInfoFragment)adapter.getItem(0);
            fragment.initState();
        }
    }

    public void showLoading(){
        if (progress!=null && !progress.isLoading )
            progress.showLoading();
    }
    public void showNoDateError(){
        if (progress!=null)
            progress.showNoData();
    }
    public void showNetError(){
        if (progress!=null)
            progress.showNetError();
    }
    public void goneLoading(){
        if (progress!=null)
            progress.goneLoading();
    }

    private void initView(){
        refuse = (TextView) findViewById(R.id.tv_circle_recommend_refuse);
        interest = (TextView) findViewById(R.id.tv_circle_recommend_interest);
        enssence = (TextView) findViewById(R.id.tv_circle_recommend_essence);

        pager = (ViewPager) findViewById(R.id.vp_circle_recommend_content);

        refuse.setOnClickListener(this);
        interest.setOnClickListener(this);
        enssence.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void onBackPressClick(View view){
        onBackPressed();
    }

    private TextView currentOperationView = null;
    private static final int selected_background_color = Color.parseColor("#e0e0e0");
    private static final int selected_text_color = Color.parseColor("#63c2de");
    private static final int normal_background_color = Color.parseColor("#eeeeee");
    private static final int normal_text_color = Color.parseColor("#6c6c6c");

    @Override
    public void onClick(View v) {

        RecommendInfoFragment fragment = (RecommendInfoFragment) adapter.getItem(pager.getCurrentItem());
        int id = v.getId();
        switch (id) {
            case R.id.tv_circle_recommend_refuse:
                fragment.refuseRecommend();
                break;
            case R.id.tv_circle_recommend_interest:
                if(!fragment.toInterest())
                    SouYueToast.makeText(this, "已选入兴趣圈", SouYueToast.LENGTH_SHORT).show();
                break;
            case R.id.tv_circle_recommend_essence:
                if(!fragment.toEnssence())
                    SouYueToast.makeText(this, "已选入精华区", SouYueToast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setNormal(TextView view) {

        view.setBackgroundColor(normal_background_color);
        view.setTextColor(normal_text_color);
    }

    private void setSelect(TextView view) {
        view.setBackgroundColor(selected_background_color);
        view.setTextColor(selected_text_color);
    }
    @Override
    public void onRecommendStateChange(long recommend_id, int state) {

        stateResult.putInt(recommend_id+"", state);

        setNormal(refuse);
        setNormal(interest);
        setNormal(enssence);

        switch (state) {
            case RecommendInfo.RECOMMEND_STATE_UNERCOMMEND:
                refuse.setText("拒绝");
                interest.setText("选入圈吧");
                enssence.setText("选入精华区");
                break;
            case RecommendInfo.RECOMMEND_STATE_ENSSENCE:
                refuse.setText("拒绝");
                interest.setText("选入圈吧");
                enssence.setText("已选入精华区");
                setSelect(enssence);
                break;
            case RecommendInfo.RECOMMEND_STATE_INTEREST:
                refuse.setText("拒绝");
                interest.setText("已选入圈吧");
                enssence.setText("选入精华区");
                setSelect(interest);
                break;
            case RecommendInfo.RECOMMEND_STATE_REFUSED:
                refuse.setText("已拒绝");
                interest.setText("选入圈吧");
                enssence.setText("选入精华区");
                setSelect(refuse);
                break;
        }

    }

    class RecommendInfoAdapter extends FragmentPagerAdapter {

        private List<RecommendInfoFragment> fragments;

        public RecommendInfoAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<RecommendInfoFragment>();
            RecommendInfoFragment fragment = null;
            for (int i=0; i<recommendIdList.length; i++) {
                fragment = new RecommendInfoFragment(recommendIdList[i], recommendType, CircleRecommendInfoActivity.this);
                fragments.add(fragment);
            }
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return recommendIdList.length;
        }
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtras(stateResult);
        result.putExtra("recommendType", recommendType);
        setResult(RESULT_OK, result);

        this.finish();
    }


}