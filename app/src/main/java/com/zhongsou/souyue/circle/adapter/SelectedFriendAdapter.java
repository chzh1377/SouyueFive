package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.CircleMemberItem;

import java.util.ArrayList;

public class SelectedFriendAdapter extends BaseAdapter {

    private ArrayList<CircleMemberItem> selMembers;
	private Context context;

	public SelectedFriendAdapter(Context context, ArrayList<CircleMemberItem> selMembers) {
		this.context = context;
		this.selMembers = selMembers;
	}

	public int getCount() {
		return selMembers.size();
	}

	public Object getItem(int position) {
		return selMembers.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.circle_selected_friend_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (selMembers != null && selMembers.size() > 0) {
            CircleMemberItem item = selMembers.get(position);
            holder.name.setText(item.getNickname());
		}
		return convertView;
	}


	public static class ViewHolder {
		TextView name;
	}

}
