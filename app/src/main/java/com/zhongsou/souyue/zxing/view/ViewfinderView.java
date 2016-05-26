/*
 * Copyright (C) 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zhongsou.souyue.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.google.zxing.ResultPoint;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private Bitmap leftTop;
    private Bitmap rightTop;
    private Bitmap leftBottom;
    private Bitmap rightBottom;
    private int step = 0;
    private Bitmap greenLine;
    private Rect sourceRec;
    private Rect desRec;
    private int ma;
    private int margin = 15;
    private Context mcontext;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext=context;
        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);

        leftTop = BitmapFactory.decodeResource(context.getResources(), R.drawable.scan_top_left);
        rightTop = BitmapFactory.decodeResource(context.getResources(), R.drawable.scan_top_right);
        leftBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.scan_bottom_left);
        rightBottom = BitmapFactory.decodeResource(context.getResources(), R.drawable.scan_bottom_right);

        greenLine = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_line);
        sourceRec = new Rect(0, 0, greenLine.getWidth(), greenLine.getHeight());
    }

    @Override
    public void onDraw(Canvas canvas) {
        try {
            Rect frame = CameraManager.get().getFramingRect();
            if (frame == null) {
                return;
            }
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            // Draw the exterior (i.e. outside the framing rect) darkened
            paint.setColor(resultBitmap != null ? resultColor : maskColor);
            canvas.drawRect(0, 0, width, frame.top, paint);
            canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
            canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
            canvas.drawRect(0, frame.bottom + 1, width, height, paint);

//            Log.d("ViewfinderView", frame.left + "");
//            Log.d("ViewfinderView", frame.right + "");
//            Log.d("ViewfinderView", frame.top + "");
//            Log.d("ViewfinderView", frame.bottom + "");
            canvas.drawBitmap(leftTop, frame.left - margin, frame.top - margin, paint);
            canvas.drawBitmap(rightTop, frame.right - rightTop.getWidth() + margin, frame.top - margin, paint);
            canvas.drawBitmap(leftBottom, frame.left - margin, frame.bottom - leftBottom.getHeight() + margin + 4, paint);
            canvas.drawBitmap(rightBottom, frame.right - rightTop.getWidth() + margin, frame.bottom - rightBottom.getHeight() + margin + 4, paint);
            if(CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())){
                step += 5;
                if (step >= frame.height() - greenLine.getHeight()) {
                    step = 0;
                }
                desRec = new Rect(frame.left, step + frame.top, frame.right, greenLine.getHeight() + step + frame.top);
                canvas.drawBitmap(greenLine, sourceRec, desRec, paint);
                if (resultBitmap != null) {
                    // Draw the opaque result bitmap over the scanning rectangle
                    paint.setAlpha(OPAQUE);
                    canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
                } else {

                    // Draw a two pixel solid black border inside the framing rect
                    paint.setColor(frameColor);
                    canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
                    canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
                    canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
                    canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

                    // // Draw a red "laser scanner" line through the middle to show decoding is active
                    // paint.setColor(laserColor);
                    // paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
                    // scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
                    // int middle = frame.height() / 2 + frame.top;
                    // canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

                    Collection<ResultPoint> currentPossible = possibleResultPoints;
                    Collection<ResultPoint> currentLast = lastPossibleResultPoints;
                    if (currentPossible.isEmpty()) {
                        lastPossibleResultPoints = null;
                    } else {
                        possibleResultPoints = new HashSet<ResultPoint>(5);
                        lastPossibleResultPoints = currentPossible;
                        paint.setAlpha(OPAQUE);
                        paint.setColor(resultPointColor);
                        for (ResultPoint point : currentPossible) {
                            canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
                        }
                    }
                    if (currentLast != null) {
                        paint.setAlpha(OPAQUE / 2);
                        paint.setColor(resultPointColor);
                        for (ResultPoint point : currentLast) {
                            canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
                        }
                    }

                    // Request another update at the animation interval, but only repaint the laser line,
                    // not the entire viewfinder mask.
                    postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    public void recycle() {
        if (resultBitmap != null && !resultBitmap.isRecycled()) {
            resultBitmap.recycle();
        }
        resultBitmap = null;
        System.gc();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
