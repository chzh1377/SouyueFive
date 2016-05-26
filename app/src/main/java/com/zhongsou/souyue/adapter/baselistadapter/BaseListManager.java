package com.zhongsou.souyue.adapter.baselistadapter;

import android.app.Activity;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.utils.SYSharedPreferences;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/1/26.
 */

public abstract class BaseListManager  {

    Activity mActivity;
    public BaseListManager(Activity context)
    {
        this.mActivity=context;
    }
    private static long lastClickTime;

    /**
     * 双击 控制
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public float getFontSize() {
        return SYSharedPreferences.getInstance().loadResFont(mActivity);
    }

    public abstract void clickItem(BaseListData item);

}
