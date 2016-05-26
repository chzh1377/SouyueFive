package com.zhongsou.souyue.view.videos;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * <p>统一管理MediaPlayer的地方,只有一个mediaPlayer实例，那么不会有多个视频同时播放，也节省资源。</p>
 * On 2015/11/30 15:39
 */
public class ZSMediaManager implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnVideoSizeChangedListener,IMediaPlayer.OnInfoListener {

    public IjkMediaPlayer mediaPlayer;
    private static ZSMediaManager mediaManager;
    public int currentVideoWidth = 0;
    public int currentVideoHeight = 0;
    public ZSMediaPlayerListener listener;
    public ZSMediaPlayerListener lastListener;
    public int lastState;
    private Context mContext;

    public static ZSMediaManager intance(Context context) {
        if (mediaManager == null) {
            mediaManager = new ZSMediaManager();
        }
        return mediaManager;
    }

    public ZSMediaManager() {
        mediaPlayer = new IjkMediaPlayer();
    }

    public void prepareToPlay(Context context, String url) {
        if (TextUtils.isEmpty(url)) return;
        try {
            mediaPlayer.release();
            mediaPlayer = new IjkMediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(context, Uri.parse(url));
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearWidthAndHeight() {
        currentVideoWidth = 0;
        currentVideoHeight = 0;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if (listener != null) {
            listener.onPrepared();
        }
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        if (listener != null) {
            listener.onCompletion();
        }
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {
        if (listener != null) {
            listener.onBufferingUpdate(percent);
        }
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {
        if (listener != null) {
            listener.onSeekComplete();
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        if (listener != null) {
            listener.onError(what, extra);
        }
        return true;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        currentVideoWidth = mp.getVideoWidth();
        currentVideoHeight = mp.getVideoHeight();
        if (listener != null) {
            listener.onVideoSizeChanged();
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        if (listener != null) {
            listener.onInfo(what,extra);
        }
        return false;
    }

    interface ZSMediaPlayerListener {
        void onPrepared();

        void onCompletion();

        void onBufferingUpdate(int percent);

        void onSeekComplete();

        void onError(int what, int extra);

        void onVideoSizeChanged();

        void onBackFullscreen();

        void onInfo(int what, int extra);
    }

    public void mediaPlayPause() {
        if (mediaPlayer != null) {
            try
            {
                mediaPlayer.stop();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public void mediaPlayResume() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.start();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    public void mediaPlayRelease() {
        if (mediaPlayer != null) {
            try
            {
                mediaPlayer.release();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean mediaPlayIsPlaying() {
        boolean flag =false;
        if (mediaPlayer != null) {
            flag =mediaPlayer.isPlaying();
        }
        return  flag;
    }

    public void mediaPlaySeekTo(int seek) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(seek);
        }
    }

    public long getCurrentPosition() {
        long posotion=0;
        if (mediaPlayer != null) {
            posotion =mediaPlayer.getCurrentPosition();
        }
        return posotion;
    }


}
