package com.zhongsou.souyue.module;

import java.util.List;

public class RecommendSubModel extends ResponseObject {
    private String title;
    private String titlecolor;
//    private String backgroundimage;
    private List<RecommendSubTab> recommends;

    public RecommendSubModel(String title, String titlecolor, String backgroundimage,
                             List<RecommendSubTab> recommends) {
        this.title = title;
        this.titlecolor = titlecolor;
//        this.backgroundimage = backgroundimage;
        this.recommends = recommends;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitlecolor() {
        return titlecolor;
    }

    public void setTitlecolor(String titlecolor) {
        this.titlecolor = titlecolor;
    }

//    public String getBackgroundimage() {
//        return backgroundimage;
//    }

//    public void setBackgroundimage(String backgroundimage) {
//        this.backgroundimage = backgroundimage;
//    }

    public List<RecommendSubTab> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<RecommendSubTab> recommends) {
        this.recommends = recommends;
    }


}
