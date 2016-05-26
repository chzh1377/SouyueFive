package com.zhongsou.souyue.utils;

import com.zhongsou.souyue.DontObfuscateInterface;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 加密类
 * Created by lvqiang on 14-11-14.
 */
public class ZSSecret implements DontObfuscateInterface{
	static{
		System.loadLibrary("encrypt");
	}

    /**
     * 在上层会将不管是get还是post都转换成json格式的字符串进行加密
     * @param _str
     * @return
     */
    public static String encrypt(String _str){
        String str_en="";
        try {//网络过来的数据和服务器协定加密的为json串的方式传往服务端
            JSONObject json = new JSONObject((_str));
            str_en = ens(json.toString());
        } catch (JSONException e) {
            str_en = enjs(_str);
        }
        return str_en;
    }
    public static native String ens(String _str);

    public static native String enjs(String _str);
}
