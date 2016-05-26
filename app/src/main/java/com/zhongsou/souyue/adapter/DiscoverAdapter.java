package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.HomePageItem;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;

public class DiscoverAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HomePageItem> items = new ArrayList<HomePageItem>();
    private final LayoutInflater mInflater;
    public static final int VIEW_TYPE_TITLE = 0;    //数据条目
    public static final int VIEW_TYPE_GROUP = 1;    //有标题名称的条目
    public static final int VIEW_TYPE_DIVIDER = 2;  //空白的分隔的条目
    public static final int VIEW_TYPES_COUNT = 3;

    public DiscoverAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void SetList(ArrayList<HomePageItem> list) {
        if (list != null) {
            this.items.clear();
            this.items.addAll(list);
            this.items.add(items.get(0));
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public HomePageItem getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPES_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        HomePageItem bean = items.get(position);
        if (bean.category() != null && StringUtils.isNotEmpty(bean.category())) {
            return VIEW_TYPE_TITLE;
        } else if(bean.title().contains("section3")){
            return VIEW_TYPE_GROUP;
        }else{
            return VIEW_TYPE_DIVIDER;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomePageItem item = items.get(position);
        ViewHolder holder;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            if (type == VIEW_TYPE_TITLE) {
                convertView = mInflater.inflate(R.layout.discover_item, parent, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_new_icon = (ImageView) convertView.findViewById(R.id.tv_new_icon);
            } else if(type == VIEW_TYPE_GROUP) {

                convertView = mInflater.inflate(R.layout.discover_group_content, parent, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_group);
            }else{
                View divider = new View(context);
                divider.setLayoutParams(new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.MATCH_PARENT,
                        DeviceUtil.dip2px(context,10)));
                convertView = divider;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            if (type == VIEW_TYPE_TITLE) {
                String imgUrl = item.getImage();
                if (imgUrl != null && "local".equals(imgUrl)) {
                    holder.iv_icon.setImageResource(R.drawable.richscan);
                } else {
                    PhotoUtils.showCard(PhotoUtils.UriType.HTTP, item.getImage(), holder.iv_icon);
                }
                holder.tv_title.setText(item.title());

                if (item.isHasNew()) {
                    holder.tv_new_icon.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_new_icon.setVisibility(View.GONE);
                }
            } else if(type == VIEW_TYPE_GROUP) {
                holder.tv_title.setHeight(DeviceUtil.dip2px(context, 50));
                holder.tv_title.setText(context.getResources().getString(R.string.cooperative));   //Cooperative Extension;
            }
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) == 0 ? true : false;
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        ImageView tv_new_icon;
        LinearLayout ll_discover_group;
    }
}
