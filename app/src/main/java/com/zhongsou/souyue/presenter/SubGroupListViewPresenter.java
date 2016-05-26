package com.zhongsou.souyue.presenter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.homepage.HomeList;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.net.group.GroupListDataReq;
import com.zhongsou.souyue.net.home.HomeListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.SubGroupListView;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.utils.CVariableKVO;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ToastUtil;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyw on 2016/3/26.
 */
public class SubGroupListViewPresenter implements View.OnClickListener {
    public static final String TAG = SubGroupListViewPresenter.class.getSimpleName();
    private final String           mGroupId;
    private final String           mTitle;
    private final String           mImage;
    public        SubGroupListView mView;
    private             boolean mPushLoad              = true; // 指示是否处于上拉加载的状态，如果正在加载，则不会发出第二次网络请求。
    private             int     visibleLast            = 0;
    public static final String  INNER_GROUP_LIST_CACHE = "inner_group_list_cache";
    private final HomePageDBHelper dbHelper;
    private       boolean          hasMore;
    protected     CVariableKVO     mKVO;
    private       boolean          mLoadDataFromLocal;
    private       boolean          mIsInLoading;
    private final ImageLoader      imageLoader;

    public SubGroupListViewPresenter(SubGroupListView view, String groupId, String title, String image) {
        this.mView = view;
        this.mGroupId = groupId;
        mTitle = title;
        mImage = image;
        dbHelper = HomePageDBHelper.getInstance();
        imageLoader = ImageLoader.getInstance();
        mKVO = new CVariableKVO(2, new CVariableKVO.KVOCallback() {
            @Override
            public void doCallback() {
                if (mView.getPtrListView() != null && mView.getAdapter().getCount() > 0) {
                    mView.removeLoading();
                    mView.getPtrListView().startRefresh();
                } else {
                    mView.setLoading();
                    getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PULL, 0 + "", getFirstId(), true);
                }

            }
        });
        mKVO.reset(1);
    }

    public void startLoading(){
        mView.getPtrListView().startRefresh();
    }

    private GetListDataResp mListDataResp = new GetListDataResp();

    public void initData() {
        mView.setLoading();
        initCache();
    }

    private void initCache() {
        getCache(Utils.MAX_LONG + "", new DataCallBack<List<BaseListData>>() {
            @Override
            public void callback(List<BaseListData> items) {
                int l = items == null ? 0 : items.size();
                if (l == 0) {
                    //显示loading
                    mView.setLoading();
                } else {
                    mView.setFootDone();
                    hasMore = true;
                    mPushLoad = true;
                    mView.removeLoading();
                    mView.getAdapter().setData(items);
                    mView.getAdapter().notifyDataSetChanged();
//
                }
                mKVO.doDone();
                mKVO.reset(1);
            }
        });
    }


    private void getCache(String _lastId, DataCallBack callBack) {
        DBhelperAsyncNew asyncTask = new DBhelperAsyncNew(callBack);
        asyncTask.excute(_lastId);
    }

    private void addCache(List<BaseListData> datas) {
        int                  l      = datas.size();
        final List<HomeList> items  = new ArrayList<HomeList>(l);
        final String         userid = SYUserManager.getInstance().getUserId();
        if (userid == null) {
            return;
        }
        BaseListData it = null;
        for (int i = 0; i < l; i++) {
            it = datas.get(i);
            String dateid = it.getId() + "";
            String data = it.getJsonResource();
            //数据库数据唯一标识是球球id+球球category+数据id
            String id_type_time = mGroupId + "_" + INNER_GROUP_LIST_CACHE + "_" + dateid;
            HomeList item = new HomeList(id_type_time, mGroupId + "", INNER_GROUP_LIST_CACHE, dateid, data);
            items.add(item);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper.addData(userid, items);
            }
        }).start();
    }

    private void setDatas(List<BaseListData> toplist, List<BaseListData> focusList, List<BaseListData> newslist) {
        if (focusList != null && focusList.size() > 0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            toplist.add(0, bean);
        }
        mView.getAdapter().setData(newslist);
        mView.getAdapter().addFirst(toplist);
    }

    private void getHomeBallContentList(int _pullType, String lastID, String indexId, boolean refresh) {
        switch (_pullType) {
            case CMainHttp.HTTP_REQUEST_HOMELIST://开始进来
                GroupListDataReq.send(HttpCommon.HOME_GET_LIST, mListDataResp, mGroupId, lastID, indexId, refresh);
                break;
            case CMainHttp.HTTP_REQUEST_HOMELIST_PULL://下拉刷新
                //非增量的类型下拉刷新时的请求参数应该和第一次请求列表的参数一致，即index都为0
                GroupListDataReq.send(HttpCommon.HOME_LIST_PULL, mListDataResp, mGroupId, lastID, indexId, refresh);
                break;
            case CMainHttp.HTTP_REQUEST_HOMELIST_PUSH://上拉刷新
                if (CMainHttp.getInstance().isNetworkAvailable(mView.getCtx()) && !mLoadDataFromLocal) {
                    mView.setFootLoading();
                    GroupListDataReq.send(HttpCommon.HOME_LIST_PUSH, mListDataResp, mGroupId, lastID, indexId, refresh);
                } else {
                    mLoadDataFromLocal = true;
                    mView.setFootLoading();
                    getCache(lastID + "", new DataCallBack<List<BaseListData>>() {
                        @Override
                        public void callback(List<BaseListData> data) {
                            if (data.size() == 0) {
                                //显示loading
                                mView.setFootDone();
                            } else {
                                mView.getAdapter().addLast(data);
                            }
                            mPushLoad = true;
                        }
                    });

                }
                break;
        }

    }


    public String getLastId() {
        String             lastId = "";
        List<BaseListData> datas  = mView.getAdapter().getDatas();
        if (datas == null) {
            return "0";
        }
        int count = datas.size();
        if (count == 0) {
            return "0";
        }
        BaseListData data = datas.get(datas.size() - 1);
        lastId = data.getId() + "";
        return lastId;
    }

    private OnListPullListener           onlistpulllistener           = new OnListPullListener();
    private PullrefreshScollListener     pullrefreshscolllistener     = new PullrefreshScollListener();
    private OnListItemClick              onListItemClick              = new OnListItemClick();
    private PTRListOnTimeRefreshListener ptrlistontimerefreshlistener = new PTRListOnTimeRefreshListener();


    public OnListPullListener getOnlistpulllistener() {
        return onlistpulllistener;
    }

    public PullrefreshScollListener getPullrefreshscolllistener() {
        return pullrefreshscolllistener;
    }

    public OnListItemClick getOnListItemClick() {
        return onListItemClick;
    }

    public PTRListOnTimeRefreshListener getPtrlistontimerefreshlistener() {
        return ptrlistontimerefreshlistener;
    }

    @Override
    public void onClick(View v) {
        IntentUtil.gotoSubGroupEdit(mView.getCtx(), mGroupId, mTitle, 2, mImage);
    }

    public class OnListPullListener implements PullToRefreshBase.OnRefreshListener<ListView> {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            mView.getManager().setmRefreshing(true);
            if (mView.getAdapter() == null) {
                return;
            }
            visibleLast = 0;
            if (!CMainHttp.getInstance().isNetworkAvailable(mView.getCtx())) {
                UIHelper.ToastMessage(mView.getCtx(),
                        R.string.cricle_manage_networkerror);
                mView.getPtrListView().onRefreshComplete();
                return;
            }
            if (!mIsInLoading) {
                mView.getPtrListView().setCanPullDown(false);
                mIsInLoading = true;
                String index = getFirstId();
                getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PULL, 0 + "", index, true);
            }
        }
    }

    public class PullrefreshScollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    imageLoader.resume();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    imageLoader.pause();
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    imageLoader.pause();
                    break;
            }
            if (mView.getAdapter() == null) {
                return;
            }
            int itemsLastIndex = mView.getAdapter().getCount();
            if (itemsLastIndex < 0) {
                return;
            }
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLast >= itemsLastIndex && mPushLoad) {
                String lastId = getLastId();
                if (hasMore) {
                    mPushLoad = false;
                    getHomeBallContentList(CMainHttp.HTTP_REQUEST_HOMELIST_PUSH, lastId, "" + 0, true);
                } else {
                    if (mView.getAdapter().getCount() != 0) {
                        mView.setFootDone();
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            visibleLast = firstVisibleItem + visibleItemCount;
        }
    }


    class OnListItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                return;
            }
            if (position > mView.getAdapter().getCount()) {
                if (mView.isFooterDone()) {
                    mView.startFresh();
                }
                return;
            }
            BaseListData item = (BaseListData) parent
                    .getItemAtPosition(position);
            if (item.getViewType() == BaseListData.view_Type_CLICK_REFRESH) {
                mView.startFresh();
                return;
            }
            BaseInvoke invo = item.getInvoke();
            HomePagerSkipUtils.skip(mView.getCtx(), invo);
        }
    }


    class PTRListOnTimeRefreshListener implements PullToRefreshBase.OnTimeRefreshListener {
        @Override
        public void onTimeRefresh() {
            if (mView.getAdapter() != null) {
                mView.setTimeLabel();
            }
        }
    }

    public PullToRefreshBase.OnCompleteRefreshListener PtrCompleteRefreshListener = new PullToRefreshBase.OnCompleteRefreshListener() {

        @Override
        public void onCompleteRefresh() {
            mView.getPtrListView().setCanPullDown(true);
            mView.getManager().setmRefreshing(false);
            mIsInLoading = false;
        }
    };

    public class GetListDataResp implements IVolleyResponse {
        @Override
        public void onHttpResponse(IRequest _request) {
            int id = _request.getmId();
            switch (id) {
                case HttpCommon.HOME_GET_LIST:
                case HttpCommon.HOME_LIST_PULL:
                case HttpCommon.HOME_LIST_PUSH:
                    doResponse(id, (List<Object>) _request.getResponse());
                    mView.getAdapter().notifyDataSetChanged();
                    break;
            }
        }

        @Override
        public void onHttpError(IRequest request) {
            ToastUtil.show(mView.getCtx(), "网络出错了，等会再试试把");
            switch (request.getmId()) {
                case HttpCommon.GROUP_LIST_REQ_COMEIN:
                    //进来
                    mView.setHeadDone();
                    mView.setNetError();
                    break;
                case HttpCommon.GROUP_LIST_REQ_PULL:
                    //上拉
                    mView.setFootDone();
                    break;
                case HttpCommon.GROUP_LIST_REQ_PUSH_DOWN:
                    //下拉刷新
//                    mView.setNetError();
                    mView.setHeadDone();
                    break;
            }
        }

        @Override
        public void onHttpStart(IRequest request) {

        }
    }

    private void doResponse(int id, List result) {
        List<BaseListData> topList   = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_TOPLIST);
        List<BaseListData> focusList = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_FOCUS);
        List<BaseListData> newsList  = (List<BaseListData>) result.get(HomeListReq.HOME_DATA_INDEX_NEWSLIST);
        addCache(newsList);
        switch (id) {
            case HttpCommon.HOME_GET_LIST://这个类型只有srp页用到了
                setDatas(topList, focusList, newsList);
                if (mView.getAdapter().getCount() == 0) {
                    mView.setNoData();
                } else {
                    mView.removeLoading();
                }
                boolean hasm1 = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                if (hasm1) {
                    mPushLoad = true;
                    mLoadDataFromLocal = false;
                    hasMore = true;
                } else {
                    mPushLoad = false;
                    mView.setFootDone();
                }
                break;
            case HttpCommon.HOME_LIST_PULL:
                mView.getPtrListView().onRefreshComplete();
                boolean hasm = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                mView.setFootDone();
                if (hasm) {
                    setDatas(topList, focusList, newsList);
                    mPushLoad = true;
                    mLoadDataFromLocal = false;
                    hasMore = true;
                } else {
                    addFirst(topList, focusList, newsList, false);
                }
                if (mView.getAdapter().getCount() == 0) {
                    mView.setNoData();
                    break;
                } else {
                    mView.removeLoading();
                }
                break;
            case HttpCommon.HOME_LIST_PUSH:
                hasMore = (Boolean) result.get(HomeListReq.HOME_DATA_INDEX_HASMORE);
                if (newsList.size() > 0) {
                    mView.getAdapter().addLast(newsList);
                }
                if (!hasMore) {
                    mView.setFootDone();
                    mPushLoad = false;
                } else {
                    mPushLoad = true;
                }
                break;
        }
    }

    /**
     * 向列表最前面加数据
     *
     * @param toplist
     * @param newslist
     */
    private void addFirst(List<BaseListData> toplist, List<BaseListData> focusList, List<BaseListData> newslist, boolean hasmore) {
        if (focusList != null && focusList.size() > 0) {
            CrouselItemBean bean = new CrouselItemBean();
            bean.setViewType(BaseListData.VIEW_TYPE_IMG_CAROUSEL);
            bean.setFocus(focusList);
            bean.setLocalTop(true);
            toplist.add(0, bean);
        }
        List<BaseListData> datas = mView.getAdapter().getDatas();
        if (datas != null) {
            if (!hasmore) {
                List<BaseListData> delete = new ArrayList<BaseListData>();
                for (BaseListData d : datas) {
                    if (d.isLocalTop()) {
                        delete.add(d);
                    }
                }
                for (BaseListData d : delete) {
                    datas.remove(d);
                }
                mView.getAdapter().setData(datas);
            } else {//下拉如果有更多就将当前页清除
                mView.getAdapter().clear();
            }
        }
        mView.getAdapter().addFirst(newslist);
        mView.getAdapter().addFirst(toplist);
    }


    public interface DataCallBack<T> {
        public void callback(T data);
    }


    class DBhelperAsyncNew<T> {
        DataCallBack mCallBack;
        ArrayList<BaseListData> mdata   = new ArrayList<BaseListData>();
        Handler                 handler = new Handler(Looper.getMainLooper());

        public DBhelperAsyncNew(DataCallBack<T> callBack) {
            mCallBack = callBack;
        }

        public void excute(final String... params) {
            new Thread() {
                @Override
                public void run() {
                    String userid = SYUserManager.getInstance().getUserId();
                    if (userid == null) {
                        Log.e(this.getClass().getName(), "userid is null");
                    } else {
                        try {
                            List<BaseListData> dbData = dbHelper.getData(userid, INNER_GROUP_LIST_CACHE, mGroupId + "", params[0], 20);
                            mdata.addAll(dbData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallBack.callback(mdata);
                        }
                    });
                }
            }.start();
        }
    }

    public String getFirstId() {
        String             lastId = "";
        List<BaseListData> datas  = mView.getAdapter().getDatas();
        if (datas == null) {
            return "0";
        }
        int count = datas.size();
        if (count == 0) {
            return "0";
        }
//        BaseListData data = datas.get(0);
        for (BaseListData data : datas) {//从前往后遍历,找到非置顶数据去更新
            if (!data.isLocalTop()) {
                lastId = data.getId() + "";
                break;
            }
        }
        return lastId;
    }
}
