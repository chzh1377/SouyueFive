package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;

public class DownLoadStatementActivity extends BaseActivity {
	
	private WebView wv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_activity_download_statement);
		wv = (WebView) this.findViewById(R.id.statement_webview);// 从XML文件中获取WebView控件
		WebSettings ws = wv.getSettings();
		ws.setJavaScriptEnabled(true);// 设定WebView可以执行JavaScript脚本
		
		//测试页面：http://103.29.135.92/dlclaim.html
		//正式页面：http://open.zhongsou.com/dlclaim.html
		wv.loadUrl("http://open.zhongsou.com/dlclaim.html");// 加载需要显示的网页

		/*
		 * 如果希望点击链接继续在当前browser中响应， 而不是新开Android的系统browser中响应该链接， 必须覆盖
		 * webview的WebViewClient对象。
		 */
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

	}

	/*
	 * 设置回退 如果不做任何处理，浏览网页，点击系统“Back”键， 整个Browser会调用finish()而结束自身， 如果希望浏览的网
	 * 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish(); // 返回WebView的上一页面
			return true;
		}
		return false;
	}
}
