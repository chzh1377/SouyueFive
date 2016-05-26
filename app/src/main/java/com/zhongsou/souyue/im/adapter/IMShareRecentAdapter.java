package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享给最近联系人列表
 */
public class IMShareRecentAdapter extends BaseAdapter {
    private List<MessageRecent> groupList = new ArrayList<MessageRecent>();
    private Context mContext;
    private DisplayImageOptions options;

    public IMShareRecentAdapter(Context cx) {
        this.mContext = cx;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnLoading(R.drawable.default_head).build();
    }

    public void setData(List<MessageRecent> groupList) {
        this.groupList = groupList;
    }

    // 清除所有引用
    public void clear() {
        groupList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public MessageRecent getItem(int i) {
        return groupList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private View inflateView(int id, ViewGroup parent) {
        LayoutInflater viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return viewInflater.inflate(id, parent, false);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageRecent item = (MessageRecent) getItem(i);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflateView(R.layout.im_swipe_contacts_list_view_row, viewGroup);
            holder = new ViewHolder();
            holder.item_left = (RelativeLayout) convertView.findViewById(R.id.item_left);
            holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);

            holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
            holder.bAction_delete = (Button) convertView.findViewById(R.id.row_btn_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (TextUtils.isEmpty(item.getChatAvatar())) {
            holder.ivImage.setImageResource(R.drawable.default_head);
        } else {
            ImageLoader.getInstance().displayImage(
                    item.getChatAvatar(),
                    holder.ivImage,options);
        }
        String username = item.getChatName();
        if (!TextUtils.isEmpty(username))
            holder.tvTitle.setText(username);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;
        ImageView ivImage;
        TextView tvTitle;
        Button bAction_delete;
    }
}
