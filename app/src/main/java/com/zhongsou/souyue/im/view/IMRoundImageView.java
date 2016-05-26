package com.zhongsou.souyue.im.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class IMRoundImageView extends ImageView {
    private Paint paint;
    public IMRoundImageView(Context context) {
        super(context,null);
        paint = new Paint();
    }

    public IMRoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        paint = new Paint();
    }

    public IMRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        try {
            Drawable drawable = getDrawable();
            if (null != drawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Bitmap b = getCroppedBitmap(bitmap, 12);
                final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
                final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
                paint.reset();
                canvas.drawBitmap(b, rectSrc, rectDest, paint);
            } else {
                super.onDraw(canvas);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int pixels) {
        //创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        //创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        //创建画笔
        Paint paint = new Paint();
        //创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);
        //画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, pixels, pixels, paint);
        //设置相交模式
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        //把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }

}
