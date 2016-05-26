package com.tuita.sdk;

import com.google.gson.Gson;
import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public abstract class ResponseObject extends Object implements Serializable,DontObfuscateInterface {

    public static final long serialVersionUID = -5752410193949578084L;

    /**
     * 网络返回数据模型定义
     *
     * @author wanglong@zhongsou.com
     */
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

