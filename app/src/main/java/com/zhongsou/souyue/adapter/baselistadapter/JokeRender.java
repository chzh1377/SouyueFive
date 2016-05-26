package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.listmodule.BaseListData;

/**
 * @description: 段子 样式
 * @auther: qubian
 * @data: 2015/12/23.
 */

public class JokeRender  extends ListTypeRender {
    private TextView descTv;
    private boolean mJokeHasRead;
    private float mJokeTextSize;
    public JokeRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext, R.layout.listitem_joke, null);
        descTv = findView(mConvertView, R.id.desc);
        return super.getConvertView();
    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        BaseListData data = (BaseListData) mAdaper.getItem(position);
        descTv.setText(data.getDesc());
        mTitleTv.setVisibility(View.GONE);
        sethasRead(descTv,data);
        setTextSize(descTv);
    }

    /**
     * 设置是否已读
     * @param textView
     * @param bean
     */
    private void sethasRead(TextView textView,BaseListData bean)
    {
        if (textView != null) {
            if (bean.isHasRead() != mJokeHasRead) {//先判定当前状态是否是已读状态，如果不是再做处理
                mJokeHasRead = bean.isHasRead();
                int color ;
                if (mJokeHasRead) {
                    color = mContext.getResources().getColor(R.color.list_has_read);
                }else {
                    color = mContext.getResources().getColor(R.color.list_has_no_read);
                }
                textView.setTextColor(color);
            }
        }
    }

    /**
     * 设置大小
     * @param textView
     */
    private void  setTextSize(TextView textView)
    {
        if (textView != null)
        {
            float size = mListManager.getFontSize();
            if (mJokeTextSize != size){
                mJokeTextSize = size;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mJokeTextSize);
            }
        }
    }

}
