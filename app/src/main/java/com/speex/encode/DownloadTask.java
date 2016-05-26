package com.speex.encode;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class DownloadTask implements Runnable {

    private DownLoadInfo downInfo;
    private int haveDown;
    private Map<String, LoadLock> loadingTask;
    private LoadLock lock;

    public DownloadTask(DownLoadInfo info, Map<String, LoadLock> loadingTask2) {
        this.downInfo = info;
        this.loadingTask = loadingTask2;
        this.lock = info.lock;
    }

    @Override
    public void run() {
        
        synchronized (lock) {

            InputStream in = null;
            File cacheFile = new File(downInfo.cacheDir, FileNameGenerate.getName(downInfo.getUrl()));
            FileOutputStream out = null;
            int fileLength = 0;
            try {
                URL localURL = new URL(this.downInfo.getUrl());
                HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURL.openConnection();
                localHttpURLConnection.setRequestMethod("GET");
                localHttpURLConnection.setConnectTimeout(5000);
                localHttpURLConnection.setDoInput(true);
                localHttpURLConnection.setReadTimeout(10000);
                localHttpURLConnection.connect();
                in = localHttpURLConnection.getInputStream();
                out = new FileOutputStream(cacheFile);
                fileLength = localHttpURLConnection.getContentLength();
                byte[] buffer = new byte[1024 * 4];
                int len = 0;
                haveDown = 0;
                startLoading(fileLength, 0);
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    haveDown += len;
                    startLoading(fileLength, haveDown);
                }
                final int total = fileLength;
                downInfo.handler.post(new Runnable() {

                    @Override
                    public void run() {
                        downInfo.audioLoaderListener.onLoadingComplete(total);
                    }
                });
                return;
            } catch (Exception e) {
                e.printStackTrace();
                if (haveDown == 0 || haveDown != fileLength) {
                    cacheFile.delete();
                }
            } finally {

                close(in);
                close(out);
                if(lock.refrence>=0){
                    loadingTask.remove(downInfo.getUrl());
                }
            }
            downInfo.handler.post(new Runnable() {

                @Override
                public void run() {
                    downInfo.audioLoaderListener.onLoadingFaild();
                }
            });

        }
    }


    private void startLoading(final int fileLength, final int i) {
        downInfo.handler.post(new Runnable() {

            @Override
            public void run() {
                downInfo.audioLoaderListener.onLoading(i, fileLength, downInfo.getView());
            }
        });
    }

    private void close(Closeable in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
