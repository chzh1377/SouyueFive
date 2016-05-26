package com.zhongsou.souyue.im.util;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.ExpressionCollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * 解压工具
 * 
 * 
 * 
 * @author wangqiang
 * 
 * 
 */

public class ZipUtil {

	private static String TAG = ZipUtil.class.getSimpleName();

	/**
	 * 解压
	 * 
	 * @param a
	 * @param is
	 * @param destinationFolder
	 * @param overwrite
	 */
	public static boolean extractZip(Context context, InputStream is,
			File destinationFolder) {
		if (!destinationFolder.exists())
			destinationFolder.mkdirs();
		try {
			ZipInputStream zin = new ZipInputStream(is);
			ZipEntry zentry = null;
			while ((zentry = zin.getNextEntry()) != null) {
				boolean isDir = zentry.isDirectory();
				if (isDir) {
					// zentry.getName()
					String fileName = zentry.getName();
					System.out.println(fileName);
					File createDir = new File(destinationFolder, fileName);
					createDir.mkdirs();

				} else {
					FileOutputStream fout = new FileOutputStream(new File(
							destinationFolder, zentry.getName()));
					int c = 0;
					byte[] buffer = new byte[64 * 1024];
					while ((c = zin.read(buffer)) > 0) {
						fout.write(buffer, 0, c);
					}
					fout.close();
				}
				zin.closeEntry();
			}
			zin.close();

		} catch (Exception e) {
			Log.e("Decompress", "unzip", e);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 读取表情配置文件
	 * 
	 * @param context
	 * 
	 * @param paramString1
	 * 
	 * @param paramString2
	 * 
	 * @return
	 */

	public static List<ExpressionBean> readExpressionEntity(Context context,
			String dirPath, String packageName) {
		List<ExpressionBean> beans = null;
		File dir = new File(dirPath);
		if (!dir.exists())
			dir.mkdirs();
		StringBuffer sb = FileUtil
				.readFile(dirPath + File.separator + packageName
						+ File.separator + Constants.PACKAGE_CONFIGUREURL);
		if (sb == null) {
			Slog.d("callback", "read configure has problem");
			return null;
		}
		Slog.d("callback", "config text:----------" + sb.toString());
		try {
			beans = ((ExpressionCollection) new Gson().fromJson(sb.toString(),
					ExpressionCollection.class)).getList();
			if (beans != null)
				Slog.d("callback", "-------------" + beans.size());
			return beans;
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}

	}



}
