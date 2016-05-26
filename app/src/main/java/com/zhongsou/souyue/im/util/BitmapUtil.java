package com.zhongsou.souyue.im.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class BitmapUtil {

	public static Drawable convertBitmap2Drawable(Resources res, Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		return (Drawable) bd;
	}

	public static Bitmap convertDrawable2Bitmap(Drawable d) {

		BitmapDrawable bd = (BitmapDrawable) d;

		Bitmap bm = bd.getBitmap();
		return bm;
	}

	public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height, Matrix m, boolean filter) {
		Bitmap bitmap = null;
		if (source != null && !source.isRecycled()) {
			try {
				bitmap = Bitmap.createBitmap(source, 0, 0, width, height, m, true);
			} catch (OutOfMemoryError e) {
				System.gc();
				try {
					bitmap = Bitmap.createBitmap(source, 0, 0, width, height, m, true);
				} catch (OutOfMemoryError e1) {
				}
			} catch (Exception e2) {
			}
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap createBitmap(int width, int height, Config config) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, config);
		} catch (OutOfMemoryError e) {
			System.gc();
			try {
				bitmap = Bitmap.createBitmap(width, height, config);
			} catch (OutOfMemoryError e1) {
			}
		} catch (Exception e2) {
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap decodeFile(String pathName) {
		Bitmap bitmap = null;
		if (FileUtil.isFileExist(pathName)) {
			BitmapFactory.Options opts = new BitmapFactory.Options();

			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inPreferredConfig = android.graphics.Bitmap.Config.ALPHA_8;
			opts.inDither = true;
			// InputStream is = FileUtil.readFileInputStream(pathName);
			// bitmap = decodeStream(is);
			bitmap = BitmapFactory.decodeFile(pathName, opts);
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap decodeFile(String pathName, int width, int height) {
		Bitmap bitmap = null;
		if (FileUtil.isFileExist(pathName)) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(pathName, opts);
			opts.inSampleSize = computeSampleSize(opts, -1, width * height);
			opts.inJustDecodeBounds = false;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inPreferredConfig = android.graphics.Bitmap.Config.ALPHA_8;
			opts.inDither = true;

			try {
				bitmap = BitmapFactory.decodeFile(pathName, opts);
			} catch (OutOfMemoryError e) {
				System.gc();
				try {
					bitmap = BitmapFactory.decodeFile(pathName, opts);
				} catch (OutOfMemoryError e1) {
				}
			} catch (Exception e2) {
			}
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap decodeStream(InputStream is) {
		Bitmap bitmap = null;
		if (is != null) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inPreferredConfig = android.graphics.Bitmap.Config.ALPHA_8;
			opts.inDither = true;

			try {
				bitmap = BitmapFactory.decodeStream(is, null, opts);
			} catch (OutOfMemoryError e) {
				System.gc();
				try {
					bitmap = BitmapFactory.decodeStream(is, null, opts);
				} catch (OutOfMemoryError e1) {
				}
			} catch (Exception e2) {
			}
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap createBitmap(int[] colors, int width, int height, Config config) {
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(colors, width, height, config);
		} catch (OutOfMemoryError e) {
			System.gc();
			try {
				bitmap = Bitmap.createBitmap(colors, width, height, config);
			} catch (OutOfMemoryError e1) {
			}
		} catch (Exception e2) {
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap createBitmap(Bitmap source, int x, int y, int width, int height) {
		Bitmap bitmap = null;
		if (source != null && !source.isRecycled()) {
			try {
				bitmap = Bitmap.createBitmap(source, x, y, width, height);
			} catch (OutOfMemoryError e) {
				System.gc();
				try {
					bitmap = Bitmap.createBitmap(source, x, y, width, height);
				} catch (OutOfMemoryError e1) {
				}
			} catch (Exception e2) {
			}
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
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

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

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

	public static Bitmap readBitmap(final String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		} else {
			// BitmapFactory.Options options=new BitmapFactory.Options();
			// options.inSampleSize = 8;
			return BitmapUtil.decodeFile(filePath);
		}
	}

	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static Bitmap decodeResource(Context context, int resId) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		} catch (OutOfMemoryError e) {
			System.gc();
		} catch (Exception e2) {
		}
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
		return bitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/** 加载本地图片 */
	public static Bitmap convertToBitmap(String path, int w, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > w || height > h) {
			// 缩放
			scaleWidth = ((float) width) / w;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int) scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), w, h, true);
	}

	/** 保存bitmap到本地 */
	public static boolean writeBitmapToFile(Bitmap bitmap, String filePath) {
		File file = new File(filePath);
		FileOutputStream m_fileOutPutStream = null;
		try {
			m_fileOutPutStream = new FileOutputStream(file);// 写入的文件路径

			bitmap.compress(CompressFormat.PNG, 100, m_fileOutPutStream);
			m_fileOutPutStream.flush();
			m_fileOutPutStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
