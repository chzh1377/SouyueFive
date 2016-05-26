package com.zhongsou.souyue.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class MyImageView extends ImageView {

    private int width;
    private int hight;
    private Bitmap bmp;

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context) {
        super(context);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        hight = width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bmp == null) {
            return;
        }
        Matrix matrix = new Matrix();
        if (width > bmp.getWidth()) {
            matrix.setScale((width + 0f) / bmp.getWidth(), (width + 0f) / bmp.getWidth());
        } else
            matrix.setScale((width + 0f) / bmp.getWidth(), 1);
        canvas.drawBitmap(bmp, matrix, null);
        super.onDraw(canvas);
    }

    public void setImage(Bitmap bmp) {
        this.bmp = bmp;

    }

    public void layouts() {
//		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, (int)(width/1.6));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, (int) (width / 1.2));
        layoutParams.bottomMargin = 5;
        setLayoutParams(layoutParams);
    }

}
