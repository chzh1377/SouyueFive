package com.zhongsou.souyue.download;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by tiansj on 14-9-20.
 */
public class UrlConsume  implements Serializable ,DontObfuscateInterface{

    private String onlyId;  // 文件ID
    private String url;     // 下载地址
    private long length;     // 长度
    private long curLength;  // 已经下载的长度
    private String filePath;    // 文件路径


    public String getOnlyId() {
        return onlyId;
    }

    public void setOnlyId(String onlyId) {
        this.onlyId = onlyId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getCurLength() {
        return curLength;
    }

    public void setCurLength(long curLength) {
        this.curLength = curLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

