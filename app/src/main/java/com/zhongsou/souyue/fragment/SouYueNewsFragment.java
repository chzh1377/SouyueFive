package com.zhongsou.souyue.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.view.PagerSlidingTabStrip;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.module.GroupKeywordItem;
import com.zhongsou.souyue.module.NewsBean;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CircleCateRecommendReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

/**
 * @author : zoulu 2014年5月27日 上午10:01:57 类说明 :首页搜悦新闻Fragment
 */
public class SouYueNewsFragment extends BaseFragment implements ProgressBarHelper.ProgressBarClickListener,View.OnClickListener {
    private View view;
    public ViewPager customViewPager;
    private String sysUrl;
//    private Http http;
    private SouYueNewsAdapter adapter;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ImageButton btnBack;
    private ImageButton btnManger;
    private FragmentManager mFragmentManager;
    private List<NewsBean> mitems ;
    private String mChannelCategory;
    SYSharedPreferences sysp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mChannelCategory = getActivity().getIntent().getStringExtra("category");
        view = inflater.inflate(R.layout.souyuenewsfragment,container, false);
//        http = new Http(SouYueNewsFragment.this);
        sysUrl = UrlConfig.HOMP_PAGE_URL;
        sysp=SYSharedPreferences.getInstance();
        initUI(view);
        if (sysp!=null&&sysp.getBoolean(SYSharedPreferences.KEY_UPDATE_YAOWEN, false)) {
            sysp.remove(SYSharedPreferences.KEY_UPDATE_YAOWEN);
            getNavData(sysUrl);
        }else{
            initNavData(sysUrl); 
        }
        return view;
    }

    private void initUI(View view) {
        pbHelp = new ProgressBarHelper(getActivity(),view.findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);

        /*
         * update by zhangyanwei
         * 5.05 red UI
         */
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.circle_index_indicator);
        /*
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.pstrip_text_selected_color);
        pagerSlidingTabStrip.setIndicatorColorResource(android.R.color.transparent);
        pagerSlidingTabStrip.setTextColorResource(R.color.cricle_list_item_topic_text_color);
        pagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.space_16));
        pagerSlidingTabStrip.setDividerColor(0xFfffff);
        pagerSlidingTabStrip.setUnderlineHeight(0);*/

        //设置正常颜色按钮
        pagerSlidingTabStrip.setTextColorResource(R.color.pstrip_text__normal_color);
        //设置高亮颜色按钮
        pagerSlidingTabStrip.setIndicatorColorResource(R.color.pstrip_text_selected_color_red);
        //设置指示器(短下划线)的高度
//        pagerSlidingTabStrip.setIndicatorHeight(6);
        //不显示分割线
        pagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        //设置选中文本颜色
        pagerSlidingTabStrip.setTextSelectedColorResource(R.color.pstrip_text_selected_color_red);
        //设置字体大小
        pagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.space_14));
        //设置下划线高度
        pagerSlidingTabStrip.setUnderlineHeight(1);

        customViewPager = (ViewPager) view.findViewById(R.id.pager);
        btnBack = (ImageButton)view.findViewById(R.id.ib_left);
        btnBack.setOnClickListener(this);
        btnManger = (ImageButton)view.findViewById(R.id.manager_btn_edit);
        btnManger.setOnClickListener(this);
        if(adapter==null){
            mFragmentManager = getChildFragmentManager();
            adapter = new SouYueNewsAdapter(mFragmentManager, getActivity());
            customViewPager.setAdapter(adapter);
        }
    }

    private void initNavData(String url) {
        if (SYUserManager.getInstance().getUser() != null && url != null) {
//            http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);

            CircleCateRecommendReq req = new CircleCateRecommendReq(HttpCommon.CIRCLE_CATERECOMMEND_FORCECACHE_REQUEST,this);
            req.addParams(SYUserManager.getInstance().getUser().userId(), 0, false);
            req.setForceCache(true);
            mMainHttp.doRequest(req);

           // http.getNewsItem(url, SYUserManager.getInstance().getUser()
           //         .userId(), 0, false);

        }else{
            if (SYUserManager.getInstance().getUser() == null) {

//                Log.e(this.getClass().getName(), "注意-----------user is null!");
            }else  if (url == null){
//                Log.e(this.getClass().getName(),"注意-----------url is null!");
            }
        }
    }

    private void getNavData(String url) {
        if (SYUserManager.getInstance().getUser() != null && url != null) {
//            http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE);
            //http.getNewsItem(url, SYUserManager.getInstance().getUser().userId(), 0, true);

            CircleCateRecommendReq req = new CircleCateRecommendReq(HttpCommon.CIRCLE_CATERECOMMEND_REQUEST,this);
            req.addParams(SYUserManager.getInstance().getUser().userId(), 0, false);
            mMainHttp.doRequest(req);

        }else{
            if (SYUserManager.getInstance().getUser() == null) {
//                Log.e(this.getClass().getName(), "注意-----------user is null!");
            }else  if (url == null){
//                Log.e(this.getClass().getName(),"注意-----------url is null!");
            }
        }
    }

    public void getNewsItemSuccess(List<NewsBean> beans) {
        pbHelp.goneLoading();
        if ((beans == null || beans.size() == 0) && CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
            getNavData(sysUrl);
        }
        if (beans.size() > 0) {
            mitems=beans;
            adapter.notifyDataSetChanged();
            pagerSlidingTabStrip.setViewPager(customViewPager);
            pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.NEWS_CHANNEL_SELECTED);//Umeng
                    stopVideo();
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });

            setViewPagerIndex();
        }
    }
    public void stopVideo(){
        List<Fragment> fragment = mFragmentManager.getFragments();
        if (fragment!=null) {
            for (Fragment frag : fragment) {
                if (frag!=null&&frag instanceof  SystemRecommendFragmentV2) {
                    ( (SystemRecommendFragmentV2)frag).stopPlayVideo();
                }
            }
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.CIRCLE_CATERECOMMEND_REQUEST:
            case HttpCommon.CIRCLE_CATERECOMMEND_FORCECACHE_REQUEST:
                List<NewsBean> beans = new Gson().fromJson(response.getBodyArray(),new TypeToken<List<NewsBean>>(){}.getType());
                getNewsItemSuccess(beans);
                break;


        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()){
            case HttpCommon.CIRCLE_CATERECOMMEND_FORCECACHE_REQUEST:
                getNavData(sysUrl);
                break;
            default:
                if (pbHelp.isLoading) {
                    pbHelp.goneLoading();
                }
                pbHelp.showNetError();
        }

    }

    void setViewPagerIndex(){
        for(int i=0;i<mitems.size();i++){
            NewsBean item = mitems.get(i);
            if(item.getTitle().equals(mChannelCategory)){
                customViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void clickRefresh() {
        getNavData(sysUrl);
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if (pbHelp.isLoading) {
//            pbHelp.goneLoading();
//        }
//        pbHelp.showNetError();
//    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.manager_btn_edit:
                IntentUtil.startChannelManngerActivity(getActivity());
                UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.NEWS_CHANNEL_MANAGEMENGT);   //Umeng
                break;
            case R.id.ib_left:
                getActivity().finish();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragment = mFragmentManager.getFragments();
        if (fragment!=null) {
            for (Fragment frag : fragment) {
                if (frag!=null) {
                    frag.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        if(data == null){
            return;
        }
        //修复bug SYFPO-95 即要闻频道页面返回应该返回到原来所在的页面
//        customViewPager.setCurrentItem(0);
        if(data.getBooleanExtra("isChange",false)){
            getNavData(sysUrl);
        }
    }

    public class SouYueNewsAdapter extends FragmentStatePagerAdapter{
        public SouYueNewsAdapter(FragmentManager fm ,Context cx) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if(mitems !=null&& mitems.size()>0){
                GroupKeywordItem item = new GroupKeywordItem();
                NewsBean i = mitems.get(position);
                item.title_$eq(i.getTitle());
                item.url_$eq(i.getUrl());
               return SystemRecommendFragmentV2.newInstance(item);
            }
            return null;
        }
        @Override
        public int getCount() {
           if(mitems !=null){
               return mitems.size();
           }else{
               return 0;
           }
        }

        @Override
        public int getItemPosition(Object object) {
            return -2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mitems.get(position).getTitle();
        }
    }
}
