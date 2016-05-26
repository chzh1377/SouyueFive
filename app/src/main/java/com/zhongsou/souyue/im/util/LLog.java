package com.zhongsou.souyue.im.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import com.zhongsou.souyue.net.UrlConfig;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * 日志打印类
 * Created by lvqiang on 15/12/31.
 */
public class LLog {
    //关闭debug
    private static final boolean DEBUBABLE = UrlConfig.isTest();

    public static void v(String tag, String msg) {
        if(DEBUBABLE){
            Log.v(tag, msg);
        }
    }
    public static void d(String tag, String msg) {
        if(DEBUBABLE){
            Log.d(tag, msg);
        }
    }
    public static void e(String tag, String msg) {
        if(DEBUBABLE){
            Log.e(tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if(DEBUBABLE){
            Log.i(tag, msg);
        }
    }

    /**
     * Enables strict mode. This should only be called when debugging the application and is useful
     * for finding some potential bugs or best practice violations.
     */
    @TargetApi(11)
    public static void enableStrictMode() {
        // Strict mode is only available on gingerbread or later
        if (hasGingerbread()) {

            // Enable all thread strict mode policies
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            // Enable all VM strict mode policies
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            // Honeycomb introduced some additional strict mode features
            if (hasHoneycomb()) {
                // Flash screen when thread policy is violated
                threadPolicyBuilder.penaltyFlashScreen();
                // For each activity class, set an instance limit of 1. Any more instances and
                // there could be a memory leak.
//                vmPolicyBuilder
//                        .setClassInstanceLimit(HomeActivity.class, 1);
            }

            // Use builders to enable strict mode policies
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    /**
     * Uses static final constants to detect if the device's platform version is Gingerbread or
     * later.
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Uses static final constants to detect if the device's platform version is Honeycomb or
     * later.
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }


    public static final boolean IS_WRITE=false;

    public static HashMap<String,Long> mMap = new HashMap<String, Long>();
    public static File mfile;
    public synchronized static void write(String key,String tag,long value){
        if (!IS_WRITE){
            mfile=new File("/storage/sdcard0/souyuelog/log.txt");
            if(mfile.exists()){
                File pare = mfile.getParentFile();
                mfile.delete();
                pare.delete();
            }
            return;
        }
        Long pre = mMap.get(key);
        if(pre==null){
            pre = 0l;
        }
        Long minus = value-pre;
        if (minus>10000000){
            minus=0L;
        }
        try {
            if(mfile==null){
                mfile=new File("/storage/sdcard0/souyuelog/log.txt");
                if(!mfile.exists()){
                    createFile(mfile);
                }
            }
            long length = mfile.length();

            RandomAccessFile output = new RandomAccessFile(mfile,"rw");
            try {
                String sss = key+"        "+tag+"        "+minus+'\n';
                output.seek(length);
                output.write(sss.getBytes());
            }finally {
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.put(key, value);
    }

    public static void createFile(File _file) throws IOException {
        if(!_file.exists()){
            File parent = _file.getParentFile();
            if(!parent.exists()) {
                parent.mkdirs();
            }
            _file.createNewFile();
        }
    }
}
