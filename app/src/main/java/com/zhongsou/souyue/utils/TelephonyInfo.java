package com.zhongsou.souyue.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephonyInfo {
    
    /**
	 * Role:获取当前设置的电话号码 <BR>
	 * Date:2012-3-12 <BR>
	 */
	public static String getNativePhoneNumber(Context context) {
		String nativePhoneNumber = null;
		try{
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			nativePhoneNumber = telephonyManager.getLine1Number();
		}catch(Exception e){
			nativePhoneNumber = "";
		}
		return nativePhoneNumber;
	}

    /**
	 * 验证手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {
		if(TextUtils.isEmpty(mobiles))
			return false;
		
		Pattern p = Pattern.compile("^1[\\d]{10}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

    /**
     *
     * 验证手机号码 7-12位
     * @param mobiles
     * @return gengsong
     */
    public static boolean isIMMobileNum(String mobiles) {
        if(TextUtils.isEmpty(mobiles))
            return false;

        Pattern p = Pattern.compile("^(1[\\d]{6,11})|([\\d]{5})$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
	/**
	 * 替换手机号中+86
	 * @param phoneNum
	 * @return
	 */
	public static String checkPhoneNum(String phoneNum) {
        Pattern p1 = Pattern.compile("^((\\+{0,1}86){0,1})1[0-9]{10}");
        Matcher m1 = p1.matcher(phoneNum);
        if (m1.matches()) {
            Pattern p2 = Pattern.compile("^((\\+{0,1}86){0,1})");
            Matcher m2 = p2.matcher(phoneNum);
            StringBuffer sb = new StringBuffer();
            while (m2.find()) {
                m2.appendReplacement(sb, "");
            }
            m2.appendTail(sb);
            return sb.toString();
        } else {
            return "";
        }
    }
	
	public static int getSimState(Context context) {
	    int simState = 0;
        try{
            TelephonyManager  mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  
            simState= mTelephonyManager.getSimState(); 
        }catch(Exception e){
            
        }
        return simState;
    }
}
