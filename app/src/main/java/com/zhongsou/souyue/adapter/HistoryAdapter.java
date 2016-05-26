package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.StringUtils;

public class HistoryAdapter extends SouyueAdapter {

	public HistoryAdapter(Context context) {
		super(context);
		setMaxCount(1);
	} 
 
	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		convertView = inflateView(R.layout.list_item_history);
		holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
		holder.date = (TextView) convertView.findViewById(R.id.tv_item_date);
		holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		convertView.setTag(holder);
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		holder.searchResultItem=datas.get(position);
		holder.title.setText(datas.get(position).title());
		if(!TextUtils.isEmpty(datas.get(position).description()))
			holder.desc.setText(replaceAllNR(datas.get(position).description()));
		else
			holder.desc.setVisibility(View.GONE);
		holder.date.setText(StringUtils.convertDateToYMD(datas.get(position).date()));
		
	}
	
	
}