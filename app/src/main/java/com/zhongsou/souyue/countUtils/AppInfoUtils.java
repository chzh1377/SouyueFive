package com.zhongsou.souyue.countUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.SYUserManager;

import java.math.BigInteger;
import java.security.MessageDigest;

public class AppInfoUtils {

	/**
	 * 获得App版本
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		String version = null;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			version = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}
	
	/**
	 * 获得App包名
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		String packageName = null;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			packageName = info.packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packageName;
	}

	/**
	 * 获得App版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			versionCode = info.versionCode;
		} catch (Exception e) {

		}
		return versionCode;
	}

	private static final String LOGTAG = "AppInfoUtils";
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


	private static final String SP_NAME = "souyue"; // share名称
//	public static final String KEY_UUID = "UUID";// UUID

//	private static String getUUID(Context context) {
//		String uuid = null;
//		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
//				Context.MODE_PRIVATE);
//		if (sp != null) {
//			uuid = sp.getString(KEY_UUID, "");
//			if (isInvalid(uuid)) {
//				uuid = UUID.randomUUID().toString();
//				// 这里少做了一步把当前获得的UUID添加到SHare当中 因为怕出什么差错导致源程序UUID混乱
//				return uuid;
//			}
//		}
//		return "";
//	}

	/**
	 * 有效性 检测
	 * 
	 * @param str
	 * @return 不可用返回true
	 */
	private static boolean isInvalid(String str) {
		return str == null || str.trim().equals("") || str.trim().length() < 5;
	}

	/**
	 * 网络类型判断，
	 * 
	 * @param context
	 * @return 有则返回正常，无网络则返回“”；
	 */
	public static String networkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			if (networkInfo != null) {
				return parseNetStatus(networkInfo);
			}
		}

		return "";
	}

	/**
	 * 获取网络类型名称 eg:WIFI or Mobile
	 * 
	 * @param networkInfo
	 * @return
	 */
	private static String parseNetStatus(NetworkInfo networkInfo) {
		if (networkInfo == null) {
			return "";
		}
		return networkInfo.getTypeName();
	}

	/**
	 * 获取网络运营商代码
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetWorkOperator(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			return tm.getNetworkOperator();
		}
		return "";
	}

	/**
	 * 获取设备mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceMac(Context context) {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wm != null) {
			return wm.getConnectionInfo().getMacAddress();
		}
		return "";
	}

	/**
	 * 得到当恰手机 sdk版本号
	 * 
	 * @return
	 */
	public static String getAndroidSDKVersion() {
		return android.os.Build.VERSION.SDK_INT + "";
	}

	/**
	 * 得到当前手机SDK版本名称
	 * 
	 * @return
	 * 
	 *         eg:MI 3,19,4.4.2
	 */
	public static String getAndroidSDKVersionName() {
		// android.os.Build.MODEL + ","+ android.os.Build.VERSION.SDK + "," +
		return android.os.Build.VERSION.RELEASE;

	}

	/**
	 * 得到当前手机屏幕分辨率
	 * 
	 * @param context
	 * @return
	 */
	public static String getResolution(Context context) {
		WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth() + "*" + display.getHeight();
	}

	/**
	 * 得到设备唯一ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getDevice_id(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			return tm.getDeviceId();
		}
		return "";

	}

	/**
	 * 得到设备名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getDevice_name(Context context) {
		return android.os.Build.MODEL;
	}

	/**
	 * 得到SDKVersion
	 * 
	 * @param context
	 * @return
	 */
	public static String getSDK_Version222222222(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			return tm.getDeviceSoftwareVersion();
		}

		return "";
	}

	/**
	 * 获得系统当纬度方法
	 * 
	 * @param context
	 * @return 纬度
	 */
	public static final String KEY_LAT = "KEY_LAT";

	public static String getLat(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		if (sp != null) {
			return sp.getString(KEY_LAT, "0.0");
		}
		return "";
	}

	/**
	 * 获得当前经度方法
	 * 
	 * @param context
	 * @return 经度
	 */
	public static final String KEY_LNG = "KEY_LNG";

	public static String getLng(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		if (sp != null) {
			return sp.getString(KEY_LNG, "0.0");
		}
		return "";
	}

	/**
	 * 获得当前城市方法
	 * 
	 * @param context
	 * @return 当前城市名称
	 */
	public static final String KEY_CITY = "KEY_CITY";

	public static String getCity(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
		if (sp != null) {
			return sp.getString(KEY_CITY, "null");
		}
		return "";
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
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (info != null) {
			return info.metaData.getString("UMENG_CHANNEL");
		}
		return "";
	}

	/**
	 * 获得Uid
	 * 
	 * @return
	 */
	public static String getUid() {
		return SYUserManager.getInstance().getUserId();
	}

	/**
	 * 是否是游客
	 * 
	 * @return
	 */
	public static boolean getGuest() {
		if ("1".equals(SYUserManager.getInstance().getUserType())) {
			return false;
		}
		return true;
	}

	/**
	 * 获取应用程序名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		String app_name = context.getString(R.string.app_name);
		if (app_name != null) {
			return app_name;
		}
		return "";
	}

	/**
	 * md5加密
	 * @param password
	 * @return 32位明文
	 */

	public static String makeMD5(String password) {
		MessageDigest md;
		try {
			// 生成一个MD5加密计算摘要
			md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(password.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String pwd = new BigInteger(1, md.digest()).toString(16);
			while(pwd.length()<32){
				pwd="0"+pwd;
			}
			return pwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return password;
	}
	
	/**
    *
    * @param plainText
    *            明文
    * @return 32位密文
    */
//   public static String encryption(String plainText) {
//       String re_md5 = new String();
//       try {
//           MessageDigest md = MessageDigest.getInstance("MD5");
//           md.update(plainText.getBytes());
//           byte b[] = md.digest();
//
//           int i;
//
//           StringBuffer buf = new StringBuffer("");
//           for (int offset = 0; offset < b.length; offset++) {
//               i = b[offset];
//               if (i < 0)
//                   i += 256;
//               if (i < 16)
//                   buf.append("0");
//               buf.append(Integer.toHexString(i));
//           }
//
//           re_md5 = buf.toString();
//
//       } catch (NoSuchAlgorithmException e) {
//           e.printStackTrace();
//       }
//       return re_md5;
//   }

}
