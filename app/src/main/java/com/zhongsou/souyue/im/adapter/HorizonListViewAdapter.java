package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.ExpressionTab;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;
import java.util.List;

public class HorizonListViewAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private int selectedIndex = 0;
	private List<ExpressionTab> tabs;

	public HorizonListViewAdapter(Context context,
			List<ExpressionTab> expressionTabs) {
		this.mContext = context;
		this.tabs = expressionTabs;
		this.mLayoutInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (tabs != null) {
			return 2 + tabs.size();
		} else {
			return 2;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 添加tab
	 * 
	 * @param tab
	 */
	public void addItem(ExpressionTab tab) {
		if (tab != null)
			tabs.add(tab);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = this.mLayoutInflater.inflate(
					R.layout.im_emoji_hlv_item, null);
			viewHolder.iv_icon = ((ImageView) convertView
					.findViewById(R.id.imageview));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// 设置data
		if (position > 0 && position < getCount() - 1) {
			ExpressionTab tab = tabs.get(position - 1);
			Bitmap bm = BitmapUtil.convertToBitmap(mContext.getFilesDir()+Constants.PACKAGE_DOWNURL
					+ File.separator + SYUserManager.getInstance().getUserId()
					+ File.separator + tab.getFileName() + File.separator
					+ "icon.png", 112, 84);
			viewHolder.iv_icon.setImageBitmap(bm);
		} else if (position == 0) {
			viewHolder.iv_icon.setImageResource(R.drawable.bottom_nav_emoji);
		} else if (position == getCount() - 1) {
			viewHolder.iv_icon.setImageResource(R.drawable.tab_setting_icon1);
		}

		if (selectedIndex == position) {
			viewHolder.iv_icon.setBackgroundColor(Color.parseColor("#B3B3B3"));
		} else {
			viewHolder.iv_icon.setBackgroundColor(Color.parseColor("#ffffff"));
		}

		return convertView;

	}

	public void setIndexSelected(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public static class ViewHolder {
		ImageView iv_icon;
	}

}
