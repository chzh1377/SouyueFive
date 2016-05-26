package com.zhongsou.souyue.im.view;

import android.content.Context;
import android.content.Intent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.PushInfo;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;
import org.json.JSONObject;

public class ImUIHelpr {
	/**
	 * 判断信息是否来自im
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isIMMsg(String data) {
		try {
//			JSONObject json = JSON.parseObject(data);
			JSONObject json = new JSONObject(data);
			return "im".equals(json.getString("t"));
		} catch (Exception e) {
			return false;
		}
	}

	public static void startIm(Context mContext) {
		Intent intent = new Intent();
		if (MainApplication.getInstance().isRunning()) {
			if (ConfigApi.isSouyue()) {
				ZhongSouActivityMgr.getInstance().goHome();
				IntentUtil.openMainActivity(mContext, new int[] { 1 });
			} else {
				PushInfo pushInfo = new PushInfo();
				pushInfo.setJumpType("im");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClass(mContext, CommonStringsApi.getHomeClass());
				intent.putExtra("push_info", pushInfo);
				mContext.startActivity(intent);
			}
		} else {
			PushInfo pushInfo = new PushInfo();
			pushInfo.setJumpType("im");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(mContext, CommonStringsApi.getHomeClass());
			intent.putExtra("push_info", pushInfo);
			mContext.startActivity(intent);
		}

	}



	/**
	 * 离线情况下从通知栏跳转到聊天列表，再跳转到聊天页
	 * 参数是跳转到聊天界面必须的参数
	 * @param context
	 */
	public static final String IS_JUMP_CHAT = "isJumpChat";
	public static final String TARGET_TYPE = "targetType";
	public static final String CHAT_ID = "chatId";
	public static void startIMChat(Context context,int targetType,long chatId){
		Intent intent = new Intent();
		PushInfo pushInfo = new PushInfo();
		pushInfo.setJumpType("im");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, CommonStringsApi.getHomeClass());
		intent.putExtra("push_info", pushInfo);
		intent.putExtra(IS_JUMP_CHAT,true);
		intent.putExtra(TARGET_TYPE,targetType);
		intent.putExtra(CHAT_ID,chatId);
		context.startActivity(intent);
	}

    /**
     * 跳到图集
     * @param context
     * @param galleryNewsHomeBean
     */
    public static void startAtlas(Context context,GalleryNewsHomeBean galleryNewsHomeBean){
        Intent intent = new Intent();
        PushInfo pushInfo = new PushInfo();
        pushInfo.setJumpType("atlas");
        pushInfo.setGalleryNews(galleryNewsHomeBean);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, CommonStringsApi.getHomeClass());
        intent.putExtra("push_info", pushInfo);
        context.startActivity(intent);
    }
}
