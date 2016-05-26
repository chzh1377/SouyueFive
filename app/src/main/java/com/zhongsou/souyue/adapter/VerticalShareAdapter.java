/*******************************************************************************
 * Copyright 2012 Steven Rudenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.zhongsou.souyue.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * 垂直分享
 * 
 * @author zhangliang01@zhongsou.com
 */
@SuppressLint("Recycle")
public class VerticalShareAdapter extends BaseAdapter {
    private final String[] mTitles;
    private final int[] mUrls;
    private final TypedArray mIcons;
    private final LayoutInflater mInflater;
    public VerticalShareAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        final Resources res = context.getResources();
        mTitles = res.getStringArray(R.array.re_share_names);
        mUrls = res.getIntArray(R.array.re_share_links);
        mIcons = res.obtainTypedArray(R.array.re_share_icons);
    }

    @Override
    public int getCount() {
        return mUrls.length;
    }

    @Override
    public Integer getItem(int position) {
        return mUrls[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sharemenu_item, parent, false);
            holder.share_way = (TextView) convertView.findViewById(R.id.share_way);
            holder.share_icon = (ImageView) convertView.findViewById(R.id.share_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.share_way.setText(mTitles[position]);
        holder.share_icon.setImageDrawable(mIcons.getDrawable(position));
        return convertView;
    }

    private static class ViewHolder {
        TextView share_way;
        ImageView share_icon;
    }
}
