package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.platform.CommonStringsApi;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/*
 * zhangliang01@zhongsou.com
 */
public class Utils {

    public static final int TIME_SPACE = 600 * 1000;

    public static final long MAX_LONG = 0X7FFFFFFFFFFFFFFFL;

    public static final String[] UN_ENCRYPT_URLS = {UrlConfig.getSouyueHost()};

    /**
     * 额外需要加密的url
     */
    public static final String[] EXTRA_URLS = {
            UrlConfig.HOST_ZHONGSOU_REWARDS, UrlConfig.ADMINTOOL,UrlConfig.CIRCLE_TOOL,
            UrlConfig.saveSendInfo, UrlConfig.getSendCoinUrl(),
            UrlConfig.credits_exchange, UrlConfig.HOST_ZHONGSOU_JF_BLANCE,
            UrlConfig.HOST_ZHONGSOU_COINS_BLANCE, UrlConfig.gift,
            /*UrlConfig.return_visit,*/ UrlConfig.integral, UrlConfig.ZHONGSOU_HD,
            UrlConfig.HOST_ZHONGSOU_COINS_BLANCE,
            UrlConfig.HOST_ZHONGSOU_JF_BLANCE, UrlConfig.SecurityCenter,
            UrlConfig.CommonRegister, UrlConfig.EquipmentTest,
            UrlConfig.ForgetPassword, UrlConfig.HOST_ZHONGSOU_JF,
            UrlConfig.share_result,UrlConfig.getSendRedPacketUrl()};
    /**
     * 要剔除的URL
     */
    public static final String[] REJECT_URLS = {UrlConfig.shortURL,
            UrlConfig.urlContent,

    };


    public static Map<String, Toast> mMapToasts = new HashMap<String, Toast>();
    public static Map<String, Long> mTimeMap = new HashMap<String, Long>();

    public static int mStutas;// 状态栏高度
    public static int mMenuHeight;// 状态栏高度

    /**
     * get Screen Width
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * get Screen height
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * get Screen Width
     *
     * @return
     */
    public static int getScreenWidth() {
        DisplayMetrics dm = MainApplication.getInstance().getResources()
                .getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * get Screen height
     *
     * @return
     */
    public static int getScreenHeight() {
        DisplayMetrics dm = MainApplication.getInstance().getResources()
                .getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * check sdcard is exist
     */
    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static boolean isIntentSafe(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(
                intent, 0);
        return activities.size() > 0;
    }

    public static String getPicPathFromUri(Uri uri, Activity activity) {
        String value = uri.getPath();

        if (value.startsWith("/external")) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else {
            return value;
        }
    }

    public static void hideBuiltInZoomControls(View view) {
        try {
            Field field = WebView.class
                    .getDeclaredField("mZoomButtonsController");
            field.setAccessible(true);
            ZoomButtonsController zoomCtrl = new ZoomButtonsController(view);
            zoomCtrl.getZoomControls().setVisibility(View.GONE);
            field.set(view, zoomCtrl);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    // 终端设备的WH
    public static int[] getDeviceWH(Context context) {
        int[] wh = new int[2];
        int w = 0;
        int h = 0;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        w = dm.widthPixels;
        h = dm.heightPixels;
        wh[0] = w;
        wh[1] = h;
        return wh;
    }

    /**
     * 获取状态栏高度
     *
     * @param cx
     * @return
     */
    public static int getTitleBarHeight(Activity cx) {
        Rect frame = new Rect();
        cx.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * _s4加密
     *
     * @return
     */
    // public static String getRegisterSign(List<String> params) {
    // StringBuilder breakString=new StringBuilder();
    // Collections.reverse(params);
    // breakString.append("s1o2u3y4u5e");
    // Collections.sort(params, new Comparator<String>() {
    // public int compare(String o1, String o2) {
    // int result = o1.compareTo(o2);
    // return result;
    // }
    // });
    //
    // for(int j=0;j<params.size();j++){
    // breakString.append(params.get(j));
    // }
    // // return Md5Util.getMD5Str(breakString.toString());
    // return EncryptUtil.test(breakString.toString());
    // }

    // add by trade start
    public static String get2MD5(byte[] source) {
        String s = null;
        char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>>
                // 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            s = new String(str); // 换后的结果转换为字符串

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String get2MD5(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return get2MD5(s.getBytes());
    }

    public  static String getMD5Hex(String str){
        byte[] data = getMD5(str.getBytes());

        BigInteger bi = new BigInteger(data).abs();

        String result = bi.toString(36);
        return result;
    }

    private static byte[] getMD5(byte[] data){

        MessageDigest digest;
        try {
            digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(data);
            byte[] hash = digest.digest();
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加密geturl
     *
     * @param url
     * @return
     */
    public static String encryptWebUrl(String url) {
        // Map<String, Object> params = new HashMap<String, Object>();
        JSONObject json = new JSONObject();
        String u = url.substring(url.indexOf('?') + 1, url.length());
        String[] pas = u.split("&");
        StringBuilder p_en = new StringBuilder(url.substring(0,
                url.indexOf('?') + 1));
        try {
            for (String p : pas) {
                String[] ppp = p.split("=");
                if (ppp[0].equals("isEncryption") || ppp[0].equals("")) {
                    continue;
                } else if (ppp[0].equals("sy_c")) {// 若是传过来的参数里面包含sy_c，则表示此url已经加密，将不做加密处理
                    return url;
                } else {
                    if (ppp.length > 1) {
                        json.put(ppp[0], ppp[1]);
                    } else {
                        json.put(ppp[0], "");
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // params.put("vc",DeviceInfo.getAppVersion());
        String en = encode(ZSSecret.encrypt(json.toString()));
        p_en.append("sy_c=" + en);
        return p_en.toString();
    }

    public synchronized static String encryptGet(Map<String, String> params,
                                                 boolean isEncrypt) {

        JSONObject obj = new JSONObject();
        try {
            for (Map.Entry<String, String> en : params.entrySet()) {
                if (en.getKey().equals("vc")
                        || en.getKey().equals("isEncryption")) {
                    continue;
                } else {
                    if (en.getValue() == null) {
                        obj.put(en.getKey(), "");
                    } else {
                        obj.put(en.getKey(), en.getValue());
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder p_en = new StringBuilder();
        if (isEncrypt) {

            String en = encode(ZSSecret.encrypt(obj.toString()));
            if (en.equals("")) {
                Log.v("加密出错", "加密出错串：" + obj.toString());
            }
            Object oo = params.get("vc");
            if (oo != null) {
                p_en.append("vc=");
                p_en.append(oo.toString());
                p_en.append("&");
            }
            p_en.append("sy_c=" + en);
        } else {
            Object oo = params.get("vc");
            if (oo != null) {
                p_en.append("vc=");
                p_en.append(oo.toString());
                p_en.append("&");
            }
            Iterator keys = obj.keys();
            while (keys.hasNext()) {
                try {
                    String k = (String) keys.next();
                    String v = obj.getString(k);
                    p_en.append(k);
                    p_en.append("=");
                    p_en.append(Utils.encode(v));
                    p_en.append("&");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (p_en.length() > 0) {
                p_en.deleteCharAt(p_en.length() - 1);
            }
        }
        return p_en.toString();
    }

    /**
     * 编码
     *
     * @param str
     * @return
     */
    public static String encode(Object str) {
        try {
            return (str == null || str.toString().trim() == "") ? ""
                    : URLEncoder.encode(str.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    /**
     * 加密post请求方法
     *
     * @param _url
     * @param params post 参数
     * @param method 不是login
     * @return
     */
    public synchronized static List<BasicNameValuePair> encryptToPostEntity(String _url,
                                                                            Map<String, String> params, String method) {
        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        try {
            String pa = encryptPost(_url, params, method);
            if (pa == null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String bnv = "";
                    if (!StringUtils.isEmpty(entry.getValue())) {
                        if ("login".equals(method)) {
                            bnv = entry.getValue();
                        } else
                            bnv = entry.getValue().trim();
                    }
                    BasicNameValuePair bn = new BasicNameValuePair(
                            entry.getKey(), bnv);
                    pairs.add(bn);
                }
                BasicNameValuePair bn1 = new BasicNameValuePair("vc",
                        DeviceInfo.getAppVersion());
                pairs.add(bn1);
            } else {
                BasicNameValuePair bn = new BasicNameValuePair("sy_c", pa);
                pairs.add(bn);
                BasicNameValuePair bn1 = new BasicNameValuePair("vc",
                        DeviceInfo.getAppVersion());
                pairs.add(bn1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pairs;
    }

    public static String getNoParamsUrl(String url) {
        return url.split("\\?")[0];
    }

    public static String encryptPost(String _url, Map<String, String> params)
            throws Exception {
        return encryptPost(_url, params, "");
    }

    public static String encryptPost(String _url, Map<String, String> params,
                                     String method) throws Exception {

        if (!isEncryt(_url)) {
            return null;
        }
        String[] us = _url.split("\\?");
        String url_ps = null;
        if (us.length > 2) {

            throw new Exception("非法url！");
        } else if (us.length > 2) {
            url_ps = us[1];
        }
        JSONObject obj = new JSONObject();

        if (url_ps != null) {
            String url_params[] = url_ps.split("&");
            for (int i = 0; i < url_params.length; i++) {
                String[] kv = url_params[i].split("=");
                if (!kv[1].equals("vc")) {
                    obj.put(kv[1], kv[2]);
                }
            }
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String bnv = "";
            if (!StringUtils.isEmpty(entry.getValue())) {
                if ("login".equals(method)) {
                    bnv = entry.getValue();
                } else
                    bnv = entry.getValue().trim();
            }
            String k = entry.getKey();
            if (!k.equals("vc")) {
                obj.put(entry.getKey(), bnv);
            }
        }
        if (obj.isNull("imei")) {
            obj.put("imei", DeviceInfo.getDeviceId());
        }

        return ZSSecret.encrypt(obj.toString());
    }

    public static boolean isEncryt(String _url) {//判断当前url是否要加密
        boolean isEncrypt = false;
        for (int i = 0; i < UN_ENCRYPT_URLS.length; i++) {
            if (_url.contains(UN_ENCRYPT_URLS[i])) {
                isEncrypt = true;
            }
        }
        // 额外需要加密的url
        if (isExtraEncrypt(_url)) {
            return true;
        }
        // 额外需要剔除的url
        for (int i = 0; i < REJECT_URLS.length; i++) {
            if (_url.contains(REJECT_URLS[i])) {
                isEncrypt = false;
            }
        }
        return isEncrypt;
    }

    public static boolean isExtraEncrypt(String _url) {
        boolean isEncrypt = false;
        for (int i = 0; i < EXTRA_URLS.length; i++) {
            if (_url.contains(EXTRA_URLS[i])) {
                isEncrypt = true;
            }
        }
        return isEncrypt;
    }

    public static String encrypt(String s) {
        return ZSSecret.encrypt(s);
    }

    public static String encryptJs(String s) {
        return ZSSecret.enjs(s);
    }

    public static String Md5(String str) {
        if (str != null && !str.equals("")) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                        '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                byte[] md5Byte = md5.digest(str.getBytes("UTF8"));
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < md5Byte.length; i++) {
                    sb.append(HEX[(int) (md5Byte[i] & 0xff) / 16]);
                    sb.append(HEX[(int) (md5Byte[i] & 0xff) % 16]);
                }
                str = sb.toString();
            } catch (NoSuchAlgorithmException e) {
            } catch (Exception e) {
            }
        }
        return str;
    }

    /**
     * 广告点击默认点击“点”坐标
     *
     * @return
     */
    public static int[] getXYInScreen() {
        int[] touchLocation = new int[2];

        DisplayMetrics dm = MainApplication.getInstance().getResources()
                .getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        touchLocation[0] = screenWidth / 2;
        touchLocation[1] = screenHeight
                - DeviceUtil.dip2px(MainApplication.getInstance(), 50);
        Log.d("XY", touchLocation[0] + "," + touchLocation[1]);
        return touchLocation;
    }

    public static void makeToast(Context context, int strid) {
        final int tagid = strid;
        if (!mMapToasts.containsKey(tagid)) {
            Toast tos = Toast.makeText(context, context.getString(strid), Toast.LENGTH_SHORT);
            mMapToasts.put(strid + "", tos);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    mMapToasts.remove(tagid);
                }
            };
            Timer time = new Timer();
            time.schedule(task, 3000);
            tos.show();
        }
    }

    public static void makeToastTest(Context context, String test) {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
		if (env != UrlConfig.SOUYUE_ONLINE) {
			Log.e("错误", "error:" + test);
			Toast.makeText(context, test, Toast.LENGTH_SHORT).show();
		}
    }

    public static boolean isTimeExpire(String tag) {
        long pre = 0;
        if (mTimeMap.containsKey(tag)) {
            pre = mTimeMap.get(tag);
        }
        long time = System.currentTimeMillis();
        boolean isexpire;
        if (time - pre > TIME_SPACE) {
            isexpire = true;
        } else {
            isexpire = false;
        }
        return isexpire;
    }

    public static void resetTime(String tag) {
        mTimeMap.put(tag, System.currentTimeMillis());
    }

    public static void clearTimeMap() {
        mTimeMap.clear();
    }

    public static int getJsonValue(JsonObject _obj, String _key, int def)
            throws JSONException {
        if (!_obj.has(_key)) {
            return def;
        } else {
            return _obj.get(_key).getAsInt();
        }

    }

    public static long getJsonValue(JsonObject _obj, String _key, long def)
            throws JSONException {
        if (!_obj.has(_key)) {
            return def;
        } else {
            return _obj.get(_key).getAsLong();
        }

    }

    public static String getJsonValue(JsonObject _obj, String _key, String def)
            throws JSONException {
        if (!_obj.has(_key)) {
            return def;
        } else {
            return _obj.get(_key).getAsString();
        }

    }

    public static double getJsonValue(JsonObject _obj, String _key, double def)
            throws JSONException {
        if (!_obj.has(_key)) {
            return def;
        } else {
            return _obj.get(_key).getAsDouble();
        }

    }

    public static JsonObject getJsonValue(JsonObject _obj, String _key)
            throws JSONException {
        if (!_obj.has(_key)) {
            return null;
        } else {
            return _obj.get(_key).getAsJsonObject();
        }

    }

    public static boolean getJsonValue(JsonObject _obj, String _key, boolean def)
            throws JSONException {
        if (!_obj.has(_key)) {
            return def;
        } else {
            return _obj.get(_key).getAsBoolean();
        }

    }

    public static JsonArray getJsonArrayValue(JsonObject _obj, String _key)
            throws JSONException {
        if (!_obj.has(_key)) {
            return new JsonArray();
        } else {
            return _obj.get(_key).getAsJsonArray();
        }

    }

    private static final String VALINO_PATH = "/souyue/login/temp/valino/";
    private static final String FILE_VALINO = "valiNo";

    public static void storeValiNoFile(Context context, byte[] bytes) {
        // 判断sd卡是否存在
        FileOutputStream out;
        try {
            out = new FileOutputStream(valiNoFile(context));
            out.write(bytes);
            out.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private static File valiNoFile(Context context) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        File file;
        File fileValiNo;
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
            String path = sdDir.getAbsolutePath() + VALINO_PATH;
            file = new File(path);
            if (!file.exists())
                file.mkdirs();
            fileValiNo = new File(path + FILE_VALINO);
            if (!fileValiNo.exists()) {
                try {
                    fileValiNo.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fileValiNo;
        } else {
            file = getCachevaliNoDir(context);
            fileValiNo = new File(file.getAbsolutePath() + FILE_VALINO);
            if (!fileValiNo.exists()) {
                try {
                    fileValiNo.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return file;
        }
    }

    public static File getCachevaliNoDir(Context context) {
        File cacheDir = null;
        if (cacheDir == null) {
            cacheDir = new File(context.getCacheDir(), FILE_VALINO);
            cacheDir.mkdirs();
        }

        return cacheDir;

    }


    /**
     * add by yinguanping 获得RSA加密串
     *
     * @param data
     * @return
     */
    public static String getRSAResult(String data, String PRIVATE_KEY) {
        String source = "";// 拼接好要传给后台服务器的数据串
        String afterencrypt = "";// 返回加密后的RSA串
        byte[] encryptByte = null;

        // 从字符串中得到公钥
        PrivateKey privateKey = null;
        try {
            privateKey = RSAUtils.loadPrivateKey(PRIVATE_KEY);
            // 加密
            encryptByte = RSAUtils.encryptData(source.getBytes(), privateKey);
            // 为了方便观察吧加密后的数据用base64加密转一下，要不然看起来是乱码,所以解密是也是要用Base64先转换
            afterencrypt = Base64Utils.encode(encryptByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return afterencrypt;
    }

    public static boolean isWifiAble(Context context) {
        int networkType = DeviceInfo.getNetWorkType(context);
        return networkType == DeviceInfo.NETWORKTYPE_WIFI;
    }


    public static long getTimeMillis(String data, String format) {
        SimpleDateFormat mFormat = new SimpleDateFormat(format);
        Date date = null;
        if (StringUtils.isNotEmpty(data)) {
            try {

                date = mFormat.parse(data);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date.getTime();
        }
        return 0;
    }

    public static boolean isAppOnForeground(Context context) {
        try {
            ActivityManager mActivityManager = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE));
            List<RunningTaskInfo> tasksInfo = mActivityManager
                    .getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                Log.d("NOtificationMsgTwo",
                        "top Activity = "
                                + tasksInfo.get(0).topActivity.getPackageName());
                ComponentName componentName = tasksInfo.get(0).topActivity;
                // 应用程序位于堆栈的顶层
                if (context.getPackageName().equals(
                        componentName.getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获得手机状态栏高度
     * @param activity
     * @return
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }


    public String getMaxStringLength(String str, int max) {
        String result = str;
        int length = str.length();
        if (length < max) {
            str = result;
        } else {

        }
        return str;
    }

    public static final String doubleToString(double d) {
        String i = DecimalFormat.getInstance().format(d);
        String result = i.replaceAll(",", "");
        return result;
    }


    public static Map<String, String> convertStringToObject(Map<String, Object> par) {
        Map<String, String> params = new HashMap<String, String>();
        Object value;
        for (Map.Entry<String, Object> e : par.entrySet()) {
            value = e.getValue();
            if (value != null) {
                params.put(e.getKey(), value.toString());
            }
        }
        return params;
    }

    //----超A抽取方法-YanBin-------------

    /**
     * linkUrlWidget:针对h5微件进行处理 <br/>  From TradeCommonUtil
     *
     * @param url
     * @param param
     * @param value
     * @return
     * @author zhaobo
     * @date 2015-4-28 上午11:46:15
     */
    public static String linkUrlWidget(String url, String param, String value) {
        if (!url.contains(param + "=") && value != null
                && !StringUtils.isEmpty(value)) {
            if (url.contains("?")) {
                url += "&" + param + "=" + value;
            } else {
                url += "?" + param + "=" + value;
            }
        }

        return url;
    }

    /**
     * 动态设置图片的点击效果   From TradeCommonUtil
     *
     * @param bitmapNormal   正常效果的bitmap
     * @param bitmapSelected 点击效果的bitmap
     * @param idFocused
     */
    public static StateListDrawable addStateDrawable(Context context, Bitmap bitmapNormal, Bitmap bitmapSelected, int idFocused) {
        StateListDrawable sd = new StateListDrawable();
        Drawable normal = new BitmapDrawable(context.getResources(), bitmapNormal);
        Drawable pressed = new BitmapDrawable(context.getResources(), bitmapSelected);
        Drawable focus = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
        sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        sd.addState(new int[]{android.R.attr.state_focused}, focus);
        sd.addState(new int[]{android.R.attr.state_pressed}, pressed);
        sd.addState(new int[]{android.R.attr.state_enabled}, normal);
        sd.addState(new int[]{}, normal);
        return sd;
    }

    /**
     * From TradeUrlConfig ISWX，提取成方法
     *
     * @return
     */
    public static boolean isWX() {
        if (ConstantsUtils.WX_APP_ID != null && !ConstantsUtils.WX_APP_ID.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    //--------------------delete ent extract method  and constant String - YanBin-------------

    public static final String MOBILENO = "user.getMobile"; // 获取手机号 from com.zhongsou.souyue.ent.http.AppRestClient;

    /**
     * 获取用户手机号 from com.zhongsou.souyue.ent.http.HttpHelper
     */
//    public static void getMobileNo(long userId, AsyncHttpResponseHandler responseHandler) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", userId);
//        new AppRestClient().get(Utils.MOBILENO, params, responseHandler);
//    }

    public static final String IMAGE_DOMAIN = getIMAGE_DOMAIN();    //from com.zhongsou.souyue.ent.http.AppRestClient;

    /**
     * 获得图片地址  from com.zhongsou.souyue.ent.http.AppRestClient;
     * @param url
     * @return
     */
    public static String getImageUrl(String url) {
        if (url != null && !url.startsWith("http://")) {
            return IMAGE_DOMAIN + url;
        }
        return url;
    }

    /**
     * 获得图片的域名
     * @return
     */
    private static  String getIMAGE_DOMAIN() {
        switch (UrlConfig.SOUYUE_SERVICE) {
            case UrlConfig.SOUYUE_TEST:
                return "http://61.135.210.177";
            case UrlConfig.SOUYUE_PRE_ONLINE:
                return "http://61.135.210.178";
            case UrlConfig.SOUYUE_ONLINE:
                return "http://sye.img.zhongsou.com";
            default:
                return "http://sye.img.zhongsou.com";
        }
    }
    public static String getAppNameUrl(String url){
        if(StringUtils.isNotEmpty(url)){
            if(url.indexOf("appname=")!=-1){
                return url;
            }else if(url.endsWith("&")){
                return url.subSequence(0, url.length()-1)+ CommonStringsApi.getUrlAppendIgId();
            }else{
                return url+CommonStringsApi.getUrlAppendIgId();
            }
        }
        return "";
    }

    /**
     * 清除首页球球缓存
     * @param token
     */
    public static void clearHomeBallCache(String token){
        String key = getCacheKey(token);
        CMainHttp.getInstance().removeCache(key);
    }

    /**
     * 获得首页球球缓存key
     * @param token
     * @return
     */
    public static String getCacheKey(String token) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("type", 1 + "");
        map.put("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication
                .getInstance()));
        map.put("channel",
                DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        return CMainHttp.getInstance().getKey(UrlConfig.GET_SHOW_HOME_BALL, map);
    }

    /**
     * 检测超时，主要是用于弹框
     * @param key
     * @return
     */
    public static boolean checkOverTime(String key,int timeStep){
            String special_timestamp = SYSharedPreferences.getInstance().getString(key, "");
            if (StringUtils.isEmpty(special_timestamp)) {
                return true;
            } else {
                // 同一用户两条消息之间的间隔超过5分钟的话客户端才处理
                String[] special_timestamps = special_timestamp.split(",");
                if (special_timestamps != null && special_timestamps.length > 1) {
                    User u = SYUserManager.getInstance().getUser();
                    if (u != null) {
                        if (special_timestamps[0].equals(u.userId() + "")) {
                            long currentTemp = System.currentTimeMillis();
                            long lastTemp = Long.parseLong(special_timestamps[1]);
                            // 5*60*1000
                            if (currentTemp - lastTemp > timeStep) {
                                return true;
                            }
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
            return false;
        }
}


