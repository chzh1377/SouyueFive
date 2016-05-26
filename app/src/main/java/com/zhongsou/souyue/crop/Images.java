package com.zhongsou.souyue.crop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * @author wanglong@zhongsou.com
 *
 */
public class Images {
	public static String filePath = new File(Environment.getExternalStorageDirectory(), "suyue_temp.jpg").getAbsolutePath();
	public static String saveMyBitmap(Bitmap mBitmap, String path) {
		File f = new File(path);
		FileOutputStream fOut = null;
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fOut != null) {
				try {
					fOut.close();
				} catch (IOException e) {
				}
			}
		}
		return filePath;
	}
	public static Bitmap decodeFile(File f, int maxSize) {
		if (!f.exists()) {
			return null;
		}
		Bitmap b = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();
			int scale = 1;
			if (o.outHeight <= 0 || o.outWidth <= 0) {
				o.outHeight = o.outWidth = 3000;
			}
			if (o.outHeight > maxSize || o.outWidth > maxSize) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(maxSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return b;
	}
	public static void decodeImageFile(File f, int maxSize) {
		if (f == null || !f.exists() || f.isDirectory() || !f.canRead()) {
			return;
		}
		Bitmap mBitmap = decodeFile(f, maxSize);
		if (mBitmap == null) {
			return;
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fOut != null) {
				try {
					fOut.close();
				} catch (IOException e) {
				}
			}
			if (mBitmap.isRecycled() == false) {
				mBitmap.recycle();
			}
		}
	}
}
