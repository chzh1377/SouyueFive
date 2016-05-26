package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.MineListInfo;

import java.util.List;

/**
 * Created by yinguanping on 15/1/7.
 */
public class MineAdapter extends BaseAdapter {
    private Context context;
    private List<MineListInfo> listInfos;

    public MineAdapter(Context context, List<MineListInfo> listInfos) {
        this.context = context;
        this.listInfos = listInfos;
    }

    @Override
    public int getCount() {
        return listInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MineListInfo mineListInfo = null;
        if (listInfos != null && !listInfos.isEmpty()) {
            mineListInfo = listInfos.get(position);
        } else {
            return null;
        }

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            if (mineListInfo.getType() == 1) {
                convertView = mInflater.inflate(R.layout.mine_listview_item, parent, false);
                holder.mine_listview_item_txtTitle = (TextView) convertView.findViewById(R.id.mine_listview_item_txtTitle);
                holder.mine_listview_item_txtContent = (TextView) convertView.findViewById(R.id.mine_listview_item_txtContent);
                holder.mine_listview_item_img = (ImageView) convertView.findViewById(R.id.mine_listview_item_img);
                holder.mine_listview_item_imgRightArrow = (ImageView) convertView.findViewById(R.id.mine_listview_item_imgRightArrow);
                holder.mine_listview_item_imgContent = (ImageView) convertView.findViewById(R.id.mine_listview_item_imgContent);
            } else {
                convertView = mInflater.inflate(R.layout.discover_group_item, parent, false);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mineListInfo.getType() == 1) {
            if (position == 0) {//我的首页，特殊处理色值
                /*holder.mine_listview_item_txtTitle.setTextColor(context.getResources().getColor(R.color.mine_headbg));
                holder.mine_listview_item_txtContent.setTextColor(context.getResources().getColor(R.color.mine_text_light_blue));
                holder.mine_listview_item_imgRightArrow.setBackgroundResource(R.drawable.mine_arrow_blue);*/
            	holder.mine_listview_item_txtContent.setVisibility(View.GONE);
            	holder.mine_listview_item_imgContent.setVisibility(View.VISIBLE);
            	holder.mine_listview_item_imgContent.setBackgroundResource(R.drawable.mine_icon_personinfo_content);
            } else {
               /* holder.mine_listview_item_txtTitle.setTextColor(context.getResources().getColor(R.color.plaza_sub_cata));
                holder.mine_listview_item_txtContent.setTextColor(context.getResources().getColor(R.color.gray_7e));
                holder.mine_listview_item_imgRightArrow.setBackgroundResource(R.drawable.mine_arrow_gray);*/
            	holder.mine_listview_item_txtContent.setVisibility(View.VISIBLE);
            	holder.mine_listview_item_imgContent.setVisibility(View.GONE);
            	holder.mine_listview_item_txtContent.setText(mineListInfo.getContent());
            }

            holder.mine_listview_item_txtTitle.setText(mineListInfo.getTitle());
            holder.mine_listview_item_img.setImageResource(mineListInfo.getImgId());
            holder.mine_listview_item_imgRightArrow.setBackgroundResource(R.drawable.mine_arrow_gray);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView mine_listview_item_img;
        TextView mine_listview_item_txtTitle;
        TextView mine_listview_item_txtContent;
        ImageView mine_listview_item_imgRightArrow;
        ImageView mine_listview_item_imgContent;
    }
}
