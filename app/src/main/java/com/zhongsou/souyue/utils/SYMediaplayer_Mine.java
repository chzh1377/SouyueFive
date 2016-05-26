package com.zhongsou.souyue.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.ui.SouYueToast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SYMediaplayer_Mine extends MediaPlayer implements OnPreparedListener {
    private Context mContext;
    private MediaPlayer mPlayer;
    private AnimationDrawable frameAnimation;
    private int loadDrawableId = 0;
    private int runDrawableId = 0;
    private String filePath = "";
    public static final int SOURCE_TYPE_LOC = 0;
    public static final int SOURCE_TYPE_NET = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (frameAnimation != null)
                frameAnimation.stop();
            switch (msg.what) {
                case 0:
                    prePlayView();
                    break;
                case 1:
                    playView();
                    break;
                default:
                    break;
            }
        }
    };

    private static SYMediaplayer_Mine Instance = null;

    private SYMediaplayer_Mine() {

    }

    private SYMediaplayer_Mine(Context context) {
        this.mContext = context;
        mPlayer = new SYMediaplayer_Mine();
    }

    public static SYMediaplayer_Mine getInstance(Context context) {
        if (Instance == null) {
            Instance = new SYMediaplayer_Mine(context);
        }
        return Instance;
    }

//    public void setLoadDrawableId(int loadDrawableId) {
//        this.loadDrawableId = loadDrawableId;
//    }
//
//    public void setRunDrawableId(int runDrawableId) {
//        this.runDrawableId = runDrawableId;
//    }

    private void prePlayView() {
        try {
            currentPlayBtn.setBackgroundResource(this.loadDrawableId <= 0 ? R.drawable.audio_loading : this.loadDrawableId);
            frameAnimation = (AnimationDrawable) currentPlayBtn.getBackground();
            frameAnimation.start();
        } catch (Exception ex) {
            Log.e(MainApplication.TAG, "prePlayView Exception + drawable id is error" + loadDrawableId);
        }
    }

    private void playView() {
        try {
            currentPlayBtn.setBackgroundResource(this.runDrawableId <= 0 ? R.drawable.audio_running_mine : this.runDrawableId);
            frameAnimation = (AnimationDrawable) currentPlayBtn.getBackground();
            frameAnimation.start();
        } catch (Exception ex) {
            Log.e(MainApplication.TAG, "playView Exception + drawable id is error" + runDrawableId);
        }
    }

    public static ImageView lastPlayBtn;
    public static ImageView currentPlayBtn;

//    public boolean mPlayerIsPlaying() {
//        return mPlayer.isPlaying();
//    }

    private void playOrStopAudio(ImageView imgBtn, int type) {
        if (!Utils.isSDCardExist()) {
            SouYueToast.makeText(mContext, R.string.check_sdcare, SouYueToast.LENGTH_SHORT).show();
            return;
        }

        stopPlayAudio();// 停止当前 并且播放
        playAudio(imgBtn, type);

    }

    private void playAudio(ImageView tag, int type) {
        try {

            if (type == SOURCE_TYPE_LOC) {
                File file = new File(genAudioFileName());
                if (file.length() <= 0)
                    return;
                FileInputStream fis = new FileInputStream(file);
                mPlayer.setDataSource(fis.getFD());
            } else {
                mPlayer.setDataSource(filePath);
            }

            // mPlayer.setDataSource(fis.getFD());//或者直接使用网络源
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(completionListener);
            currentPlayBtn = tag;
            handler.sendEmptyMessage(0);
        } catch (IllegalArgumentException e) {
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
    }

    public void play(ImageView imgBtn, int type, String filePath) {
        this.filePath = filePath;
        if (lastPlayBtn == null)
            lastPlayBtn = imgBtn;
        if (lastPlayBtn == imgBtn && (mPlayer.isPlaying() || (frameAnimation != null && frameAnimation.isRunning()))) {
            stopPlayAudio();
            return;
        } else {
            playOrStopAudio(imgBtn, type);
        }
        lastPlayBtn = imgBtn;
    }

    public void stopPlayAudio() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.stop();
        mPlayer.reset();
        if (frameAnimation != null)
            frameAnimation.stop();
        if (lastPlayBtn != null)
            lastPlayBtn.setBackgroundResource(R.drawable.bubble_voice3);
        if (currentPlayBtn!=null)
            currentPlayBtn.setBackgroundResource(R.drawable.bubble_voice3);
    }

    private String genAudioFileName() {
        if (Utils.isSDCardExist()) {
            StringBuffer filename = new StringBuffer();
            File fileDir = FileUtils.createDir(Environment.getExternalStorageDirectory() + "/souyue/file/");
            filename.append(fileDir.toString() + "/");
            filename.append("sytemp__");
            // filename.append(".amr");
            return filename.toString();
        }
        return null;
    }

    protected OnCompletionListener completionListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.reset();
            // mPlayer.reset();
            if (frameAnimation != null)
                frameAnimation.stop();
            if (lastPlayBtn != null)
                lastPlayBtn.setBackgroundResource(R.drawable.bubble_voice3);
            if (currentPlayBtn!=null)
                currentPlayBtn.setBackgroundResource(R.drawable.bubble_voice3);
        }

    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        handler.sendEmptyMessage(1);
        // mPlayer.start();
    }

}
