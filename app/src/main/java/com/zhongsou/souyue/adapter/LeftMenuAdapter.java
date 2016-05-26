/*******************************************************************************
 * Copyright 2012 Steven Rudenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.ImUtils;
import com.zhongsou.souyue.module.HomePageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 左侧导航
 * @author fan
 *
 */
public class LeftMenuAdapter extends BaseAdapter {

	public static final int VIEW_TYPE_TITLE = 0;
	public static final int VIEW_TYPE_GROUP = 1;
	public static final int VIEW_TYPES_COUNT = 2;

	private final LayoutInflater mInflater;

	public int systemPosition = -1;
	public int selectedPosition = -1;
	public boolean isHidden = true;
	public int hiddenPosition = -1;
	
	private  ArrayList<HomePageItem> items = new ArrayList<HomePageItem>();
	private Context context;
	private String countTextNum="";
	
	public LeftMenuAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public ArrayList<HomePageItem> getAllData() {
		return items;
	}

	/**
	 * 初始化数据
	 * @param homePages
	 */
	public void setData(List<HomePageItem> homePages){
		items.clear();
//		HomePageItem pageItem = new HomePageItem();
//		pageItem.category_$eq("slotMachine");
//		pageItem.title_$eq("老虎机");
//		items.add(pageItem);
		if(homePages!=null && homePages.size()>0)
			items.addAll(homePages);
	}
	
	public void addData(List<HomePageItem> homePages){
		if(homePages!=null && homePages.size()>0)
			items.addAll(homePages);
	}
	
	public HomePageItem setSelectedItem(HomePageItem toFind){
		int toSel = -1;
		HomePageItem res = null;
		if(toFind!=null && !TextUtils.isEmpty(toFind.title())){//只有标题相同就找到
			String destT = toFind.title();
			for(int i=0; items!=null&&i<items.size(); i++){
				HomePageItem temp = items.get(i);
				if(destT.equals(temp.title())){
					toSel = i;
					break;
				}
			}
		}
		if(toSel == -1){//未找到
//			res = getSysItem();
//			selectedPosition = systemPosition;
			res = getFirstShowItem();
		}else{
			selectedPosition = toSel;
			res = getItem(toSel);
		}
		return res;
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public HomePageItem getItem(int position) {
		return items.get(position);
	}
	
	/**
	 * @author fan
	 * 获取精彩推荐分组,同时设置选中项
	 * @return HomePageItem
	 */
	public HomePageItem getSysItem(){
		HomePageItem res = null;
		int i = 0;
		for(HomePageItem item : items){
			if(HomePageItem.SYSTEM.equals(item.category())){
				res = item;
				systemPosition = i;
				break;
			}
			++i;
		}
		return res;
	}
	
	/**
	 * @author fan
	 * 获取第一个不跳转的分组
	 * @return HomePageItem
	 */
	public HomePageItem getFirstShowItem(){
		HomePageItem res = null;
		int i = 0;
		for(HomePageItem item : items){
			String c = item.category();
			if(HomePageItem.SYSTEM.equals(c) || HomePageItem.RSS.equals(c) || HomePageItem.GROUP.equals(c)){
				res = item;
				selectedPosition = i;
				break;
			}
			++i;
		}
		return res;
	}
	public void setBubleText(String count){
	    this.countTextNum=count;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		boolean type = isEnabled(position);
		TextView count_text = null;
		if(convertView == null){
			if(type){
				convertView = mInflater.inflate(R.layout.action_list_item, parent, false);
			}else{
				convertView = mInflater.inflate(R.layout.category_list_item, parent, false);
			}
		}
		
		TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
		text1.setText(items.get(position).title().trim());
		if(type){
		    count_text=(TextView) convertView.findViewById(R.id.count_text);
		    setBubbleText(count_text,position);
		}
		if (selectedPosition == position) {//选中项
			text1.setTextColor(context.getResources().getColor(R.color.subscribe_gategory));
			convertView.setBackgroundResource(R.drawable.action_list_foucs);// 把当前选中的条目加上选中效果
		} else if(type){
			text1.setTextColor(context.getResources().getColor(R.color.white));
			convertView.setBackgroundResource(0);
		}
		
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPES_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		if(TextUtils.isEmpty(getItem(position).category())){
			return VIEW_TYPE_TITLE;
		}else{
			return VIEW_TYPE_GROUP;
		}
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) == VIEW_TYPE_GROUP;
	}

    /**
     * 设置点击的item，以保持高亮
     * @param position
     */
	public void setSelectedPosition(int position) {
		if(selectedPosition != position){
			selectedPosition = position;
			notifyDataSetChanged();
		}
	}
	private void setBubbleText(TextView count_text,int position){
	    if(items.get(position).category().equals("im")&&!countTextNum.equals("")&&!countTextNum.equals("0"))  {
            count_text.setText(ImUtils.getBubleText(countTextNum));
            count_text.setVisibility(View.VISIBLE);
      }else{
            count_text.setVisibility(View.GONE);
      }
	}

}
