package com.zhongsou.souyue.circle.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleNativeImageLoader;
import com.zhongsou.souyue.circle.util.CircleNativeImageLoader.NativeImageCallBack;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleImageView;
import com.zhongsou.souyue.circle.view.CircleImageView.OnMeasureListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * @author liuyh
 * 发帖选择图片适配器
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class CircleSelectImgAdapter extends BaseAdapter {
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	/**
	 * 用来存储图片的选中情况
	 */
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private GridView mGridView;
	private List<String> list;
	protected LayoutInflater mInflater;
	private OnChangeListener listener;
	private Context mcontext;
	private List<String> listPath = new ArrayList<String>(); 
	private int picLen;

	public CircleSelectImgAdapter(Context context, List<String> list, GridView mGridView,int accLen) {
		this.list = list;
		this.mGridView = mGridView;
		this.mcontext = context;
		this.picLen = accLen;
		mInflater = LayoutInflater.from(mcontext);
	}
	
	public void setListener(OnChangeListener listener1){
		this.listener = listener1;
	}
	
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
	
	public List<String> getPath(){
		/*for (int i = 0; i < list.size(); i++) {
			if (mSelectMap.containsKey(i)) {
				if (mSelectMap.get(i)) {
					listPath.add(list.get(i));
				}
			}
		}*/
		return listPath;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		 final ViewHolder viewHolder;
		String path = list.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.circle_sel_img_grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			
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
//			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//如果是未选中的CheckBox,则添加动画
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mCheckBox);
				}
				mSelectMap.put(position, !viewHolder.mCheckBox.isChecked());
				if(getSelectItems().size() > picLen){
					Toast.makeText(mcontext, "最多选择"+picLen+"张图片", Toast.LENGTH_SHORT).show();
					mSelectMap.put(position, false);
					return;
				}
                if(viewHolder.mCheckBox.isChecked()){
                    listPath.remove(list.get(position));
                }else{
                    listPath.add(list.get(position));
                }
				notifyDataSetChanged();
				if(listener != null){
					listener.onChange(getSelectItems().size());
				}
			}
		});
		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		//利用NativeImageLoader类加载本地图片
		Bitmap bitmap = CircleNativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
			
			@Override
			public void onImageLoader(Bitmap bitmap, String path) {
				ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
				if(bitmap != null && mImageView != null){
					mImageView.setImageBitmap(bitmap);
                    ((View)viewHolder.mImageView.getParent()).setClickable(true);
				}
			}
		});
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
            ((View)viewHolder.mImageView.getParent()).setClickable(false);
		}
		return convertView;
	}
	
	/**
	 * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画 
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	
	public List<Integer> getSelectItems(){
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}
	
	
	public static class ViewHolder{
		public CircleImageView mImageView;
		public CheckBox mCheckBox;
	}



}
