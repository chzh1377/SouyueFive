package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;

public class PictureAdapter extends SouyueAdapter {

    public PictureAdapter(Context context) {
        super(context);
        setMaxCount(1);
    }

    @Override
    View getCurrentView(int position, View convertView, ViewHolder holder) {
        switch (getItemViewType(position)) {
            default:
                convertView = inflateView(R.layout.list_item_imgnav);
                holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_item_img);
                holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
                convertView.setTag(holder);
                break;

        }
        //super.setFontSize(holder.title);
        return convertView;
    }


    @Override
    void setViewData(int position, ViewHolder holder) {
    	//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        holder.searchResultItem = datas.get(position);
        if (datas.get(position).image().size() > 0) {
            //aq.id(holder.iv_item).image(datas.get(position).image().get(0), true, true, 0, 0, null, AQuery.FADE_IN);
            PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item, MyDisplayImageOption.smalloptions);

        }
        if (holder.title != null) {
            holder.title.setText(datas.get(position).title());
        }
        holder.searchResultItem = datas.get(position);
    }
}