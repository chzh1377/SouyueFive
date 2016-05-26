package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.List;

public class ExpressionDetailBean implements DontObfuscateInterface
{
  private String desc;
  private String fileName;
  private String iconUrl;
  private long packageSize;
  private List<ThumbnailBean> thumbnails;

  public String getDesc()
  {
    return this.desc;
  }

  public String getFileName()
  {
    return this.fileName;
  }

  public String getIconUrl()
  {
    return this.iconUrl;
  }

  public long getPackageSize()
  {
    return this.packageSize;
  }

  public List<ThumbnailBean> getThumbnails()
  {
    return this.thumbnails;
  }

  public void setDesc(String paramString)
  {
    this.desc = paramString;
  }

  public void setFileName(String paramString)
  {
    this.fileName = paramString;
  }

  public void setIconUrl(String paramString)
  {
    this.iconUrl = paramString;
  }

  public void setPackageSize(long paramLong)
  {
    this.packageSize = paramLong;
  }

  public void setThumbnails(List<ThumbnailBean> paramList)
  {
    this.thumbnails = paramList;
  }
}