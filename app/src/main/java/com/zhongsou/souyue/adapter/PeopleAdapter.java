package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.SettingsManager;
import com.zhongsou.souyue.utils.StringUtils;

public class PeopleAdapter extends SouyueAdapter {

	public PeopleAdapter(Context context) {
		super(context);
		setMaxCount(1);
	}

	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		convertView = inflateView(R.layout.list_item_people);
		holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
		holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
		holder.desc.setMaxLines(2);
		holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_item_img);
		
		convertView.setTag(holder);
		super.setFontSize(holder.title);
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		holder.searchResultItem=datas.get(position);
		holder.title.setText(datas.get(position).title());
		holder.desc.setText(StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_60));

		if(SettingsManager.getInstance().isLoadImage()){
			holder.iv_item.setVisibility(View.VISIBLE);
			if(datas.get(position).image().size()>0){
				//aq.id(holder.iv_item).image(datas.get(position).image().get(0), true, true, 0, 0, null, AQuery.FADE_IN);
				PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item, MyDisplayImageOption.smalloptions);
				holder.iv_item.setVisibility(View.VISIBLE);
			}
		}else{
			holder.iv_item.setVisibility(View.GONE);
		}		
	}
}