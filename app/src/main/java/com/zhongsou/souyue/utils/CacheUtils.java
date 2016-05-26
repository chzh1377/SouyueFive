package com.zhongsou.souyue.utils;

import android.content.Context;
import com.androidquery.util.AQUtility;
import com.facebook.drawee.view.FrescoConfig;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.ReadHistoryHelper;

import java.io.File;

public class CacheUtils {
    public static final String FRESCO_GIF_PATH ="souyue/image";

    public static void clearWebViewCache() {
        Context context = MainApplication.getInstance();
        clearWebViewCache(context.getCacheDir(), System.currentTimeMillis());

        // 清理CustomWebView  中webSetting 的缓存位置
        File webCache   = new File(context.getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath());
        if (webCache != null&& webCache.exists()) {
            clearWebViewCache(webCache,System.currentTimeMillis());
        }
        // context.deleteDatabase(CACHE_DATA_FILE);
        // clearWebView(context);
    }

    // private static final String VIEW_DATA_FILE = "webview.db";
    // private static final String CACHE_DATA_FILE = "webviewCache.db";
    //
    // private static void clearWebView(Context context){
    // SQLiteDatabase mDatabase = context.openOrCreateDatabase(VIEW_DATA_FILE, 0, null);
    // if(mDatabase != null) {
    // mDatabase.delete("cookies", null, null);
    // mDatabase.delete("formdata", null, null);
    // mDatabase.delete("formurl", null, null);
    // mDatabase.delete("httpauth", null, null);
    // mDatabase.delete("password", null, null);
    // mDatabase.delete("tcppreconnection", null, null);
    // mDatabase.close();
    // }
    // }

    private static int clearWebViewCache(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearWebViewCache(child, numDays);
                    }
                    if (child.delete()) {
                        deletedFiles++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 获得系统缓存大小，包括aquery和webview，
     * 
     * @return
     */
    public static String getSize() {
        Context context = MainApplication.getInstance();
        long size=0;
        File aquaryFile=AQUtility.getCacheDir(context);
        if(aquaryFile!=null&&aquaryFile.exists()){
            size= FileUtils.sizeOfDirectory(AQUtility.getCacheDir(context));
        }
        long ImageLoadersize = 0;
        File imageLoaderFile=StorageUtils.getCacheDirectory(context);
        if(imageLoaderFile!=null&&imageLoaderFile.exists()){
            ImageLoadersize= FileUtils.sizeOfDirectory(StorageUtils.getCacheDirectory(context));
        }
        File frescoFile = FrescoConfig.getCacheFilePath(context);
        long FrescoSize = 0;
        if(frescoFile!=null&& frescoFile.exists())
        {
            FrescoSize= FileUtils.sizeOfDirectory(frescoFile);
        }
        File gifFile = getGifFile(context);
        long gifSize = 0;
        if(gifFile!=null&& gifFile.exists())
        {
            gifSize= FileUtils.sizeOfDirectory(gifFile);
        }
        return FileUtils.byteCountToDisplaySize(size + ImageLoadersize+FrescoSize+gifSize);

    }

    /**
     * 清缓存
     */
    public static void clear() {
        // long triggerSize = 3000000;
        // long targetSize = 2000000;
        long triggerSize = 0;
        long targetSize = 0;
        ReadHistoryHelper.getInstance().deleteAll();
        Context context = MainApplication.getInstance();
        File cacheDir = AQUtility.getCacheDir(context);
        File imageLoaderCache = StorageUtils.getCacheDirectory(context);
        LogDebugUtil.v("cache", "cacheDir=" + cacheDir.getAbsolutePath());
        AQUtility.cleanCache(cacheDir, triggerSize, targetSize);
        AQUtility.cleanCache(imageLoaderCache, triggerSize, targetSize);
        HomePageDBHelper.getInstance().deleteAll();//清除首页列表缓存，清除本地数据库
        Utils.clearTimeMap();
    }
    /**
     * 清除ImageLoader缓存
     */
    public static void clearImageLoader() {
        Context context = MainApplication.getInstance();
        if(StorageUtils.getCacheDirectory(context).exists()){
            clearWebViewCache(StorageUtils.getCacheDirectory(context), System.currentTimeMillis()); 
        }

    }

    /**
     * 清理gif
     *
     */
    public static void clearGif()
    {
        Context context = MainApplication.getInstance();
        if(getGifFile(context).exists()){
            clearWebViewCache(getGifFile(context), System.currentTimeMillis());
        }
    }

    /**
     * GIF 所在路径
     * @param context
     * @return
     */
    public static File getGifFile(Context context)
    {
        return  new File(FrescoConfig.getDirectoryPath(context)+"/"+ FRESCO_GIF_PATH);
    }
}
