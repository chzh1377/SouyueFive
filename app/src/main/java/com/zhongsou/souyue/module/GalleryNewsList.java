package com.zhongsou.souyue.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {
     "content": [
         {
         "url": "http://zsimg5.b0.upaiyun.com/7b01a8e3-581c-4ab5-bf8a-8947a7baf8f2.jpg!sy",
         "title": "图集标题",
         "desc": "描述1--描述1--描述1--描述1--描述1--"
         },

     ],
     "relate": [
        {
        "img": "http://zsimg5.b0.upaiyun.com/c19234a4-7f12-44f2-8f9e-b7c9969df7b6.jpg!sy",
        "title": "相关图集1",
        "url": "http://sycms.zhongsou.com/pics.html"
        }
     ]
 }

 这里有个坑，因为服务器后来改过接口，所以数据格式变了。更多信息请看parse部分
 */

@SuppressWarnings("serial")
public class GalleryNewsList extends ResponseObject implements Serializable {

    private List<GalleryNewsItem> content; //大图图集
    private List<GalleryNewsItem> relate; // 相关推荐

    private List<String> infos; // 大图浏览用到的描述
    private List<String> images; // 大图浏览用到的图
    private List<String> titles; // 标题列表

    private String keyword;
    private String newstime;
    private String source;
    private String srpid;
    private String title;
    private String url;//如果是推送接口的话，会有一个url

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getNewstime() {
        return newstime;
    }

    public void setNewstime(String newstime) {
        this.newstime = newstime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSrpid() {
        return srpid;
    }

    public void setSrpid(String srpid) {
        this.srpid = srpid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public List<GalleryNewsItem> getContent() {
        return content;
    }

    public void setContent(List<GalleryNewsItem> content) {
        this.content = content;
    }

    public List<GalleryNewsItem> getRelate() {
        //五条或五条以上才会显示
        if(relate != null && relate.size() >= 5){
            return relate;
        }
        return null;
    }

    public void setRelate(List<GalleryNewsItem> relate) {
        this.relate = relate;
    }

    /**
     * 将所有的大图部分的描述作为一个list返回
     * @return
     */
    public List<String> getInfos(){
        if(infos == null){
            infos = new ArrayList<String>();
            for(GalleryNewsItem items: content){
                infos.add(items.getDesc());
            }
        }
        return infos;
    }

    /**
     * 将所有的大图部分的图片地址作为一个list返回
     * @return
     */
    public List<String> getImages(){
        if(images == null){
            images = new ArrayList<String>();
            for(GalleryNewsItem items: content){
                images.add(items.getUrl());
            }
        }
        return images;
    }

    @Override
    public String toString() {
        return "GalleryNewsList{" +
                "content=" + content +
                ", relate=" + relate +
                ", infos=" + infos +
                ", images=" + images +
                ", titles=" + titles +
                '}';
    }
}
