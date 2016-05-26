package com.zhongsou.souyue.pop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.ConstantsUtils;

public class PopOneself {
	public static final int GROUP_ALL = ConstantsUtils.TYPE_ALL;
	public static final int GROUP_WEIBO = ConstantsUtils.TYPE_WEIBO_SEARCH;
	public static final int GROUP_BLOG = ConstantsUtils.TYPE_BLOG_SEARCH;
	public static final int GROUP_BBS = ConstantsUtils.TYPE_BBS_SEARCH;
	private PopupWindow popupWindow;
	private ListView lv_group;
	private View view;
	private PopOneselfAdapter popAdapter;
	public PopOneself(final Context context ,boolean isfromFriend) {
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = layoutInflater.inflate(R.layout.self_create_group_menu, null);
		}
		popAdapter = new PopOneselfAdapter(context,isfromFriend);
		lv_group = (ListView) view.findViewById(R.id.lvGroup);
		lv_group.setAdapter(popAdapter);
		popAdapter.notifyDataSetChanged();
		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
	}

	class PopOneselfAdapter extends BaseAdapter {
		private final String[] mTitles;
		private final int[] mUrls;
		private final LayoutInflater mInflater;

		public PopOneselfAdapter(Context context,boolean isfromFriend) {
			mInflater = LayoutInflater.from(context);
			final Resources res = context.getResources();
			if(isfromFriend){
				mTitles = res.getStringArray(R.array.friend_group_name);
				mUrls = res.getIntArray(R.array.friend_group_links);
			}else{
				mTitles = res.getStringArray(R.array.group_name);
				mUrls = res.getIntArray(R.array.group_links);
			}
		}

		@Override
		public int getCount() {
			return mUrls.length;
		}

		@Override
		public Integer getItem(int position) {
			return mUrls[position];
		}
		
		public String getItemText(int position){
			return mTitles[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OneSelfViewHolder holder;
			holder = new OneSelfViewHolder();
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.self_create_group_menu_item, parent, false);
				holder.group_name = (TextView) convertView.findViewById(R.id.group_name);
				convertView.setTag(holder);
			} else {
				holder = (OneSelfViewHolder) convertView.getTag();
			}
			holder.group_name.setText(mTitles[position]);
			return convertView;
		}

	}

	public void setOnItemClick(OnItemClickListener listener) {
		lv_group.setOnItemClickListener(listener);
	}

	private static class OneSelfViewHolder {
		TextView group_name;
	}

	public void showAsDropDown(View parent) {
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
//		int xPos = parent.getWidth()/2;
		popupWindow.showAsDropDown(parent, 0, 0);
		popupWindow.update();
	}

	public void dismiss() {
		if (popupWindow != null)
			popupWindow.dismiss();
	}
	
	public String getChangeTitle(int pos){
		if (null != popAdapter)
			return popAdapter.getItemText(pos);
		else 
			return "全部";
	}
	
	public int getChangeLink(int pos){
		if (null != popAdapter)
			return popAdapter.getItem(pos);
		else 
			return 0;
	}
}
