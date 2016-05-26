package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.SearchResultItem;

/**
 * 网址导航
 */
public class WebNavAdapter extends SouyueAdapter {

    public WebNavAdapter(Context context) {
        super(context);
        setMaxCount(1);
    }

    @Override
    View getCurrentView(int position, View convertView, ViewHolder holder) {
        switch (getItemViewType(position)) {
            default:
                convertView = inflateView(R.layout.list_item_webnav);
                holder.title = (TextView) convertView.findViewById(R.id.tv_official);
                holder.desc = (TextView) convertView.findViewById(R.id.tv_official_url);
                convertView.setTag(holder);
                break;
        }
        //super.setFontSize(holder.title);
        return convertView;
    }


    @Override
    void setViewData(int position, ViewHolder holder) {
        SearchResultItem sri = datas.get(position);
       // float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        if (sri != null) {
            if (holder.title != null) {
                holder.title.setText(sri.title() != null ? sri.title() : "");
            }
            if (sri.isOfficialWebsite()){
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.officialwebsite);
                if (drawable != null)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.title.setCompoundDrawables(null, null, drawable, null);
            } else {
                holder.title.setCompoundDrawables(null, null, null, null);
            }
            if (holder.desc != null){
                holder.desc.setText(sri.url());
            }
        }
        holder.searchResultItem = datas.get(position);
    }
}