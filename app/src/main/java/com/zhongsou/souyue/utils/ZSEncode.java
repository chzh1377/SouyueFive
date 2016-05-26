package com.zhongsou.souyue.utils;

import java.net.URLEncoder;

public class ZSEncode {
    public static boolean isAscii(char ch) {
        return ch <= 126;
    }
    
    /**
     * 可以使用Uri.encode()代替
     */
    @Deprecated
    public static String encodeURI(String url) {
        if (url == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (isAscii(ch)) {
                switch (ch) {
                    case '"':
                        sb.append("%22");
                        break;
                    case '%':
                        sb.append("%25");
                        break;
                    case '<':
                        sb.append("%3C");
                        break;
                    case '>':
                        sb.append("%3E");
                        break;
                    case '[':
                        sb.append("%5B");
                        break;
                    case ']':
                        sb.append("%5D");
                        break;
                    case '^':
                        sb.append("%5E");
                        break;
                    case '`':
                        sb.append("%60");
                        break;
                    case '{':
                        sb.append("%7B");
                        break;
                    case '|':
                        sb.append("%7C");
                        break;
                    case '}':
                        sb.append("%7D");
                        break;
                    case ' ':
                        sb.append("%20");
                        break;
                    default:
                        sb.append(ch);
                        break;
                }
            } else {
                try {
                    sb.append(URLEncoder.encode(Character.toString(ch), "UTF-8"));
                } catch (Exception e) {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

}
