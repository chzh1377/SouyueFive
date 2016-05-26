package com.zhongsou.souyue;

public interface UmengDefParams {
    public static final String SUPERSEARCHURL_VALUE = "http://n.zhongsou.net/supershare/join/search.aspx";//超级分享搜索地址
    public static final String COINS_PRESENT_FOR_REG_VALUE = "0";//提示用户通过手机号注册，可获得中搜币,当此值大于0时候，在登录页面弹框，否则不弹框
    public static final String INVITE_SMS_VALUE = "短信内容";//短信邀请内容 
    public static final String LAOHUJI_REPLY_NO_VALUE = "地主家也米有余粮啊，下次吧!";
    
    public static final String COINS_PRESENT_FOR_REG = "COINS_PRESENT_FOR_REG";//提示用户通过手机号注册，可获得中搜币,当此值大于0时候，在登录页面弹框，否则不弹框
    public static final String INVITE_SMS = "INVITE_SMS";//短信邀请内容 
    public static final String SUPERSEARCHURL = "SUPERSEARCHURL";//超级分享搜索地址 
    public static final String LAOHUJI_REPLY_NO = "LAOHUJI_REPLY_NO";//舍不得
    public static final String ENABLE_UPLOAD_PUSH_LOG="ENABLE_UPLOAD_PUSH_LOG";//开启推送统计开关  1 为允许，否则为不允许，默认值0
    public static final String FIRST_LOGIN_MSG = "FIRST_LOGIN_MSG";//提示用户注册欢迎语。
}
