package com.zhongsou.souyue.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.google.gson.Gson;
import com.zhongsou.souyue.module.DeviceInfoModule;
import com.zhongsou.souyue.net.DeviceInfo;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * @author wanglong@zhongsou.com
 *
 */
public class DeviceUtil {
	private static final String LOGTAG = DeviceUtil.class.getSimpleName();
	private static WifiManager wifiMgr;
	private static TelephonyManager telMgr;
	private static void initManager(Context context) {
		if (wifiMgr == null) {
			Object obj = context.getSystemService(Context.WIFI_SERVICE);
			if (obj != null) {
				wifiMgr = (WifiManager) obj;
			}
		}
		if (telMgr == null) {
			Object obj = context.getSystemService(Context.TELEPHONY_SERVICE);
			if (obj != null) {
				telMgr = (TelephonyManager) obj;
			}
		}
	}
	/**
	 * 获得设备唯一标识
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
	   return com.tuita.sdk.DeviceUtil.getDeviceId(context);
	}
	private static boolean isInvalid(String str) {
		return str == null || str.trim().equals("") || str.trim().length()<5||str.trim().equals("000000000000000");
	}
    
	/**
	 * 获得设备唯一标识集合
	 * @param context
	 * @return
	 */
	public static JSONObject getDeviceIds(Context context) {
		JSONObject json = new JSONObject();
		try {
			initManager(context);
			if (telMgr != null) {
		        json.put("sn", telMgr.getSimSerialNumber());
		        json.put("imei", DeviceInfo.getDeviceId());
		        json.put("subId", telMgr.getSubscriberId());
		        json.put("operName", telMgr.getNetworkOperatorName());
		        json.put("operId", telMgr.getNetworkOperator());
			}
			if (wifiMgr != null) {
				WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
				if (wifiInfo != null) {
					String macAddr = wifiInfo.getMacAddress();
					if (!isInvalid(macAddr)) {
						json.put("mac", macAddr);
					}
				}
			}
			json.put("model", android.os.Build.MODEL);
			json.put("version", android.os.Build.VERSION.RELEASE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public static String getDeviceModel(){
	    return android.os.Build.MODEL;
	}
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp,字体的转换
     */ 
    public static int px2sp(Context context, float pxValue) {  
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
        return (int) (pxValue / fontScale + 0.5f);  
    }

	/**
	 * 根据手机的分辨率从 sp 的单位 转成为 px(像素),字体的转换
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	
	@SuppressLint("NewApi")
    public static  String getDeviceInfo(Context context){
	    DeviceInfoModule dim=new DeviceInfoModule();
	    dim.setBuild_board(Build.BOARD);
	    dim.setBuild_bootloader(Build.BOOTLOADER);
	    dim.setBuild_brand(Build.BRAND);
	    dim.setBuild_cpu_abi(Build.CPU_ABI);
	    dim.setBuild_cpu_abi2(Build.CPU_ABI2);
	    dim.setBuild_device(Build.DEVICE);
	    dim.setBuild_display(Build.DISPLAY);
	    dim.setBuild_fingerprint(Build.FINGERPRINT);
	    dim.setBuild_hardware(Build.HARDWARE);
	    dim.setBuild_host(Build.HOST);
	    dim.setBuild_id(Build.ID);
	    dim.setBuild_manufacturer(Build.MANUFACTURER);
	    dim.setBuild_model(Build.MODEL);
	    dim.setBuild_product(Build.PRODUCT);
	    String serial=null ;
	       try {
	            Class<?> c = Class.forName("android.os.SystemProperties");
	            Method get = c.getMethod("get", String.class);
	            serial= (String) get.invoke(c, "ro.serialno");
	        } catch (Exception ignored) {
	        }
	    dim.setBuild_serial(serial);
	    dim.setBuild_tags(Build.TAGS);
	    dim.setBuild_type(Build.TYPE);
	    dim.setBuild_getradioversion(Build.getRadioVersion());
	    TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    dim.setDeviceid(tel.getDeviceId());
	    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    dim.setMacaddress(wifi.getConnectionInfo().getMacAddress());
	    dim.setAndroid_id(Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID));
	    dim.setNetworkoperator(tel.getNetworkOperatorName());
	    dim.setVersion_codes_base(String.valueOf(android.os.Build.VERSION_CODES.BASE));
	    dim.setUser(android.os.Build.USER);
	    dim.setDevicesoftwareversion(tel.getDeviceSoftwareVersion());
	    dim.setLine1number(tel.getLine1Number());
	    dim.setNetworkcountryiso(tel.getNetworkCountryIso());
	    dim.setNetworkoperator(tel.getNetworkOperator());
	    dim.setNetworktype(String.valueOf(tel.getNetworkType()));
	    dim.setPhonetype(String.valueOf(tel.getPhoneType()));
	    dim.setSimcountryiso(tel.getSimCountryIso());
	    dim.setSimoperator(String.valueOf(tel.getSimOperator()));
	    dim.setSimoperatorname(tel.getSimOperatorName());
	    dim.setSimserialnumber(tel.getSimSerialNumber());
	    dim.setSimstate(String.valueOf(tel.getSimState()));
	    dim.setSubscriberid(tel.getSubscriberId());
	    dim.setVoicemailnumber(tel.getVoiceMailNumber());
	    Gson gson=new Gson();
	    String jsonStr=gson.toJson(dim);
	    return jsonStr;
	}
}
