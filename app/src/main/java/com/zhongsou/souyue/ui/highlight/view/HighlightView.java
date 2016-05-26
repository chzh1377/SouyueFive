package com.zhongsou.souyue.ui.highlight.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.zhongsou.souyue.ui.highlight.Highlight;

import java.util.List;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description
 * @date 2015/12/25
 */
public class HighlightView extends FrameLayout {
    private static final int DEFAULT_WIDTH_BLUR = 15;
    private static final int DEFAULT_RADIUS = 6;
    private static final PorterDuffXfermode MODE = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private List<Highlight.ViewPosInfo> mViewRects;
    private Highlight mHighlight;
    private LayoutInflater mInflater;

    //some config
    private boolean isBlur = true;
    private int maskColor = 0xCC000000;


    public HighlightView(Context context, Highlight Highlight,
                         int maskColor, boolean isBlur,
                         List<Highlight.ViewPosInfo> viewRects) {
        super(context);
        mHighlight = Highlight;
        mInflater = LayoutInflater.from(context);
        mViewRects = viewRects;
        this.maskColor = maskColor;
        this.isBlur = isBlur;

        setWillNotDraw(false);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        if (isBlur)
            mPaint.setMaskFilter(new BlurMaskFilter(DEFAULT_WIDTH_BLUR, BlurMaskFilter.Blur.SOLID));
        mPaint.setStyle(Paint.Style.FILL);

        addViewForTip();
    }

    /**
     * 增加提示信息
     */
    private void addViewForTip() {
        for (Highlight.ViewPosInfo viewPosInfo : mViewRects) {  //循环加载
            View view = mInflater.inflate(viewPosInfo.layoutId, this, false);
            LayoutParams lp = buildTipLayoutParams(view, viewPosInfo);

            if (lp == null) continue;

            lp.leftMargin = (int) viewPosInfo.marginInfo.leftMargin;
            lp.topMargin = (int) viewPosInfo.marginInfo.topMargin;
            lp.rightMargin = (int) viewPosInfo.marginInfo.rightMargin;
            lp.bottomMargin = (int) viewPosInfo.marginInfo.bottomMargin;

            if (lp.leftMargin == 0 && lp.topMargin == 0) {
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            }
            addView(view, lp);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),//
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            buildMask();
            updateTipPos();
        }
    }

    /**
     * 生成View
     */
    private void buildMask() {
        mMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_4444);  //构建蒙板
        mHighlight.updateInfo();

        Canvas canvas = new Canvas(mMaskBitmap);    //根据图片创建canvas
        canvas.drawColor(maskColor);    //设置canvas背景颜色
//        mPaint.setXfermode(MODE);   //设置为底层View显示
//        for (Highlight.ViewPosInfo viewPosInfo : mViewRects) {  //循环将高亮区域加入canvas
//            canvas.drawRoundRect(viewPosInfo.rectF, DEFAULT_RADIUS, DEFAULT_RADIUS, mPaint);
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mMaskBitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    private void updateTipPos() {
        for (int i = 0, n = getChildCount(); i < n; i++) {
            View view = getChildAt(i);
            Highlight.ViewPosInfo viewPosInfo = mViewRects.get(i);

            LayoutParams lp = buildTipLayoutParams(view, viewPosInfo);
            if (lp == null) continue;
            view.setLayoutParams(lp);
        }
    }

    private LayoutParams buildTipLayoutParams(View view, Highlight.ViewPosInfo viewPosInfo) {
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        if (lp.leftMargin == (int) viewPosInfo.marginInfo.leftMargin &&
                lp.topMargin == (int) viewPosInfo.marginInfo.topMargin &&
                lp.rightMargin == (int) viewPosInfo.marginInfo.rightMargin &&
                lp.bottomMargin == (int) viewPosInfo.marginInfo.bottomMargin) return null;

        lp.leftMargin = (int) viewPosInfo.marginInfo.leftMargin;
        lp.topMargin = (int) viewPosInfo.marginInfo.topMargin;
        lp.rightMargin = (int) viewPosInfo.marginInfo.rightMargin;
        lp.bottomMargin = (int) viewPosInfo.marginInfo.bottomMargin;

        if (lp.leftMargin == 0 && lp.topMargin == 0) {
            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        }
        return lp;
    }
}
