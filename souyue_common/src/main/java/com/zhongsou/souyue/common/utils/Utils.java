package com.zhongsou.souyue.common.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;

public class Utils {
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp,字体的转换
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素),字体的转换
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率获取转义后的各种类型的值
     *
     * @param util  要转换的类型：@link TypedValue.COMPLEX_UNIT_PX TypedValue.COMPLEX_UNIT_DIP TypedValue.COMPLEX_UNIT_SP
     *              TypedValue.COMPLEX_UNIT_PT TypedValue.COMPLEX_UNIT_IN TypedValue.COMPLEX_UNIT_MM
     * @param value
     * @return
     */
    public static float applyDimension(int util, int value) {
        Resources r;
        r = Resources.getSystem();
        return TypedValue.applyDimension(util, value, r.getDisplayMetrics());
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected();
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }

    /**
     * 获取一个圆角图片.
     *
     * @param bitmap ,原图，圆角大小
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas      canvas  = new Canvas(output);
        final int   color   = 0xff424242;
        final Paint paint   = new Paint();
        final Rect  rect    = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF   = new RectF(rect);
        final float roundPx = radius;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static CommVarible.WifiState getNetWorkState(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);

            //mobile 3G Data Network
            NetworkInfo.State mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
//wifi
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();


            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (mobile.equals(NetworkInfo.State.CONNECTED)) {
                return CommVarible.WifiState.STATE_3G;
            } else if (wifi.equals(NetworkInfo.State.CONNECTED)) {
                return CommVarible.WifiState.STATE_WIFI;
            } else if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
                return CommVarible.WifiState.STATE_OTHER;
            } else {
                return CommVarible.WifiState.STATE_NONCONNECT;
            }
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return CommVarible.WifiState.STATE_NONCONNECT;
    }

    public static ColorStateList createColorStateList(int normal, int pressed, int focused, int unable) {
        int[]   colors = new int[]{pressed, focused, normal, focused, unable, normal};
        int[][] states = new int[6][];
        states[0] = new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled};
        states[1] = new int[]{android.R.attr.state_enabled, android.R.attr.state_focused};
        states[2] = new int[]{android.R.attr.state_enabled};
        states[3] = new int[]{android.R.attr.state_focused};
        states[4] = new int[]{android.R.attr.state_window_focused};
        states[5] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
}
