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
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.SubscribeItem;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsSubscribeAdapter extends BaseAdapter {
    public static final int ADD = 0;
    public static final int DEL = 1;
    public static final int NOCHANGE = -1;
    private final LayoutInflater mInflater;
    public List<SubscribeItem> subscribeNews = new ArrayList<SubscribeItem>();
    private Context context;
//    private AQuery aq;

    public NewsSubscribeAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
//        aq = new AQuery(context);
    }

    /**
     * 初始化数据
     * 
     * @param subscribeNews
     */
    public void setDatas(List<SubscribeItem> subscribeNews) {
        this.subscribeNews = subscribeNews;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return subscribeNews.size();
    }

    @Override
    public Object getItem(int position) {
        return subscribeNews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.news_source_subscribe_list_item, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.tv_newsource_subscribe);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_newsource_subscribe_add);
            holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_newsource_subscribe_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageDrawable(subscribeNews.get(position).hasSubscribe() ? context.getResources().getDrawable(R.drawable.subscribe_cancel01) : context.getResources().getDrawable(
                R.drawable.subscribe_add01));
        //aq.id(holder.iv_pic).image(subscribeNews.get(position).image(), true, true);
        PhotoUtils.showCard(UriType.HTTP, subscribeNews.get(position).image(), holder.iv_pic);
        convertView.setTag(holder);
        holder.text.setText(StringUtils.truncate(subscribeNews.get(position).keyword().trim(), StringUtils.LENGTH_12));
        return convertView;
    }

    public void changeState(int position,ArrayList<String> rssIdAdd,ArrayList<String> idDelete) {
        SubscribeItem subscribeItem = subscribeNews.get(position);
        if(!subscribeItem.hasSubscribe()){
            rssIdAdd.add(subscribeItem.id()+"");
        }else{
            idDelete.add(subscribeItem.subscribeId()+"");
        }

        notifyDataSetChanged();

    }
    private class ViewHolder {
        TextView text;
        ImageView imageView;
        ImageView iv_pic;
    }

    public void clearDatas() {
        subscribeNews.clear();
    }

}
