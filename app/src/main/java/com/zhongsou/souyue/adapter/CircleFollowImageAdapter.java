package com.zhongsou.souyue.adapter;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.zhongsou.souyue.BuildConfig;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.TouchGalleryActivity;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.utils.ImageUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CircleFollowImageAdapter extends BaseAdapter{

	private List<String> bolgImageList;
	private Context context;
	private OnChangeListener deleteListener;
	private OnChangeListener listenerAddImg;

	/**拖动的影像*/
	private View mDrapView;
	/**拖动手势*/
	private GestureDetector mGestureDetector;
	/**设置的item图像转换为bitMap显示*/
	private Bitmap iconBitmap;
	/**当前所选择的item位置*/
	private int selectIndex = -1;
	/**记录下所有item的影像，为后置寻找拖动位置做循环对照*/
	private List<View> itemViews = new ArrayList<View>();
	private boolean IS_ACTION_DROP = false;
	
	public CircleFollowImageAdapter(Context context, List<String> bolgImageList) {
		this.context = context;
		this.bolgImageList = bolgImageList;

		mGestureDetector = new GestureDetector(context,new DrapGestureListener());
	}

	public int getCount() {
		return bolgImageList.size();
	}

	public Object getItem(int position) {
		return bolgImageList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public void setDeleteListener(OnChangeListener listener){
		this.deleteListener = listener;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.circle_follow_image_item, null);
			holder = new ViewHolder();
			holder.rl_image_item = (RelativeLayout) convertView.findViewById(R.id.rl_image_item);
			holder.imgBtn_bolg_image = (ImageButton) convertView.findViewById(R.id.imgBtn_bolg_image);
			holder.cicle_follow_delete_img = (ImageButton) convertView.findViewById(R.id.cicle_follow_delete_img);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setOnDragListener(mOnDragListener);
		
		if(!bolgImageList.get(position).equals("add_pic")){
			if(itemViews.size()>position){
				itemViews.set(position, convertView);
			} else {
				itemViews.add(convertView);
			}
		}
		
		String bolg_Path = null;
		Bitmap bm = null;
		if (bolgImageList != null && bolgImageList.size() > 0) {
			bolg_Path = bolgImageList.get(position);
			if (!TextUtils.isEmpty(bolg_Path)) {
				
				if(bolg_Path.equals("add_pic")){
					holder.imgBtn_bolg_image.setImageResource(R.drawable.circle_follow_add_selector);
				}else{
					BitmapFactory.Options options = ImageUtil.getCaculateSize(bolg_Path,holder.imgBtn_bolg_image);
					if(bolg_Path.toLowerCase().contains("http")){
						//query.id(holder.imgBtn_bolg_image).image(bolg_Path, true, true);
                                                      PhotoUtils.showCard( PhotoUtils.UriType.HTTP,bolg_Path,holder.imgBtn_bolg_image);
					}else{
						bm = BitmapFactory.decodeFile(bolg_Path, options);
						if(BuildConfig.DEBUG) {
							Log.e("SOU_YUE", bolg_Path+";;"+options.inSampleSize+"");
						}
						holder.imgBtn_bolg_image.setImageBitmap(bm);
					}
				}
				
			}
			holder.bolgImagePath = bolg_Path;
		}
		holder.cicle_follow_delete_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bolgImageList.remove(position);
				notifyDataSetChanged();
				if(deleteListener != null){
					deleteListener.onChange(null);
				}
			}
		});
		
		if(bolgImageList.get(position).equals("add_pic")){
			holder.cicle_follow_delete_img.setVisibility(View.GONE);
			holder.imgBtn_bolg_image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(listenerAddImg != null){
						listenerAddImg.onChange(null);
					}
				}
			});
		}else{
			holder.cicle_follow_delete_img.setVisibility(View.VISIBLE);
			
			holder.imgBtn_bolg_image.setOnTouchListener(new OnTouchListener() {
				/*
				 *  onTouch 事件写在这里才能执行，在外面定义不行
				 */
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mDrapView = (View) v.getParent();
					return mGestureDetector.onTouchEvent(event);
				}
			});
			
			//点击查看大图
			holder.imgBtn_bolg_image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(position<(bolgImageList.size()-1)){
						Intent intent = new Intent();
	                    intent.setClass(context, TouchGalleryActivity.class);
	                    TouchGallerySerializable tg = new TouchGallerySerializable();
	                    tg.setItems(bolgImageList);
	                    tg.setClickIndex(position);
	                    Bundle extras = new Bundle();
	                    extras.putSerializable("touchGalleryItems", tg);
	                    intent.putExtras(extras);
	                    context.startActivity(intent);
					}
				}
			});
			
		}
		
		return convertView;
	}

	public static class ViewHolder {
		ImageButton imgBtn_bolg_image;
		ImageButton cicle_follow_delete_img;
		public String bolgImagePath;
		RelativeLayout rl_image_item;
	}

	public void addItemPath(String item) {
		bolgImageList.add(item);
	}
	
	public void addItemPath(int pos ,String item) {
		this.bolgImageList.add(pos,item);
	}
	
	public boolean seletePaht(String item) {
		return bolgImageList.contains(item);
	}

	public void clearBolgImageItem() {
		if (this.bolgImageList != null) {
			this.bolgImageList.clear();
		}
	}
	public List<String> getBolgImageList() {
		return bolgImageList;
	}
	
	public void setAddImgListener (OnChangeListener listener){
		this.listenerAddImg = listener;
	}
	
	public void clearBolgImageItem(String str) {
		if (this.bolgImageList != null && str != null) {
			this.bolgImageList.remove(str);
		}
	}
	
	private OnDragListener mOnDragListener = new OnDragListener() {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:// 只在应用程序调用startDrag()方法，并且获得了拖拽影子后，View对象的拖拽事件监听器才接收这种事件操作。
				break;
			case DragEvent.ACTION_DRAG_ENTERED:// 当拖拽影子刚进入View对象的边框时，View对象的拖拽事件监听器会接收这种事件操作类型。
				v.setAlpha(0.5F);
				break;
			case DragEvent.ACTION_DRAG_EXITED://在拖拽影子离开监听器View对象的边框之后，这个事件会发送给之前收到ACTION_DRAG_ENTERED事件的那个监听器。
				v.setAlpha(1F);
				break;
			case DragEvent.ACTION_DROP:// 用户在一个View对象之上释放了拖拽影子，这个对象的拖拽事件监听器就会收到这种操作类型。
//				IS_ACTION_DROP = true;
				View mView = (View) event.getLocalState();  //影像
				int srcPoint = 0,endPoint = 0;
				boolean intoSrc = false;
				boolean intoEnd = false;
				for (int i = 0, j = itemViews.size(); i < j; i++) {
					if (itemViews.get(i).equals(mView)) { 
						intoSrc = true;
						Log.e("ACTION_DROP", "原始位置是:"+i);
						srcPoint = i;
					}
					if (itemViews.get(i).equals(v)) {  //v是当前拖动到的item位置处view,也就是i即是目标位置
						intoEnd = true;
						Log.e("ACTION_DROP", "目标位置是:"+i);
							endPoint = i;
							Log.e("ACTION_DROP", "正常移动，endPoint=:"+endPoint);
					}
				}
				if(intoSrc&&intoEnd){
					String imageUrl= bolgImageList.get(srcPoint);
					clearBolgImageItem(imageUrl);
					addItemPath(endPoint,imageUrl);
				}
			    break;
			case DragEvent.ACTION_DRAG_ENDED:// 当系统结束拖拽操作时，View对象拖拽监听器会接收这种事件操作类型
				v.setAlpha(1F);
				Log.e("hubei", "DownXXX:"+v.getX());
//				if(IS_ACTION_DROP){
					notifyDataSetChanged();
//				}
				break;
				
			case DragEvent.ACTION_DRAG_LOCATION://getX和getY()方法会返回触点的X和Y轴坐标。
				break;
			default:
				break;
			}
			return true;
		}
	};
	
	private class DrapGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			ClipData data = ClipData.newPlainText("", "");

			MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
					mDrapView);
			mDrapView.startDrag(data, shadowBuilder, mDrapView, 0);
			mDrapView.setVisibility(View.INVISIBLE);
		}

		@Override
		public boolean onDown(MotionEvent e) {
//			return true;
			return false;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			if(Math.abs(distanceX)>50){
				return true;
			}
			return false;
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// TODO Auto-generated method stub
			if(Math.abs(velocityX)>50){
				return true;
			}
			return false;
		}
		
	}

	/**
	 * 这张图片被叫做拖动影子，使用View.DragShadowBuilder对象的方法来创建它，并且在使用startDrag()方法开始拖拽时，
	 * 把这个对象传递给系统。作为响应startDrag()方法的一部分，系统会调用在View.DragShadowBuilder对象中定义的回调方法，来获取拖拽影子。
	 * 
	 */
	private class MyDragShadowBuilder extends View.DragShadowBuilder {

		private final WeakReference<View> mView;

		public MyDragShadowBuilder(View view) {
			super(view);
			mView = new WeakReference<View>(view);   //构造生成拖动影像
		}

		/**
		 * 调用onProviderShadowMetrics()回调之后，系统会立即调用onDrawShadow()方法来获得拖拽影子。
		 * 这个方法有一个画布参数
		 * （Canvas对象），系统会使用onProvideShadowMetrics()方法中提供的参数来构造这个Canvas对象
		 * ，并在这个对象中描画拖拽影子。
		 */
		@Override
		public void onDrawShadow(Canvas canvas) {
			canvas.scale(1.2F, 1.2F);  
			super.onDrawShadow(canvas);
		}

		/**
		 * 在你调用startDrag()方法后，系统会立即调用这个方法，给系统发送拖拽影子的尺寸和触点。这个方法有两个参数：
		 * 
		 * dimensions：一个Point对象，其中X代表影子的宽度，Y代表影子的高度；
		 * touch_point：一个Point对象，这个触点应该是拖拽期间用户手指下方的拖拽影子的位置，X代表x轴的坐标，Y代表y轴的坐标。
		 */
		@Override
		public void onProvideShadowMetrics(Point shadowSize,
				Point shadowTouchPoint) {
			final View view = mView.get();
			if (view != null) {
				shadowSize.set((int) (view.getWidth() * 1.2F),(int) (view.getHeight() * 1.2F));
				shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
			} 
		}
	}
	
	public void setSelectIndex(int i){
		selectIndex = i;
	}
	
}
