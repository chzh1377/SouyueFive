package com.zhongsou.souyue.utils;

import android.app.Activity;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.platform.ConfigApi;

import java.util.LinkedList;
public class ZhongSouActivityMgr {

	public static LinkedList<Activity> acys;
	private static ZhongSouActivityMgr instance = null;
	private ZhongSouActivityMgr() {
	}

    public synchronized static ZhongSouActivityMgr getInstance() {
		if (instance == null) {
			instance = new ZhongSouActivityMgr();
			acys = new LinkedList<Activity>();
		}
		return instance;
	}

	public void add(Activity acy) {
		acys.add(acy);
	}

	public void remove(Activity acy) {
		if (acy.isFinishing()){
			for (int i = 0; i < acys.size(); i ++){
				if (acy == acys.get(i))
					acys.remove(i);
			}
		}
	}
	
	/**
	 * 回到首页
	 */
	public void goHome(){
		Activity acy;
		while (acys.size() != 0) {
			acy = acys.poll();
			if (!acy.isFinishing() && (ConfigApi.isSouyue() && !(acy instanceof MainActivity))) {
				acy.finish();
			}

		}
	}
	public void exit() {
//		SelfCreateUploadThread.getInstance().stop();
		MainApplication.getInstance().killService();
		MainApplication.getInstance().exitActivity();
		Activity acy;
		while (acys.size() != 0) {
			acy = acys.poll();
			if (!acy.isFinishing()) {
				acy.finish();
			}

		}
	}
	public void errorExit(){
		exit();
		MobclickAgent.onKillProcess(MainApplication.getInstance());
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
