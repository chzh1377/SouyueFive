package com.zhongsou.souyue.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @author zoulu
 * 线程池
 *
 */
public class ThreadPoolUtil {
	private static ExecutorService instance = null;

	private ThreadPoolUtil() {

	}

	public static ExecutorService getInstance() {
		if (instance == null)
			instance = Executors.newCachedThreadPool();
		return instance;
	}
}
