package com.zhongsou.souyue.utils;

import android.text.TextUtils;
import com.zhongsou.souyue.common.utils.CommSharePreference;


/**
 * Created by zyw on 2015/11/20.
 * 微信分享红包工具类
 */
public class WXShareHBUtils {

    private static final String KEY = "share_state";
    public static final int STATE_NOTGETYET = 0; // 未领取
    public static final int STATE_HASGET = 1; // 已经领取
    public static final int ALL_HB = 2;
    public static final long TIME_STEP = 24 * 60 * 60 * 1000;

    public static void setShareState(int shareState) {
        Long userid = Long.decode(SYUserManager.getInstance().getUserId());
        CommSharePreference.getInstance().putValue(userid, KEY, shareState + "##@##" + System.currentTimeMillis());
    }

    /**
     * 如果当前时间超过24小时，就是还未领取，如果还未超过，就返回上次记录的值
     *
     * @return
     */
    public static int getSharestate() {
        Long userid = Long.decode(SYUserManager.getInstance().getUserId());
        String val = CommSharePreference.getInstance().getValue(userid, KEY, "");
        if (!TextUtils.isEmpty(val)) {
            String[] strs = val.split("##@##");
            try {
                int state = Integer.decode(strs[0]);
                long time = Long.decode(strs[1]);
                if (System.currentTimeMillis() - time > TIME_STEP) {
                    return STATE_NOTGETYET;
                } else {
                    return state;
                }
            } catch (Exception e) {
                return STATE_NOTGETYET;
            }
        }
        return STATE_NOTGETYET;

    }

}
