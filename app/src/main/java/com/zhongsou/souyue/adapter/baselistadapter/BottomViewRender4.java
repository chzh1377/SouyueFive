package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.Utils;

/**
 * @description: 底部渲染器 类型4  ------赞，踩 评论，收藏，分享 样式
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class BottomViewRender4 extends  BottomViewRender implements View.OnClickListener{


    public TextView upTv;
    public TextView downTv;
    public TextView commentTv;
    public ImageView likeTv;
    public ImageView shareTv;
    private View upView;
    private View downView;
    private View commentpView;
    public BottomViewRender4(Context context, int bottomType, BaseListViewAdapter adapter) {
        super(context, bottomType, adapter);
    }


    @Override
    public View getConvertView() {
        mBottomView = View.inflate(mContext, R.layout.listitem_bottom_4,null);
        upTv = findView(mBottomView ,R.id.tv_cricle_good);
        downTv = findView(mBottomView,R.id.tv_cricle_down);
        commentTv = findView(mBottomView ,R.id.tv_cricle_follow);
        likeTv = findView(mBottomView ,R.id.tv_cricle_like);
        shareTv = findView(mBottomView ,R.id.tv_cricle_share);
        upView = findView(mBottomView ,R.id.lay1);
        downView = findView(mBottomView ,R.id.lay2);
        commentpView = findView(mBottomView ,R.id.lay3);

        return super.getConvertView();
    }

    @Override
    public void fitDatas(int object) {
        super.fitDatas(object);
        if(mFootItemBean==null)
        {
            return ;
        }
        ListUtils.setViewStatus(upTv,mFootItemBean.getUpCount(),mContext.getString(R.string.up_text));
        ListUtils.setViewStatus(downTv,mFootItemBean.getDownCount(),mContext.getString(R.string.down_text));
        ListUtils.setViewStatus(commentTv,mFootItemBean.getCommentCount(),mContext.getString(R.string.comment_text));
        ListUtils.setViewForGone(upView,mFootItemBean.getUpCount());
        ListUtils.setViewForGone(downView,mFootItemBean.getDownCount());
        ListUtils.setViewForGone(commentpView,mFootItemBean.getCommentCount());
        upTv.setTag(object);
        downTv.setTag(object);
        commentTv.setTag(object);
        likeTv.setTag(object);
        shareTv.setTag(object);
        setUp(mFootItemBean.getIsUp()== 1);
        setDown(mFootItemBean.getIsDown()== 1);
        setFavorite(mFootItemBean.getIsFavorator() ==1);

    }
    public void setFavorite(boolean IsFavorator)
    {
        Drawable drawable;
        int colorId;
        if(IsFavorator)
        {
            drawable= mContext.getResources().getDrawable(R.drawable.btn_fav_pre);
            colorId =R.color.text_red;
        }else
        {
            drawable= mContext.getResources().getDrawable(R.drawable.btn_fav_nor);

            colorId=R.color.cricle_source_date_color;
        }
//        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//        likeTv.setCompoundDrawables(drawable,null,null,null);
//        likeTv.set(mContext.getResources().getColor(colorId));
        likeTv.setImageDrawable(drawable);
    }

    public void setUp(boolean IsFavorator)
    {
        Drawable drawable;
        int colorId;
        if(IsFavorator)
        {
            drawable= mContext.getResources().getDrawable(R.drawable.btn_up_pre);
            colorId =R.color.text_red;
        }else
        {
            drawable= mContext.getResources().getDrawable(R.drawable.btn_up_nor);
            colorId=R.color.cricle_source_date_color;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        upTv.setCompoundDrawables(drawable,null,null,null);
        upTv.setTextColor(mContext.getResources().getColor(colorId));
    }
    public void setDown(boolean IsFavorator)
    {
        Drawable drawable;
        int colorId;
        if(IsFavorator)
        {
            drawable= mContext.getResources().getDrawable(R.drawable.btn_down_pre);
            colorId =R.color.text_red;
        }else
        {
            drawable= mContext.getResources().getDrawable(R.drawable.btn_down_nor);
            colorId=R.color.cricle_source_date_color;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        downTv.setCompoundDrawables(drawable,null,null,null);
        downTv.setTextColor(mContext.getResources().getColor(colorId));
    }

    @Override
    public void fitEvents() {
        super.fitEvents();
//        upTv.setOnClickListener(this);
//        downTv.setOnClickListener(this);
//        commentTv.setOnClickListener(this);
        findView(mBottomView,R.id.lay1).setOnClickListener(this);
        findView(mBottomView,R.id.lay2).setOnClickListener(this);
        findView(mBottomView,R.id.lay3).setOnClickListener(this);
        likeTv.setOnClickListener(this);
        shareTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(mListManager==null) {
            return;
        }

        if(!(mListManager instanceof  IBottomInvoke4))
        {
            Utils.makeToastTest(mContext,"IBottomInvoke4 error can not convert");
            return;
        }
        IBottomInvoke4 iBottomInvoke4 = (IBottomInvoke4) mListManager;
        if(v.getId() ==R.id.lay1 )
        {
            if(mFootItemBean.getIsUp()==1)
            {
                SouYueToast.makeText(mContext, R.string.detail_have_ding, Toast.LENGTH_SHORT).show();
                return;
            }
            iBottomInvoke4.doUp(this,v,mBaseListData);
        }else if(v.getId() ==R.id.lay2 )
        {
            if(mFootItemBean.getIsDown()==1)
            {
                SouYueToast.makeText(mContext, R.string.detail_have_cai, Toast.LENGTH_SHORT).show();
                return;
            }
            iBottomInvoke4.doDown(this,mBaseListData);
        }else if(v.getId() ==R.id.lay3 )
        {
//            mListManager.doCircleComment(v,(Integer) v.getTag(),mBaseListData,mBottomType);
            iBottomInvoke4.doComment(mBaseListData);
        }else if(v.getId() ==likeTv.getId() )
        {
            iBottomInvoke4.doFavorite(this,mBaseListData);
        }else if(v.getId() ==shareTv.getId() )
        {
            iBottomInvoke4.doShare(mBaseListData);
        }

    }
    public void doUpCallBack()
    {
        ListUtils.setViewStatus(upTv,mFootItemBean.getUpCount(),mContext.getString(R.string.up_text));
    }

    public void doDownCallBack()
    {
        ListUtils.setViewStatus(downTv,mFootItemBean.getDownCount(),mContext.getString(R.string.down_text));
    }
    public void doCommentCallBack()
    {
        ListUtils.setViewStatus(commentTv,mFootItemBean.getCommentCount(),mContext.getString(R.string.comment_text));
    }
}
