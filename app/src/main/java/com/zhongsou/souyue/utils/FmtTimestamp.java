package com.zhongsou.souyue.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 
 * @author : zoulu
 * 2014年7月18日
 * 下午2:55:52 
 * 类说明 :格式化时间戳工具类
 */

@SuppressLint("SimpleDateFormat")
public class FmtTimestamp {
	/**
	 * 
	 * @param type 需要格式化的样式  例如："yyyy-MM-dd HH:mm:ss"、"yyyy年-MM月-dd日"
	 * @param time 需要格式化的时间戳 例如：1405563348187、1405563348
	 * @param flag 如果是10位(精确到秒)的时间戳  要为false因为要乘以1000，13位时间戳(精确到毫秒)为true
	 * @return 返回可读的时间
	 */
	public static String fmtTimestamp(String type , String time, boolean flag){
		 SimpleDateFormat sdf = new SimpleDateFormat(type);
		 if(flag)
			 return sdf.format(new Date(Long.valueOf(time)));
		 else
			 return sdf.format(new Date(Long.valueOf(time) * 1000L));
	}
}
