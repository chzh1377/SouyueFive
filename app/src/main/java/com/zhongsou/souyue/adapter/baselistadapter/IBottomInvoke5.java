package com.zhongsou.souyue.adapter.baselistadapter;

import android.view.View;
import com.zhongsou.souyue.module.listmodule.BaseListData;

/**
 * @description:  删除
 * @auther: qubian
 * @data: 2016/1/22.
 */
public interface IBottomInvoke5 {

    /**
     * 删除
     * @param v
     * @param pos
     * @param data
     */
    void clickDeleteItem(View v, int pos, BaseListData data);
}
