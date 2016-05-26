package com.zhongsou.souyue.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HttpJsonResponse {
    public JsonObject json; 

    public HttpJsonResponse(JsonObject json) {
        this.json = json;
    }

    public int getHeadInt(String childName, int def) {
        JsonElement e = getHeadElement(childName);
        if (e == null)
            return def;
        else
            return e.getAsInt();
    }

    public long getHeadLong(String childName, long def) {
        JsonElement e = getHeadElement(childName);
        if (e == null)
            return def;
        else
            return e.getAsLong();
    }

    public boolean getHeadBoolean(String childName) {
        JsonElement e = getHeadElement(childName);
        if (e == null)
            return false;
        else
            return e.getAsBoolean();
    }

    public String getHeadString(String childName) {
        JsonElement e = getHeadElement(childName);
        if (e == null)
            return "";
        else
            return e.getAsString();
    }

    public JsonElement getHeadElement(String childName) {
        return getHead().get(childName);
    }

    public JsonElement getBodyElement(String childName) {
        return getBody().get(childName);
    }

    public JsonObject getHead() {
        return this.json.getAsJsonObject("head");
    }

    public JsonArray getBodyArray() {
        return this.json.getAsJsonArray("body");
    }

    public long getBodyLong(String childName) {
        JsonElement b = this.getBodyElement(childName);
        if (b != null)
            return b.getAsLong();
        else
            return 0;
    }

    public long getBodyLong() {
        JsonElement b = this.json.get("body");
        if (b != null)
            return b.getAsLong();
        else
            return 0;
    }

    public boolean getBodyBoolean() {
        JsonElement b = this.json.get("body");
        if (b != null)
            return b.getAsBoolean();
        else
            return false;
    }

    public String getBodyString() {
        JsonElement b = this.json.get("body");
        String s = "";
        if (b != null) s = b.getAsString();
        return s;
    }

    public int getBodyInt() {
        JsonElement b = this.json.get("body");
        int s = 0;
        if (b != null) s = b.getAsInt();
        return s;
    }

    public JsonObject getBody() {
        return this.json.getAsJsonObject("body");
    }

    public boolean isJsonArray() {
        return this.json.get("body").isJsonArray();
    }

    public int getCode() {
        try {
            JsonObject head = this.getHead();
            if (head.has("status")) {
                return head.get("status").getAsInt();
            }else if (head.has("stat")){
                return head.get("stat").getAsInt();
            }
        } catch (Exception ex) {
        }
        return -1;
    }

    public String getMessage() {
        try {
            return this.getBodyString();
        } catch (Exception ex) {
            return "服务器好像有点问题哦，稍候再试下吧";
        }
    }

    public boolean isOk() {
        return this.getCode() == 200;
    }

    public boolean isOK(String statusKey){
        try {
            return this.getHead().has(statusKey)&& this.getHead().get(statusKey).getAsInt() == 200;
        } catch (Exception e) {
           return false;
        }
    }

}
