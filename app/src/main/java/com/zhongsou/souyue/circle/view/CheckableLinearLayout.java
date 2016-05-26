package com.zhongsou.souyue.circle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable{
	private boolean isChecked = false;
	private static final int[] CheckedStateSet = { android.R.attr.state_checked };

	public CheckableLinearLayout(Context context) {
		super(context);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public CheckableLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked = checked;
		refreshDrawableState();
	}

	@Override
	public boolean isChecked() {
		return isChecked;
		
	}

	@Override
	public void toggle() {
		setChecked(!isChecked);
		System.out.println(isChecked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CheckedStateSet);
		}
		return drawableState;
	}
	
	@Override
	public boolean performClick() {
		toggle();
		return super.performClick();
	}

}
