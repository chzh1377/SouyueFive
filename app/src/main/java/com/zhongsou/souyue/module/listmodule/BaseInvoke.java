package com.zhongsou.souyue.module.listmodule;

import com.zhongsou.souyue.DontObfuscateInterface;
import java.io.Serializable;
import java.util.List;

/**
 * 跳转类型
 * Created by lvqiang on 15/12/23.
 */
public class BaseInvoke implements DontObfuscateInterface,Serializable,Cloneable {
    /** srp新闻详情页 */
    public static final int INVOKE_TYPE_SRP = 10;
    /** srp新闻列表页 */
    public static final int INVOKE_TYPE_SRP_INDEX = 11;
    /** 圈贴详情页 */
    public static final int INVOKE_TYPE_INTEREST = 20;
    /** 圈贴列表页 */
    public static final int INVOKE_TYPE_INTEREST_INDEX = 21;
    /** 图集页 */
    public static final int INVOKE_TYPE_PHOTOS = 30;
    /** gif详情页 */
    public static final int INVOKE_TYPE_GIF = 40;
    /** 段子详情页 */
    public static final int INVOKE_TYPE_JOKE = 50;
    /** 专题页 */
    public static final int INVOKE_TYPE_SPECIA = 60;
    /** 要闻页 */
    public static final int INVOKE_TYPE_FOCUSNEWS = 70;
    /** 视频详情页 5.2 */
    public static final int INVOKE_TYPE_VIDEO = 80;
    /** 视频 5.2 web 页面*/
    public static final int INVOKE_TYPE_VIDEO_WEB = 81;
    /** 只有 keyword 的 新闻详情页 一般 从通知栏过来*/
    public static final int INVOKE_TYPE_NEW_DETAIL = 92;
    /** 系统浏览器 */
    public static final int INVOKE_TYPE_BROWSER = 100;
    /** app浏览器 */
    public static final int INVOKE_TYPE_BROWSER_APP = 110;
    /** app浏览器没有底部 */
    public static final int INVOKE_TYPE_BROWSER_APP_NOBOTTOM = 112;
    /**推送来的图集*/
    public static final int INVODE_TYPE_PUSH_PHOTOS = 31;

    public static final int FLAG_SKIP_COMMENT=0;

    private int type;
    private long id;
    private List<String> image;//想分又分不出来
    private String srpId;
    private String keyword;
    private String channelId;//要闻跳转定位
    private String url;
    private long interestId;
    private long blogId;
    private String iconUrl;
    private String md5;//srp跳转需要定位到某一项
    private String interestName;
    private String category;
    private String bigImgUrl;

    private String title;
    private String desc;
    private String chan;//统计需要定位
    private int mFlag;
    private String signId;//  圈子部分 添加的
    private BaseListData data;

    private int isInvokeTemplate; // 5.2  增加属性，是否跳转模板详情页 0：不跳转 1：跳转
    public static int INVOKETEMPLATE =1;
    public int getIsInvokeTemplate() {
        return isInvokeTemplate;
    }

    public void setIsInvokeTemplate(int isInvokeTemplate) {
        this.isInvokeTemplate = isInvokeTemplate;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channel) {
        this.channelId = channel;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getChan() {
        return chan;
    }

    public void setChan(String chan) {
        this.chan = chan;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public void setBigImgUrl(String bigImgUrl) {
        this.bigImgUrl = bigImgUrl;
    }

    public BaseListData getData() {
        return data;
    }

    public void setData(BaseListData data) {
        this.data = data;
    }

    public void setFalg(int index,boolean bool){
        if (bool){
            mFlag =1<<index|mFlag;
        }else {
            mFlag = ~(1<<index)&mFlag;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getFlag(int index){
        return ((1<<index)&mFlag)>0;
    }

    @Override
    public BaseInvoke clone() {
        try {
            return (BaseInvoke) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
