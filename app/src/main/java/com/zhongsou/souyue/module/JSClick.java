package com.zhongsou.souyue.module;

import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import org.json.JSONObject;

import java.util.List;

@SuppressWarnings("serial")
public class JSClick extends ResponseObject {
    private String category = "";
    private String keyword = "";
    private String srpId = "";
    private String title = "";
    private String url = "";
    private String image = "";
    private String description = "";
    private String phoneNumber = "";
    private String md5 = "";
    private String callback;
    private String interest_name;
    private String interest_id;
    private String interest_logo;
    private String blog_id;
    private String user_id;
    private String ZSBcount;
    private String images;
    private String image_url;
    private String nickname;
    private String create_time;
    private String update_time;
    private String blogId;
    //新闻的来源
    private String source;
    private String date;

    //ZSSDK  add by YanBin
    private String opSource;

    //add by yinguanping 新加密:js
    private String data;
    private TaskCenterInfo guideInfo;
    private String brief;
    private long pubTime;       //图集要用到的时间

    private String slat;
    private String slng;
    private String sname;
    private String dlat;
    private String dlng;
    private String dname;
    private String dev;
    private String m;

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getSlng() {
        return slng;
    }

    public void setSlng(String slng) {
        this.slng = slng;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getDlat() {
        return dlat;
    }

    public void setDlat(String dlat) {
        this.dlat = dlat;
    }

    public String getDlng() {
        return dlng;
    }

    public void setDlng(String dlng) {
        this.dlng = dlng;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getDev() {
        return dev;
    }

    public void setDev(String dev) {
        this.dev = dev;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getOrigin_region() {
        return origin_region;
    }

    public void setOrigin_region(String origin_region) {
        this.origin_region = origin_region;
    }

    public String getDestination_region() {
        return destination_region;
    }

    public void setDestination_region(String destination_region) {
        this.destination_region = destination_region;
    }

    private String region;
    private String origin_region;
    private String destination_region;

    
    public String getBrief() {
        return this.brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public TaskCenterInfo getGuideInfo() {
        return this.guideInfo;
    }

    public void setGuideInfo(TaskCenterInfo guideInfo) {
        this.guideInfo = guideInfo;
    }
    public String getBlogId() {
        return this.blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCreate_time() {
        return this.create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getImage_url() {
        return this.image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImages() {
        return this.images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }


    public String getOpSource() {
        return opSource;
    }

    public void setOpSource(String opSource) {
        this.opSource = opSource;
    }

    private String appname;
    private String type;
    private User userInfo;
    private String sourceUrl;


    private String notice;

    private String sign_id;

    private String mOrginalSource;

    public JSONObject getPost() {
        return post;
    }

    public void setPost(JSONObject post) {
        this.post = post;
    }

    private JSONObject post;    //回复评论的数据


    private boolean isget_signid;
    private boolean isget_blog_info;
    private boolean adminMoreOper;

    private boolean isget_news_info;

    private boolean autoReturn = true;

    private String content;
    private int posting_state; //是不是匿名发帖  0：正常发帖，1：匿名发帖
    private boolean isSrp = false;
    private boolean isWebView = false;
    private boolean isShare = false;
    private boolean isTel = false;
    private boolean isGoLogin = false;
    private boolean isPasePage = false;
    private boolean isBrowser = false;
    // 充值
    private boolean isRecharge = false;
    // 兑换
    private boolean isExchange = false;
    // 索要
    private boolean isAskfor = false;
    private boolean isComment = false;

    private boolean isShowimage = false;

    private boolean isInterest = false;
    // 老虎机
    private boolean isSlotMachine = false;
    // 中搜零拍，中搜币商城
    private boolean interactWeb = false;
    // 应用宝典
    private boolean isApp = false;
    // 移动商街
    private boolean isDiscount = false;
    // 兴趣圈帖子
    private boolean isBlog = false;
    // 兴趣圈@我的列表
    private boolean isAlt = false;
    // 兴趣圈回复我的列表
    private boolean isCommentType = false;
    // 赠中搜币
    private boolean isZSBclose = false;
    // 游戏中心
    private boolean isCheckappinstalled = false;
    // 如果应用安装，打开应用
    private boolean isOpenapp = false;
    private boolean isClose;
    private boolean isOriginal;
    private boolean isSetTilte;
    private boolean isMySubscribe = false;// 我的订阅
    private boolean isSubscribe = false;// 订阅
    private boolean isSearch = false;// 搜索
    private boolean isScan = false;// 扫一扫
    private boolean isSouyueUserCenter = false;// 个人中心
    private boolean isRegister = false;// 注册
    private boolean isImAddressBook = false;// 我的订阅

    private boolean isEmptyWeb = false;// 无头尾webview
    private boolean isCommentPage = false;
    private boolean isGoHome = false;// 回首页
    private boolean isNavigationWeb = false;// 打开新详情
    private boolean isGetUser = false;// JS获取用户信息

    private boolean isUpdateSouyue = false;// 是否升级搜悦版本

    private boolean isRelogin_ModifyPwd = false;// 修改密码重新登录

    private boolean isReply = false;//对评论进行回复

    private boolean isReward = false;//打赏类型跳转
    private boolean isAtlas = false;//图集类型跳转
    private boolean isRecommendSub = false;//推荐订阅
    private boolean isGetAdList;

    private long isPre;
    private long listId;
    private String srp_id;
    private String interestLogo;



    private boolean isAdClick;

    private boolean isRelogin_CommonRegister = false;// 普通注册重新登录
    private boolean isRefreshBrowser;  //项目展示微件 从详情页跳转到列表页之后要刷新 列表页 新增类别  add  by zhaobo 2015-3-23
    private boolean isStartUpVote;     //H5微件调用客户端点赞接口 add by zhaobo 2015-03-30

    //add by yinguanping   js调用加密
    private boolean isRSAEncrypt = false;
    private boolean isGetRSAParam = false;

    
    //5.0
    private boolean openSearchDialog = false;
    private boolean openYaowen = false;
    private boolean closeSearchDialog = false;
    private boolean createShortcut = false;
    private boolean copy = false;
    private boolean openQRCode = false;
    private boolean getfocus = false;
    private boolean setWallPaper = false;

    // 新接口，微信分享
    private boolean shareToWX = false; //分享到微信
    private boolean shareToWXFriend = false; // 分享到朋友圈
    private boolean getSharePrize = false; //领取奖励
    private boolean addSrpSub = false; //订阅srp词

    //im红包
    private boolean isSendRedPacket = false;//是否是发送红包
    private boolean isOpenRedPacket = false;//是否打开红包

    public boolean isOpenMaps() {
        return openMaps;
    }

    public void setOpenMaps(boolean openMaps) {
        this.openMaps = openMaps;
    }

    private boolean openMaps = false; //打开地图

    public boolean isAddSrpSub() {
        return addSrpSub;
    }

    public void setAddSrpSub(boolean addSrpSub) {
        this.addSrpSub = addSrpSub;
    }

    public boolean isAddCircleSub() {
        return addCircleSub;
    }

    public void setAddCircleSub(boolean addCircleSub) {
        this.addCircleSub = addCircleSub;
    }

    private boolean addCircleSub = false; //订阅圈子

    public boolean isGetSharePrize() {
        return getSharePrize;
    }

    public void setGetSharePrize(boolean getSharePrize) {
        this.getSharePrize = getSharePrize;
    }

    public boolean isShareToWXFriend() {
        return shareToWXFriend;
    }

    public void setShareToWXFriend(boolean shareToWXFriend) {
        this.shareToWXFriend = shareToWXFriend;
    }

    public boolean isShareToWX() {
        return shareToWX;
    }

    public void setShareToWX(boolean shareToWX) {
        this.shareToWX = shareToWX;
    }

    public boolean isSetWallPaper() {
		return setWallPaper;
	}

	public void setSetWallPaper(boolean setWallPaper) {
		this.setWallPaper = setWallPaper;
	}

	public boolean isGetfocus() {
		return getfocus;
	}

	public void setGetfocus(boolean getfocus) {
		this.getfocus = getfocus;
	}

	public boolean isOpenQRCode() {
		return openQRCode;
	}

	public void setOpenQRCode(boolean openQRCode) {
		this.openQRCode = openQRCode;
	}

	public boolean isCopy() {
		return copy;
	}

	public void setCopy(boolean copy) {
		this.copy = copy;
	}

	public boolean isCreateShortcut() {
		return createShortcut;
	}

	public void setCreateShortcut(boolean createShortcut) {
		this.createShortcut = createShortcut;
	}

	public boolean isCloseSearchDialog() {
		return closeSearchDialog;
	}

	public void setCloseSearchDialog(boolean closeSearchDialog) {
		this.closeSearchDialog = closeSearchDialog;
	}

	public boolean isOpenYaowen() {
		return openYaowen;
	}

	public void setOpenYaowen(boolean openYaowen) {
		this.openYaowen = openYaowen;
	}

	public boolean isOpenSearchDialog() {
		return openSearchDialog;
	}

	public void setOpenSearchDialog(boolean openSearchDialog) {
		this.openSearchDialog = openSearchDialog;
	}

	public boolean isGetRSAParam() {
        return isGetRSAParam;
    }

    public void setGetRSAParam(boolean isGetRSAParam) {
        this.isGetRSAParam = isGetRSAParam;
    }

    public boolean isRSAEncrypt() {
        return isRSAEncrypt;
    }

    public void setRSAEncrypt(boolean isRSAEncrypt) {
        this.isRSAEncrypt = isRSAEncrypt;
    }

    private String event = "";

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public boolean getIsGetAdList() {
        return isGetAdList;
    }

    public void setIsGetAdList(boolean isGetAdList) {
        this.isGetAdList = isGetAdList;
    }

    public String getSign_id() {
        return sign_id;
    }

    public void setSign_id(String sign_id) {
        this.sign_id = sign_id;
    }

    public boolean isAdClick() {
        return isAdClick;
    }

    public void setAdClick(boolean isAdClick) {
        this.isAdClick = isAdClick;
    }
    // private boolean isRelogin_ForgetPwd = false;//忘记密码重新登录

    public boolean isUpdateSouyue() {
        return isUpdateSouyue;
    }

    public void setUpdateSouyue(boolean isUpdateSouyue) {
        this.isUpdateSouyue = isUpdateSouyue;
    }

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }

    private boolean isEncrypt = false;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isMySubscribe() {
        return isMySubscribe;
    }

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public boolean isScan() {
        return isScan;
    }

    public boolean isSouyueUserCenter() {
        return isSouyueUserCenter;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public boolean isImAddressBook() {
        return isImAddressBook;
    }

    // add by trade
    // add by FM
    private boolean isShowbottommenu; // 是否显示底部导航：评论、赞、收藏、分享等；
    // add by zg
    private String longitude = "";
    private String latitude = "";
    private String zurl; // 活动微件 点赞 收藏 评论 统一url add by zhaobo
    private boolean isLocation = false;
    private String iswidget; // 预约微件 iswidget=1 时 详情页登陆成功后 拼入参数uid和anonymous add
    // by zhaobo

    public String GetIsWidget() {
        if (iswidget != null) {
            return iswidget;
        } else {
            return "";
        }
    }


    public boolean isIsget_news_info() {
        return isget_news_info;
    }

    public void setIsget_news_info(boolean isget_news_info) {
        this.isget_news_info = isget_news_info;
    }

    public String getmOrginalSource() {
        return mOrginalSource;
    }

    public void setmOrginalSource(String mOrginalSource) {
        this.mOrginalSource = mOrginalSource;
    }

    public boolean isIsget_signid() {
        return isget_signid;
    }

    public void setAdminMoreOper(boolean adminMoreOper) {
        this.adminMoreOper = adminMoreOper;
    }

    public boolean isAdminMoreOper() {
        return adminMoreOper;
    }

    public void setIsget_signid(boolean isget_signid) {
        this.isget_signid = isget_signid;
    }

    public boolean isIsget_blog_info() {
        return isget_blog_info;
    }

    public void setIsget_blog_info(boolean isget_blog_info) {
        this.isget_blog_info = isget_blog_info;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public boolean isSetTilte() {
        return isSetTilte;
    }

    public void setSetTilte(boolean isSetTilte) {
        this.isSetTilte = isSetTilte;
    }

    public boolean isAutoReturn() {
        return autoReturn;
    }

    public void setAutoReturn(boolean autoReturn) {
        this.autoReturn = autoReturn;
    }

    public boolean isOriginal() {
        return this.isOriginal;
    }

    public boolean isReply() {
        return this.isReply;
    }

    public boolean isClose() {
        return this.isClose;
    }

    public boolean isOpenapp() {
        return this.isOpenapp;
    }

    public String appname() {
        return appname;
    }

    public String ZSBcount() {
        return ZSBcount;
    }

    public void setZSBcount(String zSBcount) {
        ZSBcount = zSBcount;
    }

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInterest_name() {
        return interest_name;
    }

    public void setInterest_name(String interest_name) {
        this.interest_name = interest_name;
    }

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public String getInterest_logo() {
        return interest_logo;
    }

    public void setInterest_logo(String interest_logo) {
        this.interest_logo = interest_logo;
    }

    public User getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(User userInfo) {
        this.userInfo = userInfo;
    }

    private List<String> imgs;

    private int index = 0;

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public boolean isRefreshBrowser() {
        return this.isRefreshBrowser;
    }

    public boolean isStartUpVote() {
        return this.isStartUpVote;
    }

    public boolean isCheckappinstalled() {
        return isCheckappinstalled;
    }

    public void setAlt(boolean isAlt) {
        this.isAlt = isAlt;
    }

    public void setCommentType(boolean commentType) {
        this.isCommentType = commentType;
    }

    public boolean isInteractWeb() {
        return interactWeb;
    }

    public void setInteractWeb(boolean interactWeb) {
        this.interactWeb = interactWeb;
    }

    public boolean isApp() {
        return isApp;
    }

    public void setApp(boolean isApp) {
        this.isApp = isApp;
    }

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean isDiscount) {
        this.isDiscount = isDiscount;
    }

    public boolean isSlotMachine() {
        return isSlotMachine;
    }

    public void setSlotMachine(boolean isSlotMachine) {
        this.isSlotMachine = isSlotMachine;
    }

    public boolean isInterest() {
        return isInterest;
    }

    public void setInterest(boolean isInterest) {
        this.isInterest = isInterest;
    }

    public boolean isComment() {
        return isComment;
    }

    public boolean isRecharge() {
        return isRecharge;
    }

    public boolean isExchange() {
        return isExchange;
    }

    public boolean isAskfor() {
        return isAskfor;
    }

    public String md5() {
        return md5;
    }

    public String category() {
        return category;
    }

    public String keyword() {
        return keyword;
    }

    public String srpId() {
        return srpId;
    }

    public String title() {
        return title;
    }

    public String url() {
        return url;
    }

    public String image() {
        return image;
    }

    public String description() {
        return description;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public List<String> imgs() {
        return imgs;
    }

    public String getContent() {
        return content;
    }

    public void setPosting_state(int posting_state) {
        this.posting_state = posting_state;
    }

    public int getPosting_state() {
        return posting_state;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int index() {
        return index;
    }

    public boolean isSrp() {
        return isSrp;
    }

    public boolean isWebView() {
        return isWebView;
    }

    public boolean isShare() {
        return isShare;
    }

    public boolean isTel() {
        return isTel;
    }

    public boolean isGoLogin() {
        return isGoLogin;
    }

    public boolean isPasePage() {
        return isPasePage;
    }

    public boolean isBrowser() {
        return isBrowser;
    }

    public boolean isShowimage() {
        return isShowimage;
    }

    public boolean isBlog() {
        return isBlog;
    }

    public boolean isAlt() {
        return isAlt;
    }

    public boolean isCommentType() {
        return isCommentType;
    }

    public boolean isZSBclose() {
        return isZSBclose;
    }

    public String getZurl() {
        return zurl;
    }

    public void setZurl(String zurl) {
        this.zurl = zurl;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public boolean isShowbottommenu() {
        return isShowbottommenu;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean isLocation) {
        this.isLocation = isLocation;
    }

    public boolean isEmptyWeb() {
        return isEmptyWeb;
    }

    public void setEmptyWeb(boolean isEmptyWeb) {
        this.isEmptyWeb = isEmptyWeb;
    }

    public boolean isCommentPage() {
        return isCommentPage;
    }

    public void setCommentPage(boolean isCommentPage) {
        this.isCommentPage = isCommentPage;
    }

    public boolean isGoHome() {
        return isGoHome;
    }

    public void setGoHome(boolean isGoHome) {
        this.isGoHome = isGoHome;
    }

    public boolean isRelogin_ModifyPwd() {
        return isRelogin_ModifyPwd;
    }

    public void setRelogin_ModifyPwd(boolean isRelogin_ModifyPwd) {
        this.isRelogin_ModifyPwd = isRelogin_ModifyPwd;
    }

    public boolean isRelogin_CommonRegister() {
        return isRelogin_CommonRegister;
    }

    public void setRelogin_CommonRegister(boolean isRelogin_CommonRegister) {
        this.isRelogin_CommonRegister = isRelogin_CommonRegister;
    }

    public boolean isReward() {
        return isReward;
    }

    public void setReward(boolean isReward) {
        this.isReward = isReward;
    }

    public boolean isAtlas() {
        return isAtlas;
    }

    public void setAtlas(boolean isAtlas) {
        this.isAtlas = isAtlas;
    }

    public boolean isRecommendSub() {
        return isRecommendSub;
    }

    public void setRecommendSub(boolean isRecommendSub) {
        this.isRecommendSub = isRecommendSub;
    }

    public long isPre() {
        return isPre;
    }

    public void setPre(long isPre) {
        this.isPre = isPre;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    // public boolean isRelogin_ForgetPwd() {
    // return isRelogin_ForgetPwd;
    // }
    //
    // public void setRelogin_ForgetPwd(boolean isRelogin_ForgetPwd) {
    // this.isRelogin_ForgetPwd = isRelogin_ForgetPwd;
    // }

    public boolean isNavigationWeb() {
        return isNavigationWeb;
    }

    public void setNavigationWeb(boolean isNavigationWeb) {
        this.isNavigationWeb = isNavigationWeb;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public boolean isSendRedPacket() {
        return isSendRedPacket;
    }

    public void setSendRedPacket(boolean isSendRedPacket) {
        this.isSendRedPacket = isSendRedPacket;
    }

    public boolean isOpenRedPacket() {
        return isOpenRedPacket;
    }

    public void setOpenRedPacket(boolean isOpenRedPacket) {
        this.isOpenRedPacket = isOpenRedPacket;
    }

    public String getInterestLogo() {
        return interestLogo;
    }

    public void setInterestLogo(String interestLogo) {
        this.interestLogo = interestLogo;
    }

    public void init() {
        this.isSrp = eq(this.category, "srp");
        this.isWebView = eq(this.category, "webview");
        this.isShare = eq(this.category, "share");
        this.isTel = eq(this.category, "tel");
        this.isGoLogin = eq(this.category, "login");
        this.isPasePage = eq(this.category, "pasePage");
        this.isBrowser = eq(this.category, "browser");
        this.isRecharge = eq(this.category, "recharge");
        this.isExchange = eq(this.category, "exchange");
        this.isAskfor = eq(this.category, "askfor");
        this.isComment = eq(this.category, "comment");
        this.isShowimage = eq(this.category, "showimage");
        this.isInterest = eq(this.category, "interest");
        this.isSlotMachine = eq(this.category, "slotMachine");
        this.interactWeb = eq(this.category, "interactWeb");
        this.isApp = eq(this.category, "app");
        this.isDiscount = eq(this.category, "discount");
        this.isBlog = eq(this.category, "blog");
        this.isAlt = eq(this.category, "alt");
        this.isCommentType = eq(this.category, "CommentType");
        this.isZSBclose = eq(this.category, "ZSBclose");
        this.isCheckappinstalled = eq(this.category, "checkappinstalled");
        this.isOpenapp = eq(this.category, "openapp");
        this.isClose = eq(this.category, "close");
        this.isOriginal = eq(this.category, "original");
        this.isReply = eq(this.category, "reply");
        this.isReward = eq(this.category, "reward");
        this.isAtlas = eq(this.category, "atlas");
        this.isRecommendSub = eq(this.category, "recommendSub");  //推荐订阅
        // add by trade
        this.isShowbottommenu = eq(this.category, "showbottommenu");
        this.isRefreshBrowser = eq(this.category, "refreshBrowser");
        this.isStartUpVote = eq(this.category, "startupvote");
        this.isLocation = eq(this.category, "location");
        this.isMySubscribe = eq(this.category, "mySubscribe");
        this.isSubscribe = eq(this.category, "subscribe");
        this.isSearch = eq(this.category, "search");
        this.isScan = eq(this.category, "scan");
        this.isSouyueUserCenter = eq(this.category, "souyueUserCenter");
        this.isRegister = eq(this.category, "register");
        this.isImAddressBook = eq(this.category, "imAddressBook");
        this.isEncrypt = eq(this.category, "encrypt");
        // this.isNewShare = eq(this.category, "newShare");
        this.isEmptyWeb = eq(this.category, "emptyWeb");
        this.isCommentPage = eq(this.category, "commentPage");
        this.isGoHome = eq(this.category, "goHome");
        this.isNavigationWeb = eq(this.category, "navigationWeb");

        this.isRelogin_CommonRegister = eq(this.category, "registerSucceed");
        // this.isRelogin_ForgetPwd = eq(this.category, "login");
        this.isRelogin_ModifyPwd = eq(this.category, "relogin");
        this.isUpdateSouyue = eq(this.category, "updatenote");
        this.isGetUser = eq(this.category, "getUser");
        this.isSetTilte = eq(this.category, "settitle");

        this.isGetAdList = eq(this.category, "getAdList");
        this.isAdClick = eq(this.category, "AdClick");
        this.isget_signid = eq(this.category, "get_signid");
        this.isget_blog_info = eq(this.category, "get_blog_info");
        this.isget_news_info = eq(this.category, "get_news_info");
        this.adminMoreOper = eq(this.category, "adminMoreOper");

        this.isRSAEncrypt = eq(this.category, "RSAEncrypt");
        this.isGetRSAParam = eq(this.category, "GetRSAParam");
        
        this.openSearchDialog = eq(this.category, "openSearchDialog");
        this.openYaowen = eq(this.category, "openYaowen");
        this.closeSearchDialog = eq(this.category, "closeSearchDialog");
        this.createShortcut = eq(this.category, "createShortcut");
        this.copy = eq(this.category, "copy");
        this.openQRCode = eq(this.category, "openQRCode");
        this.getfocus = eq(this.category, "getfocus");
        this.setWallPaper = eq(this.category, "setWallPaper");

        /*5.07 微信红包相关*/
        this.shareToWX = eq(this.category,"shareToWX");
        this.shareToWXFriend = eq(this.category,"shareToWXFriend");
        this.getSharePrize = eq(this.category,"getSharePrize");

        this.addCircleSub = eq(this.category,"addCircleSub");
        this.addSrpSub = eq(this.category,"addSrpSub");

        this.openMaps = eq(this.category,"openMaps");

        //IM红包相关
        this.isSendRedPacket = eq(this.category,"sendRedPacket");
        this.isOpenRedPacket = eq(this.category,"openRedPacket");
    }

    public boolean isGetUser() {
        return this.isGetUser;
    }

    private boolean eq(String eq, String eq0) {
        if (eq != null && eq0 != null) {
            return eq.equals(eq0);
        }
        return false;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getPubTime() {
        return pubTime;
    }

    public void setPubTime(long pubTime) {
        this.pubTime = pubTime;
    }
}
