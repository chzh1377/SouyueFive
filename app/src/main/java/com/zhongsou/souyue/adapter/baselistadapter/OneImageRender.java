package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.module.listmodule.DefaultItemBean;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.view.HotConfigView;

import java.util.List;

/**
 * @description: 一张图的列表
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class OneImageRender extends ListTypeRender {

    private ZSImageView image;
    private int height;
    private int width;
    private int deviceWidth;
    private HotConfigView hotConfigView;
    private ImageView controllerIv;

    public OneImageRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
        height = (int) ((2 * width) / 3);
    }


    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext,
                R.layout.listitem_oneimage, null);
        image = (ZSImageView) mConvertView.findViewById(R.id.image);
        setViewRLayout(image, width, height);
        hotConfigView = findView(mConvertView, R.id.hotconfigView);
        controllerIv = findView(mConvertView, R.id.controller);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        DefaultItemBean bean = (DefaultItemBean) mAdaper.getItem(position);
        List<String> images = bean.getImage();
        if (images != null && images.size() > 0) {
            showImage(image, images.get(0), R.drawable.default_small, null);
            image.setTag(images.get(0));
        }
        hotConfigView.setData(bean.getTitleIcon());
        mTitleTv.setText(ListUtils.calcTitle(mContext, bean.getTitleIcon(), getTitle(bean)));

        controllerIv.setVisibility(View.GONE);
        if (bean.getViewType() == DefaultItemBean.view_Type_video_one_Img) {
            controllerIv.setVisibility(View.VISIBLE);
        }
    }

    private void setViewRLayout(View v, int width, int height) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v
                .getLayoutParams();
        params.width = width;
        params.height = height;
        v.setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == image.getId()) {
            IntentUtil.goGifPlay(mContext, image.getTag());
        }

    }
}
