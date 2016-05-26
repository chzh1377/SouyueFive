package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;

import com.zhongsou.souyue.module.listmodule.BaseListData;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 适配器
 * @auther: qubian
 * @data: 2015/12/22.
 */

public class ListViewAdapter extends BaseListViewAdapter {
    List mObjects;
    private Context mContext;

    public ListViewAdapter(Context context, List objects) {
        super(context, objects);
        if (objects == null) {
            objects = new ArrayList();
        }
        mObjects = objects;
        mContext = context;
    }

    @Override
    public BaseListTypeRender getAdapterTypeRender(int position) {
        return ListUtils.getItemTypeRender(
                mContext,
                getItemViewType(position),
                ListUtils.getBottomViewType(getItem(position)),
                this);
    }

    @Override
    public BaseBottomViewRender getBottomViewRender(int position) {
        return ListUtils.getBottomView(mContext, ListUtils.getBottomViewType(getItem(position)), this);
    }

    @Override
    public int getItemViewType(int position) {
        return ListUtils.getItemViewType(getItem(position));
    }

    @Override
    public int getViewTypeCount() {
        return ListUtils.getItemTypeCount();
    }

    @Override
    public int getCount() {
        if (mObjects == null) {
            return 0;
        }
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        if (mObjects == null) {
            return 0;
        }
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List objects) {
        mObjects = objects;
    }

    public void addLast(List data) {
        mObjects.addAll(data);
    }

    public void addFirst(List data) {
        mObjects.addAll(0, data);
    }

    public void deleteData(Object data) {
        mObjects.remove(data);
    }

    public List<BaseListData> getDatas() {
        return mObjects;
    }

    public void clear() {
        mObjects.clear();
    }
}
