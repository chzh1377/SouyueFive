package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.HomePageItem;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;

public class Mine_PurseAdapter extends BaseAdapter {

    private ArrayList<HomePageItem> items = new ArrayList<HomePageItem>();
    private final LayoutInflater mInflater;
    public static final int VIEW_TYPE_TITLE = 0;
    public static final int VIEW_TYPE_GROUP = 1;
    public static final int VIEW_TYPES_COUNT = 2;
    private Context context;

    public Mine_PurseAdapter(Context context) {
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
        } else {
            return VIEW_TYPE_GROUP;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomePageItem type = items.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (getItemViewType(position) == VIEW_TYPE_TITLE) {
                convertView = mInflater.inflate(R.layout.mine_listview_item, parent, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.mine_listview_item_txtTitle);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.mine_listview_item_img);
                holder.iv_imgRightArrow = (ImageView) convertView.findViewById(R.id.mine_listview_item_imgRightArrow);
                
//                holder.tv_new_icon = (ImageView) convertView.findViewById(R.id.tv_new_icon);
//                holder.iv_right_icon = (ImageView) convertView.findViewById(R.id.iv_right_icon);
            } else {
                convertView = mInflater.inflate(R.layout.discover_group_item, parent, false);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (type != null) {
            if (getItemViewType(position) == VIEW_TYPE_TITLE) {
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SingleCricleListAdapter.dip2px(context, 18), SingleCricleListAdapter.dip2px(context, 18));
//                layoutParams.leftMargin = SingleCricleListAdapter.dip2px(context, 15);
//                layoutParams.addRule(Gravity.CENTER_VERTICAL);
//                holder.iv_icon.setLayoutParams(layoutParams);
               // aQuery.id(holder.iv_icon).image(type.getImage(), true, true, 0, AQuery.INVISIBLE);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP,type.getImage(),holder.iv_icon);
//                holder.tv_title.setTextSize(18);
                holder.tv_title.setText(type.title());
                holder.iv_imgRightArrow.setBackgroundResource(R.drawable.mine_arrow_gray);
                /*holder.iv_right_icon.setVisibility(View.VISIBLE);

                if (type.isHasNew()) {
                    holder.tv_new_icon.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_new_icon.setVisibility(View.GONE);
                }*/
            } else {
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
        ImageView iv_imgRightArrow;
//        ImageView tv_new_icon;
//        ImageView iv_right_icon;
    }


}
