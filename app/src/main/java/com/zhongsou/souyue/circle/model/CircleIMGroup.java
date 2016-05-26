package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

/**
 * Created by bob zhou on 15-1-9.
 * 兴趣圈IM群Model
 */
public class CircleIMGroup implements Serializable, DontObfuscateInterface {

    private long group_id;

    private String group_name;

    private int max_count;

    private int current_count;

    private String group_logo;

    private long create_time;

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getMax_count() {
        return max_count;
    }

    public void setMax_count(int max_count) {
        this.max_count = max_count;
    }

    public int getCurrent_count() {
        return current_count;
    }

    public void setCurrent_count(int current_count) {
        this.current_count = current_count;
    }

    public String getGroup_logo() {
        return group_logo;
    }

    public void setGroup_logo(String group_logo) {
        this.group_logo = group_logo;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
}
