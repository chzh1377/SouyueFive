package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.share.ShareType;
import com.zhongsou.souyue.share.ShareTypeHelper;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.List;
public class CircleShareAdapter extends BaseAdapter {
	private String[] mTitles;
	private int[] mIds;
	private int[] mIcons;
	private final LayoutInflater mInflater;
	private List<ShareType> shareTypes;
	private Context context;
	/**
	 * @param context
	 * @param isPrime 如果是精华区，则没有精华区分享功能 
	 */
	public CircleShareAdapter(Context context, boolean isPrime) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		if (!isPrime) {
			if (StringUtils.isNotEmpty(ShareApi.WEIXIN_APP_ID)) {
				//				if (ConfigApi.isSouyue()) {
				//					shareTypes = ShareTypeHelper.without搜悦网友And网友推荐区();//circle_share_names
				//				} else {
				shareTypes = ShareTypeHelper.without搜悦网友And网友推荐区();//share_names_rss_weixin
				//				}
			} else {
				shareTypes = ShareTypeHelper.without搜悦网友And网友推荐区And腾讯();//nowx_circle_share_names
			}
		} else {
			if (StringUtils.isNotEmpty(ShareApi.WEIXIN_APP_ID)) {
				if (ConfigApi.isSouyue()) {
					shareTypes = ShareTypeHelper.circle_share_names_no_digest();//circle_share_names_no_digest
				} else {
					shareTypes = ShareTypeHelper.nowx_circle_share_names_no_digest_weixin();//nowx_circle_share_names_no_digest_weixin
				}
			} else {
				shareTypes = ShareTypeHelper.nowx_circle_share_names_no_digest();//nowx_circle_share_names_no_digest
			}
		}
		initFromShareTypes();
	}
	private void initFromShareTypes() {
		if (this.shareTypes != null) {
			this.mTitles = ShareTypeHelper.getTitles(shareTypes);
			this.mIcons = ShareTypeHelper.getDrawables(shareTypes);
			this.mIds = ShareTypeHelper.getIds(shareTypes);
		}
	}
	public CircleShareAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		//		if (ConfigApi.isSouyue()) {
		//			shareTypes = ShareTypeHelper.has微信And微博And邮件();//re_share_names_code
		//		} else {
		shareTypes = ShareTypeHelper.has微信And微博And邮件();//re_share_names_code_weixin
		//		}
		initFromShareTypes();
	}
	@Override
	public int getCount() {
		return mIds.length;
	}
	@SuppressWarnings("boxing")
	@Override
	public Integer getItem(int position) {
		return mIds[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.sharemenu_item, parent, false);
			holder.share_way = (TextView) convertView.findViewById(R.id.share_way);
			holder.share_icon = (ImageView) convertView.findViewById(R.id.share_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.share_way.setText(mTitles[position]);
		holder.share_icon.setImageDrawable(CircleShareAdapter.this.context.getResources().getDrawable(mIcons[position]));
		return convertView;
	}
	private class ViewHolder {
		TextView share_way;
		ImageView share_icon;
	}
}
