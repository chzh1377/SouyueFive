package com.zhongsou.souyue.module;


/**
 * @author zyw
 */

public class RecommendTabSubListItem extends ResponseObject {
    private String category;
    private String keyword;
    private String srpId;

    private boolean issubed; // 是否已经订阅
    private String title; // 标题
    private String desc; // 描述
    private String imageurl; // 图片地址
    private int action = -1; // 动作 默认-1
    private boolean isDefault; // 默认订阅
    private long subId; // 订阅id

    private int isChecked;//是否选中
    public static int CHECKED_CHECKED = 1; // 未选中状态
    public static int CHECKED_UNCHECKED = 2; // 选中状态

    public int isChecked() {
        return isChecked;
    }

    public void setChecked(int checked) {
        isChecked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RecommendTabSubListItem) {
            RecommendTabSubListItem item = (RecommendTabSubListItem) o;
            return category.equals(item.getCategory()) && keyword.equals(item.getKeyword()) && srpId.equals(item.getSrpId());
        }
        return false;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public int hashCode() {
        return category.hashCode() + keyword.hashCode() * 2 + srpId.hashCode() * 3;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public boolean issubed() {
        return issubed;
    }

    public void setIssubed(boolean issubed) {
        this.issubed = issubed;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

//    public String toRequestString() {
//        StringBuilder sb = new StringBuilder();
//        if (action == 0) {
//            //添加
//            sb.append("{\"category\":\"");
//            sb.append(category);
//            sb.append("\",\"srpId\":\"");
//            sb.append(srpId);
//            sb.append("\",\"keyword\":\"");
//            sb.append(keyword);
//            sb.append("\",\"state\":\"");
//            sb.append("0\"}");
//        } else if (action == 1) {
//            //删除
//            sb.append("{\"category\":\"");
//            sb.append(category);
//            sb.append("\",\"srpId\":\"");
//            sb.append(srpId);
//            sb.append("\",\"state\":\"");
//            sb.append("1\"}");
//        }
//        return sb.toString();
//    }

    public SuberedItemInfo toSuberedItemInfo() {
        SuberedItemInfo info = new SuberedItemInfo();
        /**
         *  private String category;  //必要字段  rss special srp interest
         private String title;     //
         private String keyword;   //必要
         private long id;
         private String srpId;    //必要
         private String image;
         */
        info.setCategory(getCategory());
        info.setKeyword(getKeyword());
        info.setTitle(getTitle());
        info.setSrpId(getSrpId());
        info.setImage(getImageurl());
        return info;
    }

    public long getSubId() {
        return subId;
    }

    public void setSubId(long subId) {
        this.subId = subId;
    }
}
