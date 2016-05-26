package com.zhongsou.souyue.net.volley;

/**
 * Created by lvqiang on 15/8/5.
 */
public interface IRequestCache {
    public static final int CACHE_STATE_NO_CACHE =0;//没有缓存
    public static final int CACHE_STATE_HAVE =1;//有缓存并且没有过期
    public static final int CACHE_STATE_IS_EXPIRE =2;//有缓存但是已过期
    int getCacheState(String key);
    int deleteCache(String key);
    void clearCache();
    void deleteCacheList(String[] extra);
}
