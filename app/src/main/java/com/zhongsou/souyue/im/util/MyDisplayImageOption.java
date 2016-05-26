package com.zhongsou.souyue.im.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.DeviceUtil;

/**
 * @author wangqiang  imageloader对应的option
 */
public class MyDisplayImageOption {

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_head) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.default_head) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_head) // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_head).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
            .displayer(new RoundedBitmapDisplayer(10)) // 设置成圆角图片
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build(); // 创建配置过的DisplayImageOpti

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
                Circleoptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.mine_head_defaultimg)
                        // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.mine_head_defaultimg)
                        // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.mine_head_defaultimg)
                        // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.mine_head_defaultimg)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(150)) // 设置成全圆图片
                .build();

        public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
                persalOption = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.circle_default_head)
                        // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.circle_default_head)
                        // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.circle_default_head)
                        // 设置图片加载或解码过程中发生错误显示的图片
                .showImageOnLoading(R.drawable.circle_default_head)
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(150)) // 设置成全圆图片
                .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            bigoptions = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_big) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.default_big) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_big) // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_big).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
            .build();

    public static DisplayImageOptions // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
            smalloptions = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.default_small) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.default_small) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.default_small) // 设置图片加载或解码过程中发生错误显示的图片
            .showImageOnLoading(R.drawable.default_small).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .displayer(new RoundedBitmapDisplayer(0))
            .build();


    public static DisplayImageOptions
            newoptions = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .showImageOnLoading(R.drawable.news_default_img_c)
            .showImageForEmptyUri(R.drawable.news_default_img_c)
            .showImageOnFail(R.drawable.news_default_img_c)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(150))
            .cacheInMemory(true).build();

    public static DisplayImageOptions homeTitle = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .resetViewBeforeLoading(true)
//                .postProcessor(new BitmapProcessor() {
//                    @Override
//                    public Bitmap process(Bitmap bitmap) {
//                        return getClipBitmap(bitmap);
////                        return bitmap;
//                    }
//                })
            .displayer(new RoundedBitmapDisplayer(100))
            .cacheInMemory(true)
            .showImageOnLoading(R.drawable.circle_title_default_icon_c)
            .showImageOnFail(R.drawable.circle_title_default_icon_c)
            .showImageForEmptyUri(R.drawable.circle_title_default_icon_c)
            .build();

    /**
     * 设置默认图
     *
     * @param resId
     * @return
     */
    public static DisplayImageOptions getOptions(int resId) {
        DisplayImageOptions
                options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .showImageOnLoading(resId)
                .showImageForEmptyUri(resId)
                .showImageOnFail(resId)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true).build();
        return options;
    }
    /**
     * 设置默认图
     *
     * @param drawable
     * @return
     */
    public static DisplayImageOptions getOptionsForDrawable(Drawable drawable) {
        DisplayImageOptions
                options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
//                .showImageOnLoading(drawable)
//                .showImageForEmptyUri(drawable)
//                .showImageOnFail(drawable)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .build();
        return options;
    }


    /**
     * 没有默认图
     */
    public static DisplayImageOptions defaultOption = new DisplayImageOptions.Builder()
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true).build();

    public static DisplayImageOptions getRoundOption(Context context, int dpRadius) {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(DeviceUtil.dip2px(context, dpRadius)))
                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .build();
    }
}
