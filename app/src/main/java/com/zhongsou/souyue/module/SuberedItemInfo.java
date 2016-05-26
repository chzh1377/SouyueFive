package com.zhongsou.souyue.module;

/**
 * 订阅列表item
 *
 * @author wangqiang
 */
public class SuberedItemInfo extends AGridDynamic {
    private String category;  //必要字段  rss special srp interest
    private String title;     //
    private String keyword;   //必要
    private long id;
    private String srpId;    //必要
    private String image;
    private int invokeType; //要问频道

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String status;
    private String type;    // 1.私密圈 0 非私密圈
    private String url;
    private String desc;
    private int position;   //不需要传递


    public int getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(int invokeType) {
        this.invokeType = invokeType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { //1 表示未订阅  0表示已订阅
        this.status = status;
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "suberedInfo: [ category = " + this.category + ", image = "
                + this.image + " , keyword = " + this.keyword + " , srpId = "
                + this.srpId + ", title= " + this.title + "]";
    }


}
