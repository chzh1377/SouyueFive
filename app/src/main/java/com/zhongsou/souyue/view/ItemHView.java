package com.zhongsou.souyue.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ItemHView extends LinearLayout {
	public static final int mWidth_dpi=59;
	private float mCurScale=1;
	private float mWidth;

	
	public ItemHView(Context context) {
		super(context);
	}
	
	public ItemHView(Context context, AttributeSet attrs) {
		super(context, attrs);
		float den = context.getResources().getDisplayMetrics().density;
		final String xmlns="http://schemas.android.com/apk/res/android";
        mWidth = attrs.getAttributeIntValue(xmlns, "layout_width", 59)*den;
	}
	
	public void setmScale(float _mScale){
		mCurScale = _mScale;
		//猜测4.0以下会自动刷新，不需要这个方法。但仅仅只是猜测，需要验证--猜测失败
	    invalidate();
	}

	public float getmCurScale(){
		return mCurScale;
	}

	public float getmWidth() {
		return mWidth;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
        int count =canvas.save();
        float currentX = mWidth / 2;
        float currentY = getHeight()/8;
        canvas.scale(mCurScale, mCurScale, currentX, currentY);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);
	}

}
