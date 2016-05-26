package com.zhongsou.souyue.net.moduleparse;

import android.content.Context;
import android.os.Environment;

import com.zhongsou.souyue.circle.activity.DetailModuleActivity;
import com.zhongsou.souyue.utils.TemplateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by lvqiang on 15/6/8.
 */
public abstract class ABaseParse {

    protected static String DETAIL_MODULE;
    public static final String CHARSET = "utf-8";
    public static final String MODULE_DIR = "module";
    public static final int MODULE_VERSION = 1;
    public static HashMap<String, String> mModules;

    protected Context mContext;
    protected String mModulePath;
    protected String mAppPath;

    public void initModule(Context _context) {
        mContext = _context;
        mModulePath = MODULE_DIR + "_" + MODULE_VERSION;
        mModules = new HashMap<String, String>();
        mAppPath = getApplicationPath();
    }

//    public String getTempStringFromAssets(String dir) {
//        StringBuilder build = new StringBuilder();
//        try {
//            InputStream in = mContext.getAssets().open(dir);
//            InputStreamReader file = new InputStreamReader(in);
//            BufferedReader read = new BufferedReader(file);
//            String line;
//            while ((line = read.readLine()) != null) {
//                build.append(line);
//            }
//            DETAIL_MODULE = build.toString();
//            in.close();
//            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//        return build.toString();
//    }

    public String getTempStringByPath(String dir, String path) {
        String absolutePath = dir + path;
        StringBuilder build = new StringBuilder();
        try {
            File file = new File(absolutePath);
            InputStream in = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader read = new BufferedReader(inputStreamReader);
            String line;
            while ((line = read.readLine()) != null) {
                build.append(line);
            }
            DETAIL_MODULE = build.toString();
            in.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

        return build.toString();
    }

//    protected String getDetailModule(String id) {
//        String data = mModules.get(id);
//        if (data == null || data.equals("")) {
//            String dir = mModulePath + "/" + id + "/" + TemplateUtils.MODULE_TEMPLATE_NAME;
////            data = getTempStringFromAssets(dir);
//            mModules.put(id, data);
//        }
//        return data;
//    }

    public String getStringFromFile(File file) {
        StringBuilder build = new StringBuilder();
        try {
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), CHARSET);//考虑到编码格式
            BufferedReader buffer = new BufferedReader(read);

            String line;
            while ((line = buffer.readLine()) != null) {
                build.append(line);
            }
            read.close();
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return build.toString();
    }


    public String getExtraPath() {
        String path = getSDPath();
        if (path == null) {
            path = getApplicationPath();
        }
        return path;
    }

    private String getApplicationPath() {
        String basepath = mContext.getApplicationContext().getFilesDir().getAbsolutePath();
        return basepath;
    }


    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        } else {
            return null;
        }
    }

}
