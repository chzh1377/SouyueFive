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
import com.tuita.sdk.im.db.module.Group;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zoulu
 * on 14-9-1
 * Description:
 */
public class GroupListAdapter extends BaseAdapter{
    private List<Group> groupList = new ArrayList<Group>();
    private Context cx;
    private DisplayImageOptions options;

    public GroupListAdapter(Context cx){
        this.cx = cx;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .showImageOnLoading(R.drawable.default_head).build();
    }

    public void setData(List<Group> groupList){
        this.groupList = groupList;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Group getItem(int i) {
        return groupList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private View inflateView(int id, ViewGroup parent) {
        LayoutInflater viewInflater = (LayoutInflater) MainApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return viewInflater.inflate(id, parent, false);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Group group = (Group) getItem(i);
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

        if (TextUtils.isEmpty(group.getGroup_avatar())) {
            holder.ivImage.setImageResource(R.drawable.default_head);
        } else {
            ImageLoader.getInstance().displayImage(
                    group.getGroup_avatar(),
                    holder.ivImage,options);
        }


        String username = group.getGroup_nick_name();
        if(!TextUtils.isEmpty(username))
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
