package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;

/**
 * @description: 专门 用于 不同的 render 继承的
 * @auther: qubian
 * @data: 2016/5/6.
 */
public abstract class VideoRender extends  ListTypeRender{
    
    public VideoRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    public abstract int getCurrentPosition();

    public abstract void stopPlay();

    public abstract void startPlay();

    public abstract void pausePlay();

    public abstract void releasePlay();

    public abstract boolean isPlaying();
}
