package com.zhongsou.souyue.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.ZSImageLoader;
import com.facebook.drawee.view.ZSImageOptions;
import com.facebook.drawee.view.ZSImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.BaseBottomViewRender;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.ListViewPostsAdapterNew;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.circle.model.Reply;
import com.zhongsou.souyue.circle.model.VideoAboutResult;
import com.zhongsou.souyue.circle.ui.CPairSecondListView;
import com.zhongsou.souyue.circle.ui.HeaderGridView;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.ui.VideoDetailGridView;
import com.zhongsou.souyue.circle.util.OnChangeListener;
import com.zhongsou.souyue.circle.view.CircleFollowDialogNew;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.dialog.WXShareEnveDialog;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.im.util.DensityUtil;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.DetailItem;
import com.zhongsou.souyue.module.FootItemBeanSer;
import com.zhongsou.souyue.module.VideoDetailItem;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.detail.AddCommentDownReq;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.detail.AddFavorite2Req;
import com.zhongsou.souyue.net.detail.NewCommentListRequest;
import com.zhongsou.souyue.net.detail.VideoAboutListReq;
import com.zhongsou.souyue.net.volley.CDetailHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.receiver.HomeListener;
import com.zhongsou.souyue.receiver.ScreenListener;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.NetChangeDialog;
import com.zhongsou.souyue.ui.PairScrollView;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.AnoyomousUtils;
import com.zhongsou.souyue.utils.CVariableKVO;
import com.zhongsou.souyue.utils.CollectionUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.view.ZSVideoConst;
import com.zhongsou.souyue.view.videos.ZSMediaManager;
import com.zhongsou.souyue.view.videos.ZSVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 视频详情页
 * @auther: qubian
 * @data: 2016/3/18.
 */
public class VideoDetailActivity extends BaseActivity implements View.OnClickListener, PickerMethod {
    public static final long PAGE_SIZE_5 = 5; //每页数据
    public static final int DEVICE_COME_FROM = 3;// 来自搜悦客户端
    private String netUrl;
    private String videoUrl;
    private String imageUrl;
    private int palyPosition;
    private View loadView;
    //    private View loadAboutView;
    private ListView commentListView;
    private ProgressBarHelper list_progress;
    //    private ProgressBarHelper list_about_progress;// 相关视频加载过程的动画
    private HeaderGridView aboutGridView;
    private PairScrollView pairScrollView;
    private CPairSecondListView mPairSecond;
    private VideoDetailGridView mPairGridView;
    private ListViewPostsAdapterNew commentAdapter;
    private List<CommentsForCircleAndNews> postsList;
    private List<CommentsForCircleAndNews> postsListHot;
    private LinearLayout mHaveNoComment;
    private String mKeyword;// 分享的关键字
    private String mSrpId;// 分享的关键字
    private long last_sort_num;// 上一页最后一个跟帖的blog_id
    private long mBlogId;
    private String mTitle;
    private VideoDetailItem detailItem;
    private boolean isLoadAll;
    private boolean needLoad;
    private int visibleLast;
    private GridViewAdapter gridViewAdapter;
    private View mGridHeaderView;
    private int pno = 1;
    private LinearLayout circle_bottom_bar;// 底部栏
    private CircleFollowDialogNew circleFollowDialog;
    private ShareMenuDialog mCircleShareMenuDialog;
    private Uri imageFileUri;
    private boolean mHasUp;
    private boolean mHasDown;
    private boolean mHasFavorited;
    private int mUpCount;
    private int mDownCount;
    private int mCommentCount;
    private ImageButton collect_imagebutton, ding_imagebutton,
            share_imagebutton;
    private TextView follow_post_count;// 跟帖的个数
    private TextView ding_count;// 赞的个数
    private String shortUrl;
    private long mInterestId;
    //    private View mHaveNoAboutView; // 相关视频不存在的时候
    private int mShowComment;
    private CVariableKVO mDoneKvo;
    private long id;
    private boolean isPlaying = false;
    private boolean mUpDowning; // 正在点赞，就是防止动画重复
    private RelativeLayout videolayout;
    private boolean mHasCommentDismiss; // 评论框是否消失
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_videodetail);
        initView();
        intData();
        dealBiliVideo();
//        dealVitamio();
        addGridViewHead();
        initGridView();
        getAboutVideoListData();
        addListViewHead();
        initCommentListData();
        getCommentList(true);
        bindListener();
        setViewData(detailItem.getFootView());
        setUpdateReciever();
        setHomeListener();
        setScreenListener();
    }
    private ZSVideoPlayer videoView;
//    private ZSVitamioVideoView videoView;
//    private ZSImageView imageView;
//    private ImageView controllerView;

//    private void dealVitamio() {
//    imageView = findView(R.id.image);
//    controllerView = findView(R.id.controller);
//    controllerView.setOnClickListener(this);
//        if (palyPosition != 0 && palyPosition != -1) {
//            loadVideoView();
//        } else {
//            imageView.setImageURL(imageUrl, ZSImageOptions.getDefaultConfigList(mContext, R.drawable.default_gif), null);
//            videoView.setVisibility(View.GONE);
//            imageView.setVisibility(View.VISIBLE);
//            controllerView.setVisibility(View.VISIBLE);
//        }
//    }

    /**
     * 加载 videoview
     */
//    private void loadVideoView() {
//        isPlaying = true;
//        videoView.setUp(videoUrl, "", imageUrl, "");
//    }

//    private void loadVideoView() {
//        isPlaying = true;
////        mediaPlayRelease();
//        isViewPlayController(true);
//        videoView.loadAndPlay(null, videoUrl, palyPosition, false);
//        videoView.setVideoPlayCallback(new ZSVitamioVideoView.VideoPlayCallbackImpl() {
//
//            @Override
//            public void onSwitchPageType() {
//                swith();
//            }
//
//            @Override
//            public void onPlayFinish() {
//                closeVideo();
//            }
//
//            @Override
//            public void onErrorCallBack() {
//                if (!NetWorkUtils.isNetworkAvailable()) {
//                    releasePlay();
//                }
//            }
//
//            @Override
//            public void onCloseVideo() {
//                closeVideo();
//            }
//
//            private void closeVideo() {
//                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                    setScreenPortrait();
//                }
//                imageView.setVisibility(View.VISIBLE);
//                controllerView.setVisibility(View.VISIBLE);
//                videoView.setVisibility(View.GONE);
//                mediaPlayPause();
//            }
//        });
//    }
    private void isViewPlayController(boolean isPlay)
    {
//        imageView.setVisibility(isPlay ? View.GONE : View.VISIBLE);
//        controllerView.setVisibility(isPlay ? View.GONE : View.VISIBLE);
//        videoView.setVisibility(isPlay ? View.VISIBLE : View.GONE);
    }
    private void dealBiliVideo()
    {

        ZSMediaManager.intance(this).mediaPlayPause();
        videoView.setVisibility(View.VISIBLE);
        videoView.setUp(videoUrl, "", imageUrl, "");
        videoView.setCallBack(new ZSVideoPlayer.ZSVideoPlayCallBack() {
            @Override
            public void dealClick() {

            }

            @Override
            public boolean expand() {
                swith();
                return true;
            }

            @Override
            public void dealError() {
                stopPlay();
                goback();
            }
        });

    }

    /**
     * 网络获取 相关视频数据
     */
    private void getAboutVideoListData() {
        VideoAboutListReq req = new VideoAboutListReq(HttpCommon.DETAIL_VIDEO_ABOUT, this);
        req.setParams(id + "", SYUserManager.getInstance().getUser().userId());
        CMainHttp.getInstance().doRequest(req);
    }

    /**
     * 初始化 gridview
     */
    private void initGridView() {
        gridViewAdapter = new GridViewAdapter();
        aboutGridView.setAdapter(gridViewAdapter);

    }

    /**
     * 加载 gridview 的 头部数据
     */
    private void addGridViewHead() {
        if (mGridHeaderView != null) {
            return;
        }
        mGridHeaderView = View.inflate(this, R.layout.videodetail_headview, null);
        mGridHeaderView.findViewById(R.id.share_wchat_frient).setOnClickListener(this);
        mGridHeaderView.findViewById(R.id.share_wchat_frientcircle).setOnClickListener(this);
        mGridHeaderView.findViewById(R.id.share_getprize).setOnClickListener(this);
        ((TextView) mGridHeaderView.findViewById(R.id.videod_title)).setText(mTitle);
        aboutGridView.addHeaderView(mGridHeaderView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == 2 && resultCode != RESULT_CANCELED) { // 从相机回来
            String picPath = null;
            if (imageFileUri != null) {
                picPath = Utils.getPicPathFromUri(imageFileUri,
                        VideoDetailActivity.this);
                int degree = 0;
                if (!StringUtils.isEmpty(picPath))
                    degree = ImageUtil.readPictureDegree(picPath);
                Matrix matrix = new Matrix();
                if (degree != 0) {// 解决旋转问题
                    matrix.preRotate(degree);
                }
                ArrayList<String> list = new ArrayList<String>();
                list.add(picPath);
                circleFollowDialog.addImagePath(list);

            } else {
                Toast.makeText(this, R.string.self_get_image_error,
                        Toast.LENGTH_LONG).show();
            }
        }
        if (resultCode == UIHelper.RESULT_CODE_REPLY) { // 回复后更新回复列表
            long mBlogId = data.getLongExtra("comment_id", 0);
            ArrayList<Reply> newReplyList = (ArrayList<Reply>) data
                    .getSerializableExtra("newReplyList");
            for (CommentsForCircleAndNews p : postsList) {
                if (p.getComment_id() == mBlogId) {
                    p.setReplyList(newReplyList);
                    commentAdapter.notifyDataSetChanged();
                }
            }
        }
        if (resultCode == 0x200) { // 从相册回来
            List<String> list = data.getStringArrayListExtra("imgseldata");
            circleFollowDialog.addImagePath(list);
        }
    }

    private void showAddImgMenu() {
        initAddImgLayout();
        showDialogAddImg = showAlert(this, addIMgView, Gravity.BOTTOM);
    }

    private ViewGroup addIMgView;
    private Dialog showDialogAddImg;

    private void initAddImgLayout() {
        if (addIMgView != null) {
            return;
        }
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        addIMgView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.circle_follow_add_img_menu, null, false);
        TextView textView_xiangce = (TextView) addIMgView
                .findViewById(R.id.textView_xiangce);
        TextView textView_photo = (TextView) addIMgView
                .findViewById(R.id.textView_photo);
        TextView textView_cancel = (TextView) addIMgView
                .findViewById(R.id.textView_cancel);
        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
            }
        });
        textView_xiangce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                releasePlay();
                IntentUtil.jumpImgGroup(VideoDetailActivity.this,
                        circleFollowDialog.getImgLen());
            }
        });
        textView_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddImg.dismiss();
                jumpTakePhoto();
            }
        });
    }

    /**
     * 初始化 评论列表数据
     */
    private void initCommentListData() {
        postsList = new ArrayList<CommentsForCircleAndNews>();
        postsListHot = new ArrayList<CommentsForCircleAndNews>();
        commentAdapter = new ListViewPostsAdapterNew(this, postsList, postsListHot,
                mBlogId, mInterestId, DEVICE_COME_FROM);
        commentListView.setAdapter(commentAdapter);
        commentAdapter.setMain_title(mTitle);
        commentAdapter.setmUrl(netUrl);
        if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) {
            commentAdapter.setNickName("游客");
        } else {
            commentAdapter.setNickName(SYUserManager.getInstance().getName());
        }

    }

    /**
     * 初始化数据
     */
    private void intData() {
        detailItem = (VideoDetailItem) getIntent().getSerializableExtra("VideoDetailItem");
        videoUrl = detailItem.getVideoUrl();
        netUrl = detailItem.getNetUrl();
        palyPosition = detailItem.getPalyPosition();
        mKeyword = detailItem.getKeyword();
        mSrpId = detailItem.getSrpId();
        mBlogId = detailItem.getmBlogId();
        mTitle = detailItem.getTitle();
        imageUrl = detailItem.getImageUrl();
        mInterestId = detailItem.getmInterestId();
        mShowComment = detailItem.getSkip();
        id = detailItem.getId();
        doShowComment();
    }

    private void doShowComment() {
        mHasCommentDismiss = true;
        mDoneKvo = new CVariableKVO(1, new CVariableKVO.KVOCallback() {
            @Override
            public void doCallback() {
                if (mShowComment == DetailItem.SKIP_TO_COMMENT) {
                    mShowComment = 0;
                    commentListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pairScrollView.scrollToSecondView();
                        }
                    }, 300);
                }
                mHaveNoComment.setEnabled(true);
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        videolayout = findView(R.id.videolayout);
        videoView = findView(R.id.videoView);

        commentListView = findView(R.id.listView);
        mPairSecond = findView(R.id.pair_second);
        mPairSecond.init(this);
        mPairGridView = findView(R.id.videoGridview);
        mPairGridView.init(this);
        aboutGridView = findView(R.id.gridView);
        loadView = findView(R.id.list_loading);
        loadView.setBackgroundColor(0x00000000);
        list_progress = new ProgressBarHelper(this, loadView);
        list_progress
                .setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                    @Override
                    public void clickRefresh() {
                        list_progress.showLoading();
                        getCommentList(false);
                    }
                });
        list_progress.goneLoading();
        mHaveNoComment = findView(R.id.detail_have_no_comment);
        mHaveNoComment.setOnClickListener(this);
        mHaveNoComment.setVisibility(View.INVISIBLE);
        mHaveNoComment.setEnabled(false);
        pairScrollView = (PairScrollView) findViewById(R.id.pair_scroll);
        pairScrollView.setmTouchEnable(true);
        pairScrollView.setVisibility(View.VISIBLE);
        circle_bottom_bar = (LinearLayout) this.findViewById(R.id.ll_circle_post_bottom_bar);
        follow_post_count = (TextView) this
                .findViewById(R.id.follow_post_count);
        collect_imagebutton = (ImageButton) this
                .findViewById(R.id.collect_imagebutton);
        ding_imagebutton = (ImageButton) this
                .findViewById(R.id.ding_imagebutton);
        share_imagebutton = (ImageButton) this
                .findViewById(R.id.share_imagebutton);
        ding_count = (TextView) this.findViewById(R.id.ding_count);
//        loadAboutView = findView(R.id.list_loading_about);
//        loadAboutView.setBackgroundColor(0x00000000);
//        list_about_progress = new ProgressBarHelper(this, loadAboutView);
//        list_about_progress
//                .setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
//                    @Override
//                    public void clickRefresh() {
//                        list_about_progress.showLoading();
//                        getAboutVideoListData();
//                    }
//                });
//        mHaveNoAboutView = findView(R.id.detail_have_no_data);
    }


    private void mediaPlayPause()
    {
        videoView.pausePlay();
//        if(videoView.isPlaying())
//        {
//            videoView.pausePlay();
//        }
    }
    private void mediaPlayStart()
    {
//        videoView.startPlay();
        videoView.start();
    }
    private void mediaPlayResume()
    {
//        videoView.startPlay();
//        videoView.resume();
        videoView.releasePlay();
    }

    private void mediaPlayRelease()
    {
        videoView.releasePlay();
    }

    private boolean mediaPlayIsPlaying()
    {
        return ZSMediaManager.intance(this).mediaPlayIsPlaying();
//        return  videoView.isPlaying();
    }
    private void mediaPlaySeekTo(int seek)
    {
        ZSMediaManager.intance(this).mediaPlaySeekTo(seek);
//        videoView.seekTo(seek);
    }

    private long getCurrentDuration()
    {
        return ZSMediaManager.intance(this).getCurrentPosition();
//        return  videoView.getCurrentPosition();
    }


    private void swith() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setScreenPortrait();
        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setScreenLandscape();
        }
    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            circle_bottom_bar.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2pxX(ZSVideoConst.VIDEO_HEIGHT));
//            videolayout.setLayoutParams(lp);
////            videoView.setLayoutParams(lp);
//            videoView.setScreenPortrait();
//        }else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//            circle_bottom_bar.setVisibility(View.GONE);
//            UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_play_fullscreen, mSrpId);
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            videolayout.setLayoutParams(lp);
////            videoView.setLayoutParams(lp);
//            videoView.setScreenLandscape();
//        }
//    }

    /**
     * 设置全屏
     */
    private void setScreenLandscape()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videolayout.setLayoutParams(lp);
        videolayout.requestLayout();
        videoView.setScreenLandscape();
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        circle_bottom_bar.setVisibility(View.GONE);
        UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_play_fullscreen, mSrpId);
    }
    /**
     * 设置竖屏
     */
    private void setScreenPortrait()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        WindowManager.LayoutParams attrs = getWindow().getAttributes();
//        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2pxX(ZSVideoConst.VIDEO_HEIGHT));
        videolayout.setLayoutParams(lp);
        videolayout.requestLayout();
        videoView.setScreenPortrait();
        circle_bottom_bar.setVisibility(View.VISIBLE);
    }

    private void goback()
    {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setScreenPortrait();
        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mediaPlayRelease();
        }
    }
    private void stopPlay()
    {
        isPlaying= false;
        mediaPlayRelease();
//        mediaPlayPause();
        isViewPlayController(false);
    }
    private void releasePlay()
    {
        isPlaying= false;
        mediaPlayRelease();
        isViewPlayController(false);
    }


    /**
     * 网络获取 评论列表数据
     *
     * @param _ispull
     */
    private void getCommentList(boolean _ispull) {
        if (isLoadAll) {
            if (list_progress.isLoading) {
                list_progress.goneLoading();
            }
            UIHelper.ToastMessage(this, "已全部加载");
            needLoad = false;
            return;
        }
        if (commentListView.getFooterViewsCount() == 0 && _ispull) {
            View v = getFootView();
            v.setVisibility(View.VISIBLE);
            commentListView.addFooterView(footerView);
        }
        NewCommentListRequest request = new NewCommentListRequest(HttpCommon.DETAIL_COMMENT_NEW_LIST_ID, this);
        request.setParams(netUrl, DEVICE_COME_FROM, last_sort_num, mSrpId, mKeyword, 1);
        mMainHttp.doRequest(request);
    }


    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("position", getCurrentDuration());
        setResult(RESULT_OK, intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isPlaying)
        {
            mediaPlayResume();
        }
        setUpdateReciever();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setScreenPortrait();
        }
        cancelUpdateReciever();
        if(isPlaying)
        {
            try
            {
                stopPlay();
            }catch (Exception e)
            {
                e.printStackTrace();
                stopPlay();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayRelease();
        cancelHomeListener();
        cancelUpdateReciever();
        cancelScreenListener();
    }

    private View mListHeaderView;
    private TextView mHeadUpCount, mHeadDownCount, mHeadPayCount;
    private ImageView mHeadUpIcon, mHeadDownIcon;

    /**
     * 评论列表的头部数据
     */
    private void addListViewHead() {
        if (mListHeaderView != null) {
            return;
        }
        mListHeaderView = View
                .inflate(this, R.layout.detail_comment_head, null);
        mHeadUpIcon = (ImageView) mListHeaderView
                .findViewById(R.id.detail_up_icon);
        mHeadDownIcon = (ImageView) mListHeaderView
                .findViewById(R.id.detail_down_icon);

        mHeadUpCount = (TextView) mListHeaderView
                .findViewById(R.id.detail_up_count);
        mHeadDownCount = (TextView) mListHeaderView
                .findViewById(R.id.detail_down_count);
        mHeadPayCount = (TextView) mListHeaderView
                .findViewById(R.id.detail_pay_count);

        mListHeaderView.findViewById(R.id.detail_up).setOnClickListener(this);
        mListHeaderView.findViewById(R.id.detail_down).setOnClickListener(this);
        mListHeaderView.findViewById(R.id.detail_pay).setVisibility(View.GONE);
        mHeadUpCount.setText("");
        mHeadDownCount.setText("");
        mHeadPayCount.setText("");
        commentListView.addHeaderView(mListHeaderView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK)
        {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setScreenPortrait();
                return true;
            } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mediaPlayRelease();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_wchat_frient:
                loadData(ShareMenuDialog.SHARE_TO_WEIX);
                break;
            case R.id.share_wchat_frientcircle:
                loadData(ShareMenuDialog.SHARE_TO_FRIENDS);
                break;
            case R.id.share_getprize:
                getSharePrize();
                break;
            case R.id.backIv:
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setScreenPortrait();
                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    mediaPlayRelease();
                    finish();
                }
                break;
            case R.id.follow_post_layout:
                showComment();
                break;
            case R.id.ding_layout:
            case R.id.detail_up:
                if (mHasUp) {
                    Toast.makeText(VideoDetailActivity.this, R.string.detail_have_ding,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!mUpDowning){
                    mUpDowning= true;
                    AddCommentUpReq req = new AddCommentUpReq(HttpCommon.DETAIL_ADDUP_ID, this);
                    req.setParams(mKeyword, mSrpId, netUrl, SYUserManager
                                    .getInstance().getToken(), DEVICE_COME_FROM,
                            1, 0, mTitle, "", "", "", "", mBlogId);
                    CMainHttp.getInstance().doRequest(req);
                    UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_up, mSrpId);
                }
                break;
            case R.id.collect_imagebutton:
                if (netUrl != null) {
                    if (!mHasFavorited) {
                        UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_favorate, mSrpId);

                        AddFavorite2Req req = new AddFavorite2Req(HttpCommon.DETAIL_ADDFAVORITE_ID, this);
                        req.setParams("" + 1,
                                netUrl, SYUserManager.getInstance().getToken(),
                                DEVICE_COME_FROM, mSrpId, mKeyword, mTitle,
                                imageUrl);
                        CMainHttp.getInstance().doRequest(req);
                    } else {
                        UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_favorate_cancle, mSrpId);
                        CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID, this);
                        req.setParamsForOpenFlag(SYUserManager.getInstance().getToken(),
                                netUrl, 1, DEVICE_COME_FROM);
                        CMainHttp.getInstance().doRequest(req);
                    }
                }
                break;
            case R.id.share_imagebutton:
                showShareWindow();
                UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_share, mSrpId);
                break;
            case R.id.detail_down:
                if (mHasDown) {
                    Toast.makeText(VideoDetailActivity.this, R.string.detail_have_cai,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            {
                AddCommentDownReq req = new AddCommentDownReq(HttpCommon.DETAIL_ADDDOWN_ID, this);
                req.setParams(mKeyword, mSrpId, netUrl, SYUserManager
                                .getInstance().getToken(), DEVICE_COME_FROM,
                        1, mTitle, "", "", "", "");
                CMainHttp.getInstance().doRequest(req);
                UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_down, mSrpId);
            }
            break;
            case R.id.controller:
                if(StringUtils.isNotEmpty(videoUrl)&& (CMainHttp.getInstance().isNetworkAvailable(mContext)))
                {
                    UpEventAgent.onZSVideoEvent(mContext,UpEventAgent.video_detail_play,mSrpId);
//                    loadVideoView();
                }
                break;
            case R.id.detail_have_no_comment:
                if (mHasCommentDismiss) {
                    showComment();
                }
                break;

        }

    }

    private WXShareEnveDialog mWxShareDlg;

    private void getSharePrize() {
        if (mWxShareDlg == null) {
            mWxShareDlg = new WXShareEnveDialog(this);
        }
        if (mWxShareDlg.isShowing()) {
            mWxShareDlg.dismiss();
        }
        mWxShareDlg.show();
    }

    private void showShareWindow() {
        mCircleShareMenuDialog = new ShareMenuDialog(mContext, this,
                ShareConstantsUtils.VIDEO);
        mCircleShareMenuDialog.showBottonDialog();
    }

    private void showComment() {
        commentListView.setSelection(0);
        commentListView.invalidate();

        pairScrollView.scrollToSecondView();
        circle_bottom_bar.setVisibility(View.INVISIBLE);
        commentListView.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    showCommentDialog();
                }
            }
        });
    }

    private StringBuilder mUrl = new StringBuilder();

    private void showCommentDialog() {
        circleFollowDialog = new CircleFollowDialogNew(
                mContext, this, netUrl,
                DEVICE_COME_FROM, mSrpId, mKeyword, null);
//        circleFollowDialog.setmInterestId(mInterestId + "");
        circleFollowDialog.setMain_title(mTitle);
//        circleFollowDialog.setMain_decsription(mContent);
//        circleFollowDialog.setMain_images(imgs);
//        circleFollowDialog.mRole = mOptionRoleType;
//        circleFollowDialog.setDetailType(mDetailType);
        circleFollowDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (circleFollowDialog != null)
                    circleFollowDialog.saveInfo(mUrl.toString());
            }
        });
        circleFollowDialog.setListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                if (circleFollowDialog.getImgLen() == 0) {
                    releasePlay();
                    IntentUtil.jumpImgGroup(VideoDetailActivity.this,
                            circleFollowDialog.getImgLen());
                }
            }
        });
        circleFollowDialog.setPhotoListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                jumpTakePhoto();
            }
        });
        circleFollowDialog.setAddImgListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                showAddImgMenu();
            }
        });
        circleFollowDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        pairScrollView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mHasCommentDismiss = true;
                                circle_bottom_bar.setVisibility(View.VISIBLE);
                                pairScrollView.setmInputOut(false);
                                commentAdapter.notifyDataSetChanged();
                                if (commentAdapter.getCount() == 0) {
                                    mHaveNoComment.setVisibility(View.VISIBLE);
                                } else {
                                    mHaveNoComment.setVisibility(View.GONE);
                                }
                            }
                        }, 500);
                        circleFollowDialog.hideKeyboard();
                    }
                });
        mHasCommentDismiss = false;
        pairScrollView.setmInputOut(true);
        circleFollowDialog.showDialog();
        circleFollowDialog.setEditText(SYSharedPreferences.getInstance()
                .getString(mUrl + "_text", ""));
        String strImg = SYSharedPreferences.getInstance().getString(
                mUrl + "_img", "");
        if (strImg != null && !strImg.equals("")) {
            List<String> list = new Gson().fromJson(strImg, new TypeToken<List<String>>() {
            }.getType());
            if (list != null && list.size() != 0) {
                circleFollowDialog.addImagePath(list);
            }
        }
    }

    private void jumpTakePhoto() {
        if (circleFollowDialog.getImgLen() > 9) {
            Toast.makeText(this, "最多选择9张图片", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            imageFileUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
            if (imageFileUri != null) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                if (Utils.isIntentSafe(VideoDetailActivity.this, i)) {
                    startActivityForResult(i, 2);
                } else {
                    SouYueToast.makeText(VideoDetailActivity.this,
                            getString(R.string.dont_have_camera_app),
                            SouYueToast.LENGTH_SHORT).show();
                }
            } else {
                SouYueToast.makeText(VideoDetailActivity.this,
                        getString(R.string.cant_insert_album),
                        SouYueToast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            SouYueToast.makeText(VideoDetailActivity.this,
                    getString(R.string.cant_insert_album),
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loadData(int position) {
        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        stopPlay();
        if (CMainHttp.getInstance().isNetworkAvailable(mContext)) {
            ShareContent content = getNewsShareContent();
            stopPlay();
            doShareNews(position, content);
        } else {
            SouYueToast.makeText(mContext,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    private String mChannel = "视频";
    private SsoHandler mSsoHandler;

    private void doShareNews(int position, ShareContent content) {
        switch (position) {
            case ShareMenuDialog.SHARE_TO_INTEREST:
                LoginAlert loginDialog = new LoginAlert(mContext,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
                                interestmodel.setTitle(mTitle);
                                List<String> images = new ArrayList<>();
                                images.add(imageUrl);
                                interestmodel.setImages(images);
                                interestmodel.setKeyword(mKeyword);
                                interestmodel.setSrpId(mSrpId);
                                interestmodel.setChannel(mChannel);
                                interestmodel.setBrief(mTitle);
                                interestmodel.setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPEHTML);
                                interestmodel.setNewsUrl(shortUrl);
                                UIHelper.shareToInterest(mContext, interestmodel, mInterestId);
                            }
                        }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                loginDialog.show();
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        videoUrl, "sina_wb");
                mSsoHandler = ShareByWeibo.getInstance().share(VideoDetailActivity.this,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        videoUrl, "wx");// 点击分享了此处加统计
                ShareByWeixin.getInstance().share(content, false);
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                String wxFriendUrl = content.getUrl();
                if (null != wxFriendUrl
                        && wxFriendUrl.contains("urlContent.groovy?")) {
                    wxFriendUrl = wxFriendUrl.replace(
                            "urlContent.groovy?",
                            "urlContent.groovy?keyword="
                                    + StringUtils.enCodeRUL(mKeyword) + "&mSrpId="
                                    + mSrpId + "&");
                }
                content.setUrl(wxFriendUrl);
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        videoUrl, "friend");
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        videoUrl, "qfriend");
                content.setContent("");
                ShareByTencentQQ.getInstance().share(VideoDetailActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        videoUrl, "qzone");
                content.setContent("");
                ShareByTencentQQZone.getInstance().share(VideoDetailActivity.this,
                        content);
                break;

            default:
                break;
        }
    }

    public ShareContent getNewsShareContent() {
        getImage();
        ShareContent result = new ShareContent(mTitle, shortUrl, imageBitmap, mTitle, imageUrl);
        result.setSharePointUrl(shortUrl == null ? "" : shortUrl);
        result.setKeyword(mKeyword);
        result.setSrpId(mSrpId);
        return result;
    }

    private Bitmap imageBitmap;

    private void getImage() {
        if (!TextUtils.isEmpty(imageUrl)) {
            if(imageBitmap==null)
            {
                imageBitmap = ZSImageLoader.getImage(imageUrl);
            }
            if (imageBitmap == null) {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, imageUrl, new ImageView(this), MyDisplayImageOption.bigoptions);
                try {
                    File cache = ImageLoader.getInstance().getDiskCache().get(imageUrl);
                    String path = "";
                    if (cache != null) {
                        path = cache.getAbsolutePath();
                    }
                    imageBitmap = ImageUtil.getSmallBitmap(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private class GridViewAdapter extends BaseAdapter {
        List<VideoAboutResult> listData;

        public GridViewAdapter() {
            this.listData = new ArrayList<>();
        }

        public void setListData(List<VideoAboutResult> listData) {
            this.listData = listData;
        }

        public List<VideoAboutResult> getListData() {
            return listData;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VideoAboutHolder holder = null;
            if (convertView == null) {
                holder = new VideoAboutHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.video_grid_item, null);
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (VideoAboutHolder) convertView.getTag();
            }
            holder.bindData(listData.get(position));
            if (position == 0) {
                holder.aboutVideoTv.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                holder.aboutVideoTv.setVisibility(View.INVISIBLE);
            } else {
                holder.aboutVideoTv.setVisibility(View.GONE);
            }
            return convertView;
        }
    }


    class VideoAboutHolder implements View.OnClickListener{
        VideoAboutResult result;
        ZSImageView imageView;
        TextView timeTv;
        TextView aboutVideoTv;
        TextView titleTv;
        CheckBox playCountCb;
        View clickView;
        public void findView(View convertView) {
            imageView = (ZSImageView) convertView.findViewById(R.id.image);
            titleTv = (TextView) convertView.findViewById(R.id.title);
            aboutVideoTv = (TextView) convertView.findViewById(R.id.aboutvideos);
            timeTv = (TextView) convertView.findViewById(R.id.time);
            playCountCb = (CheckBox) convertView.findViewById(R.id.videopalycount);
            clickView  = convertView.findViewById(R.id.clickView);
            clickView.setOnClickListener(this);
        }

        public void bindData(VideoAboutResult bean) {
            result=bean;
            titleTv.setText(bean.getTitle());
            timeTv.setText(bean.getDuration());
            List<String> images = bean.getImage();
            String imageUrl = "";
            if (images != null && images.size() > 0) {
                imageUrl = images.get(0);
            }
            imageView.setImageURL(imageUrl, ZSImageOptions.getDefaultConfig(VideoDetailActivity.this, R.drawable.default_small), null);
            imageView.setAspectRatio(1.6f);
        }

        @Override
        public void onClick(View v) {

            if(result!=null)
            {
                IntentUtil.gotoVideoDetail(mContext, result);
                releasePlay();
                UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_relate_item_click, mSrpId);
            }
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        IHttpError error = request.getVolleyError();
        int id = request.getmId();
        switch (id) {
            case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID:
                commentListView.removeFooterView(footerView);
                if (pno > 1) {
                    SouYueToast.makeText(this, "网络异常，请重试！",
                            Toast.LENGTH_SHORT).show();
                    needLoad = true;
                } else {
                    list_progress.showNetError();
                }
                break;
            case HttpCommon.DETAIL_VIDEO_ABOUT:
//                list_about_progress.showNetError();
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                if (error.getErrorCode() < 700) {
                    UIHelper.ToastMessage(this, "收藏失败");
                }
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                break;
            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                if (circleFollowDialog != null) {
                    circleFollowDialog.dismissProcessDialog();
                }
                if (error.getErrorCode() < 700) {
                    Toast.makeText(this, "评论失败", Toast.LENGTH_SHORT).show();
                }
                UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_comment, mSrpId);
                break;
            case HttpCommon.DETAIL_ADDDOWN_ID:
            case HttpCommon.DETAIL_ADDUP_ID:
                mUpDowning = false;
                if (error.getErrorCode() == 700){
                    SouYueToast.makeText(this, error.getmErrorMessage(), Toast.LENGTH_LONG).show();
                }else
                {
                    SouYueToast.makeText(this, R.string.networkerror, Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        int id = request.getmId();
        switch (id) {
            case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID:
                List<Object> comls = (List<Object>) request.getResponse();
                getCommentListSuccess((List<CommentsForCircleAndNews>) comls.get(CDetailHttp.DETAIL_COMMENT_LIST_COMMENT),
                        (List<CommentsForCircleAndNews>) comls.get(CDetailHttp.DETAIL_COMMENT_LIST_HOT));
                commentListView.postInvalidate();
                break;
            case HttpCommon.DETAIL_VIDEO_ABOUT:
                List<Object> abouts = (List<Object>) request.getResponse();
                shortUrl = (String) abouts.get(VideoAboutListReq.VIDEO_DETAIL_LIST_SHORT_URL);
                FootItemBean footView = (FootItemBean) abouts.get(VideoAboutListReq.VIDEO_DETAIL_LIST_FOOTVIEW);
                setViewData(VideoDetailItem.setFoot(footView));
                getAboutVideoListDataSuccess((List<VideoAboutResult>) abouts.get(VideoAboutListReq.VIDEO_DETAIL_LIST_NEWS));
                aboutGridView.postInvalidate();
                break;
            case HttpCommon.DETAIL_ADDUP_ID:
                commentUpSuccess((BaseBottomViewRender) request.getKeyValueTag("render"),
                        (BaseListData) request.getKeyValueTag("data"), request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                newFavoriteAddSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                cancelCollectSuccess(request.<HttpJsonResponse>getResponse());
                break;

            case HttpCommon.DETAIL_COMMENTDETAIL_ID:
                commentDetailSuccess(request.<HttpJsonResponse>getResponse());
                UpEventAgent.onZSVideoEvent(mContext, UpEventAgent.video_detail_comment, mSrpId);
                break;
            case HttpCommon.DETAIL_ADDDOWN_ID:
                commentDownSuccess(request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    private void commentDownSuccess(HttpJsonResponse response) {
        mHasDown = true;
        mDownCount++;
        mHeadDownCount.setText(mDownCount + "");
        mHeadDownIcon.setImageResource(R.drawable.detail_down_red);
        mHeadDownCount.setTextColor(getResources().getColor(R.color.detail_red));
    }

    private void commentDetailSuccess(HttpJsonResponse response) {
        UIHelper.ToastMessage(this, R.string.comment_detail_success);
        // 成功后清空保存的数据
        SYSharedPreferences.getInstance().putString(mUrl + "_text", "");
        SYSharedPreferences.getInstance().putString(mUrl + "_img", "");
        CommentsForCircleAndNews published = circleFollowDialog.getPublishPosts();
        long comment_id = 0;
        try {
            comment_id = response.getBody().get("comment_id").getAsLong();
        } catch (Exception e) {
            //错误处理
            UIHelper.ToastMessage(this, "评论失败");
            circleFollowDialog.dismissProcessDialog();
            return;
        }
        published.setContent(circleFollowDialog.getPublishPosts().getContent());
        published.setCreate_time(circleFollowDialog.getPublishPosts().getCreate_time());
        published.setComment_id(comment_id);
        follow_post_count.setText(Integer.parseInt(follow_post_count.getText()
                .toString()) + 1 + "");
        published.setImage_url(SYUserManager.getInstance().getImage());
        if (AnoyomousUtils.getAnoyomouState(mInterestId + "")) {
            published.setNickname("匿名用户");
            published.setIs_anonymity(1);
            published.setImage_url("");
        } else if (SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_GUEST)) {
            published.setNickname(getResources().getString(R.string.user_guest));
        } else {
            published.setNickname(SYUserManager.getInstance().getName());
        }
        published.setGood_num("0");
        published.setSrp_id(mSrpId);
        published.setType(1);
        published.setRole(SouyueAPIManager.isLogin()?2:3);
        published.setIs_current_comment(1);
        circleFollowDialog.dismissProcessDialog();
        postsList.add(postsListHot.size(), published);
        commentAdapter.notifyDataSetChanged();
        mHaveNoComment.setVisibility(View.INVISIBLE);
        UpEventAgent.onNewsComment(this, mChannel, mKeyword, mSrpId,
                mTitle, videoUrl);

    }

    private void cancelCollectSuccess(HttpJsonResponse response) {
        SouYueToast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
        mHasFavorited = false;
        collect_imagebutton.setEnabled(true);
        collect_imagebutton.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_collect_normal));
        changeFavoriteStateBroadcast();
    }

    private void newFavoriteAddSuccess(HttpJsonResponse response) {
        SouYueToast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        mHasFavorited = true;
        collect_imagebutton.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_collect_unnormal));
        changeFavoriteStateBroadcast();
    }

    /**
     * 收藏状态改变广播，主要用来刷新收藏列表
     */
    private void changeFavoriteStateBroadcast() {
        Intent favIntent = new Intent();
        favIntent.setAction(MyFavoriteActivity.FAVORITE_ACTION);
        sendBroadcast(favIntent);
    }

    private void setViewData(FootItemBeanSer footview) {
        try {
            mUpCount = footview.getUpCount();
            mDownCount = footview.getDownCount();
            mCommentCount = footview.getCommentCount();
            mHasUp = footview.getIsUp() == 1;
            mHasDown = footview.getIsDown() == 1;
            mHasFavorited = footview.getIsFavorator() == 1;
            follow_post_count.setText(mCommentCount + "");
            ding_count.setText(mUpCount + "");
            mHeadUpCount.setText(mUpCount + "");
            mHeadDownCount.setText(mDownCount + "");
            if (mHasFavorited) {
                collect_imagebutton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.circle_collect_unnormal));
            } else {
                collect_imagebutton.setImageDrawable(getResources()
                        .getDrawable(R.drawable.circle_collect_normal));
            }
            if (mHasUp) {
                mHeadUpIcon.setImageResource(R.drawable.detail_up_red);
                mHeadUpCount.setTextColor(getResources().getColor(
                        R.color.detail_red));
                ding_imagebutton.setImageDrawable(getResources().getDrawable(
                        R.drawable.circle_up_unnormal));
            }
            if (mHasDown) {
                mHeadDownIcon.setImageResource(R.drawable.detail_down_red);
                mHeadDownCount.setTextColor(getResources().getColor(
                        R.color.detail_red));
            }
            if (!mHasDown && !mHasUp) {
                ding_imagebutton.setImageDrawable(getResources().getDrawable(
                        R.drawable.circle_up_normal));
                mHeadUpIcon.setImageResource(R.drawable.detail_up_blue);
                mHeadUpCount.setText(mUpCount + "");
                mHeadDownIcon.setImageResource(R.drawable.detail_down_blue);
                mHeadDownCount.setText(mDownCount + "");
                mHeadUpCount.setTextColor(getResources().getColor(
                        R.color.detail_blue));
                mHeadDownCount.setTextColor(getResources().getColor(
                        R.color.detail_blue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void commentUpSuccess(BaseBottomViewRender render, BaseListData data, HttpJsonResponse response) {

        mHasUp = true;
        mUpCount++;
        mUpDowning = false;
        mHeadUpCount.setText(mUpCount + "");
        mHeadUpCount.setTextColor(getResources().getColor(R.color.detail_red));
        mHeadUpIcon.setImageResource(R.drawable.detail_up_red);
        ding_count.setText(mUpCount + "");
        ding_imagebutton.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_up_unnormal));
    }

    private void getAboutVideoListDataSuccess(List<VideoAboutResult> videoAboutResults) {
//        list_about_progress.goneLoading();
        if (videoAboutResults != null && videoAboutResults.size() > 0) {
            gridViewAdapter.setListData(videoAboutResults);
            gridViewAdapter.notifyDataSetChanged();
//            mHaveNoAboutView.setVisibility(View.GONE);
        } else {
//            mHaveNoAboutView.setVisibility(View.VISIBLE);
        }

    }


    public void getCommentListSuccess(List<CommentsForCircleAndNews> listLatest, List<CommentsForCircleAndNews> listHot) {
        list_progress.goneLoading();
        if (!listHot.isEmpty()) {
            postsList.addAll(listHot);
            postsListHot.addAll(listHot);
        }
        if (!listLatest.isEmpty()) {
            last_sort_num = listLatest.get(listLatest.size() - 1)
                    .getComment_id();
            postsList.addAll(listLatest);
            commentAdapter.notifyDataSetChanged();
            pno++;
        }
        if (commentAdapter.getCount() == 0) {
            mHaveNoComment.setVisibility(View.VISIBLE);
            commentListView.removeFooterView(footerView);
            isLoadAll = true;
            needLoad = false;
        } else {
            if (CollectionUtils.isEmpty(listLatest)
                    || listLatest.size() < PAGE_SIZE_5) {
                isLoadAll = true;
                needLoad = false;
                commentListView.removeFooterView(footerView);
            } else {
                needLoad = true;
            }
            mHaveNoComment.setVisibility(View.GONE);
        }
        mDoneKvo.doDone();
    }

    private View footerView;

    private View getFootView() {
        if (footerView == null) {
            footerView = getLayoutInflater().inflate(
                    R.layout.ent_refresh_footer, null);
            footerView.setBackgroundColor(0xffffffff);
        }
        return footerView;
    }

    /**
     * 绑定视图 监听
     */
    private void bindListener() {
        findViewById(R.id.backIv).setOnClickListener(this);
        findViewById(R.id.follow_post_layout).setOnClickListener(this);
        findViewById(R.id.ding_layout).setOnClickListener(this);
        findViewById(R.id.collect_imagebutton).setOnClickListener(this);
        findViewById(R.id.share_imagebutton).setOnClickListener(this);

//        aboutGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                VideoAboutResult item = (VideoAboutResult) parent.getItemAtPosition(position);
//                if(item!=null)
//                {
//                    IntentUtil.gotoVideoDetail(mContext, item);
//                }
//
//            }
//        });
        commentAdapter.setChangeListener(new OnChangeListener() {

            @Override
            public void onChange(Object obj) {
                if (commentAdapter.getCount() == 0) {
                    mHaveNoComment.setVisibility(View.VISIBLE);
                    commentListView.removeFooterView(footerView);
                }
//                follow_post_count.setText(Integer.parseInt(follow_post_count
//                        .getText().toString()) - 1 + ""); // 帖子删除的时候，跟帖数减一
            }
        });
        commentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && visibleLast == commentAdapter.getCount() && needLoad) {
                    needLoad = false;
                    getCommentList(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (commentListView.getFooterViewsCount() == 0) {
                    visibleLast = firstVisibleItem + visibleItemCount - 1;
                } else {
                    visibleLast = firstVisibleItem + visibleItemCount - 2;
                }

            }
        });
    }

    public static Dialog showAlert(final Context context, ViewGroup content,
                                   int gravity) {
        if (content.getParent() != null) {
            ((ViewGroup) content.getParent()).removeView(content);
        }
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = gravity;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(content);
        dlg.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dlg.show();
        return dlg;
    }

    VideoUpdateBroadCastRecever receiver;

    /**
     * 注册用于刷新 视频数据的广播
     * 详情页的广播 优先级 高于 列表
     *
     */
    private void setUpdateReciever() {
        if(receiver==null)
        {
            IntentFilter inf = new IntentFilter();
            inf.addAction(ZSVideoConst.REFRESH_VIDEO);
            inf.addAction(ZSVideoConst.VIDEO_NET_ACTION);
            inf.setPriority(111120);
            receiver = new VideoUpdateBroadCastRecever();
            mContext.registerReceiver(receiver, inf);
        }
    }
    private void cancelUpdateReciever()
    {
        if(receiver!=null)
        {
            unregisterReceiver(receiver);
            receiver= null;
        }
    }
    public class VideoUpdateBroadCastRecever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ZSVideoConst.REFRESH_VIDEO))
            {
                String status =intent.getStringExtra(ZSVideoConst.VIDEO_STATUS);
                int palyPosition =intent.getIntExtra(ZSVideoConst.VIDEO_POSITION,0);
                if(status.equals(ZSVideoConst.VIDEO_STATUS_PLAY))
                {
                    mediaPlaySeekTo(palyPosition);
                }else if(status.equals(ZSVideoConst.VIDEO_STATUS_PAUSE))
                {
                    stopPlay();
                }else if(status.equals(ZSVideoConst.VIDEO_STATUS_STOP))
                {
                    stopPlay();
                }
            }if(action.equals(ZSVideoConst.VIDEO_NET_ACTION))
            {
                String status =intent.getStringExtra(ZSVideoConst.VIDEO_NET_STATUS);
                if(status.equalsIgnoreCase(ZSVideoConst.VIDEO_NET_STATUS_PHONE))
                {
                    showNetChangeDialog();
                    abortBroadcast();
                }else if(status.equalsIgnoreCase(ZSVideoConst.VIDEO_NET_STATUS_NO))
                {
                    dealWithNoNet();
                }
            }
        }
    }

    public void dealWithNoNet()
    {
        if(videoView!=null&& getCurrentDuration()<=0)
        {
            stopPlay();
        }
    }


    public void showNetChangeDialog()
    {
        if(mediaPlayIsPlaying())
        {
            mediaPlayPause();
            final NetChangeDialog dialog = NetChangeDialog.getInstance(this, new NetChangeDialog.NetClickListener() {
                @Override
                public void continuePlay()
                {
                    mediaPlayStart();
                }
                @Override
                public void canclePlay() {
                    stopPlay();
                }
            });
            if(!isFinishing())
                dialog.show();


        }
    }

    @Override
    protected void onUserLeaveHint() {
        stopPlay();
        super.onUserLeaveHint();
    }

    private HomeListener mHomeWatcher;

    public void setHomeListener()
    {
        mHomeWatcher = new HomeListener(this);
        mHomeWatcher.setOnHomePressedListener(new HomeListener.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setScreenPortrait();
                }
                releasePlay();
            }
            @Override
            public void onHomeLongPressed() {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setScreenPortrait();
                }
                releasePlay();
            }

            @Override
            public void onPowerOffPressed() {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setScreenPortrait();
                }
                releasePlay();
            }
        });
        mHomeWatcher.startWatch();
    }
    public void cancelHomeListener()
    {
        if(mHomeWatcher!=null)
        {
            mHomeWatcher.stopWatch();
        }
    }
    private ScreenListener screenListener;
    public void setScreenListener()
    {
        screenListener = new ScreenListener(this);
        screenListener.setScreenStateListener(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                stopPlay();
            }

            @Override
            public void onUserPresent() {

            }
        });
        screenListener.startWatch();
    }
    public void cancelScreenListener()
    {
        if(screenListener!=null)
        {
            screenListener.stopWatch();
        }
    }
}
