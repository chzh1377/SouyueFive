package com.zhongsou.souyue.module;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.db.ReadHistoryHelper;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.service.download.Md5Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@SuppressWarnings("serial")
public class SearchResult extends ResponseObject {

    public static final int NEWS_TYPE_FOCUS = -1;// 新闻布局类型，焦点
    //    public static final int NEWS_TYPE_FOCUS_NOPIC = -2;//新闻焦点，但无图片
    public static final int NEWS_TYPE_HEADLINETOP = -3;// 新闻布局类型，要闻
    public static final int NEWS_TYPE_NORMAL = 0; // 新闻布局类型，无图
    public static final int NEWS_TYPE_IMAGE_ONE = 1; // 新闻布局类型，一张
    public static final int NEWS_TYPE_IMAGE_TWO = 2; // 新闻布局类型，两张
    public static final int NEWS_TYPE_IMAGE_THREE = 3; // 新闻布局类型，三张
    public static final int NEWS_TYPE_CLICK_REFRESH = 4; // 新闻布局类型，点击刷新
    public static final int NEWS_TYPE_SPECIAL_TOPIC = 5; // 新增的专题页面类型
    private String title;
    private String category;
    private boolean isXiaoqi;
    private String srpId;
    private String srpImage;
    private boolean showMenu;           //是否显示二级导航
    private boolean hasExpired = false; // 数据是否过期
    private BoZhu boZhu;
    private Weibo newWeiBo;
    private List<HotTopic> hotTopics;
    private List<Weibo> weibo;
    // 问答使用,keyword 在数据库中的主键
    private String keyword;
    private String kid;
    private int version;
    private boolean hideSubsrcibe;
    private long interest_id;
    private List<NavigationBar> nav;
    private List<SearchResultItem> items;
    private JsonArray questions;
    // 问答使用
    private String md5;
    // 一站到底广告
    private Ad ad = null;
    private List<Ad> adList;
    private boolean hasMore;
    private int start;
    private int keywordCate;//
    //要闻
    private JsonArray focusArrys;
    private JsonArray topArrys;
    private JsonArray newsArrys;
    private List<SearchResultItem> focusList;
    private List<SearchResultItem> topList;
    private List<SearchResultItem> newsList;
    private Gson mGson = new Gson();

    public long getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(long interest_id) {
        this.interest_id = interest_id;
    }

    public String getSrpImage() {
        return srpImage;
    }

    public void setSrpImage(String srpImage) {
        this.srpImage = srpImage;
    }

    public SearchResult(HttpJsonResponse response) {
        start = response.getHeadInt("start", 0);
        hasMore = response.getHeadBoolean("hasMore");
        md5 = response.getHeadString("md5");
        adList = new Gson().fromJson(response.getHead().getAsJsonArray("adList"), new TypeToken<List<Ad>>() {
        }.getType());
        keyword = response.getHeadString("keyword");
        kid = response.getHeadString("kid");
        version = response.getHeadInt("version", 2);
        hideSubsrcibe = response.getHeadBoolean("hideSubsrcibe");
        title = response.getHeadString("title");
        category = response.getHeadString("category");
        isXiaoqi = response.getHeadBoolean("isXiaoqi");
        srpId = response.getHeadString("srpId");
        srpImage = response.getHeadString("srpImage");
        showMenu = response.getHeadBoolean("menu");
        interest_id = response.getHeadLong("interest_id", 0);
        keywordCate = response.getHeadInt("keywordCate", 0);
        nav = new Gson().fromJson(response.getHead().getAsJsonArray("nav"), new TypeToken<List<NavigationBar>>() {
        }.getType());

        if (response.isJsonArray()) {
            items = new Gson().fromJson(response.getBodyArray(), new TypeToken<List<SearchResultItem>>() {
            }.getType());
        } else {
            questions = response.getBody().getAsJsonArray("questions");

            //focusArrys = response.getBody().getAsJsonArray("focusList");
            focusArrys = response.getBody().getAsJsonArray("focusList");
            System.out.print("json-----"+focusArrys);
            topArrys = response.getBody().getAsJsonArray("topList");
            newsArrys = response.getBody().getAsJsonArray("newsList");

            Type types = new TypeToken<List<SearchResultItem>>(){}.getType();
            if (null != questions) {
                items = mGson.fromJson(questions,types);
            } if(focusArrys!=null){
                focusList = mGson.fromJson(focusArrys,types);
            }if( topArrys!=null){
                topList =mGson.fromJson(topArrys,types);
            }if(newsArrys!=null){
                newsList = mGson.fromJson(newsArrys,types);
            }else {
                boZhu = new Gson().fromJson(response.getBody().getAsJsonObject("bozhu"), new TypeToken<BoZhu>() {
                }.getType());
                newWeiBo = new Gson().fromJson(response.getBody().getAsJsonObject("newWeibo"), new TypeToken<Weibo>() {
                }.getType());
                hotTopics = new Gson().fromJson(response.getBody().getAsJsonArray("hotTopic"), new TypeToken<List<HotTopic>>() {
                }.getType());
                weibo = new Gson().fromJson(response.getBody().getAsJsonArray("weibo"), new TypeToken<List<Weibo>>() {
                }.getType());
            }
        }

        JsonElement ad = response.getHeadElement("ad");
        if (ad != null) {
            this.ad = new Gson().fromJson(ad, Ad.class);
        }
        List<String> urls = new ArrayList<String>();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                SearchResultItem item = items.get(i);
                if (item.url() != null) {
                    urls.add(item.url());
                }
            }
        }
        ReadHistoryHelper inst = ReadHistoryHelper.getInstance();
        Set<String> md5s = inst.select(urls);
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                SearchResultItem item = items.get(i);
                if (item.url() != null && md5s.contains(Md5Util.getMD5Str(item.url()))) {
                    item.hasRead_$eq(true);
                }
                item.setKeywordCate(keywordCate);
            }

        }
        init(items);
        init(topList);
        init(newsList);
    }

    private boolean eq(String eq, String eq0) {
        if (eq != null && eq0 != null)
            return eq.equals(eq0);
        return false;
    }

    public void init(List<SearchResultItem> items) {
        if (items != null) {
            for (int index = 0; index < items.size(); index++) {
                SearchResultItem item = items.get(index);
                item.start_$eq(this.start);
//                item.category_$eq(category);
                item.isOriginal_$eq((eq(item.source(), "搜悦原创") || item.isOriginal() == 1) ? 1 : 0);
                if (item.isOriginal() == 1) item.source_$eq("搜悦原创");
                // 类型为新闻搜索，且非焦点图，需要将地址替换为又拍云地址
                if (item.image() != null && !(item.isFocus() || item.focus())) {
                    for (int j = 0; j < item.image().size(); j++) {
                        String imgUrl = item.image().get(j);
                        if (imgUrl.startsWith("http://souyue-image.b0.upaiyun.com/") || imgUrl.startsWith("http://sns-img.b0.upaiyun.com/")) {
                            imgUrl = imgUrl.replaceAll("!.+$", "");
                            item.image().set(j, imgUrl + (category != null && category.equals("RSS") ? "_android.jpg" : "") + "!android");
                        } else if (category.equals("新闻搜索")) {
                            item.image().set(j, replaceImageWithUpyun(imgUrl));
                        }
                    }
                }
                boolean headlineTop = item.isHeadlineTop();//头条

                if (!TextUtils.isEmpty(item.srpid)) {
                    item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_SPECIAL_TOPIC);
                }

                if (item.image() != null && item.image().size() != 0) {

                    if (item.isFocus() || item.focus())
                        item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_FOCUS);
                    else if (item.image().size() >= 3)
                        item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_IMAGE_THREE);
                   /* else if (item.image().size() == 2)
                        item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_IMAGE_TWO);*/
                    else if (item.image().size() == 2)
                        item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_IMAGE_TWO);
                    else
                        item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_IMAGE_ONE);
                    if (headlineTop) {
                        item.newsLayoutType_$eq(SearchResult.NEWS_TYPE_HEADLINETOP);
                    }


                    if (item.isFocus() || item.focus()) {
                        item.image().set(0, item.bigImgUrl());
                    }

                }
            }
        }
    }

    /**
     * 替换中搜图片地址为又拍云地址
     */
    public String replaceImageWithUpyun(String urlSource) {
        // 又拍云格式
        String picUpyun = "http://souyue-image.b0.upaiyun.com/newspic/list/$2/$3/$1_android.jpg!android";
        // 原图格式
        String picZhongsou = "http://pic.+zhongsou\\.com.+(w{3}(\\w{2})(\\w{2})\\w{12}).*";
        return urlSource.replaceAll(picZhongsou, picUpyun);
    }


    public String title() {
        return title;
    }


    public void title(String title) {
        this.title = title;
    }

    public List<SearchResultItem> getFocusList() {
        return focusList;
    }

    public void setFocusList(List<SearchResultItem> focusList) {
        this.focusList = focusList;
    }

    public List<SearchResultItem> getTopList() {
        return topList;
    }

    public void setTopList(List<SearchResultItem> topList) {
        this.topList = topList;
    }

    public List<SearchResultItem> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<SearchResultItem> newsList) {
        this.newsList = newsList;
    }

    public int getKeywordCate() {
        return keywordCate;
    }

    public void setKeywordCate(int keywordCate) {
        this.keywordCate = keywordCate;
    }

    public String category() {
        return category;
    }


    public void category_$eq(String category) {
        this.category = category;
    }


    public boolean isXiaoqi() {
        return isXiaoqi;
    }


    public void setXiaoqi_$eq(boolean isXiaoqi) {
        this.isXiaoqi = isXiaoqi;
    }


    public String srpId() {
        return srpId;
    }


    public void srpId_$eq(String srpId) {
        this.srpId = srpId;
    }


    public boolean hasExpired() {
        return hasExpired;
    }


    public void hasExpired_$eq(boolean hasExpired) {
        this.hasExpired = hasExpired;
    }


    public BoZhu boZhu() {
        return boZhu;
    }


    public void boZhu_$eq(BoZhu boZhu) {
        this.boZhu = boZhu;
    }


    public Weibo newWeiBo() {
        return newWeiBo;
    }


    public void newWeiBo_$eq(Weibo newWeiBo) {
        this.newWeiBo = newWeiBo;
    }


    public List<HotTopic> hotTopics() {
        return hotTopics;
    }


    public void hotTopics_$eq(List<HotTopic> hotTopics) {
        this.hotTopics = hotTopics;
    }


    public List<Weibo> weibo() {
        return weibo;
    }


    public void weibo_$eq(List<Weibo> weibo) {
        this.weibo = weibo;
    }


    public String keyword() {
        return keyword;
    }


    public void keyword_$eq(String keyword) {
        this.keyword = keyword;
    }


    public String kid() {
        return kid;
    }


    public void kid_$eq(String kid) {
        this.kid = kid;
    }


    public int version() {
        return version;
    }


    public void version_$eq(int version) {
        this.version = version;
    }


    public boolean hideSubsrcibe() {
        return hideSubsrcibe;
    }


    public void hideSubsrcibe_$eq(boolean hideSubsrcibe) {
        this.hideSubsrcibe = hideSubsrcibe;
    }


    public List<NavigationBar> nav() {
        return nav;
    }


    public void nav_$eq(List<NavigationBar> nav) {
        this.nav = nav;
    }


    public List<SearchResultItem> items() {
        return items;
    }


    public void items_$eq(List<SearchResultItem> items) {
        this.items = items;
    }


    public JsonArray getQuestions() {
        return questions;
    }


    public void questions_$eq(JsonArray questions) {
        this.questions = questions;
    }


    public String md5() {
        return md5;
    }


    public void md5_$eq(String md5) {
        this.md5 = md5;
    }


    public Ad ad() {
        return ad;
    }


    public void ad_$eq(Ad ad) {
        this.ad = ad;
    }


    public List<Ad> adList() {
        return adList;
    }


    public void adList_$eq(List<Ad> adList) {
        this.adList = adList;
    }


    public boolean hasMore() {
        return hasMore;
    }


    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public boolean isShowMenu() {
        return showMenu;
    }

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }
}
