package com.tuita.sdk;

import java.util.HashSet;
import java.util.UUID;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
//import cn.jpush.android.api.JPushInterface;
//import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * @author wanglong@zhongsou.com
 */
public class DeviceUtil {
    private static final String LOGTAG = DeviceUtil.class.getSimpleName();
    private static WifiManager wifiMgr;
    private static TelephonyManager telMgr;
    public static String manufacturer = Build.MANUFACTURER;   // 硬件制造商
    public static String fingerprint = Build.FINGERPRINT;     //硬件识别码
    public static String brand = Build.BRAND;     //系统定制商

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
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        try {
            String imei = getIMEI(context);
            if (!isEmpty(imei)) {
                return imei;
            }
            imei = getMacAddr(context);
            if (!isEmpty(imei)) {
                return imei;
            }
            imei = getSIMNum(context);
            if (!isEmpty(imei)) {
                return imei;
            }
            imei = getUUID(context);
            if (!isEmpty(imei)) {
                return imei;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //	获取UUID
    public static String getUUID(Context context) {
        // SIM SN
        SharedPreferences pref = context.getSharedPreferences(TuitaSDKManager.TAG, Context.MODE_PRIVATE);
        String imei = null;
        if (pref != null) {
            imei = pref.getString("UUID", null);
        }
        if (imei != null) {
            return "UUID:" + imei;
        }
        String uuid = UUID.randomUUID().toString();
        pref.edit().putString("UUID", uuid).commit();
        return "UUID:" + uuid;

    }

    //获取MAC地址
    public static String getMacAddr(Context context) {

        SharedPreferences pref = context.getSharedPreferences(TuitaSDKManager.TAG, Context.MODE_PRIVATE);
        String imei = null;
        if (pref != null) {
            imei = pref.getString("macAddr", null);
        }
        if (imei != null) {
            return "MAC:" + imei;
        }
        initManager(context);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        Log.i(LOGTAG, "wifiInfo is " + wifiInfo);
        if (wifiInfo != null) {
            String macAddr = wifiInfo.getMacAddress();
//            Log.i(LOGTAG, "macAddr is " + macAddr);
            if (!isInvalid(macAddr) && checkMac(macAddr)) {
                pref.edit().putString("macAddr", macAddr).commit();
                return "MAC:" + macAddr;
            }
        }
        return null;
    }

    //获取sim卡号
    public static String getSIMNum(Context context) {
        // SIM SN
        SharedPreferences pref = context.getSharedPreferences(TuitaSDKManager.TAG, Context.MODE_PRIVATE);
        String imei = null;
        if (pref != null) {
            imei = pref.getString("simSN", null);
        }
        if (imei != null) {
            return "SIMSN:" + imei;
        }
        initManager(context);
        if (telMgr != null) {
            String simSN = telMgr.getSimSerialNumber();
//            Log.i(LOGTAG, "simSN is " + simSN);
            if (!isInvalid(simSN) && checkImsi(simSN)) {
                pref.edit().putString("simSN", simSN).commit();
                return "SIMSN:" + simSN;
            }
        }
        return null;
    }

    //获取设备号
    public static String getIMEI(Context context) {
        SharedPreferences pref = context.getSharedPreferences(TuitaSDKManager.TAG, Context.MODE_PRIVATE);
        String imei = null;
        if (pref != null) {
            imei = pref.getString("imei", null);
        }
        if (imei != null) {
            return imei;
        }
        initManager(context);
        if (telMgr != null) {//IMEI
            imei = telMgr.getDeviceId();
            if (!isInvalid(imei) && checkImei(imei)) {
                pref.edit().putString("imei", imei).commit();
                return imei;
            }
        }
        return null;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    private static boolean isInvalid(String str) {
        return str == null || str.trim().equals("") || str.trim().length() < 5;
    }

    /**
     * 检查手机串号是否有效（传进来时不能带字符串前缀）
     * 返回true为有效
     *
     * @param str
     * @return
     */
    private static boolean checkImei(String str) {
        if (!isEmpty(str)) {
            if (str.length() < 15) {
                return false;
            }
            HashSet set = new HashSet();
            for (int i = 0; i < str.length(); i++) {
                set.add(str.charAt(i));
            }
            if (set.size() <= 2) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查mac是否有效
     * 返回true为有效
     * 传进来参数的时候不能加前缀
     */
    private static boolean checkMac(String str) {
        if (!isEmpty(str)) {
            HashSet set = new HashSet();
            for (int i = 0; i < str.length(); i++) {
                set.add(str.charAt(i));
            }
            if (set.size() <= 2) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查sim卡串号是否有效（传进来时不能带字符串前缀）
     * 返回true为有效
     *
     * @param str
     * @return
     */
    private static boolean checkImsi(String str) {
        if (!isEmpty(str)) {

            if (str.equals("012345678901234")) {
                return false;
            }
            HashSet set = new HashSet();
            for (int i = 0; i < str.length(); i++) {
                set.add(str.charAt(i));
            }
            if (set.size() <= 2) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取
     *
     * @return
     */
    public static String getDeviceName() {
        Log.d("tuitaDeviceInfo", "manufacturer------>" + manufacturer);
        Log.d("tuitaDeviceInfo", "brand------>" + brand);
        Log.d("tuitaDeviceInfo", "fingerprint------>" + fingerprint);
//        Log.d("tuitaDeviceInfo", "获取版本号------>" + android.os.Build.VERSION.RELEASE);
//        Log.d("tuitaDeviceInfo", "获取手机型号------>" + android.os.Build.MODEL );
        return manufacturer;

    }

    /**
     * 获取设备注册的推送
     *
     * @return 1 : xiaomi ; 2 : else
     *
     */
    public static int getPushChannel() {
        String xiaomi = "xiaomi";
        boolean ret = fingerprint.toLowerCase().contains(xiaomi)
                || brand.toLowerCase().contains(xiaomi)
                || manufacturer.toLowerCase().contains(xiaomi);

        return ret ? 1 : 2;
    }

    /**
     * 根据手机平台获得推送的注册ID
     *
     * @param context 上下文
     * @return regID
     */
    public static String getPushRegID(Context context) {
        String regID = null;
        try {
            if (getPushChannel() == 1) {
//                regID = MiPushClient.getRegId(context);
            } else {
//                regID = JPushInterface.getRegistrationID(context);
            }
        } catch (Exception e) {
            Log.e("Tuita DeviceUtil", "getPushRegID Exception !!!!");
        }
        return regID;
    }
}
