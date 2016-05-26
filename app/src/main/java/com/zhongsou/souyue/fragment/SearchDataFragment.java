package com.zhongsou.souyue.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SearchAllActivity;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.module.listmodule.SearchItemData;
import com.zhongsou.souyue.net.news.CommonListReq;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.BallSpinLoadingView;
import com.zhongsou.souyue.ui.CommenListView;
import com.zhongsou.souyue.utils.ConstantsUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 搜索结果显示页面 - 列表
 * @date 2016/4/20
 */
public class SearchDataFragment extends BaseFragment {
    private String url = "search/search.content.groovy";
    private CommenListView commenListView;
    private UpdateSubStatusReceiver subStatusReceiver;  //更改订阅状态的广播接受者
    private List<BaseListData> extendsList;
    private LocalBroadcastManager localBroadcastManager;
    private BallSpinLoadingView loading;    //加载 圆形进度条

    public SearchDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_data, container, false);

        commenListView = new CommenListView(getActivity(), "");
        commenListView.setPullDown(false);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.searchdataView);
        layout.addView(commenListView);

        loading = new BallSpinLoadingView(getActivity());   //圆形进度条
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        loading.setLayoutParams(layoutParams);
        layout.addView(loading);

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpdateReceiver();
    }

    @Override
    public void onDestroy() {
        cancelReceiver();
        super.onDestroy();
    }

    public void doRequest(String word) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", word);
        commenListView.setOtherParams(map);
        commenListView.setDealNetCallBack(new CommenListView.DealNetCallBack() {
            @Override
            public List getAdapterData(IRequest request) {
                List<BaseListData> list = (List<BaseListData>) request.getResponse();
                List<BaseListData> newsList = (List<BaseListData>) list.get(CommonListReq.COMMENT_LIST_DATA_INDEX_NEWSLIST);
                extendsList = (List<BaseListData>) list.get(CommonListReq.COMMENT_LIST_DATA_INDEX_EXTENDS);
                if (extendsList != null && extendsList.size() > 0) {
//                    addNullItem(extendsList);
                    newsList.addAll(0, extendsList);
                }

                clearLoadingAnim();
                return newsList;
            }

            @Override
            public List getAdapterFirstData(IRequest request) {
                List<BaseListData> list = (List<BaseListData>) request.getResponse();
                List<BaseListData> topList = (List<BaseListData>) list.get(CommonListReq.COMMENT_LIST_DATA_INDEX_TOPLIST);
                List<BaseListData> focusList = (List<BaseListData>) list.get(CommonListReq.COMMENT_LIST_DATA_INDEX_FOCUS);

                if (focusList != null && focusList.size() > 0) {
                    CrouselItemBean bean = new CrouselItemBean();
                    bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
                    bean.setFocus(focusList);
                    topList.add(0, bean);
                }
                return topList;
            }

            @Override
            public boolean isHasMore(IRequest request) {
                List<Object> list = (List<Object>) request.getResponse();
                Boolean objects = (Boolean) list.get(CommonListReq.COMMENT_LIST_DATA_INDEX_HASMORE);
                return objects;
            }

        });
        commenListView.setNetUrl(url);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            ((SearchAllActivity) getActivity()).setAutoVisibility(View.GONE);
        }
    }

    private void addNullItem(List<BaseListData> list) {
        BaseListData data = new BaseListData();
        data.setViewType(BaseListData.view_Type_NULL);
        list.add(data);
    }

    public void setLoadingAnim() {
        loading.setVisibility(View.VISIBLE);
    }

    public void clearLoadingAnim() {
        loading.setVisibility(View.GONE);
    }

    /**
     * 回到第一条
     */
    public void gotoFirstItem() {
        if (commenListView != null) {
            commenListView.gotoFirstItem();
        }
    }

    /**
     * 订阅状态变更广播
     */
    private void setUpdateReceiver() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(ConstantsUtils.SUB_STATUS_ACTION);
        subStatusReceiver = new UpdateSubStatusReceiver();
        localBroadcastManager.registerReceiver(subStatusReceiver, inf);
    }

    private void cancelReceiver() {
        try {
            if (subStatusReceiver != null) {
                localBroadcastManager.unregisterReceiver(subStatusReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class UpdateSubStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantsUtils.SUB_STATUS_ACTION)) {
                int result = intent.getIntExtra("result", 0);
                String srpId = intent.getStringExtra("srp_id");

                for (BaseListData data : extendsList) {
                    if (data.getInvoke().getSrpId().equals(srpId)) {
                        if (result == 1) {
                            ((SearchItemData) data).setIsSubscribe(1);
                        } else if (result == 0) {
                            ((SearchItemData) data).setIsSubscribe(0);
                        }
                        break;
                    }
                }
                commenListView.notifyDataSetChanged();
            }
        }
    }
}
