package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * Created by zcz on 2015/4/1  11:07.
 */
public class ImDialogAdapter extends BaseAdapter{
    private int items[];
    private LayoutInflater inflater;
    public ImDialogAdapter(Context context,int items[]){
        inflater=LayoutInflater.from(context);
        this.items = items;
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return items[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.im_new_dialog_item,null);
        TextView textView = (TextView)v.findViewById(R.id.im_chat_item);
        textView.setText(items[position]);
        return v;
    }
}
