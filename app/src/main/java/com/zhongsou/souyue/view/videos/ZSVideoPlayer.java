package com.zhongsou.souyue.view.videos;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.ZSImageOptions;
import com.facebook.drawee.view.ZSImageView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.CMainHttp;

import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public class ZSVideoPlayer extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, SurfaceHolder.Callback, View.OnTouchListener, ZSMediaManager.ZSMediaPlayerListener {
    private static final String TAG = "ZSVideoPlayer";
    public ImageView ivStart;
    public ImageView ivPlay_status;
    TextView durationTv;
    ProgressBar pbLoading, pbBottom;
    ImageView ivFullScreen;
    SeekBar skProgress;
    TextView tvTimeCurrent, tvTimeTotal;
    ZSResizeSurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    TextView tvTitle;
    ImageView ivBack;
    public ZSImageView ivThumb;
    RelativeLayout rlParent;
    LinearLayout llTitleContainer, llBottomControl;
    ImageView ivCover;

    private String url;
    private String title;
    private boolean ifFullScreen = false;
    private boolean ifShowTitle = false;
    private boolean ifMp3 = false;

    private int enlargRecId = 0;
    private int shrinkRecId = 0;

    private int surfaceId;

    public int CURRENT_STATE = -1;//-1相当于null
    public static final int CURRENT_STATE_PREPAREING = 0;
    public static final int CURRENT_STATE_PAUSE = 1;
    public static final int CURRENT_STATE_PLAYING = 2;
    public static final int CURRENT_STATE_OVER = 3;
    public static final int CURRENT_STATE_NORMAL = 4;
    public static final int CURRENT_STATE_ERROR = 5;

    private OnTouchListener mSeekbarOnTouchListener;
    private static Timer mDismissControlViewTimer;
    private static Timer mUpdateProgressTimer;
    private static long clickfullscreentime;
    private static final int FULL_SCREEN_NORMAL_DELAY = 5000;

    private boolean touchingProgressBar = false;
    public static boolean isClickFullscreen = false;//一会调试一下，看是不是需要这个
    public boolean isFullscreenFromNormal = false;

    private static ImageView.ScaleType speScalType = null;

    private static ZSBuriedPoint JC_BURIED_POINT;
    private Context mContext;

    public ZSVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.bili_video_control_view, this);
        ivStart = (ImageView) findViewById(R.id.start);
        ivPlay_status = (ImageView) findViewById(R.id.play_status);
        durationTv = (TextView) findViewById(R.id.durationTv);
        pbLoading = (ProgressBar) findViewById(R.id.loading);
        pbBottom = (ProgressBar) findViewById(R.id.bottom_progressbar);
        ivFullScreen = (ImageView) findViewById(R.id.fullscreen);
        skProgress = (SeekBar) findViewById(R.id.progress);
        tvTimeCurrent = (TextView) findViewById(R.id.current);
        tvTimeTotal = (TextView) findViewById(R.id.total);
        llBottomControl = (LinearLayout) findViewById(R.id.bottom_control);
        tvTitle = (TextView) findViewById(R.id.title);
        ivBack = (ImageView) findViewById(R.id.back);
        ivThumb = (ZSImageView) findViewById(R.id.thumb);
        rlParent = (RelativeLayout) findViewById(R.id.parentview);
        llTitleContainer = (LinearLayout) findViewById(R.id.title_container);
        ivCover = (ImageView) findViewById(R.id.cover);

        ivStart.setOnClickListener(this);
        ivPlay_status.setOnClickListener(this);
        ivThumb.setOnClickListener(this);
        ivFullScreen.setOnClickListener(this);
        skProgress.setOnSeekBarChangeListener(this);
        llBottomControl.setOnClickListener(this);
        rlParent.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        skProgress.setOnTouchListener(this);
        if (speScalType != null) {
            ivThumb.setScaleType(speScalType);
        }
    }

    /**
     * <p>配置要播放的内容</p>
     * <p>Configuring the Content to Play</p>
     *
     * @param url   视频地址 | Video address
     * @param title 标题 | title
     */
    public void setUp(String url, String title) {
        setUp(url, title, true, "", "");
    }

    public void setUp(String url, String title, String imageUrl, String duration) {
        setUp(url, title, true, imageUrl, duration);
    }

    public void setUp(String url) {
        setUp(url, "", true, "", "");
    }

    public void setScreenPortrait() {

    }

    public void setScreenLandscape() {

    }

    public interface ZSVideoPlayCallBack {
        void dealClick();

        boolean expand();

        void dealError();
    }

    ZSVideoPlayCallBack callBack;

    public void setCallBack(ZSVideoPlayCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * <p>配置要播放的内容</p>
     * <p>Configuring the Content to Play</p>
     *
     * @param url         视频地址 | Video address
     * @param title       标题 | title
     * @param ifShowTitle 是否在非全屏下显示标题 | The title is displayed in full-screen under
     */
    public void setUp(String url, String title, boolean ifShowTitle, String imageUrl, String duration) {
        this.ifShowTitle = ifShowTitle;
        if ((System.currentTimeMillis() - clickfullscreentime) < FULL_SCREEN_NORMAL_DELAY) return;
        this.url = url;
        this.title = title;
        this.ifFullScreen = false;
        CURRENT_STATE = CURRENT_STATE_NORMAL;
        if (ifFullScreen) {
            ivFullScreen.setImageResource(enlargRecId == 0 ? R.drawable.biz_video_shrink : enlargRecId);
        } else {
            ivFullScreen.setImageResource(shrinkRecId == 0 ? R.drawable.biz_video_expand : shrinkRecId);
            ivBack.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(url) && url.contains(".mp3")) {
            ifMp3 = true;
            ivFullScreen.setVisibility(View.GONE);
        }
        tvTitle.setText(title);

        changeUiToNormal();

        if (ZSMediaManager.intance(mContext).listener == this) {
            try {
                ZSMediaManager.intance(mContext).mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            ivThumb.setImageURL(imageUrl, ZSImageOptions.getDefaultConfig(mContext, R.drawable.default_gif));
        }
        if (!TextUtils.isEmpty(duration)) {
            durationTv.setText(duration);
            durationTv.setVisibility(VISIBLE);
        } else {
            durationTv.setVisibility(GONE);
        }
    }

    public void setIvThumb(String imageUrl) {
        ivThumb.setImageURL(imageUrl, ZSImageOptions.getDefaultConfig(mContext, R.drawable.default_gif));
    }

    public void setDuration(String duration) {
        if (!TextUtils.isEmpty(duration)) {
            durationTv.setText(duration);
            durationTv.setVisibility(VISIBLE);
        } else {
            durationTv.setVisibility(GONE);
        }
    }

    /**
     * <p>只在全全屏中调用的方法</p>
     * <p>Only in fullscreen can call this</p>
     *
     * @param url   视频地址 | Video address
     * @param title 标题 | title
     */
    public void setUpForFullscreen(String url, String title) {
        this.url = url;
        this.title = title;
        ifShowTitle = true;
        ifFullScreen = true;
        CURRENT_STATE = CURRENT_STATE_NORMAL;
        if (ifFullScreen) {
            ivFullScreen.setImageResource(shrinkRecId == 0 ? R.drawable.biz_video_shrink : shrinkRecId);
        } else {
            ivFullScreen.setImageResource(enlargRecId == 0 ? R.drawable.biz_video_expand : enlargRecId);
        }
        tvTitle.setText(title);
        if (!TextUtils.isEmpty(url) && url.contains(".mp3")) {
            ifMp3 = true;
        }
        changeUiToNormal();
    }

    /**
     * <p>只在全全屏中调用的方法</p>
     * <p>Only in fullscreen can call this</p>
     *
     * @param state int state
     */
    public void setState(int state) {
        this.CURRENT_STATE = state;
        //全屏或取消全屏时继续原来的状态
        if (CURRENT_STATE == CURRENT_STATE_PREPAREING) {
            changeUiToShowUiPrepareing();
            setProgressAndTime(0, 0, 0);
            setProgressBuffered(0);
        } else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
            changeUiToShowUiPlaying();
        } else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
            changeUiToShowUiPause();
        } else if (CURRENT_STATE == CURRENT_STATE_NORMAL) {
            changeUiToNormal();
            cancelDismissControlViewTimer();
            cancelProgressTimer();
        } else if (CURRENT_STATE == CURRENT_STATE_ERROR) {
            ZSMediaManager.intance(mContext).mediaPlayer.release();
            changeUiToError();
        }
    }

    /**
     * 目前认为详细的判断和重复的设置是有相当必要的,也可以包装成方法
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start || i == R.id.thumb || i == R.id.play_status) {
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(getContext(), "视频地址为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (i == R.id.thumb) {
                if (CURRENT_STATE != CURRENT_STATE_NORMAL) {
                    onClickUiToggle();
                    return;
                }
            }
            if (CURRENT_STATE == CURRENT_STATE_NORMAL || CURRENT_STATE == CURRENT_STATE_ERROR) {
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
                    return;
                }
                if (this.callBack != null) {
                    callBack.dealClick();
                }
                touchingProgressBar = false;//This should not be here , but this can improve accident bug .

                addSurfaceView();

                if (ZSMediaManager.intance(mContext).listener != null) {
                    ZSMediaManager.intance(mContext).listener.onCompletion();
                }
                ZSMediaManager.intance(mContext).listener = this;

                ZSMediaManager.intance(mContext).clearWidthAndHeight();
                CURRENT_STATE = CURRENT_STATE_PREPAREING;
                changeUiToShowUiPrepareing();
                llBottomControl.setVisibility(View.INVISIBLE);
                llTitleContainer.setVisibility(View.INVISIBLE);
                setProgressAndTime(0, 0, 0);
                setProgressBuffered(0);
                ZSMediaManager.intance(mContext).prepareToPlay(getContext(), url);
                Log.i(TAG, "play video");

                surfaceView.requestLayout();
                setKeepScreenOn(true);

                if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                    if (i == R.id.start) {
                        JC_BURIED_POINT.POINT_START_ICON(title, url);
                    } else {
                        JC_BURIED_POINT.POINT_START_THUMB(title, url);
                    }
                }
            } else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
                CURRENT_STATE = CURRENT_STATE_PAUSE;

                changeUiToShowUiPause();

                ZSMediaManager.intance(mContext).mediaPlayer.pause();
                Log.i(TAG, "pause video");

                setKeepScreenOn(false);
                cancelDismissControlViewTimer();

                if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                    if (ifFullScreen) {
                        JC_BURIED_POINT.POINT_STOP_FULLSCREEN(title, url);
                    } else {
                        JC_BURIED_POINT.POINT_STOP(title, url);
                    }
                }
            } else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
                CURRENT_STATE = CURRENT_STATE_PLAYING;

                changeUiToShowUiPlaying();
                ZSMediaManager.intance(mContext).mediaPlayer.start();
                Log.i(TAG, "go on video");

                setKeepScreenOn(true);
                startDismissControlViewTimer();

                if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                    if (ifFullScreen) {
                        JC_BURIED_POINT.POINT_RESUME_FULLSCREEN(title, url);
                    } else {
                        JC_BURIED_POINT.POINT_RESUME(title, url);
                    }
                }
            }

        } else if (i == R.id.fullscreen) {
            boolean doSelf = false;
            if (this.callBack != null) {
                doSelf = callBack.expand();
            }
            if (doSelf) {
                return;
            }
            if (ifFullScreen) {
                isClickFullscreen = false;
                quitFullScreen();
            } else {
                ZSMediaManager.intance(mContext).mediaPlayer.pause();
                ZSMediaManager.intance(mContext).mediaPlayer.setDisplay(null);
                ZSMediaManager.intance(mContext).lastListener = this;
                ZSMediaManager.intance(mContext).listener = null;
                isClickFullscreen = true;
                ZSFullScreenActivity.toActivityFromNormal(getContext(), CURRENT_STATE, url, title);

                if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                    JC_BURIED_POINT.POINT_ENTER_FULLSCREEN(title, url);
                }
                changeUiToFullScreen();
            }
            clickfullscreentime = System.currentTimeMillis();
        } else if (i == surfaceId || i == R.id.parentview) {
            if (CURRENT_STATE == CURRENT_STATE_ERROR) {
                ivStart.performClick();
            } else {
                onClickUiToggle();
                startDismissControlViewTimer();

                if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                    if (ifFullScreen) {
                        JC_BURIED_POINT.POINT_CLICK_BLANK_FULLSCREEN(title, url);
                    } else {
                        JC_BURIED_POINT.POINT_CLICK_BLANK(title, url);
                    }
                }
            }
        } else if (i == R.id.bottom_control) {
            //JCMediaPlayer.intance().mediaPlayer.setDisplay(surfaceHolder);
        } else if (i == R.id.back) {
            quitFullScreen();
        }
        durationTv.setVisibility(GONE);
    }

    void addSurfaceView() {
        if (rlParent.getChildAt(0) instanceof ZSResizeSurfaceView) {
            rlParent.removeViewAt(0);
        }
        surfaceView = new ZSResizeSurfaceView(getContext());
        surfaceId = surfaceView.getId();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceView.setOnClickListener(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlParent.addView(surfaceView, 0, layoutParams);
    }

    private void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        mDismissControlViewTimer = new Timer();
        mDismissControlViewTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (CURRENT_STATE != CURRENT_STATE_NORMAL) {
                                llBottomControl.setVisibility(View.INVISIBLE);
                                pbBottom.setVisibility(View.VISIBLE);
                                setTitleVisibility(View.INVISIBLE);
                                ivStart.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        }, 2500);
    }

    private void cancelDismissControlViewTimer() {
        if (mDismissControlViewTimer != null) {
            mDismissControlViewTimer.cancel();
        }
    }

    private void onClickUiToggle() {
        if (CURRENT_STATE == CURRENT_STATE_PREPAREING) {
            if (llBottomControl.getVisibility() == View.VISIBLE) {
                changeUiToClearUiPrepareing();
            } else {
                changeUiToShowUiPrepareing();
            }
        } else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
            if (llBottomControl.getVisibility() == View.VISIBLE) {
                changeUiToClearUiPlaying();
            } else {
                changeUiToShowUiPlaying();
            }
        } else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
            if (llBottomControl.getVisibility() == View.VISIBLE) {
                changeUiToClearUiPause();
            } else {
                changeUiToShowUiPause();
            }
        }
    }

    //Unified management Ui
    private void changeUiToNormal() {
        setTitleVisibility(View.VISIBLE);
        llBottomControl.setVisibility(View.INVISIBLE);
        ivStart.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
        setThumbVisibility(View.VISIBLE);
        ivCover.setVisibility(View.VISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
        updateStartImage();
        ivThumb.setVisibility(VISIBLE);
        durationTv.setVisibility(VISIBLE);
    }

    public void changeUiToFullScreen() {
        durationTv.setVisibility(GONE);
//        ifFullScreen = true;
    }

    private void changeUiToShowUiPrepareing() {
        setTitleVisibility(View.VISIBLE);
        llBottomControl.setVisibility(View.VISIBLE);
        ivStart.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);
        setThumbVisibility(View.INVISIBLE);
        ivCover.setVisibility(View.VISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
        durationTv.setVisibility(GONE);
    }

    private void changeUiToClearUiPrepareing() {
//        changeUiToClearUi();
        setTitleVisibility(View.INVISIBLE);
        llBottomControl.setVisibility(View.INVISIBLE);
        ivStart.setVisibility(View.INVISIBLE);
        setThumbVisibility(View.INVISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
//        pbLoading.setVisibility(View.VISIBLE);
        ivCover.setVisibility(View.VISIBLE);
        durationTv.setVisibility(GONE);
    }

    private void changeUiToShowUiPlaying() {
        setTitleVisibility(View.VISIBLE);
        llBottomControl.setVisibility(View.VISIBLE);
//        ivStart.setVisibility(View.VISIBLE);
        ivStart.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
        setThumbVisibility(View.INVISIBLE);
        ivCover.setVisibility(View.INVISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
        updateStartImage();
        durationTv.setVisibility(GONE);
    }

    private void changeUiToClearUiPlaying() {
        changeUiToClearUi();
        pbBottom.setVisibility(View.VISIBLE);
    }

    private void changeUiToShowUiPause() {
        setTitleVisibility(View.VISIBLE);
        llBottomControl.setVisibility(View.VISIBLE);
        ivStart.setVisibility(View.VISIBLE);
//        ivStart.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
        setThumbVisibility(View.INVISIBLE);
        ivCover.setVisibility(View.INVISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
        durationTv.setVisibility(GONE);
        updateStartImage();
    }

    private void changeUiToClearUiPause() {
        changeUiToClearUi();
        pbBottom.setVisibility(View.VISIBLE);
    }

    private void changeUiToClearUi() {
        setTitleVisibility(View.INVISIBLE);
        llBottomControl.setVisibility(View.INVISIBLE);
        ivStart.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
        setThumbVisibility(View.INVISIBLE);
        ivCover.setVisibility(View.INVISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
    }

    private void changeUiToError() {
        setTitleVisibility(View.INVISIBLE);
        llBottomControl.setVisibility(View.INVISIBLE);
        ivStart.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
        setThumbVisibility(View.VISIBLE);
        ivCover.setVisibility(View.VISIBLE);
        pbBottom.setVisibility(View.INVISIBLE);
        updateStartImage();
        durationTv.setVisibility(GONE);
    }

    private void startProgressTimer() {
        cancelProgressTimer();
        mUpdateProgressTimer = new Timer();
        mUpdateProgressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
                                setProgressAndTimeFromTimer();
                            }
                        }
                    });
                }
            }
        }, 0, 300);
    }

    private void cancelProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
        }
    }

    //if show title in top level Logic
    private void setTitleVisibility(int visable) {
        if (ifShowTitle) {
            llTitleContainer.setVisibility(visable);
        } else {
            if (ifFullScreen) {
                llTitleContainer.setVisibility(visable);
            } else {
                llTitleContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    //if show thumb in top level Logic
    private void setThumbVisibility(int visable) {
        if (ifMp3) {
            ivThumb.setVisibility(View.VISIBLE);
        } else {
            ivThumb.setVisibility(visable);
        }
    }

    private void updateStartImage() {
        if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
//            ivStart.setImageResource(R.drawable.biz_video_pause);
            ivPlay_status.setImageResource(R.drawable.biz_video_pause);
        } else if (CURRENT_STATE == CURRENT_STATE_ERROR) {
//            ivStart.setImageResource(R.drawable.biz_video_play);
            ivPlay_status.setImageResource(R.drawable.biz_video_play);
        } else {
//            ivStart.setImageResource(R.drawable.biz_video_play);
            ivPlay_status.setImageResource(R.drawable.biz_video_play);
        }

    }

    private void setProgressBuffered(int secProgress) {
        if (secProgress >= 0) {
            skProgress.setSecondaryProgress(secProgress);
            pbBottom.setSecondaryProgress(secProgress);
        }
    }

    private void setProgressAndTimeFromTimer() {
        int position = (int) ZSMediaManager.intance(mContext).mediaPlayer.getCurrentPosition();
        int duration = (int) ZSMediaManager.intance(mContext).mediaPlayer.getDuration();
        // if duration == 0 (e.g. in HLS streams) avoids ArithmeticException
        int progress = position * 100 / (duration == 0 ? 1 : duration);
        setProgressAndTime(progress, position, duration);
    }

    private void setProgressAndTime(int progress, int currentTime, int totalTime) {
        if (!touchingProgressBar) {
            skProgress.setProgress(progress);
            pbBottom.setProgress(progress);
        }
        tvTimeCurrent.setText(Utils.stringForTime(currentTime));
        tvTimeTotal.setText(Utils.stringForTime(totalTime));
    }

    public void release() {
        if ((System.currentTimeMillis() - clickfullscreentime) < FULL_SCREEN_NORMAL_DELAY) return;
        setState(CURRENT_STATE_NORMAL);
        //回收surfaceview
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int time = (int) (progress * ZSMediaManager.intance(mContext).mediaPlayer.getDuration() / 100);
            try {
                ZSMediaManager.intance(mContext).mediaPlayer.seekTo(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pbLoading.setVisibility(View.VISIBLE);
            ivStart.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void quitFullScreen() {
        ZSFullScreenActivity.manualQuit = true;
        clickfullscreentime = System.currentTimeMillis();
        try {
            ZSMediaManager.intance(mContext).mediaPlayer.pause();
            ZSMediaManager.intance(mContext).mediaPlayer.setDisplay(null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.e(TAG, "quitFullScreen  finally");
            //这个view释放了，
            ZSMediaManager.intance(mContext).lastState = CURRENT_STATE;

            if (ZSMediaManager.intance(mContext).lastListener != null) {
                ZSMediaManager.intance(mContext).listener = ZSMediaManager.intance(mContext).lastListener;
                ZSMediaManager.intance(mContext).listener.onBackFullscreen();
            }

            if (getContext() instanceof ZSFullScreenActivity) {
                Log.e(TAG, "finish");
                ((ZSFullScreenActivity) getContext()).finish();
            }

            if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                JC_BURIED_POINT.POINT_QUIT_FULLSCREEN(title, url);
            }
        }

    }

    private void stopToFullscreenOrQuitFullscreenShowDisplay() {
        if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
            Log.i(TAG,"stopToFullscreen -- >> Pause");
            try {
                // 不管横竖屏切换 直接播放
                ZSMediaManager.intance(mContext).mediaPlayer.start();
                // 会黑屏  ...  ...
//                ZSMediaManager.intance(mContext).mediaPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
            CURRENT_STATE = CURRENT_STATE_PLAYING;
            // 不管横竖屏切换 直接播放 并设置状态
            setState(CURRENT_STATE);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    ((Activity) getContext()).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                ZSMediaManager.intance(mContext).mediaPlayer.pause();
//                            }catch (Exception e)
//                            {
//                                e.printStackTrace();
//                            }
//
//                            CURRENT_STATE = CURRENT_STATE_PAUSE;
//                        }
//                    });
//                }
//            }).start();
            surfaceView.requestLayout();
        } else if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
            try {
                ZSMediaManager.intance(mContext).mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        //TODO MediaPlayer set holder,MediaPlayer prepareToPlay
        if (ifFullScreen) {
            ZSMediaManager.intance(mContext).mediaPlayer.setDisplay(surfaceHolder);
            stopToFullscreenOrQuitFullscreenShowDisplay();
        }
        Log.i(TAG,"CURRENT_STATE:"+CURRENT_STATE);
        if (CURRENT_STATE != CURRENT_STATE_NORMAL) {
            startDismissControlViewTimer();
            startProgressTimer();
        }

        if (ZSMediaManager.intance(mContext).lastListener == this) {
            ZSMediaManager.intance(mContext).mediaPlayer.setDisplay(surfaceHolder);
            stopToFullscreenOrQuitFullscreenShowDisplay();
            startDismissControlViewTimer();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
    }

    /**
     * <p>停止所有音频的播放</p>
     * <p>release all videos</p>
     */
    public static void releaseAllVideos(Context context) {
        if (!isClickFullscreen) {
            try {
                ZSMediaManager.intance(context).mediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ZSMediaManager.intance(context).listener != null) {
                ZSMediaManager.intance(context).listener.onCompletion();
            }
            if (mUpdateProgressTimer != null) {
                mUpdateProgressTimer.cancel();
            }
        }
    }

    /**
     * <p>有特殊需要的客户端</p>
     * <p>Clients with special needs</p>
     *
     * @param onClickListener 开始按钮点击的回调函数 | Click the Start button callback function
     */
    @Deprecated
    public void setStartListener(OnClickListener onClickListener) {
        if (onClickListener != null) {
            ivStart.setOnClickListener(onClickListener);
            ivThumb.setOnClickListener(onClickListener);
        } else {
            ivStart.setOnClickListener(this);
            ivThumb.setOnClickListener(this);
        }
    }

    private void sendPointEvent(int type) {
    }

    public void setSeekbarOnTouchListener(OnTouchListener listener) {
        mSeekbarOnTouchListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchingProgressBar = true;
                cancelDismissControlViewTimer();
                cancelProgressTimer();
                break;
            case MotionEvent.ACTION_UP:
                touchingProgressBar = false;
                startDismissControlViewTimer();
                startProgressTimer();

                if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                    if (ifFullScreen) {
                        JC_BURIED_POINT.POINT_CLICK_SEEKBAR_FULLSCREEN(title, url);
                    } else {
                        JC_BURIED_POINT.POINT_CLICK_SEEKBAR(title, url);
                    }
                }
                break;
        }

        if (mSeekbarOnTouchListener != null) {
            mSeekbarOnTouchListener.onTouch(v, event);
        }
        return false;
    }

    /**
     * <p>默认的缩略图的scaleType是fitCenter，这时候图片如果和屏幕尺寸不同的话左右或上下会有黑边，可以根据客户端需要改成fitXY或这其他模式</p>
     * <p>The default thumbnail scaleType is fitCenter, and this time the picture if different screen sizes up and down or left and right, then there will be black bars, or it may need to change fitXY other modes based on the client</p>
     *
     * @param thumbScaleType 缩略图的scalType | Thumbnail scaleType
     */
    public static void setThumbImageViewScalType(ImageView.ScaleType thumbScaleType) {
        speScalType = thumbScaleType;
    }

    /**
     * In demo is ok, but in other project This will class not access exception,How to solve the problem
     *
     * @param context Context
     * @param url     video url
     * @param title   video title
     */
    @Deprecated
    public static void toFullscreenActivity(Context context, String url, String title) {
        ZSFullScreenActivity.toActivity(context, url, title);
    }

    @Override
    public void onPrepared() {
        Log.i(TAG, "onPrepared");
        if (surfaceHolder.getSurface() == null || !surfaceHolder.getSurface().isValid()) return;
        if (CURRENT_STATE != CURRENT_STATE_PREPAREING) return;
        ZSMediaManager.intance(mContext).mediaPlayer.setDisplay(surfaceHolder);
        ZSMediaManager.intance(mContext).mediaPlayer.start();
        CURRENT_STATE = CURRENT_STATE_PLAYING;

        changeUiToShowUiPlaying();
        ivStart.setVisibility(View.INVISIBLE);

        startDismissControlViewTimer();
        startProgressTimer();
    }

    @Override
    public void onCompletion() {
        Log.i(TAG, "onCompletion");
        CURRENT_STATE = CURRENT_STATE_NORMAL;
        cancelProgressTimer();
        cancelDismissControlViewTimer();
        setKeepScreenOn(false);
        changeUiToNormal();

        if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
            if (ifFullScreen) {
                JC_BURIED_POINT.POINT_AUTO_COMPLETE_FULLSCREEN(title, url);
            } else {
                JC_BURIED_POINT.POINT_AUTO_COMPLETE(title, url);
            }
        }

        if (getContext() instanceof ZSFullScreenActivity) {
            ((ZSFullScreenActivity) getContext()).finish();
        }
        if (isFullscreenFromNormal) {//如果在进入全屏后播放完就初始化自己非全屏的控件
            isFullscreenFromNormal = false;
            ZSMediaManager.intance(mContext).lastListener.onCompletion();
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        Log.i(TAG, "onBufferingUpdate" + percent);
        if (percent > 92) {
            percent = 100;// 实在没有办法的办法
        }
        if (CURRENT_STATE != CURRENT_STATE_NORMAL || CURRENT_STATE != CURRENT_STATE_PREPAREING) {
            setProgressBuffered(percent);
        }
        if (durationTv.getVisibility() == VISIBLE) {
            durationTv.setVisibility(GONE);
        }

    }

    @Override
    public void onSeekComplete() {
        Log.i(TAG, "onSeekComplete");
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onError(int what, int extra) {
        Log.i(TAG, "onError what:"+what);
        if (what != -38) {
            setState(CURRENT_STATE_ERROR);
        }
//        if (!CMainHttp.getInstance().isNetworkAvailable(mContext))
        setState(CURRENT_STATE_ERROR);
        if (ifFullScreen) {
            isClickFullscreen = false;
            ZSMediaManager.intance(mContext).lastState = CURRENT_STATE;
            if (ZSMediaManager.intance(mContext).lastListener != null) {
                ZSMediaManager.intance(mContext).listener = ZSMediaManager.intance(mContext).lastListener;
                ZSMediaManager.intance(mContext).listener.onBackFullscreen();
            }
            if (getContext() instanceof ZSFullScreenActivity) {
                Log.e(TAG, "finish");
                ((ZSFullScreenActivity) getContext()).finish();
            }
            if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                JC_BURIED_POINT.POINT_QUIT_FULLSCREEN(title, url);
            }
        } else {
            releasePlay();

        }
        if (callBack != null) {
            Log.e(TAG, "callBack:dealError");
            callBack.dealError();
        }

    }

    @Override
    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged");
        int mVideoWidth = ZSMediaManager.intance(mContext).currentVideoWidth;
        int mVideoHeight = ZSMediaManager.intance(mContext).currentVideoHeight;
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
            surfaceView.requestLayout();
        }
    }

    @Override
    public void onBackFullscreen() {
        CURRENT_STATE = ZSMediaManager.intance(mContext).lastState;
        addSurfaceView();
        setState(CURRENT_STATE);
    }

    @Override
    public void onInfo(int what, int extra) {
        Log.i(TAG, "what: " + what);
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (CURRENT_STATE != CURRENT_STATE_NORMAL || CURRENT_STATE != CURRENT_STATE_PREPAREING) {
                    setProgressBuffered(100);
                }
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
//                    releasePlay();
//                    pausePlay();
                    setState(CURRENT_STATE_NORMAL);
                    if (ifFullScreen) {
                        isClickFullscreen = false;
                        quitFullScreen();
                    }
                    if (callBack != null) {
                        callBack.dealError();
                    }
                }
                break;
        }
    }

    public static void setJcBuriedPoint(ZSBuriedPoint ZSBuriedPoint) {
        JC_BURIED_POINT = ZSBuriedPoint;
    }

    public boolean isPlaying() {
        if (CURRENT_STATE == CURRENT_STATE_PLAYING) {
            return true;
        }
        return false;
    }

    public void pausePlay() {
        CURRENT_STATE = CURRENT_STATE_PAUSE;
        changeUiToShowUiPause();
        try {
            ZSMediaManager.intance(mContext).mediaPlayer.pause();
        } catch (Exception e) {

        }
        Log.i(TAG, "pause video");
        cancelDismissControlViewTimer();
        if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
            if (ifFullScreen) {
                JC_BURIED_POINT.POINT_STOP_FULLSCREEN(title, url);
            } else {
                JC_BURIED_POINT.POINT_STOP(title, url);
            }
        }
    }

    public void releasePlay() {
        setState(CURRENT_STATE_NORMAL);
        try {
            ZSMediaManager.intance(mContext).mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ivThumb.setVisibility(VISIBLE);
    }

    /**
     * 慎用
     */
    public void startPlay() {
        if (CURRENT_STATE == CURRENT_STATE_NORMAL || CURRENT_STATE == CURRENT_STATE_ERROR) {
            preparePlay();
        } else if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
            CURRENT_STATE = CURRENT_STATE_PLAYING;

            changeUiToShowUiPlaying();
            try {
                ZSMediaManager.intance(mContext).mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "go on video");

            setKeepScreenOn(true);
            startDismissControlViewTimer();

            if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {
                if (ifFullScreen) {
                    JC_BURIED_POINT.POINT_RESUME_FULLSCREEN(title, url);
                } else {
                    JC_BURIED_POINT.POINT_RESUME(title, url);
                }
            }
        }
        if (ZSMediaManager.intance(mContext).lastListener != null) {
            ZSMediaManager.intance(mContext).listener = ZSMediaManager.intance(mContext).lastListener;
            ZSMediaManager.intance(mContext).listener.onBackFullscreen();
        }
//        ZSMediaManager.intance(mContext).mediaPlayer.setDisplay(surfaceHolder);
    }

    public void start() {
        if (CURRENT_STATE == CURRENT_STATE_PAUSE) {
            CURRENT_STATE = CURRENT_STATE_PLAYING;
            changeUiToShowUiPlaying();
            try {
                ZSMediaManager.intance(mContext).mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void preparePlay() {
        if (this.callBack != null) {
            callBack.dealClick();
        }
        touchingProgressBar = false;//This should not be here , but this can improve accident bug .

        addSurfaceView();

        if (ZSMediaManager.intance(mContext).listener != null) {
            ZSMediaManager.intance(mContext).listener.onCompletion();
        }
        ZSMediaManager.intance(mContext).listener = this;

        ZSMediaManager.intance(mContext).clearWidthAndHeight();
        CURRENT_STATE = CURRENT_STATE_PREPAREING;
        changeUiToShowUiPrepareing();
        llBottomControl.setVisibility(View.INVISIBLE);
        llTitleContainer.setVisibility(View.INVISIBLE);
        setProgressAndTime(0, 0, 0);
        setProgressBuffered(0);
        ZSMediaManager.intance(mContext).prepareToPlay(getContext(), url);
        Log.i(TAG, "play video");

        surfaceView.requestLayout();
        setKeepScreenOn(true);
        if (JC_BURIED_POINT != null && ZSMediaManager.intance(mContext).listener == this) {

            JC_BURIED_POINT.POINT_START_ICON(title, url);
        }
    }

}
