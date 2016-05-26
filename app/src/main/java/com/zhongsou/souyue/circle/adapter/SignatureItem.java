package com.zhongsou.souyue.circle.adapter;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SignatureItem {
	public RelativeLayout rel;
	public ImageView image;
	public TextView text;
	public int position;
	public boolean ifFocus;
	
	
	public SignatureItem(RelativeLayout rel, ImageView image, TextView text,int position,
			boolean ifFocus) {
		super();
		this.rel = rel;
		this.image = image;
		this.text = text;
		this.ifFocus = ifFocus;
	}
	public RelativeLayout getRel() {
		return rel;
	}
	public void setRel(RelativeLayout rel) {
		this.rel = rel;
	}
	public ImageView getImage() {
		return image;
	}
	public void setImage(ImageView image) {
		this.image = image;
	}
	public TextView getText() {
		return text;
	}
	public void setText(TextView text) {
		this.text = text;
	}
	public boolean isIfFocus() {
		return ifFocus;
	}
	public void setIfFocus(boolean ifFocus) {
		this.ifFocus = ifFocus;
	}
	

}
