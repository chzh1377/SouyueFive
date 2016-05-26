package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;

import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.CrouselItemBean;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.BannerView;

/**
 * @description: 轮播图的样式
 * @auther: qubian
 * @data: 2015/12/25.
 */

public class BannerRender extends ListTypeRender implements BannerView.ImageCycleViewListener {

    private BannerView bannerView;

    public BannerRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mConvertView =new BannerView(mContext);
        bannerView = (BannerView) mConvertView;
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        CrouselItemBean bean = (CrouselItemBean) mAdaper.getItem(position);
        if(mListManager!=null)
        {
            if(mListManager instanceof  BottomInvoke4Manager)
            {
                bannerView.setChannel(((BottomInvoke4Manager) mListManager).getmChannel());
            }
        }
        bannerView.setData(bean.getFocus());
        bannerView.setOnImageCycleViewListener(this);
    }

    @Override
    public int onImageShow(int position) {
        return 0;
    }

    @Override
    public void onItemClick(BaseListData item, int position) {
        if(mListManager!=null)
        {
            if(mListManager instanceof  IItemInvokeBanner)
            {
                ((IItemInvokeBanner)mListManager).bannerClickItem(item);
            }else
            {
                Utils.makeToastTest(mContext,"IItemInvokeBanner error can not convert");
            }
        }

    }
}
