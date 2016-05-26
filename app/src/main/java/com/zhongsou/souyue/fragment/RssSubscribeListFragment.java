package com.zhongsou.souyue.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.NewsSubscribeAdapter;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.db.SuberDao;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.dialog.SubscribeDialog;
import com.zhongsou.souyue.module.RssCate;
import com.zhongsou.souyue.module.SuberedItemInfo;
import com.zhongsou.souyue.module.SubscribeBack;
import com.zhongsou.souyue.module.SubscribeItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.sub.SubAddReq;
import com.zhongsou.souyue.net.sub.SubRssListReq;
import com.zhongsou.souyue.net.sub.SubRssListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅大全 - 报刊 Fragment
 * Created by wlong on 14-5-14.
 */
public class RssSubscribeListFragment extends SubscribeListBaseFragment
        implements IVolleyResponse {
    private Activity activity;
    private ListView newsListView, lv_category_list;
    private NewsSubscribeAdapter newsSubscribeAdapter;
    private SubscribeItem operateItem;
    protected static final int RESULT_OK = -1;
    public ArrayList<String> idDelete;
    public ArrayList<String> rssIdAdd;
    private boolean hasDatas;
    public List<RssCate> cateTrees = new ArrayList<RssCate>();
    private RssCategoryAdapter rssCategoryAdapter;
    private int currentPos = 0;
    private long groupId;
    public boolean rssFlag;
    // 本次操作是订阅还是取消订阅
    private boolean hasSubscribed;
    private int rssRightPosition;
    private List<SubscribeItem> subscribeNews;
    private SubscribeDialog subscribedialog;
    private SuberDao dao;
    private CMainHttp mainHttp;

    @Override
    public String getIndicatorTitle() {
        return MainApplication.getInstance().getString(
                R.string.manager_grid_rss);
    }

    public RssSubscribeListFragment() {
        rssIdAdd = new ArrayList<String>();
        idDelete = new ArrayList<String>();
        mainHttp = CMainHttp.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        SYSharedPreferences spys = SYSharedPreferences.getInstance();
        if (spys.getBoolean(SYSharedPreferences.KEY_UPDATE, false)) {
            sendRightRequest();
            spys.remove(SYSharedPreferences.KEY_UPDATE);
            spys.putBoolean(SYSharedPreferences.KEY_UPDATE_CIRCLE, true);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendRequest();
        dao = new SuberDaoImp();
        pbHelp.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {

            @Override
            public void clickRefresh() {
                sendRequest();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View secondview = View.inflate(activity,
                R.layout.news_source_subscribe, null);
        lv_category_list = (ListView) secondview
                .findViewById(R.id.lv_category_list);
        newsListView = (ListView) secondview
                .findViewById(R.id.lv_newsource_subscribe);
        subscribedialog = new SubscribeDialog(activity);
        rssCategoryAdapter = new RssCategoryAdapter();
        lv_category_list.setAdapter(rssCategoryAdapter);
        newsSubscribeAdapter = new NewsSubscribeAdapter(activity);
        newsListView.setAdapter(newsSubscribeAdapter);
        lv_category_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (currentPos == position) {
                    return;
                }
                currentPos = position;
                rssCategoryAdapter.setSelectedPosition(position);
                newsSubscribeAdapter.clearDatas();
                groupId = cateTrees.get(position).id();
                sendRightRequest();
                currentPos = position;
                rssCategoryAdapter.notifyDataSetChanged();

            }
        });

        newsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                rssRightPosition = position;
                operateItem = subscribeNews.get(position);
                newsSubscribeAdapter.changeState(position, rssIdAdd, idDelete);
                if (issaveNews()) {
                    saveNews();
                }
            }
        });
        return secondview;
    }


    public void rssCateListSuccess(List<RssCate> rssCates) {
        hasDatas = true;
        this.cateTrees = rssCates;
        if (cateTrees != null && cateTrees.size() > 0) {
            groupId = cateTrees.get(0).id();// 默认取第一条
            sendRightRequest();
            rssCategoryAdapter.notifyDataSetChanged();
            pbHelp.goneLoading();
        } else {
            pbHelp.goneLoading();
            pbHelp.showNoData();
        }

    }

    public void rssListSuccess(List<SubscribeItem> subscribeNews2) {
        try {
            subscribeNews = subscribeNews2;
            newsSubscribeAdapter.setDatas(subscribeNews);
            newsSubscribeAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void subscribeAddRssSuccess(SubscribeBack back) {
        if (hasSubscribed) {
            UmengStatisticUtil.onEvent(getActivity(), UmengStatisticEvent.SUBSCRIBE_ADD_ALL_CLICK);
            subscribedialog.subscribe();
            subscribeNews.get(rssRightPosition).subscribeId_$eq(
                    back.id().get(0));
            insertOne(back);
        } else {
            subscribedialog.unsubscribe();
            deleteOne();
        }
        SYSharedPreferences.getInstance().putBoolean(
                SYSharedPreferences.KEY_UPDATE, true);
        boolean subscribe = subscribeNews.get(rssRightPosition).hasSubscribe();
        subscribeNews.get(rssRightPosition).hasSubscribe_$eq(!subscribe);
        newsSubscribeAdapter.setDatas(subscribeNews);
        newsSubscribeAdapter.notifyDataSetChanged();
    }


    private void insertOne(SubscribeBack back) {
        SuberedItemInfo info = new SuberedItemInfo();
        info.setId(operateItem.id());//存rssId
        info.setTitle(operateItem.keyword());
        info.setCategory("rss");
        info.setImage(operateItem.image()); //图片地址没给
        info.setSrpId(back.id().get(0) + "");
        info.setKeyword(operateItem.keyword());
        info.setType("0");
        dao.addOne(info);
    }

    private void deleteOne() {
        SuberedItemInfo info = new SuberedItemInfo();
        info.setSrpId(operateItem.srpId());
        dao.clearOne(info);
    }

    public void sendRequest() {
        SubRssListRequest request = new SubRssListRequest(HttpCommon.SUB_RSSCATE_REQUEST, this);

        mainHttp.doRequest(request);
    }

    public void sendRightRequest() {
        String token = SYUserManager.getInstance().getToken();

        SubRssListReq req = new SubRssListReq(HttpCommon.SUB_RSSLIST2_REQUEST, this);
        req.addParams(groupId);
        mainHttp.doRequest(req);

    }

    public boolean hasDatas() {
        return hasDatas;
    }

    // 保存当前新闻源订阅的和没订阅的
    public boolean issaveNews() {
        if (rssIdAdd.size() > 0) {
            hasSubscribed = true;
            rssFlag = true;
        }
        if (idDelete.size() > 0) {
            hasSubscribed = false;
            rssFlag = true;
        }
        return rssFlag;
    }

    public void saveNews() {
        if (rssFlag) {

            SubAddReq req = new SubAddReq(HttpCommon.SUB_ADD_REQUEST, this);
            req.addParameters(rssIdAdd, idDelete);
            mainHttp.doRequest(req);

            subscribedialog.show();
            subscribedialog.progress();
            SYSharedPreferences.getInstance().putBoolean(
                    SYSharedPreferences.KEY_UPDATE, true);
            rssIdAdd.clear();
            idDelete.clear();
            rssFlag = false;

        }
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        int id = _request.getmId();
        HttpJsonResponse response = (HttpJsonResponse) _request.getResponse();

        switch (id) {
            case HttpCommon.SUB_RSSCATE_REQUEST:

                hasDatas = true;
                this.cateTrees = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<RssCate>>() {
                }.getType());
                if (cateTrees != null && cateTrees.size() > 0) {
                    groupId = cateTrees.get(0).id();// 默认取第一条
                    sendRightRequest();
                    rssCategoryAdapter.notifyDataSetChanged();
                    pbHelp.goneLoading();
                } else {
                    pbHelp.goneLoading();
                    pbHelp.showNoData();
                }
                break;

            case HttpCommon.SUB_RSSLIST2_REQUEST:
                List<SubscribeItem> items = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<SubscribeItem>>() {
                }.getType());
                rssListSuccess(items);
                break;

            case HttpCommon.SUB_ADD_REQUEST:
                SubscribeBack back = new Gson().fromJson(response.getBody(), SubscribeBack.class);
                subscribeAddRssSuccess(back);
                break;

            default:
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        switch (_request.getmId()) {
            case HttpCommon.SUB_RSSCATE_REQUEST:
                pbHelp.showNetError();
                break;
            case HttpCommon.SUB_ADD_REQUEST:
                IHttpError error = _request.getVolleyError();

                if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR) {
                    int stateCode = error.getErrorCode();
                    if (stateCode <= 700) {
                        subscribedialog.dismiss();
                    } else {
                        subscribedialog.subscribefail();
                        SystemClock.sleep(1000);
                        subscribedialog.unsubscribe();
                    }
                }


                break;
        }
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }

    public class RssCategoryAdapter extends BaseAdapter {
        public int selectedPosition;

        @Override
        public int getCount() {
            return cateTrees.size();
        }

        @Override
        public Object getItem(int position) {
            return cateTrees.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(activity,
                        R.layout.subscribe_category_list, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.tv_left);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(StringUtils.truncate(cateTrees.get(position)
                    .name().trim(), StringUtils.LENGTH_12));
            if (selectedPosition == position) {
                holder.text.setBackgroundColor(Color.parseColor("#f8f8f8"));
                holder.text.setTextColor(Color.parseColor("#da4644"));
            } else {
                holder.text.setBackgroundColor(Color.parseColor("#f2f2f2"));
                holder.text.setTextColor(Color.parseColor("#282828"));
            }

            return convertView;
        }

        private class ViewHolder {
            TextView text;
        }

        public void setSelectedPosition(int position) {
            if (position == -1 || position == cateTrees.size()) {
                selectedPosition = 0;
            } else {
                selectedPosition = position;
            }
        }
    }
}
