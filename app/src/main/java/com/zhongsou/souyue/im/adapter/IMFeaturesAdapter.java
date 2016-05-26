package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.FeaturesBean;

import java.util.List;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.1)
 * @Copyright (c) 2016 zhongsou
 * @Description class IM功能adapter
 * @date 16/1/8
 */
public class IMFeaturesAdapter extends ArrayAdapter<FeaturesBean> {

    private Context mContext;
    private List<FeaturesBean> mList;

    public IMFeaturesAdapter(Context context, List<FeaturesBean> list) {
        super(context, 0, list);
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FeaturesBean features = mList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_im_features, null);
            viewHolder.ivICon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tvText = (TextView) convertView.findViewById(R.id.tv_text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivICon.setImageResource(features.getFeaturesIcon());
        viewHolder.ivICon.setTag(position);
        viewHolder.tvText.setText(mContext.getString(features.getFeaturesText()));
        viewHolder.tvText.setTextColor(mContext.getResources().getColor(features.getTextColor()));
        return convertView;
    }

    class ViewHolder {
        private ImageView ivICon;
        private TextView tvText;
    }
}
