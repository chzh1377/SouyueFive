package com.zhongsou.souyue.net.volley;

/**
 * 所有HTTP请求时的id
 * Created by lvqiang on 15/7/30.
 */
public class HttpCommon {
    public static final int USER_BASE_REQUEST = 50000;

    public static final int USER_REPIRE_USER_INFO_REQUEST  = USER_BASE_REQUEST + 1;
    public static final int USER_CIRCLE_LIST_REQUEST       = USER_BASE_REQUEST + 2;
    public static final int USER_LOGIN_IN_REQUEST          = USER_BASE_REQUEST + 3;
    public static final int USER_GET_SECURITY_CODE_REQUEST = USER_BASE_REQUEST + 4;
    public static final int USER_PHONE_REGISTER_REQUEST    = USER_BASE_REQUEST + 5;
    public static final int USER_FAVORITE_LIST_REQUEST     = USER_BASE_REQUEST + 6;
    public static final int USER_PUSH_MSG_REQUEST          = USER_BASE_REQUEST + 7;
    public static final int USER_NOTICE_LIST_REQUEST       = USER_BASE_REQUEST + 8;
    public static final int USER_INTERNAL_REQUEST          = USER_BASE_REQUEST + 9;
    public static final int USER_CALLBACK_REQUEST          = USER_BASE_REQUEST + 10;
    public static final int USER_MOOD_LIST_REQUEST         = USER_BASE_REQUEST + 11;
    public static final int USER_DELETE_MODE_REQUEST       = USER_BASE_REQUEST + 12;
    public static final int USER_ADD_MODE_REQUEST          = USER_BASE_REQUEST + 13;
    public static final int USER_GET_REPLY_LIST_REQUEST    = USER_BASE_REQUEST + 14;
    public static final int USER_INVITE_FRIEND_REQUEST     = USER_BASE_REQUEST + 15;
    public static final int USER_UPDATE_PWD_REQUEST        = USER_BASE_REQUEST + 16;


    public static final int USER_WALLET_REQUEST = USER_BASE_REQUEST + 250;

    /**
     * 订阅 wangqiang
     */
    public static final int SUB_BASE                                 = 13000;
    public static final int SUB_LIST_REQUEST                         = SUB_BASE + 1;
    public static final int SUB_ADDDEL_REQUEST                       = SUB_BASE + 2;
    public static final int SUB_MODIFY_REQUEST                       = SUB_BASE + 3;
    public static final int SUB_RSSLIST_REQUEST                      = SUB_BASE + 4;
    public static final int SUB_CATETREE_REQUEST                     = SUB_BASE + 5;
    public static final int SUB_SRP_REQUEST                          = SUB_BASE + 6;
    public static final int SUB_CHECK_REQUEST                        = SUB_BASE + 7;
    public static final int SUB_RSSCATE_REQUEST                      = SUB_BASE + 8;
    public static final int SUB_SETTING_REQUEST                      = SUB_BASE + 9;
    public static final int SUB_GROUPLIST_REQUEST                    = SUB_BASE + 10;
    public static final int SUB_RSSLIST2_REQUEST                     = SUB_BASE + 11;
    public static final int SUB_GROUPINTEREST_REQUEST                = SUB_BASE + 12;
    public static final int SUB_ADD_REQUEST                          = SUB_BASE + 13;
    public static final int SUB_CATETREE2_REQUEST                    = SUB_BASE + 14;
    public static final int SUB_DELETE_REQUEST                       = SUB_BASE + 15;
    public static final int SUB_LIST_REQUEST2                        = SUB_BASE + 16;
    public static final int SUB_GROUPLIST_REQUEST2                   = SUB_BASE + 17;
    public static final int SUB_RECOMMENDDLGLIST_REQUEST             = SUB_BASE + 16; // 弹窗订阅请求；
    public static final int SUB_BATCHSUBANDDEL_REQUEST               = SUB_BASE + 17;// 批量订阅请求
    public static final int SUB_GUIDE_RECOMMEND_SPECIAL_LIST_REQUEST = SUB_BASE + 18;// 引导页推荐专题
    public static final int SUB_GUIDE_RECOMMEND_SRP_LIST_REQUEST     = SUB_BASE + 19;// 引导页推荐SRP词
    public static final int SUB_GUIDE_RECOMMEND_SRP_REQUEST          = SUB_BASE + 20;// 引导页订阅推荐SRP词
    public static final int SUB_SAVE_CHANNAL_REQUEST                 = SUB_BASE + 21;// 订阅保存
    public static final int SUB_CHANNAL_LIST_REQUEST                 = SUB_BASE + 22;// 订阅频道列表
    public static final int SUB_SWITCH_POST_REQUEST                  = SUB_BASE + 23;// 订阅开关post
    public static final int SUB_SWITCH_GET_REQUEST                   = SUB_BASE + 24;// 订阅开关get
    public static final int SUB_TIPS_REQUEST                         = SUB_BASE + 25;
    public static final int SUBER_SERACH_METHOD                      = SUB_BASE + 26;
    public static final int SUBER_REMCOMMEND_METHOD                  = SUB_BASE + 27;
    public static final int SUBER_UPDATE_METHOD                      = SUB_BASE + 28;
    public static final int SUBER_ALL_INTEREST_GROUP_ACTION          = SUB_BASE + 100;  //添加订阅 请求左侧接口
    public static final int SUBER_ALL_INTEREST_CHILD_ACTION          = SUB_BASE + 101;  //添加订阅 请求右侧接口
    /**
     * 订阅 -》 获取分组列表
     */
    public static final int GROUP_TITLE_REQ                          = SUB_BASE + 30;
    public static final int GROUP_LIST_REQ_COMEIN                    = SUB_BASE + 31;
    public static final int GROUP_LIST_REQ_PULL                      = SUB_BASE + 33;
    public static final int GROUP_LIST_REQ_PUSH_DOWN                 = SUB_BASE + 32;
    /**
     * 订阅 分组 添加，修改，删除，编辑
     */
    public static final int GROUP_ADD_REQ                            = SUB_BASE + 33;
    public static final int GROUP_DELETE_REQ                         = SUB_BASE + 34;
    public static final int GROUP_EDIT_REQ                           = SUB_BASE + 35;
    public static final int GROUP_GET_CHILD_REQ                      = SUB_BASE + 36;
    public static final int GROUP_SUB_REQUEST                      = SUB_BASE + 37;

    /**
     * 圈子 wangqiang
     */
    public static final int CIRCLE_BASE                             = 13100;
    public static final int CIRCLE_FREETRAIN_REQUEST                = CIRCLE_BASE + 1;
    public static final int CIRCLE_AUDIT_REQEUST                    = CIRCLE_BASE + 2;
    public static final int CIRCLE_SYSRECOMMEND_REQUEST             = CIRCLE_BASE + 3;
    public static final int CIRCLE_SETFREE_REQUEST                  = CIRCLE_BASE + 4;
    public static final int CIRClE_MEMBERINFO_REQUEST               = CIRCLE_BASE + 5;
    public static final int CIRCLE_MEMBERLIST_REQUEST               = CIRCLE_BASE + 6;
    public static final int CIRCLE_MEMBERIMAGE_REQUEST              = CIRCLE_BASE + 7;
    public static final int CIRCLE_SYSHARE_REQUEST                  = CIRCLE_BASE + 8;
    public static final int CIRCLE_INTERESTPRI_REQUEST              = CIRCLE_BASE + 9;
    public static final int CIRCLE_MESSAGEHIDE_REQEUST              = CIRCLE_BASE + 10;
    public static final int CIRCLE_SRPISADMIN_REQEUST               = CIRCLE_BASE + 11;
    public static final int CIRCLE_CATERECOMMEND_FORCECACHE_REQUEST = CIRCLE_BASE + 12;
    public static final int CIRCLE_CATERECOMMEND_REQUEST            = CIRCLE_BASE + 13;

    public static final int HIISTORY_CLEAR_REQUEST = CIRCLE_BASE + 13;


    /**
     * 圈子  qubian
     **/
    public static final int CIRLCE_BASE_REQUEST = 10000;

    public static final int CIRLCE_ADD_FAVORATE_ID           = CIRLCE_BASE_REQUEST + 1;//添加收藏
    public static final int CIRLCE_SHORT_URL_ID              = CIRLCE_BASE_REQUEST + 2;//短链接
    public static final int CIRLCE_INTEREST_LIST_ID          = CIRLCE_BASE_REQUEST + 3;//我的兴趣圈
    public static final int CIRLCE_INTEREST_GUIDE_LIST_ID    = CIRLCE_BASE_REQUEST + 4;//首页推荐兴趣圈
    public static final int CIRLCE_INTEREST_SUB_ID           = CIRLCE_BASE_REQUEST + 5;//订阅兴趣圈
    public static final int CIRLCE_BLOG_PRIME_LIST_ID        = CIRLCE_BASE_REQUEST + 6;//4.0精华区主页
    public static final int CIRLCE_BLOG_LIST_ID              = CIRLCE_BASE_REQUEST + 7;//圈子主贴列表
    public static final int CIRLCE_BLOG_LIST_ID01            = CIRLCE_BASE_REQUEST + 8;//圈子主贴列表
    public static final int CIRLCE_MANAGER_INFO_ID           = CIRLCE_BASE_REQUEST + 9;//圈吧管理信息
    public static final int CIRLCE_CANCLE_FAVORATE_ID        = CIRLCE_BASE_REQUEST + 10;//取消收藏
    public static final int CIRLCE_BLOG_TOP_ID               = CIRLCE_BASE_REQUEST + 11;//帖子置顶
    public static final int CIRLCE_BLOF_TOP_CANCLE_ID        = CIRLCE_BASE_REQUEST + 12;//帖子取消置顶
    public static final int CIRLCE_BLOG_PRIME_ID             = CIRLCE_BASE_REQUEST + 13;//帖子加精
    public static final int CIRLCE_BLOG_PRIME_CANCLE_ID      = CIRLCE_BASE_REQUEST + 14;//帖子取消加精
    public static final int CIRLCE_BLOG_DELETE_ID            = CIRLCE_BASE_REQUEST + 15;//帖子删除
    public static final int CIRLCE_MEMBERLIST_ID             = CIRLCE_BASE_REQUEST + 16;//圈成员列表
    public static final int CIRLCE_INVITATION_ID             = CIRLCE_BASE_REQUEST + 17;//邀请好友加入圈
    public static final int CIRLCE_BAN_TALK_ID               = CIRLCE_BASE_REQUEST + 18;//禁言操作
    public static final int CIRLCE_KICKOUT_MEMBER            = CIRLCE_BASE_REQUEST + 19;//踢出圈子操作
    public static final int CIRLCE_LIST_NAV_REQUEST          = CIRLCE_BASE_REQUEST + 20;//圈子导航
    public static final int CIRLCE_INTEREST_SUB_ID_2         = CIRLCE_BASE_REQUEST + 21;//订阅兴趣圈
    public static final int CIRLCE_BAR_LIST_REQUEST          = CIRLCE_BASE_REQUEST + 22;//圈吧列表
    /**
     * 详情 qubian
     **/
    public static final int DETAIL_BASE_REQUEST              = 40000;
    public static final int DETAIL_NEWS_DETAIL_ID            = DETAIL_BASE_REQUEST + 1;//新闻详情
    public static final int DETAIL_NEWS_COUNT_ID             = DETAIL_BASE_REQUEST + 2;//新闻详情顶数，评论数
    public static final int DETAIL_COMMENT_LSIST_ID          = DETAIL_BASE_REQUEST + 3;// 评论列表
    public static final int DETAIL_COMMENT_LSIST_PULLDOWN_ID = DETAIL_BASE_REQUEST + 4;
    public static final int DETAIL_COMMENT_LSIST_LOADMORE_ID = DETAIL_BASE_REQUEST + 5;
    public static final int DETAIL_COMMENT_ADD_ID            = DETAIL_BASE_REQUEST + 6;//添加收藏
    public static final int DETAIL_COMMENT_MY_ID             = DETAIL_BASE_REQUEST + 7;//我的评论列表
    public static final int DETAIL_UP                        = DETAIL_BASE_REQUEST + 8;//顶

    public static final int DETAIL_ADDUP_ID         = DETAIL_BASE_REQUEST + 9;//顶
    public static final int DETAIL_ADDDOWN_ID       = DETAIL_BASE_REQUEST + 10;
    public static final int DETAIL_ADDFAVORITE_ID   = DETAIL_BASE_REQUEST + 11;
    public static final int DETAIL_COMMENTDETAIL_ID = DETAIL_BASE_REQUEST + 12;
    public static final int DETAIL_BLOG_COMMENTS_ID = DETAIL_BASE_REQUEST + 13;
    public static final int DETAIL_BLOG_DELETE_ID   = DETAIL_BASE_REQUEST + 14;
    public static final int DETAIL_BLOG_REPLY_ID    = DETAIL_BASE_REQUEST + 15;

    public static final int DETAIL_WX_SHARE_JF_ID         = DETAIL_BASE_REQUEST + 16;//微信分享获取积分
    public static final int DETAIL_COMMENT_SET_HOT_ID     = DETAIL_BASE_REQUEST + 17;//设置热门评论
    public static final int DETAIL_COMMENT_NEW_LIST_ID    = DETAIL_BASE_REQUEST + 18;// 5.0评论列表
    public static final int DETAIL_MODULE_NEWS            = DETAIL_BASE_REQUEST + 19;//详情模板 新闻详情
    public static final int DETAIL_MODULE_CIRCLE          = DETAIL_BASE_REQUEST + 20;//详情模板 圈子
    public static final int DETAIL_MODULE_CIRCLE_FORCE          = DETAIL_BASE_REQUEST + 21;//详情模板 圈子 强制刷新的id
    public static final int DETAIL_MODULE_COMMONT         = DETAIL_BASE_REQUEST + 22;//详情模板 评论
    public static final int DETAIL_VIDEO_ABOUT            = DETAIL_BASE_REQUEST + 23;//详情模板 评论
    public static final int DETAIL_CIRCLE_FOOT_REQUEST            = DETAIL_BASE_REQUEST + 24;//详情模板 相关接口 - 圈子
    public static final int DETAIL_NEWS_FOOT_REQUEST            = DETAIL_BASE_REQUEST + 25;//详情模板 相关接口 - 新闻
    public static final int TEMPLATE_DOWN_REQUEST            = DETAIL_BASE_REQUEST + 26;//详情模板 下载路径管理
    /**
     * 个人中心 qubian
     **/
    public static final int PERSONCENTER_BASE_REQUEST     = 120000;
    public static final int PERSONCENTER_INFO_ID          = PERSONCENTER_BASE_REQUEST + 1;
    public static final int PERSONCENTER_INTEREST_LIST_ID = PERSONCENTER_BASE_REQUEST + 2;
    public static final int PERSONCENTER_INTEREST_SUB_ID  = PERSONCENTER_BASE_REQUEST + 3;
    public static final int PERSONCENTER_INTEREST_BLOG_ID = PERSONCENTER_BASE_REQUEST + 4;
    public static final int PERSONCENTER_MY_BG_ID         = PERSONCENTER_BASE_REQUEST + 5;

    /**
     * 列表中 gif 下载
     */
    public static final int LISTVIEW_DOWNLOAD_GIF        = PERSONCENTER_BASE_REQUEST + 5;
    public static final int LISTVIEW_DOWNLOAD_GIF_DETAIL = PERSONCENTER_BASE_REQUEST + 6;
    /**
     * 控制
     */
    public static final int CLIENT_CONGIG                = 123000 + 1;

    /**
     * 圈子 zyw
     **/
    public static final int CIRCLE_BASE_REQUEST_Z                 = 19000;
    public static final int CIRCLE_GET_CIRCLE_MENU_REQUESTID      = CIRCLE_BASE_REQUEST_Z + 1; // 获取圈子菜单信息接口
    public static final int CIRCLE_PRIME_HEAD_REQUESTID           = CIRCLE_BASE_REQUEST_Z + 2; // 精华区头部数据接口
    public static final int CIRCLE_IMGROUP_LIST_REQUESTID         = CIRCLE_BASE_REQUEST_Z + 3; //圈子IM群列表接口
    public static final int CIRCLE_GETCIRCLEMEBERINFO_REQUESTID   = CIRCLE_BASE_REQUEST_Z + 4; // 获取圈成员信息接口
    public static final int CIRCLE_GETUSERRECOMMENDLIST_REQUESTID = CIRCLE_BASE_REQUEST_Z + 5; // 获取用户推荐列表
    public static final int CIRCLE_GETUSERRECOMMENDINFO_REQUESTID = CIRCLE_BASE_REQUEST_Z + 6; // 获取用户推荐详情
    public static final int CIRCLE_DETAILBLOGPUSH_REQUESTID       = CIRCLE_BASE_REQUEST_Z + 7; //帖子推送
    public static final int CIRCLE_APPLY_REQUEST                  = CIRCLE_BASE_REQUEST_Z + 8;//私密圈申请加入接口
    public static final int CIRCLE_SAVEBLOG_REQUESTID             = CIRCLE_BASE_REQUEST_Z + 9;//保存帖子
    public static final int CIRCLE_SAVESENDINFO_REQUESTID         = CIRCLE_BASE_REQUEST_Z + 10;// 圈吧信息发布微件地址
    public static final int CIRCLE_GETMEBERROLE_REQUESTID         = CIRCLE_BASE_REQUEST_Z + 11;//获取用户在兴趣圈中的角色
    public static final int CIRCLE_SETUSERINFO_REQUESTID_HEAD     = CIRCLE_BASE_REQUEST_Z + 12;//修改用户资料(头像)
    public static final int CIRCLE_SETUSERINFO_REQUESTID_NICKNAME = CIRCLE_BASE_REQUEST_Z + 13;//修改用户资料(nickName)
    public static final int CIRCLE_SETUSERINFO_REQUESTID_SIGN     = CIRCLE_BASE_REQUEST_Z + 14;//修改用户资料(签名)
    public static final int CIRCLE_EXITCIRCLE_REQUESTID           = CIRCLE_BASE_REQUEST_Z + 15;//退出圈子
    public static final int CIRCLE_SETPRIVATE_REQUESTID           = CIRCLE_BASE_REQUEST_Z + 16;//设置隐私保护
    public static final int CIRCLE_DELREPLY_REQUESTID             = CIRCLE_BASE_REQUEST_Z + 17;//设置隐私保护
    public static final int CIRCLE_GETATMEBLOG_REQUESTID          = CIRCLE_BASE_REQUEST_Z + 18; //获取@我列表
    public static final int CIRCLE_GETCIRCLELIST_REQUESTID        = CIRCLE_BASE_REQUEST_Z + 19;//获取订阅列表
    public static final int CIRCLE_GETTAGS_REQUESTID              = CIRCLE_BASE_REQUEST_Z + 20;//获取兴趣圈所有标签
    public static final int CIRCLE_GETCIRCLEINFO_REQUESTID        = CIRCLE_BASE_REQUEST_Z + 21;//获取兴趣圈圈名片信息
    public static final int CIRCLE_GETUSERRELATIONSHIP_REQUESTID  = CIRCLE_BASE_REQUEST_Z + 22;//获取用户于某个私密圈关系
    public static final int CIRCLE_APPLYTIPS_REQUESTID            = CIRCLE_BASE_REQUEST_Z + 23;//获取用户私密圈申请记录数量的接口
    public static final int CIRCLE_GETMEMBERAPPLYLIST_REQUESTID   = CIRCLE_BASE_REQUEST_Z + 24;//用户私密圈申请记录列表接口
    public static final int CIRCLE_GETSYSRECOMMENDLIST_REQUESTID  = CIRCLE_BASE_REQUEST_Z + 25; // 获取系统推荐列表
    public static final int CIRCLE_READNEWSDETAIL_REQUESTID       = CIRCLE_BASE_REQUEST_Z + 26; // 读取新闻详情？

    /**
     * srp
     */
    public static final int SRP_BASE_REQUEST                   = 20000;
    public static final int SRP_CHAT_ROOM_REQUEST              = SRP_BASE_REQUEST + 1;
    public static final int SRP_AD_LIST_REQUEST                = SRP_BASE_REQUEST + 2;//广告列表
    public static final int SRP_AD_CLICK_REQUEST               = SRP_BASE_REQUEST + 3;//广告点击
    public static final int SRP_QUESTION_REQUEST               = SRP_BASE_REQUEST + 4;//问答
    public static final int SRP_ANSWER_REQUEST                 = SRP_BASE_REQUEST + 5;//问答
    public static final int SRP_QA_DETAIL_REQUEST              = SRP_BASE_REQUEST + 6;//问答详情
    public static final int SRP_QA_SAME_REQUEST                = SRP_BASE_REQUEST + 7;//问答-同问
    public static final int SRP_QA_ASK_REQUEST                 = SRP_BASE_REQUEST + 8;//问答
    public static final int SRP_QA_DOWN_REQUEST                = SRP_BASE_REQUEST + 9;//问答-踩
    public static final int SRP_QA_UP_REQUEST                  = SRP_BASE_REQUEST + 10;//问答-顶
    public static final int SRP_GET_SUBSCRIBED_REQUEST         = SRP_BASE_REQUEST + 11;//获取已经订阅的SRP
    public static final int SRP_LIST_REQUEST                   = SRP_BASE_REQUEST + 12;//SRP列表
    public static final int SRP_LIST_REFRESH_REQUEST           = SRP_BASE_REQUEST + 13;//SRP列表下拉刷新
    public static final int SRP_LIST_MORE_REQUEST              = SRP_BASE_REQUEST + 14;//SRP列表上拉加载更多
    public static final int SRP_LIST_NAV_REQUEST               = SRP_BASE_REQUEST + 15;//SRP导航列表
    public static final int SRP_LIST_RSS_REQUEST               = SRP_BASE_REQUEST + 16;//SRP-RSS
    public static final int SRP_QA_ASK_REFRESH_REQUEST         = SRP_BASE_REQUEST + 17;//SRP-问答刷新
    public static final int SRP_LIST_MY_CREATE_REQUEST         = SRP_BASE_REQUEST + 18;//原创
    public static final int SRP_LIST_MY_CREATE_MORE_REQUEST    = SRP_BASE_REQUEST + 19;//原创上拉加载更多
    public static final int SRP_LIST_MY_CREATE_REFRESH_REQUEST = SRP_BASE_REQUEST + 20;//原创列表下拉刷新


    public static final int SRP_SEARCHRESULT_REQUESTID                = SRP_BASE_REQUEST + 21;//searchresult接口
    public static final int SRP_SEARCHRESULTPULLDOWNREFRESH_REQUESTID = SRP_BASE_REQUEST + 22;//searchresult,下拉刷新接口
    public static final int SRP_SEARCHRESULTTOLOADMORE_REQUESTID      = SRP_BASE_REQUEST + 23;//searchresult,下拉刷新接口

    /**
     * qubian
     * 通用的listview 请求
     */
    public static final int COMMEN_LIST_REQUEST_BASE                   = 150000;
    public static final int COMMEN_LIST_GET_LIST                       = COMMEN_LIST_REQUEST_BASE + 1;
    public static final int COMMEN_LIST_LIST_PULL                      = COMMEN_LIST_REQUEST_BASE + 2;
    public static final int COMMEN_LIST_LIST_MORE                      = COMMEN_LIST_REQUEST_BASE + 3;

    /**
     * share zyw
     **/
    public static final int SHARE_BASE_REQUEST           = 30000; // 分享
    public static final int SHARE_PV_REQUESTID           = SHARE_BASE_REQUEST + 1; //分享统计
    public static final int SHARE_POSTTODIGIST_REQUESTID = SHARE_BASE_REQUEST + 2;//分享到精华区
    public static final int SHARE_RESULT_REQUESTID       = SHARE_BASE_REQUEST + 3; // 分享回调
    public static final int SHARE_SUC_REQUESTID          = SHARE_BASE_REQUEST + 4; // 中搜零拍分享回调接口

    public static final int SHARE_TO_PLATOM              = SHARE_BASE_REQUEST + 3; //分享统计
    //discover 接口Request id 常量 每个ID相差10
    public static final int GET_PLAZA_HOME_REQUEST_ID    = 90010;
    public static final int GET_PLAZA_SUB_SRP_REQUEST_ID = 90020;
    public static final int GET_DISCOVER_LIST_REQUEST_ID = 90030;


    //search 接口Request id 常量 每个ID相差10
    public static final int GET_SRP_INDEX_DATA_REQUEST_ID = 80010;
    public static final int SEARCH_HOT_REQUEST = 800020;
    public static final int SEARCH_DATA_REQUEST = 800030;

    //other 接口Request id 常量 每个ID相差10
    public static final int RECOMMENT_GROUP_REQUEST_ID = 120010;

    public static final int NOTICELIST_REQUEST = 120020;
    public static final int TOOLTIP_REQUEST    = 120030;

    public static final int CHATROOMADD_REQUEST      = 120040;
    public static final int CHATROOMLIST_REQUEST     = 120050;
    public static final int CHATROOMLOADMORE_REQUEST = 120060;

    //发现
    public static final int QRCODE_REQUEST = 120070;

    //账号及系统 add by wangchunyan
    public static final int UPDATECLIENT_REQUEST_ID         = 60001;
    public static final int UPDATECLIENT_REQUEST_ID1        = 60002;
    public static final int USERGUESTTOKEN_REQUEST_ID       = 60000;
    public static final int USERSOUYUEBACK_REQUEST_ID       = 60003;
    //原创  add by wangchunyan
    public static final int SELFCREATELIST_REQUEST_ID       = 60005;
    public static final int SELFCREATELIST_REF_REQUEST_ID   = 60006;
    public static final int F_SELFCREATELIST_REQUEST_ID     = 60007;
    public static final int F_SELFCREATELIST_REQUEST_REF_ID = 60008;
    public static final int SELFCREATEADD_REQUEST_ID        = 60009;
    public static final int SELFCREATEDEL_REQUEST_ID        = 60010;

    /**
     * 其他
     */
    public static final int OTHER_BASE_REQUEST        = 130000;
    public static final int OTHER_CHECK_OTHER_REQUEST = OTHER_BASE_REQUEST + 1;
    public static final int UPLOAD_PUSH_REGID_REQUEST = OTHER_BASE_REQUEST + 2; //上传推送regID的请求

    /**
     * 要闻页
     */
    public static final int YAOWEN_LOAD_BASE = 160000;
    public static final int YAOWEN_LOAD_MORE = YAOWEN_LOAD_BASE + 1;
    public static final int YAOWEN_LOAD_PULL = YAOWEN_LOAD_BASE + 2;
    public static final int YAOWEN_LOAD_LIST = YAOWEN_LOAD_BASE + 3;

    //超Ａ　小说离线
    public static final int DOWNLOAD_FICTION_VERSION_REQUEST_ID = 140020;
    /**
     * 首页
     */
    public static final int HOME_REQUEST_BASE                   = 150000;
    public static final int HOME_GET_LIST                       = HOME_REQUEST_BASE + 1;
    public static final int HOME_LIST_PULL                      = HOME_REQUEST_BASE + 2;
    public static final int HOME_LIST_PUSH                      = HOME_REQUEST_BASE + 3;
    public static final int HOME_LIST_DISLIKE                   = HOME_REQUEST_BASE + 4;
    public static final int HOME_LIST_BALL                      = HOME_REQUEST_BASE + 5;


    private static final int OLD_ENT_BASE              = 180001; // 原来ent包下的http请求。放到这里了。
    public static final  int ENT_GETMIBLIENO_REQUESTID = OLD_ENT_BASE + 1; //获取用户手机号.

    public static final int ENT_ENTZSCOINSPAY_REQUESTID = OLD_ENT_BASE + 2; //充值中搜币

    /**
     * im 下载gif图片
     */
    public static final int IM_REQUEST_BASE         = 190001;
    public static final int IM_DOWNLOAD_GIF_REQUEST = IM_REQUEST_BASE + 1; // im下载gif图


    /**
     * splash
     */
    public static final int SPLASH_BASE                 = 200001;
    public static final int SPLASH_SUBSCRIBESRP         = SPLASH_BASE + 1;
    public static final int SPLASH_GETGUIDERECOMMENDSRP = SPLASH_BASE + 2;
    public static final int SPLASH_FIRSTID_REQUESTID    = SPLASH_BASE + 3;
    public static final int SPLASH_GET_IMAGE_REQUEST    = SPLASH_BASE + 4;

    /**
     * 推荐
     */
    public static final int RECOMMEND_BASE            = 7000;
    public static final int RECOMMEND_SPECIAL_SWITCH  = RECOMMEND_BASE + 1;
    public static final int RECOMMEND_SPECIAL_LIST_ID = RECOMMEND_BASE + 2;

    /**
     * 首次引导
     */

    public static final int FIRST_GUIDE_BASE      = 8000;
    public static final int FIRST_GUIDE_GETDATA   = FIRST_GUIDE_BASE + 1;
    public static final int FIRST_GUIDE_SUBSCRIPE = FIRST_GUIDE_BASE + 2;

    /**
     * 图集
     */
    public static final int GALLERY_NEWS_BASE = 912300;
    public static final int GALLERY_NEWS_HOMELIST = GALLERY_NEWS_BASE + 1;
}
