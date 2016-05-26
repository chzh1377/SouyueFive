package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SubscribeKeywordList extends ResponseObject {

    private String maxSelect = "";
    private List<GsItem> gs = new ArrayList<GsItem>();
    public String maxSelect() {
        return maxSelect;
    }
    public void maxSelect_$eq(String maxSelect) {
        this.maxSelect = maxSelect;
    }
    public List<GsItem> gs() {
        return gs;
    }
    public void gs_$eq(List<GsItem> gs) {
        this.gs = gs;
    }

}
