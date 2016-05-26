package com.zhongsou.souyue.ui;

import android.content.Context;
import android.webkit.WebView;
import com.google.gson.Gson;
import com.zhongsou.souyue.circle.model.PersonPageParam;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.module.JSClick;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.ui.webview.JavascriptInterface;
import com.zhongsou.souyue.ui.webview.onMeasureChangedListener;
import com.zhongsou.souyue.ui.webview.onScrollChangedListener;
import com.zhongsou.souyue.utils.ImJump2SouyueUtil;
import com.zhongsou.souyue.utils.RSAUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.Utils;
import org.json.JSONObject;

/**
 * Created by zyw on 2016/1/21.
 */

public class WebViewInterface implements JavascriptInterface {

    private ButtonListener              bListener;
    private ImagesListener              imgListenter;
    private GotoSrpListener             goSrpListenter;
    private OpenAdListener              openAdListener;
    private GotoShareListener           gotoShareListener;
    private OnJSClickListener           JSClickListener;
    private GotoInterestListener        gotoInterestListener;
    private ReadNovelDictionaryListener readNovelDictionaryListener;
    private ReadNovelContentListener    readNovelContentListener;
    private DownloadRadioListener       downloadRadioListener;
    private DownloadNovelListener       downloadNovelListener;
    private SetLocalCookieListener      cookieListener;
    private GetLocalCookieListener      cookieListener2;
    private onScrollChangedListener     mScrollChanged;
    private onMeasureChangedListener    mMeasureChanged;

    private WebView mWebview;
    private Context mContext;
    private String result;

    public WebViewInterface(WebView webView, Context context){
        this.mWebview = webView;
        this.mContext = context;
    }

    @Override
    @android.webkit.JavascriptInterface
    public void setButtonDisable() {
        if (bListener != null) {
            bListener.setButtonDisable();
        }
    }

    @Override
    @android.webkit.JavascriptInterface
    public void setImages(String images) {
        if (imgListenter != null)
            imgListenter.setImages(images);
    }

    @Override
    @android.webkit.JavascriptInterface
    public void gotoSRP(String keyword, String srpId) {
        if (goSrpListenter != null)
            goSrpListenter.gotoSRP(keyword, srpId);
    }

    @Override
    @android.webkit.JavascriptInterface
    public void openAd2(String json) {
        if (openAdListener != null)
            openAdListener.openAd2(json);
    }

    @Override
    @android.webkit.JavascriptInterface
    public String getSouyueInfo() {
        User user = SYUserManager.getInstance().getUser();
        String token = (user != null
                && !user.userType().equals(SYUserManager.USER_ADMIN) ? user
                .token() : "");
        return "{\"imei\":" + "\"" + DeviceInfo.getDeviceId() + "\"" + ","
                + "\"token\":" + "\"" + token + "\"}";
    }

    @Override
    @android.webkit.JavascriptInterface
    public String getSouyueVersion() {
        return DeviceInfo.getNetworkType().toLowerCase();
    }

    @Override
    @android.webkit.JavascriptInterface
    public void gotoShare() {
        if (gotoShareListener != null)
            gotoShareListener.gotoShare();
    }

    @Override
    @android.webkit.JavascriptInterface
    public String getNetworkType() {
        return null;
    }

    @Override
    @android.webkit.JavascriptInterface
    public void onJSClick(String json) {
        try {
            final JSClick jsc = new Gson().fromJson(json, JSClick.class);
            if (jsc == null)
                return;
            jsc.init();
            if (jsc.isEncrypt()) {
                final String en = Utils.encryptJs(jsc.getContent());
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl("javascript:" + jsc.getCallback() + "('" + en
                                + "')");
                    }
                });
                return;
            }
            if (jsc.isGetUser()) {
                final String en = Utils.encryptJs(ImJump2SouyueUtil
                        .getUserInfo(mContext));
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl("javascript:" + jsc.getCallback() + "('" + en
                                + "')");
                    }
                });
                return;
            }
            if (jsc.isRSAEncrypt()) {
                User user = SYUserManager.getInstance().getUser();
                // String param = "{'openid':'" + user.getOpenid() +
                // "','opid':'" + user.getOpid()
                // + "','appid':'" + user.getAppId() +
                // " ','encryptiontype':'js','data':'" + jsc.getData() + "'}";

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("openid", user.getOpenid());
                jsonObject.put("opid", user.getOpid());
                jsonObject.put("appid", user.getAppId());
                jsonObject.put("encryptiontype", "js");
                jsonObject.put("data", jsc.getData());

                try {
                    result = RSAUtils.privateEncrypt(jsonObject.toString(),
                            user.getPrivate_key());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl("javascript:" + jsc.getCallback() + "('"
                                + result + "')");
                    }
                });
                return;
            }
            if (jsc.isGetRSAParam()) {
                User       user       = SYUserManager.getInstance().getUser();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("openid", user.getOpenid());
                jsonObject.put("opid", user.getOpid());
                jsonObject.put("appid", user.getAppId());
                jsonObject.put("auth_token", user.getAuth_token());

                // final String RSAparam = "{'openid':'" + user.getOpenid() +
                // "','opid':'" + user.getOpid()
                // + "','appid':'" + user.getAppId() + " ','auth_token':'" +
                // user.getAuth_token() + "'}";

                final String RSAparam = jsonObject.toString();

                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl("javascript:" + jsc.getCallback() + "('"
                                + RSAparam + "')");
                    }
                });
                return;
            }
            if (jsc.isSouyueUserCenter()) {//个人中心
                PersonPageParam personPageParam = new PersonPageParam();
                personPageParam.setViewerUid(Long.parseLong(SYUserManager.getInstance().getUserId().trim()));
                personPageParam.setFrom(0);
                UIHelper.showPersonPageFromContext(mContext, personPageParam);
                return;
            }
            if (JSClickListener != null) {
                JSClickListener.onJSClick(jsc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @android.webkit.JavascriptInterface
    public void gotoInterest(long interest_id) {
        if (gotoInterestListener != null)
            gotoInterestListener.gotoInterest(interest_id);
    }

    @Override
    @android.webkit.JavascriptInterface
    public String getFictionIndex(String novelId) {
        String indexStr = "";
        if (readNovelDictionaryListener != null) {
            indexStr = readNovelDictionaryListener.getFictionIndex(novelId);
        }
        return indexStr;
    }

    @Override
    @android.webkit.JavascriptInterface
    public String getFictionContent(String novelId, int begin, int offet) {
        String content = "";
        if (readNovelContentListener != null) {
            content = readNovelContentListener.getFictionContent(novelId,
                    begin, offet);
        }
        return content;
    }

    @Override
    @android.webkit.JavascriptInterface
    public void downloadVideo(String id, String name, String img, String length, String urls) {
        if (downloadRadioListener != null) {
            downloadRadioListener.downloadVideo(id, name, img, length, urls);
        }
    }

    @Override
    @android.webkit.JavascriptInterface
    public void downloadFiction(String id, String name, String img, String length, String url, String version) {
        if (downloadNovelListener != null) {
            downloadNovelListener.downloadFiction(id, name, img, length, url,
                    version);
        }
    }

    @Override
    @android.webkit.JavascriptInterface
    public void setLocalCookie(String key, String value) {
        if (cookieListener != null) {
            cookieListener.setLocalCookie(key, value);
        }
    }

    @Override
    @android.webkit.JavascriptInterface
    public void getLocalCookie(String key) {
        if (cookieListener2 != null) {
            cookieListener2.getLocalCookie(key);
        }
    }

    public ButtonListener getbListener() {
        return bListener;
    }

    public void setbListener(ButtonListener bListener) {
        this.bListener = bListener;
    }

    public ImagesListener getImgListenter() {
        return imgListenter;
    }

    public void setImgListenter(ImagesListener imgListenter) {
        this.imgListenter = imgListenter;
    }

    public GotoSrpListener getGoSrpListenter() {
        return goSrpListenter;
    }

    public void setGoSrpListenter(GotoSrpListener goSrpListenter) {
        this.goSrpListenter = goSrpListenter;
    }

    public OpenAdListener getOpenAdListener() {
        return openAdListener;
    }

    public void setOpenAdListener(OpenAdListener openAdListener) {
        this.openAdListener = openAdListener;
    }

    public GotoShareListener getGotoShareListener() {
        return gotoShareListener;
    }

    public void setGotoShareListener(GotoShareListener gotoShareListener) {
        this.gotoShareListener = gotoShareListener;
    }

    public OnJSClickListener getJSClickListener() {
        return JSClickListener;
    }

    public void setJSClickListener(OnJSClickListener JSClickListener) {
        this.JSClickListener = JSClickListener;
    }

    public GotoInterestListener getGotoInterestListener() {
        return gotoInterestListener;
    }

    public void setGotoInterestListener(GotoInterestListener gotoInterestListener) {
        this.gotoInterestListener = gotoInterestListener;
    }

    public ReadNovelDictionaryListener getReadNovelDictionaryListener() {
        return readNovelDictionaryListener;
    }

    public void setReadNovelDictionaryListener(ReadNovelDictionaryListener readNovelDictionaryListener) {
        this.readNovelDictionaryListener = readNovelDictionaryListener;
    }

    public ReadNovelContentListener getReadNovelContentListener() {
        return readNovelContentListener;
    }

    public void setReadNovelContentListener(ReadNovelContentListener readNovelContentListener) {
        this.readNovelContentListener = readNovelContentListener;
    }

    public DownloadRadioListener getDownloadRadioListener() {
        return downloadRadioListener;
    }

    public void setDownloadRadioListener(DownloadRadioListener downloadRadioListener) {
        this.downloadRadioListener = downloadRadioListener;
    }

    public DownloadNovelListener getDownloadNovelListener() {
        return downloadNovelListener;
    }

    public void setDownloadNovelListener(DownloadNovelListener downloadNovelListener) {
        this.downloadNovelListener = downloadNovelListener;
    }

    public SetLocalCookieListener getCookieListener() {
        return cookieListener;
    }

    public void setCookieListener(SetLocalCookieListener cookieListener) {
        this.cookieListener = cookieListener;
    }

    public GetLocalCookieListener getCookieListener2() {
        return cookieListener2;
    }

    public void setCookieListener2(GetLocalCookieListener cookieListener2) {
        this.cookieListener2 = cookieListener2;
    }

    public onScrollChangedListener getmScrollChanged() {
        return mScrollChanged;
    }

    public void setmScrollChanged(onScrollChangedListener mScrollChanged) {
        this.mScrollChanged = mScrollChanged;
    }

    public onMeasureChangedListener getmMeasureChanged() {
        return mMeasureChanged;
    }

    public void setmMeasureChanged(onMeasureChangedListener mMeasureChanged) {
        this.mMeasureChanged = mMeasureChanged;
    }

    public WebView getmWebview() {
        return mWebview;
    }

    public void setmWebview(WebView mWebview) {
        this.mWebview = mWebview;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
