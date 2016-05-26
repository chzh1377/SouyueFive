package com.zhongsou.souyue.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.tuita.sdk.im.db.helper.MessageHistoryDaoHelper;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.im.search.IMQuery;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.log.Logger;
import com.zhongsou.souyue.utils.SYUserManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 接收消息广播并调用 search  addmessage
 * <p/>
 * Created by zhangwenbin on 15/4/20.
 */
public class IMAddMsgReceiver extends BroadcastReceiver {

    //获取当期最大消息id
    public long maxMessageID = -1;
    public long localMaxId;

    @Override
    public void onReceive(final Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("curMaxMessageID", Context.MODE_PRIVATE);
        maxMessageID = sharedPreferences.getLong("id",-1);
        if(SYUserManager.getInstance().getUserId()==null)   return;
        long userId = Long.parseLong(SYUserManager.getInstance().getUserId());
        localMaxId = MessageHistoryDaoHelper.getInstance(context).findMaxId(userId);

        if(Math.abs(localMaxId-maxMessageID)>100){

            //打印搜索结果日志
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Logger.i("IM消息搜索————IMAddMsgReceiver", "time:" + date.format(new Date()), "localMaxId-maxMessageID>100 啦！！！");
            //根据getMaxMessageID()结果 查imdb >这个id的所有消息 addMessage 开线程
            IMQuery.init();
            SearchUtils.loadIndex(MainActivity.SEARCH_PATH_MEMORY_DIR,userId);
            try {
                MessageHistoryDaoHelper.getInstance(context).updateSearchMsg(userId,maxMessageID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            maxMessageID = IMQuery.getCurMaxMessageID();
            SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
            editor.putLong("id", maxMessageID);
            editor.commit();

            SearchUtils.saveIndex(MainActivity.SEARCH_PATH_MEMORY_DIR,userId);
            IMQuery.destory();
        }

        /**
         * 测试10000条数据
         */
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                    long time = System.currentTimeMillis();
//                try {
//                    for (int i = 0; i < 10000; i++) {
//                        SearchUtils.addMessage("测试测试测试"+i, (short) 0, (long) i, i);
//                    }
//                }catch (Exception e){
//
//                }
////                Toast.makeText(context,"一共用了"+ (System.currentTimeMillis() - time)+"毫秒",Toast.LENGTH_SHORT).show();
//                Log.i("timeaddmsg", "一共用了" + (System.currentTimeMillis() - time) + "毫秒");
//                SearchUtils.saveIndex(MainActivity.SEARCH_PATH_MEMORY_DIR,Long.parseLong(SYUserManager.getInstance().getUserId()));
//            }
//        }).start();


//        Toast.makeText(context, "11111111", Toast.LENGTH_SHORT).show();
    }
}
