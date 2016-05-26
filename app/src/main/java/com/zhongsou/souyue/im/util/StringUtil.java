package com.zhongsou.souyue.im.util;

public class StringUtil
{
  public static boolean isBlank(String paramString)
  {
    return (paramString == null) || ((paramString != null) && (paramString.trim().length() == 0));
  }
}