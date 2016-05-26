package com.zhongsou.souyue.platform;
/**
 * Project Name: souyue-platform-4.0.1
 *
 * @version 4.0.1
 */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Description: 字符串配置及操作API<br/> 
 * Company:   ZhongSou.com<br/> 
 * Copyright: 2003-2014 ZhongSou All right reserved<br/> 
 * @date 2014-7-31 上午11:16:27
 * @author liudl
 */
public class CommonStringsApi {
    public static final String APP_NAME = ConfigApi.isSouyue() ? getStringResourceValue(R.string.app_name) : getStringResourceValue(R.string.APP_NAME);
    //appname简称，搜悦显示“搜悦”，用于区分“中搜搜悦”
    public static final String APP_NAME_SHORT = ConfigApi.isSouyue() ? getStringResourceValue(R.string.app_name_short) : getStringResourceValue(R.string.APP_NAME);
    public static final String IG_ID = getStringResourceValue(R.string.IGID);
    public static final String PORTAL_KEYWORD = getStringResourceValue(R.string.KW);
    public static final String SRP_KEYWORD = getStringResourceValue(R.string.SRP_KW);
    public static final String SRP_ID = getStringResourceValue(R.string.SRP_ID);
    public static final String SHARE2FRIENDS_URL = getStringResourceValue(R.string.share_to_friends_url);
    //超A有些地方需要显示“本App”
    public static final String LOCAL_APP = ConfigApi.isSouyue() ? APP_NAME_SHORT : "本App";
    public static final String ENCODE_UTF8 = "UTF-8";
    public static final String PARAM_SUPER_APPNAME = "super_appname";
    public static final String PARAM_SUPER_IG_ID = "super_igid";
    public static final int SOUYUE_INTERFACE_ENV = Integer.parseInt(getStringResourceValue(R.string.souyue_interface_env));
    public static final String SHARE_JHQ_WARNING = String.format(getStringResourceValue(R.string.share_jhq_warning), CommonStringsApi.APP_NAME_SHORT);
    //测试环境拼srp首页的icon
    public static final String SRP_ICON_TEST_URL = "http://hems3.zhongsou.com/Img/getSrpImg?srpId=";
    //线上环境拼srp首页的icon
    public static final String SRP_ICON_ONLINE_URL = "http://edit.zhongsou.com/Img/getSrpImg?srpId=";
    /**
     * TUITA推送ID,用于查找对应的应用及密码
     */
    public static final String PUSH_ID = ConfigApi.isSouyue() ? "souyue" : "push_appid_" + IG_ID;

    /**
     * 获取资源文件string.xml中对应的文本 <br/>  
     *
     * @author liudl
     * @date 2014-8-6 下午3:58:28
     * @param id
     * @return
     */
    public static String getStringResourceValue(int id) {
        return ConfigApi.getStringResourceValue(id);
    }

    /**
     * URL请求链接增加参数appname,区分超级app用. <br/>  
     *
     * @author liudl
     * @date 2014-8-4 下午2:58:44
     * @return
     */
    public static String getUrlAppendIgId() {
        if (ConfigApi.isSouyue()) {
            return "&";
        } else {
            return "&appname=" + IG_ID + "&";
        }
    }

    /**
     * 为所有超级app请求的接口增加参数appname和igid，用于区分不同的超级app<br/>  
     *
     * @author liudl
     * @date 2014-11-20 上午11:05:08
     * @param url
     * @return
     */
    public static String urlAppAppend(String url) {
        if (url == null) {
            return "";
        }
        if (ConfigApi.isSouyue()) {
            return url;
        } else {
            StringBuilder appendUrl = new StringBuilder(url);
            try {
                String s = (url.indexOf("?") > -1) ? "&" : "?";
                if (url.indexOf("?" + PARAM_SUPER_APPNAME + "=") == -1 && url.indexOf("&" + PARAM_SUPER_APPNAME + "=") == -1) {
                    appendUrl.append(s).append(PARAM_SUPER_APPNAME).append("=").append(URLEncoder.encode(CommonStringsApi.APP_NAME, "UTF-8"));
                }
                if (url.indexOf("?" + PARAM_SUPER_IG_ID + "=") == -1 && url.indexOf("&" + PARAM_SUPER_IG_ID + "=") == -1) {
                    appendUrl.append("&").append(PARAM_SUPER_IG_ID).append("=").append(CommonStringsApi.IG_ID);
                }
            } catch (UnsupportedEncodingException e) {
                Log.e("appAppend", e.getMessage());
            }
            return appendUrl.toString();
        }
    }

    /**
     * 获取首页球球的图片地址
     * @param context
     * @param srpId
     * @return
     */
    public static  String getSrpIconUrl(Context context, String srpId) {
        int env = Integer.parseInt(context.getString(R.string.souyue_interface_env));
        String imageIcon;
        if (env == UrlConfig.SOUYUE_ONLINE || env == UrlConfig.SOUYUE_PRE_ONLINE) {
            imageIcon = CommonStringsApi.SRP_ICON_ONLINE_URL + srpId;
        } else {
            imageIcon = CommonStringsApi.SRP_ICON_TEST_URL + srpId;
        }
        return imageIcon;
    }

    /**
     * 判断左树元素是否是分类. <br/>  
     *
     * @author liudl
     * @date 2014-8-5 上午9:52:49
     * @param homePageItem
     * @return
     */
//    public static boolean isLeftMenuCate(HomePageItem homePageItem){
//        if(ConfigApi.isSouyue()){
//            return homePageItem.title().equals(ConfigApi.getStringResourceValue(R.string.leftmenu_cate_name));
//        }else{
//            return TextUtils.isEmpty(homePageItem.category());
//        }
//    }

//    /**
//     * 构造tuita action. <br/>
//     *
//     * @author liudl
//     * @date   2014-8-5 下午2:34:58
//     * @return
//     */
//    public static String getTuitaAction(){
//        String action = "com.tuita.sdk.action.";
//        if(ConfigApi.isSouyue()){
//            action += "souyue";
//        }else{
//            action += PUSH_ID;
//        }
//        return action;
//    }

    /**
     * parseNum:description. <br/>  
     *
     * @author liudl
     * @date 2014-10-15 上午10:51:04
     * @param str
     * @return
     */
    public static int parseNum(String str) {
        int num = 0;
        try {
            if (StringUtils.isNotEmpty(str)) {
                num = Integer.parseInt(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }

    /**
     * getShowVersion:关于我们布局文件显示的版本名称. <br/>  
     *
     * @author liudl
     * @date 2014-7-31 上午11:50:50
     * @param context
     * @param strFormat
     * @return
     */
    public static String getShowVersion(Context context, String strFormat) {
        String vName = "  " + DeviceInfo.getAppVersionName();
        if (ConfigApi.isSouyue()) {
            return strFormat + vName;
        } else {
            return String.format(strFormat, vName);
        }
    }

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
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取首页class  
     *
     * @author liudl
     * @date 2014-10-21 下午12:03:51
     * @return
     */
    public static Class<?> getHomeClass() {
        return MainActivity.class;
    }
}
