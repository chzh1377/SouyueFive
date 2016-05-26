package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.StringUtils;

public class CommonAdapter extends SouyueAdapter {

	

	private Context context;

	public CommonAdapter(Context context) {
		super(context);
		setMaxCount(1);
		this.context = context;

	}

	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		switch (getItemViewType(position)) {
		default:
			convertView = inflateView(R.layout.list_item_common);

			holder.iv_item = (ImageView) convertView
					.findViewById(R.id.iv_item_img);
			LinearLayout.LayoutParams params01 = (LinearLayout.LayoutParams) holder.iv_item
					.getLayoutParams();
			params01.width = width08;
			params01.height = height08;
			params01.setMargins(DeviceUtil.dip2px(context, 20), 0, 0, 0);
			holder.iv_item.setLayoutParams(params01);
			holder.date = (TextView) convertView.findViewById(R.id.item_date);
			holder.source = (TextView) convertView
					.findViewById(R.id.item_source);
			holder.title = (TextView) convertView
					.findViewById(R.id.tv_item_title);
			holder.desc = (TextView) convertView
					.findViewById(R.id.tv_item_description);
			holder.desc.setMaxLines(2);
			holder.searchResultItem = datas.get(position);
			//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
			//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
			convertView.setTag(holder);
			break;

		}
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		holder.searchResultItem = datas.get(position);
		if (datas.get(position).image().size() > 0) {
			holder.descTxt = StringUtils.truncate(
					replaceAllNR(datas.get(position).description()),
					StringUtils.LENGTH_46);
//			aq.id(holder.iv_item).image(datas.get(position).image().get(0),
//					true, true);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item, MyDisplayImageOption.smalloptions);
			holder.iv_item.setVisibility(View.VISIBLE);
		} else {
			holder.descTxt = StringUtils.truncate(
					replaceAllNR(datas.get(position).description()),
					StringUtils.LENGTH_60);
			holder.iv_item.setVisibility(View.GONE);
		}
		//尝试修复bug SYFX-2585。判断日期是否为,"0", 如果是0也认为是空的
		if (!TextUtils.isEmpty(datas.get(position).date()) && !datas.get(position).date().equals("0")) {
			holder.date.setText(StringUtils.convertDate(datas.get(position)
					.date()));
		} else {
			holder.date.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(datas.get(position).source())) {
			holder.source.setText(datas.get(position).source());
		} else {
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