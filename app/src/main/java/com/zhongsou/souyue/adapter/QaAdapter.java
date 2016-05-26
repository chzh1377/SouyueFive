package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.StringUtils;

public class QaAdapter extends SouyueAdapter {

	public boolean isRefresh = false;
	public QaAdapter(Context context) {
		super(context);
		setMaxCount(1);
	}
 
	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		convertView = inflateView(R.layout.list_item_q_a);
		holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
		holder.answercount=(TextView) convertView.findViewById(R.id.tv_item_answer_count);
		holder.sameaskcount=(TextView) convertView.findViewById(R.id.tv_item_sameask_count);
		holder.date=(TextView) convertView.findViewById(R.id.tv_item_date);
		
		convertView.setTag(holder);
		//super.setFontSize(holder.title);
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		holder.searchResultItem=datas.get(position);
		holder.desc.setText(replaceAllNR(datas.get(position).description()));
		holder.answercount.setText(datas.get(position).answerCount()+"");
		holder.sameaskcount.setText(datas.get(position).sameAskCount()+"");
		holder.date.setText(StringUtils.convertDate(datas.get(position).date()));
		
	}
}