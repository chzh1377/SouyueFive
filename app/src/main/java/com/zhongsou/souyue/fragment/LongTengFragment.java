//package com.zhongsou.souyue.fragment;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.adapter.AdAdapter;
//import com.zhongsou.souyue.module.AdList;
//import com.zhongsou.souyue.module.NavigationBar;
//import com.zhongsou.souyue.module.SearchResult;
//import com.zhongsou.souyue.net.srp.AdListRequest;
//import com.zhongsou.souyue.net.volley.HttpCommon;
//import com.zhongsou.souyue.ui.ProgressBarHelper;
//import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
//import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
//import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase.OnRefreshListener;
//import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
//
//@SuppressLint("ValidFragment")
//public class LongTengFragment extends SRPFragment {
//
//    public static final int layoutId = R.layout.longteng;
//    private PullToRefreshListView lv_ad;
//    private AdAdapter adapter;
//
//    public LongTengFragment(Context context, NavigationBar nav) {
//        super(context, nav);
//    }
//
//    public LongTengFragment() {
//    }
//
//    public void setKeyWord(String keyWord) {
//        super.keyWord = keyWord;
//    }
//
//    public void setSrpid(String srpId) {
//        super.srpId = srpId;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (savedInstanceState != null)
//            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");
//
//        View view = View.inflate(activity, layoutId, null);
//        view.findViewById(R.id.title_activity_bar_included).setVisibility(View.GONE);//隐藏内部title bar
//        inits(view);
//        return view;
//    }
//
//    public void setType(String type) {
//        super.type = type;
//    }
//
//    @Override
//    protected void inits(View srpItemView) {
//        lv_ad = (PullToRefreshListView) srpItemView.findViewById(R.id.lv_ad);
//        adapter = new AdAdapter(activity);
//        lv_ad.setAdapter(adapter);
//        createPBHelper(srpItemView.findViewById(R.id.ll_data_loading), nav);
//        lv_ad.setOnRefreshListener(new OnRefreshListener<ListView>() {
//
//            @Override
//            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                loadData();
//            }
//        });
//    }
//
//    public void adListSuccess(AdList list) {
//        lv_ad.onRefreshComplete();
//        if (list.list().size() > 0) {
//            adapter.setDatas(list.list());
//            adapter.notifyDataSetChanged();
//        } else {
//            // 默认布局
//        }
//        hasDatas = true;
//        pbHelper.goneLoading();
//    }
//
//    @Override
//    public void searchResultSuccess(SearchResult searchResult, AjaxStatus as) {
//        //如果龙腾是第一个widget 则会这个方法
//        loadData();
//    }
//
//    public void loadData() {
////		if (!hasDatas)
//        http.adList(getKeyword(), getSrpId(), 0);
//        AdListRequest request = new AdListRequest(HttpCommon.SRP_AD_LIST_REQUEST, this);
//    }
//
//    public void loadMore() {
//        int count = adapter.getCount();
//        if (count > 0 && count <= 10)
//            return;
//        else {
//            int page = count / 10;
//            http.adList(getKeyword(), getSrpId(), page);
//        }
//    }
//
//    public void createPBHelper(View view, final NavigationBar nav) {
//        pbHelper = new ProgressBarHelper(activity, view);
//        pbHelper.setProgressBarClickListener(new ProgressBarClickListener() {
//            public void clickRefresh() {
//                LongTengFragment.this.loadData();
//            }
//        });
//    }
//
//
//    @Override
//    public void onDestroyView() {
//        hasDatas = false;
//        super.onDestroyView();
//    }
//
//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        if (!hasDatas)
//            pbHelper.showNetError();
//    }
//}
