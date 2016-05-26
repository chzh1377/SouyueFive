package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.activity.FirstLeaderActivity;
import com.zhongsou.souyue.module.GuideRecommendSRP;

import java.util.ArrayList;
import java.util.List;
/**
 * 新手引导页推荐栏目
 * @author chz
 */
public class FirstLeadAdapter extends BaseAdapter {
	
	private List<GuideRecommendSRP> datas = new ArrayList<GuideRecommendSRP>();
	private LayoutInflater layoutInflater;
	private Context mContext;
	
	public FirstLeadAdapter(Context context){
		this.mContext = context;
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public synchronized void addDatas(List<GuideRecommendSRP> datas) {
		this.datas = datas;
		if (datas != null && datas.size() > 0) {
			notifyDataSetChanged();
		}
	}
	
	public synchronized void addMore(List<GuideRecommendSRP> datas) {
		this.datas.addAll(datas);
		if (datas.size() > 0) {
			notifyDataSetChanged();
		}
	}

	public List<GuideRecommendSRP> getDatas() {
		return this.datas;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null == convertView || convertView.getTag() == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.guide_lead_list_item, null);
			holder.itemTitle = (TextView) convertView.findViewById(R.id.tv_first_lead_item_title);
			convertView.setTag(holder);
		}
		
		holder = (ViewHolder) convertView.getTag();
		GuideRecommendSRP srp = datas.get(position);
		String title = srp.getTitle();
		int state = srp.getStatus();
		holder.itemTitle.setText(title == null ? "" : title);
		if(state == FirstLeaderActivity.SUBSCRIBED) {
			setSelectedState(holder);
		}else {
			setUnselectState(holder);
		}
		return convertView;
	}
	/**
	 * 设置取消订阅按钮状态
	 * @param holder
	 */
	private void setUnselectState(ViewHolder holder) {
		holder.itemTitle.setBackgroundResource(R.drawable.first_lead_item_bg_normal);
		holder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.guide_text_color_normal));
	}
	/**
	 * 设置订阅按钮状态
	 * @param holder
	 */
	private void setSelectedState(ViewHolder holder) {
		holder.itemTitle.setBackgroundResource(R.drawable.first_lead_item_bg_selected);
		holder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.white));
	}
	
	public static class ViewHolder {
		TextView itemTitle;
	}
}
