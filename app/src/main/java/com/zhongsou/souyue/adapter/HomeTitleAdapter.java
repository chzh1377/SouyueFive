package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.GroupKeywordItem;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年4月30日
 * 上午11:17:39 
 * 类说明 :首页导航栏下行标题adapter
 */
public class HomeTitleAdapter extends BaseAdapter {

	private List<GroupKeywordItem> navs = new ArrayList<GroupKeywordItem>();
    private boolean isSys = true;
    private Context context;
    
    public HomeTitleAdapter(boolean isSys,Context cx) {
        this.isSys = isSys;
        this.context = cx;
    }

    public void addNavs(List<GroupKeywordItem> nav) {
        if(nav!=null && navs.size() == 0)
        this.navs.addAll(nav);
        
    }
    public void clearNavs(){
        if (navs != null)
            this.navs.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return navs.size();
    }

    @Override
    public GroupKeywordItem getItem(int position) {
        return navs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String tag = null;
        ViewHolder holder;
        if(isSys){
            tag = navs.get(position).title();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.homenav_item, null);
                holder=new ViewHolder();
                holder.title=(TextView) convertView.findViewById(R.id.navi_title);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
        }else{
            tag = navs.get(position).keyword();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.homenav_item2, null);
                holder=new ViewHolder();
                holder.title=(TextView) convertView.findViewById(R.id.navi_title);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
        }
        
        holder.title.setText(tag);
        return convertView;
    }

    private class ViewHolder{
        TextView title;
    }
    
}
