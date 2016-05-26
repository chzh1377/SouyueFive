package com.zhongsou.souyue.utils;
/**
 * 
 * @author wangchunyan@zhongsou.com
 * 设置分享类型
 */
public interface ShareConstantsUtils {
    public static final String DIMENSIONALCODE = "1";//DimensionalCodeActivity中调用
    public static final String PLAZAHOTEVENTS = "2";//PlazaHotEventsActivity中调用
    public static final String SEARCH = "3"; //SearchActivity中调用
//    public static final String SEARCHKEYWORD = "30"; //SearchActivity中调用,没有keyword的情况
    public static final String WEBSRCVIEW = "4";//WebSrcView中调用
    public static final String WEBSRCVIEWKEYWORD = "40";//WebSrcView中调用,没有keyword
    public static final String WEBSRCVIEWWEBTYPE = "41";//WebSrcView中调用,没有keyword,类型为web（为贺卡分享添加“搜悦好友”兼容页面分享项）
    public static final String SELFCREATEDETAIL = "5";//SelfCreateDetailActivity中调用,从上到下弹出
    public static final String QRCODEA = "6";//QRCodeActivity中调用
    public static final String QRCODEF = "7";//QRCodeFragment中调用
    public static final String RECOMMENDFRIEND = "8";//RecommendFriendActivity中调用
    public static final String READABILITY = "9";//ReadabilityActivity中调用
    public static final String READABILITYKEYWORD = "11";//ReadabilityActivity中调用
    public static final String SUPERSRP = "10";//超级分享大赛
    public static final String CIRCLECARD = "12";//圈子名片
    public static final String CIRCLEQRCODE = "13";//圈子二维码
    public static final String SRP = "14";//SRPAcitivity
    public static final String NEW_DETAIL = "15";//新详情页
    public static final String GALLERY_NEWS= "16";//图及页面
    public static final String JOKE_AND_GIF= GALLERY_NEWS;//段子和gif页面
    public static final String VIDEO= "17";// 视频

}
