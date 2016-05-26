package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.listmodule.SpecialItemData;

/**
 * @description: 专题列表
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class SpecialRender extends ListTypeRender {

    ZSImageView imageIv;
    TextView dateTv;
    TextView tagTv;
    TextView descTv;

    public SpecialRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext, R.layout.listitem_special,null);
        dateTv = findView(mConvertView, R.id.date);
        descTv = findView(mConvertView, R.id.desc);
        imageIv = findView(mConvertView, R.id.image);
        tagTv = findView(mConvertView,R.id.tag_text);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        SpecialItemData bean = (SpecialItemData) mAdaper.getItem(position);
        showImage(imageIv,bean.getBigImgUrl(),R.drawable.default_big,null);
        ListUtils.setViewString(dateTv, bean.getHappenTime());
        ListUtils.setViewString(descTv,bean.getDesc());
        ListUtils.setViewString(tagTv,bean.getFocus());
        mTitleTv.setTextColor(Color.WHITE);
    }

    @Override
    public void fitEvents() {
        super.fitEvents();
    }
}
