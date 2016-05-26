package com.zhongsou.souyue.adapter.baselistadapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.circle.util.CircleUtils;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.view.HotConfigView;
import com.zhongsou.souyue.view.videos.ZSMediaManager;
import com.zhongsou.souyue.view.videos.ZSVideoPlayer;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/4/27.
 */
public class VideoBiliRender extends VideoRender{
    private static final int STOP_PLAY_POSITION =-1;
    private ZSVideoPlayer mVideoView;
    private HotConfigView hotConfigView;
    private String videoUrl;
    private int indexPostion = STOP_PLAY_POSITION;
    private boolean isPlaying;
    private SigleBigImgBean bean;
    private RelativeLayout videolayout;
    private int width;
    private int deviceWidth;

    public VideoBiliRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
        super(context, itemType, bottomType, adapter);
        deviceWidth = CircleUtils.getDeviceWidth(context);
        width = deviceWidth - DeviceUtil.dip2px(context, 20) ;

    }

    @Override
    public View getConvertView() {
        mConvertView = View.inflate(mContext, R.layout.listitem_videobili, null);
        mVideoView = findView(mConvertView, R.id.videoView);
        hotConfigView = findView(mConvertView, R.id.hotconfigView);
        videolayout = findView(mConvertView, R.id.videolayout);
        if(mListManager instanceof HomeListManager)
        {

            ((HomeListManager) mListManager).sendCancleAll();
            ((HomeListManager) mListManager).setUpdateReciever();
            ((HomeListManager) mListManager).setHomeListener();
            ((HomeListManager) mListManager).setScreenListener();
        }
        setViewLayout(videolayout, width, 1.7);
        setViewLayout(mVideoView, width, 1.7);
        return super.getConvertView();
    }
    private void setViewLayout(View v , int width, double aspectRatio)
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        params.width = width;
        params.height = (int) (width/aspectRatio);
        v.setLayoutParams(params);
    }


    @Override
    public void fitDatas(final int position) {
        super.fitDatas(position);
        mTitleTv.setOnClickListener(this);
        findView(mConvertView, R.id.bottomView).setOnClickListener(this);
        bean = (SigleBigImgBean) mAdaper.getItem(position);
        videoUrl = bean.getPhoneImageUrl();
        hotConfigView.setData(bean.getTitleIcon());
        mTitleTv.setText(ListUtils.calcTitle(mContext, bean.getTitleIcon(), getTitle(bean)));
        if (mListManager instanceof IItemInvokeVideo) {
            indexPostion=((IItemInvokeVideo) mListManager).getPlayPosition();
        }
        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.setUp(videoUrl);
        mVideoView.setIvThumb(bean.getBigImgUrl());
        mVideoView.setDuration(bean.getDuration());
        mVideoView.setCallBack(new ZSVideoPlayer.ZSVideoPlayCallBack() {
            @Override
            public void dealClick() {
                startToPlay(position);
            }

            @Override
            public boolean expand() {
                if(mListManager instanceof  HomeListManager)
                {
                    ((HomeListManager) mListManager).setIsExpand(true);
                }
                return  false;
            }

            @Override
            public void dealError() {
                stopPlay();
            }
        });
    }

    public void stopPlay()
    {
        mVideoView.pausePlay();
        mVideoView.releasePlay();
        indexPostion=STOP_PLAY_POSITION;
        isPlaying =false;
        if (mListManager instanceof IItemInvokeVideo) {
            ((IItemInvokeVideo) mListManager).setPlayPosition(indexPostion);
            ((IItemInvokeVideo) mListManager).setIsPalying(false);
        }
    }
    public void pausePlay()
    {
        ZSMediaManager.intance(mContext).mediaPlayer.pause();
    }

    @Override
    public void releasePlay() {
        ZSMediaManager.intance(mContext).mediaPlayer.release();
    }

    @Override
    public boolean isPlaying() {

        return  ZSMediaManager.intance(mContext).mediaPlayer.isPlaying();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.image:
            case R.id.controller:

                break;
            case R.id.title:
            case R.id.bottomView:
                if(mListManager instanceof HomeListManager)
                {
                    mListManager.clickItem(bean);
                }
                break;
        }

    }
    public void startToPlay(int position)
    {
        if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
            UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_list_play, bean.getInvoke().getSrpId());
            if (StringUtils.isNotEmpty(videoUrl)) {
                if (mListManager instanceof IItemInvokeVideo) {
                    ((IItemInvokeVideo) mListManager).stopPlay(position);
                    ((IItemInvokeVideo) mListManager).setPlayRender(this);
                    ((IItemInvokeVideo) mListManager).setPlayPosition(position);
                    ((IItemInvokeVideo) mListManager).setIsPalying(true);
                }
                isPlaying = true;
            }
        }

    }
    public void startPlay()
    {
        ZSMediaManager.intance(mContext).mediaPlayResume();
    }

    public int getCurrentPosition() {
        int position = 0;
        position = (int) ZSMediaManager.intance(mContext).getCurrentPosition();
        return position;
    }


}
