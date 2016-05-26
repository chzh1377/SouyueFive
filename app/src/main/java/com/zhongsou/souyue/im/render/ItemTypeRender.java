package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */
import android.view.View;

/**
 * 用于对不同类型item数据到UI的渲染
 */
public interface ItemTypeRender {

	/**
	 * 返回一个item的convertView，也就是BaseAdapter中getView方法中返回的convertView
	 * 
	 * @return
	 */
	View getConvertView();

	/**
	 * 填充item中各个控件的事件，比如按钮点击事件等
	 */
	void fitEvents();

	/**
	 * 对指定position的item进行数据的适配
	 * 
	 * @param position
	 */
	void fitDatas(int position);

	/**
	 * 设置消息的发送类
	 * 
	 * @param messageManager
	 */
	void setMesssageManager(MessageManager messageManager);

}
