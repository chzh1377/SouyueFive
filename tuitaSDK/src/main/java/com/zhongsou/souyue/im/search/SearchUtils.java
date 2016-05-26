package com.zhongsou.souyue.im.search;

import java.io.File;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;

public class SearchUtils {

//	public static String mIndexPath = getFilesDir().getAbsolutePath()+"/im_index" ;
//    MainActivity.SEARCH_PATH_MEMORY_DIR;
//    public static final String mIndexPath = "/mnt/sdcard/souyue/im_index";

	public static int addMessage(String msgText, short sessionType,
			long sessionId, int msgId) throws UnsupportedEncodingException {
		if (msgText != null) {
			byte[] buffer = msgText.getBytes("utf-8");
			return IMQuery.addMessage(buffer, buffer.length, sessionType,
					sessionId, msgId);
		}
		return -1;

	}

	public static int saveIndex(String mIndexPath,long userId) {
		return IMQuery.saveIndex(mIndexPath+userId);
	}

	public static int loadIndex(String mIndexPath,long userId) {
		return IMQuery.loadIndex(mIndexPath+userId);
	}

    /**
     * 删除一条会话
     */
    public static void deleteSession(String mIndexPath,long userId, short sessionType,long sessionId){
        IMQuery.init();
        IMQuery.loadIndex(mIndexPath+userId);
        IMQuery.deleteSession(sessionType,sessionId);
        IMQuery.saveIndex(mIndexPath+userId);
        IMQuery.destory();
    }

    /**
     * 删除一条消息
     */
    public static void delMessage(String mIndexPath,long userId, short sessionType,long sessionId,
                                  int msgId,String msgText)throws UnsupportedEncodingException{
        IMQuery.init();
        IMQuery.loadIndex(mIndexPath + userId);
        byte[] buffer = msgText.getBytes("utf-8");
        IMQuery.delMessage(sessionType,sessionId,msgId,buffer, buffer.length);
        IMQuery.saveIndex(mIndexPath+userId);
        IMQuery.destory();
    }

    /**
     * 初始化搜索引擎，并根据消息id导入数据
     */
    public static void updateIMSearchMsg(final Context context, final long targetID) {
        IMQuery.init();
        final long maxMessageID = IMQuery.getCurMaxMessageID();
        //TODO  根据getMaxMessageID()结果 查imdb >这个id的所有消息 addMessage 开线程
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MessageHistoryDaoHelper.getInstance(context).updateSearchMsg(targetID,maxMessageID);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
