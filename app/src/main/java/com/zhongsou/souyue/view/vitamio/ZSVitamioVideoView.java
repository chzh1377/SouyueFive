//package com.zhongsou.souyue.view.vitamio;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.FrameLayout;
//import android.widget.RelativeLayout;
//
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.im.util.DensityUtil;
//import com.zhongsou.souyue.utils.NetWorkUtils;
//import com.zhongsou.souyue.view.ZSVideoConst;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.TextureVideoView;
//import io.vov.vitamio.widget.VideoInstanceView;
//import io.vov.vitamio.widget.VideoView;
//
///**
// * @description:
// * @auther: qubian
// * @data: 2016/4/21.
// */
//public class ZSVitamioVideoView extends RelativeLayout {
//    private static final String TAG = "ZSVitamioVideoView";
//    private final int TIME_SHOW_CONTROLLER = 8000;
//    private final int TIME_UPDATE_PLAY_TIME = 1000;
//
//    private final int MSG_HIDE_CONTROLLER = 10;
//    private final int MSG_UPDATE_PLAY_TIME = 11;
//    private ZSVitamioMediaController.PageType mCurrPageType = ZSVitamioMediaController.PageType.SHRINK;// 当前是横屏还是竖屏
//
//    private Context mContext;
//    private ZSVitamioMediaController mMediaController;
//    private Timer mUpdateTimer;
//    private TimerTask mTimerTask;
//    private VideoPlayCallbackImpl mVideoPlayCallback;
//
//    private View mProgressBarView;
//    private VideoInstanceView mPlayer;
//    private String videourl;
//    private int progressSec = 0;
//    private ViewGroup.LayoutParams videoViewLayout;
//    private long seekTime = 0;
//    private ViewGroup containerView;
//    /**
//     * 处理时间 进度条，和 controller的隐藏和显示
//     */
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == MSG_UPDATE_PLAY_TIME) {
//                updatePlayTime();
//                updatePlayProgress();
//            } else if (msg.what == MSG_HIDE_CONTROLLER) {
//                showOrHideController();
//            }
//        }
//    };
//
//
//
//    /**
//     * view 上的 touch  事件 主要是处理 controller 的显示与隐藏
//     */
//    private OnTouchListener mOnTouchVideoListener = new OnTouchListener() {
//        @SuppressLint("ClickableViewAccessibility")
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                showOrHideController();
//            }
//            return mCurrPageType == ZSVitamioMediaController.PageType.EXPAND ? true
//                    : false;
//        }
//    };
//
//    /**
//     * 对 controller 事件的回调
//     */
//    private ZSVitamioMediaController.MediaControlImpl mMediaControl = new ZSVitamioMediaController.MediaControlImpl() {
//        @Override
//        public void alwaysShowController() {
//            ZSVitamioVideoView.this.alwaysShowController();
//        }
//
//        @Override
//        public void onPlayTurn() {
//            if (mPlayer != null) {
//                if (mPlayer.isPlaying()) {
//                    pausePlay();
//                } else {
//                    startPlay();
//                }
//            }
//
//        }
//
//        @Override
//        public void onPageTurn() {
//            if (mVideoPlayCallback != null) {
//                mVideoPlayCallback.onSwitchPageType();
//            }
//        }
//
//        @Override
//        public void onProgressTurn(ZSVitamioMediaController.ProgressState state,
//                                   int progress) {
//            if (state.equals(ZSVitamioMediaController.ProgressState.START)) {
//                mHandler.removeMessages(MSG_HIDE_CONTROLLER);
//            } else if (state.equals(ZSVitamioMediaController.ProgressState.STOP)) {
//                resetHideTimer();
//            } else {
//                if (mPlayer != null) {
//                    long duration = 0;
//                    try {
//                        duration = mPlayer.getDuration();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    long time = progress * duration / 100;
//                    mPlayer.seekTo(time);
//                    updatePlayTime();
//                }
//            }
//        }
//    };
//    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
//
//        @Override
//        public boolean onInfo(MediaPlayer mp, int what, int extra) {
//            Log.i(TAG, "OnInfoListener:" + mp.isPlaying() + ",what " + what
//                    + ",extra " + extra);
//            switch (what) {
////                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
////                    if (mProgressBarView.getVisibility() == View.VISIBLE) {
////                        mProgressBarView.setVisibility(View.GONE);
////                        mCloseBtnView.setVisibility(VISIBLE);
////                    }
////                    return true;
////                case MediaPlayer.MEDIA_INFO_FILE_OPEN_OK:
////                    Log.d("XXX", ">>>>>>>>>>>>>>>>>>>>>>>> onInfo: MEDIA_INFO_FILE_OPEN_OK");
////                    if (mPlayer != null) {
////                        long buffersize = mPlayer.audioTrackInit();
////                        mPlayer.audioInitedOk(buffersize);
////                    }
////                    return true;
//                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    if (mProgressBarView.getVisibility() == View.GONE) {
//                        mProgressBarView.setBackgroundResource(android.R.color.transparent);
//                        mProgressBarView.setVisibility(View.VISIBLE);
//                    }
//                    progressSec =0;
//                    break;
//                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    if (mProgressBarView.getVisibility() == View.VISIBLE) {
//                        mProgressBarView.setVisibility(View.GONE);
//                    }
//                    progressSec =100;
//                    break;
//                case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//                    //显示 下载速度
//                    break;
//                case MediaPlayer.MEDIA_INFO_GET_CODEC_INFO_ERROR:
//                    break;
//            }
//            return true;
//        }
//    };
//    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
//        @Override
//        public void onPrepared(MediaPlayer mediaPlayer) {
//            Log.i(TAG, "OnPreparedListener  mp-" + mediaPlayer.isPlaying() + " ,seekTime:" + seekTime);
//            mPlayer.setVisibility(VISIBLE);
//            mPlayer.start();
//            if (seekTime != 0) {
//                mPlayer.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        seekTo(seekTime);
//                    }
//                },500);
//            }
//            mediaPlayer.setPlaybackSpeed(1.0f);
//            resetHideTimer();
//            resetUpdateTimer();
//        }
//    };
//
//
//    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
//        @Override
//        public void onCompletion(MediaPlayer mediaPlayer) {
//            stopUpdateTimer();
//            stopHideTimer();
//            long duration = 0;
//            try {
//                duration = mPlayer.getDuration();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mMediaController.playFinish(duration);
//            if (mVideoPlayCallback != null) {
//                mVideoPlayCallback.onPlayFinish();
//            }
//
//            Log.e(TAG, "mOnCompletionListener video OnCompletion");
//        }
//    };
//    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
//
//        @Override
//        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            Log.i(TAG,"onBufferingUpdate"+percent);
//            progressSec = percent;
//            if(percent ==0&&!NetWorkUtils.isNetworkAvailable()&&mVideoPlayCallback != null)
//            {
//                mVideoPlayCallback.onErrorCallBack();
//            }
//            if(percent>0&&percent <= 6)
//            {
//                mPlayer.pause();
//            }
//            if(percent>12)
//            {
//                mPlayer.start();
//            }
//        }
//    };
//    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
//
//        @Override
//        public boolean onError(MediaPlayer mp, int what, int extra) {
//            if (getWindowToken() != null) {
//                Resources r = getContext().getResources();
//                int messageId;
//                if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
//                    messageId = R.string.video_erro;
//                } else {
//                    messageId = R.string.video_erro;
//                }
//                Log.e(TAG, "mErrorListener" + r.getString(messageId));
//                if (mVideoPlayCallback != null) {
//                    mVideoPlayCallback.onErrorCallBack();
//                }
//            }
//            return true;
//        }
//    };
//
//    public void setScreenLandscape() {
//        if (mPlayer != null) {
//            mPlayer.setmMatrix();
//            ViewGroup.LayoutParams lp =mPlayer.getLayoutParams();
//           lp.width =ViewGroup.LayoutParams.MATCH_PARENT;
//            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            mPlayer.setLayoutParams(lp);
////            mPlayer.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
////            mPlayer.getHolder().setSizeFromLayout();
////            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
////            int width = wm.getDefaultDisplay().getWidth();
////            int height = wm.getDefaultDisplay().getHeight();
////            mPlayer.getHolder().setFixedSize(width,height);
//            mPlayer.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//
//        }
//    }
//
//    public void setScreenPortrait() {
//        if (mPlayer != null) {
//            mPlayer.setMatrixNull();
//            ViewGroup.LayoutParams lp =mPlayer.getLayoutParams();
//            lp.width =ViewGroup.LayoutParams.MATCH_PARENT;
//            lp.height = DensityUtil.dip2pxX(ZSVideoConst.VIDEO_HEIGHT);
//           mPlayer.setLayoutParams(lp);
////            mPlayer.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
////            mPlayer.getHolder().setSizeFromLayout();
////            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
////            int width = wm.getDefaultDisplay().getWidth();
////            mPlayer.getHolder().setFixedSize(width, DensityUtil.dip2pxX(ZSVideoConst.VIDEO_HEIGHT));
//            mPlayer.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//
//        }
//    }
//
//    public void setPageType(ZSVitamioMediaController.PageType pageType) {
//        mMediaController.setPageType(pageType);
//        mCurrPageType = pageType;
//    }
//
//    public void setVideoPlayCallback(VideoPlayCallbackImpl videoPlayCallback) {
//        mVideoPlayCallback = videoPlayCallback;
//    }
//
//    public void pausePlay() {
//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.pause();
//        }
//        mMediaController.setPlayState(ZSVitamioMediaController.PlayState.PAUSE);
//
////        stopHideTimer();
//    }
//
//    public void stopPlay() {
//        pausePlay();
//        stopUpdateTimer();
//        mMediaController.setPlayState(ZSVitamioMediaController.PlayState.STOP);
//    }
//
//    public void resume() {
//        startPlayVideo();
//    }
//
//    public void close() {
//        mMediaController.setPlayState(ZSVitamioMediaController.PlayState.PAUSE);
//        stopHideTimer();
//        stopUpdateTimer();
//        if (mPlayer != null) {
//            mPlayer.stopPlayback();
////            mPlayer = null;
//        }
//    }
//
//    public ZSVitamioVideoView(Context context) {
//        super(context);
//        initView(context);
//    }
//
//    public ZSVitamioVideoView(Context context, AttributeSet attrs,
//                              int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView(context);
//    }
//
//    public ZSVitamioVideoView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initView(context);
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initView(Context context) {
//        mContext = context;
//        View.inflate(context, R.layout.super_vitamio_view, this);
//        containerView = (ViewGroup) findViewById(R.id.video_inner_container);
//        mPlayer = (VideoInstanceView) findViewById(R.id.video_view);
//        mMediaController = (ZSVitamioMediaController) findViewById(R.id.videoController);
//        mProgressBarView = findViewById(R.id.progressbar);
//
//        mMediaController.setMediaControl(mMediaControl);
//        mPlayer.setOnTouchListener(mOnTouchVideoListener);
//
//        mProgressBarView.setVisibility(VISIBLE);
//
//        requestFocus();
//
//        this.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 拦截
//                return true;
//            }
//        });
//        mMediaController.setVisibility(GONE);
//        videoViewLayout = mPlayer.getLayoutParams();
//
//    }
//
//    private void restartView() {
//        mMediaController.setMediaControl(mMediaControl);
//        mPlayer.setOnTouchListener(mOnTouchVideoListener);
//        mProgressBarView.setVisibility(VISIBLE);
//        requestFocus();
//        this.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 拦截
//                return true;
//            }
//        });
//        mMediaController.setVisibility(GONE);
//        videoViewLayout = mPlayer.getLayoutParams();
//    }
//
//    /***
//     * 加载并开始播放视频
//     *
//     * @param videoUrl
//     */
//    public void loadAndPlay(MediaPlayer player, String videoUrl, int seekTime,
//                            boolean isfull) {
//
//        pauseOtherMusic();
//        videourl = videoUrl;
//        this.seekTime = seekTime;
//        mProgressBarView.setVisibility(VISIBLE);
//        if (mPlayer != null) {
//            mPlayer.setVisibility(VISIBLE);
//        } else {
//            Log.e(TAG," VideoView is null , new a VideoView Obj");
////            mPlayer = new TextureVideoView(mContext);
////            mPlayer.setLayoutParams(videoViewLayout);
////            mPlayer.setOnTouchListener(mOnTouchVideoListener);
////            containerView.addView(mPlayer);
//        }
//        if (seekTime == 0) {
//            mProgressBarView.setBackgroundResource(android.R.color.black);
//        } else {
//            mProgressBarView.setBackgroundResource(android.R.color.transparent);
//        }
//        if (TextUtils.isEmpty(videoUrl)) {
//            Log.e("TAG", "videoUrl should not be null");
//            return;
//        }
////        mPlayer = player;
//        if (isfull) {
////            startPlayVideo();
//            play(videoUrl);
//            mProgressBarView.setVisibility(View.GONE);
//        } else {
//            play(videoUrl);
//        }
//
////        startPlayVideo(seekTime);
//        requestLayout();
//        invalidate();
//    }
//
//    private void pauseOtherMusic() {
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        mContext.sendBroadcast(i);
//    }
//
//    private void play(String url) {
//
//        if (TextUtils.isEmpty(url)) {
//            return;
//        }
//        mMediaController.setPlayState(ZSVitamioMediaController.PlayState.PLAY);
////        pausePlay();
//        if (mPlayer != null) {
////        mPlayer.reset();
//            try {
//                mPlayer.setOnCompletionListener(mOnCompletionListener);
//                mPlayer.setOnPreparedListener(mOnPreparedListener);
//                mPlayer.setOnInfoListener(mInfoListener);
//                mPlayer.setOnErrorListener(mErrorListener);
//                mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
//                mPlayer.setBufferSize(5*1024);
////            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////            mPlayer.setVideoChroma(MediaPlayer.VIDEOCHROMA_RGBA);
////            mPlayer.setScreenOnWhilePlaying(true);
//                mPlayer.setVideoURI(Uri.parse(url));
//                mPlayer.setVisibility(VISIBLE);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    public boolean isPlaying() {
//        boolean isPlay = false;
//        try {
//            if (mPlayer != null) {
//                isPlay = mPlayer.isPlaying();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return isPlay;
//    }
//
//    public void seekTo(long index) {
//        try {
//            if (mPlayer != null) {
//                mPlayer.seekTo(index);
//                updatePlayTime();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void release() {
//        if (mPlayer != null) {
//            mPlayer.stopPlayback();
////            mPlayer.setVisibility(INVISIBLE);
////            removeView(mPlayer);
////            mPlayer = null;
//        }
//    }
//
//    /**
//     * 播放视频 should called after loadVideo()
//     */
//    private void startPlayVideo(int seekTime) {
//        if (seekTime > 0) {
//            mPlayer.seekTo(seekTime);
//        }
//        mMediaController.setPlayState(ZSVitamioMediaController.PlayState.PLAY);
//        requestLayout();
//        invalidate();
//    }
//
//    public void startPlay() {
//        if (mPlayer != null) {
//            try {
//                mPlayer.start();
//                resetHideTimer();
//                resetUpdateTimer();
//                mMediaController.setPlayState(ZSVitamioMediaController.PlayState.PLAY);
//                requestLayout();
//                invalidate();
//            } catch (Exception e) {
//
//            }
//
//        }
//
//    }
//
//    /**
//     * 播放视频 should called after loadVideo()
//     */
//    private void startPlayVideo() {
//        if (mPlayer != null) {
//            try {
//                mPlayer.setVideoURI(Uri.parse(videourl));
//                resetHideTimer();
//                resetUpdateTimer();
//                mMediaController.setPlayState(ZSVitamioMediaController.PlayState.PLAY);
//                requestLayout();
//                invalidate();
//            } catch (Exception e) {
//
//            }
//
//        }
//
//    }
//
//    public String getPlayStatus() {
//        Log.d(TAG, "getStatus,getPlayStatus");
//        if (mMediaController.getStatus() == ZSVitamioMediaController.PlayState.PLAY) {
//            Log.d(TAG, "getStatus,PLAY");
//            return ZSVideoConst.VIDEO_STATUS_PLAY;
//        } else if (mMediaController.getStatus() == ZSVitamioMediaController.PlayState.PAUSE) {
//            Log.d(TAG, "getStatus,PAUSE");
//            return ZSVideoConst.VIDEO_STATUS_PAUSE;
//        } else if (mMediaController.getStatus() == ZSVitamioMediaController.PlayState.STOP) {
//            return ZSVideoConst.VIDEO_STATUS_STOP;
//        }
//        return ZSVideoConst.VIDEO_STATUS_PAUSE;
//    }
//
//    public long getCurrentPosition() {
//        long position = 0;
//        if (mPlayer != null) {
//            try {
//                position = mPlayer.getCurrentPosition();
//            } catch (Exception e) {
//                e.printStackTrace();
//                mPlayer.stopPlayback();
////                mPlayer = null;
//            }
//        }
//        Log.i(TAG,"getCurrentPosition："+position);
//        return position;
//    }
//
//
//    private void updatePlayTime() {
//        if (mPlayer == null) {
//            return;
//        }
//        try {
//            long allTime = mPlayer.getDuration();
//            long playTime = mPlayer.getCurrentPosition();
//            mMediaController.setPlayProgressTxt(playTime, allTime);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mPlayer.stopPlayback();
////            mPlayer = null;
//        }
//    }
//
//    private void updatePlayProgress() {
//        if (mPlayer == null) {
//            return;
//        }
//        try {
//            long allTime = mPlayer.getDuration();
//            long playTime = mPlayer.getCurrentPosition();
//            long progress = playTime * 100 / allTime;
//            mMediaController.setProgressBar(progress, progressSec);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mPlayer.stopPlayback();
////            mPlayer = null;
//        }
//    }
//
//    /***
//     *
//     */
//    private void showOrHideController() {
//        if (mMediaController.getVisibility() == View.VISIBLE) {
//            Animation animation = AnimationUtils.loadAnimation(mContext,
//                    R.anim.anim_exit_from_bottom);
//            animation.setAnimationListener(new AnimationImp() {
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    super.onAnimationEnd(animation);
//                    mMediaController.setVisibility(View.GONE);
//                }
//            });
//            mMediaController.startAnimation(animation);
//        } else {
//            mMediaController.setVisibility(View.VISIBLE);
//            mMediaController.clearAnimation();
//            Animation animation = AnimationUtils.loadAnimation(mContext,
//                    R.anim.anim_enter_from_bottom);
//            mMediaController.startAnimation(animation);
//            resetHideTimer();
//        }
//    }
//
//    private void alwaysShowController() {
//        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
//        mMediaController.setVisibility(View.VISIBLE);
//    }
//
//    private void resetHideTimer() {
//        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
//        mHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLLER,
//                TIME_SHOW_CONTROLLER);
//    }
//
//    private void stopHideTimer() {
//        mHandler.removeMessages(MSG_HIDE_CONTROLLER);
//        mMediaController.setVisibility(View.VISIBLE);
//        mMediaController.clearAnimation();
//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                R.anim.anim_enter_from_bottom);
//        mMediaController.startAnimation(animation);
//    }
//
//    private void resetUpdateTimer() {
//        stopUpdateTimer();
//        mUpdateTimer = new Timer();
//        mTimerTask = new TimerTask() {
//
//            @Override
//            public void run() {
//                mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);
//            }
//        };
//        mUpdateTimer.schedule(mTimerTask, 0, TIME_UPDATE_PLAY_TIME);
//    }
//
//    private void stopUpdateTimer() {
//        if (mUpdateTimer != null) {
//            mUpdateTimer.cancel();
//            mUpdateTimer = null;
//        }
//        if (mTimerTask != null) {
//            mTimerTask.cancel();
//            mTimerTask = null;
//        }
//    }
//
//    private class AnimationImp implements Animation.AnimationListener {
//
//        @Override
//        public void onAnimationEnd(Animation animation) {
//
//        }
//
//        @Override
//        public void onAnimationRepeat(Animation animation) {
//        }
//
//        @Override
//        public void onAnimationStart(Animation animation) {
//        }
//    }
//
//    public interface VideoPlayCallbackImpl {
//        void onCloseVideo();
//
//        void onSwitchPageType();
//
//        void onPlayFinish();
//
//        void onErrorCallBack();
//    }
//    public void showExtendBtn(boolean isShow)
//    {
//        if(isShow)
//        {
//            mMediaController.setPageType(ZSVitamioMediaController.PageType.SHRINK);
//        }else
//        {
//            mMediaController.setExpandBtnGone();
//        }
//
//    }
//}