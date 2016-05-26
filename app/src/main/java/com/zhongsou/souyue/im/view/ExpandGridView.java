package com.zhongsou.souyue.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 *
 * @ClassName: ExpandGridView
 * @Description: 可扩展的gridview
 * @author gengsong@zhongsou.com
 * @date 2014年8月27日 下午13:56
 * @version 4.0
 */
public class ExpandGridView extends GridView {

    public ExpandGridView(Context context) {
        super(context);
    }

    public ExpandGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


}
