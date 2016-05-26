package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.R;

import java.io.Serializable;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.1)
 * @Copyright (c) 2016 zhongsou
 * @Description class IM功能bean
 * @date 16/1/8
 */
public class FeaturesBean implements Serializable, DontObfuscateInterface {

    private static final long serialVersionUID = -8278230941938309412L;

    private int featuresType;
    private int featuresIcon;
    private int featuresText;

    private int textColor;

    public int getFeaturesText() {
        return featuresText;
    }

    public void setFeaturesText(int featuresText) {
        this.featuresText = featuresText;
    }

    public int getFeaturesType() {
        return featuresType;
    }

    public void setFeaturesType(int featuresType) {
        this.featuresType = featuresType;
    }

    public int getFeaturesIcon() {
        return featuresIcon;
    }

    public void setFeaturesIcon(int featuresIcon) {
        this.featuresIcon = featuresIcon;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * 功能栏基本的构造方法
     * 字体颜色默认为灰色
     * @param featuresType
     * @param featuresIcon
     * @param featuresText
     */
    public FeaturesBean(int featuresType, int featuresIcon, int featuresText) {
        this.featuresType = featuresType;
        this.featuresIcon = featuresIcon;
        this.featuresText = featuresText;
        this.textColor = R.color.gray_7e;
    }

    /**
     * 可以改变字体颜色的构造方法
     *
     * @param featuresType
     * @param featuresIcon
     * @param featuresText
     * @param textColor
     */
    public FeaturesBean(int featuresType, int featuresIcon, int featuresText,int textColor) {
        this.featuresType = featuresType;
        this.featuresIcon = featuresIcon;
        this.featuresText = featuresText;
        this.textColor = textColor;
    }

}
