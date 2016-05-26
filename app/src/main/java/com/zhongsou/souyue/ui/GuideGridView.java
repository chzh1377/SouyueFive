package com.zhongsou.souyue.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * 引导页SRP表格列表
 * @author chz
 *
 */
public class GuideGridView extends GridView {

	public GuideGridView(Context context) {
		super(context);
	}

	public GuideGridView(Context context,AttributeSet attrs) {
		super(context,attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
