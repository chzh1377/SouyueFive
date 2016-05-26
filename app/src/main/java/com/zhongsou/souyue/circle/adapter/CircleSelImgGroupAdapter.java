package com.zhongsou.souyue.circle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleNativeImageLoader;
import com.zhongsou.souyue.circle.util.CircleNativeImageLoader.NativeImageCallBack;
import com.zhongsou.souyue.circle.view.CircleImageBean;
import com.zhongsou.souyue.circle.view.CircleImageView;
import com.zhongsou.souyue.circle.view.CircleImageView.OnMeasureListener;

import java.util.List;
/**
 * 
 * @author liuyh
 * 发帖图片选择分组适配器
 *
 */
public class CircleSelImgGroupAdapter extends BaseAdapter{
	private List<CircleImageBean> list;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private ListView mGridView;
	protected LayoutInflater mInflater;
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public CircleSelImgGroupAdapter(Context context, List<CircleImageBean> list, ListView mGridView){
		this.list = list;
		this.mGridView = mGridView;
		mInflater = LayoutInflater.from(context);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		CircleImageBean mImageBean = list.get(position);
		String path = mImageBean.getTopImagePath();
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.circle_sel_img_grid_group_item, null);
			viewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.photoalbum_item_image);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.photoalbum_item_name);
			
			//用来监听ImageView的宽和高
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		viewHolder.mTextViewCounts.setText(mImageBean.getFolderName() + "(" + Integer.toString(mImageBean.getImageCounts()) + ")");
		//给ImageView设置路径Tag,这是异步加载图片的小技巧
		viewHolder.mImageView.setTag(path);
		
		
		//利用NativeImageLoader类加载本地图片
		Bitmap bitmap = CircleNativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					mImageView.setImageBitmap(bitmap);
				}
			}
		});
		
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		
		
		return convertView;
	}
	
	
	
	public static class ViewHolder{
		public CircleImageView mImageView;
		public TextView mTextViewCounts;
	}




	
}
