package com.zhongsou.souyue.module;

import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 模板显示数据
 * @date 2016/3/31
 */
public class TemplateShowData {

//    title: "这种自作主张自作主张自作主张",
//    datetime: "2015-12-15",
//    source: "李梅梅第三号",
//    content: "这种自作主张自作主张自作主张",
//    imageList: [
//            "http://souyue-image.b0.upaiyun.com/user/0004/59523330.jpg!ios",
//            "http://souyue-image.b0.upaiyun.com/user/0004/59527429.jpg!ios"
//            ],
//    templateVersion: "modle_0!interest_content_000"

    private String title;
    private String datetime;
    private String source;
    private String content;
    private List<String> imageList;
    private String templateVersion;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }
}
