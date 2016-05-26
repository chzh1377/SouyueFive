package com.zhongsou.souyue.utils;

import com.zhongsou.souyue.DontObfuscateInterface;

public class e implements DontObfuscateInterface{
	public static String a(){
		return b(ActivityUtils.getSignature());
	}
	
	public static native String b(String str);
}
