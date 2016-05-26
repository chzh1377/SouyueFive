package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class CWidgetSecondList extends ResponseObject implements IWidgetData {

    private String keyword = "";
    private String srpId = "";
    private long interestId;
    private String inerestName = "";
    private String blogId = "";
    private String url = "";
    private String urlOrig = "";
    private int keywordType;//新闻类型1 srp 2:interest,
    private int keywordCate;
    private String interestLogo;//兴趣圈logo
    private String interestType;//兴趣圈类型

    private boolean showMenu = true; // 是否显示二级菜单,默认是true

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    private List<NavigationBar> nav = new ArrayList<NavigationBar>();

    public int getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(int keywordType) {
        this.keywordType = keywordType;
    }

    public String getInterestLogo() {
        return interestLogo;
    }

    public void setInterestLogo(String interestLogo) {
        this.interestLogo = interestLogo;
    }

    public String getInterestType() {
        return interestType;
    }

    public void setInterestType(String interestType) {
        this.interestType = interestType;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getSrpId() {
        return srpId;
    }

    public void setSrpId(String srpId) {
        this.srpId = srpId;
    }

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }

    public String getInerestName() {
        return inerestName;
    }

    public void setInerestName(String inerestName) {
        this.inerestName = inerestName;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlOrig() {
        return urlOrig;
    }

    public void setUrlOrig(String urlOrig) {
        this.urlOrig = urlOrig;
    }

    public List<NavigationBar> getNav() {
        return nav;
    }
    public void setNav(List<NavigationBar> nav) {
        this.nav = nav;
    }

    public int getKeywordCate() {
        return keywordCate;
    }

    public void setKeywordCate(int keywordCate) {
        this.keywordCate = keywordCate;
    }
}
