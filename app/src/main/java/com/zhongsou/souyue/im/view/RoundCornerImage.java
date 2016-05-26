package com.zhongsou.souyue.im.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCornerImage extends ImageView {

    private int roundPixels;
    private Bitmap bitmap;
    private boolean cutCorner;
    private float density;
    private int bigValue;
    private int littleValue;
    public RoundCornerImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public RoundCornerImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundCornerImage(Context context) {
        super(context);
        init(context);
    }
    
    private void init(Context context) {
        density = getResources().getDisplayMetrics().density;
        bigValue = (int) (100*density);
        littleValue = (int) (50*density);
        roundPixels = 10;
    }

    public void cutCorner(boolean b){
        this.cutCorner = !b;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }
    
    @Override
    public void setImageBitmap(Bitmap bm) {
        /*if(bm == null){
            return ;
        }
        if(bm != this.bitmap){
            this.bitmap = bm;
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            setClickable(true);
            if(w>=h){
                roundPixels = h/10;
            } else {
                setMaxHeight(bigValue);
                setMaxWidth(littleValue);
                roundPixels = w/10;
            }
            setScaleType(ScaleType.CENTER_CROP);
        }*/
        super.setImageBitmap(bm);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        /*if(bitmap != null && !cutCorner){
            Log.d("COOL", "CUT bitmap");
            cutCorner = true;
            display(bitmap);
            postInvalidate();
            return;
        }*/
        super.onDraw(canvas);
    }
    
}
