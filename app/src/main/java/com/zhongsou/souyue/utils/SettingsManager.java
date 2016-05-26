package com.zhongsou.souyue.utils;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.volley.CMainHttp;

public class SettingsManager {
	private static SettingsManager instance;
	private SYSharedPreferences sysp = SYSharedPreferences.getInstance();
	private SettingsManager() {
		if (sysp == null)
			sysp = SYSharedPreferences.getInstance();
	}

	public static SettingsManager getInstance() {
		if (instance == null) {
			instance = new SettingsManager();
		}

		return instance;
	}

	public boolean getLoadImgAble() {
		return sysp.getBoolean(SYSharedPreferences.KEY_LOADIMGABLE, true);
	}
	
	public boolean isLoadImage(){
		return (CMainHttp.getInstance().isWifi(MainApplication.getInstance()) ? true : getLoadImgAble());
//        return false;
	}

	public void setLoadImgAble(boolean loadImgAble) {
		sysp.putBoolean(SYSharedPreferences.KEY_LOADIMGABLE, loadImgAble);
	}

	public int getFontSize() {
		return sysp.getInt(SYSharedPreferences.KEY_FONTSIZE, 2);
	}

	public void setFontSize(int fontSize) {
		sysp.putInt(SYSharedPreferences.KEY_FONTSIZE, fontSize);
	}

}
