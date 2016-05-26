package com.zhongsou.souyue.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.zhongsou.souyue.im.adapter.IMFeaturesAdapter;
import com.zhongsou.souyue.im.module.FeaturesBean;
import com.zhongsou.souyue.im.util.IMFeaturesHelper;

import java.util.ArrayList;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.0.1)
 * @Copyright (c) 2016 zhongsou
 * @Description class description
 * IM功能的gridview
 * @date 16/1/8
 */
public class IMFeaturesGridView extends GridView implements AdapterView.OnItemClickListener {

    private Context mContext;
    private ArrayList<FeaturesBean> mFeaturesList;
    private IMFeaturesAdapter mIMFeaturesAdapter;
    private int mChatType = 0;
    private IFeaturesClickListener mIFeaturesClickListener;

    public IMFeaturesGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        setOnItemClickListener(this);
        initData(mChatType);
    }

    /**
     * 初始化数据
     *
     * @param chatType
     */
    private void initData(int chatType) {
        mFeaturesList = IMFeaturesHelper.getFeaturesList(chatType);
        mIMFeaturesAdapter = new IMFeaturesAdapter(mContext, mFeaturesList);
        setAdapter(mIMFeaturesAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FeaturesBean featuresBean = mIMFeaturesAdapter.getItem(position);
        mIFeaturesClickListener.itemClick(featuresBean.getFeaturesType());
    }

    /**
     * 设置会话类型并且刷新adapter
     *
     * @param chatType
     */
    public void setChatType(int chatType) {
        this.mChatType = chatType;
        mFeaturesList = IMFeaturesHelper.getFeaturesList(chatType);
        mIMFeaturesAdapter.notifyDataSetChanged();
    }

    /**
     * 功能点击接口
     */
    public interface IFeaturesClickListener {
        /**
         * 条目点击事件
         * @param itemType
         */
        void itemClick(int itemType);
    }

    /**
     * 设置点击接口
     * @param featuresClickListener
     */
    public void setIFeaturesClickListener(IFeaturesClickListener featuresClickListener){
       this.mIFeaturesClickListener = featuresClickListener;
    }

}
