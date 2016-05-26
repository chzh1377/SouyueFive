package com.zhongsou.souyue.adapter.baselistadapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.ZSImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.GalleryCommentActivity;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.MyFavoriteActivity;
import com.zhongsou.souyue.activity.PickerMethod;
import com.zhongsou.souyue.circle.activity.DetailActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.module.ImShareNews;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.module.GalleryCommentDetailItem;
import com.zhongsou.souyue.module.listmodule.BaseInvoke;
import com.zhongsou.souyue.module.listmodule.BaseListData;
import com.zhongsou.souyue.module.listmodule.FootItemBean;
import com.zhongsou.souyue.module.listmodule.SigleBigImgBean;
import com.zhongsou.souyue.module.listmodule.SpecialItemData;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CancleCollectReq;
import com.zhongsou.souyue.net.detail.AddCommentDownReq;
import com.zhongsou.souyue.net.detail.AddCommentUpReq;
import com.zhongsou.souyue.net.detail.AddFavorite2Req;
import com.zhongsou.souyue.net.share.ShareAllPlat;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zhongsou.souyue.share.ShareByWeixin;
import com.zhongsou.souyue.share.ShareMenuDialog;
import com.zhongsou.souyue.ui.NetChangeDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.uikit.LoginAlert;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.HomePagerSkipUtils;
import com.zhongsou.souyue.utils.ImageUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.ShareConstantsUtils;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ZSEncode;
import com.zhongsou.souyue.view.ZSVideoConst;

/**
 * @description:
 * @auther: qubian
 * @data: 2016/1/29.
 */

public abstract class BottomInvoke4Manager extends BaseListManager implements IBottomInvoke4, IVolleyResponse, PickerMethod, IItemInvokeVideo {
    protected Activity mActivity;
    protected boolean mUpDowning;//控制重复点 顶 和踩
    protected String mChannel;//统计字段
    protected String mShortUrl;
    protected ShareMenuDialog mCircleShareMenuDialog;
    protected SsoHandler mSsoHandler;
    protected BaseListData mListData;
    protected Bitmap imageBitmap;

    public BottomInvoke4Manager(Activity context) {
        super(context);
        mActivity = context;
    }

    public String getmChannel() {
        return mChannel;
    }

    public void setChannel(String mChannel) {
        this.mChannel = mChannel;
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.DETAIL_ADDUP_ID:
                commentUpSuccess((BaseBottomViewRender) request.getKeyValueTag("render"),
                        (BaseListData) request.getKeyValueTag("data"), request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.DETAIL_ADDDOWN_ID:
                commentDownSuccess((BaseBottomViewRender) request.getKeyValueTag("render3"),
                        (BaseListData) request.getKeyValueTag("data3"), request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                cancelCollectSuccess((BaseBottomViewRender) request.getKeyValueTag("render2"),
                        (BaseListData) request.getKeyValueTag("data2"));
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                newFavoriteAddSuccess((BaseBottomViewRender) request.getKeyValueTag("render1"), (BaseListData) request.getKeyValueTag("data1"));
                break;
        }
    }

    /**
     * 添加收藏
     *
     * @param
     */
    public void newFavoriteAddSuccess(BaseBottomViewRender render, BaseListData listData) {

        SouYueToast.makeText(mActivity, "收藏成功", Toast.LENGTH_SHORT).show();
        listData.getFootView().setIsFavorator(1);
        if (render instanceof BottomViewRender4) {
            ((BottomViewRender4) render).setFavorite(true);
        }
        changeFavoriteStateBroadcast();
        UpEventAgent.onNewsFavorite(mActivity, mChannel, listData.getInvoke().getKeyword(), listData.getInvoke().getSrpId(),
                listData.getTitle(), listData.getInvoke().getUrl());
    }

    /**
     * 取消收藏成功
     *
     * @param
     */
    public void cancelCollectSuccess(BaseBottomViewRender render, BaseListData listData) {

        SouYueToast.makeText(mActivity, "取消收藏", Toast.LENGTH_SHORT).show();
        if (render instanceof BottomViewRender4) {
            ((BottomViewRender4) render).setFavorite(false);
        }
        listData.getFootView().setIsFavorator(0);
        changeFavoriteStateBroadcast();
    }

    private void changeFavoriteStateBroadcast() {
        Intent favIntent = new Intent();
        favIntent.setAction(MyFavoriteActivity.FAVORITE_ACTION);
        mActivity.sendBroadcast(favIntent);
    }

    /**
     * 点赞成功回调
     */
    public void commentUpSuccess(BaseBottomViewRender render, BaseListData listData, HttpJsonResponse res) {
        int count = listData.getFootView().getUpCount();
        count++;
        if (count <= 0) {
            count = 1;
        }
        FootItemBean foot = listData.getFootView();
        foot.setUpCount(count);
        foot.setIsUp(1);
        if (render instanceof BottomViewRender2) {
            ((BottomViewRender2) render).setUp(true);
            ((BottomViewRender2) render).upAnimation();
            ((BottomViewRender2) render).doUpCallBack();
        } else if (render instanceof BottomViewRender4) {
            ((BottomViewRender4) render).setUp(true);
            ((BottomViewRender4) render).doUpCallBack();

        }
    }

    @Override
    public void onHttpError(IRequest request) {
        IHttpError error = request.getVolleyError();
        int errorType = error.getErrorType();
        int code = error.getErrorCode();
        switch (request.getmId()) {
            case HttpCommon.DETAIL_ADDUP_ID:
                if (errorType == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    if (code < 700) {
                        SouYueToast.makeText(mActivity, "点赞失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SouYueToast.makeText(mActivity, R.string.networkerror, Toast.LENGTH_SHORT).show();
                }
                break;
            case HttpCommon.DETAIL_ADDDOWN_ID:
                mUpDowning = false;
                if (errorType == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    if (code < 700) {
                        SouYueToast.makeText(mActivity, "踩失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SouYueToast.makeText(mActivity, R.string.networkerror, Toast.LENGTH_SHORT).show();
                }

                break;
            case HttpCommon.CIRLCE_CANCLE_FAVORATE_ID:
                if (errorType == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    if (code < 700) {
                        SouYueToast.makeText(mActivity, "取消失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SouYueToast.makeText(mActivity, R.string.networkerror, Toast.LENGTH_SHORT).show();
                }
                break;
            case HttpCommon.DETAIL_ADDFAVORITE_ID:
                if (errorType == CSouyueHttpError.TYPE_SERVER_ERROR) {
                    if (code < 700) {
                        SouYueToast.makeText(mActivity, "收藏失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SouYueToast.makeText(mActivity, R.string.networkerror, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void doUp(BottomViewRender bottomViewRender, View v, BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        if (!isFastDoubleClick()) {   //防止过快点赞
            AddCommentUpReq req = new AddCommentUpReq(HttpCommon.DETAIL_ADDUP_ID, this);
            req.setParams(data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                    data.getInvoke().getUrl(), SYUserManager.getInstance().getToken(),
                    DetailActivity.DEVICE_COME_FROM, DetailActivity.UP_TYPE_MAIN,
                    0, "", "", "", "", "", data.getInvoke().getBlogId());
            req.addKeyValueTag("render", bottomViewRender);
            req.addKeyValueTag("data", data);
            CMainHttp.getInstance().doRequest(req);
            UpEventAgent.onZSListItemDoSomeThingClick(mActivity, UpEventAgent.list_up,
                    data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), data.getInvoke().getCategory());
//            v.setEnabled(false);
        }
    }

    @Override
    public void doDown(BottomViewRender bottomViewRender, BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        boolean mHasDown = false;
        if (mHasDown) {
            Toast.makeText(mActivity, R.string.detail_have_cai,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mUpDowning) {
            mUpDowning = true;
            String imageURL = "";
            if (data.getImage() != null && data.getImage().size() > 0) {
                imageURL = data.getImage().get(0);
            }
            AddCommentDownReq req = new AddCommentDownReq(HttpCommon.DETAIL_ADDDOWN_ID, this);
            req.setParams(data.getInvoke().getKeyword(), data.getInvoke().getSrpId(), data.getInvoke().getUrl(), SYUserManager
                            .getInstance().getToken(), 3,
                    1, data.getInvoke().getTitle(), imageURL, "", "", "");
            req.addKeyValueTag("render3", bottomViewRender);
            req.addKeyValueTag("data3", data);
            CMainHttp.getInstance().doRequest(req);
            UpEventAgent.onZSListItemDoSomeThingClick(mActivity, UpEventAgent.list_down,
                    data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), data.getInvoke().getCategory());
        }
    }

    public void commentDownSuccess(BaseBottomViewRender render, BaseListData listData, HttpJsonResponse res) {
        mUpDowning = false;
        FootItemBean bean = listData.getFootView();
        int count = bean.getDownCount();
        count++;
        if (count <= 0) {
            count = 1;
        }
        bean.setDownCount(count);
        bean.setIsDown(1);
        if (render instanceof BottomViewRender4) {
            ((BottomViewRender4) render).setDown(true);
            ((BottomViewRender4) render).doDownCallBack();
        }
    }

    /**
     * 跳到详情页，同时弹出评论框
     *
     * @param data
     */
    @Override
    public void doComment(final BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        forceStopPlay();
        BaseInvoke invoke = data.getInvoke();
        if (invoke.getCategory().equals(ConstantsUtils.FR_INFO_PICTURES)) {//如果是图集的话跳到
            Intent intent = new Intent(mActivity, GalleryCommentActivity.class);
            GalleryCommentDetailItem item = new GalleryCommentDetailItem();
            item.setKeyword(invoke.getKeyword());
            item.setSrpId(invoke.getSrpId());
            item.setUrl(invoke.getUrl());
            item.setTitle(invoke.getTitle());
            item.setDescription(invoke.getDesc());
            item.nickname = SYUserManager.getInstance().getUserName();
            item.is_bantalk = 0;
            FootItemBean bean = data.getFootView();
            if (bean != null) {
                item.pubTime = bean.getCtime();
                item.setSource(bean.getSource());

            }
            //不弹键盘了
//            SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.IS_SHOW_KEYBOARD,true);
            item.setChannel(mChannel);
            item.mRoletype = Constant.ROLE_NONE;
            intent.putExtra("item", item);
            mActivity.startActivity(intent);
        } else {
            invoke.setFalg(BaseInvoke.FLAG_SKIP_COMMENT, true);
            invoke.setChan(mChannel);
            HomePagerSkipUtils.skip(mActivity, invoke);
            invoke.setFalg(BaseInvoke.FLAG_SKIP_COMMENT, false);// 跳转完成之后需要把状态改回来
        }
        UpEventAgent.onZSListItemDoSomeThingClick(mActivity, UpEventAgent.list_comment,
                data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), data.getInvoke().getCategory());
    }


    @Override
    public void doFavorite(BottomViewRender bottomViewRender, BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        int mDetailType = 1;
        int mHasFavorited = data.getFootView().getIsFavorator();
        if (mHasFavorited == 0) {
            String imageURL = "";
            List<String> images = data.getImage();
            if (images != null && images.size() > 0) {
                imageURL = images.get(0);
            } else {
                BaseInvoke invoke = data.getInvoke();
                if (invoke != null) {
                    imageURL = invoke.getBigImgUrl();
                }
            }
            //收藏如果没有标题就截取20个字符作为标题
            String title = data.getTitle();
            String content = data.getDesc();
            title = StringUtils.replaceBlank(StringUtils.shareTitle(title, content));
            AddFavorite2Req req = new AddFavorite2Req(HttpCommon.DETAIL_ADDFAVORITE_ID, this);
            req.setParams(mDetailType + "",
                    data.getInvoke().getUrl(), SYUserManager.getInstance().getToken(),
                    3, data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), title,
                    imageURL);
            req.addKeyValueTag("render1", bottomViewRender);
            req.addKeyValueTag("data1", data);
            CMainHttp.getInstance().doRequest(req);
            UpEventAgent.onZSListItemDoSomeThingClick(mActivity, UpEventAgent.list_favorate,
                    data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), data.getInvoke().getCategory());
        } else {
            // 取消收藏
            CancleCollectReq req = new CancleCollectReq(HttpCommon.CIRLCE_CANCLE_FAVORATE_ID, this);
            req.setParamsForOpenFlag(SYUserManager.getInstance().getToken(),
                    data.getInvoke().getUrl(), mDetailType, 3);
            req.addKeyValueTag("render2", bottomViewRender);
            req.addKeyValueTag("data2", data);
            CMainHttp.getInstance().doRequest(req);
            UpEventAgent.onZSListItemDoSomeThingClick(mActivity, UpEventAgent.list_favorate_cancle,
                    data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), data.getInvoke().getCategory());
        }
    }

    @Override
    public void doShare(BaseListData data) {
        if (!CMainHttp.getInstance().isNetworkAvailable(mActivity)) {
            SouYueToast.makeText(mActivity, R.string.souyue_neterror, Toast.LENGTH_LONG).show();
            return;
        }
        if (!isFastDoubleClick()) {
            FootItemBean footItemBean = data.getFootView();
            String shorurl = "";
            if (footItemBean != null) {
                shorurl = footItemBean.getShareUrl();
            }
            if (StringUtils.isNotEmpty(shorurl)) {
                mShortUrl = shorurl;
            } else {
                mShortUrl = data.getInvoke().getUrl();
            }
            mListData = data;
            String shareType = ShareConstantsUtils.JOKE_AND_GIF;
            if (data.getViewType() == BaseListData.view_Type_video_0) // 视频的分享样式
            {
                shareType = ShareConstantsUtils.VIDEO;
            }
            mCircleShareMenuDialog = new ShareMenuDialog(mActivity, this,
                    shareType);
            mCircleShareMenuDialog.showBottonDialog();
        }
        UpEventAgent.onZSListItemDoSomeThingClick(mActivity, UpEventAgent.list_share,
                data.getInvoke().getSrpId(), data.getInvoke().getKeyword(), data.getInvoke().getCategory());
    }

    @Override
    public void loadData(int position) {
        ShareContent content = null;
        content = getShareContent();
        forceStopPlay();
        doShareNews(position, content, mListData);

    }

    private ShareContent getShareContent() {
        String imageURL = "";
        if (mListData.getImage() != null && mListData.getImage().size() > 0) {
            imageURL = mListData.getImage().get(0);
        }
        if (StringUtils.isEmpty(imageURL)) {
            if (mListData instanceof SigleBigImgBean) {
                SigleBigImgBean bean = (SigleBigImgBean) mListData;
                imageURL = bean.getBigImgUrl();
            } else if (mListData instanceof SpecialItemData) {
                SpecialItemData bean = (SpecialItemData) mListData;
                imageURL = bean.getBigImgUrl();
            }
        }
        getImage(imageURL);
        final String title = StringUtils.isNotEmpty(mListData.getTitle()) ? mListData.getTitle() : mListData.getDesc();
        ShareContent result = new ShareContent(StringUtils.shareTitle(title,
                null), StringUtils.isNotEmpty(mShortUrl) ? mShortUrl : mListData.getInvoke().getUrl(), imageBitmap, StringUtils.shareDesc(mListData.getDesc()), imageURL);
        result.setSharePointUrl(mShortUrl == null ? "" : mShortUrl);
        result.setKeyword(mListData.getInvoke().getKeyword());
        result.setSrpId(mListData.getInvoke().getSrpId());
        result.setContent(title);
        return result;
    }

    /**
     * 分享 新闻，段子和gif 作为新闻类型分享
     * <p/>
     * 需要的字段 统计的 mChannel
     *
     * @param position
     * @param content
     * @param data
     */
    private void doShareNews(int position, final ShareContent content, final BaseListData data) {
//        final String title=StringUtils.isNotEmpty(data.getTitle())?data.getTitle():data.getDesc();
        String tit = data.getTitle();
        String desc = data.getDesc();
        final String title = StringUtils.replaceBlank(StringUtils.shareTitle(tit, desc));
        switch (position) {
            case ShareMenuDialog.SHARE_TO_SYIMFRIEND:
                boolean islogin = (SYUserManager.getInstance().getUser().userType()
                        .equals(SYUserManager.USER_ADMIN));
                if (islogin) {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(mActivity, mChannel, data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                            title, data.getInvoke().getUrl(), "sy_friend");
                    ImShareNews imsharenews = new ImShareNews(content.getKeyword(),
                            content.getSrpId(), title,
                            content.getSharePointUrl(), content.getPicUrl());
                    IMShareActivity.startSYIMFriendAct(mActivity,
                            imsharenews);
                } else {
                    toLogin();
                }
                break;
            case ShareMenuDialog.SHARE_TO_SINA:
                // 点击分享了此处加统计
                UpEventAgent.onNewsShare(mActivity, mChannel, data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                        title, data.getInvoke().getUrl(), "sina_wb");
                mSsoHandler = ShareByWeibo.getInstance().share(mActivity,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_WEIX:
                UpEventAgent.onNewsShare(mActivity, mChannel, data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                        title, data.getInvoke().getUrl(), "wx");// 点击分享了此处加统计
                ShareByWeixin.getInstance().share(content, false);
                break;
            case ShareMenuDialog.SHARE_TO_FRIENDS:
                String wxFriendUrl = content.getUrl();
                if (null != wxFriendUrl && wxFriendUrl.contains("urlContent.groovy?")) {
                    wxFriendUrl = wxFriendUrl.replace("urlContent.groovy?", "urlContent.groovy?keyword="
                            + StringUtils.enCodeRUL(data.getInvoke().getKeyword()) + "&mSrpId="
                            + data.getInvoke().getSrpId() + "&");
                }
                content.setUrl(wxFriendUrl);
                UpEventAgent.onNewsShare(mActivity, mChannel, data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                        title, data.getInvoke().getUrl(), "friend");
                ShareByWeixin.getInstance().share(content, true);
                break;
            case ShareMenuDialog.SHARE_TO_INTEREST:
                LoginAlert loginDialog = new LoginAlert(mActivity,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                com.zhongsou.souyue.circle.model.ShareContent interestmodel = new com.zhongsou.souyue.circle.model.ShareContent();
                                interestmodel.setTitle(title);
                                interestmodel.setImages(data.getImage());
                                interestmodel.setKeyword(data.getInvoke().getKeyword());
                                interestmodel.setSrpId(data.getInvoke().getSrpId());
                                interestmodel.setChannel(mChannel);
                                interestmodel.setBrief(data.getDesc());
                                String url = ZSEncode.encodeURI(StringUtils.enCodeKeyword(data.getInvoke().getUrl()));
                                interestmodel.setTextType(com.zhongsou.souyue.circle.model.ShareContent.TYPEHTML);
                                interestmodel.setNewsUrl(data.getInvoke().getUrl());
                                UIHelper.shareToInterest(mActivity, interestmodel,
                                        data.getInvoke().getInterestId());
                            }
                        }, CommonStringsApi.SHARE_JHQ_WARNING, 0);
                loginDialog.show();
                break;
            case ShareMenuDialog.SHARE_TO_QQFRIEND:// 4.1.1新增分享qq好友
                UpEventAgent.onNewsShare(mActivity, mChannel, data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                        title, data.getInvoke().getUrl(), "qfriend");
                content.setContent("");
                ShareByTencentQQ.getInstance().share(mActivity, content);
                break;
            case ShareMenuDialog.SHARE_TO_QQZONE:// 4.1.1新增分享qq空间
                UpEventAgent.onNewsShare(mActivity, mChannel, data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                        title, data.getInvoke().getUrl(), "qzone");
                content.setContent("");
                ShareByTencentQQZone.getInstance().share(mActivity,
                        content);
                break;
            case ShareMenuDialog.SHARE_TO_SYFRIENDS:
                boolean isfreeTrial = SYUserManager.getInstance().getUser().freeTrial();
                if (isfreeTrial) {
                    Dialog alertDialog = new AlertDialog.Builder(mActivity)
                            .setMessage(mActivity.getString(R.string.share_mianshen))
                            .setPositiveButton(mActivity.getString(R.string.alert_assent),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // 点击分享了此处加统计
                                            UpEventAgent.onNewsShare(
                                                    mActivity, mChannel,
                                                    data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                                                    title, data.getInvoke().getUrl(),
                                                    "sy_webfriend");
                                            share2SYwangyou(content, "0");
                                        }
                                    })
                            .setNegativeButton(mActivity.getString(R.string.alert_cancel),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                    alertDialog.show();
                } else {
                    // 点击分享了此处加统计
                    UpEventAgent.onNewsShare(mActivity, mChannel,
                            data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                            title, data.getInvoke().getUrl(), "sy_webfriend");
                    share2SYwangyou(content, "0");
                }
                break;
            case ShareMenuDialog.SHARE_TO_DIGEST:
                // 判断用户是否登陆
                String utype = "0";
                if (null != utype && !utype.equals("1")) {

                    LoginAlert loginDialog1 = new LoginAlert(mActivity,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 点击分享了此处加统计
                                    UpEventAgent.onNewsShare(mActivity, mChannel,
                                            data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                                            title, data.getInvoke().getUrl(), "jhq");
                                    shareToWangyouTuiJian(data);
                                }
                            }, CommonStringsApi.SHARE_JHQ_WARNING, 0);

                    loginDialog1.show();

                } else {
                    UpEventAgent.onNewsShare(mActivity, mChannel,
                            data.getInvoke().getKeyword(), data.getInvoke().getSrpId(),
                            title, data.getInvoke().getUrl(), "jhq");
                    shareToWangyouTuiJian(data);
                }
                break;
            default:
                break;
        }
    }

    private void toLogin() {
        Intent intent = new Intent();
        intent.setClass(mActivity, LoginActivity.class);
        intent.putExtra(LoginActivity.Only_Login, true);
        mActivity.startActivity(intent);
    }

    public void shareToWangyouTuiJian(BaseListData data) {
        String imageURL = "";
        if (data.getImage() != null && data.getImage().size() > 0) {
            imageURL = data.getImage().get(0);
        }
        ShareAllPlat share = new ShareAllPlat(HttpCommon.SHARE_TO_PLATOM, this);
        share.setParams(data.getInvoke().getUrl(), data.getTitle(), imageURL,
                data.getDesc(), "", "", data.getInvoke().getKeyword(), data.getInvoke().getSrpId());
        CMainHttp.getInstance().doRequest(share);

    }

    private void share2SYwangyou(final ShareContent content, String utype) {
        if (null != utype && !utype.equals("1")) {
            LoginAlert loginDialog = new LoginAlert(mActivity,
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

    private void share2SYFriends(ShareContent content) {

    }

    /**
     * 授权回调操作
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void doSsoHandler(int requestCode, int resultCode, Intent data) {
        /**
         * 新浪微博sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void getImage(String imageUrl) {
        Log.d("cache ImageUrl =", imageUrl);
        imageBitmap = null;
        if (!TextUtils.isEmpty(imageUrl)) {
            try {
                File imgFile = ImageLoader.getInstance().getDiskCache().get(imageUrl);
                if (imgFile != null) {
                    String path = imgFile.getAbsolutePath();
                    if (path != null) {
                        imageBitmap = ImageUtil.getSmallBitmap(path);
                    }
                }
                if (imageBitmap == null) {
                    imageBitmap = ZSImageLoader.getImage(imageUrl);
                }
                if (imageBitmap == null) {

                    PhotoUtils.showCard(PhotoUtils.UriType.HTTP, imageUrl, new ImageView(mActivity), MyDisplayImageOption.bigoptions);
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
            } catch (Exception e) {
                imageBitmap = null;
            }
        }
    }

    /**
     * 视频播放相关的数据
     */
    private int videoPlayPosition = -1;
    private boolean isPlaying = false;
    protected VideoRender videoRender;
    private String videoStatus = "";
    @Override
    public void setPlayPosition(int playPosition) {
        videoPlayPosition = playPosition;
    }

    @Override
    public int getPlayPosition() {
        return videoPlayPosition;
    }

    @Override
    public void setIsPalying(boolean isPalying) {
        isPlaying = isPalying;
    }

    @Override
    public void stopPlay(int position) {
        if (position != this.videoPlayPosition) {
            forceStopPlay();
        }
    }

    public void forceStopPlay() {
        if (videoRender != null) {
            isPlaying = false;
            videoPlayPosition = -1;
            videoRender.stopPlay();
            videoRender = null;
        }
    }

    public void dealWithNoNet() {
        if (videoRender != null && videoRender.getCurrentPosition() <= 0) {
            videoRender.stopPlay();
            videoRender = null;
        }
    }

    @Override
    public void setPlayRender(VideoRender render) {
        videoRender = render;
    }


    @Override
    public boolean getIsPalying() {
        return isPlaying;
    }

    public void dealWithBroaCast(Intent intent) {
        String status = intent.getStringExtra(ZSVideoConst.VIDEO_STATUS);
        long palyPosition = intent.getIntExtra(ZSVideoConst.VIDEO_POSITION, 0);
        if (status.equals(ZSVideoConst.VIDEO_STATUS_PLAY)) {
            isPlaying = true;
            videoStatus = ZSVideoConst.VIDEO_STATUS_PLAY;
            if (videoRender != null) {
                videoRender.startPlay();
            }
        } else if (status.equals(ZSVideoConst.VIDEO_STATUS_PAUSE) ) {
            isPlaying = true;
            videoStatus = ZSVideoConst.VIDEO_STATUS_PAUSE;
            if(videoRender != null)
            {
                videoRender.pausePlay();
            }
        } else if (status.equals(ZSVideoConst.VIDEO_STATUS_STOP)) {
            forceStopPlay();
        }
    }

    public void onResume() {
        setIsExpand(false);
        if (videoRender != null) {
            if (videoStatus.equalsIgnoreCase(ZSVideoConst.VIDEO_STATUS_PAUSE)) {
                videoRender.startPlay();
            } else if (isPlaying) {
                videoRender.startPlay();
            } else {
                forceStopPlay();
            }
        }
    }

    public void onPause() {
        if(!isExpand())
        {
            if (videoRender != null) {
                videoRender.stopPlay();
            }
        }
    }

    private boolean isExpand = false; //是否是全屏

    public boolean isExpand() {
        return isExpand;
    }

    public void setIsExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    boolean mediaPlayIsPlaying()
    {
//        return ZSMediaManager.intance(mActivity).mediaPlayIsPlaying();
        boolean isPlay =false ;
        if(videoRender != null)
        {
            isPlay =videoRender.isPlaying();
        }
        return isPlay;
    }
    void mediaPlayRelease()
    {
        if(videoRender != null)
        {
            videoRender.releasePlay();
        }
//        ZSMediaManager.intance(mActivity).mediaPlayRelease();
    }
    public void showNetChangeDialog() {

        if (videoRender != null && mediaPlayIsPlaying()) {
            videoRender.pausePlay();
            final NetChangeDialog dialog = NetChangeDialog.getInstance(mActivity, new NetChangeDialog.NetClickListener() {
                @Override
                public void continuePlay() {
                    videoRender.startPlay();
                }

                @Override
                public void canclePlay() {
                    forceStopPlay();
                }
            });
            if(!mActivity.isFinishing())
                dialog.show();
        }
    }
}
