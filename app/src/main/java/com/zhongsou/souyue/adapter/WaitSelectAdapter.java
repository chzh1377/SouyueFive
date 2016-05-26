package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.ChannelItem;

import java.util.List;

public class WaitSelectAdapter extends BaseAdapter {
    private Context context;
    public List<ChannelItem> channelList;
    private TextView item_text;
    boolean isVisible = true;
    public int remove_position = -1;

    public WaitSelectAdapter(Context context, List<ChannelItem> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ChannelItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.channel_mannger_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        ChannelItem channel = getItem(position);
        if (channel.getName().length() >= 4) {
            item_text.setTextSize(14);
        } else {
            item_text.setTextSize(16);
        }
        item_text.setText(channel.getName());
        if (!isVisible && position == 0) {
            item_text.setText("");
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    public List<ChannelItem> getChannnelLst() {
        return channelList;
    }

    public void addItem(ChannelItem channel,int index) {
        channelList.add(index,channel);
        notifyDataSetChanged();
    }

    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    public void setListDate(List<ChannelItem> list) {
        channelList = list;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}