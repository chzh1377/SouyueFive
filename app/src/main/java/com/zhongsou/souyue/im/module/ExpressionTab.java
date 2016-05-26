package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

public class ExpressionTab implements DontObfuscateInterface
{
  private String fileName;
  private String id;
  private String packageName;

  public boolean equals(Object paramObject)
  {
    ExpressionTab localExpressionTab = (ExpressionTab)paramObject;
    return localExpressionTab.fileName.equals(localExpressionTab.getFileName());
  }

  public String getFileName()
  {
    return this.fileName;
  }

  public String getId()
  {
    return this.id;
  }

  public String getPackageName()
  {
    return this.packageName;
  }

  public void setFileName(String paramString)
  {
    this.fileName = paramString;
  }

  public void setId(String paramString)
  {
    this.id = paramString;
  }

  public void setPackageName(String paramString)
  {
    this.packageName = paramString;
  }
}