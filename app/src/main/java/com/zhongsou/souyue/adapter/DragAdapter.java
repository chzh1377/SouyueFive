package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.ChannelItem;

import java.util.List;

public class DragAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否正在显示ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    private int holdPosition;
    private boolean isChanged = false;
    boolean isVisible = true;
    public List<ChannelItem> channelList;
    private TextView item_text;
    public int remove_position = -1;

    public DragAdapter(Context context, List<ChannelItem> channelList) {
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
        if (position == 0) {
            item_text.setTextColor(0xffe1e1e1);
            item_text.setBackgroundResource(R.drawable.bg_channel_mannger_up);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    /**
     * 添加
     */
    public void addItem(ChannelItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    /**
     * 移动位置
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        ChannelItem dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获得列表
     */
    public List<ChannelItem> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置移除position
     */
    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    /**
     * 删除
     */
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

    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}