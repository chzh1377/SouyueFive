//package com.zhongsou.souyue.fragment;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.InnerTabPageIndicator;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.adapter.HomeTitleAdapter;
//import com.zhongsou.souyue.adapter.SouYueRssAdapter;
//import com.zhongsou.souyue.module.GroupKeywordItem;
//import com.zhongsou.souyue.module.RssBean;
//import com.zhongsou.souyue.ui.CustomViewPager;
//import com.zhongsou.souyue.ui.ViewPagerWithTips.OnBeginListener;
//import com.zhongsou.souyue.ui.ViewPagerWithTips.OnEndListener;
//import com.zhongsou.souyue.utils.LocalBroadCastHelper;
//import com.zhongsou.souyue.utils.SYUserManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author : zoulu
// *         2014年5月27日
// *         上午10:05:44
// *         类说明 :首页Rss新闻源Fragment
// */
//public class SouYueRssFragment extends Fragment implements OnBeginListener, OnEndListener {
//    private View view;
//    public CustomViewPager customViewPager;
//    private InnerTabPageIndicator indicator;
//    private Http http;
//    private String url;
//    private boolean imgAble;
//    private SouYueRssAdapter adapter;
//    private HomeTitleAdapter titleAdapter;
//    private int pos;
//    private String posUrl;
//    private List<GroupKeywordItem> title;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        view = View.inflate(getActivity(), R.layout.souyuerssfragment, null);
//        initUI(view);
//        initData();
//        return view;
//    }
//
//    public void setUrl(String posUrl) {
//        this.posUrl = posUrl;
//    }
//
//    private void initUI(View view) {
//        customViewPager = (CustomViewPager) view.findViewById(R.id.contentView_page);
//        customViewPager.setOnEndListener(this);
//        customViewPager.setOnBeginListener(this);
//        indicator = (InnerTabPageIndicator) view.findViewById(R.id.innerTabPageIndicator);
//    }
//
//    private void initData() {
//        http = new Http(this);
//        url = getArguments().getString(RssFragment.URL);
//        imgAble = getArguments().getBoolean(RssFragment.IMGABLE, true);
//        pos = getArguments().getInt(RssFragment.POSITION);
//        getNavData(url);
//    }
//
//    private void getNavData(String url) {
//        http.getRssGroup(url, SYUserManager.getInstance().getToken());
//    }
//
//    public void getRssGroupSuccess(List<RssBean> beans, AjaxStatus status) {
//        if (beans.size() > 0) {
//            title = new ArrayList<GroupKeywordItem>();
//            for (int i = 0; i < beans.size(); i++) {
//                GroupKeywordItem gki = new GroupKeywordItem();
//                gki.url_$eq(beans.get(i).getUrl());
//                gki.title_$eq(beans.get(i).getKeyword());
//                title.add(gki);
//            }
//            adapter = new SouYueRssAdapter(getChildFragmentManager(), title, imgAble);
//            customViewPager.setAdapter(adapter);
//            titleAdapter = new HomeTitleAdapter(true, getActivity());
//            titleAdapter.addNavs(title);
//            indicator.setViewAdapter(titleAdapter);
//            if (posUrl != null) {
//                sendPositionToRSSFragment();
//            }
//            LocalBroadCastHelper.sendGoneLoading(getActivity());
//        }
//    }
//
//    private void sendPositionToRSSFragment() {
//        for (int i = 0; i < title.size(); i++) {
//            if (title.get(i).url().equals(posUrl)) {
//                customViewPager.setCurrentItem(i);
//            }
//        }
//    }
//
//    @Override
//    public void onEndListener() {
//        // TODO Auto-generated method stub
//        LocalBroadCastHelper.sendPositionToHome(pos, getActivity(), LocalBroadCastHelper.RIGHT);
//    }
//
//    @Override
//    public void onBeginListener() {
//        // TODO Auto-generated method stub
//        LocalBroadCastHelper.sendPositionToHome(pos, getActivity(), LocalBroadCastHelper.LEFT);
//    }
//}
