package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.listmodule.SearchItemData;
import com.zhongsou.souyue.view.BorderTextView;

import java.util.List;

/**
 * @description: 5.2 新增的 搜索结果 视图类型
 * @auther: qubian
 * @data: 2016/4/14.
 */
public class SearchResultRender extends ListTypeRender {

    private ZSImageView image;
    private TextView discriptionTv;
    private ImageView addIv;
    private BorderTextView tagTv;
    private SearchItemData bean;

    public SearchResultRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext,
                R.layout.listitem_searchresult, null);
        image = (ZSImageView) mConvertView.findViewById(R.id.image);
        discriptionTv = findView(mConvertView, R.id.discription);
        addIv = findView(mConvertView, R.id.iv_add);
        tagTv = findView(mConvertView, R.id.tag);
        addIv.setOnClickListener(this);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        bean = (SearchItemData) mAdaper.getItem(position);
        if ("interest".equals(bean.getCategory())) {
            tagTv.setText(R.string.manager_grid_insterest);
            tagTv.setVisibility(View.VISIBLE);
        } else if("special".equals(bean.getCategory())) {
            tagTv.setText(R.string.home_spacial);
            tagTv.setVisibility(View.VISIBLE);
        }else{
            tagTv.setVisibility(View.GONE);
        }

        if(bean.getIsSubscribe() == 0){
            addIv.setImageDrawable(MainApplication.getInstance().getResources().getDrawable(R.drawable.subscribe_add01));
        }else if(bean.getIsSubscribe() == 1){
            addIv.setImageDrawable(MainApplication.getInstance().getResources().getDrawable(R.drawable.subscribe_cancel01));
        }
        if(bean.getIsPrivate() == 1){   // 0: 非私密圈;  1: 私密圈
            addIv.setImageDrawable(MainApplication.getInstance().getResources().getDrawable(R.drawable.search_lock));
        }
        List<String> images = bean.getImage();
        if (images != null && images.size() > 0) {
            showImage(image, images.get(0), R.drawable.default_small, null);
            image.setTag(images);
        }
        mTitleTv.setText(ListUtils.calcTitle(mContext, bean.getTitleIcon(), getTitle(bean)));
        discriptionTv.setText(bean.getDesc());
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == addIv.getId()) {   //点击加号
            if (mListManager instanceof HomeListManager) {
                ((HomeListManager) mListManager).clickSubscribe(v, bean);
            }
        }
    }
}
