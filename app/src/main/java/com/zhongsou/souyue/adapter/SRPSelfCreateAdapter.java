package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.StringUtils;

public class SRPSelfCreateAdapter extends SouyueAdapter {

	private static final int TYPE_ITEM_PICS = 3;
	private static final int TYPE_ITEM_PIC_TWO = 2;
	private static final int TYPE_ITEM_PIC = 1;
	private static final int TYPE_ITEM_PIC_NO = 0;
	
	public SRPSelfCreateAdapter(Context context) {
		super(context);
		setMaxCount(3);
	}

	@Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		switch (getItemViewType(position)) {
		case TYPE_ITEM_PICS:
			convertView = inflateView(R.layout.list_item_srpselfcreate_pics);
			holder.iv_item_1 = (ImageView) convertView.findViewById(R.id.iv_item_img_1);
			holder.iv_item_2 = (ImageView) convertView.findViewById(R.id.iv_item_img_2);
			holder.iv_item_3 = (ImageView) convertView.findViewById(R.id.iv_item_img_3);
			break;
		case TYPE_ITEM_PIC_TWO:
			convertView = inflateView(R.layout.list_item_srpselfcreate_pic_two);
			holder.iv_item_1 = (ImageView) convertView.findViewById(R.id.iv_item_img_1);
			holder.iv_item_2 = (ImageView) convertView.findViewById(R.id.iv_item_img_2);
			break;
		case TYPE_ITEM_PIC:
			convertView = inflateView(R.layout.list_item_srpselfcreate_pic);
			holder.iv_item_1 = (ImageView) convertView.findViewById(R.id.iv_item_img_1);
			break;
		default :
			convertView = inflateView(R.layout.list_item_srpselfcreate_nopic);
			  break;
		}
		holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		holder.date = (TextView) convertView.findViewById(R.id.item_date);
		holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
//		holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
		holder.answercount = (TextView) convertView.findViewById(R.id.tv_fav_count);
		holder.sameaskcount = (TextView) convertView.findViewById(R.id.tv_comment_count);
		holder.iv_on_top = (ImageView) convertView.findViewById(R.id.iv_on_top);
		holder.iv_jing = (ImageView) convertView.findViewById(R.id.iv_jing);
		
		//super.set(holder.title);
		
		return convertView;
	}

	@Override
	void setViewData(int position, ViewHolder holder) {
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		int id = getItemViewType(position);
		if (id == TYPE_ITEM_PIC && holder.iv_item_1 != null) {
//			aq.id(holder.iv_pic).image(datas.get(position).image().get(0),true, true, 0, 0, null, AQuery.FADE_IN);
			//getImageByAquery(holder.iv_item_1, datas.get(position).image().get(0));
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item_1, MyDisplayImageOption.smalloptions);
		}
		if(id == TYPE_ITEM_PIC_TWO){
//			getImageByAquery(holder.iv_item_1, datas.get(position).image().get(0));
//			getImageByAquery(holder.iv_item_2, datas.get(position).image().get(1));
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item_1, MyDisplayImageOption.smalloptions);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(1), holder.iv_item_2, MyDisplayImageOption.smalloptions);
		}
		if(id==TYPE_ITEM_PICS){
//			getImageByAquery(holder.iv_item_1, datas.get(position).image().get(0));
//			getImageByAquery(holder.iv_item_2, datas.get(position).image().get(1));
//			getImageByAquery(holder.iv_item_3, datas.get(position).image().get(2));
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_item_1, MyDisplayImageOption.smalloptions);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(1), holder.iv_item_2, MyDisplayImageOption.smalloptions);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(2), holder.iv_item_3, MyDisplayImageOption.smalloptions);

		}
		if (id == TYPE_ITEM_PIC_NO){
			holder.descTxt = StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_60);
		} else {
			holder.descTxt = StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_46);
		}
//		if (holder.desc!=null) {
//			holder.desc.setText(holder.descTxt);
//			if(id==TYPE_ITEM_PICS){
//				holder.desc.setVisibility(View.GONE);
//			}else{
//				holder.desc.setVisibility(View.VISIBLE);
//			}
//		}
		if (!TextUtils.isEmpty(datas.get(position).date())) {
			holder.date.setText(StringUtils.convertDate(datas.get(position).date()));
		} else {
			holder.date.setVisibility(View.GONE);
		}
		if (datas.get(position).isEssence()){
			holder.iv_jing.setVisibility(View.VISIBLE);
		}else{
			holder.iv_jing.setVisibility(View.GONE);
		}
		if (datas.get(position).ding()){
			holder.iv_on_top.setVisibility(View.VISIBLE);
		}else{
			holder.iv_on_top.setVisibility(View.GONE);
		}
		if (holder.title!=null) {
			holder.title.setText(datas.get(position).title());
		}  	
		if (datas.get(position).isOriginal() == 1){
			if (holder.iv_pic != null)
				holder.iv_pic.setScaleType(ScaleType.CENTER_CROP);
		}
		else{
			if (holder.iv_pic != null)
				holder.iv_pic.setScaleType(ScaleType.FIT_XY);
		}
		holder.answercount.setText(datas.get(position).upCount() + "");
		holder.sameaskcount.setText(datas.get(position).commentCount() + "");
		holder.tv_name.setText(datas.get(position).userNick() + "");
		holder.searchResultItem = datas.get(position);
		updateItemColor(holder);
	}
	
}
