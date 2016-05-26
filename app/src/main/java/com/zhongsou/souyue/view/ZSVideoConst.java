package com.zhongsou.souyue.view;

import android.content.Context;
import android.content.Intent;

/**
 * @description: 存放一些 视频相关的广播 静态数据
 * @auther: qubian
 * @data: 2016/4/26.
 */
public class ZSVideoConst {

    public static final int VIDEO_HEIGHT =200;
    public static final String REFRESH_VIDEO = "refresh_video"; // 刷新数据的广播
    public static final String VIDEO_STATUS = "status";
    public static final String VIDEO_POSITION = "palyPosition";
    public static final String VIDEO_STATUS_PLAY = "video_status_play"; //
    public static final String VIDEO_STATUS_PAUSE = "video_status_pause"; //
    public static final String VIDEO_STATUS_STOP = "video_status_stop"; //

    public static final String VIDEO_NET_ACTION = "net_status_action";
    public static final String VIDEO_NET_STATUS = "net_status";
    public static final String VIDEO_NET_STATUS_NO = "net_status_no";
    public static final String VIDEO_NET_STATUS_PHONE = "net_status_phone";
    public static final String VIDEO_NET_STATUS_wifi = "net_status_wifi";

    public static final String VIDEO_START_PLAY = "video_start_play"; //
    public static final String VIDEO_START_PLAY_POSITION = "video_start_play_position"; //
    public static final String VIDEO_START_PLAY_URL = "video_start_play_url"; //
    public static void sendStopBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(ZSVideoConst.REFRESH_VIDEO);
        intent.putExtra(ZSVideoConst.VIDEO_STATUS, ZSVideoConst.VIDEO_STATUS_STOP);
        context.sendBroadcast(intent);
    }
}
