package com.facebook.drawee.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.sax.RootElement;

import com.facebook.drawee.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;

/**
 * @description: 图片加载配置
 * @auther: qubian
 * @data: 2015/12/11.
 */
public class ZSImageOptions {

    public GenericDraweeHierarchy hierarchy;
    public ZSImageOptions         instance;


//    public static ZSImageOptions getInstance() {
//        if (instance == null) {
//            synchronized (ZSImageOptions.class) {
//                if (instance == null) {
//                    instance = new ZSImageOptions();
//                }
//            }
//        }
//        return instance;
//    }

    /**
     * 默认图
     *
     * @param context
     * @param id
     * @return
     */
    public static ZSImageOptions getDefaultConfig(Context context, int id) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .setPlaceholderImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .build();
        return options;
    }

    public static ZSImageOptions getDefaultConfigList(Context context, int id) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setPlaceholderImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .build();
        return options;
    }

    /**
     * 默认图
     *
     * @param context
     * @param drawable
     * @return
     */
    public static ZSImageOptions getDefaultConfig(Context context, Drawable drawable) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .setPlaceholderImage(drawable, ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(drawable, ScalingUtils.ScaleType.FIT_XY)
                .build();
        return options;
    }

    /**
     * 无图
     *
     * @param context
     * @return
     */
    public static ZSImageOptions getDefaultConfigForNone(Context context) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        return options;
    }

    /**
     * 设置圆形
     *
     * @param context
     * @param id
     * @return
     */
    public static ZSImageOptions getDefaultConfigCircle(Context context, int id) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .setPlaceholderImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .setRoundingParams(RoundingParams.asCircle())
                .build();
        return options;
    }

    /**
     * 设置圆角
     *
     * @param context
     * @param id      R.drawable
     * @param r       圆角半径
     * @return
     */
    public static ZSImageOptions getDefaultConfigCircle(Context context, int id, float r) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources()).setFadeDuration(300)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .setPlaceholderImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .setFailureImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_XY)
                .setRoundingParams(RoundingParams.fromCornersRadii(r, r, r, r))
                .build();
        return options;
    }

    /**
     * 引导页人物专用！
     *
     * @param context
     * @param id
     * @return
     */
    public static ZSImageOptions getFirstLeaderCharacterConfig(Context context, int id) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setPlaceholderImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_CENTER)
                .setFailureImage(context.getResources().getDrawable(id), ScalingUtils.ScaleType.FIT_CENTER)
                .build();
        return options;
    }

    /**
     * 本地图片，fitxy,没有duration
     *
     * @param context
     * @return
     */
    public static ZSImageOptions getLocalImageConfig(Context context) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();
        return options;
    }

    public static ZSImageOptions getLocalCircleConfig(Context context) {
        ZSImageOptions options = new ZSImageOptions();
        options.hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setRoundingParams(RoundingParams.asCircle())
                .build();
        return options;
    }

}
