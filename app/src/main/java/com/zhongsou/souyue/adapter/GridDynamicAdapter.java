package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.module.AGridDynamic;
import com.zhongsou.souyue.view.ItemDynamicCircle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongs
 */
public class GridDynamicAdapter extends BaseAdapter {
    private ArrayList<AGridDynamic> mDataList;
    private Context mContext;


    private boolean showDelView;
    private DeleteListener deleteListener;
    private boolean mDoingAnim;


    public interface DeleteListener {
        void removeItem(int position);
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setmDoingAnim(boolean _doing) {
        mDoingAnim = _doing;
    }

    public boolean getmDoingAnim() {
        return mDoingAnim;
    }

    public void setShowDelView(boolean showDelView) {
        this.showDelView = showDelView;
    }

    public GridDynamicAdapter(Context context, List<?> items) {
        this.mContext = context;
        mDataList = new ArrayList<AGridDynamic>();
        for (Object ite : items) {
            if (ite != null) {
                mDataList.add((AGridDynamic) ite);
            }
        }
    }

    public GridDynamicAdapter(Context context, Object item) {
        this.mContext = context;
        mDataList = new ArrayList<AGridDynamic>();
        if (item != null) {
            mDataList.add((AGridDynamic) item);
        }
    }

    public void pause() {
        ImageLoader.getInstance().pause();
    }

    public void resume() {
        ImageLoader.getInstance().resume();
    }

    public ArrayList<AGridDynamic> getItems() {
        return mDataList;
    }

    public void setData(List<?> items) {
        mDataList.clear();
        for (Object ite : items) {
            mDataList.add((AGridDynamic) ite);
        }
    }
    public void addData(List<?> items)
    {
        for (Object ite : items) {
            mDataList.add((AGridDynamic) ite);
        }
    }

    public void addOneItem(AGridDynamic mic, boolean isPre) {
        if (mDataList == null) {
            mDataList = new ArrayList<AGridDynamic>();
        }
        if (isPre) {
            mDataList.add(0, mic);
        } else {
            mDataList.add(mic);
        }
        notifyDataSetChanged();
    }

    public void removeData(AGridDynamic _data) {
        mDataList.remove(_data);

    }

    public AGridDynamic removeIndexData(int _position) {
        return mDataList.remove(_position);
    }

    public void addIndexData(int _index, AGridDynamic _data) {
        mDataList.add(_index, _data);
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public AGridDynamic getItem(int position) {
        if(mDataList != null && mDataList.size() > 0){
            return mDataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getmId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemDynamicCircle holder;
        if (convertView == null) {
            if(mContext == null) mContext = MainApplication.getInstance();
            holder = new ItemDynamicCircle(mContext, deleteListener);
        } else {
            holder = (ItemDynamicCircle) convertView;
        }
        AGridDynamic item = getItem(position);
        holder.build(this, item, position, showDelView);
        item.setmPosition(position);
        return holder;
    }
}