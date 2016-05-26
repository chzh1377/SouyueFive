package com.zhongsou.souyue.im.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Slog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 表情下载器
 *
 * @author wangqiang
 */

public class DownloaderThread {
    private String downPath; // 下载路径
    private String savePath; // 保存路径
    private PackageBean packageBean; // 下载需要的id
    private Handler mHandler;
    private Dao dao;
    private Context mContext;

    public DownloaderThread(String downPath, String savePath, PackageBean pb,
                            Handler handler, Context context) {
        this.downPath = downPath;
        this.savePath = savePath;
        this.mHandler = handler;
        this.mContext = context;
        this.packageBean = pb;
        dao = new Dao(context);
    }

    public void download() {
        int state = ListState.state.get(packageBean.getPackageId());
        if (state == ListState.DOWNLOADING) {
            print("正在下载中.........");
            return;
        }
        ListState.state.put(packageBean.getPackageId(), ListState.DOWNLOADING);
        ThreadPoolUtil.getSingleInstance().push(new MyThread(mContext));

    }

    class MyThread extends Thread {
        Context context;
        int completeSize; // 以下载大小

        public MyThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            FileOutputStream fos = null;
            URL url = null;
            HttpURLConnection conn = null;
            InputStream is = null;
            int totalSize;
            Message message = null;
            try {
                File file = new File(savePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                url = new URL(downPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length;
                byte[] buf = new byte[2048];
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();
                    totalSize = conn.getContentLength();
                    LoadInfo info = new LoadInfo(totalSize, 0, downPath,
                            packageBean.getPackageId()); // 初始化下载纪录
                    MemoryPackageDao.saveInfo(info);

                    while ((length = is.read(buf)) != -1) {
                        fos.write(buf, 0, length);
                        completeSize += length;
                        MemoryPackageDao.updateInfo(packageBean.getPackageId(),
                                completeSize);
                        message = Message.obtain();
                        message.what = 1;
                        message.obj = packageBean;
                        message.arg1 = completeSize;
                        mHandler.sendMessage(message);// 给DownloaderService发送消息
                        if (ListState.state == null)
                            continue;
                        int state;
                        try {
                            state = ListState.state.get(packageBean
                                    .getPackageId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            state = ListState.STOP;
                        }
                        // 判断暂停
                        if (state == ListState.STOP) {
                            print(packageBean.getPackageId() + "文件以暂停下载");
                            MemoryPackageDao.stopThread(packageBean
                                    .getPackageId());
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                MemoryPackageDao.stopThread(packageBean
                        .getPackageId());
                message = Message.obtain();
                message.what = -1;
                mHandler.sendMessage(message);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void print(String msg) {
        Slog.d("Downloader", msg);
    }
}
