package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class AdListItem extends ResponseObject {

    private String image = "";
    private int width = 0;
    private int height = 0;
    private String url = "";
    private long id = 0;
    private String title = "";
    private String description = "";
    private int category = 0;
    private String contentType = ""; // 提供给广告系统使用，值为json或html，如果是html，则使用内置的webview打开url，如果是json，调用adDetail(String
                                     // url)接口
    /**
     * 搜悦4.2.2广告接口升级新增
     */
    private String event = "";
    private String type = "web";//web：网页类型广告，点击后直接回调event字段的url即可;
    							//download：下载类型，点击广告后等待广告下载完成，完成后回调event字段的url即可
    public static final String WEB_AD = "web";
    public static final String DOWNLOAD_AD = "download";
    
    public String image() {
        return image;
    }
    public void image_$eq(String image) {
        this.image = image;
    }
    public int width() {
        return width;
    }
    public void width_$eq(int width) {
        this.width = width;
    }
    public int height() {
        return height;
    }
    public void height_$eq(int height) {
        this.height = height;
    }
    public String url() {
        return url;
    }
    public void url_$eq(String url) {
        this.url = url;
    }
    public long id() {
        return id;
    }
    public void id_$eq(long id) {
        this.id = id;
    }
    public String title() {
        return title;
    }
    public void title_$eq(String title) {
        this.title = title;
    }
    public String description() {
        return description;
    }
    public void description_$eq(String description) {
        this.description = description;
    }
    public int category() {
        return category;
    }
    public void category_$eq(int category) {
        this.category = category;
    }
    public String contenttype() {
        return contentType;
    }
    public void contenttype_$eq(String contentType) {
        this.contentType = contentType;
    }

    public String event() {
        return event;
    }
    public void event_$eq(String event) {
        this.event = event;
    }
    public String type() {
        return type;
    }
    public void type_$eq(String type) {
        this.type = type;
    }
    
}
