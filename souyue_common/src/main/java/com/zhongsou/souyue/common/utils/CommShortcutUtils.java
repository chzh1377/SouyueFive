package com.zhongsou.souyue.common.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap;

/**
 * 快捷方式提供者
 * @author chz
 *
 */
public class CommShortcutUtils {
	
	private static final String INSTALL_SHORTCUT_INTENT = "com.android.launcher.action.INSTALL_SHORTCUT";
	
	/**
	 * 添加应用的快捷方式
	 * @param activityLauncher
	 * @param shortcutTitle
	 * @param defaultLogo
	 */
	public static void addShortCut(Activity activityLauncher,String shortcutTitle,int defaultLogo) {
		Intent shortcut = new Intent(INSTALL_SHORTCUT_INTENT);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);
		shortcut.putExtra("duplicate", false); // 不允许重复创建
		Intent target = new Intent(Intent.ACTION_MAIN).setClassName(activityLauncher,
				activityLauncher.getClass().getName());
		target.addCategory(Intent.CATEGORY_LAUNCHER); // 防止重复启动
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activityLauncher,
				defaultLogo);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		activityLauncher.sendBroadcast(shortcut);
	}
	
	/**
	 * 添加其他类型的快捷方式
	 * @param context
	 * @param className
	 * @param shortcutTitle 
	 * @param bitmap  快捷方式图标
	 * @param defaultLogo 快捷方式默认图标
	 * @param extras 附加信息
	 */
	public static void addShortCut(Context context, String className,
			String shortcutTitle, Bitmap bitmap, int defaultLogo, Map<String, Object> extras) {
		Intent shortcut = new Intent(INSTALL_SHORTCUT_INTENT);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutTitle);
		shortcut.putExtra("duplicate", false); // 不允许重复创建
		Intent target = new Intent(Intent.ACTION_CREATE_SHORTCUT);
		if (extras != null && extras.size() > 0) {
			Set<String> keySet = extras.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object value = extras.get(key);
				target.putExtra(key, value == null ? "" : value.toString());
			}
		}
		target.setClassName(context, className);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		if (bitmap == null) {
			ShortcutIconResource iconRes = Intent.ShortcutIconResource
					.fromContext(context,defaultLogo);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		} else {
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
		}
		context.sendBroadcast(shortcut);
	}

}
