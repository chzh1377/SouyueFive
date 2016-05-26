package com.zhongsou.souyue.im.download;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 缓存下载进度信息
 * 
 * @author wangqiang
 * 
 * 
 */

public class ListState {

	public static final int INIT = 0;// 定义三种下载的状态：0初始化状态，1正在下载状态，2暂停状态
	public static final int DOWNLOADING = 1;
	public static final int STOP = 2;

	/**
	 * 
	 * 存放每个下载文件完成的下載狀態
	 */
	public static Map<String, Integer> state = new HashMap<String, Integer>();
	/**
	 * 
	 * 存放每个下载文件完成的长度
	 */

	public static Map<String, Integer> completeSizes = new HashMap<String, Integer>();
	/**
	 * 
	 * 存放每个下载文件的总长度
	 */
	public static Map<String, Integer> fileSizes = new HashMap<String, Integer>();

	/**
	 * 
	 * 存放每个下载文件的下载地址
	 */
	public static Map<String, String> downloadUrl = new HashMap<String, String>();
}
