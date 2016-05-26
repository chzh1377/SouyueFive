package com.zhongsou.souyue.circle.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bob zhou on 14-12-30.
 */
public class Constant {


    //圈成员角色
    public static final int ROLE_NONE = 0;             //非圈成员
    public static final int ROLE_ADMIN = 1;            //圈主(管理员)
    public static final int ROLE_NORMAL = 2;           //普通成员
    public static final int ROLE_VISITOR = 3;          //游客
    public static final int ROLE_SUB_ADMIN = 4;          //副圈主


    //圈子类型
    public static final int INTEREST_TYPE_NORMAL = 0;    //普通圈
    public static final int INTEREST_TYPE_PRIVATE = 1;   //私密圈


    //圈成员禁言状态
    public static final int MEMBER_BAN_TALK_NO = 0;     //未被禁言
    public static final int MEMBER_BAN_TALK_YES = 1;    //已被禁言


    //是否开启隐私保护
    public static final int MEMBER_PRIVATE_NO = 0;      //未开启隐私保护
    public static final int MEMBER_PRIVATE_YES = 1;     //已开启隐私保护


    //帖子是否置顶
    public static final int POST_TYPE_TOP_NO = 0;      //非置顶
    public static final int POST_TYPE_TOP_YES = 1;     //置顶


    //帖子是否加精
    public static final int POST_TYPE_PRIME_NO = 0;    //非加精
    public static final int POST_TYPE_PRIME_YES = 1;   //加精


    //操作类型
    public static final int OPERATE_TYPE_ADD = 1;
    public static final int OPERATE_TYPE_EDIT = 2;
    public static final int OPERATE_TYPE_DEL = 3;
    public static final int OPERATE_TYPE_UPDATE_COUNT = 4;
    public static final int OPERATE_TYPE_UPDATE_ZAN_COUNT = 5;
    
    //列表布局类型
    public static final int TYPE_ITEM_PIC_NO = 0;		//无图
    public static final int TYPE_ITEM_PIC_ONE = 1;		//一张图
    public static final int TYPE_ITEM_PIC_THREE = 3;	//三张图
    public static final int TYPE_ITEM_PIC_TOP = 4; 		// 置顶布局
    
    //列表更新接收广播类型
    public static final int CIRCLE_BROADCAST_TYPE_ADD = 1;			//新增帖子
    public static final int CIRCLE_BROADCAST_TYPE_UPDATE = 2;	    //有更新（包括跟帖、点赞、加精）
    public static final int CIRCLE_BROADCAST_TYPE_DEL = 3;			//删除
    public static final int CIRCLE_BROADCAST_TYPE_EDIT = 4;		//编辑
//    public static final int CIRCLE_BROADCAST_TYPE_PRAISE = 5;		//点赞
//    public static final int CIRCLE_BROADCAST_TYPE_JING = 6;			//加精

    public final static int POST_TOP_STATE_FAILED = 0;
    public final static int POST_TOP_STATE_SUCCESS = 1;
    public final static int POST_TOP_STATE_DENIED = 2;
    public final static int POST_TOP_STATE_NOT_EXIST = 3;
    public final static int POST_TOP_STATE_LIMIT_THREE = 5;
    public static Map<Integer, String> POST_TOP_STATE_MAP = new HashMap<Integer, String>();
    static {
        POST_TOP_STATE_MAP.put(POST_TOP_STATE_FAILED, "置顶失败");
        POST_TOP_STATE_MAP.put(POST_TOP_STATE_SUCCESS, "置顶成功");
        POST_TOP_STATE_MAP.put(POST_TOP_STATE_DENIED, "您没有权限");
        POST_TOP_STATE_MAP.put(POST_TOP_STATE_NOT_EXIST, "帖子不存在");
        POST_TOP_STATE_MAP.put(POST_TOP_STATE_LIMIT_THREE, "最多置顶3篇");
    }
    public static String get_POST_TOP_STATE_Label(Integer status) {
        return POST_TOP_STATE_MAP.get(status);
    }

    public final static int POST_PRIME_STATE_FAILED = 0;
    public final static int POST_PRIME_STATE_SUCCESS = 1;
    public final static int POST_PRIME_STATE_DENIED = 2;
    public final static int POST_PRIME_STATE_NOT_EXIST = 3;
    public static Map<Integer, String> POST_PRIME_STATE_MAP = new HashMap<Integer, String>();
    static {
        POST_PRIME_STATE_MAP.put(POST_PRIME_STATE_FAILED, "加精失败");
        POST_PRIME_STATE_MAP.put(POST_PRIME_STATE_SUCCESS, "加精成功");
        POST_PRIME_STATE_MAP.put(POST_PRIME_STATE_DENIED, "您没有权限");
        POST_PRIME_STATE_MAP.put(POST_PRIME_STATE_NOT_EXIST, "帖子不存在");
    }
    public static String get_POST_PRIME_STATE_Label(Integer status) {
        return POST_PRIME_STATE_MAP.get(status);
    }

    public final static int POST_DELETE_STATE_FAILED = 0;
    public final static int POST_DELETE_STATE_SUCCESS = 1;
    public final static int POST_DELETE_STATE_DENIED = 2;
    public final static int POST_DELETE_STATE_NOT_EXIST = 3;
    public static Map<Integer, String> POST_DELETE_STATE_MAP = new HashMap<Integer, String>();
    static {
        POST_DELETE_STATE_MAP.put(POST_DELETE_STATE_FAILED, "删除失败");
        POST_DELETE_STATE_MAP.put(POST_DELETE_STATE_SUCCESS, "删除成功");
        POST_DELETE_STATE_MAP.put(POST_DELETE_STATE_DENIED, "您没有权限");
        POST_DELETE_STATE_MAP.put(POST_DELETE_STATE_NOT_EXIST, "帖子不存在");
    }
    public static String get_POST_DELETE_STATE_Label(Integer status) {
        return POST_DELETE_STATE_MAP.get(status);
    }

    public final static int POST_CANCEL_PRIME_STATE_FAILED = 0;
    public final static int POST_CANCEL_PRIME_STATE_SUCCESS = 1;
    public final static int POST_CANCEL_PRIME_STATE_DENIED = 2;
    public final static int POST_CANCEL_PRIME_STATE_NOT_EXIST = 3;
    public static Map<Integer, String> POST_CANCEL_PRIME_STATE_MAP = new HashMap<Integer, String>();
    static {
        POST_CANCEL_PRIME_STATE_MAP.put(POST_CANCEL_PRIME_STATE_FAILED, "取消加精失败");
        POST_CANCEL_PRIME_STATE_MAP.put(POST_CANCEL_PRIME_STATE_SUCCESS, "取消加精成功");
        POST_CANCEL_PRIME_STATE_MAP.put(POST_CANCEL_PRIME_STATE_DENIED, "您没有权限");
        POST_CANCEL_PRIME_STATE_MAP.put(POST_CANCEL_PRIME_STATE_NOT_EXIST, "帖子不存在");
    }
    public static String get_POST_CANCEL_PRIME_STATE_Label(Integer status) {
        return POST_CANCEL_PRIME_STATE_MAP.get(status);
    }

    public final static int POST_CANCEL_TOP_STATE_FAILED = 0;
    public final static int POST_CANCEL_TOP_STATE_SUCCESS = 1;
    public final static int POST_CANCEL_TOP_STATE_DENIED = 2;
    public final static int POST_CANCEL_TOP_STATE_NOT_EXIST = 3;
    public static Map<Integer, String> POST_CANCEL_TOP_STATE_MAP = new HashMap<Integer, String>();
    static {
        POST_CANCEL_TOP_STATE_MAP.put(POST_CANCEL_TOP_STATE_FAILED, "取消置顶失败");
        POST_CANCEL_TOP_STATE_MAP.put(POST_CANCEL_TOP_STATE_SUCCESS, "取消置顶成功");
        POST_CANCEL_TOP_STATE_MAP.put(POST_CANCEL_TOP_STATE_DENIED, "您没有权限");
        POST_CANCEL_TOP_STATE_MAP.put(POST_CANCEL_TOP_STATE_NOT_EXIST, "帖子不存在");
    }
    public static String get_POST_CANCEL_TOP_STATE_Label(Integer status) {
        return POST_CANCEL_TOP_STATE_MAP.get(status);
    }

    public final static int POST_TUISONG_STATE_FAILED = 502;
    public final static int POST_TUISONG_STATE_MAX_FAILED = 503;
    public final static int POST_TUISONG_STATE_SUCCESS = 200;
    public final static int POST_TUISONG_STATE_DENIED = 501;
    public static Map<Integer, String> POST_TUISONG_STATE_MAP = new HashMap<Integer, String>();
    static {
        POST_TUISONG_STATE_MAP.put(POST_TUISONG_STATE_FAILED, "您已超过每日推送最大条数限制");
        POST_TUISONG_STATE_MAP.put(POST_TUISONG_STATE_MAX_FAILED, "您已超过每日推送最大条数限制");
        POST_TUISONG_STATE_MAP.put(POST_TUISONG_STATE_SUCCESS, "消息推送成功");
        POST_TUISONG_STATE_MAP.put(POST_TUISONG_STATE_DENIED, "非圈主操作");
    }
    public static String get_POST_TUISONG_STATE_Label(Integer status) {
        return POST_TUISONG_STATE_MAP.get(status);
    }

   //改变字体大小
    public final static String CHANGE_FONT_ACTION = "update_font";
}
