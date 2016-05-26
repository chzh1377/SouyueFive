package com.zhongsou.souyue.adapter.baselistadapter;

/**
 * @description:  gif 播放的控制
 * @auther: qubian
 * @data: 2016/1/22.
 */

public interface IItemInvokeGif {
    /**
     * 播放所在的位置
     * @param bigImageRender
     * @param gifPlayPosition
     */
    void setBigImageRender(BigImageRender bigImageRender,int gifPlayPosition) ;

    /**
     * 停止的位置
     * @param position
     */
    void stopPlayingGif(int position);
}
