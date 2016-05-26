package com.zhongsou.souyue.module;

@SuppressWarnings("serial")
public class Product extends ResponseObject {

    private String image = "";
    private String description = "";
    public String image() {
        return image;
    }
    public void image_$eq(String image) {
        this.image = image;
    }
    public String description() {
        return description;
    }
    public void description_$eq(String description) {
        this.description = description;
    }

}
