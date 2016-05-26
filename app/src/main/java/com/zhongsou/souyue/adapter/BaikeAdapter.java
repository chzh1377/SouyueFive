package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.StringUtils;

public class BaikeAdapter extends SouyueAdapter {

	public BaikeAdapter(Context context) {
		super(context);
		setMaxCount(1);
	}
 
	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		convertView = inflateView(R.layout.list_item_baike);
		holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
		holder.date = (TextView) convertView.findViewById(R.id.tv_item_date);
		
		//super.setFontSize(holder.title);
		convertView.setTag(holder);
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		holder.title.setText(datas.get(position).title());
		holder.date.setText(StringUtils.convertDate(datas.get(position).date()));
		holder.searchResultItem=datas.get(position);
	}
}