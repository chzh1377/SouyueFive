package com.zhongsou.souyue.im.search;

import com.zhongsou.souyue.DontObfuscateInterface;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 消息搜索结果
 * 
 * @author zhaomeng
 * 
 */
public class MsgResult extends Result implements Serializable,DontObfuscateInterface {
	/**
	 * 消息id列表
	 */
	public ArrayList<Integer> msgIds;
}
