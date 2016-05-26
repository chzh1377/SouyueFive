package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SearchAllActivity;
import com.zhongsou.souyue.adapter.SearchAdapter;
import com.zhongsou.souyue.circle.util.JSONUtils;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.common.utils.StringUtils;
import com.zhongsou.souyue.module.HotSearchInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.search.SearchHotRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IRequestCache;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 搜索页面 - 显示热搜榜和搜索历史纪录
 * @date 2016/4/20
 */
public class SearchShowFragment extends BaseFragment implements View.OnClickListener {

    private GridView gvHotSearch;
    private SearchAdapter hotAdapter;
    private int currentHotIndex = 0;  //  0 <= currentHotNum <= 4
    private int maxHotNum = 5;
    private List<HotSearchInfo> hotShowList;
    private List<HotSearchInfo> hotAllList;
    private ListView lvSearchHistory;
    private List<String> historyList;
    private SearchAdapter historySearchAdapter;
    private String etSearchHint;

    public SearchShowFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_show, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            notifyHistoryData();
        }
    }

    private void initView(View view) {
        TextView tvChange = (TextView) view.findViewById(R.id.hot_search_change);   //换一批
        lvSearchHistory = (ListView) view.findViewById(R.id.search_history_list);
        gvHotSearch = (GridView) view.findViewById(R.id.hot_search_grid);

        Button btnClean = new Button(getActivity());
        btnClean.setText(R.string.clear_search_history);
        btnClean.setTextColor(getResources().getColor(R.color.red_d64844));
        btnClean.setBackgroundColor(getResources().getColor(R.color.discover_bg));
        btnClean.setPadding(0, DeviceUtil.dip2px(getActivity(), 10), 0, DeviceUtil.dip2px(getActivity(), 20));
        lvSearchHistory.addFooterView(btnClean);

        lvSearchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchAllActivity.isFromUserInput = false;
                String text = historyList.get(position);
                ((SearchAllActivity) getActivity()).searchResult(text);
                ((SearchAllActivity) getActivity()).saveHistory(text);
            }
        });
        tvChange.setOnClickListener(this);
        btnClean.setOnClickListener(new View.OnClickListener() {    //清除历史纪录按钮
            @Override
            public void onClick(View v) {
                CommSharePreference.getInstance().putValue(
                        CommSharePreference.DEFAULT_USER,
                        "searchHistory",
                        "");
                historyList = new ArrayList<String>();
                notifyHistoryData();
            }
        });

        gvHotSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int realPosition = hotAdapter.changePosition(position);
                HotSearchInfo item = hotShowList.get(realPosition);
                IntentUtil.gotoSRP(getActivity(), item.getKeyword(), item.getSrpId(), item.getUrl(), null, null);
            }
        });
    }

    private void initData() {
        hotShowList = new ArrayList<>();
        getHotList();
        notifyHistoryData();
    }

    /**
     * 获得热搜榜
     */
    private void getHotList() {
        SearchHotRequest request = new SearchHotRequest(HttpCommon.SEARCH_HOT_REQUEST, this);
        request.setForceCache(false);
        String cachekey = request.getCacheKey();
        int cacheState = mMainHttp.getCacheState(cachekey);
        if (cacheState == IRequestCache.CACHE_STATE_IS_EXPIRE
                || cacheState == IRequestCache.CACHE_STATE_HAVE) {
            request.setForceCache(true);
            CMainHttp.getInstance().doRequest(request);
            request.setForceRefresh(true);
        }
        CMainHttp.getInstance().doRequest(request);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onHttpResponse(IRequest request) {
        int id = request.getmId();
        HttpJsonResponse response = request.getResponse();
        switch (id) {
            case HttpCommon.SEARCH_HOT_REQUEST:
                hotAllList = JSONUtils.fromJsonArray(
                        response.getBodyArray(),
                        new TypeToken<List<HotSearchInfo>>() {
                        }.getType());
                etSearchHint = getActivity().getResources().getString(R.string.always_search)
                        + hotAllList.get(0).getKeyword();
                notifyHotData();
                ((SearchAllActivity) getActivity()).setEditHint(null, etSearchHint);
//                ((SearchAllActivity) getActivity()).clearEditFocus();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.hot_search_change:    //换一批
                currentHotIndex = (++currentHotIndex % maxHotNum);
                notifyHotData();
                break;
        }
    }

    public void notifyHotData() {
        if (hotAllList == null) {
            return;
        }
        hotShowList = hotAllList.subList(6 * currentHotIndex, 6 * currentHotIndex + 6);
        hotAdapter = new SearchAdapter(getActivity(), hotShowList, R.layout.item_search_grid);
        gvHotSearch.setAdapter(hotAdapter);
        hotAdapter.notifyDataSetChanged();
    }

    public void notifyHistoryData() {
        getSearchHistory();
        historySearchAdapter = new SearchAdapter(getActivity(), historyList, R.layout.item_search_list);
        lvSearchHistory.setAdapter(historySearchAdapter);
        historySearchAdapter.notifyDataSetChanged();
        if (historyList.size() == 0) {
            lvSearchHistory.setVisibility(View.GONE);
        } else {
            lvSearchHistory.setVisibility(View.VISIBLE);
        }
    }

    private void getSearchHistory() {
        historyList = new ArrayList<>();
        String strHistory = CommSharePreference.getInstance()
                .getValue(CommSharePreference.DEFAULT_USER, "searchHistory", null);

        if (strHistory != null && !StringUtils.isEmpty(strHistory)) {
            String history[] = strHistory.split(",");
            for (int i = history.length - 1; i >= 0; i--) {
                String str = history[i];
                if (!historyList.contains(str)) {
                    historyList.add(str);
                }
            }
        }
    }

    public String getEtSearchHint() {
        return etSearchHint;
    }
}
