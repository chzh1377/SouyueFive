package com.zhongsou.souyue.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ToastUtil;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 分享qq空间
 */
public class ShareByTencentQQZone {
    private static ShareByTencentQQZone singleton;
    public Activity context;
    private ShareContent content;
    private int shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
    public static Tencent mTencent;
    public static String mAppid = ShareApi.QQ_APP_ID;

    private ShareByTencentQQZone() {
    }

    public static ShareByTencentQQZone getInstance() {
        if (singleton == null) {
            singleton = new ShareByTencentQQZone();
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
        if (ShareByTencentQQ.checkApkExist(context, ShareByTencentQQ.TENCENT_QQ_PACKAGENAME_ANDROID)) {
            this.context = context;
            this.content = content;

            if (mTencent == null) {
                mTencent = Tencent.createInstance(mAppid, this.context);
            }

            String title = ShareByTencentQQ.subStringFromStart(content.getTitle(), 300);
            String desc = ShareByTencentQQ.subStringFromStart(content.getContent(), 600);
//	        String desc = "";

            final Bundle params = new Bundle();

            // 支持传多个imageUrl
            ArrayList<String> imageUrls = new ArrayList<String>();
            if (!StringUtils.isEmpty(content.getPicUrl())) {
                imageUrls.add(content.getPicUrl());
            } else {
                //，没找到搜悦的logo，所以用它代替吧。。。
                imageUrls.add(UrlConfig.getDefShareImage());

            }
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, desc);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, content.getUrl());
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//            mTencent.shareToQzone(context, params, qZoneShareListener);
            doShareToQzone(params);
        } else {
            SouYueToast.makeText(context, "您尚未安装QQ客户端，无法进行分享",
                    SouYueToast.LENGTH_SHORT).show();
        }
    }

    /**
     * 用异步方式启动分享
     *
     * @param params
     */
    private void doShareToQzone(final Bundle params) {
        final Activity activity = this.context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQzone(activity, params, qZoneShareListener);
            }
        }).start();
    }

    IUiListener qZoneShareListener = new IUiListener() {
        @Override
        public void onCancel() {
        }

        @Override
        public void onComplete(Object response) {
            Log.i("ShareByTencentQQ", response.toString());
            try {
                if (response != null) {
                    JSONObject resp = new JSONObject(response.toString());
                    int ret = resp.getInt("ret");
                    if (ret == 0) {
                        shareQQZoneStat();
                    }
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(final UiError e) {

            Log.i("ShareByTencentQQ", "onError: " + e.errorMessage);
            if (context != null) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SouYueToast.makeText(context, e.errorMessage,
                                SouYueToast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    };

    /**
     * 分享qqZone统计
     */
    public void shareQQZoneStat() {
    	if(context != null) {
    		ToastUtil.show(context, "分享成功");
    	}
        if (content != null && !StringUtils.isEmpty(content.getSharePointUrl())) {
            SharePointInfo info = new SharePointInfo();
            info.setUrl(content.getSharePointUrl());
            info.setKeyWord(content.getKeyword());
            info.setSrpId(content.getSrpId());
            info.setPlatform(String.valueOf(ShareMenu.SHARE_TO_QQZONE));
//            http.userSharePoint(info);
            ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
        }
    }

    public void handleResultData(Intent data) {
        Tencent.handleResultData(data, qZoneShareListener);
    }
}
