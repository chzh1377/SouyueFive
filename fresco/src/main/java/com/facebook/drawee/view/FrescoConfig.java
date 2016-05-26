package com.facebook.drawee.view;

import android.content.Context;
import android.os.Environment;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.File;

/**
 * @description: Fresco 配置信息
 * @auther: qubian
 * @data: 2015/12/11.
 */

public class FrescoConfig {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;
    private static final String IMAGE_PIPELINE_CACHE_NAME = "souyue/image/fresco";
    private static ImagePipelineConfig sImagePipelineConfig;


//    public static FrescoConfig instance;
//    public static FrescoConfig getInstance() {
//        if (instance == null) {
//            synchronized (FrescoConfig.class) {
//                if (instance == null) {
//                    instance = new FrescoConfig();
//                }
//            }
//        }
//        return instance;
//    }
    public static void initFresco(Context context)
    {
        Fresco.initialize(context,getImagePipelineConfig(context));
    }

    /**
     *  配置ImagePipeline
     * @param context
     * @return
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            configureCaches(configBuilder, context);
            configureLoggingListeners(configBuilder);
            sImagePipelineConfig = configBuilder.build();
        }
        return sImagePipelineConfig;
    }

    /**
     * 配置内存和存储位置
     * @param configBuilder
     * @param context
     */
    private static void configureCaches(
            ImagePipelineConfig.Builder configBuilder,
            Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE,
                Integer.MAX_VALUE,
                MAX_MEMORY_CACHE_SIZE,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        configBuilder.setBitmapMemoryCacheParamsSupplier(new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        }).setMainDiskCacheConfig(DiskCacheConfig.newBuilder()
                                .setBaseDirectoryPath(new File(getDirectoryPath(context)))
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_NAME)
                                .build());

    }

    /**
     * 判断磁盘是否存在
     * @param context
     * @return
     */
    public static String getDirectoryPath(Context context)
    {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())    //外部存储是否挂载
                || !Environment.isExternalStorageRemovable())   //外部存储是否移除
                ) {
            cachePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static File getCacheFilePath(Context context)
    {
        return  new File(getDirectoryPath(context)+"/"+IMAGE_PIPELINE_CACHE_NAME);
    }

    /**
     *  设置Log 监听
     * @param configBuilder
     */
    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setRequestListeners(
                Sets.newHashSet((RequestListener) new RequestLoggingListener()));
    }

    /**
     * 控制滑动过程中 停止加载
     *
     */
    public static void stopLoad()
    {
//        Fresco.shutDown();
    }

    /**
     * 控制滑动过程 后 开始加载
     */
    public static void startLoad(Context context)
    {
//        initFresco(context);
    }

    /**
     * 终极者
     *
     */
    public  static void shutDown()
    {
        Fresco.shutDown();
    }

    /**
     * 清理磁盘缓存
     */
    public static void clearDiskCache(){
        Fresco.getImagePipeline().clearDiskCaches();
    }

    /**
     * 清理内存缓存
     */
    public static void  clearMemeryCache()
    {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    /**
     * 清理内存和磁盘缓存
     */
    public static void clear()
    {
        clearDiskCache();
        clearMemeryCache();
    }

}
