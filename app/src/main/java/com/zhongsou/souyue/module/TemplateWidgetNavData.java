package com.zhongsou.souyue.module;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description widgetData对象中nav数组中的对象实体
 * @date 2016/3/26
 */
public class TemplateWidgetNavData {
    //    widgetData: {
//        nav: [
//        {
//            title: "相关图片AAA",
//             category: "图片搜索",
//                md5: "789d401a29c2be854317f3bcd89b81d6",
//                url: "http://103.29.134.224/d3api2/imgSearch/index.html?keyword=%E9%80%97%E6%AF%94%E5%9C%88&srpId=8843d6330fb84a5205647f42f6afe853&md5=789d401a29c2be854317f3bcd89b81d6&vc=4.0",
//                allowEdit: 1
//        },
//        {},
//        {},
//        {},
//        {},
//        {},
//        {},
//        {},
//        {},
//        {},
//        {}
//        ],
//        isMenuShow: true
//    }
    private String title;
    private String category;
    private String md5;
    private String url;
    private int allowEdit;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getAllowEdit() {
        return allowEdit;
    }

    public void setAllowEdit(int allowEdit) {
        this.allowEdit = allowEdit;
    }
}
