package com.zhongsou.souyue.im.search;

/**
 * 
 * 
 * @author zhaomeng
 * 
 */
public class ByteUtils {
	/**
	 * 4位字节转换为int型
	 * 
	 * @param b
	 * @return
	 */
	protected static int byteToInt(byte[] b) {
		int l = 0;
		l = b[0];
		l &= 0xff;
		l |= ((int) b[1] << 8);
		l &= 0xffff;
		l |= ((int) b[2] << 16);
		l &= 0xffffff;
		l |= ((int) b[3] << 24);
		l &= 0xffffffff;
		return l;
	}

	/**
	 * 8字节转换为long型
	 * 
	 * @param b
	 * @return
	 */
	public static long byteToLong(byte[] b) {
		long l = 0;
		l |= (((long) b[7] & 0xff) << 56);
		l |= (((long) b[6] & 0xff) << 48);
		l |= (((long) b[5] & 0xff) << 40);
		l |= (((long) b[4] & 0xff) << 32);
		l |= (((long) b[3] & 0xff) << 24);
		l |= (((long) b[2] & 0xff) << 16);
		l |= (((long) b[1] & 0xff) << 8);
		l |= ((long) b[0] & 0xff);
		return l;
	}

	/**
	 * 2字节转换为int型
	 * 
	 * @param res
	 * @return
	 */
	public static int byte2int(byte[] res) {
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
		return targets;
	}
}
