package com.zhongsou.souyue.module.listmodule;

import java.util.List;

/**
 * 轮播图样式
 * Created by lvqiang on 15/12/23.
 */
public class CrouselItemBean extends BaseListData {
    private List<BaseListData> mFocus;//轮播图全都是焦点类型的page

    public List<BaseListData> getFocus() {
        return mFocus;
    }

    public void setFocus(List<BaseListData> mFocus) {
        this.mFocus = mFocus;
    }
}
