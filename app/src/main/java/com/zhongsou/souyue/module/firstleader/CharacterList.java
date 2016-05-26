package com.zhongsou.souyue.module.firstleader;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.List;

/**
 * Created by zyw on 2016/3/23.
 */
public class CharacterList implements DontObfuscateInterface{
    private String  name;
    private String  image;
    private String  bigImage;
    List<ChildGroupItem> childs;
    private String sex;

    public CharacterList() {

    }

    public CharacterList(String name, String image, String bigImage) {
        this.name = name;
        this.image = image;
        this.bigImage = bigImage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<ChildGroupItem> getChilds() {
        return childs;
    }

    public void setChilds(List<ChildGroupItem> childs) {
        this.childs = childs;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }
}
