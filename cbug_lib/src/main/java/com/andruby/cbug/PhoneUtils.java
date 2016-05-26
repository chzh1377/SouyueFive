package com.andruby.cbug;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.andruby.logutils.R;


/**
 * 
 * @Description:手机系統相关的操作类，如网络、手机号、硬件号等
 * @ClassName: PhoneUtils
 * @author: zhm andruby
 * @date: 2014年9月19日 下午5:52:38
 * 
 */
public class PhoneUtils {

	/**
	 * 
	 * @Description:获取屏幕的宽度
	 * @Title:getScreenWidth
	 * @param context
	 * @return
	 * @return:int
	 * @throws
	 * @Create: 2014年9月19日 下午5:53:44
	 * @Author : zhm andruby
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 
	 * @Description:获取屏幕的高度
	 * @Title:getScreenHeight
	 * @param context
	 * @return
	 * @return:int
	 * @throws
	 * @Create: 2014年9月19日 下午5:53:57
	 * @Author : zhm andruby
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 
	 * @Title:deviceUniqueNum
	 * @Description:根据设备ID，ShortID，AndroidId，Mac Address组合取MD5得到设备唯一标识
	 * @param context
	 * @return
	 * @return:String
	 * @throws
	 * @Create: 2014-3-26 下午10:29:46
	 * @Author : zhm 邮箱：zhaomeng@baihe.com
	 */
	public static String deviceUniqueId(Context context) {
		String uniqueId = getDeviceIdFromSP(context);
		if (uniqueId!=null&&uniqueId.length()>0)
		{
			return  uniqueId;
		}
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = TelephonyMgr.getDeviceId();

		String shortId = "35" + Build.BOARD.length() % 10
				+ Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10
				+ Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10
				+ Build.HOST.length() % 10 + Build.ID.length() % 10
				+ Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10
				+ Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10
				+ Build.TYPE.length() % 10 + Build.USER.length() % 10; // 13
																		// digits

		String androidId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);

		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		String macAdress = "";
		if (wm.getConnectionInfo() != null) {
			macAdress = wm.getConnectionInfo().getMacAddress();
		}

		String longId = deviceId + shortId + androidId + macAdress;
		// compute md5
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(longId.getBytes(), 0, longId.length());
		byte p_md5Data[] = m.digest();
		String uniqueID = "";
		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);
			if (b <= 0xF)
				uniqueID += "0";
			uniqueID += Integer.toHexString(b);
		}
		uniqueID = uniqueID.toUpperCase();

		uniqueId = uniqueID;
		setDeviceIdFromSP(context,uniqueId);
		return uniqueId;
	}

	/**
	 * 
	 * @Title:getSDCardStatus
	 * @Description:判断sd卡是可用
	 * @return
	 * @return:boolean
	 * @throws
	 * @Create: 2014-3-31 下午6:03:45
	 * @Author : zhm 邮箱：zhaomeng@baihe.com
	 */
	public static boolean getSDCardStatus() {
		String state = android.os.Environment.getExternalStorageState();
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @Description:Mac Address
	 * @Title:getMacAddress
	 * @param context
	 * @return
	 * @return:String
	 * @throws
	 * @Create: 2014年10月4日 上午12:23:06
	 * @Author : zhm andruby
	 */
	public static String getMacAddress(Context context) {
		String macAddress = "";
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = null;
		if(wifi!=null){
			info = wifi.getConnectionInfo();
		}else{
			return "";
		}
		if (info != null) {
			macAddress = info.getMacAddress();
		}else{
			return "";
		}
		return macAddress;
	}

	/**
	 * 
	 * @Description:判断网络是否可用
	 * @Title:netAvailable
	 * @param activity
	 * @return
	 * @return:boolean
	 * @throws
	 * @Create: 2014年10月4日 上午12:24:08
	 * @Author : zhm andruby
	 */
	public static boolean netAvailable(Activity activity) {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo = cwjManager.getActiveNetworkInfo();
		if (netWorkInfo != null) {
			flag = netWorkInfo.isAvailable();
		}
		return flag;
	}

	/**
	 * 
	 * @Description:得到设备ID(IMEI)或Android_ID
	 * @Title:getDeviceId
	 * @param context
	 * @return
	 * @return:String
	 * @throws
	 * @Create: 2014年10月4日 上午12:24:24
	 * @Author : zhm andruby
	 */
	public static String getDeviceId(Context context) {
		String deviceId = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (deviceId == null)
			deviceId = Settings.Secure.getString(context.getContentResolver(),
					Settings.Secure.ANDROID_ID);
		return deviceId;
	}

	/**
	 * 
	 * @Description 系统sdk版本
	 * @Title getSdkVersion
	 * @return
	 * @return int
	 * @throws
	 * @Create 2014年10月4日 上午12:39:06
	 * @Author zhm andruby
	 */
	public static int getSdkVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * 
	 * @Description 手机型号
	 * @Title getPhoheModel
	 * @return
	 * @return String
	 * @throws
	 * @Create 2014年10月4日 上午12:39:24
	 * @Author zhm andruby
	 */
	public static String getPhoheModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * 
	 * @Description 手机厂家
	 * @Title getPhoneBrand
	 * @return
	 * @return String
	 * @throws
	 * @Create 2014年10月4日 上午12:39:38
	 * @Author zhm andruby
	 */
	public static String getPhoneBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 
	 * @Description 得到屏幕尺寸大小
	 * @Title getDeviceSize
	 * @param context
	 * @return
	 * @return double
	 * @throws
	 * @Create 2014年10月4日 上午12:40:03
	 * @Author zhm andruby
	 */
	public static double getDeviceSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		return Math.sqrt(x + y);
	}

	/**
	 * 
	 * @Description 判断设备是手机、平板，<br>
	 *              返回Android Phone 小于6寸，Android mini Pad 6-9寸,Android pad 大于9寸
	 * @Title:getDeviceType
	 * @param context
	 * @return
	 * @return:String
	 * @throws
	 * @Create: 2014年10月4日 上午12:28:22
	 * @Author : zhm andruby
	 */
	public static String getDeviceType(Context context) {
		double screenInches = getDeviceSize(context);
		if (screenInches >= 6.0) {
			return "Android mini Pad";
		} else if (screenInches >= 9.0) {
			return "Android Pad";
		} else {
			return "Android Phone";
		}
	}

	/**
	 * 
	 * @Description 获取系统信息 格式<br>
	 *              SimSerialNumber|DEVICE|DISPLAY|SERIAL|SDK|SDK_INT|CODENAME|
	 *              CPU_ABI|CPU_ABI2|MANUFACTURER|MODEL|VERSION.RELEASE
	 * @Title getSystmInfo
	 * @param context
	 * @return
	 * @return String
	 * @throws
	 * @Create 2014年10月4日 上午12:32:10
	 * @Author zhm andruby
	 */
	@SuppressLint("NewApi")
	public static String getSystemInfo(Context context) {
		StringBuffer sb = new StringBuffer();
		// SimSerialNumber|DEVICE|DISPLAY|SERIAL|SDK|SDK_INT|CODENAME|CPU_ABI|CPU_ABI2|MANUFACTURER|MODEL|VERSION.RELEASE
		sb.append(getSimSerical(context)).append("|");
		sb.append(android.os.Build.DEVICE).append("|");
		sb.append(android.os.Build.DISPLAY).append("|");
		sb.append(android.os.Build.SERIAL).append("|");
		sb.append(android.os.Build.VERSION.SDK).append("|");
		sb.append(android.os.Build.VERSION.SDK_INT).append("|");
		sb.append(android.os.Build.VERSION.CODENAME).append("|");
		sb.append(android.os.Build.CPU_ABI).append("|");
		sb.append(android.os.Build.CPU_ABI2).append("|");
		sb.append(android.os.Build.MANUFACTURER).append("|");
		sb.append(android.os.Build.MODEL).append("|");
		sb.append(android.os.Build.VERSION.RELEASE).append("|");
		sb.append(getScreenInfo(context));
		return sb.toString();
	}

	public static String getCpuInfo(){
		return android.os.Build.CPU_ABI+"|"+android.os.Build.CPU_ABI2;
	}

	/**
	 * 
	 * @Description 获取手机卡的序列号(IMSI)
	 * @Title getSimSerical
	 * @param context
	 * @return
	 * @return String
	 * @throws
	 * @Create 2014年10月4日 上午12:37:52
	 * @Author zhm andruby
	 */
	public static String getSimSerical(Context context) {
		String deviceId = ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE))
				.getSimSerialNumber();
		return deviceId;
	}

	/**
	 * 
	 * @Description 得到屏幕信息 <格式>width:height:dpi:desity
	 * @Title getScreenInfo
	 * @param context
	 * @return
	 * @return String
	 * @throws
	 * @Create 2014年10月4日 上午12:40:59
	 * @Author zhm andruby
	 */
	public static String getScreenInfo(Context context) {
		StringBuffer sb = new StringBuffer();
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		sb.append(metrics.widthPixels).append(":");
		sb.append(metrics.heightPixels).append(":");
		sb.append(metrics.densityDpi).append(":");
		sb.append(metrics.density);
		return sb.toString();
	}

	public static PhoneInfo getPhoneInfo(Context context) {
		//根据以前有没有上传过判断需要上传那些字段
		PhoneInfo phoneInfo = new PhoneInfo();
		phoneInfo.deviceId = deviceUniqueId(context);
		phoneInfo.channelName=getChannel(context);
		phoneInfo.appVersion = getVersionName(context);
		if(!getIsUpLoaded(context)){
			phoneInfo.phoneModel = getPhoheModel();
			phoneInfo.macAddress = getMacAddress(context);
			phoneInfo.imei = getDeviceId(context);
			phoneInfo.phoneBrand = getPhoneBrand();
			phoneInfo.androidSdk = Build.VERSION.SDK;
			phoneInfo.phoneRelease = Build.VERSION.RELEASE;
			try{
				phoneInfo.resLayout = context.getString(R.layout.phone_info);
				phoneInfo.resDrawable = context.getString(R.drawable.phone_info);
				phoneInfo.resValues = context.getString(R.string.phone_info);
			}catch (Exception e){
				System.out.println("cbug get phone exception!  at cbu_lib 381");
			}
			phoneInfo.scrrenWidth = PhoneUtils.getScreenWidth(context)+"";
			phoneInfo.sreenHeight = PhoneUtils.getScreenHeight(context)+"";
			phoneInfo.screenSize = PhoneUtils.getDeviceSize(context)+"";
			phoneInfo.cpuAbi = getCpuInfo();
//			phoneInfo.phoneMemory = "";
			phoneInfo.manufacture = Build.MANUFACTURER;
			phoneInfo.product = Build.PRODUCT;
		}
		return phoneInfo;
	}

	/**
	 * 获得当前渠道
	 *
	 * @return 渠道名称
	 */
	public static String getChannel(Context context) {
		ApplicationInfo info = null;
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			return info.metaData.getString("UMENG_CHANNEL");
		}
		return "";
	}

	/**
	 * 
	 * @Title:getVersionName
	 * @Description:获取程序版本号
	 * @param context
	 * @return
	 * @return:String
	 * @throws
	 * @Create: 2014-3-13 下午4:46:27
	 * @Author : zhm 邮箱：zhaomeng@baihe.com
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
			Log.e("log", "[getVersionName]", e);
		}
		return null;
	}

	/**
	 * 如果phoneInfo上传成功，则存入share中，下次取此标识位为true则只上传个别字段，而不是所有都上传
	 * 用完则销毁share
	 * @param context
	 */
	public static void setShare(Context context){
		SharedPreferences share = context.getSharedPreferences("phoneInfo", Context.MODE_PRIVATE);
		share.edit().putBoolean("isUpLoaded",true).commit();
		share = null;
	}

	/**
	 * 得到之前的phone是否上传成功
	 * @param context
	 * @return
	 */
	public static boolean getIsUpLoaded(Context context){
		SharedPreferences share = context.getSharedPreferences("phoneInfo", Context.MODE_PRIVATE);
		if(share.getBoolean("isUpLoaded",false)){
			share = null;
			return true;
		}else{
			share = null;
			return false;
		}
	}

	/**
	 * 获取设备唯一标示
	 * @param context
	 * @return
	 * @author qubian
	 */
	public static String getDeviceIdFromSP(Context context){
		return context.getSharedPreferences("DeviceId", Context.MODE_PRIVATE).getString("uniqueId", null);
	}

	/**
	 * 设置设备唯一标示
	 * @param context
	 * @param uniqueId
	 * @author qubian
	 */
	public static void setDeviceIdFromSP(Context context,String uniqueId) {
		SharedPreferences share = context.getSharedPreferences("DeviceId", Context.MODE_PRIVATE);
		share.edit().putString("uniqueId",uniqueId).commit();
		share = null;
	}
}
