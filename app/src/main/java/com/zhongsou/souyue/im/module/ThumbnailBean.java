package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

public class ThumbnailBean implements DontObfuscateInterface
{
  private String realName;
  private String thumbnailUrl;

  public String getRealName()
  {
    return this.realName;
  }

  public String getThumbnailUrl()
  {
    return this.thumbnailUrl;
  }

  public void setRealName(String paramString)
  {
    this.realName = paramString;
  }

  public void setThumbnailUrl(String paramString)
  {
    this.thumbnailUrl = paramString;
  }
}