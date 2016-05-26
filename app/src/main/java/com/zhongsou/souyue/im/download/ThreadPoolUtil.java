package com.zhongsou.souyue.im.download;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 表情下载线程池
 *
 * @author wangqiang
 */
public class ThreadPoolUtil

{
    private static ThreadPoolUtil poolUtil;
    private ExecutorService pool;

    private ThreadPoolUtil() {
        pool = Executors.newSingleThreadExecutor();
    }

    public static ThreadPoolUtil getSingleInstance() {
        if (poolUtil == null)
            poolUtil = new ThreadPoolUtil();
        return poolUtil;
    }

    public void push(Runnable runnable) {
        this.pool.execute(runnable);
    }
}