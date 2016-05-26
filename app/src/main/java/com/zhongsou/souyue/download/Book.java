package com.zhongsou.souyue.download;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by tiansj on 14-9-22.
 */
public class Book implements DontObfuscateInterface{
    private UrlConsume index;
    private UrlConsume content;

    public UrlConsume getIndex() {
        return index;
    }

    public void setIndex(UrlConsume index) {
        this.index = index;
    }

    public UrlConsume getContent() {
        return content;
    }

    public void setContent(UrlConsume content) {
        this.content = content;
    }
}
