package com.zhongsou.souyue.circle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class CheckableFrameLayout extends FrameLayout implements Checkable {

	private Checkable parent;
	private boolean isChecked = false;
	private static final int[] CheckedStateSet = { android.R.attr.state_checked };

	public CheckableFrameLayout(Context context) {
		super(context);
	}

	public CheckableFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public CheckableFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setChecked(boolean checked) {
		isChecked = checked;
		if (parent != null)
			parent.setChecked(isChecked);
		refreshDrawableState();
	}

	@Override
	public boolean isChecked() {
		return isChecked;

	}

	@Override
	public void toggle() {
		setChecked(!isChecked);
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

	public void setParent(Checkable parent) {
		this.parent = parent;
	}
}
