package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class ExpressionBean extends GifBean
  implements Serializable, DontObfuscateInterface
{
  private String eCloundUrl;
  private String eId;
  private String eName;
  private String eRealName;
  private String eSendUrl;
  private String eThumbnailUrl;

  public String geteCloundUrl()
  {
    return this.eCloundUrl;
  }

  public String geteId()
  {
    return this.eId;
  }

  public String geteName()
  {
    return this.eName;
  }

  public String geteRealName()
  {
    return this.eRealName;
  }

  public String geteSendUrl()
  {
    return this.eSendUrl;
  }

  public String geteThumbnailUrl()
  {
    return this.eThumbnailUrl;
  }

  public void seteCloundUrl(String paramString)
  {
    this.eCloundUrl = paramString;
  }

  public void seteId(String paramString)
  {
    this.eId = paramString;
  }

  public void seteName(String paramString)
  {
    this.eName = paramString;
  }

  public void seteRealName(String paramString)
  {
    this.eRealName = paramString;
  }

  public void seteSendUrl(String paramString)
  {
    this.eSendUrl = paramString;
  }

  public void seteThumbnailUrl(String paramString)
  {
    this.eThumbnailUrl = paramString;
  }

  public String toString()
  {
    return "expression [ eSendUrl" + this.eSendUrl + ",eId:" + this.eId + "   eName:" + this.eName + "  , eRealName:" + this.eRealName + "  ,eThumbnailUrl:" + this.eThumbnailUrl + " ,  eCloundUrl:" + this.eCloundUrl + "]";
  }
}
