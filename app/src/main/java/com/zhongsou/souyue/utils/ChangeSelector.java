package com.zhongsou.souyue.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/** 
 * @author : zoulu
 * 2014年7月15日
 * 上午10:38:37 
 * 类说明 :改变按钮的点击色
 */
public class ChangeSelector {
	/**
	 * 
	 * @param context
	 * @param idNormal 正常颜色
	 * @param idPressed 按下后颜色
	 * @param idFocused  获取焦点颜色
	 * @return
	 */
	public static StateListDrawable addStateDrawable(Context context,  int idNormal, int idPressed, int idFocused) {  
        StateListDrawable sd = new StateListDrawable();  
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);  
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);  
        Drawable focus = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);  
        sd.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);  
        sd.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);  
        sd.addState(new int[]{android.R.attr.state_focused}, focus);  
        sd.addState(new int[]{android.R.attr.state_pressed}, pressed);  
        sd.addState(new int[]{android.R.attr.state_enabled}, normal);  
        sd.addState(new int[]{}, normal);  
        return sd;  
    }  
}
