package com.zhongsou.souyue.adapter.baselistadapter;

import android.view.View;

/**
 * @description: 底部视图 渲染器接口
 * @auther: qubian
 * @data: 2015/12/22.
 */

public interface BaseBottomViewRender  {
    /**
     * 返回一个item的convertView，也就是BaseAdapter中getView方法中返回的convertView
     *
     * @return
     */
    View getConvertView();

    /**
     * 填充item中各个控件的事件，比如按钮点击事件等
     */
    void fitEvents();

    /**
     * 对指定position的item进行数据的适配
     *
     * @param position
     */
    void fitDatas(int position);


    void setListManager(BaseListManager manager);
}
