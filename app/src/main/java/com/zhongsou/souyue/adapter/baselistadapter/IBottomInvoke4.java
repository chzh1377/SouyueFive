package com.zhongsou.souyue.adapter.baselistadapter;

import android.view.View;
import com.zhongsou.souyue.module.listmodule.BaseListData;

/**
 * @description: 赞，踩 评论，收藏，分享 样式
 * @auther: qubian
 * @data: 2016/1/22.
 */
public interface IBottomInvoke4 {



    /**
     * 点赞
     * @param bottomViewRender
     * @param v
     * @param data
     */
    void doUp(BottomViewRender bottomViewRender, View v, BaseListData data );

    /**
     * c踩
     * @param bottomViewRender
     * @param data
     */
    void doDown(BottomViewRender bottomViewRender, final BaseListData data);

    /**
     * 跳到详情页，同时弹出评论框
     * @param data
     */
    void doComment(final BaseListData data);

    /**
     * 收藏
     * @param bottomViewRender
     * @param data
     */
    void doFavorite( BottomViewRender bottomViewRender,final BaseListData data);

    /**
     * 分享
     * @param data
     */
    void doShare(final BaseListData data );
}
