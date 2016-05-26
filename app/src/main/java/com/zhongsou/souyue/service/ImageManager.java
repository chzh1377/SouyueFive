package com.zhongsou.souyue.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 保存图片到系统相册，并在相册中显示
 *
 * @author zhangliang01@zhongsou.com
 */
public class ImageManager {

    private static final Uri STORAGE_URI = Images.Media.EXTERNAL_CONTENT_URI;
    private static final int SUCCESS = 0;
    private static final int FAIL = 1;
    private static final int EXIST = 2;

    /**
     * Save iamge to Gallery
     *
     * @param cr
     * @param title
     * @param dateTaken
     * @param directory
     * @param filename
     * @param source
     * @param f
     * @return URI
     */
    public static Uri addImage(ContentResolver cr, String title, long dateTaken, String directory, String filename, Bitmap source, File f) {

        //1.保存图片
        if (!hasStorage() || filename == null) return null;
        OutputStream outputStream = null;
        directory = getDir() + directory;
        String filePath = directory + "/" + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(directory, filename);
            outputStream = new FileOutputStream(file);
            if (source != null) {
                source.compress(CompressFormat.JPEG, 75, outputStream);
            } else if (f != null) {
                outputStream.write(getBytes(f));
            } else
                return null;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //2 插入系统数据库
        // Read back the compressed file size.
        long size = new File(directory, filename).length();
        ContentValues values = new ContentValues(9);
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, filename);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);
        return cr.insert(STORAGE_URI, values);
    }


    public static int saveImageToGallery(Context context, String title, long dateTaken, String directory, String filename, Bitmap source, File f) {

        //判断是否存在
        ContentResolver cr = context.getContentResolver();
        String selection = Images.ImageColumns.PICASA_ID + " = ? ";
        String[] selectionArgs = new String[]{f.getAbsolutePath()};
        String[] projection = new String[]{Images.ImageColumns.PICASA_ID};
        Cursor c = cr.query(STORAGE_URI, projection, selection, selectionArgs, null);

        if (c != null && c.moveToNext()) {
            c.close();
            c = null;
            return EXIST;
        }

        //1.保存图片
        File file = null;
        if (!hasStorage() || filename == null) return FAIL;
        OutputStream outputStream = null;
        directory = getDir() + directory;
        String filePath = directory + "/" + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) dir.mkdirs();
            file = new File(directory, filename);
            outputStream = new FileOutputStream(file);
            if (source != null) {
                source.compress(CompressFormat.JPEG, 75, outputStream);
            } else if (f != null) {
                outputStream.write(getBytes(f));
            } else
                return FAIL;
        } catch (FileNotFoundException ex) {
            return FAIL;
        } catch (IOException ex) {
            return FAIL;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        long size = new File(directory, filename).length();
        ContentValues values = new ContentValues(9);
        values.put(Images.Media.TITLE, title);
        values.put(Images.Media.DISPLAY_NAME, filename);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.MIME_TYPE, "image/jpeg");
        values.put(Images.Media.ORIENTATION, 0);
        values.put(Images.Media.DATA, filePath);
        values.put(Images.Media.SIZE, size);
        values.put(Images.Media.PICASA_ID, f.getAbsolutePath());

        Uri uri = cr.insert(STORAGE_URI, values);

        // 最后通知图库更新
        context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
        return 0;


    }

    /**
     * file to byte
     *
     * @param file
     * @return
     */
    private static byte[] getBytes(File file) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * check sdcard status
     *
     * @return
     */
    private static boolean hasStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return checkFsWritable();
        }
        return false;
    }

    /**
     * check DCIM Directory
     *
     * @return
     */
    private static boolean checkFsWritable() {
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        return directory.canWrite();
    }

    /**
     * Get CDIM Directory
     *
     * @return
     */
    private static String getDir() {
        return Environment.getExternalStorageDirectory().toString() + "/DCIM/";
    }

}
