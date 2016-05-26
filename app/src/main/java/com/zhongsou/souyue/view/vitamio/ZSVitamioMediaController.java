//package com.zhongsou.souyue.view.vitamio;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.zhongsou.souyue.R;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @description: 播放控制器，包括 播放暂停，进度条，时间，全屏缩放
// *                  都在控制器中完成
// *                  当前只记录界面的变化，控制过程则是在 zsvideoview中
// *
// * @auther: qubian
// * @data: 2016/3/16.
// */
//public class ZSVitamioMediaController extends FrameLayout implements
//        SeekBar.OnSeekBarChangeListener, View.OnClickListener {
//    private ImageView mPlayImg;// 播放按钮
//    private SeekBar mProgressSeekBar;// 播放进度条
//    private TextView mTimeTxt;// 播放时间
//    private ImageView mExpandImg;// 最大化播放按钮
//    private ImageView mShrinkImg;// 缩放播放按钮
//
//    private MediaControlImpl mMediaControl;
//    private PlayState status;
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress,
//                                  boolean isFromUser) {
//        if (isFromUser)
//            mMediaControl.onProgressTurn(ProgressState.DOING, progress);
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        mMediaControl.onProgressTurn(ProgressState.START, 0);
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        mMediaControl.onProgressTurn(ProgressState.STOP, 0);
//    }
//
//    public PlayState getStatus() {
//        return status;
//    }
//
//    public void setStatus(PlayState status) {
//        this.status = status;
//    }
//
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.pause) {
//            mMediaControl.onPlayTurn();
//        } else if (view.getId() == R.id.expand) {
//            mMediaControl.onPageTurn();
//        } else if (view.getId() == R.id.shrink) {
//            mMediaControl.onPageTurn();
//        }
//    }
//
//    public void setProgressBar(int progress) {
//        if (progress < 0)
//            progress = 0;
//        if (progress > 100)
//            progress = 100;
//        mProgressSeekBar.setProgress(progress);
//    }
//
//    public void setProgressBar(long progress, int secondProgress) {
//        if (progress < 0)
//            progress = 0;
//        if (progress > 100)
//            progress = 100;
//        if (secondProgress < 0)
//            secondProgress = 0;
//        if (secondProgress > 100)
//            secondProgress = 100;
//        mProgressSeekBar.setProgress((int)progress);
//        mProgressSeekBar.setSecondaryProgress(secondProgress);
//    }
//
//    public void setPlayState(PlayState playState) {
//        status =playState;
//        mPlayImg.setImageResource(playState.equals(PlayState.PLAY) ? R.drawable.biz_video_pause
//                : R.drawable.biz_video_play);
//    }
//
//    public void setPageType(PageType pageType) {
//        mExpandImg.setVisibility(pageType.equals(PageType.EXPAND) ? GONE
//                : VISIBLE);
//        mShrinkImg.setVisibility(pageType.equals(PageType.SHRINK) ? GONE
//                : VISIBLE);
//    }
//    public void setExpandBtnGone()
//    {
//        findViewById(R.id.expandView).setVisibility(GONE);
//    }
//
//    public void setPlayProgressTxt(long nowSecond, long allSecond) {
//        mTimeTxt.setText(getPlayTime(nowSecond, allSecond));
//    }
//
//    public void playFinish(long allTime) {
//        mProgressSeekBar.setProgress(0);
//        setPlayProgressTxt(0, allTime);
//        setPlayState(PlayState.PAUSE);
//    }
//
//    public void setMediaControl(MediaControlImpl mediaControl) {
//        mMediaControl = mediaControl;
//    }
//
//    public ZSVitamioMediaController(Context context) {
//        super(context);
//        initView(context);
//    }
//
//    public ZSVitamioMediaController(Context context, AttributeSet attrs,
//                                    int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView(context);
//    }
//
//    public ZSVitamioMediaController(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initView(context);
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initView(Context context) {
//        View.inflate(context, R.layout.super_video_media_controller, this);
//        mPlayImg = (ImageView) findViewById(R.id.pause);
//        mProgressSeekBar = (SeekBar) findViewById(R.id.media_controller_progress);
//        mTimeTxt = (TextView) findViewById(R.id.time);
//        mExpandImg = (ImageView) findViewById(R.id.expand);
//        mShrinkImg = (ImageView) findViewById(R.id.shrink);
//        initData();
//        this.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 拦截
//                return true;
//            }
//        });
//        requestFocus();
//    }
//
//    private void initData() {
//        mProgressSeekBar.setOnSeekBarChangeListener(this);
//        mPlayImg.setOnClickListener(this);
//        mShrinkImg.setOnClickListener(this);
//        mExpandImg.setOnClickListener(this);
//        setPageType(PageType.SHRINK);
//        setPlayState(PlayState.PAUSE);
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    private String formatPlayTime(long time) {
//        DateFormat formatter = new SimpleDateFormat("mm:ss");
//        return formatter.format(new Date(time));
//    }
//
//    private String getPlayTime(long playSecond, long allSecond) {
//        String playSecondStr = "00:00";
//        String allSecondStr = "00:00";
//        if (playSecond > 0) {
//            playSecondStr = formatPlayTime(playSecond);
//        }
//        if (allSecond > 0) {
//            allSecondStr = formatPlayTime(allSecond);
//        }
//        return playSecondStr + "/" + allSecondStr;
//    }
//
//    /**
//     * 播放样式 展开、缩放
//     */
//    public enum PageType {
//        EXPAND, SHRINK
//    }
//
//    /**
//     * 播放状态 播放 暂停
//     */
//    public enum PlayState {
//        PLAY, PAUSE,STOP
//    }
//
//    public enum ProgressState {
//        START, DOING, STOP
//    }
//
//    public interface MediaControlImpl {
//        void onPlayTurn();
//
//        void onPageTurn();
//
//        void onProgressTurn(ProgressState state, int progress);
//
//        void alwaysShowController();
//    }
//
//    public boolean isShowing() {
//        return this.getVisibility()== View.VISIBLE;
//    }
//    public void hide()
//    {
//        setVisibility(View.GONE);
//    }
//    public void show()
//    {
//        setVisibility(View.VISIBLE);
//    }
//}
