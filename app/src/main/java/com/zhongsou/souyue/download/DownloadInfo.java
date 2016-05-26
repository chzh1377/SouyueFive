package com.zhongsou.souyue.download;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;

/**
  *创建一个下载信息的实体类
  */
 public class DownloadInfo implements Serializable,DontObfuscateInterface{

     public static final int STATE_INIT = 1;        // 初始化等待中
     public static final int STATE_LOADING = 2;     // 下载中
     public static final int STATE_PAUSE = 3;       // 暂停中
    public static final int STATE_FAILED = 4;       // 下载失败
     public static final int STATE_COMPLETE = 5;    // 下载完成

     public static final int DOWNLOAD_TYPE_VIDEO = 0;
     public static final int DOWNLOAD_TYPE_BOOK = 1;

	private static final long serialVersionUID = 1L;
	
	private String onlyId	;	//服务器传来的唯一id，作为一个视频（可能含有多段视频url）的唯一标示
	 private String name ;
	 private int length ;
	 private int curLength ;    // 总完成度
	 private int type;			//文件类型  0：视频 1：小说
	 private String urls;  	    //url数组
	 private String curUrl;		//当前正在下载的url
     private int compeleteSize; //当前URL完成度
     private String imgUrl;  	//封面地址

	 private int state;			// 下载状态:1初始化等待下载，2下载中, 3已暂停，4下载失败，5已完成
     private int threadId;      //下载器id
     private int startPos;      //开始点
     private int endPos;        //结束点
     private String url;        //下载器网络标识

    private List<UrlConsume> urlList;   // urls对应的bean

    public List<UrlConsume> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<UrlConsume> urlList) {
        this.urlList = urlList;
    }


     public DownloadInfo(String onlyId	,String name ,int length ,int curLength, 
    		 int type, String urls, String curUrl, String imgUrl, int state,
    		 int threadId, int startPos, int endPos, int compeleteSize, String url) {
    	 this.onlyId = onlyId;
    	 this.name = name;
    	 this.length = length;
    	 this.curLength = curLength;
    	 this.type = type;
    	 this.urls = urls;
    	 this.curUrl = curUrl;
    	 this.imgUrl = imgUrl;
    	 this.state = state;
    	 
         this.threadId = threadId;
         this.startPos = startPos;
         this.endPos = endPos;
         this.compeleteSize = compeleteSize;
         this.url=url;
     }
     public DownloadInfo(int threadId, int startPos, int endPos,int compeleteSize,String url) {
         this.threadId = threadId;
         this.startPos = startPos;
         this.endPos = endPos;
         this.compeleteSize = compeleteSize;
         this.url=url;
     }
     public DownloadInfo() {
     }
     public String getUrl() {
         return url;
     }
     public void setUrl(String url) {
         this.url = url;
     }
     public int getThreadId() {
         return threadId;
     }
     public void setThreadId(int threadId) {
         this.threadId = threadId;
     }
     public int getStartPos() {
         return startPos;
     }
     public void setStartPos(int startPos) {
         this.startPos = startPos;
     }
     public int getEndPos() {
         return endPos;
     }
     public void setEndPos(int endPos) {
         this.endPos = endPos;
     }
     public int getCompeleteSize() {
         return compeleteSize;
     }
     public void setCompeleteSize(int compeleteSize) {
         this.compeleteSize = compeleteSize;
     }
 
     @Override
     public String toString() {
         return "DownloadInfo [threadId=" + threadId
                 + ", startPos=" + startPos + ", endPos=" + endPos
                 + ", compeleteSize=" + compeleteSize +"]";
     }
     
     
     public String getOnlyId() {
 		return onlyId;
 	}
 	public void setOnlyId(String onlyId) {
 		this.onlyId = onlyId;
 	}
 	public String getName() {
 		return name;
 	}
 	public void setName(String name) {
 		this.name = name;
 	}
 	public int getLength() {
 		return length;
 	}
 	public void setLength(int length) {
 		this.length = length;
 	}
 	public int getCurLength() {
 		return curLength;
 	}
 	public void setCurLength(int curLength) {
 		this.curLength = curLength;
 	}
 	public int getType() {
 		return type;
 	}
 	public void setType(int type) {
 		this.type = type;
 	}
 	public String getUrls() {
 		return urls;
 	}
 	public void setUrls(String urls) {
 		this.urls = urls;
 	}
 	public String getCurUrl() {
 		return curUrl;
 	}
 	public void setCurUrl(String curUrl) {
 		this.curUrl = curUrl;
 	}
 	public String getImgUrl() {
 		return imgUrl;
 	}
 	public void setImgUrl(String imgUrl) {
 		this.imgUrl = imgUrl;
 	}
 	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
 }