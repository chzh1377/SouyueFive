package com.zhongsou.souyue.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.PackageInfoMap;

import java.util.List;

/**
 * Created by wangqiang on 15/12/25.
 */
public class MapAdapter extends BaseAdapter {

    private List<PackageInfoMap> lists;
    private Context mContext;
    private LayoutInflater mInflater;

    public MapAdapter(Context context,List<PackageInfoMap> lists){
        this.mContext = context;
        this.lists = lists;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists!=null?lists.size():0;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
       if(convertView==null){
           convertView = mInflater.inflate(R.layout.item_map,null);
           holder = new ViewHolder();
           holder.tv = (TextView)convertView.findViewById(R.id.tv_map);
           convertView.setTag(holder);
       }else{
           holder = (ViewHolder)convertView.getTag();
       }
        holder.tv.setText(lists.get(position).getApplicationName());
        return convertView;
    }

    static class ViewHolder{
        TextView tv;
    }
}
