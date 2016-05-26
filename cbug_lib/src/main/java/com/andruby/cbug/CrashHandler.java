package com.andruby.cbug;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.util.Log;

/**
 * 异常捕获类
 * 
 * @ClassName: CrashHandler
 * @Description:
 * @author: zhm 邮箱：zhaomeng@baihe.com
 * @date: 2014年12月4日 下午3:57:41
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private final static String LOG_TAG = CrashHandler.class.getSimpleName();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static CrashHandler INSTANCE = new CrashHandler();

	public interface CrashListener {
		void onCrashListener();
	}

	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	private CrashListener mCrashListener;
	private CbugUpload mBugUpload;

	private CrashHandler() {

	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context context,CrashListener crashListener) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mBugUpload = CbugUpload.getInstance();
		mBugUpload.uploadPhoneInfo(mContext);
		mBugUpload.uploadBug();
		mCrashListener = crashListener;
		Log.i("CrashHandler", "init.............");
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.i(LOG_TAG,"uncaughtException--->");
			ex.printStackTrace();
		if (handlerException(thread, ex)) {
			mDefaultHandler.uncaughtException(thread, ex);//暂时交给系统
			if (mCrashListener != null) {
				mCrashListener.onCrashListener();
			}
		}else{
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private boolean handlerException(Thread thread, Throwable tr) {
		Log.v(LOG_TAG, "CrashHandler 处理异常");
		if (tr == null) {
			return false;
		}
		// Log.e(LOG_TAG, Log.getStackTraceString(tr));
		// String errMsg = tr.getLocalizedMessage();
		// if (errMsg == null && tr.getCause() != null) {
		// errMsg = tr.getCause().getLocalizedMessage();
		// }

		BugInfo bugInfo = new BugInfo();
		bugInfo.packageName = mContext.getPackageName();
		boolean flag = false;
		if(tr.getCause()!=null&&tr.getCause().getStackTrace()!=null){
			for (StackTraceElement element : (tr.getCause().getStackTrace())) {
				bugInfo.className = element.getClassName();
				bugInfo.methodName = element.getMethodName();
				bugInfo.lineNumber = element.getLineNumber();
				// System.out.println("bugInfo.className = "+bugInfo.className);
				// System.out.println("bugInfo.methodName = "+bugInfo.methodName);
				// System.out.print("bugInfo.lineNumber ="+bugInfo.lineNumber);
				if (bugInfo.className != null
						&& (bugInfo.className.contains("com.zhongsou") || bugInfo.className
						.contains("com.tuita"))) {
					flag = true;
					break;
				}
			}
		}

		if (!flag) {
			bugInfo.className = tr.getStackTrace()[0].getClassName();
			bugInfo.methodName = tr.getStackTrace()[0].getMethodName();
			bugInfo.lineNumber = tr.getStackTrace()[0].getLineNumber();
		}
		bugInfo.appVersion = PhoneUtils.getVersionName(mContext);
		bugInfo.bugInfo = Log.getStackTraceString(tr);
		bugInfo.ctime = dateFormat.format(System.currentTimeMillis());//System.currentTimeMillis()+"";
		bugInfo.channelName = PhoneUtils.getChannel(mContext);
		if (tr.getCause() != null) {
			bugInfo.errorMsg = tr.getCause().getMessage();
		} else {
			bugInfo.errorMsg = "";
		}
		bugInfo.deviceId = PhoneUtils.deviceUniqueId(mContext);
		mBugUpload.saveBugInfo(bugInfo);
		return true;
	}
}
