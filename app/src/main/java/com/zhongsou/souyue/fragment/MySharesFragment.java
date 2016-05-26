package com.zhongsou.souyue.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.SelfCreateDetailActivity;
import com.zhongsou.souyue.activity.SendBlogActivity;
import com.zhongsou.souyue.adapter.MySharesAdapter;
import com.zhongsou.souyue.module.NavigationBar;
import com.zhongsou.souyue.module.SelfCreate;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.srp.SrpListRequest;
import com.zhongsou.souyue.net.srp.SrpMyCreateRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.service.SelfCreateTask;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYUserManager;

@SuppressLint("ValidFragment")
public class MySharesFragment extends SRPFragment implements View.OnClickListener {

    public static final int layoutId = R.layout.my_shares;
    private View recommend_no_searchresult;
    private View loginBtn;
    private View loginLayout;
    private boolean goLogin;
    private ImageButton btn_join;
    private com.zhongsou.souyue.fragment.MySharesFragment.RefreshReceiver refreshRec;

    public MySharesFragment(Context context, NavigationBar nav) {
        this(context, nav, null);
    }

    public MySharesFragment(Context context, NavigationBar nav,String type) {
    	super(context, nav,type);
    }
    
    public MySharesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            this.nav = (NavigationBar) savedInstanceState.getSerializable("nav");
        View view = View.inflate(activity, layoutId, null);
        initStateView(view);
        inits(view);
        setListView();
        setReciever();
        setPbHelper();
        return view;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (hasDatas) {
            pbHelper.goneLoading();
        }

        if (!hasDatas) {
            loadData();
        }
    }

    /**
     * 设置 加载失败 点击重新加载的监听
     */
    private void setPbHelper() {
        this.pbHelper.setProgressBarClickListener(new ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
                if (customListView != null) {
                    customListView.startRefresh();
                }
            }
        });
    }

    private void setListView() {
        customListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // loadData(http, 0, nav.url());
                adapter.isRefresh = true;
                loadData();
            }
        });
        customListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putSerializable("selfCreateItem", (SelfCreateItem) adapter.getItem(position - 1));
                intent.setClass(getActivity(), SelfCreateDetailActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化“暂无作品” “我要参赛”“登录”三个view
     *
     * @param view
     */
    private void initStateView(View view) {
        recommend_no_searchresult = view.findViewById(R.id.recommend_no_searchresult);
        btn_join = (ImageButton) view.findViewById(R.id.btn_join);
        btn_join.setOnClickListener(this);
        loginBtn = view.findViewById(R.id.my_share_no_login);
        loginBtn.setOnClickListener(this);
        loginLayout = view.findViewById(R.id.my_share_no_login_layout);
    }

    private void setReciever() {
        IntentFilter inf = new IntentFilter();
        inf.addAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
        refreshRec = new RefreshReceiver();
        getActivity().registerReceiver(refreshRec, inf);
    }

    class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getBooleanExtra("ismodify", false)) {
                if (null != customListView) {
                    customListView.startRefresh();
                }
            }
        }
    }

/*    public void iWorksSuccess(SelfCreate searchResult, AjaxStatus as) {
        pbHelper.goneLoading();
        customListView.setVisibility(View.VISIBLE);
        hasDatas = true;
        if (searchResult.items().size() == 0 && ((MySharesAdapter) adapter).dbData.size() == 0) {
            recommend_no_searchresult.setVisibility(View.VISIBLE);
        } else {
            recommend_no_searchresult.setVisibility(View.GONE);
        }
        adapter.isRefresh = false;
        customListView.onRefreshComplete();
        adapter.setChannelTime(as.getTime().getTime() + "");
        adapter.setHasMoreItems(searchResult.hasMore());
        ((MySharesAdapter) adapter).clearNetData();
        ((MySharesAdapter) adapter).addRefData(searchResult.items());
        ((MySharesAdapter) adapter).hasMore = searchResult.hasMore();
        ((MySharesAdapter) adapter).isNetError = false;
    }*/

    public void iWorksSuccess(SelfCreate searchResult) {
        pbHelper.goneLoading();
        customListView.setVisibility(View.VISIBLE);
        hasDatas = true;
        if (searchResult.items().size() == 0 && ((MySharesAdapter) adapter).dbData.size() == 0) {
            recommend_no_searchresult.setVisibility(View.VISIBLE);
        } else {
            recommend_no_searchresult.setVisibility(View.GONE);
        }
        adapter.isRefresh = false;
        customListView.onRefreshComplete();
        adapter.setChannelTime(System.currentTimeMillis()+"");
        adapter.setHasMoreItems(searchResult.hasMore());
        ((MySharesAdapter) adapter).clearNetData();
        ((MySharesAdapter) adapter).addRefData(searchResult.items());
        ((MySharesAdapter) adapter).hasMore = searchResult.hasMore();
        ((MySharesAdapter) adapter).isNetError = false;
    }

    public void loadNetData() {
//        http.iWorks(nav.url(), SYUserManager.getInstance().getToken(), AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
        SrpMyCreateRequest myCreateRequest = new SrpMyCreateRequest(HttpCommon.SRP_LIST_MY_CREATE_REQUEST, MySharesFragment.this);
        myCreateRequest.addParams(nav.url(),  new Long(0), true);
  	  	mainHttp.doRequest(myCreateRequest);
    }

    /**
     * 本地读取数据成功
     *
     * @param scis
     */
    public void selfCreateListToDBSuccess(List<SelfCreateItem> scis) {
        if (!CMainHttp.getInstance().isNetworkAvailable(getActivity())) {
            recommend_no_searchresult.setVisibility(View.GONE);
            if (scis != null && scis.size() != 0) {
                hasDatas = true;
                customListView.setVisibility(View.VISIBLE);
                customListView.onRefreshComplete();
                pbHelper.goneLoading();
                ((MySharesAdapter) adapter).clearNetData();
                ((MySharesAdapter) adapter).clearDBdata();
                ((MySharesAdapter) adapter).addRefData(scis);
                ((MySharesAdapter) adapter).notifyDataSetChanged();
            } else {
                customListView.setVisibility(View.GONE);
            }

        } else {
            if (null != scis && scis.size() > 0)
                ((MySharesAdapter) adapter).addDBData(scis);
            else
                ((MySharesAdapter) adapter).clearDBdata();

            loadNetData();
        }
    }

    /**
     * 判断是否登录
     */
    @Override
    protected boolean checkToken() {
        super.checkToken();
        String type = SYUserManager.getInstance().getUserType();
        if (!SYUserManager.USER_ADMIN.equals(type)) {
            loginLayout.setVisibility(View.VISIBLE);
            btn_join.setVisibility(View.GONE);
            return false;
        }
        loginLayout.setVisibility(View.GONE);
        btn_join.setVisibility(View.VISIBLE);
        return true;
    }

/*    public void iWorksMoreSuccess(final SelfCreate sr, AjaxStatus status) {
        adapter.setHasMoreItems(sr.hasMore());
        ((MySharesAdapter) adapter).addMores(sr.items());
        ((MySharesAdapter) adapter).hasMore = sr.hasMore();
        ((MySharesAdapter) adapter).isNetError = false;
    }*/

    @Override
    public void onHttpResponse(IRequest request) {
		switch (request.getmId()) {
		case HttpCommon.SRP_LIST_MY_CREATE_REQUEST:
			SelfCreate sr = request.getResponse();
			iWorksSuccess(sr);
			break;
		case HttpCommon.SRP_LIST_MY_CREATE_MORE_REQUEST:
			SelfCreate moreSelfCreate = request.getResponse();
			iWorksMoreSuccess(moreSelfCreate);
			break;
		}
    }
    
    @Override
    public void onHttpError(IRequest request) {
         if (pbHelper.isLoading) {
             pbHelper.goneLoading();
         }
         if (adapter != null && adapter.getCount() == 0) {
             pbHelper.showNetError();
         }
         customListView.onRefreshComplete();
         ((MySharesAdapter) adapter).isNetError = true;
         adapter.isRefresh = false;
         adapter.notifyDataSetChanged();
         checkToken();
    }
    
    public void iWorksMoreSuccess(final SelfCreate sr) {
        adapter.setHasMoreItems(sr.hasMore());
        ((MySharesAdapter) adapter).addMores(sr.items());
        ((MySharesAdapter) adapter).hasMore = sr.hasMore();
        ((MySharesAdapter) adapter).isNetError = false;
    }

    /*@Override
    public void onHttpError(String methodName, AjaxStatus as) {
        super.onHttpError(methodName, as);
        if (pbHelper.isLoading) {
            pbHelper.goneLoading();
        }
        if (adapter != null && adapter.getCount() == 0) {
            pbHelper.showNetError();
        }
        customListView.onRefreshComplete();
        ((MySharesAdapter) adapter).isNetError = true;
        adapter.isRefresh = false;
        adapter.notifyDataSetChanged();
        checkToken();
    }*/

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.my_share_no_login:
                intent.setClass(getActivity(), LoginActivity.class);
                intent.putExtra("Only_Login", true);
                startActivity(intent);
                goLogin = true;
                break;
            case R.id.btn_join:
                intent.setClass(activity, SendBlogActivity.class);
                SelfCreateItem sci = new SelfCreateItem();
                sci.keyword_$eq(getKeyword());
                sci.column_name_$eq(nav.title());
                sci.srpId_$eq(getSrpId());
                sci.md5_$eq(nav.md5());
                sci.column_type_$eq(ConstantsUtils.TYPE_BLOG_SEARCH);
                intent.putExtra(SendBlogActivity.TAG, sci);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (goLogin) {
            btn_join.setVisibility(View.VISIBLE);
            loadData();
            goLogin = false;
        }
    }

    public void loadData() {
        if (checkToken()) {
            SelfCreateTask.getInstance().chaojifenxiang(this, ConstantsUtils.TYPE_BLOG_SEARCH + "", "超级分享大赛", "分享赛");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        adapter.clearDatas();
        if (refreshRec != null) getActivity().unregisterReceiver(refreshRec);
        super.onDestroyView();
    }

}
