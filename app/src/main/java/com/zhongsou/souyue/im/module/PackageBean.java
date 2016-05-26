package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;

public class PackageBean
  implements Serializable,DontObfuscateInterface
{
  private static final long serialVersionUID = 47832438L;
  private String desc;
  private String fileName;
  private String iconUrl;
  private int isDownloaded;
  private int isNew;
  private String packageId;
  private String packageName;
  private long packageSize;
  private String price;
  private long sortNo;

  public boolean equals(Object paramObject)
  {
    PackageBean localPackageBean = (PackageBean)paramObject;
    return this.packageId.equals(localPackageBean.packageId);
  }

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

  public int getIsDownloaded()
  {
    return this.isDownloaded;
  }

  public int getIsNew()
  {
    return this.isNew;
  }

  public String getPackageId()
  {
    return this.packageId;
  }

  public String getPackageName()
  {
    return this.packageName;
  }

  public long getPackageSize()
  {
    return this.packageSize;
  }

  public String getPrice()
  {
    return this.price;
  }

  public long getSortNo()
  {
    return this.sortNo;
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

  public void setIsDownloaded(int paramInt)
  {
    this.isDownloaded = paramInt;
  }

  public void setIsNew(int paramInt)
  {
    this.isNew = paramInt;
  }

  public void setPackageId(String paramString)
  {
    this.packageId = paramString;
  }

  public void setPackageName(String paramString)
  {
    this.packageName = paramString;
  }

  public void setPackageSize(long paramLong)
  {
    this.packageSize = paramLong;
  }

  public void setPrice(String paramString)
  {
    this.price = paramString;
  }

  public void setSortNo(long paramLong)
  {
    this.sortNo = paramLong;
  }
}
