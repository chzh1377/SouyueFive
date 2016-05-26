package com.zhongsou.souyue.module;

import com.zhongsou.souyue.circle.model.VideoAboutResult;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;

import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/3/23.
 */
public class VideoDetailItem extends ResponseObject implements Serializable {
    private long id;
    private String netUrl;
    private String videoUrl;
    private String imageUrl;
    private String title;
    private int palyPosition;
    private String keyword;
    private String srpId;
    private long mBlogId;
    private long mInterestId;
    private FootItemBeanSer footView;
    private int skip;//跳转标识

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public FootItemBeanSer getFootView() {
        return footView;
    }

    public void setFootView(FootItemBeanSer footView) {
        this.footView = footView;
    }

    public long getmInterestId() {
        return mInterestId;
    }

    public void setmInterestId(long mInterestId) {
        this.mInterestId = mInterestId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPalyPosition() {
        return palyPosition;
    }

    public void setPalyPosition(int palyPosition) {
        this.palyPosition = palyPosition;
    }

    public long getmBlogId() {
        return mBlogId;
    }

    public void setmBlogId(long mBlogId) {
        this.mBlogId = mBlogId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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
    public static VideoDetailItem InVokeToVideoDetailItem(BaseInvoke invoke)
    {
        VideoDetailItem item = new VideoDetailItem();
        item.setId(invoke.getId());
        item.setTitle(invoke.getTitle());
        item.setKeyword(invoke.getKeyword());
        item.setmBlogId(invoke.getBlogId());
        item.setSrpId(invoke.getSrpId());
        BaseListData data =invoke.getData();
        if(data instanceof SigleBigImgBean)
        {
            item.setVideoUrl(((SigleBigImgBean) data).getPhoneImageUrl());
        }
        item.setImageUrl(invoke.getBigImgUrl());
        item.setmInterestId(invoke.getInterestId());
        item.setFootView(setFoot(invoke.getData().getFootView()));
        item.setSkip(invoke.getFlag(BaseInvoke.FLAG_SKIP_COMMENT)?1:0);
        item.setNetUrl(invoke.getUrl());
        return item;
    }
    public static FootItemBeanSer setFoot(FootItemBean bean)
    {
        FootItemBeanSer ser = new FootItemBeanSer();
        ser.setIsUp(bean.getIsUp());
        ser.setCommentCount(bean.getCommentCount());
        ser.setDeleteId(bean.getDeleteId());
        ser.setDownCount(bean.getDownCount());
        ser.setIsDown(bean.getIsDown());
        ser.setIsFavorator(bean.getIsFavorator());
        ser.setShareUrl(bean.getShareUrl());
        ser.setUpCount(bean.getUpCount());
        return ser;
    }

    public static VideoDetailItem ResultToVideoDetailItem(VideoAboutResult invoke) {

        VideoDetailItem item = new VideoDetailItem();
        item.setId(invoke.getId());
        item.setTitle(invoke.getTitle());
        item.setKeyword(invoke.getKeyword());
        item.setSrpId(invoke.getSrpId());
        item.setVideoUrl(invoke.getPhoneImageUrl());
        item.setNetUrl(invoke.getUrl());
        item.setFootView(setFoot(invoke.getFootView()));
        List<String> images = invoke.getImage();
        if(images!=null&&images.size()>0)
        {
            item.setImageUrl(images.get(0));
        }
        return item;
    }
    public static VideoDetailItem SearchResultItemToVideoDetailItem(SearchResultItem searchResultItem)
    {
        VideoDetailItem item = new VideoDetailItem();
        item.setId(Long.parseLong(searchResultItem.id()));
        item.setTitle(searchResultItem.title());
        item.setKeyword(searchResultItem.keyword());
        item.setSrpId(searchResultItem.srpId());
        item.setVideoUrl(searchResultItem.bigImgUrl());
        item.setNetUrl(searchResultItem.url());
//        item.setFootView(setFoot(invoke.getFootView()));
        List<String> images = searchResultItem.image();
        if(images!=null&&images.size()>0)
        {
            item.setImageUrl(images.get(0));
        }
        return item;
    }
}
