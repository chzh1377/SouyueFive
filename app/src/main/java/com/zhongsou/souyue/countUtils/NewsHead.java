package com.zhongsou.souyue.countUtils;

import android.content.Context;
import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 消息头 类
 * @author Administrator
 *
 */

//
//"head":{
//"time":"手机当前事件，从1970年1月1日开始的毫秒数",
//"uid":"登录或匿名用户id",//登录或匿名用户
//"guest":true,//是否是游客
//"app_key":"5073f4f2527015608c000047",//API KEY
//"app_name":"搜悦|昆仑决|天天汽车|玲珑玉",//应用名称
//"channel":"杭州渠道A",//渠道
//"app_version":"39Beta",//应用版本
//"app_version_code":39,//应用版本代码
//"package":"com.zhongsou.souyue",//应用包名
//"network":"Wi-Fi | 2G | 3G | unknow",//网络类型
//"carrier":"运营商代码,23423",
//"session_id":"5241953257e968923d78b2341dc0b428",//？？？如果是防止别人模拟数据包，
//"mac":"14:26:DF:A7:54:AA",//网卡地址
//"os":"Android|iOS",//操作系统类型
//"os_version":"2.3.4",//操作系统版本
//"lat":0.234234,//维度
//"lng":0.234234,//经度
//"city":"所在城市",
//"resolution":"屏幕分辨率",
//"device_id":"354515097569952",//imei 唯一 ID
//"device_name":"Lenovo S680",//手机型号，即设备名称
//"sdk_version":"1.0"//统计API的版本，用于区分不同API并做兼容。
//}

public class NewsHead {
	private JSONObject obj;
	private String time;//手机当前时间，从1970年1月1日开始的毫秒数
//	private String uid;//登录或匿名用户ID
//	private String guest;//是否是游客
	private String app_key="B69852E23F465103877F1B2E";//APIKey  应该是后台给
//	private String app_key="1a612226c6ce65cb0ef4eef4dfbe77b3";//APIKey  应该是后台给
	private static String app_name;//应用名称
	private static String channel;//渠道
//	private String app_version;//应用版本
//	private String app_version_code;//应用版本
	private static String packageName;//应用包名
//	private String network;//网络类型
	private static String carrier;//运营商代码
//	private String session_id;//防止别人模拟数据包
	private static String mac;//网卡地址
	private static String os="Android";//操作系统类型   //android版本固定为Android
	private static String os_version;//操作系统版本号
//	private String lat;//纬度
//	private String lng;//经度
//	private String city;//所在城市
	private static String resolution;//屏幕分辨率
	private static String device_id;//唯一ID
	private static String device_name;//手机型号，即设备名称
	private String sdk_version="1.1";//统计API版本，用于区分不同API并做兼容
	
	public NewsHead(Context context){
		obj=new JSONObject();
		this.time =System.currentTimeMillis()+"";
		try {
			obj.put("time", time);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("uid", AppInfoUtils.getUid());
//			SYUserManager  暂时根据Context无法获取
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("guest", AppInfoUtils.getGuest());
//			SYUserManager  暂时根据Context无法获取			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("app_key", app_key);//需要后台给出

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(app_name)){
			app_name=AppInfoUtils.getAppName(context);
		}
		
		try {
			obj.put("app_name",app_name);//不知道是什么
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(channel)){
			channel=AppInfoUtils.getChannel(context);
		}
		try {
			obj.put("channel", channel);//渠道号
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("app_version", AppInfoUtils.getAppVersion(context));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("app_version_code", AppInfoUtils.getAppVersionCode(context));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(packageName)){
			packageName=AppInfoUtils.getPackageName(context);
		}
		
		try {
			obj.put("package", packageName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("network", AppInfoUtils.networkType(context));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(carrier)){
			carrier = AppInfoUtils.getNetWorkOperator(context);
		}
		
		try {
			obj.put("carrier", carrier);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(TextUtils.isEmpty(mac)){
			mac=AppInfoUtils.getDeviceMac(context);
		}
		
		try {
			obj.put("mac", mac);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("os",os);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(os_version)){
			os_version = AppInfoUtils.getAndroidSDKVersionName();
		}
		
		try {
			obj.put("os_version", os_version);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("lat", AppInfoUtils.getLat(context));//需要问从哪获取
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("lng", AppInfoUtils.getLng(context));//需要问从哪获取
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("city", AppInfoUtils.getCity(context));//同上
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(resolution)){
			resolution=AppInfoUtils.getResolution(context);
		}
		
		try {
			obj.put("resolution", resolution);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(TextUtils.isEmpty(device_id)){
			device_id=AppInfoUtils.getDevice_id(context);
		}
		
		try {
			obj.put("device_id", device_id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sessionStr="ZHONGSOUSOUYUEKEY"+device_id+time;
		
		String md1=AppInfoUtils.makeMD5(sessionStr);
//		String md2=AppInfoUtils.encryption(sessionStr);
		try {
			obj.put("session_id", md1);//不知道是啥
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(TextUtils.isEmpty(device_name)){
			device_name=AppInfoUtils.getDevice_name(context);
		}
		
		try {
			obj.put("device_name", device_name);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj.put("sdk_version", sdk_version);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public JSONObject getObj() {
		return obj;
	}


	public void setObj(JSONObject obj) {
		this.obj = obj;
	}
	
}
