package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SplashActivity;
import com.zhongsou.souyue.platform.CommonStringsApi;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wanglong@zhongsou.com
 * 
 */
public class ActivityUtils {





	/**
	 * 创建桌面图标
	 * 
	 * @param activityLauncher
	 */
    public static void addShortCut(Activity activityLauncher) {

        PackageManager packageManager = MainApplication.getInstance().getPackageManager();  //包管理器
//        Log.d("shortcut_yanbin", new SplashActivity().getTitle().toString());PackageManager pm = getPackageManager();
        ActivityInfo activityInfo = null ;  //AndroidManifest中Activity节点信息类
        String shortName = CommonStringsApi.APP_NAME;   //快捷方式名称，默认APP_NAME

        try{
            activityInfo = packageManager.getActivityInfo(new ComponentName(MainApplication.getInstance().getPackageName(), SplashActivity.class.getName()),0);
            shortName = activityInfo.loadLabel(packageManager).toString();  //获得Splash节点下的label
        }catch (Exception e){
            Log.e("ActivityUtils", "addShrotCut Exception");
        }

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortName);
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        Intent target = new Intent(Intent.ACTION_MAIN).setClassName(activityLauncher,
                activityLauncher.getClass().getName());
        target.addCategory(Intent.CATEGORY_LAUNCHER); // 防止重复启动
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activityLauncher,
                R.drawable.logo);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        activityLauncher.sendBroadcast(shortcut);
    }
//
//	/**
//     * 创建桌面图标
//     *
//     * @param
//     */
//    public static void addSRPShortCut(Context context, String className,String keyword,String srpId,String url,Bitmap bitmap,String shortcutTitle) {
//        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        if(TextUtils.isEmpty(shortcutTitle)){
//            shortcutTitle = keyword;
//        }
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);
//        shortcut.putExtra("duplicate", false); // 不允许重复创建
//        Intent target = new Intent(Intent.ACTION_CREATE_SHORTCUT);
////      target.putExtra("keyword","啤酒");
////      target.putExtra("srpId","9367e9b9faf42aee65740f706834a3d4");
//        target.putExtra("from","shortcut");
//        target.putExtra("keyword",keyword);
//        target.putExtra("srpId",srpId);
////      target.putExtra("url",url);
//        target.setClassName(context,className);
////        target.addCategory(Intent.CATEGORY_LAUNCHER); // 防止重复启动
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
//        if(bitmap==null){
//            ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(MainApplication.getInstance(),
//                    R.drawable.logo);
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
//        }else{
//          shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,bitmap);
//        }
//
//        context.sendBroadcast(shortcut);
//    }


    public static String getSignature() {
//        StringBuilder builder = new StringBuilder();
        String str = "";
        try {
            PackageManager manager = MainApplication.getInstance().getPackageManager();
            /** 通过包管理器获得指定包名包含签名的包信息 **/
            PackageInfo packageInfo = manager.getPackageInfo(MainApplication.getInstance().getPackageName(), PackageManager.GET_SIGNATURES);
            /******* 通过返回的包信息获得签名数组 *******/
            Signature[] signatures = packageInfo.signatures;
            /******* 循环遍历签名数组拼接应用签名 *******/
//            for (Signature signature : signatures) {
//                builder.append(signature.toCharsString());
//            }
            str= parseSignature(signatures[0].toByteArray());
            /************** 得到应用签名 **************/
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String parseSignature(byte[] signature) {
        String result = "";
        String pub="";
        try {
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            pub=pubKey.substring("OpenSSLRSAPublicKey".length());
            JSONObject json = new JSONObject(pub);
            result=json.getString("modulus");
//            System.out.println("pubKey:" + p);
//            System.out.println("pubKey:" + p.length());
//            System.out.println("signNumber:" + signNumber);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            int index = pub.indexOf(':');
            int last = pub.indexOf('\n');
            result = pub.substring(index+1,last);
//            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param context
     * @param className 启动的Activity
     * @param bitmap    快捷方式的icon
     * @param shortcutTitle 快捷方式的名称
     * @param extras    参数列表
     */
    public static void addShortCut(Context context,String className,Bitmap bitmap,String shortcutTitle, Map<String,Object> extras) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);
        shortcut.putExtra("duplicate", false); // 不允许重复创建
        Intent target = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        if(extras != null && extras.size() > 0){
            Set<String> keySet = extras.keySet();
            Iterator<String> it = keySet.iterator();
            while (it.hasNext()) {
                String key = it.next();
                target.putExtra(key, extras.get(key).toString());
            }
        }
        target.setClassName(context,className);
//        target.addCategory(Intent.CATEGORY_LAUNCHER); // 防止重复启动
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        if(bitmap==null){
            ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(MainApplication.getInstance(),R.drawable.shortcut_logo);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        }else{
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON,bitmap);
        }
        context.sendBroadcast(shortcut);
    }


	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * 
	 * @param context
	 *            The application's environment.
	 * @param action
	 *            The Intent action to check for availability.
	 * 
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 * 
	 * @param context
	 *            The application's environment.
	 * @param packName
	 *            The packName to check for availability.
	 * @param actvityName
	 *            The Main activity to check for start.
	 * 
	 * @return True if an apk with the specified packName and actvityName can be sent and
	 *         responded to, false otherwise.
	 */
	public boolean checkIfPkgInstalled(Context context, String packName, String actvityName) {
		boolean flag = false;
		PackageManager manager = context.getPackageManager();
		// 只查找启动方式为LAUNCHER并且是ACTION_MAIN的APP
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 根据Intent值查询这样的app
		final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);

		for (ResolveInfo app : apps) {
			// 该应用的包名和主Activity
			String pkg = app.activityInfo.packageName;
			String cls = app.activityInfo.name;
			if (pkg.equals(packName) || cls.equals(actvityName)) {
				flag = true;
				try {
					PackageInfo info = manager.getPackageInfo(pkg, 0);
					String mVersonName = info.versionName;
					int versionCode = info.versionCode;
					LogDebugUtil.v("fanxing", "versionCode:" + versionCode + "=====mVersonName:" + mVersonName);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return flag;
	}

}
