package com.zhongsou.souyue.bases;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;

public abstract class RightSwipeActivity extends BaseActivity implements OnGestureListener {
	private boolean isCanRightSwipe = false;

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		detector = new GestureDetector(this);
		Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	protected boolean isCanRightSwipe() {
		return isCanRightSwipe;
	}

	private float mLastMotionX;
	private boolean isFinish = false;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (this.detector.onTouchEvent(ev)) {
			return true;
		}
		final int action = ev.getAction();
		final float x = ev.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			final float delay = x - mLastMotionX;
			if (delay > getWindow().getDecorView().getMeasuredWidth() / 2 && isCanRightSwipe()) {
				isFinish = true;
				finishAnimation(this);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isFinish) {
				isFinish = false;
				return true;
			}
			break;
		default:
			break;
		}
		try {
		    return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

//		if (e2.getY() - e1.getY() > 100 && Math.abs(velocityY) > 0 && isCanRightSwipe()) {
//			finishAnimation(this);
//			return true;
//		}
		float angle = Math.abs(velocityY / velocityX);
		if (velocityX > 1000 && angle < 0.27 && isCanRightSwipe()) {
			finishAnimation(this);
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		finishAnimation(this);
//		super.onBackPressed();
	}

	public void finishAnimation(Activity activity) {
		activity.finish(); //
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

	protected void setCanRightSwipe(boolean isCanRightSwipe) {
		this.isCanRightSwipe = isCanRightSwipe;
	}
	
	protected View inflateView(int id) {
		LayoutInflater viewInflater = (LayoutInflater) MainApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return viewInflater.inflate(id, null);
	}


}
