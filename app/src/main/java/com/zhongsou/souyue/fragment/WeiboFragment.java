package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SendWeiboActivity;
import com.zhongsou.souyue.module.BoZhu;
import com.zhongsou.souyue.module.HotTopic;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.module.Weibo;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.utils.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 微博
 */
@SuppressLint("ValidFragment")
public class WeiboFragment extends SRPFragment implements OnClickListener {

    public static final int layoutId = R.layout.srp_weibo;

    public WeiboFragment(Context context, NavigationBar nav) {
        this(context, nav, null);
    }

    public WeiboFragment(Context context, NavigationBar nav, String type) {
        super(context, nav, type);
    }

    public WeiboFragment() {
    }

    public void setType(String type) {
        super.type = type;
    }

    public void setKeyWord(String keyWord) {
        super.keyWord = keyWord;
    }

    public void setSrpid(String srpId) {
        super.srpId = srpId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");

        View view = View.inflate(activity, layoutId, null);
        inits(view);
        return view;
    }

    @Override
    protected void inits(View srpItemView) {
        ImageButton btn_new = (ImageButton) srpItemView
                .findViewById(R.id.btn_new);
        btn_new.setOnClickListener(this);
        super.inits(srpItemView);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        SearchResult sr = request.getResponse();
        switch (request.getmId()) {
            case HttpCommon.SRP_LIST_REQUEST:
                searchResultSuccess(sr);
                break;
            case HttpCommon.SRP_LIST_REFRESH_REQUEST:
                searchResultToPullDownRefreshSuccess(sr);
                break;
            case HttpCommon.SRP_LIST_MORE_REQUEST:
                searchResultToLoadMoreSuccess(sr);
                break;
        }
    }

    @Override
    public void searchResultSuccess(SearchResult searchResult) {
        Log.e("weibofragment", "get data success");
        ArrayList<SearchResultItem> searchResultItems = createSearchResultItems(searchResult);
        hasDatas = true;
        adapter.setChannelTime(System.currentTimeMillis() + "");
        pbHelper.goneLoading();
        adapter.setHasMoreItems(searchResult.hasMore());
        adapter.addDatas(searchResultItems);

    }

    private ArrayList<SearchResultItem> createSearchResultItems(SearchResult searchResult) {
        Log.e("weibofragment", "createSearchResultItems success");
        ArrayList<SearchResultItem> searchResultItems = new ArrayList<SearchResultItem>();
        try {
            BoZhu boZhu = searchResult.boZhu();
            if (boZhu != null && boZhu.user() != null && !TextUtils.isEmpty(boZhu.user().name())) {
                boZhu.newWeibo_$eq(searchResult.newWeiBo());
                SearchResultItem boZhuItem = new SearchResultItem();
                boZhuItem.boZhu_$eq(boZhu);
                searchResultItems.add(boZhuItem);
            }
            List<HotTopic> hotTopics = searchResult.hotTopics();
            if (hotTopics != null && hotTopics.size() > 0) {
                SearchResultItem hotTopicsItem = new SearchResultItem();
                hotTopicsItem.hotTopics_$eq(hotTopics);
                searchResultItems.add(hotTopicsItem);
            }
            if (searchResult.weibo() != null)
                for (Weibo weibo : searchResult.weibo()) {
                    SearchResultItem item = new SearchResultItem();
                    item.weibo_$eq(weibo);
                    item.boZhu_$eq(null);
                    item.hotTopics_$eq(null);
                    searchResultItems.add(item);
                }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return searchResultItems;
    }

    public void searchResultToLoadMoreSuccess(final SearchResult sr) {
        Log.e("weibofragment", "searchResultToLoadMoreSuccess success");
        ArrayList<SearchResultItem> searchResultItems = createSearchResultItems(sr);
        adapter.setHasMoreItems(sr.hasMore());
        adapter.addMore(searchResultItems);
    }

    public void searchResultToPullDownRefreshSuccess(final SearchResult sr) {
        Log.e("weibofragment", "searchResultToPullDownRefreshSuccess success");
        customListView.onRefreshComplete();
//        adapter.setChannelTime(as.getTime().getTime() + "");
        adapter.setChannelTime(System.currentTimeMillis() + "");
        adapter.setHasMoreItems(sr.hasMore());
        adapter.clearDatas();
        ArrayList<SearchResultItem> searchResultItems = createSearchResultItems(sr);
        adapter.addDatas(searchResultItems);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_new) {
            SelfCreateItem sci = new SelfCreateItem();
            sci.keyword_$eq(getKeyword());
            sci.column_name_$eq(nav.title());
            sci.srpId_$eq(getSrpId());
            sci.md5_$eq(nav.md5());
            sci.column_type_$eq(ConstantsUtils.TYPE_WEIBO_SEARCH);
            Intent intent = new Intent();
            intent.putExtra(SendWeiboActivity.TAG, sci);
            intent.setClass(activity, SendWeiboActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }

    }
}
