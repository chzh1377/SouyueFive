package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class JiFen extends ResponseObject {
    private String type = "";
    private String num = "";
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNum() {
        return num;
    }
    public void setNum(String num) {
        this.num = num;
    }
    
    public boolean isJF(){
        return "jf".equals(type);
    }
    public boolean isZSB(){
        return "zsb".equals(type);
    }
}
