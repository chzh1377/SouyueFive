package com.zhongsou.souyue.net.volley;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.fragment.MineFragment;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.TelephonyInfo;
import com.zhongsou.souyue.utils.Utils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;

import java.util.Map;


/**
 *
 * Created by lvqiang on 15/5/29.
 */
public class CRequestProcess implements IRequestProcess,IParser,IRequestCache,IRequestCallBack<VolleyError> {


    public static final int PROCESS_GUEST_TOKEN=0;
    public static final int PROCESS_VALI_NO=1;

    private CVolleyManager mVolleyManager;
    private final Context mContext;
    private final String mTag;

    public CRequestProcess(Context context,String tag){
        mContext = context;
        mTag=tag;
        mVolleyManager = new CVolleyManager(mContext,mTag);
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            JsonObject json = (JsonObject) msg.obj;
            switch (msg.what){
                case PROCESS_GUEST_TOKEN:
                    long guestId= 0l;
                    try {
                        guestId = json.get("guestId").getAsLong();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    String guestToken=json.get("guestToken").getAsString();
                    if(guestId!=0&&StringUtils.isNotEmpty(guestToken)){
                        User user = SYUserManager.getInstance().getUser();
                        if (user == null) {
                            user = new User();
                        }
                        user.token_$eq(guestToken);
                        user.userId_$eq(guestId);
                        user.userType_$eq(SYUserManager.USER_GUEST);
                        SYUserManager.getInstance().setUser(user);
                    }
                    break;
                case PROCESS_VALI_NO:
                    Utils.storeValiNoFile(mContext, json.get("valiNo").getAsString().getBytes());
                    break;
            }
            return false;
        }
    });

    @Override
    public void doRequest(CVolleyRequest _request) {
        _request.setPreProcess(new preProcess());
        _request.setPostProcess(new postProcess());
        if(_request.getParser()==null) {
            _request.setParser(this);
        }
        _request.setRequestProcess(this);
        if (_request.getmMethod()==CVolleyRequest.REQUEST_METHOD_GET||_request.getmMethod()==CVolleyRequest.REQUEST_METHOD_POST) {
            mVolleyManager.doRequest(_request);
        }else if(_request.getmMethod()==CVolleyRequest.REQUEST_METHOD_DOWNLOAD){
            mVolleyManager.doDownload(_request);
        }
    }

    @Override
    public void doRequest(BaseUrlRequest _request) {
        CVolleyRequest request = new CVolleyRequest();
        _request.setmProcess(this);
        request.setBaseUrlRequest(_request);
        request.setPreProcess(new preProcess());
        request.setPostProcess(new postProcess());
        if(request.getParser()==null) {
            request.setParser(this);
        }
        request.setRequestProcess(this);
        if (request.getmMethod()==CVolleyRequest.REQUEST_METHOD_GET||request.getmMethod()==CVolleyRequest.REQUEST_METHOD_POST) {
            mVolleyManager.doRequest(request);
        }else if(request.getmMethod()==CVolleyRequest.REQUEST_METHOD_DOWNLOAD){
            mVolleyManager.doDownload(request);
        }
    }

    @Override
    public Object doParse(CVolleyRequest request,String _res) throws CSouyueHttpError {

        HttpJsonResponse res = new HttpJsonResponse(
                (JsonObject) new JsonParser().parse(_res));

        //发到UI线程去做那些无理的处理
        if (!request.ismIsFromAquary()) {
            sendAuthMessage(res);
        }

        CSouyueHttpError error = new CSouyueHttpError(res);
        if (error.isError()){
            throw error;
        }
        return res;
    }

    public void sendAuthMessage(HttpJsonResponse res){
        JsonObject json = res.getHead();

        if ( json.has("guestToken")) {
            sendMessage(PROCESS_GUEST_TOKEN, json);
        }
        if (json.get("valiNo") != null) {
            sendMessage(PROCESS_VALI_NO, json);
        }
    }
    private void sendMessage(int type,JsonObject json){
        Message msg = Message.obtain();
        msg.obj = json;
        msg.what = type;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onCallBackSucess(CVolleyRequest _request, Object response) {
        try {
            _request.setResponse(response);
            _request.onCallBack(response);
        }catch (Exception e){
            e.printStackTrace();
            mVolleyManager.removeCache(_request.getCacheKey());
//            _request.setResponse(response);
//            CSouyueHttpError error = new CSouyueHttpError(new ParseError(e));
//            _request.onCallBackError(error);
        }

    }

    @Override
    public void onCallBackError(CVolleyRequest _request, VolleyError response) {
        CSouyueHttpError error;
        if (response instanceof CSouyueHttpError){
            error = (CSouyueHttpError) response;
            if (!_request.ismIsFromAquary()) {//aquary会自己处理错误信息
                processError(error.getJson());
            }
        } else {
            error = new CSouyueHttpError(response);
        }
        _request.onCallBackError(error);

    }

    @Override
    public int getCacheState(String key) {
        return mVolleyManager.getCacheState(key);
    }

    @Override
    public int deleteCache(String key) {
        mVolleyManager.removeCache(key);
        return 0;
    }

    @Override
    public void clearCache() {
        mVolleyManager.clearCache();
    }

    @Override
    public void deleteCacheList(String[] extra) {
        for (String key:extra){
            mVolleyManager.removeCache(key);
        }
    }

    public void processError(HttpJsonResponse json){
        HttpJsonResponse response = json;
        int code = response.getCode();
        String errorLog;
        switch (code) {
            case 601://账号被踢
                ZhongSouActivityMgr zsAcMark= ZhongSouActivityMgr.getInstance();
                if (zsAcMark.acys != null
                        && !zsAcMark.acys
                        .isEmpty()) {
//                    new HttpContextImpl("", zsAcMark.acys.getLast()).onTokenExpired();

                    String type = SYUserManager.getInstance().getUserType();
                    if (type != null && !(type.equals(SYUserManager.USER_GUEST)))
                        SouYueToast.makeText(MainApplication.getInstance(), "您的登录已过期，请重新登录", 0).show();
                    User user = SYUserManager.getInstance().getUserAdmin();
                    if (user != null) {
                        SYUserManager.getInstance().delUser(user);
                        Activity ac = zsAcMark.acys.getLast();
                        if (ac != null&& !ac.isFinishing()){
                            IntentUtil.goHomeSouyue(ac);
                            Intent intent = new Intent();
                            intent.setAction(MineFragment.logoutAction);
                            ac.sendBroadcast(intent);
                        }
                    }
                    SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_USER_UPDATE, true);
                }
                break;
            case 604://新设备验证
                errorLog = response.getBodyString();
                SYSharedPreferences.getInstance().putString(
                        SYSharedPreferences.PREUSERNAME, errorLog);
//                new HttpContextImpl("", ZhongSouActivityMgr.getInstance().acys.getLast())
//                        .onEquipmentTest();
                Activity ac = ZhongSouActivityMgr.getInstance().acys.getLast();
                if (ac != null&& !ac.isFinishing()){
                    IntentUtil.gotoWeb(ac, UrlConfig.EquipmentTest.contains("?")?
                            UrlConfig.EquipmentTest+"&preusername="
                                    +SYSharedPreferences.getInstance().getString(SYSharedPreferences.PREUSERNAME, ""):UrlConfig.EquipmentTest+"?preusername="
                            +SYSharedPreferences.getInstance().getString(SYSharedPreferences.PREUSERNAME, ""), "interactWeb");
                    SYSharedPreferences.getInstance().remove(SYSharedPreferences.PREUSERNAME);
                }
                break;
            default:
                if (code>=700) {
                    errorLog = response.getBodyString();
                    SouYueToast.makeText(mContext, errorLog, 0).show();
                }
                break;
        }
    }

    @Override
    public void onDestory(){
        mVolleyManager.cancelAll();
    }

    @Override
    public boolean isRunning(String url) {
        return mVolleyManager.isRunning(url);
    }

    @Override
    public void cancelDownload(int id) {
        mVolleyManager.cancelDownload(id);
    }

    @Override
    public void cancel(String tag){
        mVolleyManager.cancel(tag);
    }

    /**
     * get加密方法
     * @param url
     * @param mRequestBody
     * @param isEncrypt
     * @return
     */
    private String getGetUrl(String url,Map<String,String> mRequestBody,boolean isEncrypt) {

        if (url.contains("sy_c")||!isEncrypt) {
        } else{
            String[] qqq = url.split("\\?");
            if (qqq.length>1){
                String[] params = qqq[1].split("&");
                for (String p:params){
                    String[] kv = p.split("=");
                    if (kv.length>1) {
                        mRequestBody.put(kv[0], kv[1]);
                    }
                }
            }
            if (Utils.isEncryt(url)) {
                url = qqq[0] + "?" + Utils.encryptGet(mRequestBody, true);
            } else {
                url = qqq[0] + "?" + Utils.encryptGet(mRequestBody, false);
            }
        }
        return url;
    }

    /**
     * post加密方法
     * @param url
     * @param mRequestBody
     * @param isEncrypt
     * @return
     */
    public Map<String,String> getEncryptParams(String url,Map<String,String> mRequestBody,boolean isEncrypt){
        if (mRequestBody.containsKey("sy_c")||!isEncrypt){
            return mRequestBody;
        }else{
            String pairs = "";
            try {
                pairs = Utils.encryptPost(url, mRequestBody, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pairs==null){
                return mRequestBody;
            }
            mRequestBody.clear();
            mRequestBody.put("sy_c",pairs);
            mRequestBody.put("vc", DeviceInfo.getAppVersion());
        }
        return mRequestBody;
    }

    class preProcess implements Request.IProcess{

        @Override
        public void process(Request<?> _request) {
            if (_request instanceof CSYRequest){
                CSYRequest req = ((CSYRequest) _request);
                CVolleyRequest request = req.getmRequest();
                BaseUrlRequest request1 = request.getBaseUrlRequest();
                if (request1==null||request1.getProcessStragegy(BaseUrlRequest.REQUEST_ADDPARAMS)) {
                    req.addExtraParams("vc", DeviceInfo.getAppVersion());
                    req.addExtraParams("state", TelephonyInfo.getSimState(mContext) + "");
                    req.addExtraParams("imei", DeviceInfo.getDeviceId());
                    req.addExtraParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));
                    req.addExtraParams("lat", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LAT, ""));
                    req.addExtraParams("long", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LNG, ""));
                    req.addExtraParams("ct", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_CITY, ""));
                    req.addExtraParams("pv", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_PROVINCE, ""));
                    req.addExtraParams("netType", NetWorkUtils.getCurrentNetworkType(MainApplication.getInstance()));
                    req.addExtraParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
                    req.addExtraParams("token", SYUserManager.getInstance().getToken());
                }
                Map<String,String> params = req.getRequestParams();

                String re_url = req.getUrl();
                boolean isEncrypt = true;
                //判断是否要加密,默认情况下是需要加密的
                if (request1==null||!request1.getProcessStragegy(BaseUrlRequest.REQUEST_ENCRYPT)) {
                    isEncrypt = false;
                }
                if(req.getMethod()==Method.GET){
                    String url = getGetUrl(re_url,params,isEncrypt);
                    req.setGetUrl(url);
                    req.clearParams();
                }else if(req.getMethod()==Method.POST){
                    getEncryptParams(re_url,params,isEncrypt);
                }
//                LogDebugUtil.write(_request.getUrl(), "network start", System.currentTimeMillis());
            }
        }
    }

    class postProcess implements Request.IProcess{

        @Override
        public void process(Request<?> _request) {
//            LogDebugUtil.write(_request.getUrl(),"network success",System.currentTimeMillis());
        }
    }

}
