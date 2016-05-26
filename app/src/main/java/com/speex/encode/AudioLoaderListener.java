package com.speex.encode;

import android.view.View;

public interface AudioLoaderListener {

    public void onLoading(long curSize, long totalSize, View view);
    public void onLoadingComplete(long totalSize);
    public void onLoadingFaild();
}
