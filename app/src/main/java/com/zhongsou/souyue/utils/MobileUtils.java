package com.zhongsou.souyue.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileUtils {

    /**
     * 验证手机号码(只验证以1开头，长度11位)
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
        if(TextUtils.isEmpty(mobiles))
            return false;
        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
