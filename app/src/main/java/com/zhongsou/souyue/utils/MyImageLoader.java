package com.zhongsou.souyue.utils;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongsou.souyue.R;

/**
 * Created by yinguanping on 14-10-22.
 */
public class MyImageLoader {
    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_head)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_head)       // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_head)
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                          // 设置下载的图片是否缓存在SD卡中
            .displayer(new RoundedBitmapDisplayer(10))// 设置成圆角图片
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();                               // 创建配置过的DisplayImageOption对象


    public static ImageLoader imageLoader = ImageLoader.getInstance();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            Circleoptions       = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.mine_head_defaultimg)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.mine_head_defaultimg)       // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.mine_head_defaultimg)
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .displayer(new RoundedBitmapDisplayer(150))  // 设置成全圆图片
            .build();
    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            subrecommendOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.subrecommend_listitem_default_icon)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.subrecommend_listitem_default_icon)       // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.subrecommend_listitem_default_icon)
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .displayer(new RoundedBitmapDisplayer(150))  // 设置成全圆图片
            .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            ViewPageroptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_big)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_big)       // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_big)
            .cacheInMemory(true)                          // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                           // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            userReplyListOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.userreply_default)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.userreply_default)       // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.userreply_default)
            .cacheInMemory(true)                          // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                           // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            mCreateShort = new DisplayImageOptions.Builder()
            .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            mShareImage = new DisplayImageOptions.Builder()
            .cacheInMemory(false) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
            .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            UserBgoptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.circle_vcard_default_top_bg)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.circle_vcard_default_top_bg)       // 设置图片加载或解码过程中发生错误显示的图片
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true)                          // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)                           // 设置下载的图片是否缓存在SD卡中
            .build();

    /*private static Bitmap zoomImg(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        try {
            Log.v("创建图片", "图片宽高：" + width + "," + height);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
//            options.inSampleSize
            int[] a=getNewMatrix(width, height);
            options.outWidth = a[0];
            options.outHeight = a[1];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(is, null, options);
//            return Bitmap.createBitmap(bm, 0, 0, width, height, getNewMatrix(width, height), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    *//**
     * add by yinguanping 根据图片宽高比例计算宽或高一条边充满屏幕的新的宽高比例
     *
     * @return
     *//*
    private static int[] getNewMatrix(float width, float height) {
        Matrix matrix = new Matrix();
        float newWidth = 0;
        float newHeight = 0;
        float screenW = Utils.ScreenWidth;
        float screenH = Utils.ScreenHeight;
        if (width >= screenW && height <= screenH) {
            newWidth = screenW;
            newHeight = (((float) screenW / width)) * height;
        } else if (width < screenW && height > screenH) {
            newWidth = ((float) (screenH * width) / height);
            newHeight = screenH;
        } else {//宽高都大于屏幕宽高或宽高都小于屏幕宽高
            if ((screenW / screenH) >= (width / height)) {
                newWidth = (((float) width / height)) * screenH;
                newHeight = screenH;
            } else {
                newWidth = screenW;
                newHeight = ((float) (height / width)) * screenW;
            }
        }
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return new int[]{(int)scaleWidth,(int)scaleHeight};
    }

    private static Bitmap getClipBitmap(Bitmap _bitmap) {
        int w = _bitmap.getWidth();
        int h = _bitmap.getHeight();
        int bitSize = 0;
        int x = 0;
        int y = 0;
        if (w > h) {
            bitSize = h;
            x = (w - h) / 2;
        } else {
            bitSize = w;
            y = (h - w) / 2;
        }
        return Bitmap.createBitmap(_bitmap, x, y, bitSize, bitSize);
    }*/
}
