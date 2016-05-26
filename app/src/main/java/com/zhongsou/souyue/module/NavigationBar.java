package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NavigationBar extends ResponseObject {

    public int page = 1;
    private String title = "";
    private String url = "";
    private String category = "";
    private String md5 = "";
    private List<String> image = new ArrayList<String>();
    private int typeId;
    private String tag_id;        // 多圈吧标签id（4.1.0多圈吧改造增加字段）
    private long interest_id;   // 兴趣圈id（4.1.0多圈吧改造增加字段）
    private String onlyjing;   // 圈吧是否只显示精贴（4.1.0多圈吧改造增加字段）"1"代表只显示精华帖
    private String right;       //4.1.0 新微件，信息发布，引入1-全部成员可发布  2-仅圈主可发布
    private int typeSt; //后台专门为统计系统所加，专门应对微件类型为 -4 的情况
    private int allowEdit;

    public int getTypeSt() {
        return typeSt;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getOnlyjing() {
		return onlyjing;
	}
    
    public void setOnlyjing(String onlyjing) {
		this.onlyjing = onlyjing;
	}

    public long getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public NavigationBar() {
    }

    public NavigationBar(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String title() {
        return title;
    }

    public void title_$eq(String title) {
        this.title = title;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String category() {
        return category;
    }

    public void category_$eq(String category) {
        this.category = category;
    }

    public String md5() {
        return md5;
    }

    public void md5_$eq(String md5) {
        this.md5 = md5;
    }

    public List<String> image() {
        return image;
    }

    public void image_$eq(List<String> image) {
        this.image = image;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
    
    public int getAllowEdit() {
		return allowEdit;
	}

	public void setAllowEdit(int allowEdit) {
		this.allowEdit = allowEdit;
	}
}
