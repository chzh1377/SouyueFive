package com.zhongsou.souyue.module.listmodule;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * 默认只有文本标题，单张小图，三张小图，单张gif，三张gif显示数据
 * Created by lvqiang on 15/12/23.
 */
public class DefaultItemBean extends BaseListData {
    private Map<String,String> titleIcon;

    public Map<String,String> getTitleIcon() {
        return titleIcon;
    }

    public void setTitleIcon(Map<String,String> titleIcon) {
        this.titleIcon = titleIcon;
    }
}
