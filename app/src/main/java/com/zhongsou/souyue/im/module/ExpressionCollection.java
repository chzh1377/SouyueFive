package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.List;

/**
 * 表情
 * 
 * @author wangqiang
 * 
 */
public class ExpressionCollection implements DontObfuscateInterface{
	private List<ExpressionBean> list;

	public List<ExpressionBean> getList() {
		return this.list;
	}

	public void setList(List<ExpressionBean> list) {
		this.list = list;
	}

}
