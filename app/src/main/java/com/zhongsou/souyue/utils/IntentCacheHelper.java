package com.zhongsou.souyue.utils;

@SuppressWarnings("unchecked")
public class IntentCacheHelper<T> {
    private static IntentCacheHelper<?> instance;
    private T t;
    private boolean listFlag;
    
    public boolean isListFlag() {
        return listFlag;
    }

    public void setListFlag(boolean listFlag) {
        this.listFlag = listFlag;
    }

    public static <T> IntentCacheHelper<T> getInstance(Class<T> cls) {
        if (instance == null) {
            synchronized (IntentCacheHelper.class) {
                if (null == instance) instance = new IntentCacheHelper<T>();
            }
        }
        return (IntentCacheHelper<T>) instance;
    }

    public void setObject(T t) {
        this.t = t;
    }

    public T getObject() {
        return this.t;
    }

    public void recycle() {
//        t = null;
        listFlag=false;
    }
    
}
