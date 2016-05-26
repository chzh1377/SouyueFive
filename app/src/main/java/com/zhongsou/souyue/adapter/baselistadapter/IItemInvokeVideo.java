package com.zhongsou.souyue.adapter.baselistadapter;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/3/21.
 */
public interface IItemInvokeVideo {

    void setPlayPosition(int playPosition) ;

    int getPlayPosition();

    void setIsPalying(boolean isPalying);

    boolean getIsPalying();

    void setPlayRender(VideoRender render);

    void stopPlay(int position);

}
