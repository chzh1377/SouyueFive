package com.zhongsou.souyue.countUtils;

import android.content.Context;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zs.zssdk.ZSclickAgent;

import java.util.HashMap;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V
 * @Copyright (c) 2015 zhongsou
 * @Description zssdk Statistic
 * @date 2015/11/23
 */
public class ZSSdkUtil {
    //订阅来源定义
    //精选订阅推荐
    public static final String RECOMMEND_SUBSCRIBE="recommend.subscribe";
    //详情页顶部订阅
    public static final String NEWSDETAIL_SUBSCRIBE_TOP="newsdetail.subscribe.top";
    //详情页正文底部订阅(这个h5还没有添加)
    public static final String NEWSDETAIL_SUBSCRIBE_BUTTOM="newsdetail.subscribe.buttom";
    //SRP页面头部
    public static final String SRP_SUBSCRIBE_TITLE="srp.subscribe.title";
    //订阅菜下拉
    public static final String SRP_SUBSCRIBE_MENU="srp.subscribe.menu";
    //热门订阅
    public static final String TOPIC_SUBSCRIBE_MENU="topic.subscribe.menu";
    //订阅大全
    public static final String ALLLIST_SUBSCRIBE_EXTERNAL="alllist.subscribe.external";
    //其他订阅推荐
    public static final String OTHER_SUBSCRIBE_MENU="other.subscribe.menu";
    //有关兴趣圈订阅
    //圈吧订阅
    public static final String CIRCLEBAR_SUBSCRIBE_GROUP="circlebar.subscribe.group";
    //圈首页订阅
    public static final String CIRCLEINDEX_SUBSCRIBE_GROUP="circleindex.subscribe.group";
    //私密圈
    public static final String INVITE_SUBSCRIBE_GROUP="invite.subscribe.group";
    //圈管理
    public static final String MANAGE_SUBSCRIBE_GROUP="manage.subscribe.group";

    /**
     * zssdk的初始化  根据搜悦服务器环境设置不同的发送策略
     *
     * @param context 上下文
     */
    public static void initZSSDK(Context context) {
        initByEnvironment();    //设置发送策略
        //ZSSDK init
        ZSclickAgent.init(context, null, null);
    }

    /**
     * 根据搜悦环境进行不同的发送策略的设置
     */
    public static void initByEnvironment() {
        //zssdk上传服务器（正式）： http://mlcp.zhongsou.com
        //zssdk上传服务器（测试）： http://61.135.210.99:80
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case BaseUrlRequest.SOUYUE_DEVLOPER:
            case BaseUrlRequest.SOUYUE_TEST:
                //ZSSDK 设置发送策略，2-延时； 3-实时
                ZSclickAgent.setLogWithReportPolicy(3, 0);    //第二个参数为延时时间，以秒为单位
//                ZSclickAgent.setToTestServer();// 调用此方法表示统计日志发送到日志的测试服务器
                break;
            case BaseUrlRequest.SOUYUE_PRE_ONLINE:
                ZSclickAgent.setLogWithReportPolicy(2, 60);    //第二个参数为延时时间，以秒为单位
                break;
            case BaseUrlRequest.SOUYUE_ONLINE:
                ZSclickAgent.setLogWithReportPolicy(2, 10 * 60);    //第二个参数为延时时间，以秒为单位
                break;
            default:
                ZSclickAgent.setLogWithReportPolicy(2, 10 * 60);    //第二个参数为延时时间，以秒为单位
                break;
        }
    }

    /**
     * 统计事件
     *
     * @param context 上下文
     * @param eventId 事件ID
     * @param map     上传数据 键值对
     */
    public static void onEvent(Context context, String eventId, HashMap map){
        ZSclickAgent.onEvent(context, eventId, map);
    }

    /**
     *
     * @param context 上下文
     * @param name 当前页面名称
     * @param preference 前一个页面
     */
    public static void onPageStart(Context context, String name, String preference){
        ZSclickAgent.onPageStart(context, name, preference);
    }

    /**
     *
     * @param context 上下文
     * @param name 当前页面名称
     * @param preference 前一个页面
     */
    public static void onPageEnd(Context context, String name, String preference){
        ZSclickAgent.onPageEnd(context, name, preference);
    }
}
