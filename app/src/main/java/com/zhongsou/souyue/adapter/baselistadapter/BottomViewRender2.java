package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.Utils;

/**
 * @description: 底部渲染器 类型2 -----带顶 和 跟帖的样式
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class BottomViewRender2 extends BottomViewRender implements View.OnClickListener {

    private TextView mTimeTv;
    private TextView mSourcesTv;
    private TextView mAuthorTv;
    public TextView mUpCountTv;
    public TextView mCommentTv;
    public TextView mAddOneTv;
    public BottomViewRender2(Context context, int bottomType, BaseListViewAdapter adapter) {
        super(context, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mBottomView = View.inflate(mContext, R.layout.listitem_bottom_2,null);
        mTimeTv = findView(mBottomView ,R.id.tv_cricle_create_time);
        mAuthorTv = findView(mBottomView ,R.id.tv_cricle_nickname);
        mSourcesTv = findView(mBottomView ,R.id.tv_cricle_source);
        mUpCountTv = findView(mBottomView ,R.id.tv_cricle_good);
        mCommentTv = findView(mBottomView ,R.id.tv_cricle_follow);
        mAddOneTv = findView(mBottomView, R.id.tv_add_one);
        return super.getConvertView();
    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        mUpCountTv.setOnClickListener(this);
        mCommentTv.setOnClickListener(this);
    }

    @Override
    public void fitDatas(int  position) {
        super.fitDatas(position);
        if(mFootItemBean==null)
        {
            return ;
        }
        ListUtils.setViewString(mSourcesTv,mFootItemBean.getSource());
        ListUtils.setViewTime(mTimeTv,mFootItemBean.getCtime());
        ListUtils.setViewString(mAuthorTv,mFootItemBean.getAuthor());
        ListUtils.setViewStatus(mUpCountTv,mFootItemBean.getUpCount(),mContext.getString(R.string.up_text));
        ListUtils. setViewStatus(mCommentTv,mFootItemBean.getCommentCount(),mContext.getString(R.string.comment_text));
        mUpCountTv.setTag(position);
        mCommentTv.setTag(position);
        setUp(mFootItemBean.getIsUp()== 1);
    }

    @Override
    public void onClick(View v) {
        if(mListManager==null) {
            return;
        }
        if(mListManager instanceof  IBottomInvoke2)
        {
            if(v.getId() ==mUpCountTv.getId())
            {
                ((IBottomInvoke2)mListManager).doCircleUp(this,v,mBaseListData);
            }else  if(v.getId() ==mCommentTv.getId())
            {
                ((IBottomInvoke2)mListManager).doCircleComment(mBaseListData);
            }
        }else
        {
            Utils.makeToastTest(mContext,"IBottomInvoke2 error can not convert");
        }


    }
    public void setUp(boolean IsFavorator)
    {
        Drawable drawable;
        int colorId;
        if(IsFavorator)
        {
            drawable= mContext.getResources().getDrawable(R.drawable.cricle_list_item_good_press_icon);
            colorId =R.color.text_red;
        }else
        {
            drawable= mContext.getResources().getDrawable(R.drawable.cricle_list_item_good_icon);
            colorId=R.color.cricle_source_date_color;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mUpCountTv.setCompoundDrawables(drawable,null,null,null);
        mUpCountTv.setTextColor(mContext.getResources().getColor(colorId));
    }
    public void upAnimation()
    {
        mAddOneTv.setVisibility(View.VISIBLE);
        mAddOneTv.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.addone));
        mAddOneTv.setVisibility(View.INVISIBLE);
    }

    public void doUpCallBack()
    {
        ListUtils.setViewStatus(mUpCountTv,mFootItemBean.getUpCount(),mContext.getString(R.string.up_text));
    }
}
