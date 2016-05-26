package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.Intent;

/** 
 * @author : zoulu
 * 2014年5月27日
 * 下午10:00:33 
 * 类说明 :本地广播帮助类
 */
public class BroadCastUtils {

	public static final String HOMELIST_DELETE = "HOME_LIST_DELETE";
	public static final String SEARCH_TIME = "time";

	public static final void sendToDeleteSearchResultItemData(Context context,String time){
		Intent intent = new Intent();
		intent.setAction(HOMELIST_DELETE);
		intent.putExtra(SEARCH_TIME,time);
		context.sendBroadcast(intent);
	}

}
