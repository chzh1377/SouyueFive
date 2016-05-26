package com.zhongsou.souyue.adapter.firstleader;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.ZSImageOptions;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.firstleader.ChildGroupItem;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.Utils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zyw on 2016/3/23.
 */
public class LeaderPageThreeAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    public static final String TAG = LeaderPageThreeAdapter.class.getSimpleName();
    private final Context mCtx;
    private List<ChildGroupItem> mDatas = new ArrayList<ChildGroupItem>();
    private final LayoutInflater mInflater;

    private OnItemSelectedCountListener onItemSelectedCountListener;
    private int                         selectedCount;

    private int mlayoutHeight;

    private int mScreenWidth;

    public LeaderPageThreeAdapter(Context context, List<ChildGroupItem> datas) {
        this.mCtx = context;
        if (datas != null && datas.size() > 0) {
            this.mDatas.addAll(datas);
        }
        for (int x = 0; x < mDatas.size(); x++) {
            selectedCount += mDatas.get(x).getIsSelected();
        }
        mInflater = LayoutInflater.from(context);
        mlayoutHeight = Utils.getScreenHeight() - DeviceUtil.dip2px(mCtx, 159) - DeviceUtil.dip2px(mCtx,54);
        mScreenWidth = Utils.getScreenWidth();
    }

    public void setOnItemSelectedCountListener(OnItemSelectedCountListener onItemSelectedCountListener) {
        this.onItemSelectedCountListener = onItemSelectedCountListener;
        if (onItemSelectedCountListener != null) {
            onItemSelectedCountListener.onItemSelectedCount(selectedCount);
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public ChildGroupItem getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_first_leader_pagethree_griditem, null);
            setComvertViewLayout(convertView);
            viewHolder = new Holder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Holder) convertView.getTag();
        }
        initData(position, viewHolder);
        return convertView;
    }


    private void setComvertViewLayout(View convertView) {
        //设置convertview的属性
        View viewImage = convertView.findViewById(R.id.image);
        View viewCover = convertView.findViewById(R.id.home_leader_pagethree_selected);
        View text  = convertView.findViewById(R.id.text);
        viewImage.getLayoutParams().height = (int) (mlayoutHeight / 5.0);
        viewImage.getLayoutParams().width = mScreenWidth / 6;
        viewCover.getLayoutParams().height = (int) (mlayoutHeight /5.0);
        viewCover.getLayoutParams().width = mScreenWidth / 6;
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) text.getLayoutParams();
//        layoutParams.topMargin = DeviceUtil.dip2px(mCtx,5);
//        text.requestLayout();
        convertView.requestLayout();
    }

    public int getLayoutHeight(){
        return mlayoutHeight;
    }

    private void initData(int position, Holder viewHolder) {
        ChildGroupItem item = getItem(position);
        viewHolder.getTv().setText(item.getKeyword());
        viewHolder.imageView.setImageURI(Uri.parse(item.getImage()), ZSImageOptions.getLocalCircleConfig(mCtx), null);
        viewHolder.getSelected().setImageResource(R.drawable.home_first_leader_pagethree_selected);
        viewHolder.getSelected().setVisibility(item.getIsSelected() == 1 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setPosSelected(position);
        notifyDataSetChanged();
        if (onItemSelectedCountListener != null) {
            onItemSelectedCountListener.onItemSelectedCount(selectedCount);
        }
    }

    private void setPosSelected(int position) {
        ChildGroupItem item = getItem(position);
        item.setIsSelected(item.getIsSelected() == 1 ? 0 : 1);
        if (item.getIsSelected() == 1) {
            //如果变成选中状态
            selectedCount++;
            //TODO 变为选中状态 统计事件 guide.people.interest
            UpEventAgent.onZSGuideInterestClick(mCtx, item.getKeyword());
        } else {
            selectedCount--;
        }
        mDatas.set(position, item);
        if (item.isDefaultSelected() && item.getIsSelected() == 0) {
            //取消订阅了
            //TODO 添加事件 默认选中的取消点击 guide.selected.cancel
            UpEventAgent.onZSGuideSelectedCancel(mCtx, item.getKeyword());
        }
    }

    public List<ChildGroupItem> getDatas() {
        return mDatas;
    }

    static class Holder {
        ImageView   selected;
        TextView    tv;
        ZSImageView imageView;

        public ImageView getSelected() {
            return selected;
        }

        public Holder(View convertView) {
            this.tv = (TextView) convertView.findViewById(R.id.text);
            this.imageView = (ZSImageView) convertView.findViewById(R.id.image);
            this.selected = (ImageView) convertView.findViewById(R.id.home_leader_pagethree_selected);
        }

        public ZSImageView getImageView() {
            return imageView;
        }

        public TextView getTv() {
            return tv;
        }
    }


    public interface OnItemSelectedCountListener {
        void onItemSelectedCount(int count);
    }
}
