package com.zhongsou.souyue.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
//import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.fragment.DownloadContentPagerFragmentV2;
import com.zhongsou.souyue.circle.util.StringUtils;
import com.zhongsou.souyue.service.download.Md5Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tiansj
 *
 * 1. 下载文件思想，UI只操作队列，线程负责消费队列，UI和线程之间解耦
 * 2. 队列、线程、Service，保持一致的生命周期，队列为空，Service自动销毁
 * 3. 线程只消费队列中”等待“状态的信息，对”暂停“和”失败“状态，自动移出跳过，每次只消费队首
 * 4. 线程下载中更新进度条和DB
 * 5. 线程下载“失败”或“完成”，通知UI更新
 */
public class DownloadFileServiceV2 extends Service {

    private static final String ACTION_PREFIX = "DownloadFileService";
    private static final String ACTION_PAUSE_ITEM = ACTION_PREFIX + ".PAUSE_ITEM";
    private static final String ACTION_PAUSE_ITEMS = ACTION_PREFIX + ".PAUSE_ITEMS";
    private static final String ACTION_ADD_ITEM = ACTION_PREFIX + ".ADD_ITEM";
    private static final String ACTION_ADD_ITEMS = ACTION_PREFIX + ".ADD_ITEMS";
    private static final String ACTION_STOP_THREAD = ACTION_PREFIX + ".STOP_THREAD";

    // 文件类型，与DownloadInfo中type保持一直
    private int fileType;
    // 下载线程
    private DownloadThread downloadThread;
	// 所有待下载url队列
    private volatile LinkedList<DownloadInfo> fileQueue = new LinkedList<DownloadInfo>();
    private List<String> vecStr;
    private static volatile boolean stopThread;
    
	@Override
	public void onCreate() {
		super.onCreate();
        vecStr = new ArrayList<String>();
        stopThread = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        int command = super.onStartCommand(intent, flags, startId);
        if(intent == null) {
            return command;
        }
        String action = intent.getAction();
        if(StringUtils.isEmpty(action)) {
            return command;
        }

        if (action.equals(ACTION_ADD_ITEM)) {
            DownloadInfo item = (DownloadInfo)intent.getSerializableExtra("DownloadInfo");
            addItemToQueue(item);
        } else if (action.equals(ACTION_ADD_ITEMS)) {
            ArrayList<DownloadInfo> items = (ArrayList<DownloadInfo>) intent.getSerializableExtra("DownloadInfos");
            addItemsToQueue(items);
        } else if (action.equals(ACTION_PAUSE_ITEM)) {
            DownloadInfo item = (DownloadInfo)intent.getSerializableExtra("DownloadInfo");
            pauseQueueItem(item.getOnlyId());
        } else if (action.equals(ACTION_PAUSE_ITEMS)) {
            ArrayList<DownloadInfo> items = (ArrayList<DownloadInfo>) intent.getSerializableExtra("DownloadInfos");
            pauseQueueItems(items);
        } else if (action.equals(ACTION_STOP_THREAD)) {
            stopThread();
        }

        return command;
	}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    // 下载线程
    public class DownloadThread extends Thread {
        @Override
        public void run() {
            while (!fileQueue.isEmpty() && !stopThread) {
                DownloadInfo item = fileQueue.getFirst();

                // 暂停状态的都remove掉
                if(item.getState() == DownloadInfo.STATE_PAUSE) {
                    fileQueue.removeFirst();
                    continue;
                }

                switch (item.getType()) {
                    case DownloadInfo.DOWNLOAD_TYPE_BOOK:
                        parseBookUrl(item);
                        break;
                    case DownloadInfo.DOWNLOAD_TYPE_VIDEO:
                        parseVideoUrl(item);
                        break;
                    default: continue;
                }


                if(item.getUrlList() != null && item.getUrlList().size() > 0) {
                    item.setState(DownloadInfo.STATE_LOADING);

                    // 更新状态
                    DownloadDao.getInstance(getApplicationContext()).updataState(item.getOnlyId(), DownloadInfo.STATE_LOADING);
                    sendBroadcastUpdateState(item);

                    // 执行下载
                    for(UrlConsume uc : item.getUrlList()) {
                        if(item.getState() == DownloadInfo.STATE_FAILED || item.getState() == DownloadInfo.STATE_PAUSE) {
                            break;
                        }
                        download(uc);
                    }

                    // 失败、暂停、成功，都从队列移出
                    if(fileQueue != null) {
                        removeById(item.getOnlyId());
                    }

                    // 下载中的文件状态只能变成，失败、暂停、成功
                    if(item.getCurLength() >= item.getLength()) {
                        item.setState(DownloadInfo.STATE_COMPLETE);

                        // 更新数据库状态
                        DownloadDao.getInstance(getApplicationContext()).updataState(item.getOnlyId(), DownloadInfo.STATE_COMPLETE);

                        // 广播通知已缓存列表
                        Intent data = new Intent(DownloadContentPagerFragmentV2.BROADCAST_REFRESH_LIST);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(data);

                        if(item.getType() == DownloadInfo.DOWNLOAD_TYPE_VIDEO) {
//                            combineFile(item);
                        }
                    } 
                    sendBroadcastUpdateState(item);
                } else {
                    fileQueue.removeFirst();
                }

            }
            stopSelf();
        }
    }

    private void download(UrlConsume uc) {
        DownloadInfo item = getItem(uc.getOnlyId());
        if(item == null) {
            return;
        }
        HttpURLConnection connection = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            boolean isNeedAddLen = true;
            URL localURL = new URL(uc.getUrl());
            connection = (HttpURLConnection) localURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            if(item.getType() == DownloadInfo.DOWNLOAD_TYPE_BOOK){
              connection.setRequestProperty("Range:", "bytes="+ uc.getCurLength() + "-");
                if(uc.getFilePath().contains("_index.txt")) {
                    isNeedAddLen = false;
                }
            }else{
              connection.setRequestProperty("Range", "bytes="+ uc.getCurLength() + "-");
            }

            connection.connect();
            in = connection.getInputStream();
            out = new FileOutputStream(createFile(item, uc.getFilePath()),true);
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                if(item.getState() == DownloadInfo.STATE_PAUSE || stopThread) {
                    break;
                }
                out.write(buffer, 0, len);

                uc.setCurLength(uc.getCurLength() + len);
                if(isNeedAddLen) {
                    item.setCurLength(item.getCurLength() + len);
                }

                // 更新DB
                DownloadDao.getInstance(getApplicationContext()).updataInfo(item.getOnlyId(), uc.getCurLength(), uc.getUrl(), item.getCurLength() + len);

                // 更新进度条进行
                sendBroadcastUpdateProgress(item);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            downFailedException(item);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            downFailedException(item);
        } catch (ProtocolException e) {
            e.printStackTrace();
            downFailedException(item);
        } catch (IOException e) {
            e.printStackTrace();
            downFailedException(item);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

	private void downFailedException(DownloadInfo item) {
		pauseQueueItem(item.getOnlyId());
		if(!fileQueue.isEmpty()) {
		    fileQueue.removeFirst();
		}
		item.setState(DownloadInfo.STATE_FAILED);
	}


    // *********************外部操作队列方法开始***********************
    public static void addItemToQueue(Context ctx, DownloadInfo item) {
        Intent i = new Intent(ctx, DownloadFileServiceV2.class);
        i.setAction(ACTION_ADD_ITEM);
        i.putExtra("DownloadInfo", item);
        ctx.startService(i);
    }

    public static void addItemsToQueue(Context ctx, ArrayList<DownloadInfo> items) {
        Intent i = new Intent(ctx, DownloadFileServiceV2.class);
        i.setAction(ACTION_ADD_ITEMS);
        i.putExtra("DownloadInfos", items);
        ctx.startService(i);
    }

    public static void pauseQueueItem(Context ctx, DownloadInfo item) {
        Intent i = new Intent(ctx, DownloadFileServiceV2.class);
        i.setAction(ACTION_PAUSE_ITEM);
        i.putExtra("DownloadInfo", item);
        ctx.startService(i);
    }

    public static void pauseQueueItems(Context ctx, ArrayList<DownloadInfo> items) {
        Intent i = new Intent(ctx, DownloadFileServiceV2.class);
        i.setAction(ACTION_PAUSE_ITEMS);
        i.putExtra("DownloadInfos", items);
        ctx.startService(i);
    }
    
    public static void stopThread(Context ctx) {
        Intent i = new Intent(ctx, DownloadFileServiceV2.class);
        i.setAction(ACTION_STOP_THREAD);
        ctx.startService(i);
    }
    // *********************外部操作队列方法结束***********************


    // *********************内部操作队列方法开始***********************
    // 添加到队列
    private void addItemsToQueue(List<DownloadInfo> items) {
        for(DownloadInfo item : items) {
            // fileQueue.addLast(item);
        	boolean isInQueue = false;
            String onlyId = item.getOnlyId();
            for(DownloadInfo qItem : fileQueue) {
            	if(qItem.getOnlyId().equals(onlyId)) {
            		if(qItem.getState() == DownloadInfo.STATE_PAUSE || qItem.getState() == DownloadInfo.STATE_FAILED) {
                        item.setState(DownloadInfo.STATE_INIT);
                    }
            		isInQueue = true;
            	}
            }
            if(!isInQueue) {
            	fileQueue.addLast(item);
            }
        }
        startDownloadThread();
    }

    // 添加到队列
    private void addItemToQueue(DownloadInfo item) {
        if(item == null) {
            return;
        }
        fileQueue.addLast(item);
        startDownloadThread();
    }

    // 暂停文件下载
    private void pauseQueueItem(String onlyId) {
        for(DownloadInfo item : fileQueue) {
            if(item.getOnlyId().equals(onlyId)) {
                item.setState(DownloadInfo.STATE_PAUSE);
                break;
            }
        }
    }

    // 暂停多个文件，由于小说和视频在一个队列，全部暂停和开始，有调用者控制
    private void pauseQueueItems(List<DownloadInfo> items) {
        for(DownloadInfo file : items) {
            pauseQueueItem(file.getOnlyId());
        }
    }

    private void removeById(String onlyId) {
        for(DownloadInfo item : fileQueue) {
            if(onlyId.equals(item.getOnlyId())) {
                fileQueue.remove(item);
                break;
            }
        }
    }

    private DownloadInfo getItem(String onlyId) {
        for(DownloadInfo item : fileQueue) {
            if(item.getOnlyId().equals(onlyId)) {
                return item;
            }
        }
        return null;
    }
    
    public void stopThread() {
        stopThread = true;
        if ( downloadThread != null ) {
        	downloadThread.interrupt();
        }
    }
    
    // *********************内部操作队列方法开始***********************


    // *********************工具方法开始******************************
    //video保存目录
    public static String getVideoSDPath(){
        String strVideo = getSDCardPath() + "/souyuedownload/video/";
        return strVideo;
    }

    //book保存目录
    public static String getBookPaht(){
        String strBook = getSDCardPath() + "/souyuedownload/book/";
        return strBook;
    }

    private File createFile(DownloadInfo item, String filePath) {
        File path = null;
        if(item.getType() == DownloadInfo.DOWNLOAD_TYPE_VIDEO) {
            path = new File(getVideoSDPath());
        } else if(item.getType() == DownloadInfo.DOWNLOAD_TYPE_BOOK) {
            path = new File(getBookPaht());
        }

        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    /**
     * 获取SDCard的目录路径功能
     * @return
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

    public void startDownloadThread() {
    	stopThread = false;
        if(downloadThread != null && downloadThread.isAlive()) {
            return;
        }
        if(downloadThread == null) {
            downloadThread = new DownloadThread();
        }
        try {
        	 downloadThread.start();
		} catch (Exception e) {
			
		}
       
    }

    public static String getBookIndexPath(String onlyId) {
        return getBookPaht() + onlyId + "_index.txt";
    }

    public static String getBookContentPath(String onlyId) {
        return getBookPaht() + onlyId + "_content.txt";
    }

    public static String getVideoPath(String onlyId) {
        return getVideoSDPath() + onlyId + ".mp4";
    }

    public static String getVideoUrlPath(String onlyId, String url) {
        return getVideoSDPath() + onlyId + "_" + Md5Util.getMD5Str(url) + ".mp4";
    }

//    private void combineFile(final DownloadInfo item) {
//        // 合并文件，删除子文件，根据文件命名规则删除
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                vecStr.clear();
//                List<UrlConsume> urlList = item.getUrlList();
//                if(urlList.isEmpty()) {
//                    return;
//                }
//                if(urlList.size() == 1) {
//                    String beforeName = DownloadFileServiceV2.getVideoUrlPath(item.getOnlyId(), urlList.get(0).getUrl());
//                    String afterName = DownloadFileServiceV2.getVideoPath(item.getOnlyId());
//                    renameFile(beforeName, afterName);
//                    return;
//                }
//                for(UrlConsume url : urlList) {
//                    vecStr.add(DownloadFileServiceV2.getVideoUrlPath(item.getOnlyId(), url.getUrl()));
//                }
//                try {
//                    appendRadio(item.getOnlyId());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                //删除子文件
//                for (int i = 0; i < vecStr.size(); i++) {
//                    delFile(vecStr.get(i));
//                }
//            }
//        });
//        thread.start();
//    }

    private void renameFile(String fromFile, String toFile){
        File from = new File(fromFile);
        File to = new File(toFile);
        from.renameTo(to);
    }

//    private void appendRadio(String name) throws Exception {
//        Vector<Movie> inMovies = new Vector<Movie>();
//        for (int i = 0; i < vecStr.size(); i++) {
//            inMovies.add(MovieCreator.build(vecStr.get(i)));
//        }
//        List<Track> videoTracks = new LinkedList<Track>();
//        List<Track> audioTracks = new LinkedList<Track>();
//
//        for (Movie m : inMovies) {
//            for (Track t : m.getTracks()) {
//                if (t.getHandler().equals("soun")) {
//                    audioTracks.add(t);
//                }
//                if (t.getHandler().equals("vide")) {
//                    videoTracks.add(t);
//                }
//            }
//        }
//
//        Movie result = new Movie();
//
//        if (audioTracks.size() > 0) {
//            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
//        }
//        if (videoTracks.size() > 0) {
//            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
//        }
//
//        Container out = new DefaultMp4Builder().build(result);
//
//        FileChannel fc = new RandomAccessFile(DownloadFileServiceV2.getVideoPath(name), "rw").getChannel();
//        out.writeContainer(fc);
//        fc.close();
//
//    }

    private void sendBroadcastUpdateProgress(DownloadInfo item) {
        Intent intent = new Intent();
        intent.setAction(DownloadContentPagerFragmentV2.BROADCAST_DOWNLOAD_FILE);
        intent.putExtra("what", 1);
        intent.putExtra("obj", item);
        sendBroadcast(intent);
    }

    private void sendBroadcastUpdateState (DownloadInfo item) {
        Intent intent = new Intent();
        intent.setAction(DownloadContentPagerFragmentV2.BROADCAST_DOWNLOAD_FILE);
        intent.putExtra("what", 2);
        intent.putExtra("obj", item);
        sendBroadcast(intent);
    }

    public static void delFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    private void parseBookUrl(DownloadInfo item) {
        List<UrlConsume> urlList = new LinkedList<UrlConsume>();
        Book book = null;
        try {
//            book = JSON.parseObject(item.getUrls(), Book.class);
            book = new Gson().fromJson(item.getUrls(),Book.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(book == null) {
            return;
        }
        UrlConsume uc = book.getIndex();
        uc.setOnlyId(item.getOnlyId());
        String filePath = getBookPaht() + uc.getOnlyId() + "_index" + ".txt";
        uc.setFilePath(filePath);
        uc.setCurLength(0);

        UrlConsume ucContent = book.getContent();
        ucContent.setOnlyId(item.getOnlyId());
        String filePathContent = getBookPaht() + item.getOnlyId() + "_content" + ".txt";
        // 初始化队列里面的url为等待状态供线程消费
        ucContent.setFilePath(filePathContent);

        File fileContent = new File(filePathContent);
        if(fileContent.exists()) {
            item.setCurLength((int)fileContent.length());
            ucContent.setCurLength(fileContent.length());
        }
        if(!fileContent.exists()) {
            urlList.add(uc);
            item.setCurLength(0);
        }
        if(!fileContent.exists() || fileContent.length() < ucContent.getLength()) {
            urlList.add(ucContent);
        }
        item.setUrlList(urlList);
        if(urlList.size() > 0) {
            // 更新进度
            DownloadDao.getInstance(getApplicationContext()).updataInfo(item.getOnlyId(), urlList.get(0).getCurLength(), urlList.get(0).getUrl(), item.getCurLength());
            sendBroadcastUpdateProgress(item);
        }
    }

    private void parseVideoUrl(DownloadInfo item) {
        String urlsStr = item.getUrls();
        List<UrlConsume> urls = null;
        try {
//            urls = JSON.parseArray(urlsStr, UrlConsume.class);
            urls = new Gson().fromJson(urlsStr,new TypeToken<List<UrlConsume>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        item.setCurLength(0);
        for(UrlConsume uc : urls) {
            uc.setOnlyId(item.getOnlyId());
            String filePath = getVideoUrlPath(uc.getOnlyId(), uc.getUrl());
            File file = new File(filePath);
            if(file.exists()) {
            	item.setCurLength(item.getCurLength() + (int)file.length());
            }
            
            if(!file.exists() || file.length() < uc.getLength()) {
                // 初始化队列里面的url为等待状态供线程消费
                uc.setFilePath(filePath);
                uc.setCurLength(file.length());
            }
        }
        item.setUrlList(urls);
        if(urls != null && urls.size() > 0) {
            // 更新进度
            DownloadDao.getInstance(getApplicationContext()).updataInfo(item.getOnlyId(), urls.get(0).getCurLength(), urls.get(0).getUrl(), item.getCurLength());
            sendBroadcastUpdateProgress(item);
        }
    }
    // *********************工具方法结束******************************

}
