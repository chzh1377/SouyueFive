package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.Interest;
import com.zhongsou.souyue.im.util.LLog;
import com.zhongsou.souyue.im.util.Slog;

import java.util.*;

/**
 * @description: 基础适配器
 * @auther: qubian
 * @data: 2015/12/22.
 */
public abstract class BaseListViewAdapter<T> extends BaseAdapter{

    private  BaseListManager manager;
    private SparseArray<Queue> mBottomCache;

    public abstract BaseListTypeRender getAdapterTypeRender(int position);

    public abstract BaseBottomViewRender getBottomViewRender(int position);

    public BaseListViewAdapter(Context context, List<T> objects) {
        mBottomCache = new SparseArray<Queue>();
    }
    public void setManager(BaseListManager manager) {
        this.manager = manager;
    }

    public BaseListManager getManager() {
        return manager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseListTypeRender typeRender ;
        if(null == convertView)
        {
            typeRender =getAdapterTypeRender(position);
            typeRender.setListManager(manager);
            convertView= typeRender.getConvertView();
            convertView.setTag(R.id.base_list_adapter_item_render, typeRender);
            typeRender.fitEvents();
        }else
        {
            typeRender = (BaseListTypeRender) convertView.getTag(R.id.base_list_adapter_item_render);
        }
        convertView.setTag(R.id.base_list_adapter_item_position, position);
        initBottomView(convertView,position);
        if (null != typeRender) {
            try {
                typeRender.fitDatas(position);
            }catch (Exception e){
                LLog.e("list render error","list render 填数据出错：\n"+getItem(position));
                e.printStackTrace();
            }
        }


        return convertView;
    }

    /**
     * 底部视图，缓存复用
     * @param mConvertView
     * @param position
     */
    private void initBottomView(View mConvertView,int position)
    {
        LinearLayout view = (LinearLayout) mConvertView.findViewById(R.id.bottomView);
        int mBottomType = ListUtils.getBottomViewType(getItem(position));
        BaseBottomViewRender bottomViewRender = null;
        if(view != null&& mBottomType != 0)
        {

            View bottom =  view.getChildAt(0);
            int footType = 0;
            if (bottom !=null) {
                Object tag = bottom.getTag();
                if (tag != null) {
                    footType = (Integer) tag;
                    bottomViewRender = (BaseBottomViewRender) bottom.getTag(R.id.bottomView);
                }
            }
            if (footType != mBottomType) {
                view.removeAllViews();
                if (bottom != null) {//如果当前得到的bottom不为null就放入缓存中
                    Queue queue = mBottomCache.get(footType);
                    if (queue == null) {
                        queue = new LinkedList();
                    }
                    queue.offer(bottomViewRender);
                    mBottomCache.put(footType, queue);
                }
                Queue queue = mBottomCache.get(mBottomType);
                if (queue == null) {
                    queue = new LinkedList();
                }

                bottomViewRender = (BaseBottomViewRender) queue.poll();
                if (bottomViewRender == null) {
//                    Log.v(this.getClass().getName(), "AAA create");
                    bottomViewRender = getBottomViewRender(position);
                    if (bottomViewRender != null) {
                        View footview = bottomViewRender.getConvertView();
                        footview.setTag(mBottomType);
                        footview.setTag(R.id.bottomView, bottomViewRender);
                        view.addView(footview);
                        bottomViewRender.setListManager(manager);
                        bottomViewRender.fitEvents();
                    }
                    view.setVisibility(View.VISIBLE);
                }else{
                    View footview = bottomViewRender.getConvertView();
                    view.addView(footview);
                }
            }
            if (bottomViewRender!=null){
                bottomViewRender.fitDatas(position);
            }
        }
    }

}
