package com.zhongsou.souyue.module;

/**
 * User: zhangliang01@zhongsou.com
 * Date: 11/6/13
 * Time: 9:57 AM
 */
public class Ent extends ResponseObject {
    private String keyword;
    private String shopId;
    public Ent(String keyword, String shopId) {
        this.keyword = keyword;
        this.shopId = shopId;
    }
}
