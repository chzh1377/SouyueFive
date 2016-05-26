package com.zhongsou.souyue.service.download;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {

	public static File getCacheDirectory(Context context) {
		File appCacheDir = null;
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	public static File getExternalCacheDir(Context context) {
        File dataDir = new File(Environment.getExternalStorageDirectory(), "Download");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                return null;
            }
        }
        return dataDir;
    }
}
