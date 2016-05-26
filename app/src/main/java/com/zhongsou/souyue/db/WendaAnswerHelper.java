package com.zhongsou.souyue.db;

public class WendaAnswerHelper {

	public int getState(String questionId,String answerId) {
		WendaAnswerTableDBHelper helper = new WendaAnswerTableDBHelper();
		helper.openReadable();
		int result = helper.select(questionId,answerId);
		helper.close();
		return result;
	}

	/**
	 * 设置状态
	 * 
	 * @param st
	 *            0点击但服务器还没有返回成功 1服务器返回成功
	 * @return true成功 false已经点击过
	 */
	public boolean setState(String questionId,String answerId, int st,int upOrDown) {
		int state = getState(questionId,answerId);
		WendaAnswerTableDBHelper helper = new WendaAnswerTableDBHelper();
		helper.openWritable();
		if (state == -1) {// 没被点击 数据库没记录 可以设置为0
			helper.insert(questionId,answerId, 0,upOrDown);
			helper.close();
			return true;
		} else if (state == 0) {//点击过 更新
			helper.update(questionId,answerId, st,upOrDown);
			helper.close();
			return true;
		}
		helper.close();
		return false;// 不能被点击 已经被点击并成功提交到服务器
	}

	public int selectUpOrDown(String questionId, String answerId){
		WendaAnswerTableDBHelper helper = new WendaAnswerTableDBHelper();
		helper.openWritable();
		int up_Down = helper.selectUpOrDown(questionId, answerId);
		helper.close();
		return up_Down;
	}
}
