//package com.zhongsou.souyue.adapter.baselistadapter;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.facebook.drawee.view.ZSImageView;
//import com.zhongsou.souyue.R;
//import com.zhongsou.souyue.circle.util.CircleUtils;
//import com.zhongsou.souyue.countUtils.UpEventAgent;
//import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
//import com.zhongsou.souyue.net.volley.CMainHttp;
//import com.zhongsou.souyue.utils.DeviceUtil;
//import com.zhongsou.souyue.utils.StringUtils;
//import com.zhongsou.souyue.view.HotConfigView;
//import com.zhongsou.souyue.view.vitamio.ZSVitamioVideoView;
//
//
///**
// * @description:
// * @auther: qubian
// * @data: 2016/4/21.
// */
//public class VitamioRender extends VideoRender {
//    private static final int STOP_PLAY_POSITION = -1;
//    private static final double aspectRatio =1.7;
//    private ZSVitamioVideoView mVideoView;
//    private ZSImageView image;
//    private HotConfigView hotConfigView;
//    private ImageView controllerIv;
//    private String videoUrl;
//    private int indexPostion = STOP_PLAY_POSITION;
//    private boolean isPlaying;
//    private TextView durationTv;
//    private SigleBigImgBean bean;
//    private RelativeLayout videolayout;
//    private RelativeLayout contentView;
//    private int width;
//    private int deviceWidth;
//
//    public VitamioRender(Context context, int itemType, int bottomType, BaseListViewAdapter adapter) {
//        super(context, itemType, bottomType, adapter);
//        deviceWidth = CircleUtils.getDeviceWidth(context);
//        width = deviceWidth - DeviceUtil.dip2px(context, 20);
//    }
//
//    @Override
//    public View getConvertView() {
//        mConvertView = View.inflate(mContext, R.layout.listitem_vitamio, null);
//        mVideoView = findView(mConvertView, R.id.videoView);
//        image = findView(mConvertView, R.id.image);
//        durationTv = findView(mConvertView, R.id.durationTv);
//        hotConfigView = findView(mConvertView, R.id.hotconfigView);
//        controllerIv = findView(mConvertView, R.id.controller);
//        videolayout = findView(mConvertView, R.id.videolayout);
//        contentView = findView(mConvertView, R.id.content);
//        image.setOnClickListener(this);
//        if (mListManager instanceof HomeListManager) {
//
//            ((HomeListManager) mListManager).sendCancleAll();
//            ((HomeListManager) mListManager).setUpdateReciever();
//            ((HomeListManager) mListManager).setHomeListener();
//            ((HomeListManager) mListManager).setScreenListener();
//        }
//        return super.getConvertView();
//    }
//
//    private void setViewLayout(View v, int width, double aspectRatio) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
//        params.width = width;
//        params.height = (int) (width / aspectRatio);
//        v.setLayoutParams(params);
//    }
//    private void setViewLayoutFullScreen(View v) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
//        params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//        params.height =RelativeLayout.LayoutParams.MATCH_PARENT;
//        v.setLayoutParams(params);
//    }
//
//
//
//    @Override
//    public void fitDatas(int position) {
//        super.fitDatas(position);
//        setViewLayout(videolayout, width, aspectRatio);
//        setViewLayout(image, width, aspectRatio);
//        setViewLayout(mVideoView, width, aspectRatio);
//        mTitleTv.setOnClickListener(this);
//        findView(mConvertView, R.id.bottomView).setOnClickListener(this);
//        bean = (SigleBigImgBean) mAdaper.getItem(position);
//        showImageForce(image, bean.getBigImgUrl(), R.drawable.default_gif, null);
//        durationTv.setText(bean.getDuration());
//        videoUrl = bean.getPhoneImageUrl();
//        hotConfigView.setData(bean.getTitleIcon());
//        mTitleTv.setText(ListUtils.calcTitle(mContext, bean.getTitleIcon(), getTitle(bean)));
//        controllerIv.setTag(position);
//        if (mListManager instanceof IItemInvokeVideo) {
//            indexPostion = ((IItemInvokeVideo) mListManager).getPlayPosition();
//        }
//        setPlayViewStatus(false);
//        if (indexPostion == position) {
//            setPlayViewStatus(true);
//        }
//        mVideoView.showExtendBtn(false);
//    }
//
//    public void stopPlay() {
////        if(mVideoView.isPlaying())
////        {
////            mVideoView.release();
////        }
//        mVideoView.release();
//        setPlayViewStatus(false);
//        indexPostion = STOP_PLAY_POSITION;
//        isPlaying = false;
//        if (mListManager instanceof IItemInvokeVideo) {
//            ((IItemInvokeVideo) mListManager).setPlayPosition(indexPostion);
//            ((IItemInvokeVideo) mListManager).setIsPalying(false);
//        }
//    }
//    public void startPlay() {
//        mVideoView.startPlay();
//    }
//
//    public void pausePlay() {
//        mVideoView.pausePlay();
//    }
//    public void releasePlay() {
//        stopPlay();
//    }
//
//
//
//    private void setPlayViewStatus(boolean isPlay) {
//        controllerIv.setVisibility(isPlay ? View.GONE : View.VISIBLE);
//        image.setVisibility(isPlay ? View.GONE : View.VISIBLE);
//        mVideoView.setVisibility(isPlay ? View.VISIBLE : View.GONE);
//        durationTv.setVisibility(isPlay ? View.GONE : View.VISIBLE);
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        switch (v.getId()) {
//            case R.id.image:
//            case R.id.controller:
//                if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
//                    UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_list_play, bean.getInvoke().getSrpId());
//                    if(mListManager instanceof HomeListManager)
//                    {
//                        if(!((HomeListManager) mListManager).ismRefreshing())
//                        {
//                            startToPlay();
//                        }
//                    }
//                }
//                break;
//            case R.id.title:
//            case R.id.bottomView:
//                if (mListManager instanceof HomeListManager) {
//                    mListManager.clickItem(bean);
//                }
//                break;
//        }
//
//    }
//
//    public void startToPlay() {
//        if (StringUtils.isNotEmpty(videoUrl)) {
//            setPlayViewStatus(true);
//            indexPostion = (Integer) controllerIv.getTag();
//            if (mListManager instanceof IItemInvokeVideo) {
//                ((IItemInvokeVideo) mListManager).stopPlay(indexPostion);
//                ((IItemInvokeVideo) mListManager).setPlayRender(this);
//                ((IItemInvokeVideo) mListManager).setPlayPosition(indexPostion);
//                ((IItemInvokeVideo) mListManager).setIsPalying(true);
//            }
//            isPlaying = true;
//            controllerIv.setVisibility(View.GONE);
//            mVideoView.setVisibility(View.VISIBLE);
//            mVideoView.loadAndPlay(null, videoUrl, 0, false);
//            mVideoView.setVideoPlayCallback(new ZSVitamioVideoView.VideoPlayCallbackImpl() {
//                @Override
//                public void onCloseVideo() {
//                    stopPlay();
//                }
//
//                @Override
//                public void onSwitchPageType() {
////                    if (((Activity) mContext).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//////                        IntentUtil.gotoFullScreen(mContext, videoUrl, (int) mVideoView.getCurrentPosition(), mVideoView.getPlayStatus());
////                        if (mListManager instanceof HomeListManager) {
////                            setScreenLandscape();
////                            ((HomeListManager) mListManager).setIsExpand(true);
////                            ((HomeListManager) mListManager).setScreenLandscape(VitamioRender.this,indexPostion);
////                        }
////                    }else{
////                        if (mListManager instanceof HomeListManager) {
////                            setScreenPortrait();
////                            ((HomeListManager) mListManager).setIsExpand(false);
////                            ((HomeListManager) mListManager).setScreenPortrait(VitamioRender.this,indexPostion);
////                        }
////                    }
//                }
//
//                @Override
//                public void onPlayFinish() {
//                    stopPlay();
//                }
//
//                @Override
//                public void onErrorCallBack() {
//                    if (indexPostion != STOP_PLAY_POSITION) {
//                        stopPlay();
//                    }
//                }
//            });
//        }
//    }
//
//    public void seekToTime(long Time) {
//        if (mVideoView != null) {
//            mVideoView.seekTo(Time);
//        }
//    }
//
//    public int getCurrentPosition() {
//        long position = 0;
//        position = mVideoView.getCurrentPosition();
//        return (int) position;
//    }
//    public boolean isPlaying()
//    {
//        return  mVideoView.isPlaying();
//    }
//
//    public void setScreenLandscape()
//    {
//        setViewLayoutFullScreen(videolayout);
//        mVideoView.setScreenLandscape();
//        mTitleTv.setVisibility(View.GONE);
//        setViewLayoutFullScreen(contentView);
//        mConvertView.findViewById(R.id.bottomView).setVisibility(View.GONE);
//    }
//    public void setScreenPortrait() {
//        setViewLayoutFullScreen(videolayout);
//        mVideoView.setScreenPortrait();
//        mTitleTv.setVisibility(View.VISIBLE);
//        mConvertView.findViewById(R.id.bottomView).setVisibility(View.VISIBLE);
//    }
//}
