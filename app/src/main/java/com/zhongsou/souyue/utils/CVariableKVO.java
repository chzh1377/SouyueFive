package com.zhongsou.souyue.utils;


/**
 * 这个类应用与：需要同时做n件事，当n件事都做完时才回调
 * Created by lvqiang on 15/6/16.
 */
public class CVariableKVO {
    public interface KVOCallback {
        public void doCallback();
    }

    int mFlag;
    KVOCallback mCallback;

    public CVariableKVO(int _capicity, KVOCallback _callback) {
        mFlag = _capicity;
        mCallback = _callback;
    }

    public void reset(int value) {
        mFlag = value;
    }

    public void reset(KVOCallback _callback) {
        mCallback = _callback;
    }

    public int getFalg() {
        return mFlag;
    }

    public void doDone() {
        if (mFlag <= 0) return;
        if (mFlag > 0) {
            mFlag--;
        }
        if (mFlag == 0) {
            mCallback.doCallback();
        }
    }

    public boolean isDone() {
        return mFlag == 0;
    }
}
