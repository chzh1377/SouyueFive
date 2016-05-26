package com.zhongsou.souyue.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.List;

public class TouchGallerySerializable implements Serializable ,DontObfuscateInterface{
	private static final long serialVersionUID = -5573280569243009936L;
	private List<String> items;
	private int clickIndex = 0;
	public int getClickIndex() {
		return clickIndex;
	}
	public void setClickIndex(int clickIndex) {
		this.clickIndex = clickIndex;
	}
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
}
