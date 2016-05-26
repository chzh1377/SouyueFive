package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.StringUtils;

public class MingYanAdapter extends SouyueAdapter {

	public MingYanAdapter(Context context) {
		super(context);
		setMaxCount(1);
	}
 

	@Override
	View getCurrentView(int position, View convertView,ViewHolder holder) {
		switch (getItemViewType(position)) {
		default:
			convertView = inflateView(R.layout.list_item_mingyan);
			holder.date = (TextView) convertView.findViewById(R.id.item_date);
			holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
			holder.source = (TextView) convertView.findViewById(R.id.item_source);
			holder.searchResultItem=datas.get(position);
			
			convertView.setTag(holder);
			break;

		}
		
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		return convertView;
	}


	@Override
	void setViewData(int position, ViewHolder holder) {
		holder.searchResultItem=datas.get(position);
		if (!TextUtils.isEmpty(datas.get(position).date() )) {
			holder.date.setText(StringUtils.convertDate(datas.get(position).date()));
		}else{
			holder.date.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(datas.get(position).source() )) {
			holder.source.setText(datas.get(position).source());
		}else{
			holder.source.setVisibility(View.GONE);
		}
		if(holder.date.getVisibility()==View.GONE&&holder.source.getVisibility()==View.GONE)
			((View)holder.date.getParent()).setVisibility(View.GONE);
		if (!TextUtils.isEmpty(datas.get(position).description() )) {
			holder.desc.setText(replaceAllNR(datas.get(position).description()));
		}else{
			holder.desc.setVisibility(View.GONE);
		}
		
	}
}