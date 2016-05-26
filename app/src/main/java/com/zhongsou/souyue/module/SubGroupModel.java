package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by zyw on 2016/3/26.
 */
public class SubGroupModel implements DontObfuscateInterface {
    public static final String TAG = SubGroupModel.class.getSimpleName();
    /**
     * category : interest
     * id : 5667
     * image : http://edit.zhongsou.com/Img/getSrpImg?srpId=97d9491ebf3d898ea6117f8e9b280813
     * keyword : 历史圈
     * master : 0
     * sort_num : 1458819575151
     * srpId : 97d9491ebf3d898ea6117f8e9b280813
     * title : 历史圈
     * type : 0
     */
    private String category;
    private int    id;
    private String image;
    private String keyword;
    private int    master;
    private long   sort_num;
    private String srpId;
    private String title;
    private int    type;
    private String md5;
    private String interestName;
    private int    invokeType = -1;

    public int getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(int invokeType) {
        this.invokeType = invokeType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setMaster(int master) {
        this.master = master;
    }

    public void setSort_num(long sort_num) {
        this.sort_num = sort_num;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getMaster() {
        return master;
    }

    public long getSort_num() {
        return sort_num;
    }

    public String getSrpId() {
        return srpId;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }
}
