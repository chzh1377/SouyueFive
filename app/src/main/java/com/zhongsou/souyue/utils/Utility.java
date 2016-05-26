package com.zhongsou.souyue.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.ui.SouYueToast;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * huanglb@zhongsou.com
 */

public class Utility {
    
    /**
     * 验证修改昵称
     */
    public static boolean validateNickName(TextView nickName,Context c) {
        int nickname_length = nickName.getText().toString().trim().length();
        if (nickname_length != 0) {
            if (Utility.getStrLength(nickName.getText().toString().trim()) < 4
                    || Utility.getStrLength(nickName.getText().toString().trim()) > 20) {
                SouYueToast.makeText(c,
                    c.getResources().getString(R.string.nickname_length_error), 0).show();
                return false;
            } else {
                if (Utility.isChAndEnAndNum(nickName.getText().toString().trim())) {
                    return true;
                } else {
                    SouYueToast.makeText(c,
                        c.getResources().getString(R.string.nickname_format_error), 0).show();
                    return false;
                }
            }
        } else {
            SouYueToast.makeText(c,
                c.getResources().getString(R.string.nickname_no_empty), 0).show();
            return false;
        }
    }


   
        
    

    /**
	 * 重新计算ListView高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
	
	/**
	 * 验证中英文或数字
	 *
	 * @param email
	 * @return
	 */
	public static boolean isChAndEnAndNum(String str) {
		String regex = "^[A-Za-z0-9\u4e00-\u9fa5]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	/**
	 * 验证中英文数字，下划线，中横线，括弧
	 * @param str
	 * @return
	 */
/*	public static boolean isChAndEnAndNumAndLineAndBracket(String str) {
		String regex = "^[A-Za-z0-9(*)\u4e00-\u9fa5_-]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}*/
//	/**
//	 * 验证中英文或数字和下划线
//	 *
//	 * @param email
//	 * @return
//	 */
//	public static boolean isChAndEnAndNumAndDowndLine(String str) {
//		String regex = "^[A-Z_a-z0-9\u4e00-\u9fa5]+$";
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher(str);
//		return m.find();
//	}

    /**
     * im
     * @param str
     * @return
     */
//    public static boolean isImName(String str) {
//        String regex = "[\\x00-\\x20\\x7F\\xff\\u3000]|[\\\\ud83c\\\\udc00-\\\\ud83c\\\\udfff]|[\\\\ud83d\\\\udc00-\\\\ud83d\\\\udfff]|[\\\\u2600-\\\\u27ff]";
//        Pattern p = Pattern.compile(regex);
//        Matcher m = p.matcher(str);
//        return !m.find();
//    }


    public static boolean isImName(String name) {
        byte[] bytes;
        try {
            bytes = name.getBytes("utf-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        long flag = getHighWord(bytes, 0, 0);
        flag = flag == 0xfffe ? 1 : 0;

        long hs, ls;
        for (int i = 2; i < bytes.length; i=i+2) {
            hs = getHighWord(bytes, i, flag);
            if (0xd83c == hs) {
                if (isEofBytes(bytes, i)) {
                    ls = getLowWord(bytes, i, flag);
                    if (0xdc04 <= ls && ls <= 0xdff0) {
                        return false;
                    }
                }
            } else if (0xd83d == hs) {
                if (isEofBytes(bytes, i)) {
                    ls = getLowWord(bytes, i, flag);
                    if (0xdc0d <= ls && ls <= 0xdec0) {
                        return false;
                    }
                }
            } else if (0x23 == hs || (0x30 <= hs && hs <= 0x39)) {
                if (isEofBytes(bytes, i)) {
                    ls = getLowWord(bytes, i, flag);
                    if (ls == 0x20e3) {
                        return false;
                    }
                }
            } else {
                if (0x00 <= hs && hs <= 0x20) {
                    return false;
                } else if (hs == 0x7f || hs == 0xff || hs == 0x3000) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isEofBytes(byte[] bytes, int i) {
        return (i + 3) < bytes.length;
    }

    private static int getHighWord(byte[] bytes, int i, long flag) {
        if (flag == 1) {
            return ((bytes[i] & 0xFF) << 8) + (bytes[i + 1] & 0xFF);
        } else {
            return ((bytes[i + 1] & 0xFF) << 8) + (bytes[i] & 0xFF);
        }
    }

    private static int getLowWord(byte[] bytes, int i, long flag) {
        if (flag == 1) {
            return ((bytes[i + 2] & 0xFF) << 8) + (bytes[i + 3] & 0xFF);
        } else {
            return ((bytes[i + 3] & 0xFF) << 8) + (bytes[i + 2] & 0xFF);
        }
    }



    /**
	 * 格式化昵称(长度大于12各字符后用...表示)
	 * 
	 * @param value
	 */
	public static String getFormatName(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        StringBuffer s = new StringBuffer();
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
            s.append(temp);
            if(valueLength >= 10){
            	return s.toString() + "...";
            }
        }
        return s.toString();
    }
	
//	/**
//	 * 格式化字符串，按照指定长度裁剪给出字符串(长度大于12各字符后用...表示)
//	 *
//	 * @param value
//	 */
//	public static String getFormatName(String value,int length) {
//        int valueLength = 0;
//        String chinese = "[\u0391-\uFFE5]";
//        StringBuffer s = new StringBuffer();
//        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
//        for (int i = 0; i < value.length(); i++) {
//            /* 获取一个字符 */
//            String temp = value.substring(i, i + 1);
//            /* 判断是否为中文字符 */
//            if (temp.matches(chinese)) {
//                /* 中文字符长度为2 */
//                valueLength += 2;
//            } else {
//                /* 其他字符长度为1 */
//                valueLength += 1;
//            }
//            s.append(temp);
//            if(valueLength >= length){
//            	return s.toString();
//            }
//        }
//        return s.toString();
//    }
	
	/**
	 * 获取字符串的长度
	 * @param str
	 */
	public static int getStrLength(String str) {
		str = str.replaceAll("[^\\x00-\\xff]", "**");
		int length = str.length();
		return length;
	}

	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		return m.find() == true;
	}
	
}
