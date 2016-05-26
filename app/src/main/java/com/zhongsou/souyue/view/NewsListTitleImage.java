
package com.zhongsou.souyue.view;
/*
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zhongsou.souyue.utils.LogDebugUtil;

public class NewsListTitleImage extends ImageView {

    private int width;
    private int height;
    private Drawable mDrawable;
    private boolean hasScaled;

    public NewsListTitleImage(Context context) {
        super(context);
    }

    public NewsListTitleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NewsListTitleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageDrawable(final Drawable drawable) {
        mDrawable = drawable;
        hasScaled = false;
        LogDebugUtil.d("COOL", "setImageDrawable:"+mDrawable);
        super.setImageDrawable(drawable);
    }

    private void setScaleTypeMatrix(Drawable bm) {
        float scale;
        hasScaled = true;
        float dx = 0, dy = 0;
        int dwidth = bm.getIntrinsicWidth();
        int dheight = bm.getIntrinsicHeight();
        int vwidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int vheight = getHeight() - getPaddingTop() - getPaddingBottom();
        LogDebugUtil.d("COOL", "vwidth:"+vwidth);
        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
        } else {
            scale = (float) vwidth / (float) dwidth;
            dy = (vheight - dheight * scale) * 0.5f;
        }
        Matrix m = new Matrix();
        LogDebugUtil.d("COOL", "scalse:"+scale);
        if (scale == 0) {
            return;
        }
        m.setScale(scale, scale);
        m.postTranslate((int) (dx + 0.5f), 0);
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(m);
        postInvalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        if(mDrawable !=null && !hasScaled){
            setScaleTypeMatrix(mDrawable);
        }
        super.onDraw(canvas);
    }
}
*/

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.facebook.drawee.view.ZSImageView;

public class NewsListTitleImage extends ZSImageView {
    private Bitmap currentBitmap;
    private ImageChangeListener imageChangeListener;
    private boolean scaleToWidth = false; // this flag determines if should
    // measure height manually dependent
    // of width

    public NewsListTitleImage(Context context) {
        super(context);
        init();
    }

    public NewsListTitleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NewsListTitleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setScaleType(ScaleType.CENTER_CROP);
    }

    public void recycle() {
        setImageBitmap(null);
        if ((this.currentBitmap == null) || (this.currentBitmap.isRecycled()))
            return;
        this.currentBitmap.recycle();
        this.currentBitmap = null;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        currentBitmap = bm;
        super.setImageBitmap(currentBitmap);
        if (imageChangeListener != null)
            imageChangeListener.changed((currentBitmap == null));
    }

    @Override
    public void setImageDrawable(Drawable d) {
        super.setImageDrawable(d);
        if (imageChangeListener != null)
            imageChangeListener.changed((d == null));
    }

    @Override
    public void setImageResource(int id) {
        super.setImageResource(id);
    }

    public interface ImageChangeListener {
        // a callback for when a change has been made to this imageView
        void changed(boolean isEmpty);
    }

    public ImageChangeListener getImageChangeListener() {
        return imageChangeListener;
    }

    public void setImageChangeListener(ImageChangeListener imageChangeListener) {
        this.imageChangeListener = imageChangeListener;
    }

    private int imageWidth;
    private int imageHeight;

    public void setImageWidth(int w) {
        imageWidth = w;
    }

    public void setImageHeight(int h) {
        imageHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        /**
         * if both width and height are set scale width first. modify in future
         * if necessary
         */

        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            scaleToWidth = true;
        } else if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
            scaleToWidth = false;
        } else
            throw new IllegalStateException("width or height needs to be set to match_parent or a specific dimension");

        if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0) {
            // nothing to measure
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        } else {
            if (scaleToWidth) {
                int iw = getDrawable().getIntrinsicWidth();
                int ih = getDrawable().getIntrinsicHeight();
                int heightC = width * ih / iw;
                if (height > 0)
                    if (heightC > height) {
                        // dont let hegiht be greater then set max
                        heightC = height;
                        width = heightC * iw / ih;
                    }

                this.setScaleType(ScaleType.CENTER_CROP);
                setMeasuredDimension(width, heightC);

            } else {
                // need to scale to height instead
                int marg = 0;
                if (getParent() != null) {
                    if (getParent().getParent() != null) {
                        marg += ((RelativeLayout) getParent().getParent()).getPaddingTop();
                        marg += ((RelativeLayout) getParent().getParent()).getPaddingBottom();
                    }
                }

                int iw = getDrawable().getIntrinsicWidth();
                int ih = getDrawable().getIntrinsicHeight();

                width = height * iw / ih;
                height -= marg;
                setMeasuredDimension(width, height);
            }

        }
    }

}
