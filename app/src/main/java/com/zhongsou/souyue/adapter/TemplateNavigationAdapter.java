package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.TemplateWidgetNavData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description 横向滑动导航 - 模板使用
 * @date 2016/04/05
 */
public class TemplateNavigationAdapter extends BaseAdapter {
    private Context mContext;
    private List<TemplateWidgetNavData> navs = new ArrayList<TemplateWidgetNavData>();

    public void addNavs(List<TemplateWidgetNavData> nav) {
        if (nav != null && navs.size() == 0)
            this.navs.addAll(nav);

    }

    public void clearNavs() {
        if (navs != null)
            this.navs.clear();
        notifyDataSetChanged();
    }

    public TemplateNavigationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return navs.size();
    }

    @Override
    public TemplateWidgetNavData getItem(int position) {
        return navs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.navigation_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.navi_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(navs.get(position).getTitle());
        return convertView;
    }

    private class ViewHolder {
        TextView title;
    }
}
