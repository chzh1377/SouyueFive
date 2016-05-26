package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.InterestMemberInfo;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareToFriendAdapter extends BaseAdapter {
    public static Map<Long, Boolean> selected = new HashMap<Long, Boolean>();
    private List<InterestMemberInfo> data;
//    private AQuery aQuery;
    private Context mContext;
    public ShareToFriendAdapter(Context context,List<InterestMemberInfo> data) {
    	mContext = context;
    	this.data = data;
//        aQuery = new AQuery(context);
    }

    @Override
    public int getCount() {
       return data.size();
    }

    @Override
    public Object getItem(int position) {
        if (data.size() > position)
            return data.get(position);
          else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final InterestMemberInfo item = (InterestMemberInfo) getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.circle_share_friend_list_view_row, null);
                holder = new ViewHolder();
                holder.ivImage = (ImageView) convertView.findViewById(R.id.row_iv_image);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.row_tv_title);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (TextUtils.isEmpty(item.getInterest_logo())) {
                holder.ivImage.setImageResource(R.drawable.im_friendsicon);
            } else {
                //aQuery.id(holder.ivImage).image(item.getInterest_logo(), true, true, 0, R.drawable.im_friendsicon);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP,item.getInterest_logo(),holder.ivImage, MyDisplayImageOption.getOptions(R.drawable.im_friendsicon));
            }
            holder.tvTitle.setText(item.getInterest_name());
            if(selected.get(item.getInterest_id())!=null && selected.get(item.getInterest_id())) {
            	holder.checkBox.setChecked(true);
            } else {
            	holder.checkBox.setChecked(false);
            }
        return convertView;
    }

    public static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        public CheckBox checkBox;
    }

}
