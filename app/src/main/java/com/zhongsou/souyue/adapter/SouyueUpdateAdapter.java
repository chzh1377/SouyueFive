package com.zhongsou.souyue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.zhongsou.souyue.R;

import java.util.ArrayList;

public class SouyueUpdateAdapter extends BaseAdapter{
	private ArrayList<String> mArray;
	private LayoutInflater mInflater;
	public SouyueUpdateAdapter(Context context,ArrayList<String> array){
		this.mArray = array;
		this.mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArray.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.update_item, null);
		}
		
		if(mArray.size()-1==position){
			((TextView)convertView.findViewById(R.id.describe_item)).setText(mArray.get(position)+"\n");
		}else{
			((TextView)convertView.findViewById(R.id.describe_item)).setText(mArray.get(position));
		}
		((TextView)convertView.findViewById(R.id.num)).setText(position+1+"");
		return convertView;
	}

}
