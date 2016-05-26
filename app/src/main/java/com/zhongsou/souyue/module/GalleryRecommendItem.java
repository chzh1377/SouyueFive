package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.Arrays;
import java.util.List;

/**
 * 推荐条目的javabean
 */
@SuppressWarnings("serial")
public class GalleryRecommendItem extends ResponseObject implements DontObfuscateInterface {

    public static final int TYPE_RECOMMEND_LIST_SINGLE = 0; // 一行的推荐
    public static final int TYPE_RECOMMENDLIST_DOUBLE = 1; // 2行的推荐
    public List<GalleryNewsItem> items; // 图片地址的list
    public int type;

    public GalleryRecommendItem(int type,GalleryNewsItem ...items) {
        this.type = type;
        this.items = Arrays.asList(items);
    }
}
