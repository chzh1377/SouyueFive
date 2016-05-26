package com.zhongsou.souyue.module.listmodule;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.List;

/**
 * 基本列表数据基类
 * Created by lvqiang on 15/12/23.
 */
public class BaseListData implements DontObfuscateInterface{
    /** 默认类型，只有标题的新闻 */
    public static final int view_Type_default = 1;
    /** 大图 */
    public static final int view_Type_img_b = 10;
    /** 1张图 */
    public static final int view_Type_img_1 = 11;
    /** 3张图 */
    public static final int view_Type_img_3 = 13;
    /** 焦点图 */
    public static final int view_Type_img_f = 19;
    /** 轮播图 最低版本5.1 */
    public static final int VIEW_TYPE_IMG_CAROUSEL = 20;
    /** 大视频 */
    public static final int view_Type_video_0 = 80;
    /** 视频 小图 */
    public static final int view_Type_video_one_Img = 81;
    /** 专题 */
    public static final int view_Type_SPECIA = 40;
    /** 无图段子 */
    public static final int view_Type_JOKE = 50;
    /** 1张图段子 */
    public static final int view_Type_JOKE_img_1 = 51;
    /** 点击刷新类型 */
    public static final int view_Type_CLICK_REFRESH = 61;
    /** 搜索视图 视图类型 */
    public static final int view_Type_SEARCH_RESULT = 62;
    /** 搜索视图 视图类型 */
    public static final int view_Type_NULL = 63;

    /**
     * invokeType : 10
     * viewType : 13
     * id : 1450836250521
     * title : 妈妈在饭店拿碗给儿子尿尿,随后发生的事让所有人都震惊了
     * category : 新闻搜索
     * desc :
     * bigImgUrl :
     * url : http://toutiao.com/group/6231201012784201985/
     * image : ["http://souyue-image.b0.upaiyun.com/newspic/list/f6/c0/420f6c04df8edb3f41c_android.jpg!android","http://souyue-image.b0.upaiyun.com/newspic/list/c1/a0/420c1a01faadc814ae5_android.jpg!android","http://souyue-image.b0.upaiyun.com/newspic/list/36/5e/420365e360bf06d4be4_android.jpg!android"]
     * interestName : 0
     * srpId : b525ed404d619b891851985d6ae3a33a
     * keyword : 社会
     * footView : {"footType":3,"source":"育儿一路","channelName":"0","channelInvokeType":21,"ctime":1450836179680,"showMenu":1,"upCount":-1,"downCount":-1,"commentCount":-1,"disLike":[{"tag":"重复内容","log":"1"},{"tag":"质量差","log":"2"},{"tag":"广告","log":"4"},{"tag":"分类:社会新闻","log":"5_3"},{"tag":"社会","log":"7_b525ed404d619b891851985d6ae3a33a"}]}
     */

    private int invokeType;
    private int viewType;
    private long id;
    private boolean localTop;//标识本地是否是置顶数据
    private boolean hasRead;//标识本地是否已读
    private String title;
    private String category;
    private String desc;
    private String jsonResource;//json


    private FootItemBean footView;
    private BaseInvoke invoke;
    private List<String> image;

    public void setInvokeType(int invokeType) {
        this.invokeType = invokeType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLocalTop(boolean localTop) {
        this.localTop = localTop;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setJsonResource(String jsonResource) {
        this.jsonResource = jsonResource;
    }

    public void setFootView(FootItemBean footView) {
        this.footView = footView;
    }

    public void setInvoke(BaseInvoke invoke) {
        this.invoke = invoke;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public int getInvokeType() {
        return invokeType;
    }

    public int getViewType() {
        return viewType;
    }

    public long getId() {
        return id;
    }

    public boolean isLocalTop() {
        return localTop;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }

    public String getJsonResource() {
        return jsonResource;
    }

    public FootItemBean getFootView() {
        return footView;
    }

    public BaseInvoke getInvoke() {
        return invoke;
    }

    public List<String> getImage() {
        return image;
    }

    @Override
    public String toString() {
        return super.toString()+ "\n viewType:"+viewType+"\n invokeType:"+invokeType
                +"\n title:"+title+"\n desc:"+desc;
    }
}
