package com.zhongsou.souyue.module;

import java.util.List;

/**
 * @author YanBin
 * @version V1.0
 * @project trunk
 * @Description 模板请求 返回数据实体
 * @date 2016/3/26
 */
public class TemplateDataAll {
    private String title;
    private String datetime;
    private String source;
    private String content;
    private List<String> imageList;
    private int isSubscribe;
    private int interestRole;
    private String singId;
    private String templateVersion;
    private String shareTitle;
    private List<String> shareImage;
    private String shortUrl;
    private InnerWidgetData widgetData;

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

    public int getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(int isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public int getInterestRole() {
        return interestRole;
    }

    public void setInterestRole(int interestRole) {
        this.interestRole = interestRole;
    }

    public String getSingId() {
        return singId;
    }

    public void setSingId(String singId) {
        this.singId = singId;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public List<String> getShareImage() {
        return shareImage;
    }

    public void setShareImage(List<String> shareImage) {
        this.shareImage = shareImage;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public InnerWidgetData getWidgetData() {
        return widgetData;
    }

    public void setWidgetData(InnerWidgetData widgetData) {
        this.widgetData = widgetData;
    }

    public class InnerWidgetData {
        private List<InnerWidgetNavData> nav;
        private boolean isMenuShow;

        public List<InnerWidgetNavData> getNavDataList() {
            return nav;
        }

        public void setNavDataList(List<InnerWidgetNavData> navDataList) {
            this.nav = navDataList;
        }

        public boolean isMenuShow() {
            return isMenuShow;
        }

        public void setIsMenuShow(boolean isMenuShow) {
            this.isMenuShow = isMenuShow;
        }

        public class InnerWidgetNavData {
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
    }
}
