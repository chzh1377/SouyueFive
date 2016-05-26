package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.CircleIMGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bob zhou on 15-1-9.
 * for CircleIMGroupActivity
 * 兴趣圈讨论群列表
 */
public class CircleIMGroupListAdapter extends BaseAdapter {


    private List<CircleIMGroup> list = new ArrayList<CircleIMGroup>();

    private Context ctx;

    private ListView listView;

    private DisplayImageOptions options;

    private ImageLoader imgLoader;

    public CircleIMGroupListAdapter(Context ctx) {
        this.ctx = ctx;
        this.imgLoader = ImageLoader.getInstance();
        this.options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true).showImageOnLoading(R.drawable.circle_default_head).showImageForEmptyUri(R.drawable.circle_default_head).showImageOnFail(R.drawable.circle_default_head).displayer(new SimpleBitmapDisplayer()).build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(ctx, R.layout.circle_im_group_item, null);
            viewHolder = new ViewHolder();
            initViewHolder(viewHolder, convertView);
            convertView.setTag(viewHolder);
            convertView.setBackgroundResource(R.drawable.circle_list_item_selector);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        setViewData(viewHolder,position);
        return convertView;
    }

    public void setList(List<CircleIMGroup> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<CircleIMGroup> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public long getLastSortNum() {
        return list.get(getCount() - 1).getCreate_time();
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public void updateRow(long group_id) {
        for (CircleIMGroup group : list) {
            if (group_id == group.getGroup_id()) {
                group.setCurrent_count(group.getCurrent_count() + 1);
                updateItemAtPosition(group);
                break;
            }
        }
    }


    private void updateItemAtPosition(Object target) {
        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
                if (target == listView.getItemAtPosition(i)) {
                    View view = listView.getChildAt(i - start);
                    listView.getAdapter().getView(i, view, listView);
                    break;
                }
        }
    }


    class ViewHolder {
        public ImageView logoIv;
        public TextView nameTv;
        public TextView countTv;
    }


    private void initViewHolder(ViewHolder viewHolder, View convertView) {
        viewHolder.logoIv = (ImageView) convertView.findViewById(R.id.group_logo_img);
        viewHolder.nameTv = (TextView) convertView.findViewById(R.id.group_name_tv);
        viewHolder.countTv = (TextView) convertView.findViewById(R.id.group_count_tv);
    }

    private void setViewData(ViewHolder viewHolder, int position) {
        CircleIMGroup item = list.get(position);
        if(item != null){
            imgLoader.displayImage(item.getGroup_logo(),viewHolder.logoIv,options);
            viewHolder.nameTv.setText(item.getGroup_name());
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append(item.getCurrent_count()).append("/").append(item.getMax_count());
            viewHolder.countTv.setText(strBuffer.toString());
        }

    }

}
