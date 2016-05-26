/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods. Warning: Methods might changed in future versions.
 */

public class AQUtility {

    private static boolean debug = true;
    private static final String VALINO_PATH = "/souyue/login/temp/valino/";
    private static final String FILE_VALINO = "valiNo";

    public static void warn(Object msg, Object msg2) {
        Log.w("AQuery", msg + ":" + msg2);
    }

    public static void debug(Object msg, Object msg2) {
        if (debug) {
            Log.w("AQuery", msg + ":" + msg2);
        }
    }

    public static void debug(Throwable e) {
        if (debug) {
            String trace = Log.getStackTraceString(e);
            Log.w("AQuery", trace);
        }
    }

    public static void report(Throwable e) {
        if (e == null) return;
        try {
            //debug(e);
            warn("reporting", Log.getStackTraceString(e));

            if (eh != null) {
                eh.uncaughtException(Thread.currentThread(), e);
            }
        } catch (Exception ex) {
            AQUtility.debug(ex);
        }
    }

    private static UncaughtExceptionHandler eh;

    private static Map<String, Long> times = new HashMap<String, Long>();

    public static void time(String tag) {
        times.put(tag, System.currentTimeMillis());
    }

    public static void transparent(View view, boolean transparent) {

        float alpha = 1;
        if (transparent) alpha = 0.5f;

        setAlpha(view, alpha);
    }

    private static void setAlpha(View view, float alphaValue) {

        if (alphaValue == 1) {
            view.clearAnimation();
        } else {
            AlphaAnimation alpha = new AlphaAnimation(alphaValue, alphaValue);
            alpha.setDuration(0); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            view.startAnimation(alpha);
        }
    }

    private static File cacheDir;

    public static File getCacheDir(Context context) {

        if (cacheDir == null) {
            cacheDir = new File(context.getCacheDir(), "aquery");
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static File getCachevaliNoDir(Context context) {
        if(context == null){
            return null;
        }
        if (cacheDir == null) {
            cacheDir = new File(context.getCacheDir(), FILE_VALINO);
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static void cleanCache(File cacheDir, long triggerSize, long targetSize) {
        try {
            File[] files = cacheDir.listFiles();
            if (files == null) return;
            Arrays.sort(files, new SortComparator());
            if (testCleanNeeded(files, triggerSize)) {
                cleanCache(files, targetSize);
            }

            File temp = getTempDir();
            if (temp != null && temp.exists()) {
                cleanCache(temp.listFiles(), 0);
            }
        } catch (Exception e) {
            AQUtility.report(e);
        }
    }

    private static class SortComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {

            long m1 = f1.lastModified();
            long m2 = f2.lastModified();

            if(m2 > m1){
                return 1;
            }else if(m2 == m1){
                return 0;
            }else{
                return -1;
            }
        }
    }

    public static File getTempDir() {
        File ext = Environment.getExternalStorageDirectory();
        File tempDir = new File(ext, "aquery/temp");
        tempDir.mkdirs();
        if (!tempDir.exists()) {
            return null;
        }
        return tempDir;
    }

    private static boolean testCleanNeeded(File[] files, long triggerSize) {
        long total = 0;
        for (File f : files) {
            total += f.length();
            if (total > triggerSize) {
                return true;
            }
        }
        return false;
    }

    private static void cleanCache(File[] files, long maxSize) {

        long total = 0;
        int deletes = 0;

        for (int i = 0; i < files.length; i++) {

            File f = files[i];

            if (f.isFile()) {

                total += f.length();

                if (total < maxSize) {
                    //ok
                } else {
                    f.delete();
                    deletes++;
                    //AQUtility.debug("del", f.getAbsolutePath());
                }
            }
        }
        AQUtility.debug("deleted", deletes);
    }

    private static Context context;

    private static File valiNoFile() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        File file;
        File fileValiNo;
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
            String path = sdDir.getAbsolutePath() + VALINO_PATH;
            file = new File(path);
            if (!file.exists())
                file.mkdirs();
            fileValiNo = new File(path + FILE_VALINO);
            if (!fileValiNo.exists()) {
                try {
                    fileValiNo.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fileValiNo;
        } else {
            file = getCachevaliNoDir(context);
            fileValiNo = new File(file.getAbsolutePath() + FILE_VALINO);
            if (!fileValiNo.exists()) {
                try {
                    fileValiNo.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file;
        }
    }

    public static String readValiNoFile() {
        RandomAccessFile f;
        byte[] bytes = null;
        try {
            f = new RandomAccessFile(valiNoFile(), "r");
            bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bytes == null) {
            return "";
        }
        return new String(bytes);
    }
}