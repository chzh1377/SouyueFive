package com.tuita.sdk.im.db.module;

import com.google.gson.Gson;
import com.zhongsou.souyue.DontObfuscateInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * @description:  用于 IM 群 以后的拓展
 * @auther: qubian
 * @data: 2016/4/15.
 */
public class GroupExtendInfo implements Serializable,DontObfuscateInterface {

    private List<ImToCricle> circle_boundCircleList;

    public List<ImToCricle> getCircle_boundCircleList() {
        return circle_boundCircleList;
    }

    public void setCircle_boundCircleList(List<ImToCricle> circle_boundCircleList) {
        this.circle_boundCircleList = circle_boundCircleList;
    }
}
