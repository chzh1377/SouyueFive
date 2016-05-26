package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.Notice;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;

public class MsgSysPushHistoryListAdapter extends BaseAdapter {

	private List<Notice> news;
	private Context context;


	public MsgSysPushHistoryListAdapter(Context context, List<Notice> news) {
		this.context = context;
		this.news = news;
	}

	public int getCount() {
		return news.size();
	}

	public Object getItem(int position) {
		return news.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if (convertView == null) {
			view = View.inflate(context, R.layout.msgpush_history_item, null);
			holder = new ViewHolder();
			holder.tv_title = (TextView) view
					.findViewById(R.id.tv_msgpush_history_item_title);
			holder.tv_content = (TextView) view
					.findViewById(R.id.tv_msgpush_history_item_content);
			holder.tv_time = (TextView) view
					.findViewById(R.id.tv_msgpush_history_item_time);
			view.setTag(holder);

		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}

		Notice n = news.get(position);
		holder.tv_title.setText(n.title());
		holder.tv_content.setText(n.content());
		holder.tv_content.setMaxLines(2);
		holder.tv_time.setText(StringUtils.convertDate(n.date()+""));
		holder.notice = n;

		return view;
	}

	public static class ViewHolder {
		TextView tv_title;
		TextView tv_content;
		TextView tv_time;
		public Notice notice;
	}

	public void addItem(Notice item) {
		news.add(item);
	}
	public void clearNotices() {
		if (this.news != null) {
			this.news.clear();
		}
	}
	
}
