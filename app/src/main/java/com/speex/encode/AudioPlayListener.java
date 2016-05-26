package com.speex.encode;

import android.view.View;

public interface AudioPlayListener {

    public void onDisplayingPause(long curTime, long totalTime, View view);
    public void onDisplayingStart(long curTime, long totalTime, View view);
    public void onDisplayingEnd(long curTime, long totalTime, View view);
    public void onDisplayPreparing(View view);
}
