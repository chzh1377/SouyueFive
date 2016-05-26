package com.zhongsou.souyue.ui.webview;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.module.JSClick;

/**
 * 与javascript通信类。
 *
 * @author wanglong@zhongsou.com
 */

public interface JavascriptInterface extends DontObfuscateInterface{

    @android.webkit.JavascriptInterface
    public void setButtonDisable();// 设置按钮状态

    @android.webkit.JavascriptInterface
    public void setImages(String images);// 注入image url
    @android.webkit.JavascriptInterface
    public void gotoSRP(String keyword, String srpId);// 锚点 跳转到srp
    @android.webkit.JavascriptInterface
    public void openAd2(String json);// 广告返回数据 josn格式
    @android.webkit.JavascriptInterface
    public String getSouyueInfo();// 为js提供info
    @android.webkit.JavascriptInterface
    public String getSouyueVersion();//获得搜悦版本号
    @android.webkit.JavascriptInterface
    public void gotoShare();// 原创大赛 根据用户类型 跳转原创列表或登陆页面
    @android.webkit.JavascriptInterface
    public String getNetworkType();
    @android.webkit.JavascriptInterface
    public void onJSClick(String json);// js交互
    @android.webkit.JavascriptInterface
    public void gotoInterest(long interest_id);// 搜索页-搜索兴趣圈关键词-点击调整到兴趣圈接口
    @android.webkit.JavascriptInterface
    public String getFictionIndex(String novelId);
    @android.webkit.JavascriptInterface
    public String getFictionContent(String novelId, int begin, int offet);
    @android.webkit.JavascriptInterface
    public void downloadVideo(String id, String name, String img, String length, String urls);
    @android.webkit.JavascriptInterface
    public void downloadFiction(String id, String name, String img, String length, String url,String version);
    @android.webkit.JavascriptInterface
    public void setLocalCookie(String key, String value);
    @android.webkit.JavascriptInterface
    public void getLocalCookie(String key);

//    public void redirectCouponDetail(long coupon_zsb);  // 现金券兑换跳转

//    public void login();// 跳转到登录界面
//
//    public boolean isLogin(); // 判断用户是否登录 true已登录

    /*public interface LoginListener {
        public void login();
    }

    public interface IsLoginListener {
        public boolean isLogin();
    }*/

    /*public interface RedirectCouponDetail {
        public void redirectCouponDetail(long coupon_zsb);
    }*/

    public interface ButtonListener {
        @android.webkit.JavascriptInterface
        public void setButtonDisable();
    }

    public interface ImagesListener {
        @android.webkit.JavascriptInterface
        public void setImages(String iags);
    }

    public interface GotoSrpListener {
        @android.webkit.JavascriptInterface
        public void gotoSRP(String keyword, String srpId);
    }

    public interface OpenAdListener {
        @android.webkit.JavascriptInterface
        public void openAd2(String json);
    }

   /* public interface GetAdListListener {
        public void getAdList(String json);
    }*/
    
    public interface GotoShareListener {
        @android.webkit.JavascriptInterface
        public void gotoShare();
    }

    public interface OnJSClickListener {
        @android.webkit.JavascriptInterface
        public void onJSClick(JSClick json);
    }

    public interface GotoInterestListener {
        @android.webkit.JavascriptInterface
        public void gotoInterest(long interest_id);
    }

    public interface ReadNovelDictionaryListener {
        @android.webkit.JavascriptInterface
        public String getFictionIndex(String novelId);
    }
    
    public interface ReadNovelContentListener {
        @android.webkit.JavascriptInterface
        public String getFictionContent(String novelId, int begin, int offet);
    }

    public interface DownloadRadioListener {
        @android.webkit.JavascriptInterface
        public void downloadVideo(String id, String name, String img, String length, String urls);
    }

    public interface DownloadNovelListener {
        @android.webkit.JavascriptInterface
        public void downloadFiction(String id, String name, String img, String length, String url,String version);
    }

    public interface SetLocalCookieListener {
        @android.webkit.JavascriptInterface
        public void setLocalCookie(String key, String value);
    }

    public interface GetLocalCookieListener {
        @android.webkit.JavascriptInterface
        public void getLocalCookie(String key);
    }

}
