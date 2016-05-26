package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.zhongsou.souyue.MainApplication;

/**
 * @author : zoulu
 *         2014年3月10日
 *         下午2:07:37
 *         类说明 :保存和查看微信分享状态，有朋友圈和用户之说
 */
public class WXState {
    public static final String WXSTATE = "wxstate";//保存微信状态
    /**
     * 会话
     */
    public static final int SESSION = 1;
    /**
     * 朋友圈
     */
    public static final int TIMELINE = 2;
    /**
     * 登录
     */
    public static final int LOGIN = 3;


    public static void changeWXState(int state) {
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(WXSTATE, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(WXSTATE, state);
        editor.commit();
    }

    public static int getWXState() {
        SharedPreferences sp = MainApplication.getInstance().getSharedPreferences(WXSTATE, Context.MODE_PRIVATE);
        return sp.getInt(WXSTATE, 0);
    }
}
