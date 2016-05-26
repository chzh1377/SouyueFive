package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.GroupKeywordItem;

import java.util.List;

public class SendNextGridAdapter extends BaseAdapter {

	private Context context;
	private int selected = 1000;
	private List<GroupKeywordItem> mykeywordlist;
	int mCount = 0;

	public SendNextGridAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return mykeywordlist == null ? 0 : mykeywordlist.size();
	}

	public void setdata(List<GroupKeywordItem> mykeywordlist) {
		this.mykeywordlist = mykeywordlist;
		notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mykeywordlist.get(position) != null) {
			return mykeywordlist.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setSelected(int position) {
		// TODO Auto-generated method stub
		selected = position;
		System.out.println(" setSelected " + selected);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_select_item, parent, false);
			viewholder.selected_text = (TextView) convertView.findViewById(R.id.selectgrid_text);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		if (selected == position) {
			// 点击的item项处理
			if (mykeywordlist.get(position).ischeck()) {
				viewholder.selected_text.setBackgroundResource(R.drawable.selfcreate_keyword_select);
			} else {
				viewholder.selected_text.setBackgroundResource(R.drawable.selfcreate_keyword_nomal);
			}
		} else {
			// 未被点击的项目处理
			if (mykeywordlist.get(position).ischeck()) {
				viewholder.selected_text.setBackgroundResource(R.drawable.selfcreate_keyword_select);
			} else {
				viewholder.selected_text.setBackgroundResource(R.drawable.selfcreate_keyword_nomal);
			}
		}

		viewholder.selected_text.setText(mykeywordlist.get(position).keyword());
		return convertView;

	}

	private class ViewHolder {
		public TextView selected_text;
	}

}
