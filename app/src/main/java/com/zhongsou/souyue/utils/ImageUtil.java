package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.WriterException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageUtil {
	private static String TAG = "IMGS";
	private static final String TEMP_PATH = "/souyue/share/temp/";
	private static final String SELF_TEMP_PATH = "/souyue/self/temp/";
	public static final String GIF_PATH = "/souyue/gif/";
	public static final String GIF_SDCARD_PATH = "file:///sdcard/souyue/gif/";
	private static String PIC_PATH = null;
	private static String SELF_PIC_PATH = null;

	public static int getColorByKey(String k) {
		int caseInt = 0;
		if (k == null || k.trim().length() == 0)
			caseInt = -1;
		else
			caseInt = (k.length() % 4);
		switch (caseInt) {
		case 0:
			return R.color.lan;
		case 1:
			return R.color.lvse;
		case 2:
			return R.color.cheng;
		case 3:
			return R.color.huang;
		default:
			return R.color.lan;
		}
	}

	public static void saveBitmapToFile(Bitmap bitmap, String filename) {

		FileOutputStream fos = null;
		File file = new File(filename);
		if (file.exists()) {
			return;
		}
		if (!file.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			if (!file.getParentFile().mkdirs()) {
				return;
			}
		}
		try {
			fos = new FileOutputStream(file);
			fos.write(bitmap2byte(bitmap));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static byte[] bitmap2byte(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
		return baos.toByteArray();
	}

	public static String getfilename() {
		String dir = getDir();
		if (dir != null) {
			long time = System.currentTimeMillis();
			return dir + time + ".png";
		} else {
			return null;
		}

	}

	public static String getDir() {
		if (PIC_PATH == null) {
			// 判断sd卡是否存在
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
			if (sdCardExist) {
				File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
				String path = sdDir.getAbsolutePath() + TEMP_PATH;
				PIC_PATH = path;
				return path;
			} else {
				return null;
			}
		} else {
			return PIC_PATH;
		}
	}

	public static String chooseDir(String path1) {
		// 判断sd卡是否存在
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			String path = sdDir.getAbsolutePath() + path1;
			return path;
		} else {
			return null;
		}
	}

	public static String getSelfDir() {
		if (SELF_PIC_PATH == null) {
			// 判断sd卡是否存在
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
			if (sdCardExist) {
				File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
				String path = sdDir.getAbsolutePath() + SELF_TEMP_PATH;
				SELF_PIC_PATH = path;
				return path;
			} else {
				return null;
			}
		} else {
			return SELF_PIC_PATH;
		}
	}

	public static boolean delTempShareImages() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardExist) {
			return false;
		}
		return ImageUtil.delAllFile(getDir());
	}

	// public static boolean fileExists(String filename) {
	// File file = new File(filename);
	// return file.exists();
	// }

	public static boolean delAllFile(String path) {
		boolean flag = false;
		if (StringUtils.isEmpty(path)) {
			return flag;
		}
		File file = new File(path);
		if (null == file || !file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		if(tempList == null)
			return true;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public static Bitmap readScaledBitmap(byte[] data, int width, int height)
	// {
	// if (data == null || data.length < 1)
	// return null;
	// Bitmap bm = null;
	// InputStream is = null;
	// try {
	// // get real width/height of an image
	// is = new ByteArrayInputStream(data);
	// BitmapFactory.Options opts = new BitmapFactory.Options();
	// opts.inJustDecodeBounds = true;
	// BitmapFactory.decodeStream(is, null, opts);
	// int ph = opts.outHeight, pw = opts.outWidth;
	// close(is);
	//
	// // read scale image
	// is = new ByteArrayInputStream(data);
	// opts = new BitmapFactory.Options();
	// opts.inSampleSize = Math.max(ph / height, pw / width);
	// opts.inPurgeable = true;
	// opts.inScaled = true;
	// Log.d(TAG, "inSampleSize = " + opts.inSampleSize);
	// bm = BitmapFactory.decodeStream(is, null, opts);
	// if (bm != null)
	// Log.d(TAG, String.format("converted size: %dx%d",
	// bm.getWidth(), bm.getHeight()));
	// } catch (Throwable e) {
	// e.printStackTrace();
	// } finally {
	// close(is);
	// }
	// return bm;
	// }

	/**
	 * Drawable到Bitmap的转换
	 * 
	 * @return
	 */
    public static Bitmap drawableToBitmap(int width, int height) {
        InputStream is = MainApplication.getInstance().getResources().openRawResource(R.drawable.weixin_logo);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = width;
        options.outHeight = height;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        return bitmap;
    }

	public static void close(Closeable closeable) {
		if (closeable != null)
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	// public static byte[] bmpToByteArray(final Bitmap bmp,
	// final boolean needRecycle) {
	// ByteArrayOutputStream output = new ByteArrayOutputStream();
	// bmp.compress(CompressFormat.PNG, 100, output);
	// if (needRecycle) {
	// bmp.recycle();
	// }
	//
	// byte[] result = output.toByteArray();
	// try {
	// output.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return result;
	// }

	/**
	 * 判断图片旋转情况
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		if (bitmap != null) {
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	public  static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = computeSampleSize(options, -1, 960 * 960);
		// options.inSampleSize = (int) Math.pow(2, (int)
		// Math.round(Math.log(960 / (double) Math.max(options.outWidth,
		// options.outHeight)) / Math.log(0.5)));
		// options.inSampleSize = calculateInSampleSize(options, 960, 960);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;// 降低图片从AR
		int angle = readPictureDegree(filePath);
		return rotaingImageView(angle,
				BitmapFactory.decodeFile(filePath, options));
	}

	// add by trade start
	public static Bitmap getSmallBitmap(String filePath, int maxNumOfPixels) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);

		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;// 降低图片从AR

		int angle = readPictureDegree(filePath);

		return rotaingImageView(angle,
				BitmapFactory.decodeFile(filePath, options));
	}

	public static Bitmap getSmallBitmap(Resources res, int resId,
			int maxNumOfPixels) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);

		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.RGB_565;// 降低图片从AR

		Bitmap bitmap = BitmapFactory.decodeResource(res, resId, options);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), new Matrix(), true);
	}

	// add by trade end

	public static Bitmap extractThumbNail(String filePath) {
		Bitmap bm = getSmallBitmap(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bm != null) {
			bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);

			byte[] b = baos.toByteArray();
			Log.i(TAG,
					"bitmap croped size=" + bm.getWidth() + "x"
							+ bm.getHeight());
			Log.i(TAG, "bitmap croped size=" + b.length / 1024);
			ByteArrayInputStream bas = new ByteArrayInputStream(b);
			return BitmapFactory.decodeStream(bas);
		} else {
			return null;
		}

	}

	// private Bitmap getimage(String srcPath) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// //开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
	//
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 800f;//这里设置高度为800f
	// float ww = 480f;//这里设置宽度为480f
	// //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;//be=1表示不缩放
	// if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;//设置缩放比例
	// //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	// }

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));

		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;

		}
	}

	/**
	 * 根据image需要显示的大小区计算sampsize的值
	 */
	public static Options getCaculateSize(String file, ImageView img) {
		Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		options.inInputShareable = true;
		BitmapFactory.decodeFile(file, options);
		LayoutParams lp = img.getLayoutParams();
		int with = lp.width;
		int height = lp.height;
		int size = calculateInSampleSize(options, with, height);
		options = new BitmapFactory.Options();
		options.inSampleSize = size;
		Log.e("SOU_YUE", "options.inSampleSize=" + options.inSampleSize);
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		return options;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			final float totalPixels = width * height;

			final float totalReqPixelsCap = reqWidth * reqHeight * 2;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * 保存bitmap到本地
	 * 
	 * @param context
	 * @param mBitmap
	 * @param mFileName
	 * @throws IOException
	 */
	public static String saveBitmap(Context context, Bitmap mBitmap,
			File pic_file, String mFileName) {
		String file_path = null;
		if (!pic_file.exists() && !pic_file.mkdirs()) {
			Toast.makeText(context, "Can't make path to save pic.",
					Toast.LENGTH_LONG).show();
		}
		File pictureFile = new File(pic_file, mFileName);
		if (!pictureFile.exists()) {
			try {
				pictureFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(pictureFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
		try {
			fOut.flush();
			fOut.close();
			file_path = pictureFile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file_path;
	}

	/**
	 * 获取可以使用的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 *            目录名称
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = context.getExternalCacheDir() != null ? context
				.getExternalCacheDir().getAbsolutePath() : context
				.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * Get bitmap from specified image path
	 * 
	 * @return
	 */
	// public static Bitmap getBitmap(String imgPath) {
	// // Get bitmap through image path
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);//此时返回bm为空
	// newOpts.inJustDecodeBounds = false;
	// // Do not compress
	// newOpts.inSampleSize = 1;
	// newOpts.inPreferredConfig = Config.RGB_565;
	// int angle = readPictureDegree(imgPath);
	// return rotaingImageView(angle,
	// BitmapFactory.decodeFile(imgPath, newOpts));
	// }

	public static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh;
		float ww;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		hh = 800f;// 这里设置高度为800f
		ww = 480f;// 这里设置宽度为480f

		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = 4 / 3;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static String getFileName(String apath) {

		int start = apath.lastIndexOf("/");

		int end = apath.lastIndexOf(".");

		if (start != -1 && end != -1) {

			return apath.substring(start + 1, end);

		} else {

			return null;

		}

	}

	/**
	 * 按需求裁切图片
	 */
	// public static Bitmap ImageCrop(Bitmap bitmap,boolean flag) {
	// int w = bitmap.getWidth(); // 得到图片的宽，高
	// int h = bitmap.getHeight();
	//
	// int width = 0;
	// int height = 0;
	//
	// // int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
	// // if(flag){
	// // height = 12
	// // }else{
	// // height = h;
	// // width = 720;
	// // }
	//
	// int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
	// int retY = w > h ? 0 : (h - w) / 2;
	//
	// //下面这句是关键
	// return Bitmap.createBitmap(bitmap, retX, retY, width, height, null,
	// false);
	// }

	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		int retX = 0;
		int retY = 0;
		if (width >= (height * 2)) {
			retX = (width - height) / 2;
			retY = 0;
			width = height;
		}

		if (height >= (width * 2)) {
			retX = 0;
			retY = (height - width) / 2;
			height = width;
		}
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, retX, retY, width, height,
				matrix, true);
		return newbm;
	}

	/**
	 * 新增缩放图片方法，Im聊天专用对图片进行精确处理
	 * 
	 * @param bm
	 * @return
	 */
	public static Bitmap newZoomImg(Context context, Bitmap bm) {
		int bigSize = DeviceUtil.dip2px(context, 120);// 竖图大于两倍图片的高度
		int lowSize = DeviceUtil.dip2px(context, 60);// 横图大于两倍图片的宽度
		int width = bm.getWidth();
		int height = bm.getHeight();
		int newHeight = 0;
		int newWidth = 0;
		int retX = 0;
		int retY = 0;
		if (width > height) {// 判断图片是横图还是竖图并第一次给新的宽高赋值
			newWidth = bigSize;
			newHeight = lowSize;
		} else if (width < height) {
			newWidth = lowSize;
			newHeight = bigSize;
		} else {
			newHeight = bigSize;
			newWidth = bigSize;
		}
		if (width >= (height * 2)) {// 宽是高的两倍或更多
			retX = (width - 2 * height) / 2;
			width = bigSize * height / lowSize;
		}
		if (height >= (width * 2)) {// 高是宽的两倍或更多
			height = bigSize * width / lowSize;
		}
		if (width > height && width < (height * 2)) {// 宽图但小于二倍
			newHeight = height * bigSize / width;
		}
		if (height > width && height < (width * 2)) {// 竖图但小于二倍
			newWidth = bigSize * width / height;
		}

		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, retX, retY, width, height,
				matrix, true);
		return newbm;
	}

	public static File getImageByPath(String arg0) {
		ImageLoader loader = ImageLoader.getInstance();
		return loader.getDiskCache().get(arg0);
	}

          public static File getImageByPathAQ(Context context,String imgurl){
             return ImageLoader.getInstance().getDiskCache().get(imgurl);
//              return aq.getCachedFile(imgurl);
          }

	/**
	 * 获取圆角的矩形图片
	 * 
	 * @param bitmap
	 *            原始图片
	 * @param pixels
	 *            圆角半径
	 * @param isSquare
	 *            是否需要正方形
	 * @return
	 */
	public static Bitmap getRoundCornerRect(Bitmap bitmap, int pixels,
			boolean isSquare) {
		if (isSquare) {
			bitmap = getSquareBitmap(bitmap);
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 获取正方形的图片
	 * 
	 * @param bmp
	 *            原始图片
	 * @return
	 */
	public static Bitmap getSquareBitmap(Bitmap bmp) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}
		return squareBitmap;
	}

	/**
	 * android4.4专用 网上搜不到的
	 * 
	 * @param qrCodeBitmap
	 * @throws com.google.zxing.WriterException
	 */
	public static void saveImg(Bitmap qrCodeBitmap, String name)
			throws WriterException {
		String dir = chooseDir(GIF_PATH);
		if (dir != null) {
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
		}
	}

	/**
	 * android4.4以下版本获取图片路径的方法
	 * @param context
	 * @param data
	 * @return
	 */
	public static String getImageRealPath(Context context, Uri data) {
		String filename = null;
		Cursor cursor = null;
		if (data.getScheme().toString().compareTo("content") == 0) {
			cursor = context.getContentResolver().query(data, new String[] { Audio.Media.DATA }, null, null, null);
			if (cursor.moveToFirst()) {
				filename = cursor.getString(0);
			}
		} else if (data.getScheme().toString().compareTo("file") == 0) {
			// file:///开头的uri
			filename = data.toString();
			filename = data.toString().replace("file://", "");
			// 替换file://
			if (!filename.startsWith("/mnt")) {
				// 加上"/mnt"头
				filename += "/mnt";
			}
		}

		return filename;

	}
	
	/**
	 * android4.4及以上版本获取图片路径的方法
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getRealPathFromUri4Kitkat(Context context, Uri uri) {
		String filename = null;
		Log.d(TAG, "sdk version="+android.os.Build.VERSION.SDK_INT);
		Log.d(TAG, "filename="+uri.getPath());
		String uriPath = uri.getPath();
		if(uriPath != null) {
			filename = uriPath.toUpperCase();
			String fileId = null;
			if(filename.contains("%3A")) {// "%3A" is :
				fileId = filename.substring(filename.indexOf("%3A") + 1, filename.length());
			} else if (filename.contains(":")) {
				fileId = filename.substring(filename.indexOf(":") + 1, filename.length());
			} else {
				// eg, /external/images/media/204
				fileId = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
			}
			Log.d(TAG, "fileId="+fileId);
			String selection = MediaColumns._ID + " = ?";
			String[] selectionArgs = new String[] { fileId };

			Uri contentUri =  MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
			filename = getDataColumn(context, contentUri, selection, selectionArgs );
			Log.d(TAG, "sdk >= 19...filename="+filename);
		}
		return filename;
	}
	
	/**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     * 
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {
 
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
 
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
