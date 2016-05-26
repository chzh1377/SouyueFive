package com.zhongsou.souyue.ui;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SubGroupActivity;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.adapter.baselistadapter.ListViewAdapter;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.presenter.SubGroupListViewPresenter;
import com.zhongsou.souyue.ui.pulltorefresh.CFootView;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * Created by zyw on 2016/3/26.
 */
public class SubGroupListView extends FrameLayout {
    public static final String TAG = SubGroupListView.class.getSimpleName();
    private final SubGroupListViewPresenter mPresent;
    private final SubGroupActivity          mContext;
    private       PullToRefreshListView     ptrListView;
    private       CFootView                 footerView;
    private       int                       mFootState;
    private       ListViewAdapter           adapter;
    private             String mCurrentTime       = "";
    public static final int    FOOT_STATE_LOADING = 0;
    public static final int    FOOT_STATE_DONE    = 1;
    private ProgressBarHelper pbHelper;
    private LinearLayout      llGroupNoData;
    private HomeListManager manager;

    public SubGroupListView(SubGroupActivity context, String groupId, String title, String image) {
        super(context);
        this.mContext = context;
        View.inflate(context, R.layout.subgroup_view_list, this);
        mPresent = new SubGroupListViewPresenter(this, groupId, title, image);

    }

    public void onCreate() {
        initView();
        mPresent.initData();
        adapter = new ListViewAdapter(mContext, null);
        manager = new HomeListManager(mContext);
        adapter.setManager(manager);
        manager.setView(adapter, ptrListView.getRefreshableView());
        ptrListView.setAdapter(adapter);
        ptrListView.setOnRefreshListener(mPresent.getOnlistpulllistener());
        ptrListView.setOnScrollListener(mPresent.getPullrefreshscolllistener());
        ptrListView.setOnItemClickListener(mPresent.getOnListItemClick());
        ptrListView.setOnTimeRefreshListener(mPresent.getPtrlistontimerefreshlistener());
        ptrListView.setOncompleteListener(mPresent.PtrCompleteRefreshListener);
    }
    public   HomeListManager getManager(){
        return manager;
    }

    public PullToRefreshListView getPtrListView() {
        return ptrListView;
    }

    public ListViewAdapter getAdapter() {
        return adapter;
    }

    public Activity getCtx() {
        return mContext;
    }

    private void initView() {
        pbHelper = new ProgressBarHelper(mContext, findViewById(R.id.ll_data_loading));
        pbHelper.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                mPresent.initData();
            }
        });
        ptrListView = (PullToRefreshListView) findViewById(R.id.sub_group_list);
        ptrListView.setCanPullDown(true);
        footerView = (CFootView) View.inflate(mContext, R.layout.list_refresh_footer, null);
        footerView.initView();
        llGroupNoData = (LinearLayout) findViewById(R.id.group_empty_data);
        llGroupNoData.setVisibility(GONE);
        llGroupNoData.findViewById(R.id.btn_group_nodata).setOnClickListener(mPresent);
    }

    public void onDestroy() {

    }

    public void setLoading() {
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    llGroupNoData.setVisibility(GONE);
                    pbHelper.showLoadingUI();
                } catch (Exception e) {

                }
            }
        });
    }


    public void setFootDone() {
        post(new Runnable() {
            @Override
            public void run() {
                mFootState = FOOT_STATE_DONE;
                if (ptrListView != null) {
                    ListView view = ptrListView.getRefreshableView();
                    if (view.getFooterViewsCount() > 0) {
                        view.removeFooterView(footerView);
                    }
                }
            }
        });
    }

    public void setFootLoading() {
        post(new Runnable() {
            @Override
            public void run() {
                mFootState = FOOT_STATE_LOADING;
                if (ptrListView.getRefreshableView().getFooterViewsCount() == 0) {
                    ptrListView.getRefreshableView().addFooterView(footerView);
                }
                if (ptrListView != null) {
                    footerView.setLoading();
                    footerView.setVisibility(View.VISIBLE);
                    ListView view = ptrListView.getRefreshableView();
                    if (view.getFooterViewsCount() == 0) {
                        view.addFooterView(footerView);
                    }
                }
            }
        });
    }

    public void setTimeLabel() {
        final String time = StringUtils
                .convertDate(mCurrentTime);
        post(new Runnable() {
            @Override
            public void run() {
                ptrListView.setTimeLabel(time);
            }
        });
    }

    public void startFresh() {
        mPresent.startLoading();
    }

    public void showPullNetError() {
        if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
            UIHelper.ToastMessage(mContext,
                    R.string.cricle_manage_networkerror);
            ptrListView.onRefreshComplete();
            return;
        }
        mCurrentTime = System.currentTimeMillis() + "";
    }


    public void removeLoading() {
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    pbHelper.goneLoadingUI();
                    llGroupNoData.setVisibility(GONE);
                } catch (Exception e) {
                }
            }
        });
    }

    public void setNoData() {
        pbHelper.showNoData();
        if (mContext.getPresenter().isEmptyGroup()) {
            llGroupNoData.setVisibility(VISIBLE);
        }else{
            llGroupNoData.setVisibility(GONE);
        }
    }

    public void setNetError() {
        pbHelper.showNetError();
    }

    public void setHeadDone() {
        ptrListView.onRefreshComplete();
    }

    public boolean isFooterDone() {
        return mFootState == FOOT_STATE_DONE;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onCreate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    public void setListNoData(boolean isTitleNoData) {
        if (adapter.getCount() == 0 && isTitleNoData) {
            llGroupNoData.setVisibility(VISIBLE);
        }else{
            llGroupNoData.setVisibility(GONE);
        }
    }


}
