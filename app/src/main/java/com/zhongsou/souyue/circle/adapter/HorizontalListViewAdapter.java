package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.ui.RoundRectImageView;

import java.util.ArrayList;

public class HorizontalListViewAdapter extends BaseAdapter {
    private ArrayList<String> mIconIDs;
    private Context mContext;
    private LayoutInflater mInflater;

    public HorizontalListViewAdapter(Context context, ArrayList<String> ids) {
        this.mContext = context;
        this.mIconIDs = ids;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mIconIDs.size();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.circle_horiztal_list_item, null);
            holder.mImage = (RoundRectImageView) convertView.findViewById(R.id.img_list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(mIconIDs.get(position))) {
            holder.mImage.setImageResource(R.drawable.default_head);
        } else {
//        	aQuery.id(holder.mImage).image(mIconIDs.get(position), true, true,0,R.drawable.default_head);
            holder.mImage = (RoundRectImageView) convertView.findViewById(R.id.img_list_item);
            holder.mImage.setImageResource(R.drawable.default_head);
//            DisplayImageOptions options =
            PhotoUtils.getImageLoader().displayImage(mIconIDs.get(position), holder.mImage, null, null);
            // PhotoUtils.showCard(PhotoUtils.UriType.HTTP,mIconIDs.get(position),holder.mImage, MyDisplayImageOption.options);
        }
        return convertView;
    }

    private static class ViewHolder {
        private RoundRectImageView mImage;
    }
}