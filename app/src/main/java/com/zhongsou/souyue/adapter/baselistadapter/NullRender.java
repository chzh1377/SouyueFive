package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.DefaultItemBean;
import com.zhongsou.souyue.view.HotConfigView;

/**
 * @description:  默认的视图渲染器，即：只有一个标题的视图
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class NullRender extends ListTypeRender {

    public NullRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext,R.layout.discover_group_item,null);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
    }
}
