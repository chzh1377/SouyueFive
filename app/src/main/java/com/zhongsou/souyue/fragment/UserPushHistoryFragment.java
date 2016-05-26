package com.zhongsou.souyue.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SRPActivity;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.adapter.MsgUserPushHistoryListAdapter;
import com.zhongsou.souyue.module.Notice;
import com.zhongsou.souyue.module.NoticeList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserNoticeList;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class UserPushHistoryFragment extends BaseFragment {

    //	protected FragmentMsgPushHistoryActivity activity;
    private TextView push_history_list_empty;
    Handler handler = new Handler() {
    };
    private String token;
    private ProgressBarHelper pbHelp;
    private View userLoadMoreView;
    private TextView userLoadmore_btn;
    private ListView push_history_list_user;
    private int userVisibleLast = 0;
    private int userVisibleCount;
    private MsgUserPushHistoryListAdapter userAdapter;
    private List<Notice> userNotices;
    private boolean userHasMore = false;
    private long userLastId;
    private boolean userDataLoadMore;
    private boolean userDataLoadError = false;
    private boolean userDataLoading = true;
    private boolean userIsHidden = false;

    public UserPushHistoryFragment() {
    }
//	public UserPushHistoryFragment(Activity context) {
//		this.activity = (FragmentMsgPushHistoryActivity) context;
//	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Huang", "onCreateView" + this);
        View userView = getUserView(inflater);
        init(inflater, userView);
        return userView;
    }

    protected void init(LayoutInflater inflater, View view) {
        token = SYUserManager.getInstance().getToken();
        // 用户更多
        userLoadMoreView = inflater.inflate(R.layout.load_more, null);
        userLoadmore_btn = (TextView) userLoadMoreView.findViewById(R.id.btn_load_more);

        push_history_list_user = (ListView) view.findViewById(R.id.lv_msgpush_history_list_user);
        push_history_list_empty = (TextView) view.findViewById(R.id.tv_msgpush_history_user);

        push_history_list_user.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == userAdapter.getCount()) {
                    return;
                }
                Notice notice = ((MsgUserPushHistoryListAdapter.ViewHolder) view.getTag()).notice;
                if (notice == null) {
                    return;
                }
                if (notice.pushType() == 2) {//跳srp页
                    Intent srpPageIntent = new Intent(getActivity(), SRPActivity.class);
                    srpPageIntent.putExtra("keyword", notice.keyword());
                    srpPageIntent.putExtra("srpId", "");
                    startActivity(srpPageIntent);
                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
//                } else if (notice.pushType() == 4) {//跳老虎机页面
//                    Intent intent = new Intent();
//                    intent.putExtra(TigerGameActivity.RECORD_ID_SLOT, notice.keyword());
//                    intent.setClass(getActivity(), TigerGameActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    getActivity().startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    //中间压srp页,跳详情页
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
                   
                }

            }
        });

        push_history_list_user.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int itemsLastIndex = userAdapter.getCount() - 1; //
                int lastIndex = itemsLastIndex + 1; //
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && userVisibleLast == lastIndex) {
                    if (userDataLoadMore) {
                        doUserUpdate();
                    }
                    userDataLoadMore = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                userVisibleCount = visibleItemCount;
                userVisibleLast = firstVisibleItem + visibleItemCount - 1; //
            }

        });
        push_history_list_user.addFooterView(userLoadMoreView);
        userNotices = new ArrayList<Notice>();
        userAdapter = new MsgUserPushHistoryListAdapter(getActivity(), userNotices);
        push_history_list_user.setAdapter(userAdapter);

        this.pbHelp = new ProgressBarHelper(getActivity(), view.findViewById(R.id.ll_data_loading));
        this.pbHelp.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                loadUserNoticeData();
            }
        });

        token = SYUserManager.getInstance().getToken();

        if (!TextUtils.isEmpty(token)) {
            this.pbHelp.showLoading();
            loadUserNoticeData();
        } else {
            SouYueToast.makeText(getActivity(), getResString(R.string.token_error), 0).show();
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.USER_NOTICE_LIST_REQUEST:
                NoticeList list = new NoticeList();
                list.hasMore_$eq(response.getHead().get("hasMore").getAsBoolean());
                list.items_$eq((List<Notice>) new Gson().fromJson(
                        response.getBodyArray(), new TypeToken<List<Notice>>() {
                        }.getType()));
                noticeUserListSuccess(list);
        }
    }

    /**
     * 用户推送历史列表回调方法noticeUserList
     */
    public void noticeUserListSuccess(NoticeList list) {
        this.pbHelp.goneLoading();
        userDataLoading = false;
        userDataLoadError = false;
        userDataLoadMore = true;
        if (userAdapter != null && userLastId == 0)
            userAdapter.clearNotices();
        userHasMore = false;
        push_history_list_user.removeFooterView(userLoadMoreView);
        List<Notice> notices = list.items();
        if (notices != null && notices.size() != 0) {
            push_history_list_user.setVisibility(View.VISIBLE);
            push_history_list_empty.setVisibility(View.GONE);
            userLastId = notices.get(notices.size() - 1).id();
            userNotices = notices;
            userHasMore = list.hasMore();
            for (Notice notice : notices) {
                userAdapter.addItem(notice);
            }
            push_history_list_user.setSelection(userVisibleLast - userVisibleCount + 1);
            userAdapter.notifyDataSetChanged();
        } else {
            push_history_list_user.setVisibility(View.INVISIBLE);
            push_history_list_empty.setText(getResString(R.string.msgpush_user_empty));
            push_history_list_empty.setVisibility(View.VISIBLE);
        }

        userLoadmore_btn.setText(getResString(R.string.high_pull_loadMore));

        if (userHasMore) {
            push_history_list_user.addFooterView(userLoadMoreView);
        } else {
            push_history_list_user.removeFooterView(userLoadMoreView);
        }
        Log.v("Huang", "userLastId:" + userLastId);
    }

    /**
     * 加载更多用户推荐信息
     */
    protected void doUserUpdate() {
        userLoadmore_btn.setText(getResString(R.string.more_loading));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUserNoticeData();
            }
        }, 2000);
    }

    /**
     * 加载用户推荐消息
     */
    private void loadUserNoticeData() {
        UserNoticeList list = new UserNoticeList(HttpCommon.USER_NOTICE_LIST_REQUEST,this);
        list.setParams(userLastId);
        mMainHttp.doRequest(list);
//        http.noticeUserList(token, userLastId);
    }

    private View getUserView(LayoutInflater inf) {
        View view = inf.inflate(R.layout.fragment_msgpush_history_user, null);
        return view;
    }

    //获取资源String
    public String getResString(int id) {
        return this.getResources().getString(id);
    }

    @Override
    public void onHttpError(IRequest request) {
        Log.v("Huang", "user-onHttpError:" + request.getmId());
        userDataLoadMore = true;
        userDataLoadError = true;
        userDataLoading = false;
        if (userIsHidden) {
            return;
        }
        this.pbHelp.showNetError();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        userIsHidden = hidden;
        if (!hidden) {
            if (userDataLoadError) {
                this.pbHelp.showNetError();
                return;
            }
            if (userDataLoading) {
                this.pbHelp.showLoading();
            } else {
                this.pbHelp.goneLoading();
            }
        }
    }
}
