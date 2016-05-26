package com.zhongsou.souyue.countUtils;

import android.content.Context;
import com.umeng.analytics.MobclickAgent;

/**
 * @author YanBin yanbin@zhongsou.com
 * @version V 1.0
 * @Copyright (c) 2015 zhongsou
 * @Description Umeng 统计工具类
 * @date 2015/11/12
 */
public class UmengStatisticUtil {
    /**
     * 封装Umeng统计事件
     * @param context 上下文
     * @param string 统计事件名
     */
    public static void onEvent(Context context, String string){
        MobclickAgent.onEvent(context, string);
    }

    //TODO 继续封装其他重载方法
}
