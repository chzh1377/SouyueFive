package com.zhongsou.souyue.im.module;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.util.List;

/**
 * 一套表情元素
 * 
 * @author wangqiang
 * 
 */
public class ExpressionPackage implements DontObfuscateInterface {
	private List<ExpressionBean> expressionBeans;
	private ExpressionTab tab;

	public List<ExpressionBean> getExpressionBeans() {
		return this.expressionBeans;
	}

	public ExpressionTab getTab() {
		return this.tab;
	}

	public void setExpressionBeans(List<ExpressionBean> paramList) {
		this.expressionBeans = paramList;
	}

	public void setTab(ExpressionTab expressionTab) {
		this.tab = expressionTab;
	}
}