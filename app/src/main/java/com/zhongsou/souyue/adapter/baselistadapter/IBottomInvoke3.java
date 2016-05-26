package com.zhongsou.souyue.adapter.baselistadapter;

import android.view.View;
import com.zhongsou.souyue.module.listmodule.BaseListData;

/**
 * @description:  来源的点击 与  不感兴趣
 * @auther: qubian
 * @data: 2016/1/22.
 */

public interface IBottomInvoke3 {

    /**
     * 点击底部右侧来源 跳转相关srp页
     * @param data
     */
    void clickSource(BaseListData data);
    /**
     * 点击弹出不感兴趣框
     * @param v
     * @param pos
     * @param data
     */
    void clickUnLike(View v, int pos, BaseListData data);
}
