package com.zhongsou.souyue.module;

/**
 * 数据结构一般有四点，
 * 1、独特的身份标识
 * 2、对象结构的结构信息
 * 3、当前的状态
 * 4、存储供访问的数据
 */
public abstract class AGridDynamic extends ResponseObject{
    public static final int STATE_INIT=0;
    public static final int STATE_DELETE=1;
    public static final int STATE_SPACE=2;

    private long mId;
    private int mState;


    private int mPosition;



    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public int getmState() {
        return mState;
    }

    public void setmState(int mState) {
        this.mState = mState;
    }


}


