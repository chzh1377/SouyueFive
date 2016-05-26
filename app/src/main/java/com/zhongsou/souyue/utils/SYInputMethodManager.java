package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class SYInputMethodManager {
	private Activity activity;
	public static int status = -1;

	public SYInputMethodManager(Activity con) {
		this.activity = con;
	}

	public void showSoftInput() {
		InputMethodManager im = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
//		if (!(activity.getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN))
		if (status == -2)
			im.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
	}
	
	public void hideSoftInput() {
		InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (null != activity.getCurrentFocus())
			im.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
