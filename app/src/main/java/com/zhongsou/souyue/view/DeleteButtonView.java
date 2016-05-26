package com.zhongsou.souyue.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class DeleteButtonView extends View {

    protected static final int SET_VISIBILITY = 10;
    private Context context;
    private int resourceId;
    private VisiblePart curVisible;
    private int width;
    private int height;
    private Bitmap bitmap;
    private int curMsg;
    protected long delay = 10;
    public int duration = 400;
    protected int step = 1;
    private int times;

    public DeleteButtonView(Context context) {
        super(context);
        init(context);
    }

    public DeleteButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DeleteButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        if(VERSION.SDK_INT >= 14){
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setImageResource(int resource) {
        resourceId = resource;
        bitmap = BitmapFactory.decodeResource(getResources(), resource);
    }
    
    public void setDuration(int d){
        duration = d;
    }

    /**
     * 
     * @param visibility
     * @param smoothAni 是否有过渡动画
     */
    public void setVisibility(int visibility, boolean smoothAni) {
        if (smoothAni) {
            if (isShown()) {
                if (visibility != View.VISIBLE) {
                    dismissDraw();
                }
            } else {
                if (visibility == View.VISIBLE) {
                    showDraw();
                }
            }
        } else {
            setVisibility(visibility);
        }
    }

    private void showDraw() {
        setVisibility(VISIBLE);
        Log.d("COOL", "set visible");
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            
            @Override
            public void onGlobalLayout() {
                Log.d("COOL", "onGlobalLayout");
                curVisible = new VisiblePart();
                curVisible.x = width;
                curMsg = VISIBLE;
                handler.sendEmptyMessage(VISIBLE);
                postInvalidate();
                if(VERSION.SDK_INT >= 16){
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    private void dismissDraw() {
        curVisible = new VisiblePart();
        curVisible.x = 0;
        curMsg = GONE;
        handler.sendEmptyMessage(GONE);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SET_VISIBILITY) {
                setVisibility(curMsg);
            } else {  //显示动画
                new Thread() {
                    public void run() {
                        Log.d("COOL", "while start"+curVisible.x);
                        while (curVisible.x >= 0 && curVisible.x <= width) {
                            if (curMsg == GONE) {
                                curVisible.x += step;
                            } else if (curMsg == VISIBLE) {
                                curVisible.x -= step;
                            }
                            postInvalidate();
                            SystemClock.sleep(delay);
                        }
                        Log.d("COOL", "while end"+curVisible.x);
                        handler.sendEmptyMessage(SET_VISIBILITY);
                    }
                }.start();

            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(new Rect(curVisible.x, 0, width, height), Region.Op.REPLACE);
        canvas.clipRect(new Rect(0, 0, width, height));
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawColor(Color.TRANSPARENT);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, width, height), paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        times = width / step;
        if (times > 0) {
            int s = duration / times;
            if (s > 0) {
                delay = s;
            }
        }

        curVisible = new VisiblePart();
        curVisible.x = 0;
    }

    private class VisiblePart {
        public int x;

        public Rect getRect() {
            return new Rect(0, 0, x, height);
        }
    }

}
