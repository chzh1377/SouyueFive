package com.zhongsou.souyue.adapter.baselistadapter;

import android.view.View;
import com.zhongsou.souyue.module.listmodule.BaseListData;

/**
 * @description:  包含的 事件 ： 顶 和 跟帖
 * @auther: qubian
 * @data: 2016/1/22.
 */

public interface IBottomInvoke2 {

    /**
     * 置顶
     * @param bottomViewRender
     * @param v
     * @param data
     */
    void doCircleUp(BottomViewRender bottomViewRender, View v, BaseListData data );


    /**
     * 跟帖
     * @param data
     */
    void doCircleComment(final BaseListData data );
}
