package com.zhongsou.souyue.im.transfile;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by xyh0125 on 15/11/10.
 */
public class FileDownloadInfo implements Serializable,DontObfuscateInterface {

    public static final int STATE_FOR_INIT = 1;         // 初始化等待中
    public static final int STATE_FOR_LOADING = 2;      // 下载中
    public static final int STATE_FOR_PAUSE = 3;        // 暂停中
    public static final int STATE_FOR_FAILED = 4;       // 下载失败
    public static final int STATE_FOR_COMPLETE = 5;     // 下载完成

    private String url;
    private int length ;
    private int curLength ;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCurLength() {
        return curLength;
    }

    public void setCurLength(int curLength) {
        this.curLength = curLength;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
