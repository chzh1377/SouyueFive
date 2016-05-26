/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zhongsou.souyue.ui.gallery.touchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.ui.gallery.touchview.PhotoViewAttacher.OnViewTapListener;
import com.zhongsou.souyue.utils.MyImageLoader;

public class UrlTouchImageView extends RelativeLayout {
    //    protected ProgressBar mProgressBar;
    protected PhotoView mImageView;
    //protected ProgressBar mProgressBar;
    //private View layoutView;


    protected Context mContext;
    private DownLoadLinstener dll;

    public void setDownLoadLinstener(DownLoadLinstener dll) {
        this.dll = dll;
    }

    public UrlTouchImageView(Context ctx) {
        super(ctx);
        mContext = ctx;
        init();

    }

    public UrlTouchImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        mContext = ctx;
        init();
    }

    public ImageView getImageView() {
        return mImageView;
    }

    protected void init() {
        mImageView = new PhotoView(mContext);
        mImageView.setOnViewTapListener((OnViewTapListener) mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(params);
//		mImageView.setBackgroundResource(R.drawable.default_image_bg);
      //  mImageView.setScaleType(ScaleType.FIT_CENTER);
        this.addView(mImageView);
       // mImageView.setVisibility(VISIBLE);
       // LayoutInflater inflater = LayoutInflater.from(getContext());
        //layoutView = inflater.inflate(R.layout.download_progressbar, null);
       // mProgressBar = (ProgressBar) layoutView.findViewById(R.id.style_progress);
       // mProgressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.progress_spin));
       // params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //params.addRule(RelativeLayout.CENTER_IN_PARENT);
        //layoutView.setLayoutParams(params);
        /*mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyle);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        // params.setMargins(30, 0, 30, 0);
        mProgressBar.setIndeterminate(false);
        Drawable d = this.getResources().getDrawable(R.drawable.spinner_48_outer_holo_new);
//        d.setBounds(0, 0, 0, 0);
        mProgressBar.setProgressDrawable(d);
        mProgressBar.setIndeterminateDrawable(d);
        mProgressBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.progress_spin));
        mProgressBar.setLayoutParams(params);
        mProgressBar.setMax(100);*/
       // this.addView(layoutView);
        //layoutView.setVisibility(View.GONE);
    }

    public void setBgUrl(String url) {
        if (mImageView != null) {
//			File f = new AQuery(mContext).getCachedFile(url);
//			if (f != null && f.canRead()){
//				try {
//					Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f));
//					mImageView.setImageBitmap(bm);
//					mImageView.setBackgroundResource(R.color.transparent);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//			setUrl(replaceUrl(url));
            setUrl(url);
        }
    }


    private void setUrl(String imageUrl) {

//        MyImageLoader.imageLoader.displayImage(imageUrl, mImageView, MyImageLoader.ViewPageroptions, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String s, View view) {
//            }
//
//            @Override
//            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                layoutView.setVisibility(GONE);
//            }
//
//            @Override
//            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                layoutView.setVisibility(GONE);
//                ImageView iv = (ImageView) view;
//                if (iv != null) {
//                    iv.setVisibility(View.VISIBLE);
////                    Bitmap bm = zoomImg(bitmap);
////                    if (null != bm) {
////                        iv.setImageBitmap(bitmap);
////                    }
//                    iv.setBackgroundColor(getResources().getColor(R.color.transparent));
//                }
//                if (dll != null)
//                    dll.downLoadSuccess(s);
//            }
//
//            @Override
//            public void onLoadingCancelled(String s, View view) {
//                layoutView.setVisibility(GONE);
//            }
//        });

        /**
         * 控制仅在wifi下加载图片
         */
        PhotoUtils.showCardOnlyInWifi(imageUrl, mImageView, MyDisplayImageOption.getOptions(R.drawable.pic_default), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
              //  layoutView.setVisibility(GONE);
                ImageView iv = (ImageView) view;
                iv.setScaleType(ScaleType.CENTER);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                //layoutView.setVisibility(GONE);
                ImageView iv = (ImageView) view;
                iv.setScaleType(ScaleType.CENTER);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
             //   layoutView.setVisibility(GONE);
                ImageView iv = (ImageView) view;
                iv.setScaleType(ScaleType.FIT_CENTER);
                if (dll != null)
                    dll.downLoadSuccess(s);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                //layoutView.setVisibility(GONE);
            }
        },R.drawable.pic_default);
        /*new AQuery(mContext).id(mImageView).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {
            @Override
            protected void callback(String url, ImageView iv, Bitmap bitmap, AjaxStatus status) {
                layoutView.setVisibility(GONE);
                if (iv != null) {
                    iv.setVisibility(View.VISIBLE);
                }
                if (bitmap != null && status.getCode() == 200) {
                    if (isHardwareAccelate()) {
                        int[] desSize = new int[2];
                        if (isOverMaxSize(bitmap.getWidth(), bitmap.getHeight(), desSize)) {
                            try {
                                Bitmap bitmapSample = zoomImg(bitmap, desSize[0], desSize[1]);
                                mImageView.setImageBitmap(bitmapSample);
                                if (!bitmap.isRecycled()) bitmap.recycle();
                            } catch (OutOfMemoryError e) {
                                e.printStackTrace();
                            }
                        } else
                            mImageView.setImageBitmap(bitmap);
                    } else {
                        mImageView.setImageBitmap(bitmap);
                    }
                    if (mUrl.equals(url) && iv.isShown()) {
                        Bitmap bt = zoomImg(bitmap);
                        if (null != bt)
                            iv.setImageBitmap(bt);
                    }
                    if (iv != null)
                        iv.setVisibility(View.VISIBLE);
                    if (dll != null)
                        dll.downLoadSuccess(url);
//				mImageView.setVisibility(VISIBLE);
                    iv.setBackgroundColor(getResources().getColor(R.color.transparent));

                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                    iv.setVisibility(View.VISIBLE);
//                    mImageView.setImageResource(R.drawable.about_logo_icon);
                }
            }
        });*/
    }

    protected boolean isOverMaxSize(int width, int height, int[] desSize) {
        desSize[0] = Math.min(2048, width);
        desSize[1] = Math.min(2048, height);
        return width > 2048 || height > 2048;
    }

    /**
     * 判断是否开启硬件加速
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected boolean isHardwareAccelate() {
        return VERSION.SDK_INT > 10 && mImageView.isHardwareAccelerated();
    }

    public void recyle() {
        mImageView.recycle();
    }

    public interface DownLoadLinstener {
        public void downLoadSuccess(String url);
    }

//	private String replaceUrl(String str) {
//		if (str == null)
//			return null;
//		return str.replace("!ios", "").replace("!android", "");
//	}
}
