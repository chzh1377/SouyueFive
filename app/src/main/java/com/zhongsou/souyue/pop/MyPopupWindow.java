package com.zhongsou.souyue.pop;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

/**
 * 自定义的popupwindow
 * 
 * @author chefb@zhongsou.com
 */
@SuppressLint("NewApi")
public class MyPopupWindow extends PopupWindow {

	private View view;
	public boolean isPullUp;//是上拉的动画 在新闻详情里用
	public boolean isGoHome;
	private AnimationListener listener;
	public MyPopupWindow(View contentView, int width, int height, boolean focusable) {
		super(contentView, width, height, focusable);
		this.view = contentView;
//		if (VERSION.SDK_INT >= 11)//硬件加速  此处关闭 防止UI丢失
//			this.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		view.measure(0,0);

	}
	public void setAnimationEndListener(AnimationListener listener){
		this.listener=listener;
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		view.measure(0, 0);
//		if (view.getMeasuredHeight() == 0) {
//			view.measure(0, 0);
//		}
		//上拉动画
		if(isPullUp){
			
			TranslateAnimation animation = new TranslateAnimation(0, 0, view.getMeasuredHeight(),0);
			animation.setDuration(view.getMeasuredHeight());
			view.startAnimation(animation);
			super.showAsDropDown(anchor, xoff, - (anchor.getHeight()+view.getMeasuredHeight()));
			return;
		}
		//下拉动画
		TranslateAnimation animation = new TranslateAnimation(0, 0, -view.getMeasuredHeight(), 0);
		animation.setDuration(view.getMeasuredHeight());
		if(listener!=null)
			animation.setAnimationListener(listener);
		view.startAnimation(animation);
		super.showAsDropDown(anchor, xoff, yoff);
	}

	public void dismiss() {
//		if(isGoHome){
//			view.invalidate();
//			super.dismiss();
//			return;
//		}
		
		if(isPullUp){
			
			TranslateAnimation animation = new TranslateAnimation(0, 0, 0, view.getMeasuredHeight());
			animation.setDuration(view.getMeasuredHeight());
			animation.setFillAfter(true);
			view.startAnimation(animation);
//			new Handler().postDelayed(new Runnable() {
//				public void run() {
					view.invalidate();
					super.dismiss();
//				}
//			}, view.getMeasuredHeight());
			return;
		}
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -view.getMeasuredHeight());
		animation.setDuration(view.getMeasuredHeight());
		animation.setFillAfter(true);
		if(listener!=null)
			animation.setAnimationListener(listener);
		view.startAnimation(animation);
//		new Handler().postDelayed(new Runnable() {
//			public void run() {
				view.invalidate();
				super.dismiss();
//			}
//		}, view.getMeasuredHeight());
	}
	
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		
		super.showAtLocation(parent, gravity, x, y);
	}
}