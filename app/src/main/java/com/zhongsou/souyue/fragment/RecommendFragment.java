package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SearchResult;

/**
 * Fragment 精华区
 *
 * @author chefb@zhongsou.com
 */
@SuppressLint("ValidFragment")
public class RecommendFragment extends SRPFragment {
    public static final int layoutId = R.layout.srp_recommend;
    private View recommend_no_searchresult;

    public RecommendFragment(Context context, NavigationBar nav) {
        this(context, nav, null);
    }

    public RecommendFragment(Context context, NavigationBar nav,String type) {
    	super(context, nav,type);
    }
    
    public RecommendFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState
                    .getSerializable("nav");
        View view = View.inflate(activity, layoutId, null);
        recommend_no_searchresult = view
                .findViewById(R.id.recommend_no_searchresult);
        inits(view);
        return view;

    }

    @Override
    public void searchResultSuccess(SearchResult searchResult) {
        if (searchResult.items().size() == 0) {
            recommend_no_searchresult.setVisibility(View.VISIBLE);
        } else {
            recommend_no_searchresult.setVisibility(View.GONE);
        }
        super.searchResultSuccess(searchResult);
    }

    @Override
    public void searchResultToPullDownRefreshSuccess(SearchResult sr) {
        if (sr.items().size() == 0) {
            recommend_no_searchresult.setVisibility(View.VISIBLE);
        } else {
            recommend_no_searchresult.setVisibility(View.GONE);
        }
        super.searchResultToPullDownRefreshSuccess(sr);
    }
}
