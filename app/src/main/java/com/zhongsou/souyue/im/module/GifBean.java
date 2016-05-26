package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class GifBean extends SendGifBean
  implements Serializable, DontObfuscateInterface
{
  private int gifDrawableId;
  private String gifName;
  private String gifRealName;
  private String gifid;
  private String gifurl;

  public int getGifDrawableId()
  {
    return this.gifDrawableId;
  }

  public String getGifName()
  {
    return this.gifName;
  }

  public String getGifRealName()
  {
    return this.gifRealName;
  }

  public String getGifid()
  {
    return this.gifid;
  }

  public String getGifurl()
  {
    return this.gifurl;
  }

  public void setGifDrawableId(int paramInt)
  {
    this.gifDrawableId = paramInt;
  }

  public void setGifName(String paramString)
  {
    this.gifName = paramString;
  }

  public void setGifRealName(String paramString)
  {
    this.gifRealName = paramString;
  }

  public void setGifid(String paramString)
  {
    this.gifid = paramString;
  }

  public void setGifurl(String paramString)
  {
    this.gifurl = paramString;
  }
}
