package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.StringUtils;

public class RecommendAdapter extends SouyueAdapter {
	private static final int TYPE_ITEM_PICS = 3;
	private static final int TYPE_ITEM_PIC = 1;
	private static final int TYPE_ITEM_PIC_NO = 0;
	private String category;
	public RecommendAdapter(Context context) {
		super(context);
		setMaxCount(3);
	}
	
	public void setCategory(String category) {
        this.category = category;
    }


    @Override
	View getCurrentView(int position, View convertView, ViewHolder holder) {
		switch (getItemViewType(position)) {
		case TYPE_ITEM_PICS:
			convertView = inflateView(R.layout.list_item_recommend_pics);
			if(mContext.getString(R.string.ranking).equals(category)){
			    getRankTitle(convertView,holder);
			}
			holder.iv_item_1 = (ImageView) convertView.findViewById(R.id.iv_item_img_1);
			holder.iv_item_2 = (ImageView) convertView.findViewById(R.id.iv_item_img_2);
			holder.iv_item_3 = (ImageView) convertView.findViewById(R.id.iv_item_img_3);
			break;
		case TYPE_ITEM_PIC:
			convertView = inflateView(R.layout.list_item_recommend_pic);
			 if(mContext.getString(R.string.ranking).equals(category)){
	                getRankTitle(convertView,holder);
	            }
			holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
			break;
		default :
			convertView = inflateView(R.layout.list_item_recommend_nopic);
			  if(mContext.getString(R.string.ranking).equals(category)){
	                getRankTitle(convertView,holder);
	            }
			  break;
		}
		holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
		holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
		holder.answercount = (TextView) convertView.findViewById(R.id.tv_fav_count);
		holder.sameaskcount = (TextView) convertView.findViewById(R.id.tv_comment_count);
		return convertView;
	}

	private void getRankTitle(View convertView,ViewHolder holder) {
	    ViewStub viewstub= (ViewStub)convertView.findViewById(R.id.rank_title);
        View view=viewstub.inflate();
        holder.week_rank_text=(TextView) view.findViewById(R.id.week_rank_text);
        holder.month_rank_text=(TextView) view.findViewById(R.id.month_rank_text);
        holder.score_text=(TextView) view.findViewById(R.id.score_text);
        holder.pubish_time=(TextView) view.findViewById(R.id.publish_time_text);
    }


    void setViewData(int position, ViewHolder holder) {
		int id = getItemViewType(position);
		//float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		if (id == TYPE_ITEM_PIC && holder.iv_pic != null) {
			//aq.id(holder.iv_pic).image(datas.get(position).image().get(0),true, true, 0, 0, null, AQuery.FADE_IN);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_pic, MyDisplayImageOption.smalloptions);
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
		if (holder.desc!=null) {
			holder.desc.setText(holder.descTxt);
			if(id==TYPE_ITEM_PICS){
				holder.desc.setVisibility(View.GONE);
			}else{
				holder.desc.setVisibility(View.VISIBLE);
			}
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
		if(mContext.getString(R.string.ranking).equals(category)){
		    holder.week_rank_text.setText(datas.get(position).wrank()+"");
	        holder.month_rank_text.setText(datas.get(position).mrank()+"");
	        holder.score_text.setText(datas.get(position).score()+"");
	        holder.pubish_time.setText(StringUtils.convertDate(datas.get(position).date()));
		}
		updateItemColor(holder);
	}
}