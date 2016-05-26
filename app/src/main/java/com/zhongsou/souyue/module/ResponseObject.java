package com.zhongsou.souyue.module;

import com.google.gson.Gson;
import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public abstract class ResponseObject extends Object implements Serializable,DontObfuscateInterface {

    public static final long serialVersionUID = -5752410193949578084L;

    /**
     * 网络返回数据模型定义
     * 
     * @author wanglong@zhongsou.com
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}


//@SuppressWarnings("serial")
//class JSClick extends ResponseObject {
//    public String category = "";
//    public String keyword = "";
//    public String srpId = "";
//    public String title = "";
//    public String url = "";
//    public String image = "";
//    public String description = "";
//    public boolean isSrp = false;;
//    public boolean isWebView = false;;
//    public boolean isShare = false;;
//
//    public void init() {
//        this.isSrp = this.category == "srp";
//        this.isWebView = this.category == "webview";
//        this.isShare = this.category == "share";
//    }
//}
//
//
//@SuppressWarnings("serial")
//class PushInfo extends ResponseObject {
//    public String keyword = "";
//    public String pushId = "";
//    public String srpId = "";
//    public String url = "";
//    public String g = "";
//}
//
//
//@SuppressWarnings("serial")
//class GsItem extends ResponseObject {// 发原创选词
//    public String g = "";
//    public List<KsItem> ks = new ArrayList<KsItem>();
//}
//
//
//@SuppressWarnings("serial")
//class KsItem extends ResponseObject {// 发原创选词
//    public String k = "";//  keyword
//    public String i = "";// srpid
//}
//
//
//@SuppressWarnings("serial")
//class SubscribeKeywordList extends ResponseObject {
//    public String maxSelect = "";
//    // public String gs: java.util.List[GsItem] = new Gson().fromJson(response.getBodyArray, new
//    // TypeToken[java.util.List[GsItem]] {}.getType)
//    public List<GsItem> gs = new ArrayList<GsItem>();
//}
//
//
//@SuppressWarnings("serial")
//class ADInfo extends ResponseObject {
//    public String url = "";
//    public boolean download = false;;
//    public String event = "";
//}
//
//
///**
// * 热门搜索
// */
//@SuppressWarnings("serial")
//class SearchTop extends ResponseObject {
//    public String keyword = "";
//    public String srpId = "";
//    public String title = "";
//}
//
//
//@SuppressWarnings("serial")
//class User extends ResponseObject {
//    public String image = "";
//    public String email = "";
//    public long userId = 0;
//    public String name = "";
//    public String token = "";
//    public String userType = "";
//    public String url = "";
//}
//
//
//@SuppressWarnings("serial")
//class SubscribeBack extends ResponseObject {
//    public List<Integer> id = new ArrayList<Integer>();
//    public long groupId = 0;
//}
//
//
//@SuppressWarnings("serial")
//class Subscribe extends ResponseObject {
//    public String requestUrl = "";
//    public List<SubscribeItem> items = new ArrayList<SubscribeItem>();
//
//    Subscribe(HttpJsonResponse response) {
//        items = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<SubscribeItem>>() {}.getType());
//    }
//
//}
//
//
//@SuppressWarnings("serial")
//class SubscribeItem extends ResponseObject {
//    /**
//     * 原rss的id
//     */
//    public long id = 0;
//    /**
//     * 用户订阅后的id
//     */
//    public long subscribeId = 0;
//    public long groupId = 0;
//    public String keyword = "";
//    public String category = "";
//    public String url = "";
//    public boolean hasSubscribe = false;
//    public String image = "";
//    public String srpId = "";
//
//    public void setHasSubscribe(boolean sub) {
//        hasSubscribe = sub;
//    }
//}
//
//
//@SuppressWarnings("serial")
//class CateTree extends ResponseObject {
//    public String title = "";
//    public long id = 0;
//    public String srpId = "";
//    public boolean hasSubscribed = false;
//    public List<CateTree> child = new ArrayList<CateTree>();
//
//    public void setHasSubscribed(boolean sub) {
//        hasSubscribed = sub;
//    }
//}
//
//
//
//
///**
// * 一站到底广告
// */
//@SuppressWarnings("serial")
//class Ad extends ResponseObject {
//    public String image = "";
//    public String url = "";
//    public String category = "";
//}
//
//
//@SuppressWarnings("serial")
//class SelfCreate extends ResponseObject {
//    public boolean hasMore;
//    public List<SelfCreateItem> items;
//
//    public SelfCreate(HttpJsonResponse response) {
//        hasMore = response.getHeadBoolean("hasMore");
//        items = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<SelfCreateItem>>() {}.getType());
//        if (null != items && items.size() > 0) {
//            for (int i = 0; i < items.size(); i++) {
//                SelfCreateItem images = items.get(i);
//                if (images.conpic.trim().length() == 0) {
//                    images.conpics = null;
//                } else {
//                    images.conpics = Arrays.asList(images.conpic.trim().split(" "));
//                }
//            }
//        }
//    }
//
//
//}
//
//
//@SuppressWarnings("serial")
//class SelfCreateItem extends ResponseObject {
//    /* 原创大赛start */
//    public int wrank = 0;// 周排行
//    public int mrank = 0;// 月排行
//    public int score = 0; // 积分
//    /* 原创大赛end */
//    public String url = "";
//    public String _id = "";
//    public String token = "";
//    public String id = ""; // 数据库id
//    public String keyword = ""; // "关键词" zhongguo,beijing,renmien,
//    public String srpId = ""; // "关键词对应srpid", sfsfsff,sfsdfsd,fsf,fs
//    public String kid = ""; // ?
//    public String md5 = ""; // 微件对应md5"
//    public String column_name = ""; // "栏目名",
//    public long column_type = 0; // 微件类型
//    public String title = ""; // 原创标题",
//    public String content = ""; // "原创内容",
//    public String conpic = ""; // 内容图片(若有多个图片空格隔开共同存储)
//    public String pubtime = ""; // 发布时间
//    public int status = 0; // 审核状态
//    public List<String> conpics = new ArrayList<String>(); // 图片容器
//
//
//}
//
//
//@SuppressWarnings("serial")
//class SearchResult extends ResponseObject {
//    public String title;
//    public String category;
//    public boolean isXiaoqi;
//    public String srpId;
//    public boolean hasExpired = false; // 数据是否过期
//    public BoZhu boZhu;
//    public Weibo newWeiBo;
//    public List<HotTopic> hotTopics;
//    public List<Weibo> weibo;
//    // 问答使用,keyword 在数据库中的主键
//    public String keyword;
//    public String kid;
//    public int version;
//    public boolean hideSubsrcibe;
//    public List<NavigationBar> nav;
//    public List<SearchResultItem> items;
//    public JsonArray questions;
//    // 问答使用
//    public String md5;
//    // 一站到底广告
//    public Ad ad = null;
//    public List<Ad> adList;
//    public boolean hasMore;
//
//    public SearchResult(HttpJsonResponse response) {
//        hasMore = response.getHeadBoolean("hasMore");
//        md5 = response.getHeadString("md5");
//        adList = new Gson().fromJson(response.getHead().getAsJsonArray("adList"), new TypeToken<List<Ad>>() {}.getType());
//        keyword = response.getHeadString("keyword");
//        kid = response.getHeadString("kid");
//        version = response.getHeadInt("version", 2);
//        hideSubsrcibe = response.getHeadBoolean("hideSubsrcibe");
//        title = response.getHeadString("title");
//        category = response.getHeadString("category");
//        isXiaoqi = response.getHeadBoolean("isXiaoqi");
//        srpId = response.getHeadString("srpId");
//        nav = new Gson().fromJson(response.getHead().getAsJsonArray("nav"), new TypeToken<List<NavigationBar>>() {}.getType());
//        if (response.isJsonArray()) {
//            items = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<SearchResultItem>>() {}.getType());
//        } else {
//            questions = response.getBody().getAsJsonArray("questions");
//            if (null != questions) {
//                items = new Gson().fromJson(questions, new TypeToken<List<SearchResultItem>>() {}.getType());
//            } else {
//                boZhu = new Gson().fromJson(response.getBody().getAsJsonObject("bozhu"), new TypeToken<BoZhu>() {}.getType());
//                newWeiBo = new Gson().fromJson(response.getBody().getAsJsonObject("newWeibo"), new TypeToken<Weibo>() {}.getType());
//                hotTopics = new Gson().fromJson(response.getBody().getAsJsonArray("hotTopic"), new TypeToken<List<HotTopic>>() {}.getType());
//                weibo = new Gson().fromJson(response.getBody().getAsJsonArray("weibo"), new TypeToken<List<Weibo>>() {}.getType());
//            }
//        }
//
//        JsonElement ad = response.getHeadElement("ad");
//        if (ad != null) {
//            this.ad = new Gson().fromJson(ad, Ad.class);
//        }
//        List<String> urls = new ArrayList<String>();
//        if (items != null) {
//            for (int i = 0; i < items.size(); i++) {
//                SearchResultItem item = items.get(i);
//                if (item.url != null) {
//                    urls.add(item.url);
//                }
//            }
//        }
//        ReadHistoryHelper inst = ReadHistoryHelper.getInstance();
//        Set<String> md5s = inst.select(urls);
//        if (items != null) {
//            for (int i = 0; i < items.size(); i++) {
//                SearchResultItem item = items.get(i);
//                if (item.url != null && md5s.contains(AQUtility.getMD5Hex(item.url))) {
//                    item.hasRead = true;
//                }
//            }
//
//        }
//        init(items);
//    }
//
//
//    public void init(List<SearchResultItem> items) {
//        if (items != null) {
//            for (int index = 0; index < items.size(); index++) {
//                SearchResultItem item = items.get(index);
//                item.isOriginal = (item.source == "搜悦原创" || item.isOriginal == 1) ? 1 : 0;
//                if (item.isOriginal == 1) item.source = "搜悦原创";
//                // 类型为新闻搜索，且非焦点图，需要将地址替换为又拍云地址
//                for (int j = 0; j < item.image.size(); j++) {
//                    String imgUrl = item.image.get(j);
//                    if (category == "新闻搜索") {
//                        item.image.set(j, replaceImageWithUpyun(imgUrl));
//                    } else if (imgUrl.startsWith("http://souyue-image.b0;.upaiyun.com/")) {
//                        item.image.set(j, imgUrl + (category == "RSS" ? "_android.jpg" : "") + "!android");
//                    }
//                }
//
//                if (item.image != null && item.image.size() != 0) {
//
//                    if (item.isFocus)
//                        item.newsLayoutType = SearchResultLayout.NEWS_TYPE_FOCUS;
//                    else if (item.image.size() >= 3)
//                        item.newsLayoutType = SearchResultLayout.NEWS_TYPE_IMAGE_THREE;
//                    else
//                        item.newsLayoutType = SearchResultLayout.NEWS_TYPE_IMAGE_ONE;
//                    if (item.isFocus) item.image.set(0, item.bigImgUrl);
//                }
//
//            }
//        }
//    }
//
//    /**
//     * 替换中搜图片地址为又拍云地址
//     */
//    public String replaceImageWithUpyun(String urlSource) {
//        // 又拍云格式
//        String picUpyun = "http://souyue-image.b0;.upaiyun.com/newspic/list/$2/$3/$1_android.jpg!android";
//        // 原图格式
//        String picZhongsou = "http://pic.+zhongsou\\.com.+(4\\w{2}(\\w{2})(\\w{2})\\w{12}).*";
//        return urlSource.replaceAll(picZhongsou, picUpyun);
//    }
//}
//
//
//@SuppressWarnings("serial")
//class BoZhu extends ResponseObject {
//    public User User = new User();
//    public long follow = 0; // 关注数
//    public long fans = 0; // 粉丝数
//    public long weibo = 0; // 微博数
//    public String source = ""; // "新浪微博"//来源
//    public Weibo newWeibo = new Weibo();
//}
//
//
//@SuppressWarnings("serial")
//class ImageUrlInfo extends ResponseObject {
//    public String small = "";
//    public String middle = "";
//    public String big = "";
//}
//
//
//@SuppressWarnings("serial")
//class Weibo extends ResponseObject {
//    public int category = 0; // 微博类型（1原创、）
//    public String id = ""; // 234,//此条微博的id
//    public User user = new User();
//    public String content = ""; // 内容
//    public List<ImageUrlInfo> image = new ArrayList<ImageUrlInfo>(); // 图片url数组
//    public String source = ""; // "新浪微博",//来源
//    public String date = ""; // "12345678",//发布时间
//    public String url = ""; // "http://t.zhongsou.net",//微博连接
//    public ReplyTo replyTo = new ReplyTo();
//    public String zsUrl = "";
//}
//
//
//@SuppressWarnings("serial")
//class ReplyTo extends ResponseObject {
//    public int category = 0; // 微博类型（1原创、）
//    public String id = ""; // 234,//此条微博的id
//    public User user = new User();
//    public String content = ""; // 内容
//    public List<ImageUrlInfo> image = new ArrayList<ImageUrlInfo>(); // 图片url数组
//    public String source = ""; // "新浪微博",//来源
//    public String date = ""; // "12345678",//发布时间
//    public String url = ""; // "http://t.zhongsou.net",//微博连接
//}
//
//
//@SuppressWarnings("serial")
//class HotTopic extends ResponseObject {
//    public String title = ""; // : "话题标题",
//    public String url = ""; // : "话题连接"
//    public String source = "";// 新浪微博
//}
//
//
//@SuppressWarnings("serial")
//class SearchResultItem extends ResponseObject {
//    public BoZhu boZhu = new BoZhu();
//    public Weibo newWeiBo = new Weibo();
//    public List<HotTopic> hotTopics = new ArrayList<HotTopic>();
//    public Weibo weibo = new Weibo();
//    public ImageUrlInfo imageUrlInfo = new ImageUrlInfo();
//    /**
//     * 问答使用
//     */
//    public String md5 = "";
//    /**
//     * 问答使用,keyword 在数据库中的主键
//     */
//    public String kid = "";
//    public String id = "";
//    public String srpId = "";
//    public String title = "";
//    public String keyword = "";
//    public String description = "";
//    public List<String> image = new ArrayList<String>();
//    public String source = "";
//    public String date = "";
//    public String url = "";
//    public String quality = "";
//    public String length = "";
//    public String author = "";
//    public int newsLayoutType = SearchResultLayout.NEWS_TYPE_NORMAL; // 新闻布局类型
//    public User user = new User();
//    public long answerCount = 0;
//    public long sameAskCount = 0;
//    public List<AdListItem> adlist = new ArrayList<AdListItem>();
//    public boolean isFocus = false;
//    public String bigImgUrl = "";
//    public long pushId = 0;;
//    public boolean hasRead = false; /* 是否已经读过 */
//    /* 精华区 */
//    public long favoriteCount = 0; /* 精华区收藏数 */
//    public long upCount = 0;/* 精华区顶数 */
//    public String userNick = ""; /* 第一个加入精华区的用户昵称 */
//    public long commentCount = 0; /* 精华区收藏数 */
//
//    /* 博客和论坛 */
//    public int isOriginal = 0;
//
//    /* 排行榜 */
//    public int wrank = 0;// 周排行
//    public int mrank = 0;// 月排行
//    public int score = 0; // 积分
//
//}
//
//
//@SuppressWarnings("serial")
//class GroupKeywordItem extends ResponseObject {
//    public String url = "";
//    public String keyword = "";
//    public String srpId = "";
//}
//
//
//@SuppressWarnings("serial")
//class NewsDetail extends ResponseObject {
//    public String url = "";
//    public String srpId = "";
//    public String urlOrig = "";
//    public String title = "";
//    public String date = "";
//    public String source = "";
//    public List<String> image = new ArrayList<String>();
//    public List<NavigationBar> nav = new ArrayList<NavigationBar>();
//}
//
//@SuppressWarnings("serial")
//class NewsCount extends ResponseObject {
//    public long newsId = 0; //
//    public int commentsCount = 0; // 评论数
//    public boolean hasFavorited = false; // 是否收藏过
//    public boolean hasUp = false; // 是否顶，新增加的字段
//}
//
//@SuppressWarnings("serial")
//class NavigationBar extends ResponseObject {
//    public String title = "";
//    public String url = "";
//    public String category = "";
//    public String md5 = "";
//    public List<String> image = new ArrayList<String>();
//}
//
//@SuppressWarnings("serial")
//class WendaDetail extends ResponseObject {
//    public String id = "";
//    public List<Wenda> wendaList;
//    public boolean hasMore;
//
//    public WendaDetail(HttpJsonResponse response) {
//        wendaList = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<Wenda>>() {}.getType());
//        hasMore = response.getHeadBoolean("hasMore");
//    }
//}
//
//@SuppressWarnings("serial")
//class Wenda extends ResponseObject {
//    public String id = "";
//    public User user = new User();
//    public String content = "";
//    public long date = 0;
//    public int upCount = 0;
//    public int downCount = 0;
//}
//
//@SuppressWarnings("serial")
//class WendaUpDown extends ResponseObject {
//    public String answerId = "";
//    public String questionId = "";
//    public int upCount = 0;
//    public int downCount = 0;
//    public String md5 = "";
//}
//
//@SuppressWarnings("serial")
//class WendaSameAsk extends ResponseObject {
//    public String questionId = "";
//    public String md5 = "";
//    public int sameAskCount = 0;
//}
//
//@SuppressWarnings("serial")
//class ToolTip extends ResponseObject {
//    public String category = "";
//    public String rssImage = "";
//    public String url = "";
//    public String keyword = "";
//    public String srpId = "";
//    public String srpCate = "";
//    public String m = ""; // 主词
//    public String g = ""; // 分义标注
//}
//
//@SuppressWarnings("serial")
//class FavoriteList extends ResponseObject {
//    public boolean hasMore = false;
//    public List<Favorite> items = new ArrayList<Favorite>();
//}
//
//@SuppressWarnings("serial")
//class Favorite extends ResponseObject {
//    public long id = 0;
//    public String url = "";
//    public String title = "";
//    public String image = "";
//    public String description = "";
//    public String date = "";
//    public String source = "";
//    public String keyword = "";
//    public String srpId = "";
//
//}
//
//@SuppressWarnings("serial")
//class NoticeList extends ResponseObject {
//    public boolean hasMore = false;
//    public List<Notice> items = new ArrayList<Notice>();
//}
//
//@SuppressWarnings("serial")
//class Notice extends ResponseObject {
//    public String title = "";
//    public String keyword = "";
//    public String content = "";
//    public long date = 0;
//    public long id = 0;
//    public String nick = "";
//    public String srpId = "";
//    public long pushType = 0;
//    public int IsGetContent = 1;// 消息推送打开标志 1表示需要抽取原文， 0; 是直接打开
//}
//
//@SuppressWarnings("serial")
//class Group extends ResponseObject {
//    public long id = 0;
//    public String name = "";
//    public String isPushMsg = "";
//}
//
//@SuppressWarnings("serial")
//class Voice extends ResponseObject {
//    public String url = "";
//    public long length = 0;
//}
//
//@SuppressWarnings("serial")
//class CommentList extends ResponseObject {
//    public boolean hasMore;
//    public List<Comment> comments;
//
//    public CommentList(HttpJsonResponse response) {
//        hasMore = response.getHeadBoolean("hasMore");
//        comments = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<Comment>>() {}.getType());
//    }
//
//
//}
//
//
class CommentType {
    public static int COMMENTTYPE_MINE = 1; // 我评论的
    public static int COMMENTTYPE_ME_TO_OTHER = 2; // 我回复他人的
    public static int COMMENTTYPE_OTHER_TO_ME = 3; // 他人回复我的
}
//
//@SuppressWarnings("serial")
//class Comment extends ResponseObject {
//    public int commentType = CommentType.COMMENTTYPE_MINE; // 回复类型，int值，1为我评论的，2我回复他人的，3为他人回复我的
//    public String content = "";
//    public long date = 0;
//    public long id = 0;
//    public String keyword = "";
//    public String srpId = "";
//    public String title = "";
//    public String url = "";
//    public User user = new User();
//    public Voice voice = null;
//    public Comment replyTo = null;
//}
//
//@SuppressWarnings("serial")
//class AdList extends ResponseObject {
//    public List<AdListItem> list = new ArrayList<AdListItem>();
//
//    public AdList(HttpJsonResponse response) {
//        this.list = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<AdListItem>>() {}.getType());
//    }
//}
//
//@SuppressWarnings("serial")
//class AdListItem extends ResponseObject {
//    public String image = "";
//    public int width = 0;
//    public int height = 0;
//    public String url = "";
//    public long id = 0;
//    public String title = "";
//    public String description = "";
//    public int category = 0;
//    public String contentType = ""; // 提供给广告系统使用，值为json或html，如果是html，则使用内置的webview打开url，如果是json，调用adDetail(String
//                                    // url)接口
//}
//
//@SuppressWarnings("serial")
//class AdContact extends ResponseObject {
//    public String phone = "";
//    public String weixin = "";
//    public String website = "";
//}
//
//@SuppressWarnings("serial")
//class AdDetail extends ResponseObject {
//    public String logo = "";
//    public String name = "";
//    public String brand = "";
//    public List<Product> product = new ArrayList<Product>();
//    public AdContact contact = new AdContact();
//    public String description = "";
//}
//
//@SuppressWarnings("serial")
//class RssCate extends ResponseObject {
//    public long id = 0;
//    public String name = "";
//}
//
//@SuppressWarnings("serial")
//class Product extends ResponseObject {
//    public String image = "";
//    public String description = "";
//}
//
//@SuppressWarnings("serial")
//class HomePage extends ResponseObject {
//    public List<HomePageItem> item;
//    public String guestToken;
//    public long guestId;
//
//    public HomePage(HttpJsonResponse response) {
//        item = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<HomePageItem>>() {}.getType());
//        guestToken = response.getHeadString("guestToken");
//        guestId = response.getHeadLong("guestId", 0);
//    }
//
//}
//
//@SuppressWarnings("serial")
//class HomePageItem extends ResponseObject {
//    public String srpId = ""; // 分享大赛
//    public String keyword = ""; // 分享大赛
//    public String title = ""; // 显示的标题
//    public String category = ""; // 类型
//    public String url = ""; // url地址
//    public String english = ""; // 英文显示的标题
//    public String id = ""; // id，如否是订阅,用于删除订阅
//    public long subId = 0; // 张亮 新闻源订阅id
//    boolean isSrp = "srp" == this.category;
//    /**
//     * 是否是收藏
//     * 
//     * @return
//     */
//    boolean isFavorite = "favorite" == this.category;
//    /**
//     * 是否是系统
//     * 
//     * @return
//     */
//    boolean isSystem = "system" == this.category;
//    /**
//     * 是否是订阅的srp分组
//     * 
//     * @return
//     */
//    boolean isGroup = "group" == this.category;
//    /**
//     * 是否是rss订阅
//     * 
//     * @return
//     */
//    boolean isRss = "rss" == this.category;
//    /**
//     * 是否是一个简单标签
//     * 
//     * @return
//     */
//    boolean isLabel = !isFavorite && !isSystem && !isGroup && !isRss;
//}

