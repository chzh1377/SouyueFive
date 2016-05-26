package com.zhongsou.souyue.im.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.io.File;

/**
 * @author wangqiang  图片加载器
 */
public class PhotoUtils {


    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    private static ImageLoader imageLoader = ImageLoader.getInstance();

    private static boolean isWifi = !CMainHttp.getInstance().isWifi(MainApplication.getInstance());

    private static DisplayImageOptions defaultDisplayImageOptions;

    public static void retainWifi(){
        isWifi = !CMainHttp.getInstance().isWifi(MainApplication.getInstance());
    }

    private static DisplayImageOptions getDefaultDisplayImageOptions() {
        defaultDisplayImageOptions = MyDisplayImageOption.newoptions;
        return defaultDisplayImageOptions;
    }

    /**
     * 仅在wifi情况下显示图片
     */
    public static void showCardOnlyInWifi(String uri, ImageView imageView, DisplayImageOptions options,
                                          ImageLoadingListener listener,int picResWithOutWifi){

        if(SYSharedPreferences.getInstance().getLoadWifi(MainApplication.getInstance())){
            if(CMainHttp.getInstance().isWifi(MainApplication.getInstance())) {
                //如果是wifi环境，加载正常图片
                imageLoader.displayImage(uri, imageView, options, listener);
            }else{
                if(isBitmapExist(uri)){
                    //如果大图存在，加载图
                    imageLoader.displayImage(uri, imageView, options, listener);
                }else {
                    //否则加载默认图
                    imageLoader.displayImage("", imageView, options, listener);
                }
            }
        }else{
            //关闭选项，正常加载
            imageLoader.displayImage(uri, imageView, options, listener);
        }
    }

    public static void showCard( UriType uriType, String photoPath, ImageView iv) {
        try {
            showCard(uriType, photoPath, iv, null);
        } catch (OutOfMemoryError oom) {
            System.out.println("oom exception");
        }
    }

    public static void showCard( UriType uriType, String photoPath,
                                ImageView iv, DisplayImageOptions options) {
        showCard(uriType, photoPath, iv, options, null);
    }

    public static void showCard(UriType uriType, String photoPath,
                                ImageView iv, DisplayImageOptions options,
                                ImageLoadingListener listener) {
        try {
            if (options == null) {
                options = getDefaultDisplayImageOptions();
            }
            String url = uriType.toString() + photoPath;
            boolean picExist = isBitmapExist(url);
            boolean onlyWifi = getSetting();
            if (!picExist && onlyWifi) {
                url = "";
            }
            Slog.d("callback", "url---------------" + url);
            imageLoader.displayImage(url, iv, options, listener);
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }
    }

    /**
     * 直接显示
     *
     * @param context
     * @param uriType
     * @param photoPath
     * @param iv
     * @param options
     * @param listener
     */
    public static void showNoCondition(Context context, UriType uriType, String photoPath,
                                 ImageView iv, DisplayImageOptions options,
                                 ImageLoadingListener listener) {
        try {
            String url = uriType.toString() + photoPath;
            imageLoader.displayImage(url, iv, options, listener);
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();
        }
    }

    private static boolean isBitmapExist(String url) {
        Bitmap bmp = ImageLoader.getInstance().getMemoryCache().get(url);
        File diskFile = ImageLoader.getInstance().getDiskCache().get(url);
        return bmp != null || (diskFile != null && diskFile.exists());
    }

    private static boolean getSetting() {
        return isWifi && SYSharedPreferences.getInstance().getLoadWifi(MainApplication.getInstance().getApplicationContext()) ;
    }

    public static enum UriType {
        HTTHPS(""), HTTP(""), FILE("file://"), DRAWABLE("drawable://"), ASSETS(
                "assets://");

        private final String value;

        UriType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

}
