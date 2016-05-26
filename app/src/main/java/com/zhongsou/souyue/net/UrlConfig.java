package com.zhongsou.souyue.net;

import com.zhongsou.souyue.module.ResponseObject;
import com.zhongsou.souyue.platform.CommonStringsApi;

@SuppressWarnings("serial")
public class UrlConfig extends ResponseObject {
    // 0 测试环境
    public static final int SOUYUE_TEST = 0;
    // 1预上线环境
    public static final int SOUYUE_PRE_ONLINE = 1;
    // 2线上环境
    public static final int SOUYUE_ONLINE = 2;

    // 3开发服务器
    public static final int SOUYUE_DEVLOPER = 3;
    // 4专门为SRP搜索提供的一套预上线测试环境
    public static final int SOUYUE_PRE_ONLINE_FOR_SRP = 4;

    // 指向服务器环境: 修改为配置项，因为超A打包时需要动态替换,在souyue_config.xml中修改即可  add by liudl
    public static final int SOUYUE_SERVICE = CommonStringsApi.SOUYUE_INTERFACE_ENV;
    public static final String HOST = getSouyueHost();
    public static final String HOST_D3API3 = getSouyueSRPHost();
    public static final String HOST_SHARE_SHORT_URL = getShareShortUrlHost();//默认获取短链的url
    public static final String HOST_SHARE_GIF_URL = HOST_SHARE_SHORT_URL+"share/gifShare.groovy?";
    public static final String HOST_SHARE_JOKE_URL = HOST_SHARE_SHORT_URL+"share/jokesShare.groovy?";
    /************************************************************************************
     *                          超级app
     *************************************************************************************/
    //小说离线阅读html的下载更新
    public static final String HOST_DOWNLOAD_HTML_ZIP = getDownload_Html_Zip();
    //小说离线html版本检测
    public static final String getDownloadFictionVersion = getDownloadFictionVersion(); //ko
    // 搜悦企业 目前只有一个环境
    private static final String HOST_PLAZA = getSouyue_Plaza();//Host 不是接口
    // 合作经营
    public static final String HOST_COOPER_MANAGE = getCooperManage(); //Host 拼网址，传入SrpWebViewActivity

    /************************************************************************************
     *                          我的账号相关
     *************************************************************************************/
    public static final String HOSE_ERROR_URL=HOST+"error.groovy";//错误页面，在首页列表中点击如果数据不匹配就跳转到这个错误页面
    // 中搜币 目前只有一个环境
    private static final String HOST_ZHONGSOU_COINS = getZhongSouCoins();
    // 积分 目前只有一个环境
    public static final String HOST_ZHONGSOU_JF = getZhongSouJF();
    // 打赏
    public static final String HOST_ZHONGSOU_REWARDS = getZhongSouRewards();
    // 我的活动
    public static final String HOST_ZHONGSOU_HD = getZhongSouHD();
    //友宝扫描结果
    public static final String HOST_YOUBAO_SCANING = getYouBaoScaning();
    // 友宝相关
    private static final String HOST_WEBNAV = getYouBao();
    //普通注册，安全中心
    private static final String HOST_REGISTER = getCommonRegister();
    // 我的积分
    public static final String HOST_INTEGRAL = getMyJF();
    public static final String HOST_ZHONGSOU_COINS_BLANCE = HOST_ZHONGSOU_COINS
            + "zsbrecord";
    //用户中心回访
    public static final String HOST_BACK_SOUYUE = getBackSouyueHost();
    public static final String HOST_ZHONGSOU_JF_BLANCE = HOST_ZHONGSOU_JF
            + "pointrecord";
    public static final String updateClientId = HOST + "updateClientId.groovy";// 更新推送id

    public static final String selfCreateAdd = HOST + "selfcreate/add.groovy"; // 增加用户原创内容
    public static final String selfCreateDel = HOST
            + "selfcreate/delete.groovy"; // 删除用户原创
    public static final String selfCreateList = HOST + "selfcreate/list.groovy"; // 用户原创列表
    public static final String friendCreateList = HOST
            + "selfcreate/list.2.groovy"; // 用户原创列表
    public static final String token = HOST + "user/token.groovy";// 获取游客token
    public static final String youbaoDuihuan = HOST_WEBNAV
            + "d3wap/ubox/orderlist.aspx";// 跳转我的赠送页面
    public static final String SecurityCenter = HOST_REGISTER+"SecurityCenter/index";//安全中心
    public static final String CommonRegister = HOST_REGISTER+"GeneralRegister/index";//普通用户注册
    public static final String ForgetPassword = "https://security.zhongsou.com/GeneralRegister/FindPwd";//选择找回账号
    public static final String EquipmentTest = "https://security.zhongsou.com/GeneralRegister/ChooseWay?fromtype=2";//新设备验证
    /************************************************************************************
     *                          搜索相关
     *************************************************************************************/
    // m端 搜索
    public static final String HOST_SHARE = getSouyueSearch();
    //圈主工具 5.2
    public static final String HOST_CIRCLE_TOOL = getSouyueCircleTool();
    // 搜索 二代词
    private static final String HOST_2X = getSearch_2X();

    // ---------------------------------垂直搜索----------------------------------------

    public static final String search2x = HOST_2X + "s?vc="
            + DeviceInfo.getAppVersion() + "&w=";
    public static final String searchWeibo = HOST_2X + "weibo?vc="
            + DeviceInfo.getAppVersion() + "&w=";
    public static final String searchNews = HOST_2X + "news?vc="
            + DeviceInfo.getAppVersion() + "&w=";
    public static final String searchBBS = HOST_SHARE
            + "bbsIndex#page_bbssearch_index?vc=" + DeviceInfo.getAppVersion()
            + "&k=";
    public static final String searchVideo = HOST_SHARE
            + "video#page_video_search?vc=" + DeviceInfo.getAppVersion()
            + "&w=";
    public static final String searchPhoto = HOST_SHARE
            + "image#page_pic_search?vc=" + DeviceInfo.getAppVersion() + "&k=";
    public static final String searchApp = HOST_SHARE
            + "apk?search_keyword=#search?vc=" + DeviceInfo.getAppVersion()
            + "&w=";
    public static final String getSrpIndexData = HOST_D3API3 + "search"; // sy4.0获取SRP  ko

    public static final String getSrpIndexTemplates = HOST_D3API3 + "templates/";   //未使用AQuery
    // ---------------------------------消歧--------------------------------------------
    public static final String searchxq = HOST_2X
            + "search2x/xiaoqi.groovy?vc=" + DeviceInfo.getAppVersion() + "&w=";
    //5.0搜索
    public static final String S_CURRENT_PAGE = HOST_SHARE + "sy5_search";
    public static final String S_INDEX_PAGE = HOST_SHARE + "sy5_index"; //ko

    /************************************************************************************
     *                          分享相关
     *************************************************************************************/
    // 老虎机
    private static final String HOST_SLOTMACHINE = getSlotmachine();
    public static final String pv = HOST + "pv/pv.groovy";// 分享统计
    public static final String share_result = HOST_WEBNAV
            + "souyueapi/sharestats.ashx";
// ---------------------------------分享垂直搜索--------------------------------------

    public static final String share_complex = HOST_SHARE
            + "#page_summary_srp?k=";
    public static final String share_news = HOST_SHARE + "infoList?keyword=";
    public static final String share_video = HOST_SHARE
            + "video#page_video_search?w=";
    public static final String share_bbs = HOST_SHARE
            + "bbsIndex#page_bbssearch_index?k=";
    public static final String share_image = HOST_SHARE
            + "image#page_pic_search?k=";
    public static final String share_app = HOST_SHARE + "apk#search?w=";
    public static final String share_weibo = "http://search.souyue.mobi/search2x/weibo.groovy?w=";
    public static final String shareUrl = HOST_SLOTMACHINE
            + "lhjshare?record_id="; // 分享打开落地页
    public static final String shareInterestCard = HOST
            + "webdata/interest.card.share.groovy?interestId=";// 分享圈名片落地页地址（兴趣圈首页菜单分享圈名片）
    public static final String shareInterestBlog = HOST
            + "webdata/interest.share.groovy?blogId=";// 分享帖子落地页地址（帖子详情分享）
    /************************************************************************************
     *                          srp相关
     *************************************************************************************/
    // SRP页广告
    public static final String adList = getSRPAD();
    //4.2.2升级广告接口
    public static final String getAdList = getAD();
    public static final String SRPURL = HOST_SHARE + "EditWidget/index?";// SRP优化主题

    public static final String SRPFINDERROR = HOST_SHARE + "Witkey/newErrorTastks?";// SRP右上角菜单报错
    // ---------------------------------分享srp？----------------------------------
    public static final String srp = HOST + "srp.groovy?keyword=";
    public static final String searchResult = HOST
            + "webdata/search.result5.1.groovy";// srp页面列表
    public static final String wendaAnswer = HOST
            + "webdata/wenda.answer.groovy";// 问答
    public static final String wendaAsk = HOST + "webdata/wenda.ask.groovy";// 问答
    public static final String wendaDetail = HOST
            + "webdata/wenda.detail.groovy";// 问答详情
    public static final String wendaDown = HOST + "webdata/wenda.down.groovy";// 问答
    // 踩
    public static final String wendaSameAsk = HOST
            + "webdata/wenda.sameAsk.groovy";// 问答
    // 同问
    public static final String wendaUp = HOST + "webdata/wenda.up.groovy";// 问答
    public static final String getPlaza_hotevents_news = HOST + "webdata/search.result.news.groovy";
    public static final String subscibe = HOST
                + "subscribe/subscribe.list.groovy?subType=1";// 我的SRP订阅词
    /************************************************************************************
     *                          发现相关
     *************************************************************************************/
    //二维码接口
    public static final String QRCODE_WEB_URL = getSouyueHost() + "function/qrcodeJump.groovy";
    // 2.x正式环境
    // 我的活动
    public static final String ZHONGSOU_HD = HOST_ZHONGSOU_HD
            + "mobile/myactive";
    public static final String gift = HOST_WEBNAV + "d3wap/mall/index.aspx"; // 中搜币商城
    // new
    public static final String sup = HOST_WEBNAV
            + "supershare/join/search.aspx";// 超级分享大赛搜索地址
    public static final String cm = HOST_WEBNAV + "supermodel/search.aspx";// 掌上超模
    public static final String youbao = HOST_WEBNAV + "d3wap/ubox/detail.aspx";// 扫描二维码获取友宝接口
    public static final String getPlazaHome = HOST_PLAZA + "square.ashx";// 广场首页  引用类已删除
    public static final String keywordVersion = HOST
            + "webdata/keywordVersion.groovy";// 检查关键词版本 ko  未使用
    public static final String loveSubscribe = HOST_PLAZA + "list.ashx";// 广场 引用类已删除
    public static final String getPlaza_self_create = HOST_PLAZA + "list.ashx";// 广场;
    public static final String getPlaza_praise_list = HOST_PLAZA + "list.ashx";
    public static final String getPlaza_share_list = HOST_PLAZA + "list.ashx";


    public static final String ADMINTOOL = HOST_SHARE + "Circletooles/index"; // 测试环境
    // 圈主工具 5.2
    public static final String CIRCLE_TOOL = HOST_CIRCLE_TOOL +"Circletooles/index";

    public static final String bible = getSouyueSearch() + "apk";//"http://m.zhongsou.com/apk";应用宝典  ko 直接引用url
    public static final String discover_list = HOST + "webdata/sy.applys.groovy";// 发现列表  ko
    /************************************************************************************
     *                          个人中心
     *************************************************************************************/
//    public static final String getPostListForPersonal = HOST
//            + "interest/personal.querymblog.list.groovy";   //个人中心点击更多获取帖子列表-------------√------------
//    public static final String login = HOST + "user/login.groovy";// 登录-------------√------------
//    public static final String register = HOST + "user/register.groovy";// 注册-------------√------------旧的注册不用了，删掉
//    public static final String register31 = HOST + "user/register3.1.groovy";// 注册-------------√------------
//    public static final String updateProfile = HOST
//            + "user/updateProfile.groovy";// 更新用户信息
//    public static final String registerValidate = HOST
//            + "user/register.validate.groovy";// 注册验证
//    public static final String report = HOST + "comment/report.groovy";// 举报
    public static final String function = "http://m.souyue.mobi/features.html";// 功能介绍
//    public static final String favoriteList = HOST
//            + "favorite/favorite.list.groovy";// 收藏列表
//    public static final String userPushMsg = HOST + "notice/userPushMsg.groovy";// 用户消息推送列表
//    public static final String noticeUserList = HOST
//            + "notice/notice.user.list.groovy";// 系统推送列表
//    public static final String loginSns = HOST + "user/login.sns.groovy";// 第三方登录
//    public static final String share = HOST + "favorite/share.add.groovy";//
    public static final String up = HOST + "favorite/up.add.groovy";
    public static final String integral = HOST_INTEGRAL
            + "index.php?s=userscore/get/";//目测应该是获取用户积分的
    public static final String web_exchange = HOST_WEBNAV
            + "d3wap/gift/for-record.aspx?token=";// 兑换记录
//    public static final String return_visit = HOST_INTEGRAL
//            + "index.php?s=ScoreApi/addScore";// 用户回访

//    public static final String back_souyue = HOST_BACK_SOUYUE
//            + "api/user/visit";// 用户
    public static final String credits_exchange = HOST_WEBNAV
            + "d3wap/gift/exchange-zsb-point.aspx?"; // 兑中搜币

//    public static final String getMoodList = HOST + "user/mood.list.groovy";// 获得心情列表
//    public static final String delMood = HOST + "user/mood.del.groovy";// 删除心情
//    public static final String addMood = HOST + "user/mood.add.groovy";// 添加心情
//    public static final String getUpdateInfo = HOST + "checkVersion.groovy";//获得版本更新信息
//    public static final String getReplyMeList = HOST
//            + "interest/follow.my.blog.groovy";// 添加心情
    // ---------------------------------获取验证码--------------------------------------------
//    public static final String code_regist = HOST
//            + "user/sendMobileVerify.groovy?mobile=";//-------------√------------
//    public static final String code_reset = HOST
//            + "users/zsresetpwd_getverify.json?phonenum=";// 预留
//    public static final String giveCoinAfterSendSmsToInviteUser = HOST
//            + "user/sms_recommend.groovy";// 发送短信给好友邀请注册，成功后赠送中搜币
//    public static final String update_pwd = HOST + "user/updatePwd.groovy?";
//    public static final String getPersonalCenterInfo = HOST
//            + "user/userCenterInfo.groovy"; // 个人中心头部数据接口
    // ---------------------------------4.1--------------------------------------------------
//    public static final String interestForUser = HOST
//            + "interest/personal.interest.list.groovy"; // 个人中心点击更多获取兴趣圈列表
//    public static final String zhutiForUser = HOST
//            + "interest/personal.interest.sub.groovy"; // 个人中心点击更多获取主题列表
//    public static final String getPostListForPerson = HOST
//            + "interest/personal.mblog.list.groovy"; // 个人中心点击更多获取帖子列表
    /************************************************************************************
     *                          详情页
     *************************************************************************************/

    public static final String commentAdd = HOST + "comment/comment.add.groovy";// 添加评论
    public static final String commentList = HOST
            + "comment/comment.list.groovy";// 评论列表
    public static final String commentListMy = HOST
            + "interest/comment4.3.list.my.groovy";// 我的评论列表
    public static final String favoriteAdd = HOST
            + "favorite/favorite.add.groovy";// 添加收藏
    public static final String newsCount = HOST + "webdata/news.count.groovy";// 新闻详情顶数，评论数
    public static final String shortURL = HOST + "shortURL.groovy";// 短链接
    public static final String web_nav = HOST_WEBNAV + "d3wap/nav/main.aspx";

    /************************************************************************************
     *                          订阅
     *************************************************************************************/
    public static final String subscribeAdd = HOST
            + "subscribe/subscribe.add.groovy";// 添加订阅
    public static final String subscribeList = HOST
            + "subscribe/subscribe.list.groovy";// 订阅列表
    public static final String subscribeDelete = HOST
            + "subscribe/subscribe.delete.groovy";// 删除订阅
    public static final String groupModifyNew = HOST
            + "subscribe/subscribe.cate.modify.groovy";// 不记得来，也是和订阅相关的
    public static final String subscribeModify = HOST
            + "subscribe/subscribe.modify.groovy";// 同上
    public static final String subscribeMove = HOST
            + "subscribe/subscribe.move.groovy";// 移动订阅
    public static final String rssCateList = HOST
            + "subscribe/rss.cate.list.groovy"; // 新闻源订阅分组
    public static final String cateTree30 = HOST + "cate.tree3.0.groovy";// 取订阅列表页面
    public static final String srpSubscribe30 = HOST
            + "subscribe/srp.subscribe3.0.groovy";// 订阅srp词
    public static final String entAdd = HOST + "ent.add.groovy";// 企业词订阅
    public static final String subscribeCheck30 = HOST
            + "subscribe/subscribe.check3.0.groovy";// 检查关键词是否订阅
    public static final String subscribeGroupHomepage = HOST
            + "subscribe/subscribe.group.homepage.groovy";// 订阅列表管理
    public static final String subscribeGroupList = HOST
            + "subscribe/subscribe.group.list.groovy";// 获取订阅组内容
    /************************************************************************************
     *                          兴趣圈
     *************************************************************************************/

    public static final String interestGroup = HOST
            + "interest/interest.my.groovy";// 我的兴趣圈

    // --------------------------------------兴趣圈------------------------------------------------------

    public static final String clearPostCount = HOST
            + "interest/interest.user.tips.clear.groovy";// 清空兴趣圈主界面获取@帖子个数
    public static final String getRecommendCircleMethod = HOST
            + "interest/interest.guide.list.groovy";// 首页推荐兴趣圈列表
    public static final String saveRecommendCircleMethod = HOST // 订阅兴趣圈
            + "interest/interest.subscriber.groovy";
    public static final String getNewEssencePost = HOST
            + "interest/prime4.0.mblog.list.groovy";// 4.0精华区主页
    public static final String getSingleCricleList = HOST
            + "interest/bar.mblog.list.groovy";// 圈子主贴列表
    public static final String getNewSingleCricleList = HOST
            + "interest/bar4.0.mblog.list.groovy";// 圈子主贴列表(4.0接口)
    public static final String getCricleManageInfo = HOST
            + "interest/bar.manage.info.groovy";// 圈吧管理信息
    public static final String cancelCollect = HOST
            + "favorite/favorite.delete.groovy";// 取消收藏
    public static final String toTop = HOST + "interest/mblog.dotop.groovy"; // 帖子置顶
    public static final String toPrime = HOST + "interest/mblog.doprime.groovy"; // 帖子加精
    public static final String deletePosts = HOST + "interest/blog.del.groovy"; // 帖子删除
    public static final String cancleTotop = HOST
            + "interest/mblog.undotop.groovy"; // 帖子取消置顶
    public static final String canclePrime = HOST
            + "interest/mblog.undoprime.groovy"; // 帖子取消加精
    public static final String getMemberList = HOST
            + "interest/member.list.groovy"; // 圈成员列表
    public static final String imFriend = HOST
            + "interest/interest.invitation.groovy"; // 邀请好友加入圈
    public static final String banTalk = HOST
            + "interest/member.bantalk.groovy"; // 禁言操作
    public static final String kickCircle = HOST
            + "interest/member.kicked.out.groovy"; // 踢出圈子操作
    public static final String tuiSong = HOST + "interest/mblog.push.groovy";// 帖子推送
    public static final String MINE_PURSE_LIST = HOST
            + "webdata/sy.pay.groovy";// 钱包列表
    public static final String savePosts = HOST
            + "interest/blog.save.groovy?vc=" + DeviceInfo.getAppVersion();// 保存帖子
    public static final String saveSendInfo = getsaveSendInfo(); //圈吧信息发布微件地址

    private static String getsaveSendInfo() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://202.108.1.109/webapi/savearticle";//"http://hems3.zhongsou.com/webapi/savearticle"
            case SOUYUE_PRE_ONLINE:
                return "http://202.108.1.114/webapi/savearticle";
            case SOUYUE_ONLINE:
                return "http://edit.zhongsou.com/webapi/savearticle";
            default:
                return "http://edit.zhongsou.com/webapi/savearticle";
        }
    }
    public static final String getMemberRole = HOST
            + "interest/member.role.groovy"; // 用户在兴趣圈中的角色
    // 0-非圈子成员
    // 1-圈主
    // 2-圈子普通成员
    // 3-游客
    public static final String updateCricleManageUserInfoSetting = HOST
            + "interest/member.setinfo.groovy";// 圈吧管理-用户信息设置
    public static final String updateCricleManageQuitSetting = HOST
            + "interest/member.exitinterest.groovy";// 圈吧管理-退出圈子
    public static final String updatePrivateInfoSetting = HOST
            + "interest/member.setprivate.groovy";// 圈吧管理-修改是否保护隐私
    public static final String deleteComment = HOST
            + "interest/reply.del.groovy";
    public static final String getAtMePostList = HOST
            + "interest/mentionblog.list.groovy";// 获取at我的帖子列表
    public static final String sharePostToprime = HOST
            + "interest/blog.share.groovy";// 分享帖子到精华帖
    public static final String getInterestListAll = HOST
            + "interest/member.interest.list.groovy";// 用户订阅的兴趣圈列表接口
    public static final String getInterestTags = HOST
            + "interest/interest.tags.list.groovy"; // 获取兴趣圈所有标签


    // --------------------------------------私密圈------------------------------------------------------
    public static final String getCircleCardInfo = HOST
            + "interest/interest.carte.info.groovy";// 获取兴趣圈圈名片信息
    public static final String getRelationWithCircle = HOST
            + "interest/interest.user.audit.status.groovy";// 获取用户与某个私密圈关系的接口
    public static final String applyForSecretCircle = HOST
            + "interest/interest.apply.groovy";// 私密圈申请加入接口
    public static final String getApplyTips = HOST
            + "interest/interest.my.apply.tips.groovy";// 获取用户私密圈申请记录数量的接口
    public static final String getMemberApplyList = HOST
            + "interest/member.interest.apply.list.groovy";// 用户私密圈申请记录列表接口
    public static final String getCircleMenu = HOST
            + "interest/interest.menu.info.groovy";// 获取圈子菜单信息接口
    public static final String getPrimeHeader = HOST    //精华区头部数据接口
            + "interest/prime.head.groovy";
    public static final String getInterestIMGroupList = HOST    //圈子IM群列表接口
            + "interest/im.group.list.groovy";
    public static final String getCircelMemberInfo = HOST
            + "interest/interest.member.personal.info.groovy"; // 获取圈成员信息接口
    public static final String getUserRecommendList = HOST
            + "interest/user.recommend.list.groovy";// 获取用户推荐列表
    public static final String getUserRecommendInfo = HOST
            + "interest/user.recommend.info.groovy";//获取用户推荐详情
    public static final String getSysRecommendList = HOST
            + "interest/sys.recommend.list.groovy";//获取系统推荐列表
    public static final String getSysRecommendInfo = HOST
            + "interest/sys.recommend.info.groovy";//获取系统推荐详情
    public static final String auditRecommend = HOST
            + "interest/recommend.audit.groovy";//收稿箱审批接口
    public static final String getFreetrialInfo = HOST
            + "interest/interest.freetrial.info.groovy";//获得免审状态
    public static final String setFreetrial = HOST
            + "interest/interest.setfreetrial.groovy";//设置免审
    public static final String getMemberInfo = HOST
            + "interest/member.info.groovy";//获取成员信息
    public static final String getMemberPostList = HOST
            + "interest/member.mblog.list.groovy";//获取成员列表
    public static final String setbgimg = HOST
            + "interest/member.setbgimg.groovy";//设置图像
    public static final String news_share = HOST
            + "interest/sy.share.to.interest.groovy?vc="//新闻分享到兴趣圈
            + DeviceInfo.getAppVersion();
    public static final String client_config = HOST
            + "client/clientConfig.groovy?vc="
            + DeviceInfo.getAppVersion();//控制客户端数据及开关
    public static final String inviteFriend = HOST
            + "interest/interest.private.invitation.groovy"; // 私密圈邀请好友
    public static final String HIDE_TABRED_ACTION = "com.zhongsou.im.souyuemsg.hide";// 隐藏tab栏气泡action
    public static final String isSRPAdmin = HOST + "interest/srp.isamdin.groovy";//判定是否是srp管理员
    // --------------------------------------4.0------------------------------------------------------
    public static final String HOMP_PAGE_URL = HOST
            + "webdata/cate.recommend.groovy";//搜悦首页新闻


    /************************************************************************************
     *                          其他
     *************************************************************************************/
//    public static final String COLLECTLOG = HOST + "notice/notice.accept.groovy";// 统计推送日志
    public static final String urlContent = HOST
            + "webdata/urlContent.groovy?client=souyue";
    public static final String groupList = HOST + "subscribe/group.list.groovy";// 分组列表
//    public static final String groupSettingModify = HOST
//            + "subscribe/group.setting.modify.groovy";// 移动删除分组
    public static final String newsDetail = HOST + "webdata/news.detail.groovy";//
    public static final String rssList = HOST + "subscribe/rss.list.groovy";// 新闻源

    public static final String noticeList = HOST + "notice/notice.list.groovy";// 通知
    public static final String groupModify = HOST
            + "subscribe/group.modify.groovy";// 修改分组

    public static final String tooltip = HOST + "webdata/tooltip.groovy";// 搜索tip提示
    public static final String upload = HOST + "comment/upload.groovy";// 上传评论
    public static final String speed = HOST + "speed.groovy";// 用于记录网速  ko  不再使用

    // ---------------------------------首页广告地址-------------------------------------
    public static final String homecomplaint = HOST + "news/complaints.groovy";//投诉 ko 使用CVolley
    public static final String contactUsUrl = HOST + "html/contactus.html";// 联系我们  ko  直接load
    public static final String userAgreementUrl = "http://m.souyue.mobi/protocol.html";// 用户协议 -- YanBin ko 直接load

    // ------------------------------------3.0新增---------------------------------------

    public static final String homepage30 = HOST + "homepage3.0.groovy";// 左侧菜单,    ko 未使用

    public static final String RecommentGroup = HOST_PLAZA + "group.ashx";// 人物、企业频道导航： ko

    public static final String slidingmenu = HOST + "homepage4.0.groovy";// 左树菜单部分 ko 未使用

    // -----------------------------chat room----------------------
    public static final String chatRoomList = HOST + "chat/chat.list.groovy?";
    public static final String chatRoomAdd = HOST + "chat/chat.add.groovy?";

    /*************************************************************************************
     *                               目测以下都是volley请求
     *************************************************************************************/
    //详情新增接口
    public static final String mUrlWidgetHead = HOST + "webdata/widget.head.groovy";//获取详情头信息
    public static final String mUrlWidgetSecondList = HOST + "webdata/widget.list.groovy";//获二级导航菜单
    public static final String mCommentCount = HOST + "interest/comment.count.groovy";//获取评论数，点赞数等
    public static final String mCommentUp = HOST + "interest/interest.comment.dogoodpc.groovy";//点赞
    public static final String mCommentDown = HOST + "interest/comment.dobad.groovy";//拍
    public static final String mFavoriteAdd = HOST + "interest/favorite4.3.add.groovy";//收藏

    //详情页模板接口
    public static final String mUrlGetDetailData = HOST + "detail/widget.head.list.groovy";//获二级导航菜单
    public static final String mUrlGetContent = HOST + "webdata/detail.data.offline.groovy";//详情页数据新闻
    public static final String mUrlGetContentCircle = HOST + "detail/detail.data.interest.groovy";//详情页数据帖子
    public static final String mUrlGetCommentData = HOST + "detail/comment5.0.list.count.groovy";//获详情页评论数据

//    public static final String getBlogCommentsNew = HOST + "interest/comment.reply.list.groovy";  //4.2.2获取回复列表
//    public static final String commentDetail = HOST + "interest/comment.add.groovy";  //4.2.2评论接口
//    public static final String deleteCommentNew = HOST + "interest/comment.del.groovy"; //4.2.2删除评论
//    public static final String replyNew = HOST + "interest/comment.reply.add.groovy"; //4.2.2回复接口

    //图集页面的新接口
    public static final String gallerynewsHome = HOST + "photos/details.groovy"; // 可获取大图图集和推荐图集
    //表情列表
    public static final String getExpressionListUrl = HOST + "im/package_list.groovy";
    public static final String getExpressionDetailUrl = HOST + "im/package_detail.groovy";
    public static final String getExpressionDownload = HOST + "im/download_package.groovy";

    //订阅
    public static final String SUBER_ALL_INTEREST_ADD_URL = HOST + "interest/interest.subscriber.groovy"; //兴趣圈 订阅
    public static final String SUBER_ALL_INTEREST_DELETE_URL = HOST + "interest/member.exitinterest.groovy";// 圈吧管理-退出圈子
    public static final String SUBER_ALL_INTEREST_GROUP_URL = HOST + "interest/group.list.groovy";
     // 订阅 group
    public static final String SUBER_ALL_INTEREST_CHILD_URL = HOST + "interest/group.interest.groovy";  // 订阅 child


    public static final String SUBER_INFO_URL = HOST
            + "subscribe/subscribe.list.my5.0.groovy";// 订阅列表页
    public static final String SUBER_REMCOMMEND_URL = HOST
            + "recommend/top.recommend.list.groovy";// 热门推荐
    public static final String SUBER_UPDATE_URL = HOST
            + "subscribe/subscribe.update.my5.0.groovy"; // 取消订阅
    public static final String SUBER_SERACH_URL = HOST
            + "recommend/search.enjoy.content.groovy"; // 订阅搜索
    public static final String SUBER_ORDER_URL = HOST
            + "subscribe/subscribe.modify5.0.groovy"; // 订阅排序
    public static final String SUBER_GET_SWITCH_URL = HOST + "subscribe/user.switch.query.groovy";  // 获取订阅开关
    public static final String SUBER_POST_SWITCH_URL = HOST + "subscribe/user.switch.cfg.groovy";  //

    //im服务号查看群详情
    public static final String IMWatchServiceMsgDetail = HOST + "im/showHistoryPage.groovy";

    public static final String channelList = HOST + "webdata/channel/channel.list.groovy"; //获取频道管理的列表
    public static final String editChannel = HOST + "webdata/channel/channel.update.groovy"; //订阅频道后的保存接口
    public static final String newGetCommentList = HOST + "interest/comment5.0.list.groovy";//5.0评论列表
    public static final String sethot = HOST + "interest/comment.sethot.groovy";//把某条评论设置为热门评论
    public static final String specialRecommendList = HOST + "webdata/focus.recommend.groovy";//5.0评论列表

    //5.0首页接口
    public static final String GET_SHOW_HOME_BALL = HOST + "subscribe/srp.interest.sub5.2.groovy"; //首页球球接口
    public static final String GET_SHOW_HOME_LIST = HOST + "webdata/homepage.news5.0.groovy"; //首页列表接口
    public static final String GET_SHOW_HOME_DISLIKE = HOST + "news/disLike.groovy"; //首页不感兴趣接口

    public static final String GET_USER_BG = HOST + "user/myBack.img.groovy"; //获取用户背景图

    public static final String GET_USER_RECOMMEND = HOST + "user/recommendSpecialSwitch.groovy"; //是否展示系统推荐数据


    //5.0获取引导页
    public static final String getGuideRecommandSpecial = HOST + "webdata/guide.novice.groovy";
    public static final String getSplashImage = HOST + "webdata/startImg.config.groovy";
    public static final String getGuideRecommandSRP = HOST + "subscribe/subscribe.guide.list.groovy";
    public static final String subscribeGuideRecommandSRP = HOST + "subscribe/subscribe.guide.groovy";

    //5.0.7
    /**
     * 新手引导地址
	    1.新手引导页地址为http://m.souyue.mobi/guide20151106/index.html；
	    2.新手引导页懒加载地址为http://m.souyue.mobi/guide201511062/index.html
		应该用第二个
     */
    public static final String NEW_USER_GUIDE = "http://m.souyue.mobi/guide201511062/index.html";

    //    红包接口 ,领取红包
    public static final String WXSHARE_REQUEST_DEBUG = "http://jftest.zhongsou.com/";
    public static final String WXSHARE_REQUEST_PRE_ONLINE = "http://jfpre.zhongsou.com/";
    public static final String WXSHARE_REQUEST_ONLINE = "http://jf.zhongsou.com/";

    public static final String getWXShareJFApi() {
        String apiurl = "";
        switch (SOUYUE_SERVICE) {
            //测试环境
            case SOUYUE_TEST:
                apiurl = WXSHARE_REQUEST_DEBUG;
                break;
            //预上线环境
            case SOUYUE_PRE_ONLINE:
                apiurl = WXSHARE_REQUEST_PRE_ONLINE;
                break;
            //搜索预上线
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                apiurl = WXSHARE_REQUEST_DEBUG;
                break;
            //线上环境
            case SOUYUE_ONLINE:
                apiurl = WXSHARE_REQUEST_ONLINE;
                break;
            //开发环境
            case SOUYUE_DEVLOPER:
                apiurl = WXSHARE_REQUEST_DEBUG;
                break;
        }
        return apiurl + "redback/getUserInfo";
    }

    ;


    //默认分享图片 update qqsdk to 2.9.1
    public static final String getDefShareImage() {
        return getSouyueHost().concat("images/souyue-logo.png");
//        switch (SOUYUE_SERVICE){
//            case SOUYUE_TEST:
//                return "http://103.29.134.224/d3api2/images/souyue-logo.png";
//            default:
//                return "http://103.29.134.224/d3api2/images/souyue-logo.png";
//        }
    }

    // 搜悦环境
    public static String getSouyueHost() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://103.29.134.224/d3api2/";
            case SOUYUE_PRE_ONLINE:
                return "http://103.29.134.225/d3api2/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://111.206.69.38:8111/d3api2/";
            case SOUYUE_ONLINE:
                return "http://api2.souyue.mobi/d3api2/";

            case SOUYUE_DEVLOPER:
                return "http://61.135.210.239:8888/d3api2/";
            default:
                return "http://api2.souyue.mobi/d3api2/";
        }
    }

    // 用户中心回访
    public static String getBackSouyueHost() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://usercentertest.zhongsou.com/";
            case SOUYUE_PRE_ONLINE:
                return "http://usercenter.zhongsou.com/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://usercentertest.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://usercenter.zhongsou.com/";
            case SOUYUE_DEVLOPER:
                return "http://usercentertest.zhongsou.com/";
            default:
                return "http://usercenter.zhongsou.com/";
        }
    }

    // 搜悦SRP首页环境
    private static String getSouyueSRPHost() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://103.29.135.93:2046/d3api3/";
            case SOUYUE_PRE_ONLINE:
                return "http://103.7.220.208:2046/d3api3/";// SRP首页现在貌似没有预上线环境
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://103.7.220.208:2046/d3api3/";
            case SOUYUE_ONLINE:
                return "http://api2.souyue.mobi/d3api3/";
            default:
                return "http://api2.souyue.mobi/d3api3/";
        }
    }

    // 搜悦分享获取短链的主机
    private static String getShareShortUrlHost() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://newcmstest.zhongsou.com/d3api3/";
            case SOUYUE_PRE_ONLINE:
                return "http://newcmstest.zhongsou.com/d3api3/";
            case SOUYUE_DEVLOPER:
                return "http://newcmstest.zhongsou.com/d3api3/";
            case SOUYUE_ONLINE:
                return "http://sycms.zhongsou.com/d3api3/";
            default:
                return "http://newcmstest.zhongsou.com/d3api3/";
        }
    }


    //小说离线阅读html的下载更新

    private static String getDownload_Html_Zip() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://103.29.135.93/nreaderOffline.zip";
            case SOUYUE_PRE_ONLINE:
                return "http://103.7.220.208/nreaderOffline.zip";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://103.7.220.208/nreaderOffline.zip";
            case SOUYUE_ONLINE:
                return "http://open.zhongsou.com/nreaderOffline.zip";
            default:
                return "http://open.zhongsou.com/nreaderOffline.zip";
        }
    }

    //小说离线html版本检测
    private static String getDownloadFictionVersion() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://103.29.135.93/novelpack?tst=1";
            case SOUYUE_PRE_ONLINE:
                return "http://103.7.220.208/novelpack?tst=1";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://103.7.220.208/novelpack?tst=1";
            case SOUYUE_ONLINE:
                return "http://open.zhongsou.com/novelpack?tst=1";
            default:
                return "http://open.zhongsou.com/novelpack?tst=1";
        }
    }

    // 搜悦企业 目前只有一个环境
    private static String getSouyue_Plaza() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://d3cms.zhongsou.com/souyue/api/";
            case SOUYUE_PRE_ONLINE:
                return "http://d3cms.zhongsou.com/souyue/api/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://d3cms.zhongsou.com/souyue/api/";
            case SOUYUE_ONLINE:
                return "http://d3cms.zhongsou.com/souyue/api/";
            default:
                return "http://d3cms.zhongsou.com/souyue/api/";
        }
    }

    // 中搜币 目前只有一个环境
    private static String getZhongSouCoins() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://cms.jf.zhongsou.com/souyue/";
            case SOUYUE_PRE_ONLINE:
                return "http://cms.jf.zhongsou.com/souyue/";// SRP首页现在貌似没有预上线环境
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://cms.jf.zhongsou.com/souyue/";
            case SOUYUE_ONLINE:
                return "http://cms.jf.zhongsou.com/souyue/";
            default:
                return "http://cms.jf.zhongsou.com/souyue/";
        }
    }

    // 积分 目前只有一个环境
    private static String getZhongSouJF() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://jf.zhongsou.com/souyueintegral/";
            case SOUYUE_PRE_ONLINE:
                return "http://jf.zhongsou.com/souyueintegral/";// SRP首页现在貌似没有预上线环境
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://jf.zhongsou.com/souyueintegral/";
            case SOUYUE_ONLINE:
                return "http://jf.zhongsou.com/souyueintegral/";
            default:
                return "http://jf.zhongsou.com/souyueintegral/";
        }
    }

    // 打赏
    private static String getZhongSouRewards() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://mtest.zhongsou.com/circlerewards/";
            case SOUYUE_PRE_ONLINE:
                return "http://moltest.zhongsou.com/circlerewards/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://moltest.zhongsou.com/circlerewards/";
            case SOUYUE_ONLINE:
                return "http://m.zhongsou.com/circlerewards/";
            case SOUYUE_DEVLOPER:
                return "http://mtest.zhongsou.com/circlerewards/";
            default:
                return "http://m.zhongsou.com/circlerewards/";
        }
    }

    // 我的活动
    private static String getZhongSouHD() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://hdgl.test.zae.zhongsou.com/";
            case SOUYUE_PRE_ONLINE:
                return "http://hd.zae.zhongsou.com/";// 没有预上线环境
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://hd.zae.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://hd.zae.zhongsou.com/";
            default:
                return "http://hd.zae.zhongsou.com/";
        }
    }

    // m端 搜索
    private static String getSouyueSearch() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://mtest.zhongsou.com/";
            case SOUYUE_PRE_ONLINE:
                return "http://moltest.zhongsou.com/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://moltest.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://m.zhongsou.com/";
            case SOUYUE_DEVLOPER:
                return "http://mtest.zhongsou.com/";
            default:
                return "http://m.zhongsou.com/";
        }
    }

    /**
     * 圈主工具 5.2
     * @return
     */
    private static String getSouyueCircleTool() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://circletooltest.zhongsou.com/";
            case SOUYUE_PRE_ONLINE:
                return "http://circletoolpre.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://circletool.zhongsou.com/";
            case SOUYUE_DEVLOPER:
                return "http://circletooltest.zhongsou.com/";
            default:
                return "http://circletool.zhongsou.com/";
        }
    }

    // 老虎机
    private static String getSlotmachine() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://lhj.test.zae.zhongsou.com/api/";
            case SOUYUE_PRE_ONLINE:
//                return "http://lhj.zae.zhongsou.com/api/";
                return "http://lhj.dev.zae.zhongsou.com/api/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://lhj.dev.zae.zhongsou.com/api/";
            case SOUYUE_ONLINE:
                return "http://lhj.zae.zhongsou.com/api/";
            default:
                return "http://lhj.zae.zhongsou.com/api/";
        }
    }

    // 搜索 二代词
    private static String getSearch_2X() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://202.108.1.229:8080/";
            case SOUYUE_PRE_ONLINE:
                return "http://search.souyue.mobi/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://search.souyue.mobi/";
            case SOUYUE_ONLINE:
                return "http://search.souyue.mobi/";
            default:
                return "http://search.souyue.mobi/";
        }
    }

    // SRP页面广告
    private static String getSRPAD() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://souyuetestad.zhongsou.com/souyueapi/getAdFromKw";
            case SOUYUE_PRE_ONLINE:
                return "http://ad.souyue.mobi/souyueadapi/getAdFromKw";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://ad.souyue.mobi/souyueadapi/getAdFromKw";
            case SOUYUE_ONLINE:
                return "http://ad.souyue.mobi/souyueadapi/getAdFromKw";
            default:
                return "http://ad.souyue.mobi/souyueadapi/getAdFromKw";
        }
    }

    //4.2.2升级广告接口
    private static String getAD() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                //测试环境域名：	202.108.1.109 api.ad.zhongsou.com
                return "http://api.ad.zhongsou.com/app/get.json";
            case SOUYUE_PRE_ONLINE:
                //预上线环境域名： 61.135.210.44 api.ad.zhongsou.com
                return "http://api.ad.zhongsou.com/app/get.json";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://api.ad.zhongsou.com/app/get.json";
            case SOUYUE_ONLINE:
                return "http://api.ad.zhongsou.com/app/get.json";
            default:
                return "http://api.ad.zhongsou.com/app/get.json";
        }
    }

    // 友宝相关"http://n.zhongsou.net/"
    private static String getYouBao() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
//          这里要求测试环境也得按照正式环境的来
                return "http://202.108.33.137:8122/";
            case SOUYUE_PRE_ONLINE:
                return "http://n.zhongsou.net/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://n.zhongsou.net/";
            case SOUYUE_ONLINE:
                return "http://n.zhongsou.net/";
            default:
                return "http://n.zhongsou.net/";
        }
    }
    // 普通注册和安全中心
    private static String getCommonRegister() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
//          这里要求测试环境也得按照正式环境的来
                return "https://securitytest.zhongsou.com/";
            case SOUYUE_PRE_ONLINE:
                return "https://securitypre.zhongsou.com/";
            case SOUYUE_DEVLOPER:
                return "https://securitytest.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "https://security.zhongsou.com/";
            default:
                return "https://security.zhongsou.com/";
        }
    }

    //友宝扫描结果
    private static String getYouBaoScaning() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
//          这里要求测试环境也得按照正式环境的来
                return "http://v.dev.uboxol.com/";
            case SOUYUE_PRE_ONLINE:
                return "http://v.ubox.cn/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://v.ubox.cn/";
            case SOUYUE_ONLINE:
                return "http://v.ubox.cn/";
            default:
                return "http://v.ubox.cn/";
        }
    }


    // 我的积分
    private static String getMyJF() {

        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://103.29.134.124:8081/";
            case SOUYUE_PRE_ONLINE:
                return "http://d3sc.zhongsou.com/";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://d3sc.zhongsou.com/";
            case SOUYUE_ONLINE:
                return "http://d3sc.zhongsou.com/";
            default:
                return "http://d3sc.zhongsou.com/";
        }

    }

    // 控制推送以及IM
    public static int getPUSH_TEST() {
        int pushTest = 2;
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                pushTest = 0;
                break;
            case SOUYUE_DEVLOPER:
                pushTest = 3;
                break;
            case SOUYUE_PRE_ONLINE:
                pushTest = 1;
                break;
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                pushTest = 4;
                break;
            case SOUYUE_ONLINE:
                pushTest = 2;
                break;
            default:
                pushTest = 2;
                break;
        }
        return pushTest;
    }

    /**
     * 赠送中搜币
     *
     * @return
     */
    public static String getSendCoinUrl() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://zspay.test.zhongsou.com/index/index?pf=zsbgrant";
            case SOUYUE_PRE_ONLINE:
                return "http://zspay.zhongsou.com/index/index?pf=zsbgrant";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://zspay.zhongsou.com/index/index?pf=zsbgrant";
            case SOUYUE_ONLINE:
                return "http://zspay.zhongsou.com/index/index?pf=zsbgrant";
            default:
                return "http://zspay.zhongsou.com/index/index?pf=zsbgrant";
        }
    }

    /**
     * 统计地址URL
     *
     * @return 统计地址
     */
    public static String sendCountUrl() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://103.29.135.92/logsy";
            case SOUYUE_PRE_ONLINE:
                return "http://103.29.135.92/logsy";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "http://103.29.135.92/logsy";
            case SOUYUE_ONLINE:
                return "http://log.souyue.mobi/logsy";
            default:
                return "http://103.29.135.92/logsy";
        }
    }

    /**
     * Srp合作经营
     *
     * @return
     */
    private static String getCooperManage() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://srpkf.zhongsou.com/Apieinlet/entrance";
            case SOUYUE_DEVLOPER:
                return "http://srpkf.zhongsou.com/Apieinlet/entrance";
            case SOUYUE_PRE_ONLINE:
                return "http://e.zhongsou.com/Apieinlet/entrance";
            case SOUYUE_ONLINE:
                return "http://e.zhongsou.com/Apieinlet/entrance";
            default:
                return "http://e.zhongsou.com/Apieinlet/entrance";
        }
    }

    /**
     * 广告秘钥
     *
     * @return String key = "!zs-ad@#ll!~@#";//app_secrect（仅限预上线环境与线上环境修改为：!zs-ad@#ll!~@#，测试环境app_secrect不变，仍为1）
     */
    public static String getSouyueADHost() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "1";
            case SOUYUE_PRE_ONLINE:
            case SOUYUE_ONLINE:
                return "!zs-ad@#ll!~@#";
            default:
                return "!zs-ad@#ll!~@#";
        }
    }

    /**
     * 根据搜悦环境区分JPush的TAG
     * @return
     */
    public static String getJPushTag() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "souyue_jpush_test_520";
            case SOUYUE_PRE_ONLINE:
                return "souyue_jpush_pre_520";
            case SOUYUE_ONLINE:
                return "souyue_jpush_520";
            default:
                return "souyue_jpush_test_520";
        }
    }

    /**
     * 根据搜悦环境区分MiPush的Topic（标签）
     * @return
     */
    public static String getMiPushTopic() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "souyue_mipush_test_520";
            case SOUYUE_PRE_ONLINE:
                return "souyue_mipush_pre_520";
            case SOUYUE_ONLINE:
                return "souyue_mipush_520";
            default:
                return "souyue_mipush_test_520";
        }
    }

    /**
     //     * 根据搜悦环境区分HuaweiPush的Topic（标签）
     //     * @return
     //     */
//    public static String getHwPushTopic() {
//        switch (SOUYUE_SERVICE) {
//            case SOUYUE_TEST:
//                return "souyue_hwpush_test";
//            case SOUYUE_PRE_ONLINE:
//                return "souyue_hwpush_pre";
//            case SOUYUE_ONLINE:
//                return "souyue_hwpush";
//            default:
//                return "souyue_hwpush";
//        }
//    }

    /**
     * 是否是测试环境
     * @return
     */
    public static boolean isTest() {
        boolean ret = false;
        switch (SOUYUE_SERVICE) {
            case SOUYUE_DEVLOPER:
            case SOUYUE_TEST:
                ret = true;
                break;
            case SOUYUE_PRE_ONLINE:
            case SOUYUE_ONLINE:
                ret = false;
                break;
        }
        return ret;
    }

    /**
     * 发送红包跳转的webview url
     *
     * @return
     */
    public static String getSendRedPacketUrl() {
        switch (SOUYUE_SERVICE) {
            case SOUYUE_TEST:
                return "http://zspay.test.zhongsou.com/Redpacket/send";
            case SOUYUE_PRE_ONLINE:
                return "https://zspaypre.zhongsou.com/Redpacket/send";
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                return "https://zspaypre.zhongsou.com/Redpacket/send";
            case SOUYUE_ONLINE:
                return "https://zspay.zhongsou.com/Redpacket/send";
            default:
                return "https://zspay.zhongsou.com/Redpacket/send";
        }
    }
}
