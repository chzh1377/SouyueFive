package com.facebook.drawee.view;

import android.graphics.drawable.Animatable;

/**
 * @description: 下载监听
 * @auther: qubian
 * @data: 2015/12/15.
 */
public interface ZSImageListener  {
    public void onStart(String id,Object callerContext);
    public void onSuccess(String id,Object imageInfo, Animatable animatable);
    public void onFailure(String id, Throwable throwable);
    public void onCancel(String id);

}
