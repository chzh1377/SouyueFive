package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.BorderTextView;

/**
 * @description: 底部数据类型5   ---历史列表中的  删除样式
 * @auther: qubian
 * @data: 2016/1/22.
 */

public class BottomViewRender5 extends BottomViewRender implements View.OnClickListener{



    private TextView mSourcesTv;
    private ImageView mShowMenuIv;
    private TextView mTimeTv;
    private LinearLayout mRightLinear;
    private ImageView mGreatIv;
    private TextView mGreatNumsTv;
    private ImageView mCommentIv;
    private TextView mCommentNumsTv;
    private BorderTextView mTagTv;
    public BottomViewRender5(Context context, int bottomType, BaseListViewAdapter adapter) {
        super(context, bottomType, adapter);
    }


    @Override
    public View getConvertView() {
        mBottomView = View.inflate(mContext, R.layout.listitem_bottom_5,null);
        mShowMenuIv =findView(mBottomView, R.id.home_wgd_ib_add);
        mSourcesTv = findView(mBottomView, R.id.home_wgd_tv_news);
        mTimeTv = findView(mBottomView, R.id.home_wgd_tv_time);
        mRightLinear = findView(mBottomView, R.id.home_wgd_tx_congraduation);
        mGreatIv = findView(mBottomView, R.id.imageView2);
        mCommentIv = findView(mBottomView, R.id.imageView3);
        mGreatNumsTv = findView(mBottomView, R.id.home_wgd_tv_great);
        mCommentNumsTv = findView(mBottomView, R.id.home_wgd_tv_comment);
        mTagTv = findView(mBottomView, R.id.tv_home_bottom_spacial);
        mShowMenuIv.setVisibility(View.VISIBLE);
        mShowMenuIv.setOnClickListener(this);
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
        mShowMenuIv.setImageResource(R.drawable.home_selector_bubble);
        mShowMenuIv.setVisibility(View.VISIBLE);
        ListUtils.setViewStatusForGone(mShowMenuIv,mFootItemBean.getShowMenu());
        mShowMenuIv.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if(mListManager==null) {
            return;
        }
        if(v.getId()== mShowMenuIv.getId())
        {
            if(mListManager instanceof  IBottomInvoke5)
            {
                ((IBottomInvoke5)mListManager).clickDeleteItem(v,(Integer) mShowMenuIv.getTag(),mBaseListData);
            }else
            {
                Utils.makeToastTest(mContext,"IBottomInvoke5 error can not convert");
            }

        }
    }
}
