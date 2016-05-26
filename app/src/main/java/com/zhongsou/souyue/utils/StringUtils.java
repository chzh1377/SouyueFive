package com.zhongsou.souyue.utils;

import android.text.TextUtils;
import android.util.FloatMath;
import com.zhongsou.souyue.platform.ConfigApi;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static final int LENGTH_46 = 46;
	public static final int LENGTH_60 = 60;
	public static final int LENGTH_12 = 12;
	public static final int LENGTH_200 = 200;

	public static int isSuperSrp(String keyword, String srpId){
	    if (keyword != null)
	        keyword = keyword.split(",")[0];
	    if ((keyword != null && (keyword.equals("超级分享大赛") || keyword.equals("分享赛"))) || (srpId != null && srpId.equals("da87eadcde647e2903419d4bda2f6dd6"))) {
	        return 1;//超级分享大赛
	    } else if ((keyword != null && (keyword.equals("掌上超模大赛"))) || (srpId != null && srpId.equals("7873daec778acfe6a84166fa5916921a"))) {
	    		return 2;//超模
	    } 
	    return 0;
	}


	/**
	 * URL解码
	 *
	 * @param str
	 * @return
	 */
	public static String decodeURL(String str) {
		if (!StringUtils.isEmpty(str)) {
			try {
				return URLDecoder.decode(str, "UTF-8");
			} catch (Exception e) {

			}
		}
		return str;
	}

	public static String enCodeRUL(String str) {
		if (!StringUtils.isEmpty(str)) {
			try {
				return URLEncoder.encode(str, "UTF-8");
			} catch (Exception ex) {
			}
		}
		return str;
	}

	public static boolean isEmpty(Object str) {
		return str == null || str.toString().length() == 0;
	}

	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}

	/* 去掉时间为00:00:00 */
	public static String replaceTimeZero(String date) {
		if (date != null) {
			if (date.indexOf("00:00:00") > 0) {
				date = date.replaceAll("00:00:00", "");
			} else if (date.indexOf(":00") == 16) {
				date = date.substring(0, 16);
			}
		}
		return date;
	}

	public static boolean startWithHttp(Object str) {
		return str != null
				&& str.toString().toLowerCase().startsWith("http://");
	}

	/* 字符串截取 防止出现半个汉字 */
    @Deprecated
	public static String truncate(String str, int byteLength) {
		switch (byteLength) {
		case LENGTH_200:
		case LENGTH_12:
			return truncate(str, byteLength, false);
		case LENGTH_46:
		case LENGTH_60:
		default:
			return truncate(str, byteLength, true);
		}

	}

	private static String truncate(String str, int byteLength, boolean isRandom){
		if (byteLength < 0)
			return "";
		if (str == null) {
			return null;
		}
		if (str.length() == 0) {
			return str;
		}
//		if (byteLength < 0) {
//			throw new IllegalArgumentException(
//					"Parameter byteLength must be great than 0");
//		}
		if (isRandom)
			byteLength += new Random().nextInt(15);
		int i = 0;
		int len = 0;
		int leng = 0;
		char[] chs = str.toCharArray();
		try {
			leng = str.getBytes("gbk").length;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (leng <= byteLength)
			return str;
		try {
			while ((len < byteLength) && (i < leng)) {
				len = (chs[i++] > 0xff) ? (len + 2) : (len + 1);
			} 
		} catch (Exception e){
			
		}

		if (len > byteLength) {
			i--;
		}
		return new String(chs, 0, i) + "...";
	}

	/**
	 * 分割keyword 按最后一个出现的@分割
	 *
	 * @param data
	 * @return keyword
	 */
	public static String splitKeyWord(String data) {
		if (data == null || data.length() == 0)
			return null;
		if (data.lastIndexOf("@") == -1)
			return data;
		return data.substring(0, data.lastIndexOf("@"));
	}

	/**
	 *
	 * @param date
	 *            (时间戳)
	 * @return 年－月－日 (2013-03-01)
	 */
	public static String convertDate(String date) {
		try {
			if (date == null || "".equals(date) || "0".equals(date))
				return "";
			if (isNumeric(date))
				return computingTime(Long.parseLong(date));
			else
				return "";
		} catch (Exception e) {
			return "";
		}
	}
    /**
     * 如果 缓存类型为AbstractAQuery.CACHE_POLICY_CACHE
     * 需要减去文件缓存时间
     * @param date
     * @return
     */
    public static String convertDate(String date,boolean reduce) {
		try {
			if (date == null || "".equals(date))
				return "";
			if (isNumeric(date))
				return computingTime(Long.parseLong(date),reduce);
			else
				return "";
		} catch (Exception e) {
			return "";
		}
	}

	public static String convertDateToYMD(String date){
	    try {
            if (date == null || "".equals(date))
                return "";
            if (isNumeric(date))
                return toNYR(Long.parseLong(date));
            else
                return date;
        } catch (Exception e) {
            return "";
        }
	}

	/**
	 * 确定是否是时间戳
	 *
	 * @param str
	 * @return
	 */
	private static boolean isNumeric(String str) {
		if (str == null || "".equals(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;

	}

    /**
     * 将精准时间格式转换为粗略时间格式
     *
     * @param
     */
//    public static String getTimeDiff(String specificTime) {
//        Date specificTimeDateMode = null;
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        try {
//            specificTimeDateMode = df.parse(specificTime);
//
//            specificTimeDateMode.getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Calendar cal = Calendar.getInstance();
//        long diff = 0;
//        Date dnow = cal.getTime();
//        String str = "";
//        diff = dnow.getTime() - specificTimeDateMode.getTime();
//        if (diff > 31104000000L) {// 12 * 30 * 24 * 60 * 60 * 1000=31104000000
//            // 毫秒
//            str = (int) Math.floor(diff / 31104000000L) + "年前";
//        } else if (diff > 2592000000L) {// 30 * 24 * 60 * 60 * 1000=2592000000
//            // 毫秒
//            str = (int) Math.floor(diff / 2592000000L) + "个月前";
//        } else if (diff > 1814400000) {// 21 * 24 * 60 * 60 * 1000=1814400000 毫秒
//            str = "3周前";
//        } else if (diff > 1209600000) {// 14 * 24 * 60 * 60 * 1000=1209600000 毫秒
//            str = "2周前";
//        } else if (diff > 604800000) {// 7 * 24 * 60 * 60 * 1000=604800000 毫秒
//            str = "1周前";
//        } else if (diff > 86400000f) { // 24 * 60 * 60 * 1000=86400000 毫秒
//            str = (int) Math.floor(diff / 86400000f) + "天前";
//        } else if (diff > 3600000) {// 1 * 60 * 60 * 1000=18000000 毫秒
//            str = (int) Math.floor(diff / 3600000f) + "小时前";
//        } else if (diff > 60000) {// 1 * 60 * 1000=60000 毫秒
//            str = (int) Math.floor(diff / 60000) + "分钟前";
//        } else {
//            str = "刚刚";
//        }
//        return str;
//    }


    /**
     * 将系统时间转成标准时间
     *
     * @param time
     * @return
     */
//    public static String convertToStandardTime(long time) {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date curDate = new Date(time);
//        String str = formatter.format(curDate);
//        return str;
//
//    }

        /**
         * 计算时间1-59分钟前，
         *
         * @param date
         * @return
         */
	public static String computingTime(Long date) {
		if (date < 10000)
			return "";
		float currentTime = System.currentTimeMillis();
		float x = (currentTime - date) / 1000L;
		int hour = (int) Math.floor((x / (60 * 60)));
		int min = (int) Math.floor((x / 60)) % 60;
//        LogDebugUtil.i("SystemRecommendFragment computingTime time================>",x+"");
		if (x / 60 <= 60) {
			if (min <= 1) {
				return "刚刚";
			} else if (min == 60) {
				return "59分钟前";
			} else
				return min + "分钟前";
		} else if (hour < 24) {
			if (hour <= 0)
				return "2小时前";
			return hour % 24 + "小时前";
		} else if (hour < 48)
				return "昨天";
		return toNYR(date);
	}


    private static String computingTime(Long date,boolean reduce) {
        if (date < 10000)
            return "";
        float currentTime = System.currentTimeMillis();
        float x = currentTime / 1000L - date / 1000L;
        if(reduce) x-=10;
        int hour = (int) FloatMath.ceil((x / (60 * 60)));
        int min = (int) FloatMath.ceil((x / 60)) % 60;
        if (x / 60 <= 60) {
            if (min <= 1) {
                return "刚刚";
            } else if (min == 60) {
                return "59分钟前";
            } else
                return min + "分钟前";
        } else if (hour < 24) {
            if (hour <= 0)
                return "2小时前";
            return hour % 24 + "小时前";
        } else if (hour < 48)
            return "昨天";
        return toNYR(date);
    }
	/**
	 * 截取年月日 如（2013-01-08）
	 *
	 * @param data
	 * @return yyyy-MM-dd
	 */
	private static String toNYR(long data) {
		SimpleDateFormat dateFormat;
		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(calendar.YEAR);
		calendar.setTimeInMillis(data);
		int sourceYear = calendar.get(calendar.YEAR);
		if(ConfigApi.isSouyue()){
			if(curYear == sourceYear) {
				dateFormat = setDataFormat("MM-dd");
			}else{
				dateFormat = setDataFormat("yyyy-MM-dd");
			}
		}else
		{
			dateFormat = setDataFormat("yyyy-MM-dd");
		}
		
		try {
			return dateFormat.format(data);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
     * 截取年月日 如（2013-01-08）
     *
     * @param data
     * @return yyyy-MM-dd 
     */
    public static String toYMHS(long data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d HH:mm");
        try {
            Date d= new Date(data);
            String str = dateFormat.format(d);
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    public static SimpleDateFormat setDataFormat(String format) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    	return dateFormat;
    }
	/**
	 * 抽正文标题
	 *
	 * @return
	 *//*
	public static String setReadabilityTitle(String str) {
		String res = null;
		if (str != null)
			if (str.length() > 9)
				res = str.substring(0, 3) + "..."
						+ str.substring(str.length() - 3, str.length());
		return res == null ? str : res;
	}
*/
	/**
	 * 解析去掉url路径，保留参数部分
	 *
	 * @param strURL
	 * @return
	 */
	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;

		strURL = strURL.trim().toLowerCase();

		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}

		return strAllParam;
	}

	/**
	 * 根据key 取得url中对应的参数值
	 *
	 * @param url
	 * @param key
	 * @return
	 */
	public static String getUrlParam(String url, String key) {
		if (key == null || url == null)
			return "";
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;

		String strUrlParam = TruncateUrlPage(url);
		if (strUrlParam == null) {
			return "";
		}
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");

			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest.get(key) == null ? "" : mapRequest.get(key);
	}

	/**
	 * 编码url中keword
	 *
	 * @param url
	 * @return
	 */
	public static String enCodeKeyword(String url) {
		String keyword = StringUtils.getUrlParam(url, "keyword");
		String reurl = url;
		if (!StringUtils.isEmpty(keyword))
			reurl = url.replace("&keyword=" + keyword, "&keyword="
					+ StringUtils.enCodeRUL(keyword));
		return reurl;
	}

	/**
	 * 获得分享的title
	 *
	 * @param title
	 * @param desc
	 * @return
	 */
	public static String shareTitle(String title, String desc) {
		String reTitle = title;
		if (!isEmpty(reTitle)) {
			return reTitle;
		} else if (!isEmpty(desc)) {
			if (desc.length() > 20) {
				reTitle = desc.substring(0, 20);
			} else
				reTitle = desc;
		}
		return reTitle;
	}

	public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

    public static String spliceString(List list, String splice) {
        if (null == list || list.size() <= 0 || splice == null || splice.length() == 0)
            return "";
        StringBuffer result = new StringBuffer();
        int macCount = list.size();
        for (int i = 0; i < macCount; i++) {
            result.append(list.get(i).toString());
            if (i != macCount - 1)
                result.append(splice);
        }
        return result.toString();
    }

    
    /**
	 * 分割优拍云
	 */
	public static String UpaiYun(String url) {
		if (url == null) {
			return "";
		}
		if (url.startsWith("http://souyue-image.b0.upaiyun.com/")||url.startsWith("http://sns-img.b0.upaiyun.com/")) {
			url  =  url.replaceAll("!.+$", "");
			String newUrl = url +"!android";
			return newUrl;
		}
		return url;
	}

    /**
     * 
     * @param str 长整型的数字  小于100万  则加千位分隔符（234,098），否则 取万为单位 200W+
     * @return
     */
    public static String formatNum(String str) {
        /*try{
            long l = Long.parseLong(str);
            if(l < 1000000){
                return (l/1000>0 ? l/1000+ ","+str.substring(str.length()-3, str.length()):""+l) ;
            } else {
                return l/10000 + "W" + (l%10000 == 0? "": "+");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(TextUtils.isEmpty(str)){
            return "";
        }
        return str.substring(0, Math.min(7, str.length()));
    }
    



//    public static String join(Object[] array, char separator)
//    {
//        if (array == null) {
//            return null;
//        }
//        return join(array, separator, 0, array.length);
//    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex)
    {
        if (array == null) {
            return null;
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return "";
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1);
        StringBuffer buf = new StringBuffer(bufSize);
        for (int i = startIndex; i < endIndex; i++)
        {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

//    public static String join(Object[] array, String separator)
//    {
//        if (array == null) {
//            return null;
//        }
//        return join(array, separator, 0, array.length);
//    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex)
    {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return "";
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());


        StringBuffer buf = new StringBuffer(bufSize);
        for (int i = startIndex; i < endIndex; i++)
        {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator iterator, char separator)
    {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first.toString();
        }
        StringBuffer buf = new StringBuffer(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext())
        {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator iterator, String separator)
    {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first.toString();
        }
        StringBuffer buf = new StringBuffer(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext())
        {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

//    public static String join(Collection collection, char separator)
//    {
//        if (collection == null) {
//            return null;
//        }
//        return join(collection.iterator(), separator);
//    }

    public static String join(Collection collection, String separator)
    {
        if (collection == null) {
            return null;
        }
        return join(collection.iterator(), separator);
    }
    public static String shareDesc(String desc) {
        if (!isEmpty(desc)) {
              if (desc.length() > 50) {
                  return desc.substring(0, 50);
              } else
                  return desc;
          }
        return "";
      }
}
