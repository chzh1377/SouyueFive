package com.zhongsou.souyue.pop;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.PopActionItem;

import java.util.ArrayList;

/**
 * 重写popupWindow
 * 
 * @author Administrator
 * 
 */
public class PopActionBar extends PopupWindow {

	private View root;

	// private ImageView mArrowUp;

	private LayoutInflater inflater;

	private Context context;

	private View anchor;

	// private PopupWindow window;

	private Drawable background = null;

	private WindowManager windowManager;

	public static final int ANIM_AUTO = 4;

	private int animStyle;
	private ViewGroup mTrack;
	private ArrayList<PopActionItem> actionItems;

	public PopActionBar(View anchor) {

		super(anchor);

		this.anchor = anchor;

		// this.window = new PopupWindow(anchor.getContext());

		/**
		 * 在popwindow外点击即关闭该window
		 */
		// window.setTouchInterceptor(new OnTouchListener() {
		setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {

					// 让其消失
					// PopActionBar.this.window.dismiss();
					PopActionBar.this.dismiss();

					return true;

				}

				return false;
			}
		});

		context = anchor.getContext();

		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		actionItems = new ArrayList<PopActionItem>();

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		root = (ViewGroup) inflater.inflate(R.layout.pop_action_bar, null);

		// mArrowUp = (ImageView) root.findViewById(R.id.arrow_up);
		mTrack = (ViewGroup) root.findViewById(R.id.tracks);
		setContentView(root);

		animStyle = ANIM_AUTO;// 设置动画风格

	}

	/**
	 * 设置动画风格
	 * 
	 * @param animStyle
	 */
	public void setAnimStyle(int animStyle) {
		this.animStyle = animStyle;
	}

	/**
	 * 增加一个Action
	 * 
	 * @param actionItem
	 */
	public void addActionItem(PopActionItem actionItem) {
		actionItems.add(actionItem);
	}

	/**
	 * 弹出窗体
	 */
	public void show() {

		preShow();

		int[] location = new int[2];
		// 得到anchor的位置
		anchor.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());
		int screenWidth = windowManager.getDefaultDisplay().getWidth();
		// 创建action list
		createActionList();
		// root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));
		// root.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		// 设置弹窗弹出的位置的X y
		int xPos = screenWidth/2;
		int yPos = anchorRect.top;
		// 根据弹出位置，设置不同的方向箭头图片
		// showArrow(upPos);

		// 设置弹出动画风格
		setAnimationStyle();

		// 在指定位置弹出弹窗
		// window.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos , yPos);
		// window.showAsDropDown(this.anchor, 0, 0);
		showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos);
//		showAsDropDown(this.anchor, 0, 25);
	}

	// private void showArrow(int requestedX) {
	// ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)
	// mArrowUp.getLayoutParams();
	// // 以此设置距离左边的距离
	// param.leftMargin = requestedX;
	//
	// }

	/**
	 * 预处理窗口
	 */
	protected void preShow() {

		if (root == null) {
			throw new IllegalStateException("需要为弹窗设置布局");
		}

		if (background == null) {
			// window.setBackgroundDrawable(new BitmapDrawable());
			setBackgroundDrawable(new BitmapDrawable());
		} else {
			// window.setBackgroundDrawable(background);
			setBackgroundDrawable(background);
		}

		// // 设置宽度
		// window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		// // 设置高度
		// window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		//
		// window.setTouchable(true);
		// window.setFocusable(true);
		// window.setOutsideTouchable(true);
		// 设置宽度
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置高度
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		setTouchable(true);
		setFocusable(true);
		setOutsideTouchable(true);

		// 指定布局
		// window.setContentView(root);
	}

	/**
	 * 设置动画风格
	 *
	 */
	private void setAnimationStyle() {
		switch (animStyle) {
		case ANIM_AUTO:
			// window.setAnimationStyle(R.style.Animations_PopDownMenu_Right);
			setAnimationStyle(R.style.Animations_PopDownMenu_Right);
			break;
		}

	}

	/**
	 * 创建Action List
	 */
	private void createActionList() {

		View view;

		String title;

		Drawable icon;

		OnClickListener clickListener;

		for (int i = 0; i < actionItems.size(); i++) {

			title = actionItems.get(i).getTitle();
			icon = actionItems.get(i).getIcon();

			clickListener = actionItems.get(i).getClickListener();

			// 得到Action item
			view = getActionItem(title, icon, clickListener);
			view.setFocusable(true);
			view.setClickable(true);

			mTrack.addView(view, i);
		}

	}

	/**
	 * 得到Action Item
	 * 
	 * @param title
	 * @param icon
	 * @param listener
	 * @return
	 */
	private View getActionItem(String title, Drawable icon,
			OnClickListener listener) {

		// 装载Action布局

		LinearLayout linearLayout = (LinearLayout) inflater.inflate(
				R.layout.pop_action_item, null);

		ImageView img_icon = (ImageView) linearLayout.findViewById(R.id.icon);

		TextView tv_title = (TextView) linearLayout.findViewById(R.id.title);

		if (icon != null) {
			img_icon.setImageDrawable(icon);
		} else {
			img_icon.setVisibility(View.GONE);
		}
		if (title != null) {
			tv_title.setText(title);
		} else {
			tv_title.setVisibility(View.GONE);
		}

		if (listener != null) {
			linearLayout.setOnClickListener(listener);
		}

		return linearLayout;

	}

//	public void dismiss() {
//		if (window != null)
//			window.dismiss();
//	}

}
