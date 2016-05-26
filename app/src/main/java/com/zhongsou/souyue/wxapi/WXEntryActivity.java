package com.zhongsou.souyue.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.module.WeiXinTokenInfo;
import com.zhongsou.souyue.module.WeiXinUserInfo;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.net.share.ShareSucRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.share.ShareAppKeyUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.WXState;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author : zoulu
 *         2014年3月10日
 *         下午1:36:43
 *         类说明 :
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler,IVolleyResponse {
    private IWXAPI api;
    public static final String CALL_BACKF = "3";//微信好友
    public static final String CALL_BACK = "4";//微信朋友圈
    public static final int GET_ACCESSTOKEN = 10;//取access_token和openid
    public static final int GET_USERINFO = 11;//取用户信息

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, ShareApi.WEIXIN_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {  //分享到微信和朋友圈成功
        //ERR_OK = 0(用户同意)
        //ERR_AUTH_DENIED = -4（用户拒绝授权）
        //ERR_USER_CANCEL = -2（用户取消）
        Log.e("WXEntryActivity", "resp=" + resp.errCode + "|" + resp.errStr);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (WXState.getWXState() == WXState.TIMELINE) {//微信朋友圈
//                    Http http = new Http(this);
                    share(CALL_BACK);

                    if (!StringUtils.isEmpty(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHARECALLBACK, ""))) {
//                        http.shareSuc(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHARECALLBACK, ""), CALL_BACK, SYSharedPreferences.getInstance().getString(SYSharedPreferences.CONTENT, ""));
                        ShareSucRequest request = new ShareSucRequest(HttpCommon.SHARE_SUC_REQUESTID,
                                SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHARECALLBACK, ""),
                                this);
                        request.setParams(CALL_BACK, SYSharedPreferences.getInstance().getString(SYSharedPreferences.CONTENT, ""));
                        CMainHttp.getInstance().doRequest(request);
                        SYSharedPreferences.getInstance().remove(SYSharedPreferences.SHARECALLBACK);
                        SYSharedPreferences.getInstance().remove(SYSharedPreferences.CONTENT);
                    }

                } else if (WXState.getWXState() == WXState.SESSION) {//微信好友
//                    Http http = new Http(this);
                    share(CALL_BACKF);
                    if (!StringUtils.isEmpty(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHARECALLBACK, ""))) {
//                        http.shareSuc(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHARECALLBACK, ""), CALL_BACKF, SYSharedPreferences.getInstance().getString(SYSharedPreferences.CONTENT, ""));
                        ShareSucRequest request = new ShareSucRequest(HttpCommon.SHARE_SUC_REQUESTID,
                                SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHARECALLBACK, ""),
                                this);
                        request.setParams(CALL_BACKF, SYSharedPreferences.getInstance().getString(SYSharedPreferences.CONTENT, ""));
                        CMainHttp.getInstance().doRequest(request);

                        SYSharedPreferences.getInstance().remove(SYSharedPreferences.SHARECALLBACK);
                        SYSharedPreferences.getInstance().remove(SYSharedPreferences.CONTENT);
                    }
                } else if (WXState.getWXState() == WXState.LOGIN) {//微信登录
                    SendAuth.Resp resp1 = (SendAuth.Resp) resp;
                    getAccess_token(resp1.token);
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }
        this.finish();
    }

    private void share(String call) {
        Log.e("WXEntryActivity", "SYSharedPreferences.SHAREURL=" + SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHAREURL, ""));
        if (!StringUtils.isEmpty(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHAREURL, ""))) {
            SharePointInfo info = new SharePointInfo();
            info.setUrl(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SHAREURL, ""));
            info.setKeyWord(SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEYWORD, ""));
            info.setSrpId(SYSharedPreferences.getInstance().getString(SYSharedPreferences.SRPID, ""));
            info.setPlatform(call);
//            http.userSharePoint(info);
            ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID, null, info);
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.SHAREURL);
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.KEYWORD);
            SYSharedPreferences.getInstance().remove(SYSharedPreferences.SRPID);
        }
    }

    /**
     * 通过或得到的code获取access_token和openid
     *
     * @param code
     */
    public void getAccess_token(String code) {
        final String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + ShareAppKeyUtils.WX_APP_ID + "&secret=" + ShareAppKeyUtils.WX_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
        new Thread(new Runnable() {
            @Override
            public void run() {
                //第一步，创建HttpGet对象
                HttpGet httpGet = new HttpGet(url);
                //第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象
                HttpResponse httpResponse = null;
                try {
                    httpResponse = new DefaultHttpClient().execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第三步，使用getEntity方法活得返回结果
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        wxHandler.obtainMessage(GET_ACCESSTOKEN, result).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 通过access_token获取用户信息
     *
     * @param weiXinTokenInfo
     */
    public void getUserInfo(WeiXinTokenInfo weiXinTokenInfo) {
        final String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + weiXinTokenInfo.getAccess_token() + "&openid=" + weiXinTokenInfo.getOpenid();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //第一步，创建HttpGet对象
                HttpGet httpGet = new HttpGet(url);
//                httpGet.setHeader("Accept-Charset", "UTF-8");
                httpGet.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
                //第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象
                HttpResponse httpResponse = null;
                try {
                    httpResponse = new DefaultHttpClient().execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第三步，使用getEntity方法活得返回结果
                        String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                        wxHandler.obtainMessage(GET_USERINFO, result).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler wxHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_ACCESSTOKEN://获取token和openid
                    String result = (String) msg.obj;
//                    WeiXinTokenInfo weiXinTokenInfo = JSON.parseObject(result, WeiXinTokenInfo.class);
                    WeiXinTokenInfo weiXinTokenInfo = new Gson().fromJson(result, WeiXinTokenInfo.class);
                    getUserInfo(weiXinTokenInfo);
                    break;
                case GET_USERINFO://获取用户信息
                    String userInfo = (String) msg.obj;
//                    WeiXinUserInfo weiXinUserInfo = JSON.parseObject(userInfo, WeiXinUserInfo.class);
                    WeiXinUserInfo weiXinUserInfo = new Gson().fromJson(userInfo, WeiXinUserInfo.class);
                    Intent intent = new Intent();
                    intent.setAction("com.zhongsou.login.wxlogin");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("weiXinUserInfo", weiXinUserInfo);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                    break;
            }
        }
    };

    @Override
    public void onHttpResponse(IRequest request) {

    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    public void onHttpStart(IRequest request) {

    }
}
