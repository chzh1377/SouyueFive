package com.zhongsou.souyue.module;

public class HomeBallBean extends ResponseObject {
    private long    id;
    private String  category;
    private String  title;
    private String  keyword;
    private String  srpId;
    private String  image;
    private String  url;
    //此字段专门统计用
    private String  sy_channel;
    private int     isStop;
    private int     lastFixed;
    private int     invokeType;
    private boolean subscription;
    private boolean mSub;//显示加号
    public static String SPECIAL       = "special";
    public static String YAOWEN        = "masternews";
    public static String HEADLINE      = "headline";
    public static String RECOMMEND     = "recommend";
    public static String SRP           = "srp";
    public static String HISTORY       = "history";
    public static String INTEREST      = "interest";
    public static String SPECIAL_TOPIE = "specials";
    public static String GROUP_NEWS    = "group";
//    category=group


    public String getSy_channel() {
        return this.sy_channel;
    }

    public void setSy_channel(String sy_channel) {
        this.sy_channel = sy_channel;
    }

    public boolean isSubscription() {
        return subscription;
    }

    public void setSubscription(boolean subscription) {
        this.subscription = subscription;
    }

    public boolean isSub() {
        return mSub;
    }

    public void setSub(boolean mSub) {
        this.mSub = mSub;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIsStop() {
        return isStop;
    }

    public void setIsStop(int isStop) {
        this.isStop = isStop;
    }

    public int getLastFixed() {
        return lastFixed;
    }

    public void setLastFixed(int lastFixed) {
        this.lastFixed = lastFixed;
    }

    public int getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(int invokeType) {
        this.invokeType = invokeType;
    }

    /**
     * 是否有点击进入的功能
     *
     * @param type
     * @return
     */
    public static boolean isEnable(String type) {
        boolean able;
        if (type.equals(HomeBallBean.HEADLINE)
                || type.equals(HomeBallBean.RECOMMEND) || type.equals(HomeBallBean.SPECIAL_TOPIE) || type.equals(HomeBallBean.HISTORY)) {
            able = false;
        } else {
            able = true;
        }
        return able;
    }

    public static boolean isEnableSub(String type) {
        boolean able;
        if (type.equals(HomeBallBean.SRP)
                || type.equals(HomeBallBean.INTEREST)
                || type.equals(HomeBallBean.SPECIAL)
                || type.equals(HomeBallBean.GROUP_NEWS)) {
            able = true;
        } else {
            able = false;
        }
        return able;
    }
}
