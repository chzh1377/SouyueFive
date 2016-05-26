package com.zhongsou.souyue.circle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.im.db.module.Group;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleIMGroupListAdapter;
import com.zhongsou.souyue.circle.model.CircleIMGroup;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.IMApi;
import com.zhongsou.souyue.im.util.IMIntentUtil;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleIMGroupListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

/**
 * Created by bob zhou on 15-1-9.
 * <p/>
 * 兴趣圈讨论群列表
 */
public class CircleIMGroupActivity extends BaseActivity{

    private String srp_id;

    private String interest_name;

    private ListView listView;

    private CircleIMGroupListAdapter  adapter;

    private View footerView;

    private View footerProgressBar;

    private TextView footerRefreshTv;

    private boolean isLoading;

    private boolean canLoadMore = true;

    private boolean isFirstLoad = true;

    private int lastItem;



    private ProgressBarHelper progress;

    private boolean showToast = true;

    static final int PSIZE = 20;

    private ImserviceHelp service = ImserviceHelp.getInstance();

    public static final String ACTION_UPDATE_CIRCLE_IMGROUP_LIST = "ACTION_UPDATE_CIRCLE_IMGROUP_LIST";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_im_group);
        initData();
        initView();
        bindListener();
    }


    public void initView(){
        progress = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
//        aq.id(R.id.btn_option).gone();
        findViewById(R.id.btn_option).setVisibility(View.GONE);
//        listView = aq.id(R.id.group_list_view).getListView();
        listView = findView(R.id.group_list_view);
        adapter.setListView(listView);
        footerView = LayoutInflater.from(this).inflate(R.layout.ent_refresh_footer, null);
        footerView.setBackgroundResource(R.drawable.circle_list_item_selector);
        footerProgressBar = footerView.findViewById(R.id.pull_to_refresh_progress);
        footerRefreshTv = (TextView)footerView.findViewById(R.id.pull_to_refresh_text);
        listView.addFooterView(footerView);
        listView.setAdapter(adapter);
//        aq.id(R.id.activity_bar_title).text(interest_name.concat("讨论群"));
        ((TextView)(findView(R.id.activity_bar_title))).setText(interest_name.concat("讨论群"));
    }



    public void initData(){
        Intent intent = getIntent();
        srp_id = intent.getStringExtra("srp_id");
        interest_name = intent.getStringExtra("interest_name");
        adapter = new CircleIMGroupListAdapter(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_CIRCLE_IMGROUP_LIST);
        registerReceiver(myBroadcastReceiver,filter);

        CircleIMGroupListRequest.send(HttpCommon.CIRCLE_IMGROUP_LIST_REQUESTID,this,srp_id, SYUserManager.getInstance().getToken(), 0, PSIZE);
//        http.getInterestIMGroupList(srp_id, SYUserManager.getInstance().getToken(), 0, PSIZE);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.CIRCLE_IMGROUP_LIST_REQUESTID:
                getInterestIMGroupListSuccess(request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.CIRCLE_IMGROUP_LIST_REQUESTID:
                progress.showNetError();
                break;
        }
    }

    public void getInterestIMGroupListSuccess(HttpJsonResponse res){
        progress.goneLoading();
        JsonArray json = res.getBodyArray();
        List<CircleIMGroup> list =  new Gson().fromJson(json, new TypeToken<List<CircleIMGroup>>() {}.getType());
        if(CollectionUtils.isEmpty(list)){
            if(isFirstLoad){
                progress.showNoData();    //没有数据
            }else {
                if(showToast){
                    Toast.makeText(CircleIMGroupActivity.this, "已加载全部", Toast.LENGTH_SHORT).show();            //加载完毕
                }
//                aq.id(footerView).gone();
                footerView.setVisibility(View.GONE);
            }
            canLoadMore = false;
            return;
        }

        if(isFirstLoad){
            adapter.setList(list);
            if(list.size() < PSIZE){
                showToast = false;
            }
            isFirstLoad = false;
        }else {
            isLoading = false;
            adapter.addList(list);
        }

        if(list.size() < PSIZE){
//            aq.id(footerView).gone();
            footerView.setVisibility(View.GONE);
        }

    }


    public void bindListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CircleIMGroup group = (CircleIMGroup) adapter.getItem(position);
                if (IntentUtil.isLogin()) {
                    if (group != null) {
                        Group mGroup =  service.db_updateGroup(group.getGroup_id());
                        if(mGroup==null){
                            IMApi.gotoGroupInfoActivity(CircleIMGroupActivity.this, group.getGroup_id(),interest_name);
                        }else{
                            IMIntentUtil.gotoGroupChatActivity(CircleIMGroupActivity.this, mGroup, 0);
                        }
                    }
                } else {
                    IntentUtil.goLogin(CircleIMGroupActivity.this, true);
                }
            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(lastItem == adapter.getCount() && scrollState == SCROLL_STATE_IDLE && !isLoading && canLoadMore){
                    if(CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())){
                        CircleIMGroupListRequest.send(HttpCommon.CIRCLE_IMGROUP_LIST_REQUESTID,CircleIMGroupActivity.this,srp_id, SYUserManager.getInstance().getToken(), adapter.getLastSortNum(), PSIZE);
//                        http.getInterestIMGroupList(srp_id, SYUserManager.getInstance().getToken(), adapter.getLastSortNum(), PSIZE);
                        isLoading = true;
                        footerProgressBar.setVisibility(View.VISIBLE);
                        footerRefreshTv.setText("正在加载…");
                    }else {
                        Toast.makeText(CircleIMGroupActivity.this,"网络异常，请重试",Toast.LENGTH_SHORT).show();
                        footerProgressBar.setVisibility(View.GONE);
                        footerRefreshTv.setText("上拉加载更多");
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(listView.getFooterViewsCount() == 0){
                    lastItem = firstVisibleItem + visibleItemCount ;
                }else{
                    lastItem = firstVisibleItem + visibleItemCount - 1;
                }
            }
        });

        progress.setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
            @Override
            public void clickRefresh() {
//                http.getInterestIMGroupList(srp_id, SYUserManager.getInstance().getToken(), lastItem, PSIZE);
                CircleIMGroupListRequest.send(HttpCommon.CIRCLE_IMGROUP_LIST_REQUESTID,CircleIMGroupActivity.this,srp_id, SYUserManager.getInstance().getToken(), lastItem, PSIZE);
                isLoading = true;
            }
        });

    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if("getInterestIMGroupList".equals(methodName)){
//            progress.showNetError();
//        }
//
//    }


    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_UPDATE_CIRCLE_IMGROUP_LIST.equals(intent.getAction())) {
                long group_id = intent.getLongExtra("group_id", 0);
                adapter.updateRow(group_id);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }
}