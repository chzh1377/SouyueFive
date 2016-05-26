package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import com.zhongsou.souyue.R;

/**
 * @description: 刷新的类型
 * @auther: qubian
 * @data: 2015/12/30.
 */

public class FreshRender extends ListTypeRender {

    public FreshRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext, R.layout.listitem_fresh,null);
        return super.getConvertView();
    }

}
