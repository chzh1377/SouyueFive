package com.zhongsou.souyue.ui.highlight.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V1.0
 * @Copyright (c) 2015 zhongsou
 * @Description
 * @date 2015/12/25
 */
public class HighlightViewUtils {
    /**
     * 获得子View在父View中的位置
     * @param parent 父View
     * @param child 子View
     * @return
     */
    public static Rect getLocationInView(View parent, View child) {
        if (child == null || parent == null) {
            throw new IllegalArgumentException("parent and child can not be null .");
        }

        View decorView = null;
        Context context = child.getContext();
        if (context instanceof Activity) {
            decorView = ((Activity) context).getWindow().getDecorView();
        }

        Rect result = new Rect();
        Rect tmpRect = new Rect();

        View tmp = child;

        if (child == parent) {
            child.getHitRect(result);
            return result;
        }
        while (tmp != null && tmp != decorView && tmp != parent) {      //判断非空
            tmp.getHitRect(tmpRect);

            result.left += tmpRect.left;
            result.top += tmpRect.top;
            tmp = (View) tmp.getParent();
            int location[] = new int[2];
            child.getLocationInWindow(location);
            child.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });
            Log.d("Highlight","w=( " + location[0] +" , " + location[1] +" )");
            child.getLocationOnScreen(location);
            Log.d("Highlight","s=( " + location[0] +" , " + location[1] +" )");
        }
        result.right = result.left + child.getMeasuredWidth();
        result.bottom = result.top + child.getMeasuredHeight();
        return result;
    }
}
