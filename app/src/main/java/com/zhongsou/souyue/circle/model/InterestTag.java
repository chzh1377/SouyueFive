package com.zhongsou.souyue.circle.model;

import com.zhongsou.souyue.module.ResponseObject;

/**
 * Created by tiansj on 14/11/6.
 */
public class InterestTag extends ResponseObject {

    private String id;    // 标签ID
    private String tag_name; // 标签名称
    private String srp_id;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getSrp_id() {
        return srp_id;
    }

    public void setSrp_id(String srp_id) {
        this.srp_id = srp_id;
    }
}
