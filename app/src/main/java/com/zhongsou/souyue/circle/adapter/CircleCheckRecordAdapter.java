package com.zhongsou.souyue.circle.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.model.CircleCheckRecord;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;
import com.zhongsou.souyue.utils.FmtTimestamp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zoulu 2014年7月18日 上午11:42:41 类说明 :查看圈子申请记录adapter
 */
public class CircleCheckRecordAdapter extends BaseAdapter {
	private Context mContext;
	private List<CircleCheckRecord> checkRecords = new ArrayList<CircleCheckRecord>();
//	private ImageOptions options;
//	private AQuery aQuery;

	public CircleCheckRecordAdapter(Context context) {
		this.mContext = context;
//		options = new ImageOptions();
//		aQuery = new AQuery(context);
	}

	public void setList(List<CircleCheckRecord> checkRecords) {
		this.checkRecords = checkRecords;
	}

	public void addMoreList(List<CircleCheckRecord> list) {
		this.checkRecords.addAll(list);
		if (checkRecords.size() > 0) {
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return checkRecords.size();
	}

	@Override
	public CircleCheckRecord getItem(int arg0) {
		// TODO Auto-generated method stub
		return checkRecords.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.circle_check_record_item, null);
			holder = new ViewHolder();
			holder.circle_logo = (ImageView) convertView
					.findViewById(R.id.circle_logo);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.nickname);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.tv_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//aQuery.id(holder.circle_logo).image(
			//	checkRecords.get(position).getInterest_logo(), options);
		PhotoUtils.showCard(UriType.HTTP, checkRecords.get(position).getInterest_logo(),holder.circle_logo);
		holder.nickname.setText(checkRecords.get(position).getInterest_name());

		holder.content.setText("申请时间:"
				+ FmtTimestamp.fmtTimestamp("yyyy-MM-dd HH:mm:ss", checkRecords
						.get(position).getCreate_time(), true));
		switch (checkRecords.get(position).getAudit_status()) {
		case 1:// 1:待审核
			holder.tv_status.setText(Html
					.fromHtml("<font color=#54B4E6>待审核</font> "));
			break;
		case 2:// 2：已拒绝
			holder.tv_status.setText(Html
					.fromHtml("<font color=#FF7D7F>已拒绝</font> "));
			break;
		case 3:// 3：已通过
			holder.tv_status.setText(Html
					.fromHtml("<font color=#D2D2D2>已通过</font> "));
			break;
		default:
			break;
		}
		return convertView;
	}

	public static class ViewHolder {
		ImageView circle_logo;
		TextView nickname;
		TextView content;
		TextView tv_status;
	}
}
