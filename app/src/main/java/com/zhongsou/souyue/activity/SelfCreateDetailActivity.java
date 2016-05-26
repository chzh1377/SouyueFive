package com.zhongsou.souyue.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.content.IShareContentProvider;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.db.SelfCreateHelper;
import com.zhongsou.souyue.dialog.SYProgressDialog;
import com.zhongsou.souyue.dialog.ShareToSouyueFriendsDialog;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.BitmapUtil;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.NewsCount;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.module.TouchGallerySerializable;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.detail.AddFavoriteReq;
import com.zhongsou.souyue.net.detail.NewsCountReq;
import com.zhongsou.souyue.net.detail.ShortURLReq;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.volley.*;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.SelfCreatePublishInView;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.FastDoubleCliceUtils;
import com.zhongsou.souyue.utils.MyImageLoader;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZSEncode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 原创分享详情页面
 *
 * @author Administrator
 */
public class SelfCreateDetailActivity extends RightSwipeActivity implements
        OnClickListener, OnCancelListener, OnItemClickListener,
        IShareContentProvider, PickerMethod {
    private LinearLayout imageContent;
    private List<String> images;
    private List<String> sourceimages;
    public static final int START_FOR_RESULT = 1010;
//    private AQuery aq;
    private SelfCreateItem sci;
//    private Http htp;
    private SelfCreateDetailHttp http;
    private SYProgressDialog sydialog;
    private ImageButton self_create_del, self_create_modify;
    private ImageButton self_create_share, self_create_comment, self_create_up,
            self_create_collect;
    private ImageView head;
    private TextView nick, pubtime, title, contents, status;
    private TextView commentCount, shareCount, upCount;
    private Bitmap shareBm;
    private String shortUrl;
    private SsoHandler mSsoHandler;
    private LinearLayout publishIn;
    private SelfCreatePublishInView publishes;
    private String srpId;
    private String keyWord;
    private View publishLayou;
    private SYUserManager sym;
    private Contact contact;

    private View comment_layout, upLayout;
    private ShareContent content;
    private String CALLBACK = "9";
    private ShareMenuDialog mShareMenuDialog;
    private RelativeLayout self_create_detail_bottombar;
    private Boolean isCollect = false;
    private Boolean isUp = false;
    private NewsCount newsCount;
    private boolean isfreeTrial;
    private View lineView;

    private void initFromIntent() {
        Intent i = getIntent();
        if (null != i)
            sci = (SelfCreateItem) i.getSerializableExtra("selfCreateItem");
        if (null != sci) {
            images = sci.conpics();
            keyWord = sci.keyword();
            if (StringUtils.isEmpty(sci.srpId())) {
                srpId = sci.kid();
                sci.srpId_$eq(srpId);
            } else {
                srpId = sci.srpId();
            }
        }
        if (!ConfigApi.isSouyue() && sci.url().contains("source=mongo")) {
            sci.url_$eq(sci.url().replace("source=mongo", "source=mongo" + CommonStringsApi.getUrlAppendIgId()));
        }
        if (ConstantsUtils.STATUS_SEND_PASS == sci.status()) {
//            htp.shortURL(sci.url());
//            http.shortURL(SelfCreateDetailHttp.HTTP_REQUEST_SHORT_URL, sci.url(), this);
            ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID,this);
            req.setParams(sci.url());
            CMainHttp.getInstance().doRequest(req);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_create_detail);
        sym = SYUserManager.getInstance();
        contact = (Contact) getIntent().getSerializableExtra("contact");
        sydialog = new SYProgressDialog(this, 0, getResources().getString(
                R.string.self_deling));
        sydialog.setOnCancelListener(this);
//        aq = new AQuery(this);
//        htp = new Http(this);
        http = new SelfCreateDetailHttp(this);
        initFromIntent();
        newsCount = new NewsCount();
        findAndInitView();
//		htp.newsCount(getToken(), sci.url());
        resetView();
        imageContent = (LinearLayout) findViewById(R.id.iamge_content);
        if (null != images) {
            String imageurl;
            sourceimages = new ArrayList<String>();
            for (int i = 0; i < images.size(); i++) {
                imageurl = images.get(i)
                        .replace("!ios", "").replace("!android", "");
                imageContent.addView(getImageview(new ImageInfo(imageurl, i)));
                sourceimages.add(imageurl);
            }
        }
        publishLayou = findViewById(R.id.self_create_detail_publish_layout);
        setPublishData();
    }

    protected void onResume() {
        super.onResume();
        if (ConstantsUtils.STATUS_SEND_PASS == sci.status()) {
//            htp.newsCount(getToken(), sci.url());
//            http.newsCount(SelfCreateDetailHttp.HTTP_REQUEST_NEWS_COUNT, getToken(), sci.url(), this);
            NewsCountReq req = new NewsCountReq(HttpCommon.DETAIL_NEWS_COUNT_ID,this);
            req.setParams(getToken(),sci.url());
            CMainHttp.getInstance().doRequest(req);
        }

//        if (isCollect) {
//            self_create_collect.setImageResource(R.drawable.circle_collect_unnormal);
//        } else {
//            self_create_collect.setImageResource(R.drawable.circle_collect_normal);
//        }
//
//        if (isUp) {
//            self_create_up.setImageResource(R.drawable.circle_up_unnormal);
//        }
    }

    private void setPublishData() {
        if (TextUtils.isEmpty(keyWord) || TextUtils.isEmpty(srpId)) {
            publishLayou.setVisibility(View.GONE);
        } else {
//			publishLayou.setVisibility(View.VISIBLE);
            publishes = (SelfCreatePublishInView) findViewById(R.id.self_create_detail_public_in);
            String[] str = keyWord.split(",");
            publishes.setData(Arrays.asList(str),
                    Arrays.asList(srpId.split(",")));
        }
    }

    private void findAndInitView() {

        upLayout = findViewById(R.id.self_create_detail_up_layout);
        upLayout.setOnClickListener(this);
        self_create_detail_bottombar = (RelativeLayout) findViewById(R.id.self_create_detail_bottombar);
        upCount = findView(R.id.self_create_upcount);
        self_create_share = findView(R.id.self_create_share_imbtn);
        self_create_comment = findView(R.id.self_create_comment_imbtn);
        self_create_up = findView(R.id.self_create_up_imbtn);
        self_create_collect = findView(R.id.self_create_collect_imbtn);
        commentCount = findView(R.id.self_create_commentcount);
        self_create_del = findView(R.id.self_create_del);
        self_create_modify = findView(R.id.self_create_modify);

        head = findView(R.id.self_create_detail_head);
        nick = findView(R.id.self_create_detail_nick);
        if (contact != null) {
            MyImageLoader.imageLoader.displayImage(contact.getAvatar(), head, MyImageLoader.options);
            nick.setText(StringUtils.isNotEmpty(contact.getComment_name()) ? contact
                    .getComment_name() : contact.getNick_name());
        } else {
            MyImageLoader.imageLoader.displayImage(sym.getImage(), head, MyImageLoader.options);
            nick.setText(sym.getName());
        }
        pubtime = findView(R.id.self_create_detail_time);
        pubtime.setText(StringUtils.convertDate(sci.pubtime()));
        title = findView(R.id.self_create_title);
        if (StringUtils.isEmpty(sci.title())) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(sci.title());
        }

        contents = findView(R.id.self_create_contents);
        contents.setText(sci.content());
    }

    private static final int STATUS_SEND_REVIEW = 0;
    private static final int STATUS_SEND_PASS = 1;
    private static final int STATUS_SEND_NOPASS = 2;
    private static final int STATUS_SEND_FAIL = 3;
    private static final int STATUS_SEND_ING = 4;

    private void resetView() {
        if (contact != null) {
            self_create_del.setVisibility(View.INVISIBLE);
            self_create_modify.setVisibility(View.INVISIBLE);
            return;
        }
        lineView = findViewById(R.id.comment_line_view);
        switch (sci.status()) {
            case STATUS_SEND_REVIEW:
                self_create_del.setVisibility(View.INVISIBLE);
                self_create_modify.setVisibility(View.INVISIBLE);
                break;
            case STATUS_SEND_PASS:
                self_create_del.setVisibility(View.VISIBLE);
                if (sci != null && !sci.isHtml())
                    self_create_modify.setVisibility(View.GONE);
                break;
            case STATUS_SEND_NOPASS:
                self_create_del.setVisibility(View.VISIBLE);
                if (sci != null && !sci.isHtml())
                    self_create_modify.setVisibility(View.GONE);
                self_create_detail_bottombar.setVisibility(View.INVISIBLE);
                lineView.setVisibility(View.INVISIBLE);
                break;
            case STATUS_SEND_FAIL:
                self_create_del.setVisibility(View.VISIBLE);
                if (sci != null && !sci.isHtml())
                    self_create_modify.setVisibility(View.VISIBLE);
                break;
            case STATUS_SEND_ING:          //草稿状态
                self_create_del.setVisibility(View.VISIBLE);
                self_create_modify.setVisibility(View.VISIBLE);
                self_create_detail_bottombar.setVisibility(View.INVISIBLE);
                lineView.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
    }

    /**
     * 获取imageview
     *
     * @return
     */
    public ImageView getImageview(ImageInfo imageInfo) {
        // 创建显示图片的对象
        ImageView mImageView = new ImageView(SelfCreateDetailActivity.this);
        mImageView.setBackgroundResource(R.drawable.default_big);
        //SYFX-1756
//        mImageView.setOnViewTapListener((OnViewTapListener) mContext);
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageInfo iif = (ImageInfo) v.getTag();
                Intent i = new Intent();
                i.setClass(SelfCreateDetailActivity.this,
                        TouchGalleryActivity.class);
                Bundle b = new Bundle();
                TouchGallerySerializable tgs = new TouchGallerySerializable();
                tgs.setClickIndex(iif.pos);
                tgs.setItems(images);
                b.putSerializable("touchGalleryItems", tgs);
                i.putExtras(b);
                startActivity(i);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.bottomMargin = 10;
        mImageView.setMinimumWidth(600);
        mImageView.setLayoutParams(params);
        mImageView.setScaleType(ScaleType.CENTER_INSIDE);
        mImageView.setTag(imageInfo);
//        aq.id(mImageView).image(imageInfo.url, true, true, 0, 0,
//                new BitmapAjaxCallback() {
//                    protected void callback(String url, ImageView iv,
//                                            Bitmap bm, AjaxStatus status) {
//                        if (status.getCode() == 200) {
//                            Bitmap bitmapSample = zoomImg(bm);
//                            iv.setImageBitmap(bitmapSample);
//                            iv.setBackgroundColor(getResources().getColor(R.color.transparent));
//                        } else {
//                            iv.setImageResource(R.drawable.default_big);
//                        }
//                    }
//                });

        PhotoUtils.showCard(PhotoUtils.UriType.HTTP, imageInfo.url, mImageView, MyDisplayImageOption.bigoptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    Bitmap bitmapSample = zoomImg(loadedImage);
                    ((ImageView) view).setImageBitmap(bitmapSample);
                    ((ImageView) view).setBackgroundColor(getResources().getColor(R.color.transparent));
                }catch (OutOfMemoryError error){

                }


            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        return mImageView;
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        if (null != sydialog && sydialog.isShowing()) {
//            sydialog.cancel();
//            SouYueToast.makeText(SelfCreateDetailActivity.this,
//                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }

    class ImageInfo {
        public ImageInfo(String url, int pos) {
            this.pos = pos;
            this.url = url;
        }

        public int pos;
        public String url;
    }

    /**
     * 当前页面底部信息（评论，顶，回调）的回调
     *
     * @param nc
     */
    public void newsCountSuccess(NewsCount nc) {
        // 得到顶，收藏的状态，在newsconunt中
        // 更新toolbar button状态
        newsCount.commentsCount_$eq(nc.commentsCount());
        newsCount.hasFavorited_$eq(nc.hasFavorited());
        newsCount.upCount_$eq(nc.upCount());
        newsCount.hasUp_$eq(nc.hasUp());
        isUp = nc.hasUp();
        isCollect = nc.hasFavorited();
        if (isUp) {
//            self_create_up.setClickable(false);
            self_create_up.setImageResource(R.drawable.circle_up_unnormal);
        }
        if (isCollect) {
            self_create_collect.setImageResource(R.drawable.circle_collect_unnormal);
//            isCollect = true;
        }
        if (!isCollect) {
            self_create_collect.setImageResource(R.drawable.circle_collect_normal);
//            isCollect = false;
        }

        upCount.setText("" + nc.upCount());
        commentCount.setText("" + newsCount.commentsCount());
        sci.commentCount_$eq("" + newsCount.commentsCount());
        sci.upCount_$eq("" + newsCount.upCount());
    }

    /**
     * 评论button
     */
    public void onCommentButtonClick(View view) {

        if (FastDoubleCliceUtils.isFastDoubleClick())
            return;
        Intent i = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selfCreateItem", sci);
        i.putExtras(bundle);
        i.setClass(this, CommentaryActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i, START_FOR_RESULT);
    }

    /**
     * 顶一下
     *
     * @param view
     */
    public void onUpButtonClick(View view) {
//        //请求没成功，居然就设置状态了？？？？？？是不是有特殊的意义呢？？？？
//        upLayout.setEnabled(false);
//        self_create_up.setClickable(false);
        if(isUp){   //如果已赞
            SouYueToast.makeText(this, R.string.detail_have_ding, Toast.LENGTH_SHORT).show();
            return;
        }
        self_create_up.setImageResource(R.drawable.circle_up_unnormal);
        upCount.setText(Integer.parseInt(upCount.getText().toString()) + 1 + "");
        isUp = true;
//        htp.up(getToken(), sci.url(), sci.title(), "", sci.content(), pubDate(), "搜悦原创",
//                keyWord, srpId);
        http.up(SelfCreateDetailHttp.HTTP_REQUEST_UP, getToken(), sci.url(), sci.title(), "", sci.content(), pubDate(), "搜悦原创",
                keyWord, srpId, this);
    }

    /**
     * 收藏
     *
     * @param view
     */
    public void onCollectButtonClick(View view) {

        if (isCollect) {// 当前已订阅,取消订阅
//            htp.favoriteDelete(getToken(), sci.url(), 0, 0l);
//            http.favoriteDelete(SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_DELETE, getToken(), sci.url(), 0, 0l, this);
            CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID,this);
            req.setParams( getToken(), sci.url(), 0, 0l);
            CMainHttp.getInstance().doRequest(req);
        }
        if (!isCollect) {
//            htp.favoriteAdd(getToken(), sci.url(), sci.title(), "", sci.content(), pubDate(),
//                    "搜悦原创", keyWord, srpId);
//            http.favoriteAdd(SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_ADD, getToken(), sci.url(), sci.title(), "", sci.content(), pubDate(),
//                    "搜悦原创", keyWord, srpId, this);
            AddFavoriteReq req  = new AddFavoriteReq(HttpCommon.CIRLCE_ADD_FAVORATE_ID,this);
            req.setParams(getToken(), sci.url(), sci.title(), "", sci.content(), pubDate(),
                    "搜悦原创", keyWord, srpId);
            CMainHttp.getInstance().doRequest(req);
        }
    }


    /**
     * 修改按钮
     */
    public void onModifyButtonClick(View view) {
        Intent intent = new Intent();
        Bundle b = new Bundle();
        if (sci.column_type() == com.zhongsou.souyue.utils.ConstantsUtils.TYPE_WEIBO_SEARCH) {// 微博
            intent.setClass(this, SendWeiboActivity.class);
        } else {//
            intent.setClass(this, SendBlogActivity.class);
        }
        b.putSerializable("selfCreateItem", sci);
        b.putBoolean("isModify", true);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    /**
     * 分享
     */
    public void onShareButtonClick(View view) {
//        if (htp == null)
//            htp = new Http(this);
        http = new SelfCreateDetailHttp(this);
        if (StringUtils.isEmpty(shortUrl))
        {
            ShortURLReq req = new ShortURLReq(HttpCommon.CIRLCE_SHORT_URL_ID,this);
            req.setParams(sci.url());
            CMainHttp.getInstance().doRequest(req);
        }
//            htp.shortURL(sci.url());
//            http.shortURL(SelfCreateDetailHttp.HTTP_REQUEST_SHORT_URL, sci.url(), this);

        showShareWindow();

    }

    /**
     * 删除Button
     */
    public void onDelButtonClick(View view) {
        AlertDialog.Builder builder = new Builder(this);
        builder.setMessage("是否删除？删除后不能恢复");
        builder.setTitle("提示");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sydialog.show();
                if (StringUtils.isEmpty(sci.id())
                        && sci.status() == ConstantsUtils.STATUS_SEND_ING) {
                    SelfCreateHelper.getInstance().delSelfCreateItem(sci);
                    sydialog.cancel();
                } else {
//                    htp.delSelfCreate(sym.getToken(), sci.id());
                    http.delSelfCreate(SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_DEL_SELCREATE, sym.getToken(), sci.id(), SelfCreateDetailActivity.this);
                }
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void delSelfCreateSuccess() {
        if (null != sydialog && sydialog.isShowing())
            sydialog.cancel();

        SouYueToast.makeText(SelfCreateDetailActivity.this,
                getResources().getString(R.string.favorite_del_success), 0)
                .show();
    }


//    public void shortURLSuccess(String url) {
//        shortUrl = url;
//    }

    public void shareSuccess(Long id) {
        SouYueToast.makeText(this, R.string.share_success, 0).show();
        if (content != null && !StringUtils.isEmpty(content.getSharePointUrl())) {
            SharePointInfo info = new SharePointInfo();
            info.setUrl(content.getSharePointUrl());
            info.setKeyWord(content.getKeyword());
            info.setSrpId(content.getSrpId());
            info.setPlatform(CALLBACK);
//            htp.userSharePoint(info);
            http.userSharePoint(SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_USER_SHARE_POINT, info, this);
        }
    }

    // 添加订阅回调
    public void favoriteAddSuccess() {
        SouYueToast.makeText(this, R.string.favorite_add, SouYueToast.LENGTH_SHORT).show();
//       htp.newsCount(getToken(), sci.url());
        isCollect = true;
        self_create_collect.setImageResource(R.drawable.circle_collect_unnormal);
    }

    public void favoriteDeleteSuccess() {
        SouYueToast.makeText(this, R.string.favorite_del, SouYueToast.LENGTH_SHORT).show();
//        htp.newsCount(getToken(), sci.url());
        self_create_collect.setImageResource(R.drawable.circle_collect_normal);
        isCollect = false;
    }

    /**
     * 返回
     */
    public void onGoBackClick(View view) {
        this.finish();
    }

    public static class ViewHolder {
        public static ImageView imageItem;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Intent i = new Intent();
        i.putExtra("ismodify", true);
        i.setAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
        this.sendBroadcast(i);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (!Utils.isSDCardExist()) {
            Toast.makeText(MainApplication.getInstance(),
                    getString(R.string.sdcard_exist), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
    }

    @Override
    public ShareContent getShareContent() {
        String picUrl = null;
        if (images != null && images.size() > 0) {
            picUrl = images.get(0).replace("!ios", "").replace("!android", "");
//            shareBm = new AQuery(this).getCachedImage(picUrl);
            File fileImage = PhotoUtils.getImageLoader().getDiskCache().get(picUrl);
            if(fileImage != null){
                shareBm = BitmapFactory.decodeFile(fileImage.getAbsolutePath());
            }
        }
        if(StringUtils.isEmpty(sci.keyword()))
        {
            sci.keyword_$eq("");
        }if(StringUtils.isEmpty(sci.keyword()))
        {
            sci.keyword_$eq("");
        }
        ShareContent result = new ShareContent(StringUtils.shareTitle(
                sci.title(), sci.content()),
                (shortUrl == null) ? ZSEncode.encodeURI(StringUtils
                        .enCodeKeyword(sci.url())) : shortUrl, shareBm,
                StringUtils.shareDesc(sci.content()), picUrl);
        result.setSharePointUrl(sci.url());
        result.setKeyword(sci.keyword().split(",")[0]);
        result.setSrpId(sci.kid().split(",")[0]);
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void showShareWindow() {
        if (mShareMenuDialog == null) {

            mShareMenuDialog = new ShareMenuDialog(this, this,
                    ShareConstantsUtils.SELFCREATEDETAIL);

        }
//		int bottomHeight = self_create_detail_bottombar.getHeight();
//		if (bottomHeight > 0) {
//			mShareMenuDialog.showBottonDialog(bottomHeight);
//
//		}
        mShareMenuDialog.showBottonDialog();
    }

    private String getToken() {
        return SYUserManager.getInstance().getToken();
    }

    private String getUrl() {
        return SYUserManager.getInstance().getUser().url();
    }

    private long pubDate() {
        long pubDate = 0;
        try {
            pubDate = Long.parseLong(sci.pubtime());
        } catch (Exception e) {
        }
        return pubDate;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void loadData(int args) {
        if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
            SouYueToast.makeText(SelfCreateDetailActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        content = getShareContent();
        switch (args) {
            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                ImShareNews imsharenews = new ImShareNews(content.getKeyword(),
                        content.getSrpId(), content.getTitle(),
                        content.getSharePointUrl(), content.getPicUrl());
//			ContactsListActivity.startSYIMFriendAct(
//					SelfCreateDetailActivity.this, imsharenews);
                IMShareActivity.startSYIMFriendAct(
                        SelfCreateDetailActivity.this, imsharenews);
                break;
            case ShareMenuDialog.SHARE_TO_DIGEST:
                // 登陆用户直接分享到精华区
                StringBuffer imageUrls = new StringBuffer();
                if (images != null) {
                    for (String url : images) {
                        imageUrls.append(url).append(" ");
                    }
                } else
                    imageUrls.append("");
                ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM,this);
                share.setParams(sci.url(),
                        StringUtils.shareTitle(sci.title(), sci.content()),
                        imageUrls.toString().trim(), sci.content(), sci.pubtime(),
                        "搜悦原创", sci.keyword().split(",")[0],
                        sci.kid().split(",")[0]);
                mMainHttp.doRequest(share);
//                htp.share(sym.getToken(), sci.url(),
//                        StringUtils.shareTitle(sci.title(), sci.content()),
//                        imageUrls.toString().trim(), sci.content(), sci.pubtime(),
//                        "搜悦原创", sci.keyword().split(",")[0],
//                        sci.kid().split(",")[0]);// kid
                // 就是srpId？
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                mSsoHandler = ShareByWeibo.getInstance().share(
                        SelfCreateDetailActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                ShareByWeixin.getInstance().share(content, false);
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                final Intent it = new Intent();
                Bundle b = new Bundle();
                SearchResultItem sri = new SearchResultItem();
                if (sci == null)
                    break;
                sri.srpId_$eq(sci.kid().split(",")[0]);
                sri.url_$eq(sci.url());
                sri.title_$eq(sci.title());
                sri.description_$eq(sci.content());
                sri.keyword_$eq(sci.keyword().split(",")[0]);
                b.putSerializable("searchResultItem", sri);
                it.setClass(this, ShareToSouyueFriendsDialog.class);
                it.putExtras(b);
                it.putExtra("content", content.getContent());
                it.putExtra("shareUrl", content.getSharePointUrl());

                isfreeTrial = SYUserManager.getInstance().getUser().freeTrial();
                if (isfreeTrial) {
                    Dialog alertDialog = new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.share_mianshen))
                            .setPositiveButton(getString(R.string.alert_assent),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            startActivity(it);
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
                    startActivity(it);
                }

                break;
            case ShareMenuDialog.SHARE_TO_INTEREST:
                com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
                interestmodel.setTitle(sci.title());
                interestmodel.setImages(sourceimages);
                interestmodel.setKeyword(sci.keyword());
                interestmodel.setSrpId(sci.srpId());
                interestmodel.setNewsUrl(sci.url());
                interestmodel.setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPETEXT);
                interestmodel.setContent(sci.content());
                UIHelper.shareToInterest(this, interestmodel);
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND://4.1.1新增分享qq好友
                ShareByTencentQQ.getInstance().share(SelfCreateDetailActivity.this, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE://4.1.1新增分享qq空间
                ShareByTencentQQZone.getInstance().share(SelfCreateDetailActivity.this, content);
                break;
            default:
                break;
        }

    }

    private Bitmap zoomImg(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        Matrix matrix = getNewMatrix(width, height);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    private Matrix getNewMatrix(float width, float height) {
        Matrix matrix = new Matrix();
        float newWidth = 0;
        float newHeight = 0;
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        float screenW = wm.getDefaultDisplay().getWidth();
        float screenH = wm.getDefaultDisplay().getHeight();
        if (width >= screenW && height <= screenH) {
            newWidth = screenW;
            newHeight = (((float) screenW / width)) * height;
        } else if (width < screenW && height > screenH) {
            newWidth = ((float) (screenH * width) / height);
            newHeight = screenH;
        } else {//宽高都大于屏幕宽高或宽高都小于屏幕宽高
            if ((screenW / screenH) >= (width / height)) {
                newWidth = (((float) width / height)) * screenH;
                newHeight = screenH;
            } else {
                newWidth = screenW;
                newHeight = ((float) (height / width)) * screenW;
            }
        }
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        return matrix;
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        int id = _request.getmId();
        switch (id) {
            case SelfCreateDetailHttp.HTTP_REQUEST_SHORT_URL:
                shortUrl = ((HttpJsonResponse) _request.getResponse()).getBodyString();
                break;
            case SelfCreateDetailHttp.HTTP_REQUEST_NEWS_COUNT:
                newsCount = new Gson().fromJson(((HttpJsonResponse) _request.getResponse()).getBody(), NewsCount.class);
                newsCountSuccess(newsCount);
                break;
            case SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_DELETE:
                favoriteDeleteSuccess();
                break;
            case SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_ADD:
                favoriteAddSuccess();
                break;
            case SelfCreateDetailHttp.HTTP_REQUEST_FAVOURITE_DEL_SELCREATE:
                delSelfCreateSuccess();
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                favoriteDeleteSuccess();
                break;
            case HttpCommon.CIRLCE_ADD_FAVORATE_ID:
                favoriteAddSuccess();
                break;
            case HttpCommon.CIRLCE_SHORT_URL_ID:
                shortUrl = ((HttpJsonResponse) _request.getResponse()).getBodyString();
                break;
            case HttpCommon.DETAIL_NEWS_COUNT_ID:
                newsCountSuccess( new Gson().fromJson(((HttpJsonResponse) _request.getResponse()).getBody(), NewsCount.class));
                break;
            case HttpCommon.SHARE_TO_PLATOM:
                HttpJsonResponse response = _request.getResponse();
                shareSuccess(response.getBodyLong("newsId"));
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        if (null != sydialog && sydialog.isShowing()) {
            sydialog.cancel();
            SouYueToast.makeText(SelfCreateDetailActivity.this,
                    getString(R.string.nonetworkerror), Toast.LENGTH_SHORT)
                    .show();
        }
        IHttpError error = _request.getVolleyError();
        int errorType = error.getErrorType();
        int code = error.getErrorCode();
        switch (_request.getmId())
        {
            case HttpCommon.SHARE_TO_PLATOM:
                if(errorType == CSouyueHttpError.TYPE_SERVER_ERROR )
                {
                    if (code<700)
                    {
                        if (code==600){
                            HttpJsonResponse json = error.getJson();
                            SouYueToast.makeText(this,json.getBodyString(),Toast.LENGTH_SHORT).show();
                        }else
                        {
                            SouYueToast.makeText(this, "分享失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else
                {
                    SouYueToast.makeText(this,R.string.networkerror, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
