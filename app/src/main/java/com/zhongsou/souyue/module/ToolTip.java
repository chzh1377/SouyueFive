package com.zhongsou.souyue.module;

import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.utils.StringUtils;

@SuppressWarnings("serial")
public class ToolTip extends ResponseObject {
    private String _id;
    private Long id;
    private String category = "";
    private String rssImage = "";
    private String url = "";
    private String keyword = "";
    private String srpId = "";
    private String srpCate = "";
    private String m = ""; // 主词
    private String g = ""; // 分义标注
    private String version = "";//词性
    private boolean xiaoqi;
    private String lastUpdate;
    private boolean ischeck = false;
    
    public ToolTip(){}
    public ToolTip( String _id, 
		    		String category, 
		    		String rssImg, 
		    		String url, 
		    		String keyword, 
		    		String srpId, 
		    		String srpCate, 
		    		String m, 
		    		String g, 
		    		String lastUpdate, 
		    		String version
    		) {
        this._id = _id;
        this.category = category;
        this.rssImage = rssImg;
        this.url = url;
        this.keyword = keyword;
        this.srpId = srpId;
        this.srpCate = srpCate;
        this.m = m;
        this.g = g;
        this.lastUpdate = lastUpdate;
        this.version = version;
    }
    
    public boolean ischeck() {
        return ischeck;
    }

    public void ischeck_$eq(boolean ischeck) {
        this.ischeck = ischeck;
    }
    
    public String _id(){
        return _id;
    }
    public void _id_$eq(String _id){
        this._id = _id;
    }
    public String lastUpdate(){
        return lastUpdate;
    }
    public void lastUpdate_$eq(String lastUpdate){
        this.lastUpdate = lastUpdate;
    }
    
    public Long id(){
        return id;
    }
    public void id_$eq(Long id){
        this.id = id;
    }
   
    public ToolTip(HttpJsonResponse httpJsonResponse){
        this.keyword = httpJsonResponse.getHeadString("keyword");
        this.version = httpJsonResponse.getHeadString("version");
        this.xiaoqi = httpJsonResponse.getHeadBoolean("xiaoqi");
    }

    public String version(){
        return version;
    }

    public void version_$eq(String str){
        this.version = str;
    }
    public String category() {
        return category;
    }

    public void category_$eq(String category) {
        this.category = category;
    }

    public String rssImage() {
        return rssImage;
    }

    public void rssImage_$eq(String rssImage) {
        this.rssImage = rssImage;
    }

    public String url() {
        return url;
    }

    public void url_$eq(String url) {
        this.url = url;
    }

    public String keyword() {
        return keyword;
    }

    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }

    public String srpId() {
        return srpId;
    }

    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }

    public String srpCate() {
        return srpCate;
    }

    public void srpCate_$eq(String srpCate) {
        this.srpCate = srpCate;
    }

    public String m() {
        return m;
    }

    public void m_$eq(String m) {
        this.m = m;
    }

    public String g() {
        return g;
    }

    public void g_$eq(String g) {
        this.g = g;
    }

    public boolean isRss(){
        return this.category != null && this.category.equals("rss");
    }

    public boolean isSrp(){
        return (this.category != null && this.category.equals("srp")) && !StringUtils.isEmpty(this.srpId);
    }

    public boolean isHotSrp(){
        return !StringUtils.isEmpty(this.keyword) && !StringUtils.isEmpty(this.srpId);
    }

    public boolean xiaoqi(){
        return xiaoqi;
    }

    public boolean isHot(){
        return this.category != null && this.category.equals("hotWord");
    }
}
