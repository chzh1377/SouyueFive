package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.HomePager;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年5月27日
 * 上午9:39:03 
 * 类说明 :首页导航条
 */
public class NewHomeNavAdapter extends BaseAdapter{
	private Context mContext;
	private List<HomePager> navs = new ArrayList<HomePager>();
	private int  selectItem = -1;
	
	public NewHomeNavAdapter(Context context){
		this.mContext = context;
	}
	
	public void setList(List<HomePager> list){
		this.navs = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return navs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return navs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder hv;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.newhomenav, parent, false);
			hv = new ViewHolder();
			hv.title = ((TextView) convertView.findViewById(R.id.navi_title));
			convertView.setTag(hv);
		}else{
			hv = (ViewHolder) convertView.getTag();
		}
		if(position == selectItem)
			hv.title.setText(Html.fromHtml("<font color='#0099CC'>" + navs.get(position).getTitle() + "</font>"));
		else
			hv.title.setText(Html.fromHtml("<font color='#909090'>" + navs.get(position).getTitle() + "</font>"));
		return convertView;
	}
	
	private class ViewHolder{
		TextView title;
	}
	
	public void setSelectItem(int selectItem) {  
        this.selectItem = selectItem;  
	} 
	
	public int getSelectItem(){
		return selectItem;
	}
	
}
