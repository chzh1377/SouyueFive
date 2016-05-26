package com.zhongsou.souyue.module;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SubscribeBack extends ResponseObject{

    private List<Integer> id = new ArrayList<Integer>();
    private long groupId = 0;

    public List<Integer> id() {
        return id;
    }

    public void id_$eq(List<Integer> id) {
        this.id = id;
    }

    public long groupId() {
        return groupId;
    }

    public void groupId_$eq(long groupId) {
        this.groupId = groupId;
    }

}
