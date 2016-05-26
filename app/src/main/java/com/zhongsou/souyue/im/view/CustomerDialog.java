package com.zhongsou.souyue.im.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

/**
 * 卸载确认dialog
 * 
 * @author wangqiang
 * 
 */
public class CustomerDialog extends Dialog {

	private static int default_width = LayoutParams.WRAP_CONTENT;
	private static int default_height = LayoutParams.WRAP_CONTENT;

	public CustomerDialog(Context context, int layout, int style,int gravity) {
		this(context, default_width, default_height, layout, style, false,gravity);
	}

	public CustomerDialog(Context context, int layout, int width, int style,int gravity) {
		this(context, width, default_height, layout, style, false,gravity);
	}

	public CustomerDialog(Context context, int width, int height, int layout,
			int style, boolean isFullScreen,int gravity) {
		super(context, style);
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		float density = getDensity(context);
		// 代码修改，FILL_PARENT也会留出一个边
		if (width < 0) {
			int[] widthAndHeight = getSrceenPixels(context);
			if (isFullScreen) {
				params.width = (int) (widthAndHeight[0]);
			} else {
				params.width = (int) (widthAndHeight[0] - 20 * density);
			}
		} else {
			params.width = (int) (width * density);
			// params.width = width;
		}

		if (height < 0) {
			params.height = default_height;
		} else {
			params.height = (int) (height * density);
		}
		params.gravity = gravity;
		window.setAttributes(params);
	}

	public CustomerDialog(Context context, int width, int layout, int style,
			boolean isFullScreen) {
		super(context, style);
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		float density = getDensity(context);
		// 代码修改，FILL_PARENT也会留出一个边
		int[] widthAndHeight = getSrceenPixels(context);
		params.width = (int) (widthAndHeight[0] - width * density);
		params.height = default_height;

		if (isFullScreen) {
			params.width = (int) widthAndHeight[0];
			params.height = default_height;
		}
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}

	private float getDensity(Context context) {
		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		return dm.density;
	}

	private int[] getSrceenPixels(Context context) {
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) (context
				.getSystemService(Context.WINDOW_SERVICE));
		windowManager.getDefaultDisplay().getMetrics(displaysMetrics);
		int[] widthAndHeight = new int[2];
		widthAndHeight[0] = displaysMetrics.widthPixels;
		widthAndHeight[1] = displaysMetrics.heightPixels;
		return widthAndHeight;
	}
	
}