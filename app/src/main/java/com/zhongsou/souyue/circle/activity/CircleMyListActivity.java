package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.CheeseDynamicAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.InterestBean;
import com.zhongsou.souyue.module.SubscribeItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserInterestListReq;
import com.zhongsou.souyue.net.personal.UserInterestSubReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.dynamicgrid.DynamicGridView;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;
/**
 * @description: 添加的注释，这个类在 从个人中心进入我订阅的兴趣圈/主题
 *
 * @auther: qubian 加注释
 * @data: 2015/12/13.
 */

public class CircleMyListActivity extends BaseActivity  {

    private TextView tvType;
    private DynamicGridView gridView;
    private CheeseDynamicAdapter adapter;
    private List<InterestBean> itemsInterest;
    private List<SubscribeItem> itemsSRP;
    protected ProgressBarHelper progress;
    private int type = 0;  //是兴趣圈还是主题  0主题，1兴趣圈
    private String token;
    private long looked_user_id;
    private CMainHttp mCMainHttp;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.circle_my_list);
        initView();
        initData();
    }

    private void initView() {
        tvType = findView(R.id.manager_group_text);
        gridView = findView(R.id.dynamic_grid);
        // item的点击
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == 1) {
                    InterestBean item = itemsInterest.get(position);
                    UIHelper.showCircleIndex(CircleMyListActivity.this, item.getSrpId(), item.getSrp_word(), item.getName(), item.getImage());
                } else if (type == 0) {
                    SubscribeItem item = itemsSRP.get(position);
                    IntentUtil.gotoSouYueSRP(CircleMyListActivity.this, item.keyword(), item.srpId(), item.image());
                }

            }
        });
        progress = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
    }

    private void initData() {
        token = SYUserManager.getInstance().getToken();
        looked_user_id = getIntent().getLongExtra("looked_user_id", -1);
        long my_userId = 0;
        try {
            my_userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        type = getIntent().getIntExtra("type", -1);
        if (type == 1) {
            if (my_userId == looked_user_id) {
                tvType.setText("我的兴趣圈");
            } else {
                tvType.setText("TA的兴趣圈");
            }
        } else if (type == 0) {
            if (my_userId == looked_user_id) {
                tvType.setText("我的主题");
            } else {
                tvType.setText("TA的主题");
            }
        }
        mCMainHttp = CMainHttp.getInstance();
        if (type == 0) {
//            http.zhutiForUser(looked_user_id);
            loadInterestSub(looked_user_id);
        } else if (type == 1) {
//            http.interestForUser(token, looked_user_id);
            loadInterestForUser(token,looked_user_id);
        }
        showLoad();
    }

    /**
     * 加载网络数据---个人中心点击更多获取兴趣圈列表
     * @param token
     * @param user_id
     */
    private void loadInterestForUser(String token, long user_id)
    {
        UserInterestListReq req = new UserInterestListReq(HttpCommon.PERSONCENTER_INTEREST_LIST_ID,this);
        req.setParams(token,user_id);
        mCMainHttp.doRequest(req);
    }

    /**
     *  加载网络数据---个人中心点击更多获取主题列表
     * @param user_id
     */
    private void  loadInterestSub(long user_id)
    {
        UserInterestSubReq req = new UserInterestSubReq(HttpCommon.PERSONCENTER_INTEREST_SUB_ID,this);
        req.setParams(String.valueOf(user_id));
        mCMainHttp.doRequest(req);
    }

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        switch (request.getmId())
        {
            case HttpCommon.PERSONCENTER_INTEREST_LIST_ID:
                this.itemsInterest = obj2InterestList((HttpJsonResponse)request.getResponse());
                doNotify(itemsInterest);
                break;
            case HttpCommon.PERSONCENTER_INTEREST_SUB_ID:
                this.itemsSRP = obj2SubscribeItemList((HttpJsonResponse)request.getResponse());
                doNotify(itemsSRP);
                break;
        }

    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()) {
            case HttpCommon.PERSONCENTER_INTEREST_LIST_ID:
            case HttpCommon.PERSONCENTER_INTEREST_SUB_ID:
                showNetError();
                break;
        }
    }

//    public void interestForUserSuccess(HttpJsonResponse jsonObj, AjaxStatus status) {
//        this.itemsInterest = obj2InterestList(jsonObj);
//        doNotify(itemsInterest);
//    }
//
//    public void zhutiForUserSuccess(HttpJsonResponse jsonObj, AjaxStatus status) {
//        this.itemsSRP = obj2SubscribeItemList(jsonObj);
//        doNotify(itemsSRP);
//    }

    private List<InterestBean> obj2InterestList(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            return null;
        }
        return new Gson().fromJson(res.getBodyArray(), new TypeToken<List<InterestBean>>() {
        }.getType());
    }

    private List<SubscribeItem> obj2SubscribeItemList(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            return null;
        }
        return new Gson().fromJson(res.getBodyArray(), new TypeToken<List<SubscribeItem>>() {
        }.getType());
    }

    private void doNotify(List<?> items) {
        if (null == items) {
            return;
        }
        if (items.size() <= 0) {
            showNoData();
        } else {
            goneLoad();
        }
        adapter = new CheeseDynamicAdapter(this, items, 3);
        gridView.setAdapter(adapter);
    }

    private void showNoData() {
        if (progress != null)
            progress.showNoData();
    }

    private void goneLoad() {
        if (progress != null)
            progress.goneLoading();
    }

    private void showLoad() {
        if (progress != null)
            progress.showLoading();
    }

    private void showNetError() {
        if (progress != null)
            progress.showNetError();
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if ("interestForUser".equals(methodName)) {// 获取兴趣圈
//            showNetError();
//        } else if ("zhutiForUser".equals(methodName)) {// 获取所有订阅数据
//            showNetError();
//        }
//    }


    @Override
    public void onBackPressed() {
        onBackPressClick(null);
    }

    @Override
    public void onBackPressClick(View view) {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
