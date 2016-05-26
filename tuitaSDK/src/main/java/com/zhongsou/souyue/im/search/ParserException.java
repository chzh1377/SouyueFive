package com.zhongsou.souyue.im.search;

/**
 * 数据解析异常
 * 
 * @author zhaomeng
 *
 */
public class ParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 13045793233312639L;

	public ParserException() {
		super("data is null or data format is error");
	}
}
