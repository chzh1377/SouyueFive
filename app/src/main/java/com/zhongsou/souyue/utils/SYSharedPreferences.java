package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.UmengDefParams;
import com.zhongsou.souyue.circle.util.Constant;

public class SYSharedPreferences {

    public static final String KEY_TASKCENTER_KICKUSER_TOKEN = "taskcenter_kickuser_token";// 任务中心踢人消息缓存_token
    public static final String KEY_TASKCENTER_KICKUSER_MSG = "taskcenter_kickuser_msg";// 任务中心踢人消息缓存_msg
    public static final String KEY_TASKCENTER_DISCOVERMSG = "taskcenter_discovermsg";// 任务中心发现提醒消息缓存
    public static final String KEY_TASKCENTER_REDSHOW = "taskcenter_redshow";// 任务中心缓存发现处红点显示

    public static final String KEY_SHOW_GUIDE_DY = "showGuide";// 是否显示引导页，思路，每种引导页都起一个自己的名字，唯一标识这个引导页
    public static final String KEY_SHOW_GUIDE_DY_NEW = "showGuide_new_502";

    public static final String KEY_SHOW_GUEST_SPECIAL = "KEY_SHOW_GUEST_SPECIAL";// 游客是否弹出系统推荐
    public static final String KEY_SHOW_ADMIN_SPECIAL = "KEY_SHOW_ADMIN_SPECIAL";// 登录用户是否弹出系统推荐

    public static final String KEY_SHOW_TIMESTAMP_SPECIAL = "KEY_SHOW_TIMESTAMP_SPECIAL";// 设置弹框推荐时间戳
    public static final String KEY_SHOW_SYSTEM_SPECIAL = "KEY_SHOW_SYSTEM_SPECIAL";// 来弹框时，如果搜悦在后台，记录标记
    public static final String KEY_SHOW_SYSTEM_SUBRECOMMEND = "KEY_SHOW_SYSTEM_SUBRECOMMEND";// 来弹框时，如果搜悦在后台，记录标记
    public static final String KEY_SHOW_TIMESTAMP_SUBRECOMMEND = "KEY_SHOW_TIMESTAMP_SUBRECOMMEND";// 来弹框时，如果搜悦在后台，记录标记

    public static final String KEY_VERSION = "version_name";
    public static final String KEY_SHOW_APPMENU = "showAppMenu";
    public static final String KEY_CREATESHORTCUT = "createShortCut";// 是否创建快捷方式
    public static final String KEY_ISRUNNING = "isRunning";
    public static final String KEY_PUSHSWITCH = "pushSwitch";// 请注意：此处原本是消息推送，后改成消息免打扰，但是覆盖安装时，其内容不能改
    // 所以其内容取反，故使用此字段时， 一定要注意逻辑问题
    public static final String KEY_LOGIN_TOKEN = "login_token";
    public static final String KEY_LOGIN_TYPE = "login_type";
    public static final String KEY_UPDATE = "update";
    public static final String KEY_HOME_UPDATE = "home_update";
    public static final String KEY_USER_UPDATE = "user_update";
    public static final String KEY_UPDATE_YAOWEN = "key_update_yaowen";
    public static final String KEY_UPDATE_CIRCLE = "updateCircle";
    public static final String KEY_INPUT_MODEL = "input_model";
    public static final String KEY_REGISTERSUCCESS = "registerSuccess";
    public static final String KEY_LAST_SEARCH_KEYWORD = "last_search_keyword";
    // public static final String KEY_SENDALERTHIDE = "sendAlertHide"; //废弃
    public static final String KEY_SENDALERTHIDE = "showSendAlertByVersion";
    public static final String KEY_ISCREATE = "isCreate";
    public static final String KEY_UUID = "UUID";
    public static final String KEY_LOADIMGABLE = "loadImgAble";// 是否加载图片
    public static final String KEY_FONTSIZE = "fontSize";// 字体大小 1,2,3
    public static final String KEY_SOUYUEAD = "souyueAd";
    public static final String KEY_AD_CLOSED = "ad_closed";
    public static final String KEY_IS_DualSIM = "isDualSIM";
    public static final String KEY_WARNING_IMG = "warning_img";// 是否提示无图模式
    public static final String ISFIRSTREADCONTACT = "isFirstReadContact"; // 第一次取手机联系人
    public static final String ISCHATFIRSTREADCONTACT = "isChatFirstReadContact"; // 进入im第一次取手机联系人
    public static final String IS_SHOW_KEYBOARD = "isShowKeyboard"; // 标识点击列表评论跳到图集评论页是否弹键盘

    public static final String SHARECALLBACK = "call_back";// 是否提示无图模式
    public static final String CONTENT = "content";// 是否提示无图模式
    public static final String KEYWORD = "keyword";// 分享的keyword
    public static final String SRPID = "srpid";// 分享的srpid
    public static final String SHAREURL = "shareurl"; // 分享用的链接
    private static final String SP_NAME = "souyue";
    public static final String KEY_CITY = "KEY_CITY";
    public static final String KEY_PROVINCE = "KEY_PROVINCE";
    public static final String KEY_LAT = "KEY_LAT";
    public static final String KEY_LNG = "KEY_LNG";
    public static final String KEY_PUSH_SOUND = "tuita_push_sound";
    public static final String KEY_PUSH_VIBRATE = "tuita_push_vibrate";
    public static final String SHOWMENU = "showmenu";

    public static final String KEY_SLOT_MUTE = "is_tiger_game_mute";

    public static final String SLIDINGMENU_UP = "slidingmenu_up";
    public static final String SLIDINGMENU_DOWN = "slidingmenu_down";

    public static final String MYSUBSCRIBE_TUTORIAL_PREFS_NAME = "mysubscribe";
    public static final String HOMEPAGER_TUTORIAL_PREFS_NAME = "homepager";
    public static final String CIRCLE_TUTORIAL_PREFS_NAME = "circle";
    public static final String IS_FIRST_ENTER = "isFirstEnter"; // 判断是不是每天第一次进入app(用来更新小说离线阅读的html)
    public static final String FICTION_VERSION = "fiction_version";
    public static final String PREUSERNAME = "preusername";

    public static final String FIRST_INSTALL_RECOMMEND = "first_install_recommend";// 安装完毕之后第一次启动推荐...

    // -----------友盟参数-------------------------------
    public static final String UM_SUPERSEARCHURL = UmengDefParams.SUPERSEARCHURL;
    public static final String UM_COINS_PRESENT_FOR_REG = UmengDefParams.COINS_PRESENT_FOR_REG;
    public static final String UM_INVITE_SMS = UmengDefParams.INVITE_SMS;
    public static final String UM_FIRST_LOGIN_MSG = UmengDefParams.FIRST_LOGIN_MSG;
    // public static final String UM_LAOHUJI_REPLY_NO =
    // UmengDefParams.LAOHUJI_REPLY_NO;

    // 搜悦检测新版本 -- 非强制更新保存标识
    public static final String UPDATE = "update_version";
    public static final String UPDATE_ID = "update_id";
    // 搜悦引导页每个版本对应不同的名字
    public static final String FIRSTGUIDE = "souyue5.0";

    // public static final String UPDATE_FILE = "update_file";
    // ======= super app end =========

    // 登陆提示
    public static final String KEY_LOGIN_TIP = "KEY_LOGIN_TIP";

    // ======= super app start =========
    public static final String KEY_ISFIRST_SHOW_GUIDE_DY = "isFirstshowGuide"; // 是否第一次显示引导页
    public static final String KEY_FIRST_OPEN_APP = "isFirstOpen"; // 是否第一次打开app
    public static final String CAROUSEL_MD5 = "md5"; // 轮播图-产品微件md5
    public static final String CAROUSEL_SRP = "srp"; // 轮播图-产品微件srp
    public static final String CAROUSEL_SRPID = "srpid"; // 轮播图--产品微件srpid
    public static final String SUBSCRIBE_SRP = "subscribeSrp"; // 启动app自动订阅的srp词
    // 可配置显示开关项 zhaobo 2014-08-22
    public static final String KEY_SHOW_LEFTGRADE = "leftgradeisshow"; // 左树等级是否显示
    public static final String KEY_SHOW_LEFTMONEY = "leftmoneyisshow"; // 左树顶部
    // 中搜币是否显示
    public static final String KEY_SHOW_MYGRADE = "mygradeisshow"; // 会员中心
    // 等级是否显示
    public static final String KEY_SHOW_MYMONEY = "mymoneyisshow"; // 会员中心
    // 中搜币余额是否显示
    public static final String KEY_SHOW_MYSCORE = "myscoreisshow"; // 会员中心
    // 积分余额是否显示
    public static final String KEY_SHOW_MONEYSHOP = "moneyshopisshow"; // 会员中心
    // 中搜币商城是否显示
    public static final String KEY_SHOW_EXCHANGE = "exchangeisshow"; // 会员中心
    // 积分兑换中搜币是否显示
    public static final String KEY_SHOW_CHARGE = "chargeisshow"; // 会员中心 充值是否显示
    public static final String KEY_SHOW_MYCREATE = "mycreateisshow"; // 会员中心
    // 我的原创是否显示
    public static final String KEY_SHOW_MYCOMMENT = "mycommentisshow"; // 会员中心
    // 我的评论是否显示
    public static final String KEY_SHOW_MYASK = "myaskisshow"; // 会员中心 我的询报价是否显示
    public static final String KEY_SHOW_MYSTREET = "mystreetisshow"; // 会员中心
    // 我的移动商街是否显示
    public static final String KEY_SHOW_PAYMENT = "paymentisshow"; // 会员中心
    // 支付密码设置是否显示
    public static final String KEY_SHOW_MYMOVEMENT = "mymovement"; // 会员中心
    // 我的活动是否显示
    public static final String KEY_SHOW_MYDOWNLOAD = "mydownload"; // 会员中心
    // 我的离线是否显示
    public static final String KEY_SHOW_CAROUSEL = "carouselisshow"; // 天天汽车轮播图是否显示
    public static final String KEY_SHOW_WEIBOLOGIN = "weibologin"; // 微博登陆
    public static final String KEY_SHOW_QQLOGIN = "qqlogin"; // QQ登陆
    public static final String KEY_SHOW_HYLOGIN = "hylogin"; // 行业登陆
    public static final String SHOPSTATE = "shopstate"; // 移动商街搜索是否传参custom_type
    // 搜悦用户名和密码，格斗商城用 add by fm
    public static final String ISTHIRDTYPE = "ISTHIRDTYPE"; // 是否是第三方
    public static final String USERNAME = "name";
    public static final String UID = "uid";
    public static final String PASSWORD = "password";
    public static final String TYPE = "0"; // 登录类型，0 搜悦登录， 1第三方登录
    public static final String TYPES = "type";

	/*
           * // 搜悦检测新版本 -- 非强制更新保存标识 public static final String UPDATE =
	 * "update_version"; public static final String UPDATE_ID = "update_id"; //
	 * 搜悦引导页每个版本对应不同的名字 public static final String FIRSTGUIDE = "newyear";
	 */

    public static final String FONT_SIZE = "font_size";

    // public static final String UPDATE_FILE = "update_file";
    // ======= super app end =========

    /**
     * 搜悦企业
     */
    public static final String KEY_ENT_MALL_ID = "KEY_ENT_MALL_ID";
    public static final String KEY_ENT_KEYWORD = "KEY_ENT_KEYWORD";

    // 字体（大中小）标志和字体大小
    public static final String FONT_VALUE_BIG = "big";
    public static final String FONT_VALUE_MIDDLE = "middle";
    public static final String FONT_VALUE_SMALL = "small";

    public static final int FONT_VALUE_BIG_SIZE = 20;
    public static final int FONT_VALUE_MIDDLE_SIZE = 18;
    public static final int FONT_VALUE_SMALL_SIZE = 16;

    public static final String LOAD_ONLY_WIFI = "load_only_wifi";
    public static final String NEW_EXPRESSION = "new_expression";
    public static final String SHOW_TIPS = "show_tips";
    public static final String APP_REPLAY = "app_replay";
    public static final String PRE_POSITION = "pre_postion";
    public static final String SUBER_CLICKED = "suber_clicked";
    public static  final String SUBER_SRPID = "suber_id";
    public static  final String CLEAR_HOME_CACHE = "clear_homeball_cache";
    public static  final String CLEAR_HOME_MAIN = "HOMEBALL_MAIN";

    private static SYSharedPreferences instance = new SYSharedPreferences();

    public SYSharedPreferences() {

    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new SYSharedPreferences();
        }
    }

    public static SYSharedPreferences getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    private SharedPreferences getSp() {
        return MainApplication.getInstance().getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
    }

    public int getInt(String key, int def) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getInt(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putInt(String key, int val) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putInt(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getLong(String key, long def) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getLong(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putLong(String key, long val) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putLong(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String key, String def) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getString(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putString(String key, String val) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putString(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getBoolean(String key, boolean def) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null)
                def = sp.getBoolean(key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public void putBoolean(String key, boolean val) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putBoolean(key, val);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remove(String key) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.remove(key);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void putString(String keyToken, String Toaekval, String keyMsg,
                          String Msgkval) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putString(keyToken, Toaekval);
                e.putString(keyMsg, Msgkval);
                e.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存字体大小
     *
     * @param fontSize
     */
    public void putFontString(String fontSize, Context context) {
        try {
            SharedPreferences sp = getSp();
            if (sp != null) {
                Editor e = sp.edit();
                e.putString(FONT_SIZE, fontSize);
                e.commit();
            }
            Intent intent = new Intent(Constant.CHANGE_FONT_ACTION);
            context.sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWebViewFont() {
        SharedPreferences sp = getSp();
        String fontSize = sp.getString(FONT_SIZE, FONT_VALUE_MIDDLE);
        if (FONT_VALUE_MIDDLE.equals(fontSize)) {
            return 100;
        } else if (FONT_VALUE_BIG.equals(fontSize)) {
            return 150;
        } else if (FONT_VALUE_SMALL.equals(fontSize)) {
            return 75;
        }
        return 75;
    }

    public float loadResFont(Context context) {
        SharedPreferences sp = getSp();
        String fontSize = sp.getString(FONT_SIZE, FONT_VALUE_MIDDLE);
        if (FONT_VALUE_MIDDLE.equals(fontSize)) {
            return FONT_VALUE_MIDDLE_SIZE;
        } else if (FONT_VALUE_BIG.equals(fontSize)) {
            return FONT_VALUE_BIG_SIZE;
        } else if (FONT_VALUE_SMALL.equals(fontSize)) {
            return FONT_VALUE_SMALL_SIZE;
        }
        return FONT_VALUE_MIDDLE_SIZE;
    }

    public boolean getTipShow(Context context) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(SHOW_TIPS, false);
    }

    public void putTipShow(Context context, boolean b) {
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putBoolean(SHOW_TIPS, b);
            e.commit();
        }
    }

    public void putLoadWifi(Context context, boolean b) {
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putBoolean(LOAD_ONLY_WIFI, b);
            e.commit();
        }
    }

    public boolean getLoadWifi(Context context) {
        SharedPreferences sp = getSp();
        return sp.getBoolean(LOAD_ONLY_WIFI, false);
    }

    public void setNewExpression(boolean b) {
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putBoolean(NEW_EXPRESSION, b);
            e.commit();
        }
    }

    public boolean hasNewExpression() {
        SharedPreferences sp = getSp();
        boolean isNew = sp.getBoolean(NEW_EXPRESSION, false);
        return isNew;
    }

    public void setReplay(boolean b) {
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putBoolean(APP_REPLAY, b);
            e.commit();
        }
    }

    public boolean getReplay() {
        SharedPreferences sp = getSp();
        boolean replay = sp.getBoolean(APP_REPLAY, false);
        return replay;
    }

    public boolean getPosition() {
        SharedPreferences sp = getSp();
        boolean isPre = sp.getBoolean(PRE_POSITION, true);
        return isPre;
    }

    public void putPosition(boolean b) {
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putBoolean(PRE_POSITION, b);
            e.commit();
        }
    }

    public void putClicked(boolean b){
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putBoolean(SUBER_CLICKED, b);
            e.commit();
        }
    }

    public boolean hasClicked() {
        SharedPreferences sp = getSp();
        boolean clicked = sp.getBoolean(SUBER_CLICKED, false);
        return clicked;
    }

    public void setSuberSrpId(String srpId){
        SharedPreferences sp = getSp();
        if (sp != null) {
            Editor e = sp.edit();
            e.putString(SUBER_SRPID, srpId);
            e.commit();
        }
    }

    public String getSrpId(){
        SharedPreferences sp = getSp();
        String srpId = sp.getString(SUBER_SRPID,"");
        return srpId;
    }

}
