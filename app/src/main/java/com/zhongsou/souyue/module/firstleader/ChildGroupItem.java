package com.zhongsou.souyue.module.firstleader;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by zyw on 2016/3/25.
 */
public class ChildGroupItem implements DontObfuscateInterface {
    public static final String TAG = ChildGroupItem.class.getSimpleName();
    private String  keyword;
    private String  srpId;
    private String  category;
    private int     isSelected;
    private String  image;
    private boolean isDefaultSelected;

    public boolean isDefaultSelected() {
        return isDefaultSelected;
    }

    public void setDefaultSelected(boolean defaultSelected) {
        this.isDefaultSelected = defaultSelected;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSrpId() {
        return srpId;
    }

    public String getCategory() {
        return category;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public String getImage() {
        return image;
    }
}
