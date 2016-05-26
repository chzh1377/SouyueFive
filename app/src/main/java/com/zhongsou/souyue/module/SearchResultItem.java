package com.zhongsou.souyue.module;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SearchResultItem extends ResponseObject implements Serializable {

    private String allDataString;

    private String channelName;// 属于哪个srp词
    private String interestLogo;// 圈子需要的东西
    private String interestName;

    private String channelId;

    private boolean showMenu;

    private String doprime_time;

    private int isHot;

    private int good_num;
    private int follow_num;

    // headline
    private String pubTime;
    private String type;


    // interest
    private long interest_id;
    private long blog_id;
    private int mOptionRoleType;  //角色
    private String interestType;  //圈子类型
    private String sign_id;  //每条详情的唯一标识

    private int keywordCate;
    private String channel;

    //统计
    private String statisticsJumpPosition;//推送新闻到达统计标识

    private String clickFrom;//表示是条目点击还是其他点击
    private String pushFrom;//表示是哪里推送过来
    private String msgId;//消息唯一id

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getPushFrom() {
        return pushFrom;
    }

    public void setPushFrom(String pushFrom) {
        this.pushFrom = pushFrom;
    }

    public String getStatisticsJumpPosition() {
        return statisticsJumpPosition;
    }

    public void setStatisticsJumpPosition(String statisticsJumpPosition) {
        this.statisticsJumpPosition = statisticsJumpPosition;
    }

    public String getClickFrom() {
        return clickFrom;
    }

    public void setClickFrom(String clickFrom) {
        this.clickFrom = clickFrom;
    }
    public void setSign_id(String sign_id) {
        this.sign_id = sign_id;
    }

    public String getSign_id() {
        return sign_id;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getInterestType() {
        return interestType;
    }

    public int getmOptionRoleType() {
        return mOptionRoleType;
    }

    public void setmOptionRoleType(int mOptionRoleType) {
        this.mOptionRoleType = mOptionRoleType;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getInterest_id() {
        return interest_id;
    }

    public long getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(long blog_id) {
        this.blog_id = blog_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public int getKeywordCate() {
        return keywordCate;
    }

    public void setKeywordCate(int keywordCate) {
        this.keywordCate = keywordCate;
    }

    private BoZhu boZhu = new BoZhu();
    private Weibo newWeiBo = new Weibo();
    private List<HotTopic> hotTopics = new ArrayList<HotTopic>();
    private Weibo weibo = new Weibo();
    private ImageUrlInfo imageUrlInfo = new ImageUrlInfo();
    /**
     * 问答使用
     */
    private String md5 = "";
    /**
     * 问答使用,keyword 在数据库中的主键
     */
    private String kid = "";
    private String id = "";
    private String srpId = "";
    private String title = "";
    private String keyword = "";
    private String description = "";
    private List<String> image = new ArrayList<String>();
    private String source = "";
    private double date;
    private String url = "";
    private String quality = "";
    private String length = "";
    private String author = "";
    private boolean isEssence = false; /* 原创是否加精 */
    private int newsLayoutType = SearchResult.NEWS_TYPE_NORMAL; // 新闻布局类型
    private User user = new User();
    private long answerCount = 0;
    private long sameAskCount = 0;
    private List<AdListItem> adlist = new ArrayList<AdListItem>();
    private boolean isFocus = false;
    private boolean focus = false;
    private String bigImgUrl = "";
    private long pushId = 0;
    private List<DiskLikeBean> disLike;
    private boolean hasRead = false; /* 是否已经读过 */
    /* 精华区 */
    private long favoriteCount = 0; /* 精华区收藏数 */
    private long upCount = 0;/* 精华区顶数 */
    private String userNick = ""; /* 第一个加入精华区的用户昵称 */
    private long commentCount = 0; /* 精华区收藏数 */

    /* 博客和论坛 */
    private int isOriginal = 0;

    /* 排行榜 */
    private int wrank = 0;// 周排行
    private int mrank = 0;// 月排行
    private int score = 0; // 积分

    /* 是否是官方网站 */
    private int isOfficialWebsite;

    /* 是否是hems */
    private boolean ding;
    /* 锁定当前文件类型 */
    private String category;

    private int start;// 记录新闻list == 2时 服务端提供的start值

    private String callback;

    private boolean headlineTop;// 是否是新闻头条

    private String sourceUrl;// webview原地址

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public List<DiskLikeBean> getDisLike() {
        return disLike;
    }

    public void setDisLike(List<DiskLikeBean> disLike) {
        this.disLike = disLike;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public String interestName() {
        return interestName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String interestName) {
        this.channelName = interestName;
    }

    public String getInterestLogo() {
        return interestLogo;
    }

    public void setInterestLogo(String interestLogo) {
        this.interestLogo = interestLogo;
    }

    public String getAllDataString() {
        return allDataString;
    }

    public void setAllDataString(String allDataString) {
        this.allDataString = allDataString;
    }

    public String pubTime() {
        if (pubTime == null) {
            return null;
        }
        if (pubTime.equals("")) {
            return "";
        }
        try {
//            double ll = Double.parseDouble(pubTime);
//            DecimalFormat dt = new DecimalFormat("#.#");
//            return dt.format(ll);
            long ll = new Double(pubTime).longValue();
            return ll + "";
        } catch (Exception e) {
            return "";
        }

    }

    public String getDoprime_time() {
        return doprime_time;
    }

    public void setDoprime_time(String doprime_time) {
        this.doprime_time = doprime_time;
    }

    public int getGood_num() {
        return good_num;
    }

    public void setGood_num(int good_num) {
        this.good_num = good_num;
    }

    public int getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(int follow_num) {
        this.follow_num = follow_num;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void pubTime_$eq(String pubTime) {
        this.pubTime = pubTime;
    }

    // add by trade end
    public String callback() {
        return callback;
    }

    public void callback_$eq(String callback) {
        this.callback = callback;
    }

    public int start() {
        return start;
    }

    public void start_$eq(int start) {
        this.start = start;
    }

    public boolean isEssence() {
        return isEssence;
    }

    public void isEssence_$eq(boolean isEssence) {
        this.isEssence = isEssence;
    }

    public String category() {
        return category;
    }

    public void category_$eq(String category) {
        this.category = category;
    }

    public boolean ding() {
        return ding;
    }

    public BoZhu boZhu() {
        return boZhu;
    }

    public void boZhu_$eq(BoZhu boZhu) {
        this.boZhu = boZhu;
    }

    public Weibo newWeiBo() {
        return newWeiBo;
    }

    public void newWeiBo_$eq(Weibo newWeiBo) {
        this.newWeiBo = newWeiBo;
    }

    public List<HotTopic> hotTopics() {
        return hotTopics;
    }

    public void hotTopics_$eq(List<HotTopic> hotTopics) {
        this.hotTopics = hotTopics;
    }

    public Weibo weibo() {
        return weibo;
    }

    public void weibo_$eq(Weibo weibo) {
        this.weibo = weibo;
    }

    public ImageUrlInfo imageUrlInfo() {
        return imageUrlInfo;
    }

    public void imageUrlInfo_$eq(ImageUrlInfo imageUrlInfo) {
        this.imageUrlInfo = imageUrlInfo;
    }

    public String md5() {
        return md5;
    }

    public void md5_$eq(String md5) {
        this.md5 = md5;
    }

    public String kid() {
        return kid;
    }

    public void kid_$eq(String kid) {
        this.kid = kid;
    }

    public String id() {
        return id;
    }

    public void id_$eq(String id) {
        this.id = id;
    }

    public String srpId() {
        return srpId;
    }


    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }

    public String description() {
        return description;
    }

    public void description_$eq(String description) {
        this.description = description;
    }

    public List<String> image() {
        return image;
    }

    public void image_$eq(List<String> image) {
        this.image = image;
    }

    public String source() {
        return source;
    }

    public void source_$eq(String source) {
        this.source = source;
    }

    public String date() {

        try {
//            double ll = date;
//            DecimalFormat dt = new DecimalFormat("#.#");
            long ll = new Double(date).longValue();
            return ll + "";
        } catch (Exception e) {
            return "0";
        }
    }

//    public String getDateId(){
//        return date+"";
//    }

    public void date_$eq(String d) {
        if (d == null || d.equals("")) {
            this.date = 0;
        } else {
            this.date = Long.parseLong(d);
        }
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String quality() {
        return quality;
    }

    public void quality_$eq(String quality) {
        this.quality = quality;
    }

    public String length() {
        return length;
    }

    public void length_$eq(String length) {
        this.length = length;
    }

    public String author() {
        return author;
    }

    public void author_$eq(String author) {
        this.author = author;
    }

    public int newsLayoutType() {
        return newsLayoutType;
    }

    public void newsLayoutType_$eq(int newsLayoutType) {
        this.newsLayoutType = newsLayoutType;
    }

    public User user() {
        return user;
    }

    public void user_$eq(User user) {
        this.user = user;
    }

    public long answerCount() {
        return answerCount;
    }

    public void answerCount_$eq(long answerCount) {
        this.answerCount = answerCount;
    }

    public long sameAskCount() {
        return sameAskCount;
    }

    public void sameAskCount_$eq(long sameAskCount) {
        this.sameAskCount = sameAskCount;
    }

    public List<AdListItem> adlist() {
        return adlist;
    }

    public void adlist_$eq(List<AdListItem> adlist) {
        this.adlist = adlist;
    }

    public boolean isFocus() {
        return isFocus || focus;
    }

    public void isFocus_$eq(boolean isFocus) {
        this.isFocus = isFocus;
    }

    public String bigImgUrl() {
        return bigImgUrl;
    }

    public void bigImgUrl_$eq(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public long pushId() {
        return pushId;
    }

    public void pushId_$eq(long pushId) {
        this.pushId = pushId;
    }

    public boolean hasRead() {
        return hasRead;
    }

    public void hasRead_$eq(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public long favoriteCount() {
        return favoriteCount;
    }

    public void favoriteCount_$eq(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public long upCount() {
        return upCount;
    }

    public void upCount_$eq(long upCount) {
        this.upCount = upCount;
    }

    public String userNick() {
        return userNick;
    }

    public void userNick_$eq(String userNick) {
        this.userNick = userNick;
    }

    public long commentCount() {
        return commentCount;
    }

    public void commentCount_$eq(long commentCount) {
        this.commentCount = commentCount;
    }

    public int isOriginal() {
        return isOriginal;
    }

    public void isOriginal_$eq(int isOriginal) {
        this.isOriginal = isOriginal;
    }

    public int wrank() {
        return wrank;
    }

    public void wrank_$eq(int wrank) {
        this.wrank = wrank;
    }

    public int mrank() {
        return mrank;
    }

    public void mrank_$eq(int mrank) {
        this.mrank = mrank;
    }

    public int score() {
        return score;
    }

    public void score_$eq(int score) {
        this.score = score;
    }

    public boolean isOfficialWebsite() {
        return this.isOfficialWebsite == 1;
    }

    public boolean focus() {
        return focus;
    }

    public void focus_$eq(boolean focus) {
        this.focus = focus;
    }

    public boolean isHeadlineTop() {
        return headlineTop;
    }

    public void setHeadlineTop(boolean headlineTop) {
        this.headlineTop = headlineTop;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void init() {
        start_$eq(this.start);
//                item.category_$eq(category);
        isOriginal = eq(source, "搜悦原创") || isOriginal == 1 ? 1 : 0;
        if (isOriginal == 1) source = "搜悦原创";
        // 类型为新闻搜索，且非焦点图，需要将地址替换为又拍云地址
        if (image != null && !(isFocus || focus)) {
            for (int j = 0; j < image.size(); j++) {
                String imgUrl = image.get(j);
                if (imgUrl.startsWith("http://souyue-image.b0.upaiyun.com/") || imgUrl.startsWith("http://sns-img.b0.upaiyun.com/")) {
                    imgUrl = imgUrl.replaceAll("!.+$", "");
                    image.set(j, imgUrl + (category != null && category.equals("RSS") ? "_android.jpg" : "") + "!android");
                } else if (category.equals("新闻搜索")) {
                    image.set(j, replaceImageWithUpyun(imgUrl));
                }
            }
        }
        if (!TextUtils.isEmpty(srpid)) {
            newsLayoutType = SearchResult.NEWS_TYPE_SPECIAL_TOPIC;
        }
        if (image != null && image.size() != 0) {

            if (isFocus() || focus())
                newsLayoutType = SearchResult.NEWS_TYPE_FOCUS;
            else if (image.size() >= 3)
                newsLayoutType = SearchResult.NEWS_TYPE_IMAGE_THREE;
                   /* else if (image.size() == 2)
                        newsLayoutType = SearchResult.NEWS_TYPE_IMAGE_TWO);*/
            else if (image.size() == 2)
                newsLayoutType = SearchResult.NEWS_TYPE_IMAGE_TWO;
            else
                newsLayoutType = SearchResult.NEWS_TYPE_IMAGE_ONE;
            if (headlineTop) {
                newsLayoutType = SearchResult.NEWS_TYPE_HEADLINETOP;
            }

            if (isFocus() || focus()) {
                image.set(0, bigImgUrl());
            }

        }
    }

    /**
     * 替换中搜图片地址为又拍云地址
     */
    private String replaceImageWithUpyun(String urlSource) {
        // 又拍云格式
        String picUpyun = "http://souyue-image.b0.upaiyun.com/newspic/list/$2/$3/$1_android.jpg!android";
        // 原图格式
        String picZhongsou = "http://pic.+zhongsou\\.com.+(w{3}(\\w{2})(\\w{2})\\w{12}).*";
        return urlSource.replaceAll(picZhongsou, picUpyun);
    }

    private boolean eq(String eq, String eq0) {
        if (eq != null && eq0 != null)
            return eq.equals(eq0);
        return false;
    }

    //专题页面新增：
    public String tag;//标签
    public String pic;// 大图地址
    public String descreption; //出现俩描述，服务器接口问题
    public String srpid; //出现俩srpid ,服务器接口问题。
    public String happenTime; // 出现的时间
    public String day;

    //图集页面新增
    public String isRecommend;
    public String jumpType;
    public String newsType;
    public String sourceId;

}
