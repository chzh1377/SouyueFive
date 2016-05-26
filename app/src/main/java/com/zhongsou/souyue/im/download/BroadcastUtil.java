package com.zhongsou.souyue.im.download;

import android.content.Context;
import android.content.Intent;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Constants;

/**
 * 发送广播工具类
 * 
 * @author wangqiang
 * 
 */
public class BroadcastUtil {

	/**
	 * 卸载表情包
	 * 
	 * @param context
	 * @param packageBean
	 */
	public static void sendDeleteBroadCast(Context context, PackageBean packageBean) {
		Intent intent = new Intent();
		intent.setAction(Constants.DELETE_ACTION);
		intent.putExtra("packagebean", packageBean);
		context.sendBroadcast(intent);
	}

	/**
	 * 完成下载
	 * @param context
	 * @param packageBean
	 */
	public static void sendAddBroadCast(Context context, PackageBean packageBean) {
		Intent intent = new Intent();
		intent.setAction(Constants.ADD_ACTION);
		intent.putExtra("packagebean", packageBean);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 更新下载进度条
	 * @param context
	 * @param packageBean
	 * @param completeSize
	 */
	public static void sendUpdateBroadCast(Context context,PackageBean packageBean,int completeSize){
		Intent intent = new Intent();
		intent.setAction("updateUI");
		intent.putExtra("completeSize", completeSize);
		intent.putExtra("packageId", packageBean.getPackageId());
		context.sendBroadcast(intent);
	}
	
	public static void sendFailBroadCast(Context context){
		Intent intent = new Intent();
		intent.setAction(Constants.FAIL_ACTION);
		context.sendBroadcast(intent);
	}
}

