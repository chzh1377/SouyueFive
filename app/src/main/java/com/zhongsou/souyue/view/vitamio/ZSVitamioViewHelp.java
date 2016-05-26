//package com.zhongsou.souyue.view.vitamio;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.sina.weibo.sdk.utils.LogUtil;
//import com.zhongsou.souyue.view.ZSVideoConst;
//
//import io.vov.vitamio.MediaPlayer;
//
///**
// * @description: 负责拿到整个 播放过程中 MediaPlayer 的唯一单例
// * 用于全屏时的播放，控制其过程中 全屏在原有位置播放，以及 退出全屏后，继续播放位置播放
// * <p/>
// * 拓展：考虑，youtube 过程中的 窗口播放模式
// * 但是还是要保证其播放唯一
// * @auther: qubian
// * @data: 2016/3/16.
// */
//public class ZSVitamioViewHelp {
//
//    private static MediaPlayer mPlayer;
//
//    public static MediaPlayer getInstance(Context context) {
//        if (mPlayer == null) {
//            mPlayer = new MediaPlayer(context);
//        }
//        return mPlayer;
//    }
//
//    /**
//     * MediaPlayer resume
//     */
//    public static void resume() {
//        try {
//            if (mPlayer != null) {
//                mPlayer.start();
//            }
//        } catch (Exception e) {
//            LogUtil.i("ZSVideoViewHelp", "resume error");
//        }
//
//    }
//
//    /**
//     * MediaPlayer pause
//     */
//    public static void pause() {
//        try {
//            if (mPlayer != null) {
//                if (mPlayer.isPlaying()) {
//                    mPlayer.pause();
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.i("ZSVideoViewHelp", "pause error");
//        }
//
//    }
//
//    /**
//     * MediaPlayer reset
//     */
//    public static void reset() {
//        try {
//            if (mPlayer != null) {
//                mPlayer.reset();
//            }
//        } catch (Exception e) {
//            LogUtil.i("ZSVideoViewHelp", "pause error");
//        }
//
//    }
//    public static void stop() {
//        try {
//            if (mPlayer != null) {
//                if (mPlayer.isPlaying()) {
//                    mPlayer.stop();
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.i("ZSVideoViewHelp", "pause error");
//        }
//
//    }
//
//    /**
//     * MediaPlayer release
//     */
//    public static void release() {
//        try {
//            if (mPlayer != null) {
//                mPlayer.reset();
//                mPlayer.release();
//                mPlayer = null;
//            }
//        } catch (Exception e) {
//            LogUtil.i("ZSVideoViewHelp", "release error");
//        }
//
//    }
//    public static boolean isPlaying()
//    {
//        boolean isPlay =false;
//        if (mPlayer != null)
//        {
//            synchronized (mPlayer)
//            {
//                try {
//                    isPlay = mPlayer.isPlaying();
//                }catch (Exception e)
//                {
//                    LogUtil.i("ZSVideoViewHelp", "isPlaying error");
//                }
//            }
//        }
//        return  isPlay;
//    }
//    public static long getCurrentPosition()
//    {
//        long position =0;
//        if(isPlaying())
//        {
//            try
//            {
//                position = mPlayer.getCurrentPosition();
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//        return  position;
//    }
//    public static void sendStopBroadcast(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(ZSVideoConst.REFRESH_VIDEO);
//        intent.putExtra(ZSVideoConst.VIDEO_STATUS, ZSVideoConst.VIDEO_STATUS_STOP);
//        context.sendBroadcast(intent);
//    }
//
//    public static void mediaPlaySeekTo(int seek) {
//        try
//        {
//            mPlayer.seekTo(seek);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//}
