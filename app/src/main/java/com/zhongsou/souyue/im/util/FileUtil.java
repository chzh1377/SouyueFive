package com.zhongsou.souyue.im.util;

import android.graphics.Bitmap;
import android.widget.Toast;
import com.google.zxing.WriterException;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.ImageUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 文件操作工具类
 * 
 * @author wangqiang
 * 
 */
public class FileUtil {

	public static boolean createFolder(String dirPath) {
		return createFolder(dirPath, false);
	}

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 * @param overWrite
	 * @return
	 */
	public static boolean createFolder(String dirPath, boolean overWrite) {
		if (StringUtil.isBlank(dirPath))
			return false;
		File dirFile = new File(dirPath);
		if (dirFile.exists()) {
			if (overWrite) {
				deleteFile(dirPath);
				return dirFile.mkdirs();
			}
			return true;
		} else {
			dirFile.mkdirs();
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String path) {
		if (StringUtil.isBlank(path)) {
			return true;
		}

		File file = new File(path);
		if (!file.exists()) {
			return true;
		}
		if (file.isFile()) {
			return file.delete();
		}
		if (!file.isDirectory()) {
			return false;
		}
		for (File f : file.listFiles()) {
			if (f.isFile()) {
				f.delete();
			} else if (f.isDirectory()) {
				deleteFile(f.getAbsolutePath());
			}
		}
		return file.delete();
	}

	/**
	 * 读取配置文件内容
	 * 
	 * @param filePath
	 * @return
	 */
	public static StringBuffer readFile(String filePath) {
		StringBuffer buffer = new StringBuffer();
		try {
			InputStream is = new FileInputStream(filePath);
			String line; // 用来保存每行读取的内容
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			line = reader.readLine(); // 读取第一行
			while (line != null) { // 如果 line 为空说明读完了
				buffer.append(line); // 将读到的内容添加到 buffer 中
				buffer.append("\n"); // 添加换行符
				line = reader.readLine(); // 读取下一行
			}
			reader.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer;
	}

	/**
	 * 文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		if (StringUtil.isBlank(filePath)) {
			return false;
		}

		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	/**
	 * 列举指定文件夹下的所有文件
	 * 
	 * @param dirFile
	 * @return
	 */
	public static String[] list(File dirFile) {
		if (dirFile != null && dirFile.isDirectory())
			return dirFile.list();
		return null;
	}
    /**
     * android4.4专用  网上搜不到的
     * @param qrCodeBitmap
     * @throws WriterException
     */
    public static String saveImg(Bitmap qrCodeBitmap,String name) throws WriterException {
        String dir = ImageUtil.getDir();
        File path = new File(dir);
        if (!path.exists()) {
            path.mkdirs();
        }
        OutputStream os = null;
        File file = new File(path, name);
        try {
            String savePath = file.getAbsolutePath();
            os = new FileOutputStream(savePath);
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Toast.makeText(MainApplication.getInstance(), R.string.down_image_fail, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
}