package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.module.SearchResult;
import com.zhongsou.souyue.utils.StringUtils;

public class ForumAdapter extends SouyueAdapter {

	public ForumAdapter(Context context) {
		super(context);
		setMaxCount(2);
	}

	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		if (getItemViewType(position) == SearchResult.NEWS_TYPE_IMAGE_THREE) {
			convertView = inflateView(R.layout.list_item_common_images);
			holder.iv_item_1 = (ImageView) convertView.findViewById(R.id.iv_item_img_1);
			holder.iv_item_2 = (ImageView) convertView.findViewById(R.id.iv_item_img_2);
			holder.iv_item_3 = (ImageView) convertView.findViewById(R.id.iv_item_img_3);
		} else {
			convertView = inflateView(R.layout.list_item_bbs);
			holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
			holder.desc.setMaxLines(2);
			holder.iv_item = (ImageView) convertView.findViewById(R.id.iv_item_img);
		}
		holder.source = (TextView) convertView.findViewById(R.id.item_source);
		holder.date = (TextView) convertView.findViewById(R.id.item_date);
		holder.isSelfCreate = (TextView) convertView.findViewById(R.id.tv_is_self_create);
		holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
		convertView.setTag(holder);
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		holder.title.setText(datas.get(position).title());
		if (StringUtils.isEmpty(datas.get(position).date()) || "0".equals(datas.get(position).date())) {
			holder.date.setVisibility(View.GONE);
		} else {
			holder.date.setVisibility(View.VISIBLE);
			holder.date.setText(StringUtils.convertDate(datas.get(position).date()));
		}
		holder.searchResultItem = datas.get(position);
		if (datas.get(position).isOriginal() == 1){
			holder.isSelfCreate.setVisibility(View.VISIBLE);
			if (null != holder.iv_item)
				holder.iv_item.setScaleType(ScaleType.CENTER_CROP);
			holder.source.setVisibility(View.GONE);
		}
		else{
			holder.isSelfCreate.setVisibility(View.GONE);
			if (null != holder.iv_item)
				holder.iv_item.setScaleType(ScaleType.FIT_XY);
		}
		if (getItemViewType(position) == SearchResult.NEWS_TYPE_IMAGE_THREE) {
			holder.source.setText(datas.get(position).source());
//			getImageByAquery(holder.iv_item_1, datas.get(position).image().get(0));
//			getImageByAquery(holder.iv_item_2, datas.get(position).image().get(1));
//			getImageByAquery(holder.iv_item_3, datas.get(position).image().get(2));
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item_1, MyDisplayImageOption.smalloptions);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(1), holder.iv_item_2, MyDisplayImageOption.smalloptions);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(2), holder.iv_item_3, MyDisplayImageOption.smalloptions);
		} else {
			if (datas.get(position).image().size() > 0&&isImgAble()) {
				holder.descTxt = StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_46);
				//getImageByAquery(holder.iv_item, datas.get(position).image().get(0));
				PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item, MyDisplayImageOption.smalloptions);
				holder.iv_item.setVisibility(View.VISIBLE);
			} else{
				holder.descTxt = StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_60);
				holder.iv_item.setVisibility(View.GONE);
			}
            if (holder.desc != null)
			    holder.desc.setText(holder.descTxt);
		}
		updateItemColor(holder);
	}
}