package com.tuita.sdk.im.db.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PingYinUtil {
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		if (inputString == null || "".equals(inputString)) {
			return "#";
		}
		char[] input = inputString.trim().toCharArray();
		StringBuilder output = new StringBuilder();
		if (input != null && input.length > 0) {
			for (int i = 0; i < input.length; i++) {
				String cstr = String.valueOf(input[i]);
				if(cstr.matches("[\\u4E00-\\u9FA5]+")){
					try {
						String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
						if(temp!=null){
						    output.append(temp[0]);
						}
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				}else{
					output.append(input[i]);
				}
			}
		}
		if(output.toString()==null||"".equals(output.toString())){
		    return "#";
		}else{
		    return output.toString();  
		}
		
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		if (chines == null || "".equals(chines)) {
			return "#";
		}
		char[] nameChar = chines.trim().toCharArray();
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
				    if(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)!=null){
                        pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
                    }
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (('a'<nameChar[i]&& 'z'>nameChar[i])||('A'<nameChar[i]&& 'Z'>nameChar[i])) {//设置此分支是为了让手机通讯录识别字母
				pinyinName += (""+nameChar[i]).toUpperCase();
			} else{
				// pinyinName += nameChar[i];
				pinyinName += "#";
			}
		}
		return pinyinName;
	}
	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 *
	 * @param chines汉字
	 * @return 拼音
	 */
	public static String converter2FirstSpell(String chines) {
//        long mTestTime = System.currentTimeMillis();
//        System.out.println("---->pinyin---->0节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
		String pinyinName = "";
		if (chines == null || "".equals(chines)) {
			return "#";
		}
//        System.out.println("---->pinyin---->1节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
		char[] nameChar = chines.trim().toCharArray();
//        System.out.println("---->pinyin---->2节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					if(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)!=null){
						pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
					}else{
						pinyinName += nameChar[i];
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
//				pinyinName += "#";
			}
		}
//        System.out.println("---->pinyin---->3节点："+ (System.currentTimeMillis() - mTestTime) + "毫秒");
		return pinyinName;
	}
	//所有的都转为大写
	public static HanyuPinyinOutputFormat defaultFormat;
	static{
		defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
	}
	
	//转换为数据库查询部分
	public static String conver2SqlReg(String key) {
		StringBuilder pinyinName = new StringBuilder();
		String chines = key.replaceAll("\\s*", "");// 去掉所有空格
		char[] chars = chines.toCharArray();
		boolean mark = false;
//		boolean lastIsNum = false;
		for (char c : chars) {
//			if (isNumber(c)) {
//				pinyinName.append(lastIsNum ? "" : "%");
//				pinyinName.append(c);
//				lastIsNum = true;
//				continue;
//			}
			if (mark){
				if (c == '_' || c == '%' || c == '\\')
					pinyinName.append("\\" + c);
				else
					pinyinName.append(c);
			}else {
				if (c == '_' || c == '%' || c == '\\')
					pinyinName.append("%" + "\\" + c);
				else
					pinyinName.append("%" + c);

				mark = true;
			}
//			lastIsNum = false;
		}
		pinyinName.append("%");
		return pinyinName.toString().toUpperCase();
	}

	//转换为数据库记录字段
	public static String conver2SqlRow(String key) {// 结果如： " 123 a b ji 记"
		StringBuilder pinyinName = new StringBuilder();
		String chines = key.trim();
		char[] chars = chines.toCharArray();
		for (char c : chars) {
			if(!isChinese(c)){
				pinyinName.append(c);
			}else{
				if (c > 128) {
					try {
						char py = PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0].charAt(0);
						pinyinName.append(py);
					} catch (Exception e) {
						pinyinName.append(c);
						e.printStackTrace();
					}
				}else{
					pinyinName.append(c);
				}
			}
		}
		pinyinName.append(" ").append(key.toUpperCase());
		return pinyinName.toString();
	}

    /**
     * im @好友搜索
     * @param key
     * @return
     * @author gengsong
     */
    public static String converReg(String key) {
        StringBuilder pinyinName = new StringBuilder();
        String chines = key.replaceAll("\\s*", "");// 去掉所有空格
        char[] chars = chines.toCharArray();
        boolean lastIsNum = false;
        for (char c : chars) {
            if (isNumber(c)) {
                pinyinName.append(lastIsNum ? "" : "");
                pinyinName.append(c);
                lastIsNum = true;
                continue;
            }
            pinyinName.append(c);
            lastIsNum = false;
        }
        return pinyinName.toString().toUpperCase();
    }

	public static boolean hasHanZi(String str) {
		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 输入的字符是否是汉字
	 * 
	 * @param a
	 *            char
	 * @return boolean
	 */
	public static boolean isChinese(char a) {
		int v = (int) a;
		return (v >= 19968 && v <= 171941);
	}
	
	public static boolean isNumber(char a) {
		char c_0 = '0';
		char c_9 = '9';
		return (a >= c_0 && a <= c_9);
	}
}
