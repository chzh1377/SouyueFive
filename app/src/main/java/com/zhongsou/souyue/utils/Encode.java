package com.zhongsou.souyue.utils;


import android.util.Base64;

public class Encode {

//	public static String decode(String name){
//		String result = null;
//		String key = "$^&*%$*%^&";
//		try {
//			String fullStr = new String(new BASE64Decoder().decodeBuffer(name));
//			result = fullStr.substring(0,fullStr.length()-key.length());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}

	public static String encode(String name) {
		String key = "$^&*%$*%^&";
		return new String(Base64.encode((name + key).getBytes(), Base64.DEFAULT)).replaceAll("\n", "");
	}
}