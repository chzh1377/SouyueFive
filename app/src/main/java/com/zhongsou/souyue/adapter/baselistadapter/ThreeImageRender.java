package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.LayoutInflater;
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
import com.zhongsou.souyue.view.HotConfigView;

/**
 * @description: 三张图 的列表
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class ThreeImageRender extends  ListTypeRender {
    private ZSImageView image1;
    private ZSImageView image2;
    private ZSImageView image3;
    private HotConfigView hotConfigView;
    private int height;
    private int width;
    private int deviceWidth;
    private int mMarginLeft;
    private int mMarginTop;
    public static final int MARGIN_LEFT=10;
    public static final int MARGIN_TOP=5;
    public ThreeImageRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
        mMarginLeft = DeviceUtil.dip2px(context, MARGIN_LEFT);
        mMarginTop = DeviceUtil.dip2px(context,MARGIN_TOP);
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = (deviceWidth - DeviceUtil.dip2px(context, 48)) / 3;
        height = (int) ((2 * width) / 3);
    }
    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext,
                R.layout.listitem_threeimage, null);
        image1 =findView(mConvertView,R.id.iv1);
        image2 =findView(mConvertView,R.id.iv2);
        image3 =findView(mConvertView,R.id.iv3);

        hotConfigView =findView(mConvertView,R.id.hotconfigView);
        setViewLayout(image1,width,height);
        setViewLayout(image2,width,height);
        setMargin(image2,mMarginLeft);
        setViewLayout(image3,width,height);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        DefaultItemBean bean = (DefaultItemBean) mAdaper.getItem(position);
        hotConfigView.setData(bean.getTitleIcon());
        mTitleTv.setText(ListUtils.calcTitle(mContext,bean.getTitleIcon(),getTitle(bean)));
        showImage(image1,(bean.getImage().size()>0 ? bean.getImage().get(0):""),R.drawable.default_small ,null);
        showImage(image2,(bean.getImage().size()>1 ? bean.getImage().get(1):""),R.drawable.default_small ,null);
        showImage(image3,(bean.getImage().size()>2 ? bean.getImage().get(2):""),R.drawable.default_small ,null);

    }

    private void setViewLayout(View v , int width, int height)
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v
                .getLayoutParams();
        params.width = width;
        params.height = height;
        v.setLayoutParams(params);
    }

    private void setMargin(View v ,int mMarginLeft)
    {
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) v
                .getLayoutParams();
        params2.setMargins(mMarginLeft, 0, mMarginLeft, 0);
        v.setLayoutParams(params2);
    }

    @Override
    public void onClick(View v) {


    }
}
