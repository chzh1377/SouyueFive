package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.GalleryNewsPagerAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.emoji.EmojiPattern;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.DetailItem;
import com.zhongsou.souyue.module.GalleryCommentDetailItem;
import com.zhongsou.souyue.module.GalleryNewsHomeBean;
import com.zhongsou.souyue.module.GalleryNewsItem;
import com.zhongsou.souyue.module.GalleryNewsList;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.common.CommentCountRequest;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.detail.AddFavorite2Req;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.gallerynews.GalleryHomeListPushRequest;
import com.zhongsou.souyue.net.gallerynews.GalleryHomeListRequest;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.volley.CDetailHttp;
import com.zhongsou.souyue.net.volley.CGalleryNewsHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.service.DownloadImageTask;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.ui.gallery.GalleryViewPager;
import com.zhongsou.souyue.ui.gallery.touchview.PhotoViewAttacher.OnViewTapListener;
import com.zhongsou.souyue.ui.gallery.touchview.UrlTouchImageView.DownLoadLinstener;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.MakeCookie;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新增的图集类型.
 * author : zyw
 * mail: me@raw.so
 * 请求接口流程：
 * intent -> 获取    GalleryNewsHomeBean
 * 拿到数据，再次请求secondlist，拿到数据...
 */

public class GalleryNewsActivity extends BaseActivity implements
        OnViewTapListener, DownLoadLinstener, GalleryNewsPagerAdapter.OnItemClickListener, View.OnClickListener, PickerMethod, IVolleyResponse {

    //提示数字的TextView
    private static final String NUM_STRING = "<big>%d</big>/<font>%d</font>";

    public static final int DEVICE_COME_FROM = 3;// 来自搜悦客户端

    public static final int CIRCLE_TYPE_NEWS = 0;// 新闻

    public static final String EXTRA_IS_FROM_PUSH = "extra_is_from_push";
    public static final String EXTRA_PUSH_ID      = "extra_pushid";
    //视图对象
    private TextView       num; // 指示器
    private ImageButton    save; //保存按钮
    private ImageButton    ibPost; // 评论按钮
    private TextView       tvDingCount; // 顶的个数
    private ImageButton    ibDing; // 顶按钮
    private ImageButton    ibCollect; // 收藏按钮
    private ImageButton    ibShare; // 分享按钮
    private TextView       tvCommentCount; // 评论个数
    private RelativeLayout rlFooterloading;// 底部的loading界面


    //标题
    private TextView         tvTitle;
    //描述
    private TextView         tvDesc;
    //底部按钮区域
    private RelativeLayout   controllerLayout;
    private GalleryViewPager mViewPager; //...


    //业务对象
    private   CGalleryNewsHttp mGalleryNewsHttp; // 图集信息的http对象;
    protected CDetailHttp      mDetailHttp; // 详情页的http对象
    private boolean mIsFullScreen = false; // 是否全屏
    private ProgressBarHelper loadingHelper; // loading界面控制


    //数据对象
    private String utype; // 用户类别
    private int                  pos             = 0; //当前位置
    private String               currentImageUrl = ""; //当前图片地址
    private Map<String, Boolean> url2pic         = new HashMap<String, Boolean>();
    private GalleryNewsPagerAdapter pagerAdapter;
    private GalleryNewsList         mItem; //进来需要获取的数据对象，如果获取失败，表示这个页面整个处于Fail状态
    private GalleryNewsHomeBean     mMainItem; // 从itent拿到的数据
    private SsoHandler              mSsoHandler; //新浪微博的回调
    private boolean                 isFromPush; // 来源是否是推送
    private String                  pushId;// 推送id

    //TODO get follow field...
    private String mKeyword = ""; // 关键词


    //获取顶帖数据操作，更改顶帖数据操作
    private boolean      mHasUp; // 是否已经顶帖过了
    private boolean      mHasFavorited; // 是否已经收藏了
    private int          mUpCount; //顶帖数量
    private int          mCommentCount; // 回复数量
    private String       mSrpId;// 分享的关键字 && 顶帖接口的关键字
    private String       mUrl; // 当前页面的地址
    private String       mSourceUrl; // 源地址s
    private String       mTitle; // 标题
    private String       mContent;  // 简介
    private List<String> mShareImageUrls; //分享出去的图片
    private String       imageUrl; //图片地址
    private ImageLoader  mImageLoader;
    private String       mParamUrl; // 参数url，encode过的。
    private int mDetailType = CIRCLE_TYPE_NEWS;
    private String mBlogShareUrl; // 分享地址 (短地址)
    private Bitmap mImageBitmap; //分享图片
    private String mNickName;//昵称

    /**
     * 调试用的吐司
     */
    private Toast mToast;

    private boolean mIsfreeTrial; // ?免费试用？
    private long    newsId; // 新闻id
    private boolean isLogin; // 登录否？

    private String mSharedUrl; // 分享地址（长）

    private String mChannel;  //统计用


    private ImageButton ibBack; // 返回按钮
    private ScrollView  sv_desc; // 描述的外边框


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        setContentView(R.layout.gallerynews_activity);
        initContext(); // 初始化用到的业务对象
        initView(); // 初始化视图
        initData(false); // 初始化数据
        initEvent(); // 初始化事件
        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.PHOTOS_VIEW);
    }

    private void initContext() {
        mGalleryNewsHttp = new CGalleryNewsHttp(this); // 图及页面的http对象
        mDetailHttp = new CDetailHttp(this); // 详情页http对象
        mImageLoader = ImageLoader.getInstance(); // imageloader
    }

    /**
     * //跟初始化新闻的流程一样
     */
    private void initNews() {

    }


    /**
     * //获取落地页地址
     *
     * @return
     */
    private String getNewsUrl() {
        String url = "";
        if (mSourceUrl == null || mSourceUrl.contains("ugc.groovy")
                || mSourceUrl.contains("urlContent.groovy")
                || mSourceUrl.contains("interest.content.groovy")
                || mSourceUrl.contains("isextract=1")
                || mSourceUrl.contains("or_id")) {
            url = mSourceUrl;
        } else {
            if (mKeyword == null) {  //从微信过来的报刊是没有mkeyword的，为了防止编码报错
                mKeyword = "";
            }
            try {
                //分享地址...
                url = UrlConfig.HOST_SHARE + "newsdetail/index?category=picnews&keyword="
                        + URLEncoder.encode(mKeyword, "utf-8") + "&srpId=" + mSrpId + "&url="
                        + URLEncoder.encode(mSourceUrl, "utf-8") + "&title=" + URLEncoder.encode(mTitle, "utf-8")
                        + "&source=" + URLEncoder.encode(mMainItem.getSource(), "utf-8") + "&pubTime=" + mMainItem.getPubTime();
            } catch (Exception e) {
            }
        }

        return url;
    }

    /**
     * 获取底部详情
     */
    private void getCommentCount() {
        setBottomControlayout(false);
        CommentCountRequest.send(CGalleryNewsHttp.GALLERY_POST_GET_DING_COUNT, mParamUrl + "",
                DEVICE_COME_FROM + "", this);
    }

    /**
     * 获取分享的图
     */
    private void getImage() {
        if (mShareImageUrls != null && mShareImageUrls.size() > 0) {
            imageUrl = mShareImageUrls.get(0);
        } else {
            imageUrl = "";
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            try {
                mImageBitmap = ImageUtil.getSmallBitmap(mImageLoader.getDiskCache().get(StringUtils.UpaiYun(imageUrl)).getAbsolutePath());
            } catch (Exception e) {
                mImageBitmap = null;
            }
            if (mImageBitmap == null) {
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP, StringUtils.UpaiYun(imageUrl), new ImageView(this), MyDisplayImageOption.bigoptions);
                try {
                    mImageBitmap = ImageUtil.getSmallBitmap(mImageLoader.getDiskCache().get(StringUtils.UpaiYun(imageUrl)).getAbsolutePath());
                } catch (Exception e) {
                    Log.e("gallerynews", "分享图片未娶到，请确认传入的images有值!");
                }
            }
        }
    }

    private void initData(boolean isRefresh) {
        isFromPush = getIntent().getBooleanExtra(EXTRA_IS_FROM_PUSH, false);
        pushId = getIntent().getStringExtra(EXTRA_PUSH_ID);
        mMainItem = (GalleryNewsHomeBean) getIntent().getSerializableExtra("item");
        if (isFromPush) {
            mMainItem = new GalleryNewsHomeBean();
            initHomeListFromPush(pushId);
        } else {
            //进来先获取页面信息
            if (mMainItem != null) {
                initHomeList();
            } else {
                //获取url出错，显示错误页面
                loadingHelper.showNoData();
            }
        }
        if (isRefresh) {
            //如果是网络出错强刷的话，就再次获取评论数
            getCommentCount();
        }
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    private void initHomeList() {
        mShareImageUrls = mMainItem.getImage();
        mUrl = mMainItem.getUrl();
        mSrpId = mMainItem.getSrpId();
        mKeyword = mMainItem.getKeyword();
        mChannel = mMainItem.getChannel();
        utype = SYUserManager.getInstance().getUserType(); // 获取用户类别
        mNickName = SYUserManager.getInstance().getUserName();
        mDetailType = CIRCLE_TYPE_NEWS;
        try {
            mParamUrl = URLEncoder.encode(mUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("gallerynews","编码出错");
        }
        //先获取主列表
        GalleryHomeListRequest.send(mUrl, mSrpId, mKeyword,
                mMainItem.getClickFrom(), mMainItem.getMsgId(), mMainItem.getPushFrom(), this, CGalleryNewsHttp.GALLERY_HOMELIST);
    }

    /**
     * 推送来的图集，只有一个id
     *
     * @param pushId
     */
    private void initHomeListFromPush(String pushId) {
        GalleryHomeListPushRequest.send(pushId, this, HttpCommon.GALLERY_NEWS_HOMELIST);
    }

    @Override
    public void onHttpResponse(IRequest _request) {
//        if (!mGalleryNewsHttp.setFinished(_request.getmId())) {
//            Log.e("gallerynewshttp", "mutil response ===> " + _request.getmId());
//            return;
//        }
        switch (_request.getmId()) {
            //根据不同的分类执行不同的改变ui的操作
            case CGalleryNewsHttp.GALLERY_HOMELIST: //获取图集信息成功..
            case HttpCommon.GALLERY_NEWS_HOMELIST:
                mItem = ((GalleryNewsList) (_request.getResponse()));
                onGetHomeList();
                break;
            case CGalleryNewsHttp.GALLERY_SHORTURL: // 获取短地址
                mBlogShareUrl = _request.getResponse();
                break;
            case CGalleryNewsHttp.GALLERY_POST_GET_DING_COUNT: // 获取下面的顶帖，回复，是否收藏数据
                onGetCommentCount(_request.getResponse());
                break;
            case CGalleryNewsHttp.GALLERY_DING_COMMENT: // 点击顶帖按钮
                Log.e("gallerynews_act", _request.getResponse() + "....on resp");
                onDingComment(_request.getResponse());
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                cancelCollectSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                mBlogShareUrl = (_request.<HttpJsonResponse>getResponse().getBodyString());
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse response = _request.getResponse();
                shareSuccess(response.getBodyLong("newsId"));
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                newFavoriteAddSuccess(_request.<HttpJsonResponse>getResponse());
                break;
        }
    }

    /**
     * //加载完全部信息了。可以gong掉loadingbar了。
     */
    private void loadingFinished() {
//        getSignId(); //from js
//        getMainPost(); // from js
    }


    /**
     * 顶帖返回 顶帖之后的操作
     *
     * @param res
     */
    private void onDingComment(Object res) {
        mHasUp = true;
        mUpCount++;
        setBottomControlayout(true);
        JsonObject obj = ((HttpJsonResponse) (res)).getBody();
        try {
            mToast.setText(obj.getAsString());
            mToast.show();
        } catch (Exception e) {

        }
        UpEventAgent.onNewsUp(this, mChannel, mKeyword, mSrpId, mTitle,
                mParamUrl);
    }

    /**
     * 获取 顶帖数量...信息.更新界面
     *
     * @param res
     */
    private void onGetCommentCount(Object res) {
        JsonObject obj = ((HttpJsonResponse) (res)).getBody();
        try {
            mUpCount = Utils.getJsonValue(obj, "upCount", 0);
            mCommentCount = Utils.getJsonValue(obj, "commentsCount", 0);
            mHasUp = Utils.getJsonValue(obj, "hasUp", false);
            mHasFavorited = Utils.getJsonValue(obj, "hasFavorited", false);
        } catch (JSONException e) {
            mUpCount = 0;
            mCommentCount = 0;
            mHasUp = false;
            mHasFavorited = false;
        } finally {
            mNeedFresh = false; // 刚刚拿到数据，不需要刷新
            setBottomControlayout(true);
            try {
                loadingFinished();
            } finally {
                gongLoading();
            }
        }
    }

    /**
     * 设置下部按钮的状态
     */
    private void setBottomControlayout(boolean enable) {
        rlFooterloading.setVisibility(enable ? View.GONE : View.VISIBLE);
        if (enable) {
            tvDingCount.setText(Integer.toString(mUpCount));
            tvCommentCount.setText(Integer.toString(mCommentCount));
            //判断是否顶过帖子了
            ibDing.setImageResource(mHasUp ?
                    R.drawable.circle_up_unnormal :
                    R.drawable.circle_up_normal);
            //判断是否收藏了
            ibCollect.setImageResource(mHasFavorited ?
                    R.drawable.circle_collect_unnormal :
                    R.drawable.circle_collect_normal);
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
//        if (!mGalleryNewsHttp.setFinished(_request.getmId())) {
//            Log.e("gallerynewshttp", "mutil response ===> " + _request.getmId());
//            return;
//        }
        switch (_request.getmId()) {
            //获取首页失败..
            case CGalleryNewsHttp.GALLERY_HOMELIST:
                if (getHomeListCache(_request)) {
                    loadingHelper.goneLoading();
                    loadingHelper.goneLoadingUI();
                    onGetHomeList();
                } else {
                    loadingHelper.showNetError();
                }
                break;
            //点赞失败
            case CGalleryNewsHttp.GALLERY_DING_COMMENT:
                setBottomControlayout(true);
//                loadingHelper.showNetError();
                break;
            case CGalleryNewsHttp.GALLERY_POST_GET_DING_COUNT:
                setBottomControlayout(true);
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID: // 获取短连接失败
                break;
            default:
                loadingHelper.showNetError();
                break;
        }
    }

    /**
     * 获取首页的缓存
     *
     * @param _request
     */
    private boolean getHomeListCache(IRequest _request) {
        String homeCache = GalleryHomeListRequest.getCache(_request.getCacheKey());
        if (!TextUtils.isEmpty(homeCache)) {
            mItem = GalleryHomeListRequest.parseHomeListCache(homeCache);
            return true;
        }
        return false;
    }

    //初始化点击事件
    private void initEvent() {
        ibPost.setOnClickListener(this);
        ibDing.setOnClickListener(this);
        ibCollect.setOnClickListener(this);
        ibShare.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mIsFullScreen) {
            toggleFullScreen(true);
        } else {
            super.onBackPressed();
            onBackPressClick(null);
        }
    }


    /**
     * 初始化视图
     */
    private void initView() {
        sv_desc = findView(R.id.sv_desc);
        ibBack = findView(R.id.images_back);
        rlFooterloading = findView(R.id.ent_footer_loading);
        rlFooterloading.setBackgroundColor(Color.BLACK);
        num = findView(R.id.images_num);
        save = findView(R.id.images_save);
        controllerLayout = findView(R.id.controller_layout);
        tvTitle = findView(R.id.gallerynews_activity_title);
        tvDesc = findView(R.id.gallerynews_activity_desc);
        ibPost = findView(R.id.follow_post_imagebutton);
        tvDingCount = findView(R.id.ding_count);
        ibDing = findView(R.id.ding_imagebutton);
        ibCollect = findView(R.id.collect_imagebutton);
        ibShare = findView(R.id.share_imagebutton);
        tvCommentCount = findView(R.id.follow_post_count);
        mViewPager = (GalleryViewPager) findViewById(R.id.gallery_viewer);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                setcurrentPage(pos);
//                UpEventAgent.onZSYaoWenTujiClick(GalleryNewsActivity.this, mMainItem.getUrl() , mChannel,String.valueOf(pos+1));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        //初始化loading界面
        View loadingBar = findView(R.id.list_loading);
        loadingBar.setBackgroundColor(Color.BLACK);
        loadingHelper = new ProgressBarHelper(this, loadingBar, ProgressBarHelper.MODE_LOADING_PROGRESS);
        loadingHelper
                .setProgressBarClickListener(new ProgressBarHelper.ProgressBarClickListener() {
                    @Override
                    public void clickRefresh() {
                        initData(true);
                    }
                });
        //显示loading界面
        loadingHelper.setFailureImageRes(R.drawable.gallery_loading_fail);
        loadingHelper.setNoDataImageRes(R.drawable.gallery_loading_fail);
//        loadingHelper.setOnFailureListener(onLoadingFail);
//        loadingHelper.setOoNoDateListener(onLoadingFail);
        loadingHelper.showLoading();
    }

    /**
     * 加载失败..
     */
    private Runnable onLoadingFail = new Runnable() {
        @Override
        public void run() {
            ibBack.setVisibility(View.VISIBLE);
        }
    };

    /**
     * 获取图集列表成功的时候，执行这里
     */
    private void onGetHomeList() {
        if (mItem != null) {
            //
            try {
                mTitle = mItem.getTitle();
                mMainItem.setSource(mItem.getSource());
                mMainItem.setPubTime(mItem.getNewstime());
                mMainItem.setTitle(mItem.getTitle());
                if (!TextUtils.isEmpty(mItem.getUrl())) {
                    mMainItem.setUrl(mItem.getUrl());
                }
                mContent = (mItem.getContent().get(0).getDesc());
                for (String s : mItem.getImages()) {
                    url2pic.put(s, false);
                }
                pagerAdapter = new GalleryNewsPagerAdapter(this, mItem);
                mViewPager.setAdapter(pagerAdapter);
                mViewPager.setCurrentItem(pos);
                setcurrentPage(pos);
            } catch (Exception e) {
                loadingHelper.showNoData();
                return;
            }
            //获取乱七八糟的数据
            initHomeData();
            //获取次要的列表
            try {
                initSecondData();
            } catch (Exception e) {
                Log.e("gallerynews", "初始化二级页面错误");
            }
        } else {
            loadingHelper.showNoData();
        }
    }

    private void initHomeData() {
        mShareImageUrls = mMainItem.getImage();
        mUrl = mMainItem.getUrl();
        mSrpId = mMainItem.getSrpId();
        mKeyword = mMainItem.getKeyword();
        mChannel = mMainItem.getChannel();
        utype = SYUserManager.getInstance().getUserType(); // 获取用户类别
        mNickName = SYUserManager.getInstance().getUserName();
        mDetailType = CIRCLE_TYPE_NEWS;
        try {
            mParamUrl = URLEncoder.encode(mUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("gallerynews", "编码出错");
        }
        if (mSourceUrl == null || mSourceUrl.equals("")) {
            mSourceUrl = mUrl;
        }
        if (mSourceUrl != null && !mSourceUrl.equals("")) {
            mSharedUrl = getNewsUrl(); // 分享短连接之前的链接
        }
    }


    @Override
    public void onBackPressClick(View view) {
        finishWithResult();
    }

    private void finishWithResult() {
        Intent intent = new Intent();
//        intent.putExtra("isSubscribeSuccess", false);
        intent.putExtra("isLogin", isLogin);
        intent.putExtra("isQuite", false);
        intent.putExtra("isBackSearchRefresh", false);//此变量只用来表示返回搜索页面屏蔽刷新页面
        setResult(RESULT_OK, intent);
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    /**
     * loading完毕
     */
    private void gongLoading() {
        if (!mGalleryNewsHttp.isRunning(CGalleryNewsHttp.GALLERY_HOMELIST)) {
            if (mItem.getImages() != null && mItem.getImages().size() > 0) {
                loadingHelper.goneLoading();
                loadingHelper.goneLoadingUI();
            }
//            UpEventAgent.onNewsView(GalleryNewsActivity.this, mMainItem.getUrl() , mChannel,String.valueOf(1));    //ZSSDK 第一张图片统计
            //只进SDK
        }
        if (!mGalleryNewsHttp.isRunning(CGalleryNewsHttp.GALLERY_POST_GET_DING_COUNT)) {
            if (mItem != null && mItem.getImages() != null && mItem.getImages().size() > 0) {
                loadingHelper.goneLoading();
                loadingHelper.goneLoadingUI();
            }
        }
    }

    /**
     * 主要的数据获取完毕之后，接下来就是获取次要的数据了
     */
    private void initSecondData() {
        if (mShareImageUrls != null && mShareImageUrls.size() > 0) {
            imageUrl = mShareImageUrls.get(0);
        }
        getImage();
        // 预加载Emoji表情
        new Thread(new Runnable() {
            @Override
            public void run() {
                EmojiPattern.getInstace().getFileText(GalleryNewsActivity.this);
            }
        }).start();
        //get short url
        ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID, this);
        req.setParams(mSharedUrl);
        CMainHttp.getInstance().doRequest(req);
        gongLoading();
    }

    /**
     * 返回键事件
     *
     * @param v
     */
    public void onBackClick(View v) {
        if (mIsFullScreen) {
            toggleFullScreen(true);
        } else {
            this.finish();
        }
    }


    /**
     * 设置当前页面的数据
     *
     * @param pos
     */
    private void setcurrentPage(int pos) {
        //滑动到最后一页,推荐页面
        sv_desc.scrollTo(0, 0);
        if (GalleryNewsPagerAdapter.RELATE_TAG.equals(pagerAdapter.getItem(pos))) {
            save.setVisibility(View.INVISIBLE);
            num.setVisibility(View.INVISIBLE);
            controllerLayout.setVisibility(View.GONE);
            ibBack.setVisibility(View.VISIBLE);
            mIsFullScreen = false;
            return;
        } else {
            //滑动到前面的页面（非推荐页面）
            if (!mIsFullScreen) {
                controllerLayout.setVisibility(View.VISIBLE);
                num.setVisibility(View.VISIBLE);
                ibBack.setVisibility(View.VISIBLE);
            }
            /*滑动到前面的大图部分*/
            currentImageUrl = pagerAdapter.getItem(pos).toString();
            if (num != null && tvDesc != null && tvTitle != null) {
                tvDesc.setText(mItem.getContent().get(pos).getDesc());
                tvTitle.setText(mItem.getTitle());
                num.setText(Html.fromHtml(String.format(NUM_STRING, ++pos, mItem.getContent().size())));
            }
            if (url2pic != null && url2pic.get(currentImageUrl)) {
                save.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.INVISIBLE);
            }
            UpEventAgent.onZSNewsView(GalleryNewsActivity.this, mChannel,
                    mSrpId, mTitle, mUrl, mMainItem.getCategory(), pos + "");    //ZSSDK 新闻统计-图集
        }
    }


    /**
     * 点击保存按钮
     *
     * @param v
     */
    public void onSaveToSdClick(View v) {
        if (currentImageUrl != null) {
            Toast.makeText(this, R.string.down_image_ing, Toast.LENGTH_SHORT)
                    .show();
            DownloadImageTask task = new DownloadImageTask(this);
            task.execute(currentImageUrl);
        } else {
            Toast.makeText(this, R.string.down_image_fail, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击viewPager
     *
     * @param view - View the user tapped.
     * @param x    - where the user tapped from the left of the View.
     * @param y    - where the user tapped from the top of the View.
     */
    @Override
    public void onViewTap(View view, float x, float y) {
        toggleFullScreen(true);
    }

    /**
     * 切换屏幕状态，参数代表是否重置布局参数
     * 重置布局：设置viewpager的padding大小
     *
     * @param resetLayout
     */
    private void toggleFullScreen(boolean resetLayout) {
        if (!mIsFullScreen) {
            ibBack.setVisibility(View.GONE);
            controllerLayout.setVisibility(View.GONE);
        } else {
            ibBack.setVisibility(View.VISIBLE);
            controllerLayout.setVisibility(View.VISIBLE);
        }
        //切换全屏状态
        mIsFullScreen = !mIsFullScreen;
    }

    /**
     * 下载成功
     *
     * @param url
     */
    @Override
    public void downLoadSuccess(String url) {
        url2pic.put(url, true);
        save.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
            switch (v.getId()) {
                case R.id.follow_post_imagebutton:
                case R.id.ding_imagebutton:
                case R.id.collect_imagebutton:
                case R.id.share_imagebutton:
                    mToast.setText("网络不可用");
                    mToast.show();
                    break;
            }
        } else {
            switch (v.getId()) {
                //评论按钮
                case R.id.follow_post_imagebutton:
                    onPostCommentClick();
                    break;
                //顶按钮
                case R.id.ding_imagebutton:
                    onDingClick();
                    break;
                //收藏按钮
                case R.id.collect_imagebutton:
                    onCollectClick();
                    break;
                //分享按钮
                case R.id.share_imagebutton:
                    onShareClick();
                    break;
            }
        }
    }

    /**
     * 分享按钮被点击
     */
    private void onShareClick() {
        ShareMenuDialog shareMenuDialog = new ShareMenuDialog(this, this,
                ShareConstantsUtils.GALLERY_NEWS);
        shareMenuDialog.showBottonDialog();
    }

    /**
     * 收藏按钮被点击
     */
    private void onCollectClick() {
        if (!mHasFavorited) {
            // mDetailType为了配合微件头的思维数组，从0开始，但是服务器端是1和2
//            http.newFavoriteAdd(String.valueOf(mDetailType + 1),
//                    mParamUrl, SYUserManager.getInstance().getToken(),
//                    DEVICE_COME_FROM, mSrpId, mKeyword, mTitle,
//                    imageUrl);
            AddFavorite2Req req = new AddFavorite2Req(HttpCommon.DETAIL_ADDFAVORITE_ID, this);
            req.setParams(String.valueOf(mDetailType + 1),
                    mParamUrl, SYUserManager.getInstance().getToken(),
                    DEVICE_COME_FROM, mSrpId, mKeyword, mTitle,
                    imageUrl);
            CMainHttp.getInstance().doRequest(req);
        } else {
            // 取消收藏
//            http.cancelCollect(SYUserManager.getInstance().getToken(),
//                    mUrl, mDetailType + 1, DEVICE_COME_FROM);
            CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID, this);
            req.setParamsForOpenFlag(SYUserManager.getInstance().getToken(),
                    mUrl, mDetailType + 1, DEVICE_COME_FROM);
            CMainHttp.getInstance().doRequest(req);
        }
    }


    // 收藏返回
    public void newFavoriteAddSuccess(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            SouYueToast.makeText(this, "收藏失败", Toast.LENGTH_SHORT).show();
            return;
        }
        SouYueToast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
        mHasFavorited = true;
        newsId = res.getBody().get("newsId").getAsInt();
        ibCollect.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_collect_unnormal));
        changeFavoriteStateBroadcast();
        //统计
        UpEventAgent.onNewsFavorite(this, mChannel, mKeyword, mSrpId,
                mTitle, mParamUrl);
    }

    // 取消收藏成功
    public void cancelCollectSuccess(HttpJsonResponse res) {
        int statusCode = res.getCode();
        if (statusCode != 200) {
            return;
        }
        SouYueToast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
        mHasFavorited = false;
        ibCollect.setImageDrawable(getResources().getDrawable(
                R.drawable.circle_collect_normal));

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


    /**
     * 顶按钮被点击
     */
    private void onDingClick() {
        if (mHasUp) {
            //已经顶帖
            mToast.setText(R.string.detail_have_ding);
            mToast.show();
        } else {
            //还未顶帖
//            mGalleryNewsHttp.doCommentUp(mKeyword, mSrpId, mParamUrl, CGalleryNewsHttp.UP_TYPE_MAIN, 0, mTitle, imageUrl, mContent, mMainItem.getPubTime(), mMainItem.getSource(), 0, this);
            AddCommentUpReq req = new AddCommentUpReq(CGalleryNewsHttp.GALLERY_DING_COMMENT, this);
            req.setParams(mKeyword, mSrpId, mParamUrl, CGalleryNewsHttp.UP_TYPE_MAIN, 0, mTitle, imageUrl, mContent, mMainItem.getPubTime(), mMainItem.getSource(), 0);
            CMainHttp.getInstance().doRequest(req);
        }
    }

    /**
     * 评论按钮被点击
     */
    private boolean mNeedFresh = true; // 是不是需要重新获取下面的评论数，顶帖数

    /**
     * 点击推荐事件
     */
    private void onPostCommentClick() {
        mNeedFresh = true;
        Intent                   intent = new Intent(this, GalleryCommentActivity.class);
        GalleryCommentDetailItem item   = new GalleryCommentDetailItem();
        item.setKeyword(mKeyword);
        item.setSrpId(mSrpId);
        item.setUrl(mParamUrl);
        item.setTitle(mTitle);
        item.setDescription(mContent);
        item.nickname = mNickName;
        item.is_bantalk = 0;
        try {
            item.pubTime = Long.decode(mMainItem.getPubTime());
        } catch (Exception e) {
        }
        item.setChannel(mChannel);
        item.mRoletype = Constant.ROLE_NONE;
        item.setSource(mMainItem.getSource());
        intent.putExtra("item", item);
        startActivity(intent);
    }

    /**
     * 推荐列表中的项目被点击触发的事件
     *
     * @param item
     */
    @Override
    public void onItemClick(GalleryNewsItem item) {
        Intent              intent = new Intent(getApplicationContext(), GalleryNewsActivity.class);
        GalleryNewsHomeBean bean   = new GalleryNewsHomeBean();
        bean.setUrl(item.getUrl());
        bean.setSrpId(item.getSrpid());
        bean.setKeyword(item.getKeyword());
        bean.setPubTime(item.getNewstime());
        bean.setSource(item.getSource());
        bean.setImage(Arrays.asList(item.getImg()));
        bean.setTitle(item.getTitle());
        bean.setCategory(mMainItem.getCategory());
        intent.putExtra("item", bean);
        startActivity(intent);
        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.PHOTOS_RECOMMAND_CLICK);
        UpEventAgent.onZSRecommendImageView(this, item.getSrpid(), bean.getUrl());      //ZSSDK 统计 推荐图集浏览次数

//        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 取消http任务
         */
        if (mDetailHttp != null)
            mDetailHttp.cancelAll();
        if (mGalleryNewsHttp != null)
            mGalleryNewsHttp.cancelAll();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        if (requestCode == 0x500) { // 详情页登陆后，在关闭时候通知列表做数据的更新
            isLogin = true;
        }
        if (data == null) {
            return;
        }
    }

    @Override
    protected void onResume() {
        if (mNeedFresh) {
            getCommentCount();
        }
        super.onResume();
    }

    // 点击分享第三方时调用
    @Override
    public void loadData(int position) {
        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (CMainHttp.getInstance().isNetworkAvailable(this)) {
//            if(!TextUtils.isEmpty(mBlogShareUrl)){
            ShareContent content = getNewsShareContent();
            doShareNews(position, content);
//            }else{
//                initNewsData();
//                SouYueToast.makeText(this,
//                        getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
//                        .show();
//                return;
//            }
        } else {
            SouYueToast.makeText(this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

    }

//    private void doShareRss(int position, ShareContent content) {
//        switch (position) {
//            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
//                boolean islogin = (SYUserManager.getInstance().getUser().userType()
//                        .equals(SYUserManager.USER_ADMIN));
//                if (islogin) {
//                    // 点击分享了此处加统计
//                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
//                            mTitle, mParamUrl, "sy_friend");
//                    ImShareNews imsharenews = new ImShareNews(content.getKeyword(),
//                            content.getSrpId(), content.getTitle(),
//                            content.getSharePointUrl(), content.getPicUrl());
//                    ContactsListActivity.startSYIMFriendAct(this,
//                            imsharenews);
//                } else {
//                    toLogin();
//                }
//                break;
//            case ShareMenuDialog.SHARE_TO_SINA:
//                // 点击分享了此处加统计
//                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
//                        mParamUrl, "sina_wb");
//                mSsoHandler = ShareByWeibo.getInstance().share(this,
//                        content);
//                break;
//            case ShareMenuDialog.SHARE_TO_WEIX:
//                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
//                        mParamUrl, "wx");// 点击分享了此处加统计
//                ShareByWeixin.getInstance().share(content, false);
//                break;
//            case ShareMenuDialog.SHARE_TO_FRIENDS:
//                String wxFriendUrl = content.getUrl();
//                if (null != wxFriendUrl
//                        && wxFriendUrl.contains("urlContent.groovy?")) {
//                    wxFriendUrl = wxFriendUrl.replace(
//                            "urlContent.groovy?",
//                            "urlContent.groovy?keyword="
//                                    + StringUtils.enCodeRUL(mKeyword) + "&mSrpId="
//                                    + mSrpId + "&");
//                }
//                content.setUrl(wxFriendUrl);
//                // 点击分享了此处加统计
//                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
//                        mParamUrl, "friend");
//                ShareByWeixin.getInstance().share(content, true);
//                break;
//            case ShareMenuDialog.SHARE_TO_INTEREST:
//                LoginAlert loginDialog = new LoginAlert(this,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                shareToInterest();
//                            }
//                        }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
//                loginDialog.show();
//                break;
//            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
//                // 点击分享了此处加统计
//                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
//                        mParamUrl, "qfriend");
//                content.setContent("");
//                ShareByTencentQQ.getInstance().share(this, content);
//                break;
//            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
//                // 点击分享了此处加统计
//                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
//                        mParamUrl, "qzone");
//                content.setContent("");
//                ShareByTencentQQZone.getInstance().share(this,
//                        content);
//                break;
//            default:
//                break;
//        }
//    }

    /**
     * 分享新闻类型
     *
     * @param position
     * @param content
     */
    private void doShareNews(int position, final ShareContent content) {
        switch (position) {
            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                boolean islogin = (SYUserManager.getInstance().getUser().userType()
                        .equals(SYUserManager.USER_ADMIN));
                if (islogin) {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mParamUrl, "sy_friend");
                    ImShareNews imsharenews = new ImShareNews(content.getKeyword(),
                            content.getSrpId(), content.getTitle(),
                            content.getSharePointUrl(), content.getPicUrl());
                    IMShareActivity.startSYIMFriendAct(GalleryNewsActivity.this,
                            imsharenews);
                } else {
                    toLogin();
                }
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mParamUrl, "sina_wb");
                mSsoHandler = ShareByWeibo.getInstance().share(GalleryNewsActivity.this,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mParamUrl, "wx");// 点击分享了此处加统计
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
                        mParamUrl, "friend");
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_INTEREST:
                LoginAlert loginDialog = new LoginAlert(GalleryNewsActivity.this,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                shareToInterest();
                            }
                        }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                loginDialog.show();
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mParamUrl, "qfriend");
                content.setContent("");
                ShareByTencentQQ.getInstance().share(this, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId, mTitle,
                        mParamUrl, "qzone");
                content.setContent("");
                ShareByTencentQQZone.getInstance().share(this,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                mIsfreeTrial = SYUserManager.getInstance().getUser().freeTrial();
                if (mIsfreeTrial) {
                    Dialog alertDialog = new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.share_mianshen))
                            .setPositiveButton(getString(R.string.alert_assent),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // 点击分享了此处加统计
                                            UpEventAgent.onNewsShare(
                                                    GalleryNewsActivity.this, mChannel,
                                                    mKeyword, mSrpId, mTitle, mParamUrl,
                                                    "sy_webfriend");
                                            share2SYwangyou(content);
                                        }
                                    })
                            .setNegativeButton(getString(R.string.alert_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                    alertDialog.show();
                } else {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mParamUrl, "sy_webfriend");
                    share2SYwangyou(content);
                }
                break;
            case ShareMenuDialog.SHARE_TO_DIGEST:
                // 判断用户是否登陆
                if (null != utype && !utype.equals("1")) {

                    LoginAlert loginDialog1 = new LoginAlert(GalleryNewsActivity.this,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 点击分享了此处加统计
                                    UpEventAgent.onNewsShare(GalleryNewsActivity.this,
                                            mChannel, mKeyword, mSrpId, mTitle,
                                            mParamUrl, "jhq");
                                    shareToWangyouTuiJian();
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 0);

                    loginDialog1.show();

                } else {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(this, mChannel, mKeyword, mSrpId,
                            mTitle, mParamUrl, "jhq");
                    // 登陆用户直接分享到精华区
                    shareToWangyouTuiJian();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 获取分享的详情
     *
     * @return
     */
    public ShareContent getNewsShareContent() {
        getImage();
        //获取短链失败的情况下
        if (TextUtils.isEmpty(mBlogShareUrl)) {
            try {
                mBlogShareUrl = UrlConfig.getSouyueHost() + "picsShare.groovy?url=" +
                        URLEncoder.encode(mSourceUrl, "utf-8") + "&appName=souyue&userId=" + SYUserManager.getInstance().getUserId();
            } catch (UnsupportedEncodingException e) {

            }
        }
        ShareContent result = new ShareContent(StringUtils.shareTitle(mTitle,
                mContent), mBlogShareUrl, mImageBitmap, StringUtils.shareDesc(mContent), imageUrl);
        result.setSharePointUrl(mSourceUrl == null ? "" : mSourceUrl);
        result.setKeyword(mKeyword);
        result.setSrpId(mSrpId);
        return result;
    }

    /**
     * 分享到网友推荐
     */
    public void shareToWangyouTuiJian() {
        if (newsId > 0) {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(newsId);
            mMainHttp.doRequest(share);
//            http.share(getToken(), newsId);
        } else {
            ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
            share.setParams(mParamUrl, StringUtils.shareTitle(mTitle,
                    mContent), imageUrl == null ? "" : imageUrl.toString()
                    .trim(), mContent, "", "", mKeyword, mSrpId);
            mMainHttp.doRequest(share);
//            http.share(getToken(), mParamUrl, StringUtils.shareTitle(mTitle,
//                    mContent), imageUrl == null ? "" : imageUrl.toString()
//                    .trim(), mContent, "", "", mKeyword, mSrpId);
        }
    }

    public void shareSuccess(Long id) {
        newsId = id;
        SouYueToast.makeText(this, R.string.share_success,
                SouYueToast.LENGTH_SHORT).show();
    }

    /**
     * 获取token
     */
    public String getToken() {
        return SYUserManager.getInstance().getToken();
    }

    /**
     * 分享到网友
     */
    private void share2SYwangyou(final ShareContent content) {
        if (null != utype && !utype.equals("1")) {
            LoginAlert loginDialog = new LoginAlert(this,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            share2SYFriends(content);
                        }
                    }, CommonStringsApi.SHARE_JHQ_WARNING, 1);
            loginDialog.show();
        } else {
            share2SYFriends(content);
        }
    }

    /**
     * 分享到网友
     */
    private void share2SYFriends(ShareContent content) {
        DetailItem       mItem = new DetailItem();
        Bundle           b     = new Bundle();
        Intent           i     = new Intent();
        SearchResultItem is    = DetailItem.SearchResultToDetailItem(mItem);
        b.putSerializable("searchResultItem", is);
        i.setClass(this, ShareToSouyueFriendsDialog.class);
        i.putExtras(b);
        i.putExtra("content", content.getContent());
        i.putExtra("shareUrl", content.getSharePointUrl());
        startActivity(i);
    }

    /**
     * 分享到圈子
     */
    private void shareToInterest() {
        com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
        interestmodel.setTitle(mTitle);
        interestmodel.setImages(mShareImageUrls);
        interestmodel.setKeyword(mKeyword);
        interestmodel.setSrpId(mSrpId);
        interestmodel.setChannel(mChannel);
        interestmodel.setBrief(mContent);
        String url = ZSEncode.encodeURI(StringUtils.enCodeKeyword(mUrl));
        if (containsUGC(mUrl)) {
            interestmodel
                    .setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPESOURCE);
            interestmodel.setNewsUrl(url);
        } else {
            interestmodel
                    .setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPEHTML);
            interestmodel.setNewsUrl(mSourceUrl);
        }
        UIHelper.shareToInterest(this, interestmodel,
                0);
    }

    /**
     * 是否包含ugc
     */
    public boolean containsUGC(String url) {
        if (!TextUtils.isEmpty(url))
            return url.toLowerCase().contains("ugc.groovy")
                    || url.toLowerCase().contains("interest.content.groovy");
        return false;
    }

    /**
     * 跳转到登录页
     */
    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        startActivity(intent);
    }
}
