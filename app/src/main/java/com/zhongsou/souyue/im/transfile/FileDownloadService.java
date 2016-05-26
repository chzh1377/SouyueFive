package com.zhongsou.souyue.im.transfile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.tuita.sdk.im.db.helper.MessageFileDaoHelper;
import com.tuita.sdk.im.db.module.MessageFile;
import com.zhongsou.souyue.im.render.MsgUtils;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by xyh0125 on 15/11/9.
 * 文件下载service
 */
public class FileDownloadService extends Service {

    private static final String ACTION_FIRSTNAME = "DownloadFileService";
    private static final String ACTION_PAUSE_ITEM = ACTION_FIRSTNAME + ".PAUSE_ITEM";
    private static final String ACTION_ADD_ITEM = ACTION_FIRSTNAME + ".ADD_ITEM";
    private static final String ACTION_STOP_THREAD = ACTION_FIRSTNAME + ".STOP_THREAD";

    private static IMFileDetailActivity.FileDownloadListener fileDownloadListener;
    private CMainHttp cMainHttp;
    // 下载线程
    private FileDownloadThread fileDownloadThread;
    // 所有待下载url队列
    private volatile BlockingDeque<MessageFile> fileQueue = new LinkedBlockingDeque<MessageFile>();
    private static volatile boolean stopThread;

    @Override
    public void onCreate() {
        super.onCreate();
        stopThread = false;
        cMainHttp = CMainHttp.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int command = super.onStartCommand(intent, flags, startId);

        if (intent == null) {
            return command;
        }

        String action = intent.getAction();
        if (StringUtils.isEmpty(action)) {
            return command;
        }

        if (action.equals(ACTION_ADD_ITEM)) {
            MessageFile item = (MessageFile) intent.getSerializableExtra("MessageFile");
            addItemToQueue(item);
        } else if (action.equals(ACTION_PAUSE_ITEM)) {
            MessageFile item = (MessageFile) intent.getSerializableExtra("MessageFile");
//            pauseQueueItem(item.getId());
            pauseQueueItem();
        } else if (action.equals(ACTION_STOP_THREAD)) {
            MessageFile item = (MessageFile) intent.getSerializableExtra("MessageFile");
            stopThread(item);
//            stopDownload();
        }

        return command;
    }

    public void stopThread(MessageFile item) {
        stopThread = true;
    }

    private void startDownloadThread() {

        stopThread = false;
        if (fileDownloadThread != null && fileDownloadThread.isAlive()) {
            return;
        }
        if (fileDownloadThread == null) {
            fileDownloadThread = new FileDownloadThread();
        }
        try {
            fileDownloadThread.start();
        } catch (Exception e) {

        }
    }


    public class FileDownloadThread extends Thread {

        @Override
        public void run() {

//            while ( !stopThread ){
//                MessageFile toDoItem = null;
//                try {
//                    toDoItem = fileQueue.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //开始下载
//                if (toDoItem!=null) {
//                    cMainHttp.doDownload(101, getSavePath(), toDoItem.getUrl(), FileDownloadService.this, FileDownloadService.this);
//                }
//            }
            //线程停止，移除队列
            if (stopThread) {
                fileQueue.remove();
            }

            while (!fileQueue.isEmpty() && !stopThread) {
                MessageFile item = fileQueue.getFirst();

                if (item != null) {
                    item.setState(MessageFile.DOWNLOAD_STATE_LOADING);
                    sendBroadcastUpdateState(item);

                    // 执行下载
                    if (item.getState() == MessageFile.DOWNLOAD_STATE_FAILED || item.getState() == MessageFile.DOWNLOAD_STATE_PAUSE) {
                        break;
                    }

                    download(item);

                    //TODO:貌似不需要发广播，暂时注掉，11.28晚上注
//                    if(item.getCursize()==null){
//                        item.setCursize((long) 0);
//                    }
//                    sendBroadcastUpdateState(item);
                } else {
                    fileQueue.removeFirst();
                }

            }

            stopSelf();
        }

    }

    // *********************内部操作
    // 添加到队列
    private void addItemToQueue(MessageFile item) {
        if (item == null) {
            return;
        }
        fileQueue.addLast(item);
        //自己的download逻辑
        startDownloadThread();
        //用别人的download逻辑
//        startDownload();
    }

    private void pauseQueueItem() {
        for (MessageFile item : fileQueue) {
            item.setState(MessageFile.DOWNLOAD_STATE_PAUSE);
            break;
        }
    }

    private void download(MessageFile item) {
        if (item == null) {
            return;
        }
        HttpURLConnection connection = null;
        InputStream in = null;
        RandomAccessFile randomFile = null;
        try {
            //自己encode，排除中文影响，现在encode操作已经放在服务器处理
//            String url = URLEncoder.encode(item.getUrl(), "utf-8").replaceAll("\\+", "%20");
//            URL localURL = new URL(url.replaceAll("%3A", ":").replaceAll("%2F", "/"));
            URL localURL = new URL(item.getUrl());
            connection = (HttpURLConnection) localURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Encoding", "identity");   //防止返回-1
            if (item.getCursize() == null) {
                item.setCursize((long) 0);
            }
            String onlyName;
            if (item.getCursize() != 0) {
                connection.setRequestProperty("Range", "bytes=" + item.getCursize() + "-");  //断点续传
                onlyName = item.getName();
            } else {
                onlyName = MsgUtils.createOnlyFileName(getSavePath(), item.getName());
                item.setName(onlyName);
            }
            connection.connect();
            randomFile = new RandomAccessFile(getSavePath() + onlyName, "rw");
            in = connection.getInputStream();
            randomFile.seek(item.getCursize());
            File path = null;
            path = new File(getSavePath());
            if (!path.exists()) {
                path.mkdirs();
            }
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {

                randomFile.write(buffer, 0, len);

                if (stopThread) {
//                    delFile(getSavePath()+onlyName);
                    long curSize = item.getCursize() + len;
                    Log.i("ImDownload", "FileDownloadService==> stopThread=curSize:" + curSize);
                    item.setCursize(curSize);
//                    //TODO:下面这句是和svn上不同的
//                    item.setState(MessageFile.DOWNLOAD_STATE_PAUSE);
                    MessageFileDaoHelper.getInstance(getApplicationContext()).update(item);
                    break;
                }

                //总下载进度
                Long cur = item.getCursize() + len;
                item.setCursize(cur);
                Log.i("ImDownload", "FileDownloadService==> setCursize=:" + cur);

                if (item.getState() == MessageFile.DOWNLOAD_STATE_PAUSE) {
                    //以后断点续传用
//                        item.setCursize(cur);
                    break;
                }

                if (cur >= item.getSize()) {
                    item.setState(MessageFile.DOWNLOAD_STATE_COMPLETE);
                    item.setLocalpath(getSavePath() + onlyName);
                    item.setUpdateTime(System.currentTimeMillis());
                    item.setName(onlyName);
                    MessageFileDaoHelper.getInstance(getApplicationContext()).updateState(item.getId(), MessageFile.DOWNLOAD_STATE_COMPLETE);
                    MessageFileDaoHelper.getInstance(getApplicationContext()).update(item);
                    sendBroadcastUpdateState(item);
                    stopThread = true;
                }

//                    if(item.getSize()>1024*200 && fileLength>1024*100){
                if (item.getSize() > 1024 * 200) {
                    long block = item.getSize() / 100;
                    Log.i("aboutBlock", "block=:" + block + "  cur=:" + cur + "   item.getSize():" + item.getSize() + "   cur%(block*2)=" + cur % (block * 2));
                    if (cur % (block * 2) < (0.5 * block)) {
                        // 更新进度条进行
                        sendBroadcastUpdateProgress(item);
                        Log.i("aboutBlock", " 进入进度条进行啦 cur=:" + cur);
                    }
                } else {
                    sendBroadcastUpdateProgress(item);
                }
//                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            downFailedException(item);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    private void downFailedException(MessageFile item) {
        pauseQueueItem();
        if (!fileQueue.isEmpty()) {
            fileQueue.removeFirst();
        }
        item.setState(MessageFile.DOWNLOAD_STATE_PAUSE);
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    /**
     * 更新进度
     * @param item
     */
    private void sendBroadcastUpdateProgress(MessageFile item) {
        Intent intent = new Intent();
        intent.setAction(IMFileDetailActivity.BROADCAST_DOWNLOAD_FILE_TAG);
        intent.putExtra("what", 1);
        intent.putExtra("obj", item);
        sendBroadcast(intent);
    }

    /**
     * 更新状态
     * @param item
     */
    private void sendBroadcastUpdateState(MessageFile item) {
        Intent intent = new Intent();
        intent.setAction(IMFileDetailActivity.BROADCAST_DOWNLOAD_FILE_TAG);
        intent.putExtra("what", 2);
        intent.putExtra("obj", item);
        sendBroadcast(intent);
//        fileDownloadListener.setState();
    }

    // *********************外部调用方法
    public static void addItemToQueue(Context ctx, MessageFile item) {
        Intent i = new Intent(ctx, FileDownloadService.class);
        i.setAction(ACTION_ADD_ITEM);
        i.putExtra("MessageFile", item);
        ctx.startService(i);
    }

    public static void pauseQueueItem(Context ctx, MessageFile item) {
        Intent i = new Intent(ctx, FileDownloadService.class);
        i.setAction(ACTION_PAUSE_ITEM);
        i.putExtra("MessageFile", item);
        ctx.startService(i);
    }

    public static void stopThread(Context ctx, MessageFile item) {
        Intent i = new Intent(ctx, FileDownloadService.class);
        i.setAction(ACTION_STOP_THREAD);
        i.putExtra("MessageFile", item);
        ctx.startService(i);
    }

    // *********************工具方法*********************
    public static String getSavePath() {
        String path = getSDCardPath() + "/souyue/";
        return path;
    }

    /**
     * 获取SDCard的目录路径功能
     */
    public static String getSDCardPath() {
        String sdcardDir = "";
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory().toString();
        }
        return sdcardDir;
    }

    public static void setListener(IMFileDetailActivity.FileDownloadListener listener) {
        fileDownloadListener = listener;
    }

/**
 *
 *  调用volley下载框架
  */

//    @Override
//    public void onHttpProcess(long _totle_length, long _cur_length) {
//
//        MessageFile item = new MessageFile();
//        item.setSize(_totle_length);
//        item.setCursize(_cur_length);
//        Log.i("ImDownload", "FileDownloadService==> _cur_length=:" + _cur_length);
//        Log.i("ImDownload", "FileDownloadService==> _totle_length=:"  + _totle_length);
//        Log.i("ImDownload", "FileDownloadService==> percent=:"        +(_cur_length/_totle_length)*100);
//
//        //下载完成 调用 cancelAll ，避免内存泄露
//        if(_totle_length==_cur_length){
//////            stopThread();
////            stopDownload();
////            return;
//
//            //下载完成  更新db
//            MessageFile messageFile =new MessageFile();
//            messageFile.setState(MessageFile.DOWNLOAD_STATE_COMPLETE);
//            messageFile.setCursize(_cur_length);
//            messageFile.setUpdateTime(System.currentTimeMillis());
//
//            MessageFileDaoHelper.getInstance(this).update(messageFile);
//
//            //广播通知 下载完成
//            sendBroadcastUpdateState(item);
//
//            //避免内存泄露
//            cMainHttp.cancelAll();
//
//            return;
//        }
//
//        //更新进度:将下载回调的结果通过广播通知UI
//        sendBroadcastUpdateProgress(item);
//    }
//
//
//    @Override
//    public void onHttpResponse(IRequest _request) {
//
//    }
//
//    @Override
//    public void onHttpError(IRequest _request) {
//
//    }
//
//    @Override
//    public void onHttpStart(IRequest _request) {
//
//    }
//
//    private void startDownload() {
//        MessageFile toDoItem = null;
//        try {
//            toDoItem = fileQueue.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        cMainHttp.doDownload(101, getSavePath(), toDoItem.getUrl(), FileDownloadService.this, FileDownloadService.this);
//        download(toDoItem);
//    }
//
//    private void stopDownload() {
//        cMainHttp.cancelDownload(101);
////        cMainHttp.cancelAll();
//    }


}