package com.zhongsou.souyue.utils;

import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.UrlConfig;

public interface ConstantsUtils {

    //是否是测试推送环境 0 内网，1外网，2线上
    public static final int PUSH_TEST = UrlConfig.getPUSH_TEST();
    public static final String LINK = "com.tuita.sdk.action.souyue";
    public static final int START_FOR_RESULT = 1010;
    //---------------Fragment类型---------------
    public static final String FR_BAIKE = "百科";
    public static final String FR_QA = "有问必答";
    public static final String FR_WEIBO_SEARCH = "微博搜索";
    public static final String FR_BLOG_SEARCH = "博客搜索";
    public static final String FR_BBS_SEARCH = "论坛搜索";
    public static final String FR_SRP_SELFCREATE = "网友原创";    //原创微件
    public static final String FR_JHQ = "精华区";
    //    public static final String FR_ENT = "推荐企业";
    public static final String FR_IMG_SEARCH = "图片搜索";
    public static final String FR_SELF_CREATE = "原创";        //网友原创分享大赛
    public static final String FR_WEB = "web";
    //    public static final String FR_IMG_NAV = "图片导航";
    public static final String FR_WEB_SUB = "网页订阅";
    public static final String FR_CHAT_ROOM = "聊天室";//"聊天室";
    public static final String VJ_DUANZI_SEARCH = "段子";
    public static final String VJ_GIF_SEARCH = "GIF";
    public static final String FR_INTEREST_GROUP = "兴趣圈精华区";
    public static final String FR_INTEREST_BAR = "兴趣圈圈吧";
    public static final String FR_NEW_INDEX = "homepage"; //搜悦4.0 第三代SRP优化，引入首页微件
    public static final String FR_INFO_PUB = "信息发布";    //搜悦4.1引入信息发布微件
    public static final String FR_INFO_SPECIAL = "专题";    //搜悦5.0.5引入信息发布微件
    public static final String FR_INFO_PICTURES = "图集";    //搜悦5.0.7引入图集
    public static final String FR_INFO_GIF = "GIF";    //搜悦5.1.0引入GIF
    public static final String FR_INFO_JOKE = "段子";    //搜悦5.1.0引入段子
    public static final String FR_INFO_VIDEO = "视频";    //搜悦5.1.0引入视频


    //---------------微件类型----------------------
    public static final String VJ_NEW_SEARCH = "新闻搜索";
    public static final String VJ_VIDEO_SEARCH = "视频搜索";
    public static final String VJ_BAIKE_LORE = "百科知识";
    //    public static final String VJ_BAIKE = "百科";
    public static final String VJ_BBS_SEARCH = "论坛搜索";
    public static final String VJ_QA = "有问必答";
    public static final String VJ_PEOPLE = "相关人物";
    public static final String VJ_HISTORY = "历史事件";
    public static final String VJ_WEIBO_SEARCH = "微博搜索";
    //    public static final String VJ_WEB_SUB = "网页订阅";
    public static final String VJ_BLOG_SEARCH = "博客搜索";
    public static final String VJ_SRP_SELFCREATE = "网友原创";    //原创微件
    public static final String VJ_STAR_SAY = "名人名言";
    public static final String VJ_JHQ = "精华区";
    public static final String VJ_SELF_CREATE = "原创";        //网友原创分享大赛
    //    public static final String VJ_ENT = "推荐企业";
//    public static final String VJ_IMG_SEARCH = "图片搜索";
//    public static final String VJ_WEB = "web";
    public static final String VJ_IMG_NAV = "图片导航";
    public static final String VJ_WEB_NAV = "网站订阅";
    public static final String VJ_PHB = "排行榜";

    //-------------原创类型-----------------------
//    public static final String YQ_WEIBO_SEARCH = "微博搜索";
//    public static final String YQ_BLOG_SEARCH = "博客搜索";
//    public static final String YQ_BBS_SEARCH = "论坛搜索";

    //    public static final int STATUS_SEND_SAVE = -1;
    public static final int STATUS_SEND_REVIEW = 0;
    public static final int STATUS_SEND_PASS = 1;
    public static final int STATUS_SEND_NOPASS = 2;
    public static final int STATUS_SEND_FAIL = 3;
    public static final int STATUS_SEND_ING = 4;
//	public static final String STATUS_STR_SEND_FAIL = "3";
//	public static final String STATUS_STR_SEND_ING = "4";

    public static final String INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV = "action.refresh.selfcreate.listview";
    public static final int TYPE_ALL = 0;

    //-------超A 抽取常量 - YanBin------------------
    //From SupplyDetailActivity
    public static final int FROM_SALE_DETAIL = 1;
    //From SupplyDetailActivity
    public static final String FROM = "from";
    // 应用宝典action
    public static String ACTION_APPBIBLE = "com.zhongsou.appbible.ACTION_APPBIBLE";
    // From TradeUrlConfig
    public static String MEGAGAME_SEARCH_KEYWORD = "中华之梦创业大赛"; // 大赛搜索词 正式环境
    // From TradeUrlConfig
    public static boolean PUSH_DEFAULT_OPEN = false; // 推送默认开关状态
    //From InquiryActivity
    public static final String TITLE_TAG = "title";
    //From TradeUrlConfig
    public static String APP_NAME = MainApplication.getInstance().getResources().getString(R.string.APP_NAME);
    ;
    //判断是否活动srp Preference前缀,是否全部下载完成 From LoadImgsService
    public static final String ACTION_SRPID_DOWNLOAD_OVER_PRE = "action_srpid_download_over";
    //From TradeUrlConfig
    public static String IGID = MainApplication.getInstance().getResources().getString(R.string.IGID);
    //From TradeUrlConfig
    public static String WX_APP_ID = MainApplication.getInstance().getResources().getString(R.string.WX_APP_ID);// 微信分享Id
    //From TradeUrlConfig
    public static String PUSH_ID = "push_appid_" + IGID;// 推送ID后缀
    //From TradeUrlConfig
//    public static String KW = MainApplication.getInstance().getResources().getString(R.string.KW);

    //	public static final int TYPE_WEB_SUB = 1;//: [name: '网页订阅', ],
//	2: [name: '网站订阅', ],
//	3: [name: '新闻搜索', ],
    public static final int TYPE_BLOG_SEARCH = 4;//: [name: '博客搜索', ],
    //	5: [name: '图书搜索', ],
//	6: [name: '图片搜索', ],
//	7: [name: '视频搜索', ],
//	8: [name: '相关推荐', ],
//	9: [name: '百科', ],
//	10: [name: '名人名言',],
//	11: [name: '历史事件',],
    public static final int TYPE_BBS_SEARCH = 12;//: [name: '论坛搜索',],
    //	13: [name: '贴吧搜索',],
//	14: [name: '百科知识',],
    public static final int TYPE_WEIBO_SEARCH = 1121;//: [name: '微博搜索',],
//	17: [name: '意见反馈',],
//	16: [name: '订阅rss',],
//	19: [name: '本页导读', ],
//	18: [name: '搜索历史', ],
//	21: [name: 'iframe', ],
//	20: [name: '天气预报', ],
//	23: [name: '贴吧互动', ],
//	22: [name: 'HTML嵌入',],
//	25: [name: '求购微件',],
//	24: [name: '供应微件',],
//	27: [name: '购物搜索',],
//	26: [name: '企业微件',],
//	29: [name: '次级导航',],
//	28: [name: '导航栏', ],
//	31: [name: '图集微件', ],
//	30: [name: '商品推荐', ],
//	34: [name: '促销推荐', ],
//	35: [name: '导购资讯', ],
//	32: [name: '团购微件', ],
//	33: [name: '文本标签', ],
//	38: [name: '商品导航', ],
//	39: [name: '面包屑', ],
//	36: [name: '排行榜', ],
//	37: [name: '品类资讯', ],
//	42: [name: '商家入驻', ],
//	43: [name: '价格走势', ],
//	40: [name: '商品信息', ],
//	41: [name: '购物iframe', ],
//	51: [name: '导航', ],
//	50: [name: '有问必答', ],
//	55: [name: '大事记', ],
//	54: [name: '成绩', ],
//	53: [name: '知识塔', ],
//	52: [name: '小档案', ],
//	57: [name: '国际天气', ],
//	56: [name: '地图应用', ],
//	60: [name: '视频播放', ],

//	//订阅内容类型
//	public static final int TYPE_SRP = 1;
//	public static final int TYPE_RSS = 2;
//	public static final int TYPE_GROUP = 3;
//	public static final int TYPE_ENT = 4;// 企业

    public static final String SHARE_COUNT = "share_count";   //提取自MultipleActivity的常量
    public static final String PUSH_REGID_NAME = "pushRegID";   //存在SharedPreference中推送 regID 是否需要上传标志的n

    String SUB_STATUS_ACTION = "com.zhongsou.souyue.sub.status";    //订阅状态变化广播 Action
}
