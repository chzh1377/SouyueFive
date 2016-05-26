package com.zhongsou.souyue.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.module.ThumbnailBean;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.im.util.PhotoUtils.UriType;

import java.util.List;

/**
 * 详情页 图片适配器
 * 
 * @author wangqiang 15/4/21
 * 
 */
public class IMExpressionDetaiAdapter extends BaseAdapter {

	private List<ThumbnailBean> mThumbnails;
	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public IMExpressionDetaiAdapter(Context context,
			List<ThumbnailBean> thumbnails) {
		this.mContext = context;
		this.mThumbnails = thumbnails;
		this.mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mThumbnails != null ? mThumbnails.size() : 0;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.gif_item, null);
			viewHolder.iv_face = (ImageView) convertView
					.findViewById(R.id.imageview);
			viewHolder.iv_name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ThumbnailBean thumbnail = mThumbnails.get(position);
		// 设置真实名称
		viewHolder.iv_name.setText(thumbnail.getRealName());
		// 设置图片
		PhotoUtils.showCard(UriType.HTTP, thumbnail.getThumbnailUrl(),
				viewHolder.iv_face);

		return convertView;
	}

	public static class ViewHolder {
		private ImageView iv_face;
		private TextView iv_name;
	}

}
