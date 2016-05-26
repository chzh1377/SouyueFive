package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.adapter.MsgSysPushHistoryListAdapter;
import com.zhongsou.souyue.module.Notice;
import com.zhongsou.souyue.module.NoticeList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.other.NoticeListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class SysPushHistoryFragment extends Fragment implements IVolleyResponse {

    //	protected FragmentMsgPushHistoryActivity activity;
    private TextView push_history_list_empty;
    Handler handler = new Handler() {
    };
    private String token;
//    private Http http;
    private CMainHttp mainHttp;
    private ProgressBarHelper pbHelp;
    private View sysLoadMoreView;
    private TextView sysLoadmore_btn;
    private ListView push_history_list_system;
    private int sysVisibleLast = 0;
    private int sysVisibleCount;
    private MsgSysPushHistoryListAdapter sysAdapter;
    private List<Notice> sysNotices;
    private boolean sysHasMore = false;
    private long sysLastId = -1;
    private long sysNoticeMaxId = 0;
    private boolean sysDataLoadMore;
    private boolean sysDataLoadError = false;
    private boolean sysDataLoading = true;
    private boolean sysIsHidden = false;

    public SysPushHistoryFragment() {

    }
//	public SysPushHistoryFragment(Activity context) {
//		this.activity = (FragmentMsgPushHistoryActivity) context;
//	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Huang", "onCreateView" + this);
        View sysView = getSysView(inflater);
        init(inflater, sysView);
        return sysView;
    }

    protected void init(LayoutInflater inflater, View view) {
//        http = new Http(this);
        mainHttp = CMainHttp.getInstance();
        token = SYUserManager.getInstance().getToken();
        // 系统更多
        sysLoadMoreView = inflater.inflate(R.layout.load_more, null);
        sysLoadmore_btn = (TextView) sysLoadMoreView.findViewById(R.id.btn_load_more);

        push_history_list_system = (ListView) view.findViewById(R.id.lv_msgpush_history_list_system);
        push_history_list_empty = (TextView) view.findViewById(R.id.tv_msgpush_history_sys);

        push_history_list_system.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == sysAdapter.getCount()) {
                    return;
                }
                Notice notice = ((MsgSysPushHistoryListAdapter.ViewHolder) view.getTag()).notice;
                if (notice.pushType() == 2) {//跳srp页
                    Intent srpPageIntent = new Intent(getActivity(), SRPActivity.class);
                    srpPageIntent.putExtra("keyword", notice.keyword());
//					srpPageIntent.putExtra("srpId", "");
                    startActivity(srpPageIntent);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    SearchResultItem resultItem = new SearchResultItem();
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();

                    resultItem.title_$eq(notice.title());
                    resultItem.keyword_$eq(notice.keyword());
                    resultItem.pushId_$eq(notice.id());

                    bundle.putSerializable("searchResultItem", (Serializable) resultItem);
                    intent.putExtras(bundle);
                    intent.putExtra("from", "push");
                    switch (notice.IsGetContent()) {
                        case 0:
                            intent.setClass(getActivity(), WebSrcViewActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            break;
                        default:
//                            intent.setClass(getActivity(), ReadabilityActivity.class);
//                            intent.putExtra("gotoSRP", true);
                            IntentUtil.startskipDetailPage(getActivity(), resultItem);
                            break;
                    }
                    //中间压srp页,跳详情页
                    
                }

            }
        });

        push_history_list_system.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int itemsLastIndex = sysAdapter.getCount() - 1; //
                int lastIndex = itemsLastIndex + 1; //
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && sysVisibleLast == lastIndex) {
                    if (sysDataLoadMore) {
                        doSysUpdate();
                    }
                    sysDataLoadMore = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                sysVisibleCount = visibleItemCount;
                sysVisibleLast = firstVisibleItem + visibleItemCount - 1; //
            }

        });
        push_history_list_system.addFooterView(sysLoadMoreView);
        sysNotices = new ArrayList<Notice>();
        sysAdapter = new MsgSysPushHistoryListAdapter(getActivity(), sysNotices);
        push_history_list_system.setAdapter(sysAdapter);

        this.pbHelp = new ProgressBarHelper(getActivity(), view.findViewById(R.id.ll_data_loading));
        this.pbHelp.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                loadSysNoticeData();
            }
        });

        token = SYUserManager.getInstance().getToken();

        if (!TextUtils.isEmpty(token)) {
            loadSysNoticeData();
        } else {
            SouYueToast.makeText(getActivity(), getResString(R.string.token_error), 0).show();
        }
    }

//    /**
//     * 系统推送历史列表回调方法
//     */
//    public void noticeListSuccess(NoticeList list) {
//        this.pbHelp.goneLoading();
//        sysDataLoading = false;
//        sysDataLoadError = false;
//        sysDataLoadMore = true;
//        if (sysAdapter != null && sysLastId == 0)
//            sysAdapter.clearNotices();
//        sysHasMore = false;
//        push_history_list_system.removeFooterView(sysLoadMoreView);
//        List<Notice> notices = list.items();
//        if (notices != null && notices.size() != 0) {
//            push_history_list_system.setVisibility(View.VISIBLE);
//            push_history_list_empty.setVisibility(View.GONE);
//            sysLastId = notices.get(notices.size() - 1).id();
//            if (sysNoticeMaxId == 0) {
//                sysNoticeMaxId = notices.get(0).id();
//            }
//            sysNotices = notices;
//            sysHasMore = list.hasMore();
//            for (Notice notice : notices) {
//                sysAdapter.addItem(notice);
//            }
//            push_history_list_system.setSelection(sysVisibleLast - sysVisibleCount + 1);
//            sysAdapter.notifyDataSetChanged();
//        } else {
//            push_history_list_system.setVisibility(View.INVISIBLE);
//            push_history_list_empty.setText(getResString(R.string.msgPushHistoryActivity_msgpush_empty));
//            push_history_list_empty.setVisibility(View.VISIBLE);
//        }
//
//        sysLoadmore_btn.setText(getResString(R.string.high_pull_loadMore));
//
//        if (sysHasMore) {
//            push_history_list_system.addFooterView(sysLoadMoreView);
//        } else {
//            push_history_list_system.removeFooterView(sysLoadMoreView);
//        }
//        Log.v("Huang", "sysLastId:" + sysLastId);
//    }

    /**
     * 加载更多系统推荐信息
     */
    protected void doSysUpdate() {
        sysLoadmore_btn.setText(getResString(R.string.more_loading));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadSysNoticeData();
            }
        }, 2000);
    }

    /**
     * 加载系统推荐消息
     */
    private void loadSysNoticeData() {
//        http.noticeList(token, sysLastId);
        NoticeListRequest request = new NoticeListRequest(HttpCommon.NOTICELIST_REQUEST,this);
        request.setParams(token, sysLastId);
        mainHttp.doRequest(request);
    }

    private View getSysView(LayoutInflater inf) {
        View view = inf.inflate(R.layout.fragment_msgpush_history_sys, null);
        return view;
    }

    //获取资源String
    public String getResString(int id) {
        return this.getResources().getString(id);
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        Log.v("Huang", "system-onHttpError:" + methodName);
//        sysDataLoadMore = true;
//        sysDataLoadError = true;
//        sysDataLoading = false;
//        if (sysIsHidden) {
//            return;
//        }
//        this.pbHelp.showNetError();
//    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        sysIsHidden = hidden;
        if (!hidden) {
            if (sysDataLoadError) {
                this.pbHelp.showNetError();
                push_history_list_system.setVisibility(View.INVISIBLE);
                push_history_list_empty.setVisibility(View.INVISIBLE);
                return;
            }
            if (sysDataLoading) {
                this.pbHelp.showLoading();
            } else {
                this.pbHelp.goneLoading();
            }
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.NOTICELIST_REQUEST:
                NoticeList list = request.getResponse();
                this.pbHelp.goneLoading();
                sysDataLoading = false;
                sysDataLoadError = false;
                sysDataLoadMore = true;
                if (sysAdapter != null && sysLastId == 0)
                    sysAdapter.clearNotices();
                sysHasMore = false;
                push_history_list_system.removeFooterView(sysLoadMoreView);
                List<Notice> notices = list.items();
                if (notices != null && notices.size() != 0) {
                    push_history_list_system.setVisibility(View.VISIBLE);
                    push_history_list_empty.setVisibility(View.GONE);
                    sysLastId = notices.get(notices.size() - 1).id();
                    if (sysNoticeMaxId == 0) {
                        sysNoticeMaxId = notices.get(0).id();
                    }
                    sysNotices = notices;
                    sysHasMore = list.hasMore();
                    for (Notice notice : notices) {
                        sysAdapter.addItem(notice);
                    }
                    push_history_list_system.setSelection(sysVisibleLast - sysVisibleCount + 1);
                    sysAdapter.notifyDataSetChanged();
                } else {
                    push_history_list_system.setVisibility(View.INVISIBLE);
                    push_history_list_empty.setText(getResString(R.string.msgPushHistoryActivity_msgpush_empty));
                    push_history_list_empty.setVisibility(View.VISIBLE);
                }

                sysLoadmore_btn.setText(getResString(R.string.high_pull_loadMore));

                if (sysHasMore) {
                    push_history_list_system.addFooterView(sysLoadMoreView);
                } else {
                    push_history_list_system.removeFooterView(sysLoadMoreView);
                }
                Log.v("Huang", "sysLastId:" + sysLastId);
                break;
        }

    }

    @Override
    public void onHttpError(IRequest request) {
        sysDataLoadMore = true;
        sysDataLoadError = true;
        sysDataLoading = false;
        if (sysIsHidden) {
            return;
        }
        this.pbHelp.showNetError();
    }

    @Override
    public void onHttpStart(IRequest request) {

    }
}
