package com.zhongsou.souyue.platform;
/**
 * Project Name: souyue_platform_4.0.1
 * @version 4.0.1
 */

import com.zhongsou.souyue.R;

/**
 * Description: 平台配置项API<br/>
 * Company:     ZhongSou.com<br/>
 * Copyright:   2003-2014 ZhongSou All right reserved<br/>
 *
 * @author liudl
 * @date 2014-7-30 下午2:10:31
 */
public class ConfigApi extends CommonConfig {

    public static final boolean isPrintWebViewLogToSDCard = false;//是否打印webview log到SD卡

    public static final boolean isUseWebViewImageBlock = true;//是否使用webview图片阻塞

    /**
     * isSouyue:当前应用是否是搜悦 <br/>
     *
     * @return
     * @author liudl
     * @date 2014-7-31 上午10:57:32
     */
    public static boolean isSouyue() {
        return Boolean.valueOf(getStringResourceValue(R.string.config_is_souyue));
    }
//
//    /**
//     * isShowSplash:是否显示启动图<br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-7-31 上午9:15:38
//     */
//    public static boolean isShowSplash() {
//        return getBooleanValue(getResourceName(R.string.config_show_splash));
//    }
//
//    /**
//     * setShowSpalsh:设置是否显示启动图. <br/>
//     *
//     * @param isShowSplash
//     * @author liudl
//     * @date 2014-7-31 上午9:16:07
//     */
//    protected static void setShowSpalsh(boolean isShowSplash) {
//        setBooleanValue(getResourceName(R.string.config_show_splash), isShowSplash);
//    }
//
//    /**
//     * isShowSplashOnceOnly:是否仅显示一次启动图<br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-7-31 上午9:15:38
//     */
//    public static boolean isShowSplashOnceOnly() {
//        return getBooleanValue(getResourceName(R.string.config_show_splash_once_only));
//    }
//
//    /**
//     * setShowSpalshOnceOnly:设置是否仅显示一次启动图. <br/>
//     *
//     * @param isShowSplashOnceOnly
//     * @author liudl
//     * @date 2014-7-31 上午9:16:07
//     */
//    protected static void setShowSpalshOnceOnly(boolean isShowSplashOnceOnly) {
//        setBooleanValue(getResourceName(R.string.config_show_splash_once_only), isShowSplashOnceOnly);
//    }
//
//    /**
//     * isShowBootImg:是否显示引导图<br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-7-31 上午9:15:38
//     */
//    public static boolean isShowBootImg() {
//        return getBooleanValue(getResourceName(R.string.config_show_boot_img));
//    }
//
//    /**
//     * setShowBootImg:设置是否显示引导图. <br/>
//     *
//     * @param isShowBootImg
//     * @author liudl
//     * @date 2014-7-31 上午9:16:07
//     */
//    protected static void setShowBootImg(boolean isShowBootImg) {
//        setBooleanValue(getResourceName(R.string.config_show_boot_img), isShowBootImg);
//    }
//
//    /**
//     * isShowBootImgOnceOnly:是否仅显示一次引导图<br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-7-31 上午9:15:38
//     */
//    public static boolean isShowBootImgOnceOnly() {
//        return getBooleanValue(getResourceName(R.string.config_show_boot_img_once_only));
//    }
//
//    /**
//     * setShowBootImgOnceOnly:设置是否仅显示一次引导图. <br/>
//     *
//     * @param isShowBootImgOnceOnly
//     * @author liudl
//     * @date 2014-7-31 上午9:16:07
//     */
//    protected static void setShowBootImgOnceOnly(boolean isShowBootImgOnceOnly) {
//        setBooleanValue(getResourceName(R.string.config_show_boot_img_once_only), isShowBootImgOnceOnly);
//    }
//
//    /**
//     * isShowLoginRenRen:是否显示人人登陆. <br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-7-31 上午9:42:20
//     */
//    public static boolean isShowLoginRenRen() {
//        return getBooleanValue(getResourceName(R.string.config_show_login_renren));
//    }
//
//    /**
//     * isShowLoginTradeChina:是否显示行业中国登陆. <br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-7-31 上午9:43:23
//     */
//    public static boolean isShowLoginTradeChina() {
//        return getBooleanValue(getResourceName(R.string.config_show_login_trade_china));
//    }
//
//    /**
//     * 系统推送开关默认是否是打开状态 <br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-8-4 下午2:07:11
//     */
//    public static boolean isPushOpenDefault() {
//        return Boolean.valueOf(getStringResourceValue(R.string.config_push_open_default));
//    }
//
//    /**
//     * 推送是否为线上环境 <br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-8-4 下午2:07:11
//     */
//    public static boolean isPushOnline() {
//        return Boolean.valueOf(getStringResourceValue(R.string.config_push_environment_online));
//    }
//
//    /**
//     * http请求是否为线上环境 <br/>
//     *
//     * @return
//     * @author liudl
//     * @date 2014-8-4 下午2:07:11
//     */
//    public static boolean isHttpRequestOnline() {
//        return Boolean.valueOf(getStringResourceValue(R.string.config_http_request_online));
//    }

    /**
     * 当前项目是否是百强<br/>
     *
     * @return
     * @author liudl
     * @date 2014-11-7 上午10:17:47
     */
    public static boolean isBQProject() {
        return !isSouyue() && "0".equals(getStringResourceValue(R.string.ProjectType));
    }

    /**
     * 当前项目是否是超级APP<br/>
     *
     * @return
     * @author liudl
     * @date 2014-11-7 上午10:17:47
     */
    public static boolean isSuperAppProject() {
        return !isSouyue() && "1".equals(getStringResourceValue(R.string.ProjectType));
    }

    /**
     * 当前是哪个应用平台(针对广告接口  搜悦:1	    超A:2	移动船票:3)
     *
     * @return
     */
    public static String getSouyuePlatform() {
        return String.valueOf(getStringResourceValue(R.string.config_souyue_platform).trim());
    }
}
  
	