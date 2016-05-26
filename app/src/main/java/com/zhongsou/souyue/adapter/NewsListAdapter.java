package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * common adapter
 * 
 * @author zhangliang01@zhongsou.com
 * 搜悦报刊详情界面adapter
 */
public class NewsListAdapter extends SouyueAdapter {

	private static final int TYPE_ITEM_FOCUS = -1;
	private static final int TYPE_ITEM_PIC = 1;
	private static final int TYPE_ITEM_FOCUS_NOPIC = -2;
	private static final int TYPE_ITEM_PIC_NO = 0;
	private static final int TYPE_ITEM_COUNT = 4;// item的数量 不包括公共的
//	public boolean showFocus = true;
	public boolean isRss = false;
	private Context context;
	public NewsListAdapter(Context context) {
		super(context);
		setMaxCount(TYPE_ITEM_COUNT);
		this.context = context;
	}

	@Override
    View getCurrentView(int position, View convertView, ViewHolder holder) {
        int id = getItemViewType(position);
//        if (!showFocus && id == TYPE_ITEM_FOCUS) // srp也不要焦点图i
//            datas.get(position).newsLayoutType_$eq(TYPE_ITEM_PIC);
        switch (id) {
            case TYPE_ITEM_FOCUS_NOPIC:
                convertView = inflateView(R.layout.focus_nopic_item);
                holder.iv_marked = (ImageView) convertView.findViewById(R.id.focus_noimg);
                holder.title = (TextView) convertView.findViewById(R.id.focus_noimg_txt);
//                holder.date = (TextView) convertView.findViewById(R.id.focus_date);
//                holder.source = (TextView) convertView.findViewById(R.id.focus_source);
                break;
            case TYPE_ITEM_FOCUS:
                convertView = inflateView(R.layout.fragment_rss_marked_item);
                holder.marked = (TextView) convertView.findViewById(R.id.item_text_marked);
                holder.iv_marked = (ImageView) convertView.findViewById(R.id.iamge_marked);
                break;
            case TYPE_ITEM_PIC:
                convertView = inflateView(R.layout.fragment_rss_pic_item);
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                
                RelativeLayout.LayoutParams params01 = (RelativeLayout.LayoutParams) holder.iv_pic
    					.getLayoutParams();
    			params01.width = width08;
    			params01.height = height08;
    			params01.setMargins(DeviceUtil.dip2px(context, 20), 0, 0, 0);
    			holder.iv_pic.setLayoutParams(params01);
                break;
            case TYPE_ITEM_PIC_NO:
                convertView = inflateView(R.layout.fragment_rss_list_item);
                break;
            default:
                convertView = inflateView(R.layout.fragment_rss_list_item);
                break;
        }
        if (TYPE_ITEM_PIC == id || TYPE_ITEM_PIC_NO == id) {
            holder.title = (TextView) convertView.findViewById(R.id.tv_item_title);
             View v= convertView.findViewById(R.id.tv_home_hot);
			if (v!=null){
				v.setVisibility(View.GONE);
			}
            holder.desc = (TextView) convertView.findViewById(R.id.tv_item_description);
            holder.desc.setMaxLines(2); holder.desc.setEllipsize(TextUtils.TruncateAt.END);
            holder.date = (TextView) convertView.findViewById(R.id.item_date);
            holder.source = (TextView) convertView.findViewById(R.id.item_source);
        }
        //float fontSize = SYSharedPreferences.getInstance().loadResFont(mContext);
		//holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        return convertView;
    }

	void setViewData(int position, ViewHolder holder) {
		int id = getItemViewType(position);
                    float fontSize = SYSharedPreferences.getInstance().loadResFont(context);
                   if(holder.title!=null) {
                       holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                   }
		if (id == TYPE_ITEM_FOCUS_NOPIC){
		    int color = ImageUtil.getColorByKey(datas.get(position).title());
		    holder.iv_marked.setBackgroundColor(MainApplication.getInstance().getResources().getColor(color));
		}
		if (id == TYPE_ITEM_FOCUS) {
			holder.iv_marked.setBackgroundResource(R.drawable.default_big);
			//aquery.id(holder.iv_marked).image(datas.get(position).image().get(0), true, true);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_marked, MyDisplayImageOption.smalloptions);
			holder.marked.setText(datas.get(position).title());
		}
		if (id == TYPE_ITEM_PIC && holder.iv_pic != null) {
			holder.iv_pic.setImageResource(R.drawable.default_small);
			//aquery.id(holder.iv_pic).image(datas.get(position).image().get(0), true, true);
			PhotoUtils.showCard(UriType.HTTP, datas.get(position).image().get(0), holder.iv_pic, MyDisplayImageOption.smalloptions);
			holder.descTxt =datas.get(position).description();// StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_46);
		} else {
			holder.descTxt =datas.get(position).description();// StringUtils.truncate(replaceAllNR(datas.get(position).description()), StringUtils.LENGTH_60);
		}
		if (holder.date != null)
			holder.date.setText(StringUtils.convertDate(datas.get(position).date()));
		if (holder.source != null)
			holder.source.setText(datas.get(position).source());
		if (holder.desc!=null) {
			holder.desc.setText(holder.descTxt);
		}
		if (holder.title!=null) {
				holder.title.setMaxLines(2);
			holder.title.setText(datas.get(position).title());
		}  
		holder.searchResultItem = datas.get(position);
		if (id != TYPE_ITEM_FOCUS_NOPIC)
		    updateItemColor(holder);
	}
}
