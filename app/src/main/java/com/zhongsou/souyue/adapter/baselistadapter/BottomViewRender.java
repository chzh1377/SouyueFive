package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * @description: 公共底部 渲染器
 * @auther: qubian
 * @data: 2015/12/22.
 */

public abstract class BottomViewRender implements BaseBottomViewRender {

    public static final int BottomViewDEFAULT= 0;
    public static final int BottomViewTYPE1= 1;
    public static final int BottomViewTYPE2= 2;
    public static final int BottomViewTYPE3= 3;
    public static final int BottomViewTYPE4= 4;
    public static final int BottomViewTYPE5= 5;
    public static final int VIEWFOTINVISIBLE = -1;
    protected View mBottomView;
    protected Context mContext;
    protected int mBottomType;
    protected BaseListViewAdapter mAdaper;
    protected BaseListData mBaseListData;
    protected FootItemBean mFootItemBean;
    protected BaseListManager mListManager;
    public BottomViewRender(Context context,int bottomType,BaseListViewAdapter adapter)
    {
        mContext =context;
        mBottomType =bottomType;
        mAdaper =adapter;
    }
    @Override
    public View getConvertView() {
        return mBottomView;
    }

    @Override
    public void fitEvents() {

    }

    @Override
    public void setListManager(BaseListManager manager) {
        mListManager = manager;
    }

    @Override
    public void fitDatas(int position) {
        mBaseListData =((BaseListData)mAdaper.getItem(position));
        mFootItemBean =mBaseListData.getFootView();

    }

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public void setHasRead(){

    }

}
