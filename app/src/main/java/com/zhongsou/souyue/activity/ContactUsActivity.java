package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.webview.JavascriptInterface.OnJSClickListener;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.MakeCookie;

public class ContactUsActivity extends BaseActivity implements ProgressBarHelper.ProgressBarClickListener,OnJSClickListener{
    private String sourcePageUrl = "";
    private WebView mWebView;
    private RelativeLayout webView_parent;
    private TextView barTitle;
    private ProgressBarHelper pbHelp;
    private boolean isReqFailed = false;

    private boolean isContactUs ;
    
	@Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.contact_us_activity);
        init();
    }

    private void init() {
        initFromIntent();
        initTitleBar();
        initView();
        if (sourcePageUrl != null) {
            MakeCookie.synCookies(this, sourcePageUrl);
            mWebView.loadUrl(sourcePageUrl);
        }
    }

    private void initTitleBar() {
        barTitle = findView(R.id.activity_bar_title);
        if (barTitle != null){
            String title = null;
            if(isContactUs){
                title  = "联系我们";
            }else{
                title  = "用户协议";
            }
            barTitle.setText(title);
        }
    }

    private void initView() {
    	pbHelp = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
    	pbHelp.setProgressBarClickListener(this);
    	pbHelp.setFromH5(true);
        webView_parent = (RelativeLayout) findViewById(R.id.webView_parent);
        mWebView = (WebView) findViewById(R.id.contactUsWebView);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);// 进度条
                if (newProgress > 70) {
                    pbHelp.goneLoading();
                }
                
                if (newProgress == 100) {
                	if (isReqFailed()){
                		pbHelp.showNetError();
					}else {
						pbHelp.goneLoading();
					}
                }
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
        	@Override
        	public void onReceivedError(WebView view, int errorCode,
        			String description, String failingUrl) {
        		isReqFailed = true;
                super.onReceivedError(view, errorCode, description, failingUrl);
        	}
        	
        	@Override
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {
        		return true;
        	}

        });
    }

    /**
     * 初始数据
     */
    private void initFromIntent() {
        Intent i = this.getIntent();
        if (i != null) {
            sourcePageUrl = i.getStringExtra(WebSrcViewActivity.PAGE_URL);
            if(sourcePageUrl.contains("contactus.html")){
                isContactUs = true;
            }
        } 
    }

    public void onCloseClick(View v) {
        onCloseActivityClick(v);
    }

    public void onCloseActivityClick(View v) {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            webView_parent.removeView(mWebView);
            mWebView.setVisibility(View.GONE);
            mWebView.destroy();
            mWebView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//    }
    
    @Override
    public void onBackPressed() {
        onCloseActivityClick(null);
    }
    
    public boolean isReqFailed() {
		return isReqFailed;
	}

	public void setReqFailed(boolean isReqFailed) {
		this.isReqFailed = isReqFailed;
	}

	@Override
	public void clickRefresh() {
		if(mWebView != null && sourcePageUrl != null && !"".equals(sourcePageUrl)){
			if(CMainHttp.getInstance().isNetworkAvailable(this)) {
				setReqFailed(false);
				mWebView.loadUrl(sourcePageUrl);
			}
		}
	}

    @Override
    public void onJSClick(JSClick json) {
        ImJump2SouyueUtil.IMAndWebJump(ContactUsActivity.this,json,null);
    }
}
