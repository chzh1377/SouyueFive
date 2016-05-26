package com.tuita.sdk;
class Log {
	private static boolean enableLog = true;
	protected static void i(String tag, String msg) {
		if (enableLog) {
			android.util.Log.i("Tuita", msg);
		}
	}
	protected static void i(String tag, String msg, Throwable e) {
		if (enableLog) {
			android.util.Log.i("Tuita", msg, e);
			e.printStackTrace();
		}
	}
}
