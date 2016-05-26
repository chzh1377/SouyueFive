package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.StringUtils;

public class WebAdapter extends SouyueAdapter {

	public WebAdapter(Context context) {
		super(context);
		setMaxCount(1);
	}
 
	@Override
	View getCurrentView(int position, View convertView,ViewHolder holder) {
		switch (getItemViewType(position)) {
		default:
			convertView = inflateView(R.layout.list_item_common);
			holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_item_img);
			holder.date = (TextView) convertView.findViewById(R.id.item_date);
			holder.source = (TextView) convertView.findViewById(R.id.item_source);
			holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
			holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
			holder.desc.setMaxLines(2);
			convertView.setTag(holder);
			break;

		}
		return convertView;
	}


	@Override
	void setViewData(int position, ViewHolder holder) {
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		holder.searchResultItem=datas.get(position);
		if(datas.get(position).image().size()>0){
			holder.descTxt = StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_46);
			//aq.id(holder.iv_item).image(datas.get(position).image().get(0), true, true, 0, 0, null, AQuery.FADE_IN);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item, MyDisplayImageOption.smalloptions);
			holder.iv_item.setVisibility(View.VISIBLE);
		}else{
			holder.descTxt = StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_60);
			holder.iv_item.setVisibility(View.GONE);
		}
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
		if (holder.desc != null) {
			holder.desc.setText(holder.descTxt);
		}
		if (holder.title != null) {
			holder.title.setText(datas.get(position).title());
		}
		updateItemColor(holder);
		
	}
}