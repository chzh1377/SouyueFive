package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.BorderTextView;

/**
 * @description: 底部渲染器 类型3  --- 不感兴趣的样式
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class BottomViewRender3 extends BottomViewRender implements View.OnClickListener{
    private TextView mSourcesTv;
    private ImageView mShowMenuIv;
    private TextView mTimeTv;
    private BorderTextView mTagTv;
    private TextView mChannelNameTv;
    public BottomViewRender3(Context context, int bottomType, BaseListViewAdapter adapter) {
        super(context, bottomType, adapter);
    }


    @Override
    public View getConvertView() {
        mBottomView = View.inflate(mContext, R.layout.listitem_bottom_3,null);
        mShowMenuIv =findView(mBottomView, R.id.home_wgd_ib_add);
        mSourcesTv = findView(mBottomView, R.id.home_wgd_tv_news);
        mTimeTv = findView(mBottomView, R.id.home_wgd_tv_time);
        mTagTv = findView(mBottomView, R.id.tv_home_bottom_spacial);
        mChannelNameTv = findView(mBottomView, R.id.home_wgd_tv_come);
        mChannelNameTv.setOnClickListener(this);
        mShowMenuIv.setOnClickListener(this);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int object) {
        super.fitDatas(object);
        if(mFootItemBean==null)
        {
            return ;
        }
        ListUtils.setViewString(mSourcesTv,mFootItemBean.getSource());
        ListUtils.setViewTime(mTimeTv,mFootItemBean.getCtime());
//        ListUtils.setViewString(mTagTv,mFootItemBean.getTag());
        mTagTv.setHotViews(mFootItemBean.getTag());
        ListUtils.setViewString(mChannelNameTv,mFootItemBean.getChannelName());
        ListUtils.setViewStatusForGone(mShowMenuIv,mFootItemBean.getShowMenu());
        mShowMenuIv.setTag(object);
    }


    @Override
    public void onClick(View v) {
        if(mListManager==null) {
            return;
        }
        if(mListManager instanceof  IBottomInvoke3)
        {
            if(v.getId() ==mChannelNameTv.getId())
            {
                ((IBottomInvoke3)mListManager).clickSource(mBaseListData);
            }else if(v.getId() ==mShowMenuIv.getId())
            {
                ((IBottomInvoke3)mListManager).clickUnLike(v,(Integer) mShowMenuIv.getTag(),mBaseListData);
            }
        }else
        {
            Utils.makeToastTest(mContext,"IBottomInvoke3 error can not convert");
        }


    }
}
