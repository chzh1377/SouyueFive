package com.zhongsou.souyue.module;


import android.os.Parcel;
import android.os.Parcelable;

public class TitleBarSource implements Parcelable{

	private String picurl; //素材图片地址
	
	private String title; //素材名称
	
	private String desc; //type为1时代表图片描述，为2时代表色值
	
	private String type ; // 1： 图片   2：色值
	
	private String data;

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public static final Creator<TitleBarSource> CREATOR = new Creator<TitleBarSource>() {
        public TitleBarSource createFromParcel(Parcel source) {
        	TitleBarSource titleBarSource = new TitleBarSource();
        	titleBarSource.picurl = source.readString();  
        	titleBarSource.title = source.readString();  
        	titleBarSource.desc = source.readString();  
        	titleBarSource.type = source.readString(); 
        	titleBarSource.data = source.readString(); 
            return titleBarSource;  
        }  
        public TitleBarSource[] newArray(int size) {
            return new TitleBarSource[size];
        }  
    };  
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(picurl); 
		arg0.writeString(title); 
		arg0.writeString(desc); 
		arg0.writeString(type); 
		arg0.writeString(data); 
	}
}
