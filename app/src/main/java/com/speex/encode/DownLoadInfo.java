package com.speex.encode;

import android.os.Handler;
import android.view.View;

import java.io.File;

public class DownLoadInfo {
    private String url;
    private View view;
    public Handler handler;
    public AudioLoaderListener audioLoaderListener;
    public File cacheDir;
    public LoadLock lock;

    
    public DownLoadInfo(Handler handler, String url, View view,AudioLoaderListener audioLoaderListener, File cacheDir, LoadLock lock2) {
        this.url = url;
        this.view = view;
        this.audioLoaderListener = audioLoaderListener;
        this.cacheDir = cacheDir;
        this.handler = handler;   
        this.lock = lock2;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the view
     */
    public View getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(View view) {
        this.view = view;
    }

}
