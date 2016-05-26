package com.zhongsou.souyue.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
//import com.alibaba.fastjson.JSONObject;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DeviceInfo {
    /**
     * 客户端信息
     *
     * @author wanglong@zhongsou.com YanBin
     */
    public static String appName = CommonStringsApi.APP_NAME_SHORT;
    public static String deviceName = Build.MODEL; // 硬件设备名称：三星Note2
    public static String osName = "Android"; // 操作系统名：Android
    public static String osVersion = Build.VERSION.RELEASE; // 版本呢：2.3.4
    public static String manufacturer = Build.MANUFACTURER;   // 硬件制造商
    public static String fingerprint = Build.FINGERPRINT;     //硬件识别码
    public static String brand = Build.BRAND;     //系统定制商


    public static String getAppVersion() {
        try {
            if (ConfigApi.isSouyue()) {
                return getAppVersionName();
            } else {
                return "4.1";
            }
        } catch (Exception e) {
        }
        return "";
    }

    public static String getAppVersionName() {
        try {
            return MainApplication.getInstance().getPackageManager().getPackageInfo(MainApplication.getInstance().getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static int getAppVersionCode() {
        try {
            return MainApplication.getInstance().getPackageManager().getPackageInfo(MainApplication.getInstance().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    // 获得浮点版本号
    public static String getFloatVersion() {
        try {
            String version = MainApplication.getInstance().getPackageManager().getPackageInfo(MainApplication.getInstance().getPackageName(), 0).versionName;
            version = version.replace(".", "");
            version = "0." + version;
            return version;
        } catch (Exception e) {
        }
        return "0.00";
    }

    public static String getDeviceId() {
        try {
            return DeviceUtil.getDeviceId(MainApplication.getInstance());
        } catch (Exception e) {

        }
        return "";
    }

    /**
     * 获得设备地理位置
     *
     * @return
     */
    public static Double[] getLocation() {
        Double location[] = new Double[]{0.0, 0.0};
        try {
            LocationManager mgr = (LocationManager) MainApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
            Location loc = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                loc = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            location[0] = loc.getLongitude();
            location[1] = loc.getLatitude();
        } catch (Exception ex) {
        }
        return location;
    }

    /**
     * 获得运营商名称：联通，电信，移动
     *
     * @return
     */
    public static String getNetworkOperatorName() {
        try {
            TelephonyManager tm = (TelephonyManager) MainApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getNetworkOperatorName();
        } catch (Exception ex) {
        }
        return "";
    }

    /**
     * 获得运营商标识
     *
     * @return
     */
    public static String getIMSI() {
        try {
            TelephonyManager tm = (TelephonyManager) MainApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        } catch (Exception ex) {
        }
        return "";
    }

    /**
     * 获得网络类型，wifi，gprs。。。
     *
     * @return
     */
    public static String getNetworkType() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MainApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info == null ? "" : info.getTypeName();
        } catch (Exception ex) {
        }
        return "";
    }

    public static String getUmengChannel(Context context) {
        try {
            String channel = getMetaData(context).getString("UMENG_CHANNEL");
            Log.i("", "UMENG_CHANNEL," + channel);
            return channel;
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
        return "";
    }


    private static Bundle getMetaData(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCityName() {
        return SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_CITY, "");
    }

    
    /**
     * 屏幕尺寸
     * @return
     */
    public static String getScreenSize() {
        try {
        	DisplayMetrics dm = MainApplication.getInstance().getResources().getDisplayMetrics();  
        	return String.valueOf(dm.widthPixels)+"*"+String.valueOf(dm.heightPixels);
        } catch (Exception ex) {}
        return "";
    }

    /**
     * 获得屏幕高度（像素）
     * @return
     */
    public static int getScreenHeight() {
        try {
        	DisplayMetrics dm = MainApplication.getInstance().getResources().getDisplayMetrics();  
        	return dm.heightPixels;
        } catch (Exception ex) {}
        return 0;
    }

    /**
     * 获得屏幕宽度（像素）
     * @return
     */
    public static int getScreenWidth() {
        try {
            DisplayMetrics dm = MainApplication.getInstance().getResources().getDisplayMetrics();
            return dm.widthPixels;
        } catch (Exception ex) {}
        return 0;
    }
    /**
     * 本机ip
     * @return
     */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (intf.getName().toLowerCase().equals("eth0")
						|| intf.getName().toLowerCase().equals("wlan0")) {
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ipaddress = inetAddress.getHostAddress()
									.toString();
							if (!ipaddress.contains("::")) {// ipV6的地址
								return ipaddress;
							}
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}


    private static boolean isFastMobileNetwork(Context context) {

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

    public static final int NETWORKTYPE_INVALID = 0;
    public static final int NETWORKTYPE_2G = 1;
    public static final int NETWORKTYPE_3G = 2;
    public static final int NETWORKTYPE_4G = 3;
    public static final int NETWORKTYPE_WIFI = 4;

    public static int getNetWorkType(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        int mNetWorkType = NETWORKTYPE_INVALID;

        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();

            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();

                mNetWorkType = TextUtils.isEmpty(proxyHost)
                        ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)
                        : NETWORKTYPE_4G;
            }
        } else {
            mNetWorkType = NETWORKTYPE_INVALID;
        }

        return mNetWorkType;
    }
    
    /**
     * 客户端字体
     * @return
     */
    public static JSONObject getSize() {
		JSONObject json = new JSONObject();
        try {
            int size = SYSharedPreferences.getInstance().getWebViewFont();
            if (size == 100)
                json.put("fontsize", "middle");
            if (size == 150)
                json.put("fontsize", "big");
            if (size == 75)
                json.put("fontsize", "small");
            Slog.d("callback", json.toString());

        }catch (Exception e){
        }
        return json;
	}


    //获取用户自己安装的App
    public static String getAppList(Context ctx){
        List<PackageInfo> packages = ctx.getPackageManager().getInstalledPackages(0);
        ArrayList<String> appList = new ArrayList<String>();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            	CharSequence appName = packageInfo.applicationInfo.loadLabel(ctx.getPackageManager());
            	if(appName != null && !"".equals(appName)) {
            		 appList.add(appName.toString());
            	}
            }
        }
        String result = appList.toString();
        if(StringUtils.isNotEmpty(result)) {
        	result = result.substring(1, result.length() -1);
        }
        return result;
    }

    /**
     * 获取用户安装的App列表，返回data格式
     * @param context
     * @return
     */
    public static List<AppData> getAppData(Context context){
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        ArrayList<AppData> appList = new ArrayList<AppData>();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                CharSequence appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager());
                String packageName = packageInfo.applicationInfo.packageName;
                if(appName != null && !"".equals(appName) && packageName != null && !TextUtils.isEmpty(packageName)) {
                    AppData d = new AppData();
                    d.setName(appName.toString());
                    d.setPkg(packageName);
                    appList.add(d);
                }
            }
        }
        return appList;
    }
}
