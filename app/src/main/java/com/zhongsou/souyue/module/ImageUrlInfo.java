package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class ImageUrlInfo extends ResponseObject {

    private String small = "";
    private String middle = "";
    private String big = "";
    public String small() {
        return small;
    }
    public void small_$eq(String small) {
        this.small = small;
    }
    public String middle() {
        return middle;
    }
    public void middle_$eq(String middle) {
        this.middle = middle;
    }
    public String big() {
        return big;
    }
    public void big_$eq(String big) {
        this.big = big;
    }

}
