package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

/**
 * Created by wlong on 14-5-15.
 */
public class InterestGroup  implements  DontObfuscateInterface {

    private long group_id;
    private String group_name;
    private long group_sort;
    private String create_time;

    public long getGroup_id() {
        return group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public long getGroup_sort() {
        return group_sort;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public void setGroup_sort(long group_sort) {
        this.group_sort = group_sort;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
