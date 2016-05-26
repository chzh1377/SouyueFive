package com.zhongsou.souyue.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public FileUtils() {
		super();
	}

	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final long ONE_KB = 1024;

	/**
	 * The number of bytes in a kilobyte.
	 * 
	 * @since 2.4
	 */
	public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);

	/**
	 * The number of bytes in a megabyte.
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * The number of bytes in a megabyte.
	 * 
	 * @since 2.4
	 */
	public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);

	/**
	 * The file copy buffer size (30 MB)
	 */
	private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;

	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;

	/**
	 * The number of bytes in a gigabyte.
	 * 
	 * @since 2.4
	 */
	public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);

	/**
	 * The number of bytes in a terabyte.
	 */
	public static final long ONE_TB = ONE_KB * ONE_GB;

	/**
	 * The number of bytes in a terabyte.
	 * 
	 * @since 2.4
	 */
	public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);

	/**
	 * The number of bytes in a petabyte.
	 */
	public static final long ONE_PB = ONE_KB * ONE_TB;

	/**
	 * The number of bytes in a petabyte.
	 * 
	 * @since 2.4
	 */
	public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);

	/**
	 * The number of bytes in an exabyte.
	 */
	public static final long ONE_EB = ONE_KB * ONE_PB;

	/**
	 * The number of bytes in an exabyte.
	 * 
	 * @since 2.4
	 */
	public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

	/**
	 * The number of bytes in a zettabyte.
	 */
	public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));

	/**
	 * The number of bytes in a yottabyte.
	 */
	public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

	public static long sizeOfDirectory(File directory) {
		long size = 0;
		try {
			checkDirectory(directory);
		} catch (IllegalArgumentException e) {
			return size;
		}

		final File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			return 0L;
		}

		for (final File file : files) {
		    if(file==null||!file.exists()){
		        continue;
		    }
			size += sizeOf(file);
			if (size < 0) {
				break;
			}
		}

		return size;
	}

	/**
	 * Checks that the given {@code File} exists and is a directory.
	 * 
	 * @param directory
	 *            The {@code File} to check.
	 * @throws IllegalArgumentException
	 *             if the given {@code File} does not exist or is not a
	 *             directory.
	 */
	private static void checkDirectory(File directory) {
		if (!directory.exists()) {
			throw new IllegalArgumentException(directory + " does not exist");
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory + " is not a directory");
		}
	}

	public static long sizeOf(File file) {

		if (!file.exists()) {
			String message = file + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (file.isDirectory()) {
			return sizeOfDirectory(file);
		} else {
			return file.length();
		}

	}

	// -----------------------------------------------------------------------
	/**
	 * Returns a human-readable version of the file size, where the input
	 * represents a specific number of bytes.
	 * <p>
	 * If the size is over 1GB, the size is returned as the number of whole GB,
	 * i.e. the size is rounded down to the nearest GB boundary.
	 * </p>
	 * <p>
	 * Similarly for the 1MB and 1KB boundaries.
	 * </p>
	 * 
	 * @param size
	 *            the number of bytes
	 * @return a human-readable display value (includes units - EB, PB, TB, GB,
	 *         MB, KB or bytes)
	 * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 -
	 *      should the rounding be changed?</a>
	 * @since 2.4
	 */
	// See https://issues.apache.org/jira/browse/IO-226 - should the rounding be
	// changed?
	public static String byteCountToDisplaySize(BigInteger size) {
		String displaySize;

		if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(size.divide(ONE_EB_BI)) + " EB";
		} else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(size.divide(ONE_PB_BI)) + " PB";
		} else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(size.divide(ONE_TB_BI)) + " TB";
		} else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(size.divide(ONE_GB_BI)) + " GB";
		} else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
			displaySize = String.valueOf(size.divide(ONE_MB_BI)) + " MB";
		} else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) >= 0) {
			displaySize = String.valueOf(size.divide(ONE_KB_BI)) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}
		return displaySize;
	}

	/**
	 * Returns a human-readable version of the file size, where the input
	 * represents a specific number of bytes.
	 * <p>
	 * If the size is over 1GB, the size is returned as the number of whole GB,
	 * i.e. the size is rounded down to the nearest GB boundary.
	 * </p>
	 * <p>
	 * Similarly for the 1MB and 1KB boundaries.
	 * </p>
	 * 
	 * @param size
	 *            the number of bytes
	 * @return a human-readable display value (includes units - EB, PB, TB, GB,
	 *         MB, KB or bytes)
	 * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 -
	 *      should the rounding be changed?</a>
	 */
	// See https://issues.apache.org/jira/browse/IO-226 - should the rounding be
	// changed?
	public static String byteCountToDisplaySize(long size) {
		return byteCountToDisplaySize(BigInteger.valueOf(size));
	}

	/**
	 * create directory
	 * 
	 * @param dirName
	 * @return
	 */
	public static File createDir(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		} else
			for (String filename : dir.list()) {
				File f = new File(filename);
				if (f.isFile())
					f.delete();
			}
		return dir;
	}

	/**
	 * del file
	 * 
	 * @param fileName
	 */
	public static synchronized void retireFile(String fileName) {
		if (fileName == null)
			return;
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 获取权限
	 *
	 * @param permission
	 *            权限
	 * @param path
	 *            路径
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取表情配置文件
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("emoji");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
