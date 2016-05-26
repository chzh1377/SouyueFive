package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;

/**
 * @description: 新闻布局类型，焦点
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class FocusRender extends ListTypeRender {

    private ZSImageView image;

    public FocusRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }


    @Override
    public View getConvertView() {
        mConvertView =View.inflate(mContext,
                R.layout.listitem_focus, null);
        image = (ZSImageView) mConvertView.findViewById(R.id.image);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        mTitleTv.setTextColor(Color.WHITE);
//        BaseListData d = (BaseListData) mAdaper.getItem(position);
//        if (!(d instanceof SigleBigImgBean)){
//            Slog.e(this.getClass().getName(),"数据出错,"+position);
//            return;
//        }
        SigleBigImgBean data = (SigleBigImgBean) mAdaper.getItem(position);
        showImage(image,data.getBigImgUrl(),R.drawable.default_big,null);
        ListUtils.setViewString(mTitleTv,getTitle(data));
    }
}
