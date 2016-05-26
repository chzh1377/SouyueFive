package com.zhongsou.souyue.utils;

public class FastDoubleCliceUtils {

	private static long lastClickTime;

	public static synchronized boolean isFastDoubleClick() {

		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
