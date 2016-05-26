package com.zhongsou.souyue.platform;

/**
 * Project Name: souyue-platform-4.0.1
 * @version  4.0.1
 */

import android.content.Context;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.share.ShareAppKeyUtils;

/**
 * Description: 第三方分享API<br/>
 * Company: ZhongSou.com<br/>
 * Copyright: 2003-2014 ZhongSou All right reserved<br/>
 * 
 * @date 2014-7-31 上午11:16:27
 * @author liudl
 */
public class ShareApi {
    // 新浪微博
    public static final String SINA_CONSUMER_KEY = ConfigApi.isSouyue() ? ShareAppKeyUtils.SINA_CONSUMER_KEY
            : ConfigApi.getStringResourceValue(R.string.SINA_CONSUMER_KEY);
    public static final String SINA_REDIRECT_URL = ConfigApi.isSouyue() ? ShareAppKeyUtils.SINA_REDIRECT_URL
            : ConfigApi.getStringResourceValue(R.string.SINA_REDIRECT_URL);
    public static final String SINA_CONSUMER_SECRET = ConfigApi.isSouyue() ? ShareAppKeyUtils.SINA_CONSUMER_SECRET
            : ConfigApi.getStringResourceValue(R.string.SINA_CONSUMER_SECRET);
    // 微信
    public static final String WEIXIN_APP_ID = ConfigApi.isSouyue() ? ShareAppKeyUtils.WX_APP_ID
            : ConfigApi.getStringResourceValue(R.string.WX_APP_ID);
    // QQ登陆
    public static final String QQ_APP_ID = ConfigApi.isSouyue() ? ShareAppKeyUtils.QQ_APP_ID
            : ConfigApi.getStringResourceValue(R.string.QQ_APP_ID);
    public static final String QQ_APP_KEY = ConfigApi.isSouyue() ? ShareAppKeyUtils.QQ_APP_KEY
            : ConfigApi.getStringResourceValue(R.string.QQ_APP_KEY);
    // 腾讯微博
//    public static final String TENCENT_WEIBO_APP_KEY = ConfigApi.isSouyue() ? Util.getConfig(context).getProperty("APP_KEY") : ConfigApi
//            .getStringResourceValue(R.string.TWIBO_APP_KEY);
//    public static final String TENCENT_WEIBO_APP_SECRET = ConfigApi
//            .getStringResourceValue(R.string.TWIBO_APP_KEY_SEC);
//    public static final String TENCENT_WEIBO_REDIRECT_URL = ConfigApi
//            .getStringResourceValue(R.string.TWIBO_REDIRECT_URI);
    // 人人
    public static final String RENREN_APP_ID = ShareAppKeyUtils.RR_RENREN_APP_ID;
    public static final String RENREN_API_KEY = ShareAppKeyUtils.RR_API_KEY;
    public static final String RENREN_SECRET_KEY = ShareAppKeyUtils.RR_SECRET_KEY;

    // 腾讯微博from
    public static final String TENCENT_WEIBO_FROM = CommonStringsApi
            .getStringResourceValue(R.string.weibo_from)
            + (ConfigApi.isSouyue() ? CommonStringsApi
                    .getStringResourceValue(R.string.tecent_weibo_from)
                    : CommonStringsApi
                            .getStringResourceValue(R.string.TWEIBO_FROM));
    // 新浪微博from
    public static final String SINA_WEIBO_FROM = "【 "
            + CommonStringsApi.getStringResourceValue(R.string.weibo_from)
            + (ConfigApi.isSouyue() ? CommonStringsApi
                    .getStringResourceValue(R.string.sina_weibo_from)
                    : CommonStringsApi
                            .getStringResourceValue(R.string.WEIBO_FROM)) + " 】";
    
//    /**
//     * 获取腾讯微博APP KEY
//     * 腾讯微博配置信息在/assets/tencentconfig.properties中配置 <br/>
//     * @author liudl
//     * @date   2014-10-20 下午4:08:30
//     * @param context
//     * @return
//     */
//    public static String getTecentWeiboAppKey(Context context){
//        return Util.getConfig(context).getProperty("APP_KEY");
//    }
//
//    /**
//     * 获取腾讯微博APP SECRET
//     * 腾讯微博配置信息在/assets/tencentconfig.properties中配置 <br/>
//     * @author liudl
//     * @date   2014-10-20 下午4:12:15
//     * @param context
//     * @return
//     */
//    public static String getTecentWeiboAppSecret(Context context){
//        return Util.getConfig(context).getProperty("APP_KEY_SEC");
//    }
    
//    /**
//     * 获取腾讯微博APP Redirect Url
//     * 腾讯微博配置信息在/assets/tencentconfig.properties中配置 <br/>
//     * @author liudl
//     * @date   2014-10-20 下午4:12:15
//     * @param context
//     * @return
//     */
//    public static String getTecentWeiboRdrtUrl(Context context){
//        return Util.getConfig(context).getProperty("REDIRECT_URI");
//    }
}
