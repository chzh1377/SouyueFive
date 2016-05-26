package com.zhongsou.souyue.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.AdListItem;

import java.util.ArrayList;
import java.util.List;

public class AdAdapter extends BaseAdapter {
	Activity activity;
	public AdAdapter(Activity activity) {
		this.activity=activity;
	}
	
	private List<AdListItem> list=new ArrayList<AdListItem>();
	public void setDatas(List<AdListItem>  items){
		list =items;
	}
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (list.size() > 0 && list.size() - 1 >= position) {
			return list.get(position).category();
		} else
			return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AdListItem adListItem = list.get(position);
		if (convertView == null) {
			if (getItemViewType(position) == 1) {
				convertView = View.inflate(activity,
						R.layout.list_item_ad_pic, null);
			} else if (getItemViewType(position) == 2) {
				convertView = View.inflate(activity,
						R.layout.list_item_ad_nopic, null);
			}
		}

		if (getItemViewType(position) == 1) {
			ImageView image = (ImageView) convertView
					.findViewById(R.id.iv_image);
			TextView title = (TextView) convertView
					.findViewById(R.id.tv_title);
			//aq.id(image).image(adListItem.image(), true, true);
                        PhotoUtils.showCard(PhotoUtils.UriType.HTTP,adListItem.image(),image);
			title.setText(adListItem.title());
		} else {
			TextView title = (TextView) convertView
					.findViewById(R.id.tv_title);
			TextView description = (TextView) convertView
					.findViewById(R.id.tv_description);
			title.setText(adListItem.title());
			description.setText(adListItem.description());
		}
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (TextUtils.isEmpty(adListItem.url()))
					return;
				Intent intent = new Intent();
				intent.setClass(activity, WebSrcViewActivity.class);
				intent.putExtra(WebSrcViewActivity.PAGE_URL,
						adListItem.url());
				activity.startActivity(intent);
				activity.overridePendingTransition(R.anim.left_in,
						R.anim.left_out);
			}
		});
		return convertView;
	}

}
