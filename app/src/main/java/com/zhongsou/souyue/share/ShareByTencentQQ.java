package com.zhongsou.souyue.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ToastUtil;
import org.json.JSONObject;

/**
 * 分享qq好友
 */
public class ShareByTencentQQ {
    private static ShareByTencentQQ singleton;
    public Activity context;
    private ShareContent content;
    private int shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
    public static Tencent mTencent;
    public static String mAppid = ShareApi.QQ_APP_ID;
    public static final String TENCENT_QQ_PACKAGENAME_ANDROID = "com.tencent.mobileqq";

    private ShareByTencentQQ() {
    }

    public static ShareByTencentQQ getInstance() {
        if (singleton == null) {
            singleton = new ShareByTencentQQ();
        }
        return singleton;
    }

    /**
     * 分享
     *
     * @param context
     * @param content
     */
    public void share(Activity context, ShareContent content) {
        //已经安装QQ客户端
        if (checkApkExist(context, TENCENT_QQ_PACKAGENAME_ANDROID)) {
            this.context = context;
            this.content = content;

            if (mTencent == null) {
                mTencent = Tencent.createInstance(mAppid, this.context);
            }

            String title = subStringFromStart(content.getTitle(), 30);
            String desc = subStringFromStart(content.getContent(), 40);
//	        String desc = "";
            String picUrl = "";

            if (!StringUtils.isEmpty(content.getPicUrl())) {
                picUrl = content.getPicUrl();
            }
            else {
                //，没找到搜悦的logo，所以用它代替吧。。。
                picUrl = UrlConfig.getDefShareImage();
            }
            final Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, content.getUrl());
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, desc);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, picUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, CommonStringsApi.APP_NAME_SHORT);
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
            //空间先隐藏吧.
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            doShareToQQ(params);
        } else {
            SouYueToast.makeText(context, "您尚未安装QQ客户端，无法进行分享",
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

    private void doShareToQQ(final Bundle params) {
        mTencent.shareToQQ(this.context, params, qqShareListener);
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            Log.i("ShareByTencentByQQZone", "onCancel");
        }

        @Override
        public void onComplete(Object response) {
            Log.i("ShareByTencentByQQZone", response.toString());
            try {
                if (response != null) {
                    JSONObject resp = new JSONObject(response.toString());
                    int ret = resp.getInt("ret");
                    if (ret == 0) {
                        shareQQStat();
                        if(context != null){
                        	context.setResult(111);
                        }
                    }
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            Log.i("ShareByTencentByQQZone", "onError: " + e.errorMessage);
            SouYueToast.makeText(context, e.errorMessage,
                    SouYueToast.LENGTH_SHORT).show();
        }
    };

    public static String subStringFromStart(String source, int len) {
        if (!StringUtils.isEmpty(source)) {
            if (source.length() > len) {
                source = source.substring(0, len);
            }
        }
        return source;
    }

    /**
     * 分享qq统计
     */
    public void shareQQStat() {
    	if(context != null) {
    		ToastUtil.show(context, "分享成功");
    	}
        if (content != null && !StringUtils.isEmpty(content.getSharePointUrl())) {
            SharePointInfo info = new SharePointInfo();
            info.setUrl(content.getSharePointUrl());
            info.setKeyWord(content.getKeyword());
            info.setSrpId(content.getSrpId());
            info.setPlatform(String.valueOf(ShareMenu.SHARE_TO_QQFRIEND));
//            http.userSharePoint(info);
            ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public void handleResultData(Intent data) {
        Log.e(getClass().getCanonicalName(), "shared,.,,,");
        Tencent.handleResultData(data, qqShareListener);
    }
}
