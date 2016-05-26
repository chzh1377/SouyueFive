package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.view.BorderTextView;

/**
 * @description: 底部渲染器 类型1  ----注： 数据类型1 中没有任何点击事件
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class BottomViewRender1 extends BottomViewRender implements View.OnClickListener{



    private TextView mSourcesTv;
    private ImageView mShowMenuIv;
    private TextView mTimeTv;
    private LinearLayout mRightLinear;
    private ImageView mGreatIv;
    private TextView mGreatNumsTv;
    private ImageView mCommentIv;
    private TextView mCommentNumsTv;
    private BorderTextView mTagTv;

    public BottomViewRender1(Context context, int bottomType, BaseListViewAdapter adapter) {
        super(context, bottomType, adapter);
    }


    @Override
    public View getConvertView() {
        mBottomView = View.inflate(mContext, R.layout.listitem_bottom_1,null);
        mShowMenuIv =findView(mBottomView, R.id.home_wgd_ib_add);
        mSourcesTv = findView(mBottomView, R.id.home_wgd_tv_news);
        mTimeTv = findView(mBottomView, R.id.home_wgd_tv_time);
        mRightLinear = findView(mBottomView, R.id.home_wgd_tx_congraduation);
        mGreatIv = findView(mBottomView, R.id.imageView2);
        mCommentIv = findView(mBottomView, R.id.imageView3);
        mGreatNumsTv = findView(mBottomView, R.id.home_wgd_tv_great);
        mCommentNumsTv = findView(mBottomView, R.id.home_wgd_tv_comment);
        mTagTv = findView(mBottomView, R.id.tv_home_bottom_spacial);
        mShowMenuIv.setVisibility(View.GONE);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        if(mFootItemBean==null)
        {
            return ;
        }
        ListUtils.setViewString(mSourcesTv,mFootItemBean.getSource());
        ListUtils.setViewTime(mTimeTv,mFootItemBean.getCtime());
        ListUtils.setViewStatusForGone(mCommentIv,mFootItemBean.getCommentCount());
        ListUtils.setViewStatusForGone(mGreatIv,mFootItemBean.getUpCount());
        ListUtils.setViewStatusForText(mCommentNumsTv,mFootItemBean.getCommentCount());
        ListUtils.setViewStatusForText(mGreatNumsTv,mFootItemBean.getUpCount());
        mTagTv.setHotViews(mFootItemBean.getTag());
        mShowMenuIv.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

    }
}
