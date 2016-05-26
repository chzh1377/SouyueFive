package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.RecommendSubTab;
import com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zyw on 2015/12/21.
 * 订阅弹窗的pager
 */
public class SubRecommendDlgPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<RecommendSubTab> mRecommendSubTabs;


    public SubRecommendDlgPagerAdapter(Context context, List<RecommendSubTab> recommends) {
        this.mContext = context;
        mRecommendSubTabs = recommends;
    }

    @Override
    public int getCount() {
        return mRecommendSubTabs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ListView pagerPage = (ListView) View.inflate(mContext, R.layout.subrecommend_dlg_listview, null);
        initListView(pagerPage, position);
        container.addView(pagerPage);
        return pagerPage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 初始化listview
     *
     * @param listviewPage
     * @param position
     */
    private void initListView(ListView listviewPage, final int position) {
        RecommendSubTab recommendSubTab = mRecommendSubTabs.get(position);
        SubrecommendDlgListAdapter adapter = new SubrecommendDlgListAdapter(mContext, recommendSubTab);
        listviewPage.setAdapter(adapter);
        listviewPage.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean mLastMaskState = true; // 遮罩默认是显示状态的,上次遮罩的状态
            private boolean mScrollToLast; // 是否滑动到了最后一个
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mScrollToLast) {
                    if (mLastMaskState) {
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(SubRecommendDialog.ACTION_REMOVE_LIST_MASK));
                        Log.e("onScrollStateChanged", "移除遮罩");
                        mMaskState.put(position,false);
                        mLastMaskState = false;
                    }
                } else {
                    if (!mLastMaskState) {
                        Log.e("onScrollStateChanged", "显示遮罩");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(SubRecommendDialog.ACTION_SHOW_LIST_MASK));
                        mLastMaskState = true;
                        mMaskState.put(position,true);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mScrollToLast = (firstVisibleItem + visibleItemCount) == totalItemCount;

            }
        });
        listviewPage.setCameraDistance(20);
        listviewPage.setDivider(null);
        listviewPage.setCacheColorHint(Color.TRANSPARENT);
        listviewPage.setSelector(R.drawable.bg_selector_subrecommend_item);
        listviewPage.setOnItemClickListener(adapter);
        listviewPage.setDividerHeight(0);
    }

    /**
     * 保存当前的遮罩状态
     */
    private HashMap<Integer,Boolean> mMaskState = new HashMap<Integer, Boolean>();

    public boolean getCurrentMaskState(int position){
        if(mMaskState.containsKey(position)){
            return mMaskState.get(position);
        }
        //默认遮罩是显示的
        return true;
    }

}
