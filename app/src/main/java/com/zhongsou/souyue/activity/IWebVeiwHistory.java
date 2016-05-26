package com.zhongsou.souyue.activity;

/**
 * webview是否有上一个历史记录
 * @author wanglong@zhongsou.com
 *
 */
public interface IWebVeiwHistory {
	public boolean canGoBack();
	public void goBack();
}
