package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GsItem extends ResponseObject {// 发原创选词
    private String g = "";
    private List<KsItem> ks = new ArrayList<KsItem>();

    public String g() {
        return g;
    }

    public void g_$eq(String g) {
        this.g = g;
    }

    public List<KsItem> ks() {
        return ks;
    }

    public void ks_$eq(List<KsItem> ks) {
        this.ks = ks;
    }
}
