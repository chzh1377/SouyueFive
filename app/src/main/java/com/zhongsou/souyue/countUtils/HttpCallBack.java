package com.zhongsou.souyue.countUtils;

public interface HttpCallBack {
	public void onSuccess(String str);
	public void onTaskError(String str);
	public void onNetError();
	public void onTimeOut();
}
