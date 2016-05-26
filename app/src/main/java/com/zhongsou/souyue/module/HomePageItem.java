package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class HomePageItem extends ResponseObject {

    public enum CATEGORY {
        /**
         * category类型 ：精彩推荐
         */
        system,
        /**
         * category类型 ：srp词组
         */
        group,
        /**
         * category类型 ：rss词组
         */
        rss,
        /**
         * category类型 ：我的收藏
         */
        favorite,
        /**
         * category类型 ：超级分享大赛
         */
        srp,
        /**
         * category类型 ：搜悦广场
         */
        square,
        /**
         * category类型 ：应用宝典
         */
        app,
        /**
         * category类型 ：网址导航
         */
        url,
        /**
         * category类型 ：移动商街
         */
        discount,
        /**
         * category类型 ：我的商家
         */
        business,
        /**
         * category类型 ：我的原创
         */
        selfCreate,
        /**
         * category类型 ：老虎机
         */
        slotMachine,
        /**
         * category类型 ：我的聊天
         */
        im,
        /**
         * category类型 ：中搜零拍
         */
        interactWeb,
        /**
         * category类型 ：兴趣圈
         */
        interest,
        /**
         * category类型 ：我的订阅
         */
        subscibe,
        /**
         * category类型 ：综合搜索
         */
        search,
        /**
         * category类型 ：二维码扫描
         */
        scan,
        /**
         * category类型 ：增加首页
         */
        homepage,
        /**
         * 添加category类型 ：个人中心
         */
        pcenter,
        /**
         * 添加category类型 ：移动商城
         */
        shop,
        /**
         * 添加category类型 ：中搜币商城
         */
        moneyshop,
        /**
         * 添加category类型 ： 零拍
         */
        lingpai,
        /**
         * 添加category类型 ：企业黄页
         */
        corplist,
        /**
         * 添加category类型 ：行业资讯
         */
        infolist,
        /**
         * 添加category类型 ：供应商机
         */
        productlist,
        /**
         * 添加category类型 ： 资讯详情
         */
        infodetail,
        /**
         * 添加category类型 ：供应详情
         */
        productdetail,
        /**
         * 添加category类型 ：询报价列表
         */
        supdem,
        /**
         * 添加category类型 ：企业详情
         */
        corpdetail,
        /**
         * category类型 : 预留cma
         */
        cma,
        /**
         * 跳到指定兴趣圈
         */
        special_interest,
        /**
         * 添加category类型 ：积分兑换中搜币
         */
        exchangeZsb,
        /**
         * 添加category类型 ：充值
         */
        rechargeZsb,
        /**
         * 添加category类型 ：跳无头无尾的webview
         */
        interactNoHeadWeb;
    }

    public static final String GROUP = "group";
    public static final String RSS = "rss";
    public static final String SYSTEM = "system";
    public static final String SRP = "srp";
    public static final String SELF_CREATE = "selfCreate";
    public static final String SQUARE = "square";
    public static final String APP = "app";
    public static final String URL = "url";
    public static final String DISCOUNT = "discount";
    public static final String FAVORITE = "favorite";
    public static final String BUSSINESS = "business";
    public static final String IM = "im";
    public static final String CMA = "cma";
    // 企业词订阅
    public static final int ENT_TYPE = 4; // 合作商家
    public static final int ENT_COMMON_TYPE = 41; // 普通商家

    private String srpId = ""; // 分享大赛
    private String keyword = ""; // 分享大赛
    private String title = ""; // 显示的标题
    private String category = ""; // 类型
    private String url = ""; // url地址
    private String cma = ""; // cma
    private String english = ""; // 英文显示的标题
    private String id = ""; // id，如否是订阅,用于删除订阅
    private long subId = 0; // 张亮 新闻源订阅id
    private String entId = ""; // 普通商家ID是字符串
    private int subscribeType; // 企业词订阅类别，4合作商家，41普通商家
    private String images;// 原图
    private String imagesGray;// 灰色图
    private int subCount;// 订阅的个数
    private String image;// icon
    private boolean hasNew;
    private boolean outBrowser;
    // add by trade
    // 超A模板
    private String action;// 启动商城的action
    private String streetname;// 移动商街默认搜索关键词
    private String interestinfo; // 左树新增跳到指定兴趣圈类型
    private String interest_ID; // 指定兴趣圈ID
    private String interest_SRPID; // 指定兴趣圈SRPID
    private String interest_SRPWORD; // 指定兴趣圈SRP词
    private String interest_name; // 指定兴趣圈名称
    // 行业
    private String infosecid;// 资讯列表
    private String infoid;// 资讯详情
    private String productsecid;// 供应列表
    private String productid;// 供应详情
    private String corpigid;// 企业详情

    public String getInterestID() {
        return GetIntesetInfo(1);
    }

    private String getInterestName() {
        return GetIntesetInfo(0);
    }

    public String getInterestSRPWORD() {
        return GetIntesetInfo(3);
    }

    public String getInterestSRPID() {
        return GetIntesetInfo(2);
    }

    private String GetIntesetInfo(int location) {

        if (interestinfo == null || ("").equals(interestinfo)) {
            return "";
        } else {
            String[] interestArray = interestinfo.split("\\|");
            return interestArray.length > location ? interestArray[location]
                    : "";
        }

    }

    public void interestinfo_$eq(String interestinfo) {
        this.interestinfo = interestinfo;
    }

    // 搜悦广场
    public boolean isSquare() {
        return SQUARE.equals(this.category);
    }

    // 应用宝典
    public boolean isApp() {
        return APP.equals(this.category);
    }

    // 网址导航
    public boolean isUrl() {
        return URL.equals(this.category);
    }

    public boolean isCma() {
        return CMA.equals(this.category);
    }

    // 优惠商家
    public boolean isDiscount() {
        return DISCOUNT.equals(this.category);
    }

    /**
     * category类型 是否为 超级分享大赛
     */
    public boolean isSrp() {
        return SRP.equals(this.category);
    }

    /**
     * category类型 是否为 收藏
     */
    @Deprecated
    public boolean isFavorite() {
        return FAVORITE.equals(this.category);
    }

    // 是否是系统
    public boolean isSystem() {
        return SYSTEM.equals(this.category);
    }

    public boolean isBusiness() {
        return BUSSINESS.equals(this.category);
    }

    // 是否是订阅的srp分组
    public boolean isGroup() {
        return GROUP.equals(this.category);
    }

    // 是否是rss订阅
    public boolean isRss() {
        return RSS.equals(this.category);
    }

    // 是否是一个简单标签
    public boolean isLabel() {
        return !isFavorite() && !isSystem() && !isGroup() && !isRss();
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String entId() {
        return entId;
    }

    public void entId_$eq(String entId) {
        this.entId = entId;
    }

    public String category() {
        return category;
    }

    public void category_$eq(String category) {
        this.category = category;
    }

    public String url() {
        return url;
    }

    public String cma() {
        return cma;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public void cma_$eq(String cma) {
        this.cma = cma;
    }

    public String english() {
        return english;
    }

    public void english_$eq(String english) {
        this.english = english;
    }

    public String id() {
        return id;
    }

    public void id_$eq(String id) {
        this.id = id;
    }

    public long subId() {
        return subId;
    }

    public void subId_$eq(long subId) {
        this.subId = subId;
    }

    public int subscribeType() {
        return subscribeType;
    }

    public void subscribeType_$eq(int subscribeType) {
        this.subscribeType = subscribeType;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getImagesGray() {
        return imagesGray;
    }

    public void setImagesGray(String imagesGray) {
        this.imagesGray = imagesGray;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public boolean isOutBrowser() {
        return outBrowser;
    }

    public void setOutBrowser(boolean outBrowser) {
        this.outBrowser = outBrowser;
    }

    public String streetname() {
        return streetname;
    }

    public void streetname_$eq(String streetname) {
        this.streetname = streetname;
    }

    public String action() {
        return action;
    }

    public void action_$eq(String action) {
        this.action = action;
    }

    public String infoid() {
        return infoid;
    }

    public void infoid_$eq(String infoid) {
        this.infoid = infoid;
    }

    public String infosecid() {
        return infosecid;
    }

    public void infosecid_$eq(String infosecid) {
        this.infosecid = infosecid;
    }

    public String productid() {
        return productid;
    }

    public void productid_$eq(String productid) {
        this.productid = productid;
    }

    public String productsecid() {
        return productsecid;
    }

    public void productsecid_$eq(String productsecid) {
        this.productsecid = productsecid;
    }

    public String corpigid() {
        return corpigid;
    }

    public void corpigid_$eq(String corpigid) {
        this.corpigid = corpigid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((english == null) ? 0 : english.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
        result = prime * result + ((srpId == null) ? 0 : srpId.hashCode());
        result = prime * result + (int) (subId ^ (subId >>> 32));
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HomePageItem other = (HomePageItem) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (english == null) {
            if (other.english != null)
                return false;
        } else if (!english.equals(other.english))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (keyword == null) {
            if (other.keyword != null)
                return false;
        } else if (!keyword.equals(other.keyword))
            return false;
        if (srpId == null) {
            if (other.srpId != null)
                return false;
        } else if (!srpId.equals(other.srpId))
            return false;
        if (subId != other.subId)
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

}
