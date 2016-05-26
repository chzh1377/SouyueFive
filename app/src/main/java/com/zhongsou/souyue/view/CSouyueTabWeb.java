package com.zhongsou.souyue.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.fragment.SouyueTabFragment;
import com.zhongsou.souyue.module.HomeBallBean;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.webview.CBaseWebView;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYSharedPreferences;

/**
 * 首页底部需要展示的webview
 * Created by lvqiang on 15/6/13.
 */
public class CSouyueTabWeb extends AFragmentBaseView<HomeBallBean> implements
        ProgressBarHelper.ProgressBarClickListener, AbsListView.OnScrollListener {

    /**
     * 首页请求详情的requestCode
     */


    protected ProgressBarHelper pbHelp;

    private HomeBallBean homeBallBean; //表示当前页面的分类信息，最有用的就是category了

    private String mType;
    private CBaseWebView mWebView;
    protected boolean mIsDestory;
    private SouyueTabFragment mFragment;
    private String mUrl;
    private String hasPic = SYSharedPreferences.getInstance().getLoadWifi(getContext()) ? "0" : "1";
    private String wifi ;


    public CSouyueTabWeb(Context context) {
        super(context);
    }

    public CSouyueTabWeb(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CSouyueTabWeb(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initView() {
        wifi = CMainHttp.getInstance().isWifi(getContext()) ? "1" : "0";
        initView(this);
    }

    @Override
    public void unInitView() {
        mWebView.clearHistory();
        mWebView.loadUrl(CBaseWebView.BLANK_URL);
            pbHelp.showLoadingUI();
    }

    @Override
    public void setData(HomeBallBean homeball, SouyueTabFragment fragment) {
        mFragment = fragment;
        homeBallBean = homeball;
        mType = homeBallBean.getCategory();
        mIsDestory = false;
        initData();
    }


    private void initData() {
        mWebView.setmProcess(pbHelp);
        mWebView.setVisibility(GONE);//若是webview加载失败了即使是INVISIBLE的也会影响下层viewpage的滑动事件，所以设置为Gon
        cleanWebviewData();
        mUrl = homeBallBean.getUrl() + "&hasPic=" + hasPic + "&wifi=" + wifi;
        MakeCookie.synCookies(getContext(), mUrl);
        mWebView.loadUrl(CBaseWebView.BLANK_URL);
        pbHelp.showLoadingUI();
        mWebView.setDestory(false);
    }


    public void checked(TextView v,boolean ischecked){
        if (ischecked){
            v.setBackgroundResource(R.drawable.homepage_delete_item_checked);
            v.setTextColor(getResources().getColor(R.color.homepage_delete_red));
        }else{
            v.setBackgroundResource(R.drawable.homepage_delete_item_normal);
            v.setTextColor(getResources().getColor(R.color.ball_text_color));
        }
    }

    private void cleanWebviewData() {
        mWebView.clearFormData();
        mWebView.clearHistory();
//        mWebView.loadUrl("about:blank");
    }

    @Override
    public void setDestory(boolean des) {
        mIsDestory = des;
        mWebView.clearHistory();
        mWebView.loadUrl(CBaseWebView.BLANK_URL);
        pbHelp.showLoadingUI();
    }

    private void initView(View view) {
        //这些东西和对象有关，对象不销毁就只创建一份
        mMainView = view;
        mWebView = (CBaseWebView) view.findViewById(R.id.cover_webview);
        pbHelp = new ProgressBarHelper(mActivity,
                view.findViewById(R.id.ll_data_loading));
        pbHelp.setProgressBarClickListener(this);
    }


    public void pullToRefresh(boolean force) {
        if (force){
            return;
        }
        if (!HomeBallBean.isEnable(homeBallBean.getCategory())){
            mFragment.setEnterAphla(0.f);
        }
        mWebView.setVisibility(View.VISIBLE);
        mWebView.setmProcess(pbHelp);
        pbHelp.showLoadingUI();
        cleanWebviewData();
        mWebView.setVisibility(GONE);//若是webview加载失败了即使是INVISIBLE的也会影响下层viewpage的滑动事件，所以设置为Gone
        mUrl = homeBallBean.getUrl() + "&hasPic=" + hasPic + "&wifi=" + wifi;
        Log.v(this.getClass().getName(), "home load url:" + mUrl);
        MakeCookie.synCookies(getContext(), mUrl);
        mWebView.loadUrl(mUrl);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void updateViewList() {
        Log.v(this.getClass().getName(), "----------刷新字体");
        mWebView.updateWebViewFont();
    }


    @Override
    public void clickRefresh() {
        pbHelp.showLoadingUI();
        mWebView.setVisibility(GONE);//若是webview加载失败了即使是INVISIBLE的也会影响下层viewpage的滑动事件，所以设置为Gone
        MakeCookie.synCookies(getContext(), homeBallBean.getUrl());
        mUrl = homeBallBean.getUrl() + "&hasPic=" + hasPic + "&wifi=" + wifi;
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (HomeBallBean.isEnable(mType)) {
                    mFragment.setEnterButtonAphla(1.f);
                }else{
                    mFragment.setEnterButtonAphla(0.f);
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                //persent为负值
                if (HomeBallBean.isEnable(mType)) {
                    mFragment.setEnterButtonAphla(0.5f);
                }else{
                    mFragment.setEnterButtonAphla(0.f);
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                if (HomeBallBean.isEnable(mType)) {
                    mFragment.setEnterButtonAphla(0.5f);
                }else{
                    mFragment.setEnterButtonAphla(0.f);
                }
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

}
