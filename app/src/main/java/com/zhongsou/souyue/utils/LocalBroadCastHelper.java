package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/** 
 * @author : zoulu
 * 2014年5月27日
 * 下午10:00:33 
 * 类说明 :本地广播帮助类
 */
public class LocalBroadCastHelper {
	public static final String KEY = "key";
	public static final String POS = "pos";
	public static final String DIRECTION = "direction";
	public static final String RIGHT = "right";
	public static final String LEFT = "left";
	public static final String GONE = "gone";
	public static final String HAVEREAD = "haveread";
	public static final String READPOS = "readpos";

	public static final String HOMELIST_DELETE = "HOME_LIST_DELETE";
	
	public static void sendPositionToHome(int position,Context cx,String type){
		Intent intent = new Intent(KEY);  
		intent.putExtra(POS, position);  
		intent.putExtra(DIRECTION, type);  
		LocalBroadcastManager.getInstance(cx).sendBroadcast(intent);  
	}
	
	public static void sendGoneLoading(Context cx){
		Intent intent = new Intent(GONE);  
		LocalBroadcastManager.getInstance(cx).sendBroadcast(intent);  
	}
	
//	public static void sendHasRead(Context cx, int[] pos){
//		Intent intent = new Intent(HAVEREAD);
//		intent.putExtra(READPOS, pos);
//		LocalBroadcastManager.getInstance(cx).sendBroadcast(intent);
//	}
}
