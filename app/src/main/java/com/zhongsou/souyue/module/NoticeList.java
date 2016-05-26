package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class NoticeList extends ResponseObject {

    private boolean hasMore = false;
    private List<Notice> items = new ArrayList<Notice>();

    public boolean hasMore() {
        return hasMore;
    }

    public void hasMore_$eq(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<Notice> items() {
        return items;
    }

    public void items_$eq(List<Notice> items) {
        this.items = items;
    }

}
