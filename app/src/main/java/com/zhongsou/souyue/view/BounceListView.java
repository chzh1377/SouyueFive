package com.zhongsou.souyue.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * 我的页面中的ListView（5.0.7需求）
 * 需求：回弹效果，
 *
 * @author qubian
 * @data 2015年10月28日
 * @email naibbian@163.com
 */
public class BounceListView extends ListView {
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;
    private Context mContext;
    public BounceListView(Context context) {
        super(context);
        mContext = context;
        initBounceListView();
    }

    public BounceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBounceListView();
    }

    public BounceListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceListView();
    }

    private void initBounceListView() {
        final DisplayMetrics metrics = mContext.getResources()
                .getDisplayMetrics();
        final float density = metrics.density;
    }

//    @SuppressLint("NewApi")
//    @Override
//    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
//                                   int scrollY, int scrollRangeX, int scrollRangeY,
//                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
//                scrollRangeX, scrollRangeY, maxOverScrollX,
//                mMaxYOverscrollDistance, isTouchEvent);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
//
//    /**
//     * 设置没有阴影效果
//     */
//    private void setNoShadow()
//    {
//        try {
//            Class<?> c = (Class<?>) Class.forName(AbsListView.class.getName());
//            Field egtField = c.getDeclaredField("mEdgeGlowTop");
//            Field egbBottom = c.getDeclaredField("mEdgeGlowBottom");
//            egtField.setAccessible(true);
//            egbBottom.setAccessible(true);
//            Object egtObject = egtField.get(this);
//            Object egbObject = egbBottom.get(this);
//            Class<?> cc = (Class<?>) Class.forName(egtObject.getClass()
//                    .getName());
//            Field mGlow = cc.getDeclaredField("mGlow");
//            mGlow.setAccessible(true);
//            mGlow.set(egtObject, new ColorDrawable(Color.TRANSPARENT));
//            mGlow.set(egbObject, new ColorDrawable(Color.TRANSPARENT));
//            Field mEdge = cc.getDeclaredField("mEdge");
//            mEdge.setAccessible(true);
//            mEdge.set(egtObject, new ColorDrawable(Color.TRANSPARENT));
//            mEdge.set(egbObject, new ColorDrawable(Color.TRANSPARENT));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}