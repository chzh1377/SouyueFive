package com.zhongsou.souyue.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.WXShareBean;
import com.zhongsou.souyue.net.detail.WXShareRequest;
import com.zhongsou.souyue.net.sub.SubCheckRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CWXShareHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.ToastUtil;

/**
 * Created by zyw on 2015/10/26.
 */
public class WXShareEnveDialog extends Dialog implements View.OnClickListener, IVolleyResponse {

//    private final CWXShareHttp mHttp; // http对象
	private CMainHttp mainHttp;
    private Context mContext; // 上下文
    private RelativeLayout contentLayout; //内容布局
    private LinearLayout loadingLayout; // loading 布局

    private WebView contentWebView;

    private WXShareRequest mRequest;
    
    public WXShareEnveDialog(Context context) {
        super(context, R.style.dialog_alert_old);
        this.mContext = context;
        mainHttp = CMainHttp.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wxshare_envelop_dialog);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                initData();
            }
        });
    }

    private void initView() {
        contentLayout = (RelativeLayout) findViewById(R.id.wxshare_envelop_contentlayout);
        loadingLayout = (LinearLayout) findViewById(R.id.wxshare_envelop_loadinglayout);
        contentWebView = (WebView) findViewById(R.id.wxshare_envelop_content);
        findViewById(R.id.wxshare_envelop_btnok).setOnClickListener(this);
    }

    private void initData() {
        // init webview
        WebSettings webSettings = contentWebView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(false); // 设置显示缩放按钮
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);// 提高渲染优先级
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webSettings.setSupportZoom(false);
        
        mRequest = new WXShareRequest(HttpCommon.DETAIL_WX_SHARE_JF_ID, this);
        mRequest.setParams();
        mainHttp.doRequest(mRequest);
        
//        mHttp.doGetJF(CWXShareHttp.REQUEST_JF, this);
        setLoading();
    }


    //设置界面为loading
    private void setLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
//        contentLayout.setVisibility(View.INVISIBLE);
        findViewById(R.id.wxshare_envelop_btnok).setVisibility(View.INVISIBLE);
        findViewById(R.id.wxshare_envelop_divider).setVisibility(View.INVISIBLE);
    }

    //设置有数据的界面
    private void setLoadingSuccess(int state) {
//        contentLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.INVISIBLE);
        if (state != WXShareBean.STATE_GETSUCCESS) {
            findViewById(R.id.main_content).setBackgroundResource(R.drawable.wxshare_enve_background);
            findViewById(R.id.wxshare_envelop_btnok).setVisibility(View.VISIBLE);
            findViewById(R.id.wxshare_envelop_divider).setVisibility(View.VISIBLE);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
        } else {
            findViewById(R.id.main_content).setBackgroundResource(android.R.color.transparent);
            contentWebView.setBackgroundColor(0); // 设置背景色
            contentWebView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
            //2秒消失
            contentWebView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            },2000);
        }
    }

    //设置获取数据失败的界面
    private void setLoadingFail() {
        ToastUtil.show(getContext(), "加载失败...");
        dismiss();
    }

    @Override
    public void onClick(View v) {
        if (isShowing())
            dismiss();
    }

    //http返回值
    @Override
    public void onHttpResponse(IRequest _request) {
        int id = _request.getmId();
        switch (id) {
            case HttpCommon.DETAIL_WX_SHARE_JF_ID: // 获取积分接口返回
                onGetJFResp((WXShareBean) _request.getResponse());
                break;
        }
    }

    /**
     * 积分返回回调
     * @param bean
     */
    private void onGetJFResp(final WXShareBean bean) {
        if (bean != null) {
            if (mStateInterface != null) {
                mStateInterface.onGetJF(bean.getState());
            }
            contentWebView.loadDataWithBaseURL(null, bean.getBody(), "text/html", "UTF-8", null);
            contentWebView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    contentWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setLoadingSuccess(bean.getState());
                        }
                    },20);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    setLoadingFail();
                }
            });
        } else {
            setLoadingFail();
        }

    }

    /**
     * 返回键消失
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    /**
     * 设置状态监听器
     * @param stateInterface
     */
    public void setStateListener(IGetJFInterface stateInterface) {
        this.mStateInterface = stateInterface;
    }

    private IGetJFInterface mStateInterface;

    //领取积分之后的回调
    public static interface IGetJFInterface {
        void onGetJF(int state);
    }

    @Override
    public void onHttpError(IRequest _request) {
        mRequest.setFinished(_request.getmId());
        setLoadingFail();
    }

    @Override
    public void onHttpStart(IRequest _request) {

    }
}
