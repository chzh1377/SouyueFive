package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class KsItem extends ResponseObject {
    private String k = "";//  keyword
    private String i = "";// srpid

    public String k() {
        return k;
    }

    public void k_$eq(String k) {
        this.k = k;
    }

    public String i() {
        return i;
    }

    public void i_$eq(String i) {
        this.i = i;
    }
}
