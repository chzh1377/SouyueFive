package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class SearchNavigationAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] mbuttonTitles;
    private int curSelected;

    public SearchNavigationAdapter(Context mContext) {
        this.mContext = mContext;
        final Resources res = mContext.getResources();
        mbuttonTitles=res.getStringArray(R.array.search_type);
    }
    @Override
    public int getCount() {
        return mbuttonTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mbuttonTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_type_head_include_item, parent,false);
        }
        ((TextView) convertView.findViewById(R.id.s_type_text)).setText(mbuttonTitles[position]);
        if (curSelected == position) {
            convertView.setBackgroundResource(R.drawable.search_type_bg);
        }else{
            convertView.setBackgroundColor(R.color.transparent);
        }
        return convertView;
      
    }
    public void setSelected(int p) {
        if (p == curSelected) {
            return;
        }
        curSelected = p;
    }

}
