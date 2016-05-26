package com.zhongsou.souyue.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;

import java.util.ArrayList;
import java.util.List;


/** 
 * @author : zoulu
 * 2014年7月22日
 * 下午3:08:55 
 */
public class ShareSNSDialogAdapter extends BaseAdapter{
	
	private List<ShareSNSInfo> lists = new ArrayList<ShareSNSInfo>();
	private List<Integer> titles = new ArrayList<Integer>();//分享顺序及个数，文档参考ShareSNSDialog
	private final LayoutInflater mInflater;
	
	public ShareSNSDialogAdapter(Context context){
		 mInflater = LayoutInflater.from(context);
	}
	
	public void setTitles(List<Integer> titles){
		this.titles = titles;
		lists = getList();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Integer getItem(int arg0) {
		// TODO Auto-generated method stub
		return lists.get(arg0).getPostions();
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
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
		holder.share_way.setText(lists.get(position).getTitle());
		holder.share_icon.setImageResource(lists.get(position).getIcons());
		return convertView;
	}
	
	private static class ViewHolder {
		TextView share_way;
		ImageView share_icon;
	}
	
	private List<ShareSNSInfo> getList(){
		for(int i = 0; i < titles.size(); i++){
			ShareSNSInfo info = new ShareSNSInfo();
			switch (titles.get(i)) {
//			case 0:
//				info.setTitle(ShareSNSDialog.TO_DIGETST);
//				info.setPostions(0);
//				info.setIcons(R.drawable.ic_sy_digest_icon);
//				lists.add(info);
//				break;
			case 1:
				info.setTitle(ShareSNSDialog.TO_SINA);
				info.setPostions(1);
				info.setIcons(R.drawable.ic_sina_icon);
				lists.add(info);
				break;
			case 2:
				info.setTitle(ShareSNSDialog.TO_WEIX);
				info.setPostions(2);
				info.setIcons(R.drawable.ic_weix_icon);
				lists.add(info);
				break;
			case 3:
				info.setTitle(ShareSNSDialog.TO_FRIENDS);
				info.setPostions(3);
				info.setIcons(R.drawable.ic_friends_quan_icon);
				lists.add(info);
				break;
			case 6:
				info.setTitle(ShareSNSDialog.TO_RENREN);
				info.setPostions(6);
				info.setIcons(R.drawable.ic_renren_icon);
				lists.add(info);
				break;
//			case 8:
//				info.setTitle(ShareSNSDialog.TO_SYFRIENDS);
//				info.setPostions(8);
//				info.setIcons(R.drawable.ic_sy_friend_icon);
//				lists.add(info);
//				break;
			case 9:
				info.setTitle(ShareSNSDialog.TO_SYIMFRIEND);
				info.setPostions(9);
				info.setIcons(R.drawable.ic_souyuefriends_icon);
				lists.add(info);
				break;
			case 10:
				info.setTitle(ShareSNSDialog.TO_INTEREST);
				info.setPostions(10);
				info.setIcons(R.drawable.circle_primeicon);
				lists.add(info);
				break;
			case 11:
				info.setTitle(ShareSNSDialog.TO_QQFRIEND);
				info.setPostions(11);
				info.setIcons(R.drawable.ic_tencent_qq_friend_icon);
				lists.add(info);
				break;
			case 12:
				info.setTitle(ShareSNSDialog.TO_QQZONE);
				info.setPostions(12);
				info.setIcons(R.drawable.ic_tencent_qq_zone_icon);
				lists.add(info);
				break;

			default:
				break;
			}
		}
		return lists;
	}

}
