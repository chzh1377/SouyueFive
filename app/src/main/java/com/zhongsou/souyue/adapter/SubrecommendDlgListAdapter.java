package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.RecommendSubTab;
import com.zhongsou.souyue.module.RecommendTabSubListItem;
import com.zhongsou.souyue.ui.subrecommend.SubRecommendDialog;
import com.zhongsou.souyue.utils.MyImageLoader;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by zyw on 2015/12/21.
 * 订阅弹窗的listadapter
 */
public class SubrecommendDlgListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private final Context mContext;
    ArrayList<RecommendTabSubListItem> mSubTabs = new ArrayList<RecommendTabSubListItem>();
    private final LocalBroadcastManager mLbm;


    public SubrecommendDlgListAdapter(Context context, RecommendSubTab subTabs) {
        this.mContext = context;
        mSubTabs.addAll(subTabs.getTablist());
        //初始化list
        Iterator<RecommendTabSubListItem> iterator = mSubTabs.iterator();
        while (iterator.hasNext()) {
            RecommendTabSubListItem next = iterator.next();
            boolean checked = next.issubed() || next.isDefault();
            next.setChecked(checked ? RecommendTabSubListItem.CHECKED_CHECKED : RecommendTabSubListItem.CHECKED_UNCHECKED);
        }
        mLbm = LocalBroadcastManager.getInstance(context);
    }

    public ArrayList<RecommendTabSubListItem> getSubTabs() {
        return mSubTabs;
    }

    @Override
    public int getCount() {
        return mSubTabs.size();
    }

    @Override
    public RecommendTabSubListItem getItem(int position) {
        return mSubTabs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.subrecommend_dlg_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setData(holder, position);
        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param position
     */
    private void setData(final ViewHolder holder, final int position) {
        holder.divider.setVisibility(View.VISIBLE);
//        holder.cbCheck.setChecked(false);
        final RecommendTabSubListItem item = getItem(position);
        PhotoUtils.getImageLoader().displayImage(item.getImageurl(), holder.ivTitle, MyImageLoader.subrecommendOptions);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDesc());
//        if (item.isChecked() != RecommendTabSubListItem.CHECKED_NONE) {
//            holder.cbCheck.setChecked(item.isChecked() == RecommendTabSubListItem.CHECKED_CHECKED);
//        } else {
//            holder.cbCheck.setChecked(item.issubed() || item.isDefault());
//        }
        holder.cbCheck.setChecked(item.isChecked() == RecommendTabSubListItem.CHECKED_CHECKED);
//        holder.cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    //添加删除订阅项
//                    setItemAction(item, SubRecommendDialog.ACTION_ADD);
//                } else {
//                    setItemAction(item, SubRecommendDialog.ACTION_REMOVE);
//                }
//                //记录点击，用于回显
//                item.setChecked(isChecked ? RecommendTabSubListItem.CHECKED_CHECKED : RecommendTabSubListItem.CHECKED_UNCHECKED);
//                mSubTabs.set(position, item);
//            }
//        });
        holder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
//                boolean checked =holder.cbCheck.isChecked();
//                if(checked){
//                    setItemAction(item, SubRecommendDialog.ACTION_ADD);
//                }else{
//                    setItemAction(item, SubRecommendDialog.ACTION_REMOVE);
//                }
//                item.setChecked(checked ? RecommendTabSubListItem.CHECKED_CHECKED : RecommendTabSubListItem.CHECKED_UNCHECKED);
//                mSubTabs.set(position, item);
//                holder.cbCheck.setChecked(!checked);
                boolean checked = cb.isChecked();
                if(checked){
                    setItemAction(item, SubRecommendDialog.ACTION_ADD);
                }else{
                    setItemAction(item, SubRecommendDialog.ACTION_REMOVE);
                }
                item.setChecked(checked ? RecommendTabSubListItem.CHECKED_CHECKED : RecommendTabSubListItem.CHECKED_UNCHECKED);
                mSubTabs.set(position, item);
                cb.setChecked(!checked);
                notifyDataSetChanged();
            }
        });
        if (position == getCount() - 1) {
            holder.divider.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 添加删除订阅项，是个本地广播。
     *
     * @param item
     * @param action
     */
    private void setItemAction(RecommendTabSubListItem item, String action) {
        Intent intent = new Intent(action);
        intent.putExtra("data", item);
        mLbm.sendBroadcast(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecommendTabSubListItem item = getItem(position);
        boolean checked = item.isChecked() == RecommendTabSubListItem.CHECKED_CHECKED;
        checked = !checked;
        if(checked){
            setItemAction(item, SubRecommendDialog.ACTION_ADD);
        }else{
            setItemAction(item, SubRecommendDialog.ACTION_REMOVE);
        }
        item.setChecked(checked ? RecommendTabSubListItem.CHECKED_CHECKED : RecommendTabSubListItem.CHECKED_UNCHECKED);
        mSubTabs.set(position, item);
//        cb.setChecked(false);
        notifyDataSetChanged();
    }


    static class ViewHolder {
        ImageView ivTitle;
        TextView tvTitle;
        TextView tvDesc;
        CheckBox cbCheck;
        View divider;

        public ViewHolder(View convertView) {
            ivTitle = (ImageView) convertView.findViewById(R.id.subrecommanddlg_listitem_image);
            tvTitle = (TextView) convertView.findViewById(R.id.subrecommanddlg_listitem_title);
            tvDesc = (TextView) convertView.findViewById(R.id.subrecommanddlg_listitem_desc);
            cbCheck = (CheckBox) convertView.findViewById(R.id.subrecommanddlg_listitem_checkbox);
            divider = convertView.findViewById(R.id.subrecommend_listitem_divider);
        }
    }

}
