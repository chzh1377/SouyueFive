package com.zhongsou.souyue.view.videos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.receiver.HomeListener;
import com.zhongsou.souyue.receiver.ScreenListener;
import com.zhongsou.souyue.ui.NetChangeDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.view.ZSVideoConst;



public class ZSFullScreenActivity extends BaseActivity {

    static void toActivityFromNormal(Context context, int state, String url, String title) {
        STATE = state;
        URL = url;
        TITLE = title;
        start = false;
        Intent intent = new Intent(context, ZSFullScreenActivity.class);
        context.startActivity(intent);
    }

    /**
     * <p>直接进入全屏播放</p>
     * <p>Full screen play video derictly</p>
     *
     * @param context context
     * @param url     video url
     * @param title   video title
     */
    public static void toActivity(Context context, String url, String title) {
        STATE = ZSVideoPlayer.CURRENT_STATE_NORMAL;
        URL = url;
        TITLE = title;
        start = true;
        Intent intent = new Intent(context, ZSFullScreenActivity.class);
        context.startActivity(intent);
    }

    ZSVideoPlayer videoPlayer;
    /**
     * 刚启动全屏时的播放状态
     */
    public static int STATE = -1;
    public static String URL;
    public static String TITLE;
    public static boolean manualQuit = false;
    static boolean start = false;
    PowerManager powerManager = null;
    PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_fullscreen);

        videoPlayer = (ZSVideoPlayer) findViewById(R.id.jcvideoplayer);
        videoPlayer.setUpForFullscreen(URL, TITLE);
        videoPlayer.setState(STATE);
        videoPlayer.changeUiToFullScreen();
        videoPlayer.setCallBack(new ZSVideoPlayer.ZSVideoPlayCallBack() {
            @Override
            public void dealClick() {
            }

            @Override
            public boolean expand() {
                return false;
            }
            @Override
            public void dealError() {
                Log.e("ZSVideoPlayer","deal  error  fullscreen");
                try{
                    videoPlayer.releasePlay();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }finally {
                    finish();
                }
            }
        });
        manualQuit = false;
        if (start) {
            videoPlayer.ivStart.performClick();
        } else {
            videoPlayer.isFullscreenFromNormal = true;
            videoPlayer.addSurfaceView();
            if (ZSMediaManager.intance(this).listener != null) {
                ZSMediaManager.intance(this).listener.onCompletion();
            }
            ZSMediaManager.intance(this).listener = videoPlayer;
        }
        // 不管横竖屏切换 直接播放
        videoPlayer.start();

        setUpdateReciever();
        setHomeListener();
        setScreenListener();
        powerManager = (PowerManager)this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }
    @Override
    protected void onUserLeaveHint() {
        stopVideo();
        videoPlayer.isClickFullscreen = false;
        try {
            videoPlayer.quitFullScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        videoPlayer.isClickFullscreen = false;
        try {
            videoPlayer.quitFullScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
        if (!manualQuit) {
            videoPlayer.isClickFullscreen = false;
            videoPlayer.releaseAllVideos(this);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelReciever();
        cancelHomeListener();
        cancelScreenListener();

    }

    VideoUpdateBroadCastRecever receiver;

    /**
     * 注册用于刷新 视频数据的广播
     */
    public void setUpdateReciever() {
        if (receiver == null) {
            IntentFilter inf = new IntentFilter();
            inf.addAction(ZSVideoConst.REFRESH_VIDEO);
            inf.addAction(ZSVideoConst.VIDEO_NET_ACTION);
            inf.setPriority(111120);
            receiver = new VideoUpdateBroadCastRecever();
            registerReceiver(receiver, inf);
        }

    }

    private void cancelReciever() {
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {

        }

    }

    public class VideoUpdateBroadCastRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ZSVideoConst.REFRESH_VIDEO)) {
                dealWithBroaCast(intent);
            }
            if (action.equals(ZSVideoConst.VIDEO_NET_ACTION)) {
                String status = intent.getStringExtra(ZSVideoConst.VIDEO_NET_STATUS);
                if (status.equalsIgnoreCase(ZSVideoConst.VIDEO_NET_STATUS_PHONE)) {
                    showNetChangeDialog();
                    abortBroadcast();
                } else if (status.equalsIgnoreCase(ZSVideoConst.VIDEO_NET_STATUS_NO)) {
                }

            }
        }
    }

    public void dealWithBroaCast(Intent intent) {
        String status = intent.getStringExtra(ZSVideoConst.VIDEO_STATUS);
        int palyPosition = intent.getIntExtra(ZSVideoConst.VIDEO_POSITION, 0);
        if (status.equals(ZSVideoConst.VIDEO_STATUS_STOP)) {
            finish();
        }
    }


    public void showNetChangeDialog() {
        if (mediaPlayIsPlaying()) {
            mediaPlayPause();
            final NetChangeDialog dialog = NetChangeDialog.getInstance(this, new NetChangeDialog.NetClickListener() {
                @Override
                public void continuePlay() {
                    mediaPlayResume();
                }

                @Override
                public void canclePlay() {
                    finish();
                }
            });
            if(!isFinishing())
                dialog.show();
        }
    }

    private HomeListener mHomeWatcher;

    public void setHomeListener() {
        if (mHomeWatcher == null) {
            mHomeWatcher = new HomeListener(mContext);
            mHomeWatcher.setOnHomePressedListener(new HomeListener.OnHomePressedListener() {
                @Override
                public void onHomePressed() {
                    stopVideo();
                }

                @Override
                public void onHomeLongPressed() {
                    stopVideo();
                }

                @Override
                public void onPowerOffPressed() {
                    stopVideo();
                }
            });
            mHomeWatcher.startWatch();
        }

    }

    public void cancelHomeListener() {
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
            mHomeWatcher = null;
        }
    }

    private ScreenListener screenListener;

    public void setScreenListener() {
        if (screenListener == null) {
            screenListener = new ScreenListener(mContext);
            screenListener.setScreenStateListener(new ScreenListener.ScreenStateListener() {
                @Override
                public void onScreenOn() {
                    stopVideo();
                }

                @Override
                public void onScreenOff() {
                    stopVideo();
                }

                @Override
                public void onUserPresent() {
                    stopVideo();
                }
            });
            screenListener.startWatch();
        }

    }

    private void stopVideo() {
        manualQuit =true;
        videoPlayer.releasePlay();
        ZSVideoConst.sendStopBroadcast(mContext);
        finish();
    }

    public void cancelScreenListener() {
        if (screenListener != null) {
            screenListener.stopWatch();
            screenListener = null;
        }
    }

    private void mediaPlayPause() {
        videoPlayer.pausePlay();
    }

    private void mediaPlayResume() {
        videoPlayer.start();
    }

    private void mediaPlayRelease() {
        videoPlayer.releasePlay();
    }

    private boolean mediaPlayIsPlaying() {
        return videoPlayer.isPlaying();
    }

    private void mediaPlaySeekTo(int seek) {
        ZSMediaManager.intance(this).mediaPlaySeekTo(seek);
    }

}
