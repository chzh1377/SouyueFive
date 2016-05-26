package com.zhongsou.souyue.circle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页遮罩层VIEW
 */
public class HideView extends View implements OnClickListener {
	
	private List<View> showViews = new ArrayList<View>();
    private Context context;
    public static final String NEED_SHOW = "need_show";

	public HideView(Context context) {
		super(context);
        this.context = context;
		setOnClickListener(this);
	}

	public HideView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.context = context;
		setOnClickListener(this);
	}

	public HideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        this.context = context;
		setOnClickListener(this);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
        boolean needShow = SYSharedPreferences.getInstance().getBoolean(NEED_SHOW, true);
		if(needShow) {
            //如果没有需要展示的View，则该遮罩View也不展示。
            if (showViews.size() == 0) {
                this.setVisibility(View.GONE);
                return;
            }

            //画出showView以外的部分
            //下
            Paint p = new Paint();
            p.setColor(Color.parseColor("#aa000000"));
            RectF r = new RectF(0.0f, getShowHeight() + dip2px(context, 35), this.getWidth(), this.getHeight());
//            RectF r = new RectF(0.0f, getShowHeight(), this.getWidth(), this.getHeight());
            canvas.drawRect(r, p);
            //左
            r.set(0.0f, 0.0f, getShowViewLeft(0), getShowHeight());
            canvas.drawRect(r, p);

            //俩View中间的部分
            //if (getShowViewLeft(0) - showViews.get(0).getWidth() - getShowViewLeft(0) > 10) {
            r.set(getShowViewLeft(0) + showViews.get(0).getWidth(), 0.0f, getShowViewLeft(1), getShowHeight());
            //} else {
            //    r.set(getShowViewLeft(0) + showViews.get(0).getWidth()-20, 0.0f, getShowViewLeft(1), getShowHeight());
            // }
            canvas.drawRect(r, p);

            r.set(0.0f, getShowHeight(), this.getWidth() - dip2px(context, 45), dip2px(context, 35) + getShowHeight());
            canvas.drawRect(r, p);
            //绘制提示
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.new_1);
            canvas.drawBitmap(bitmap1, 6.0f, getShowHeight(), p);

            //绘制提示
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.new_2);
            canvas.drawBitmap(bitmap2, getWidth() - bitmap2.getWidth() - (showViews.get(1).getWidth() / 2), getShowHeight(), p);
            //绘制提示
            Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.new_3);
            canvas.drawBitmap(bitmap3, getWidth() - bitmap3.getWidth() - 20, getShowHeight() * 2 - 5, p);


            p.setColor(Color.WHITE);
            p.setFlags(Paint.ANTI_ALIAS_FLAG);
            p.setTextSize(dip2px(context, 25));
            p.setTextAlign(Paint.Align.CENTER);

            Paint.FontMetrics metrics = p.getFontMetrics();
            float fontHeight = metrics.bottom - metrics.top;
            float baseY = getHeight() - (getHeight() - fontHeight) / 2 - metrics.bottom;

            canvas.drawText("开始使用搜悦>>", getWidth() / 2, baseY, p);
        } else {
            this.setVisibility(GONE);
        }

	}
	
	private int getShowHeight(){
		return showViews.get(0).getHeight() > showViews.get(1).getHeight() ? showViews.get(0).getHeight() :showViews.get(1).getHeight();
	}
	
	
	private float getShowViewLeft(int index){
		int[] location = new int[2];
		showViews.get(index).getLocationInWindow(location);
		return location[0];
	}

	
	
	public void addShowViews(View v1, View v2) {
		showViews.add(v1);
		showViews.add(v2);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.setVisibility(GONE);
        SYSharedPreferences.getInstance().putBoolean(NEED_SHOW, false);
        return true;
	}

	@Override
	public void onClick(View v) {
		this.setVisibility(View.GONE);
	}

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
