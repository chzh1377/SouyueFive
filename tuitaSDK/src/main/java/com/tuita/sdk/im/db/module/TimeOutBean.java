package com.tuita.sdk.im.db.module;

import java.io.Serializable;
import com.google.gson.Gson;
import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * on 2014/11/7
 * Description:请求超时
 */
public class TimeOutBean implements Serializable,DontObfuscateInterface{
    public static int TIMEOUT = 8000;
    public static int REQUESTFAILED = 8001;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private int code;
    private String msg;

    /**
     * 设置超时bean
     * @return
     */
    public static String setTimeOutBean(){
        TimeOutBean timeOutBean = new TimeOutBean();
        timeOutBean.setCode(TIMEOUT);
        timeOutBean.setMsg("TIMEOUT");
        return new Gson().toJson(timeOutBean);
    }

    /**
     * 设置失败bean
     * @return
     */
    public static String setFailedBean(){
        TimeOutBean timeOutBean = new TimeOutBean();
        timeOutBean.setCode(REQUESTFAILED);
        timeOutBean.setMsg("REQUESTFAILED");
        return new Gson().toJson(timeOutBean);
    }
}
