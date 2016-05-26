package com.zhongsou.souyue.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.dialog.LoginDialog;
import com.zhongsou.souyue.dialog.LoginDialog.Builder.LoginDialogInterface;
import com.zhongsou.souyue.module.AccountInfo;
import com.zhongsou.souyue.module.QQUserInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.WeiXinUserInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.personal.UserLogin;
import com.zhongsou.souyue.net.personal.UserLoginThird;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.platform.LayoutApi;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.platform.TradeBusinessApi;
import com.zhongsou.souyue.pop.MyPopupWindow;
import com.zhongsou.souyue.share.QQAuthUtil;
import com.zhongsou.souyue.share.QQAuthUtil.QQAuthListener;
import com.zhongsou.souyue.ui.CustomProgressDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.AccessTokenKeeper;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.UserInfoUtils;
import com.zhongsou.souyue.utils.WXState;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends RightSwipeActivity implements OnClickListener {
    public static final String Only_Login = "Only_Login";

    protected static final int LGOIN_SINA_SUCCESS = 14;

    public static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String FINDPSW = "findpsw";
    public static final String PHONEREG = "phonereg";
    public static final String MSGLOGIN = "msglogin";

    private EditText username, userpwd;
    private Button loginBtn, register, forgetBtn, btn_login_username_clear;
    private ImageView sinaWeiboLogin, tengQQLogin, hangyeLogin, weixinLogin;
    private CustomProgressDialog pd;
//    private Http http;
    private TextView title;
    private LinearLayout rl_msg_login;

    // 新浪微博,仅当sdk支持sso时有效，否则web授权方式
    SsoHandler mSsoHandler;
    private AuthInfo authInfo;
    public static Oauth2AccessToken accessToken;
    // 腾讯QQ
    public LoginDialog dialog;
    private QQAuthUtil qqAuth;

    private String s_username;
    private String s_userpwd;
    private String s_userHeadUrl;
    private String s_userId;
    private String third_type;
    private boolean fromGame;
    private String tipValue;
    private boolean only_login;

    //add by trade start
    //未登陆时进入圈子页面，点击设置(三个点)，登陆后需要跳转到搜悦新闻页
    public static final String CIRCLE_SET_INTEREST_ID = "circle_set_interest_id";
    public static final String TAG = "login_success_show";
    private String type = "0";
    private String uid;
    private String syuid;
    private String token;
    private String showPager;
    private String shopAction;
    private int from = 0;   // 从供应详情页跳转到登陆页
    private TextView mtxtUserLogin;
    //add by trade end

    private WXBroadCastReceiver wxBroadCastReceiver = null;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (pd != null) {
                if (pd != null) {
                    pd.dismiss();
                }
            }
            switch (msg.what) {
//          case LOGIN_SUCCESS:
//              pd.dismiss();
//              Intent userInfoIntent = new Intent(LoginActivity.this, UserAccountActivity.class);
//              startActivity(userInfoIntent);
//              LoginActivity.this.finish();
//              overridePendingTransition(R.anim.left_in, R.anim.left_out);
//              break;
//          case LGOIN_ERROR:
//              pd.dismiss();
//              break;
                case LGOIN_SINA_SUCCESS:
                    pd.setMessage(getResources().getString(R.string.login_sns_getinfoing));
                    if (pd != null && !LoginActivity.this.isFinishing()) {
                        pd.show();
                    }
                    break;
            }
            return false;
        }
    }) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHideKeyBoard();
        setContentView(LayoutApi.getLayoutResourceId(R.layout.login));
        //add by trade start
        showPager = getIntent().getStringExtra(TAG);
        shopAction = getIntent().getStringExtra("shopAction");
        from = getIntent().getIntExtra(ConstantsUtils.FROM, 0);
        //add by trade end

//        fromGame = getIntent().getBooleanExtra(TigerGameActivity.LOGIN_TAG, false);
        only_login = getIntent().getBooleanExtra(LoginActivity.Only_Login, false);
        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();
//        if (ConfigApi.isSouyue()) {
            tipValue = sysp.getString(SYSharedPreferences.UM_FIRST_LOGIN_MSG, "");
//        } else {
//            tipValue = String.format(CommonStringsApi.getStringResourceValue(R.string.trade_welcome), CommonStringsApi.APP_NAME);
//        }

//        pd = new ProgressDialog(this);
        pd = CustomProgressDialog.createDialog(this);

        pd.setCanceledOnTouchOutside(false);
//        http = new Http(LoginActivity.this);
        doInitQQAuth();
        // 新浪微博
        authInfo = new AuthInfo(this, ShareApi.SINA_CONSUMER_KEY, ShareApi.SINA_REDIRECT_URL, null);
        // 人人网
//        rennClient = ShareByRenren.getRenren(this);
        username = findView(R.id.et_login_username);
        userpwd = findView(R.id.et_login_pwd);
        loginBtn = findView(R.id.btn_login_login);
        forgetBtn = findView(R.id.btn_login_forget);
        sinaWeiboLogin = findView(R.id.iv_login_sina_weibo);
        tengQQLogin = findView(R.id.iv_login_qq);
        weixinLogin = findView(R.id.iv_login_weixin);
//        renrenLogin = findView(R.id.iv_login_renren);
//        if (!ConfigApi.isSouyue()) {
//            hangyeLogin = findView(R.id.iv_login_hangye);
//            hangyeLogin.setOnClickListener(this);
//        }
        forgetBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        sinaWeiboLogin.setOnClickListener(this);
        tengQQLogin.setOnClickListener(this);
        weixinLogin.setOnClickListener(this);
//        renrenLogin.setOnClickListener(this);

        register = findView(R.id.btn_login_register);
        register.setOnClickListener(this);
        //短信登陆
        rl_msg_login = findView(R.id.rl_msg_login);
        rl_msg_login.setOnClickListener(this);

        btn_login_username_clear = findView(R.id.btn_login_username_clear);
        btn_login_username_clear.setOnClickListener(this);

        title = findView(R.id.activity_bar_title);
        title.setText(getResources().getString(R.string.loginActivity_login));
        if (isShowDialg()) {
            initDialog();
        }
        setResult(RESULT_OK);

        username.addTextChangedListener(mTextWatcher);

        registerWXReceiver();//注册微信登录广播  ygp
    }
    /**
     * 隐藏部分手机 输入时的虚拟返回键盘
     */
    private void setHideKeyBoard()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void doInitQQAuth() {
        qqAuth = new QQAuthUtil(this, new QQAuthListener() {

            @Override
            public void onCallback(QQUserInfo info) {
                if (info != null) {
                    s_username = info.getNickname();
                    s_userHeadUrl = info.getFigureurl_qq_1();
                    s_userId = info.getId();
                    third_type = "QQ";
                    pd.setMessage(LoginActivity.this.getResources().getString(R.string.login_sns_getinfoing));
                    pd.show();
                    System.out.println("souyueQQ info :"+s_userId+"  "+s_username);
                    UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,LoginActivity.this);
                    third.setParams(s_userId, s_username, s_userHeadUrl,null,UserLoginThird.LOGIN_THIRD_QQ);
                    mMainHttp.doRequest(third);
//                    http.loginQQ(s_userId, s_username, s_userHeadUrl,LoginActivity.this);
                }
            }
        });
    }

    private boolean isShowDialg() {
        return !sysp.getBoolean(SYSharedPreferences.KEY_LOGIN_TIP, false) && !tipValue.equals("");
    }

    private void initDialog() {
        LoginDialog.Builder build = new LoginDialog.Builder(this);
        build.setMessage(tipValue);
        build.setNegativeButton(R.string.login_dialog_notip, new LoginDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                sysp.putBoolean(SYSharedPreferences.KEY_LOGIN_TIP, true);
            }
        });
        build.setPositiveButton(R.string.login_dialog_goreg, new LoginDialogInterface() {
            @Override
            public void onClick(DialogInterface dialog, View v) {
                if (ConfigApi.isSouyue()) {
                    Intent phoneIntent = new Intent(LoginActivity.this, LoginInputPhoneNumActivity.class);
                    phoneIntent.putExtra(LOGIN_TYPE, PHONEREG);
                    startActivity(phoneIntent);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                } else {
                    TradeBusinessApi.getInstance().registerRedirect(LoginActivity.this, false);
                }
            }
        });
        build.create().show();
    }


    private Dialog showDialog;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login_forget:
                IntentUtil.gotoWeb(this, UrlConfig.ForgetPassword, "interactWeb");
//            showDialog = showAlert(this, forgetPswView);
//              Intent intent = new Intent();
//              intent.setClass(LoginActivity.this, LoginInputPhoneNumActivity.class);
//              intent.putExtra(LOGIN_TYPE, FINDPSW);
//              intent.putExtra(TigerGameActivity.LOGIN_TAG, fromGame);
//              intent.putExtra(LoginActivity.Only_Login,only_login);
//              startActivity(intent);
//              overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;

            case R.id.btn_login_login:// 登录

                s_username = username.getText().toString().trim();
                s_userpwd = userpwd.getText().toString();

        /*  if (TextUtils.isEmpty(s_username) && TextUtils.isEmpty(s_userpwd)) {
                showToast(R.string.user_login_input_name_pwd);
            } else */
                if (TextUtils.isEmpty(s_username)) {
                    showToast(R.string.user_login_input_name);
                } else if (TextUtils.isEmpty(s_userpwd)) {
                    showToast(R.string.loginActivity_input_pwd);
                } else {
                    //判断网络
                    if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                        showToast(R.string.user_login_networkerror);
                        return;
                    }
                    // 发出网络请求，进行登录
                    pd.setMessage(this.getResources().getString(R.string.loginActivity_pd_message));
                    pd.show();
                    UserLogin login = new UserLogin(HttpCommon.USER_LOGIN_IN_REQUEST,this);
                    login.setParams(s_username, s_userpwd,"");
                    mMainHttp.doRequest(login);
//                    http.login(s_username, s_userpwd,LoginActivity.this);
                }

                break;
            case R.id.iv_login_sina_weibo:// 新浪微博登录
                logoutSns();// 先注销sinaWeibo，但保留过期时间等信息
                sinaWeiBoLogin();// 再进行登录
                break;
            case R.id.iv_login_qq:// 腾讯QQ登录
                tencentLogin();
                break;
            case R.id.iv_login_weixin://微信登陆
                IWXAPI WXapi;
                WXapi = WXAPIFactory.createWXAPI(this, ShareApi.WEIXIN_APP_ID, true);
                WXapi.registerApp(ShareApi.WEIXIN_APP_ID);

                if (!WXapi.isWXAppInstalled()) {
                    SouYueToast.makeText(this, "登录失败,您还没有安装微信!", SouYueToast.LENGTH_LONG).show();
                    return;
                }

                WXState.changeWXState(WXState.LOGIN);

                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "get_simple_userinfo";
                WXapi.sendReq(req);
                break;
//            case R.id.iv_login_hangye:// 行业中国登录
//                Intent hangye_intent = new Intent(LoginActivity.this,
//                        HangYeLoginActivity.class);
//                startActivityForResult(hangye_intent, 76);
//                break;
           /* 4.1.1删除人人登陆
            * case R.id.iv_login_renren:// 人人网登录
                if (rennClient != null) {// 先注销renren，所有信息全部注销
                    rennClient.logout();// 人人注销
                }
                // 再进行登录
                rennClient.setLoginListener(new RenrenListener());
                rennClient.login(this);
                break;*/
            case R.id.btn_login_register:
                //add by trade
                TradeBusinessApi.getInstance().registerRedirect(this, fromGame);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.rl_msg_login:
                Intent msgIntent = new Intent(this, LoginInputPhoneNumActivity.class);
                msgIntent.putExtra(LOGIN_TYPE, MSGLOGIN);
//                msgIntent.putExtra(TigerGameActivity.LOGIN_TAG, fromGame);
                msgIntent.putExtra(LoginActivity.Only_Login, only_login);
                startActivity(msgIntent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.btn_login_username_clear:
                username.setText("");
                break;

            default:
                break;
        }
    }

    private ViewGroup forgetPswView;
    private MyPopupWindow popupWindow;

    private void initForgetPswLayout() {
        LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        forgetPswView = (ViewGroup) mLayoutInflater.inflate(R.layout.forget_password, null, false);

        TextView textView_cancel = (TextView) forgetPswView.findViewById(R.id.textView_cancel);
        TextView textView_mobile = (TextView) forgetPswView.findViewById(R.id.textView_mobile);
        TextView textView_email = (TextView) forgetPswView.findViewById(R.id.textView_email);


        textView_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
            }
        });

        textView_mobile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, FindPasswordByMobileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });

        textView_email.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                Intent intent = new Intent();
//                intent.setClass(LoginActivity.this, ForgetPwdActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
    }

    public static Dialog showAlert(final Context context, ViewGroup content) {
        if (content.getParent() != null) {
            ((ViewGroup) content.getParent()).removeView(content);
        }

        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;//改变显示位置
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(content);
        dlg.show();
        return dlg;
    }

/*    private void showPopupWindow(ViewGroup content) {
        if(content.getParent() != null) {
            ((ViewGroup)content.getParent()).removeView(content);
        }

        View parent = findViewById(R.id.view_popup_window_parent);
        if(popupWindow == null) {
            popupWindow = new MyPopupWindow(content, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            popupWindow.isPullUp = true;
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        }
        popupWindow.showAsDropDown(parent, 0, 0);
        popupWindow.update();
    }*/

    public void loginSuccess(User user) {
        loginSucc(user);
    }

    private void loginSucc(User user) {
        sysp.putBoolean(SYSharedPreferences.KEY_USER_UPDATE, true);
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        //add by trade mall
        TradeBusinessApi.getInstance().mallLoginSuccess(user, s_userpwd);

        logoutSns();// 第三方登录成功后，注销信息
        if (user != null) {
            user.userType_$eq("1");

            Log.i("Tuita", "loginSuccess:" + user);
            SYUserManager.getInstance().setUser(user);
//            ImserviceHelp.getInstance().im_logout();
            if (from == ConstantsUtils.FROM_SALE_DETAIL) {
                LoginActivity.this.finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            } else {
                new SYInputMethodManager(this).hideSoftInput();
                Intent i = new Intent(ConstantsUtils.LINK);
                i.putExtra(com.tuita.sdk.Constants.TYPE, 40);
                sendBroadcast(i);
                //统计
                UpEventAgent.onLogin(this);
                boolean onlyLogin = getIntent().getBooleanExtra(Only_Login, false);
//                long circleSetInterestId = getIntent().getLongExtra(CIRCLE_SET_INTEREST_ID, 0);
                //add by trade 
                TradeBusinessApi.getInstance().loginRedirect(this,  onlyLogin);
            }
        }

    }

    public void loginSuccess(HttpJsonResponse response) {
        User user = new Gson().fromJson(response.getBody(), User.class);
        loginSucc(user);
        if((StringUtils.isNotEmpty(response.getHead().get("activityUrl")))){
            String activeUrl = response.getHead().get("activityUrl").getAsString();
            IntentUtil.gotoWeb(this, activeUrl, "interactWeb");
        }
        
        if((StringUtils.isNotEmpty(response.getHead().get("guide_url")))){
            TaskCenterInfo taskCenterInfo=new TaskCenterInfo();
            taskCenterInfo.setGuide_isforced(response.getHead().get("guide_isforced").getAsString());
            taskCenterInfo.setGuide_url(response.getHead().get("guide_url").getAsString());
            taskCenterInfo.setGuide_msg(response.getHead().get("guide_msg").getAsString());
            UserInfoUtils.jumpToFillUser(taskCenterInfo);
        }
        if(StringUtils.isNotEmpty(response.getHead().get("cpmRecommend"))){
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_ADMIN_SPECIAL,
                user.userId()+","+response.getHead().get("cpmRecommend").getAsString());
        }
    }

    private void saveLoginToken(String uid, AccountInfo.THIRDTYPE type) {
        sysp.putString(SYSharedPreferences.KEY_LOGIN_TOKEN, uid);
        sysp.putString(SYSharedPreferences.KEY_LOGIN_TYPE, type.toString());
    }

    public void logoutSns() {
        logoutSina();// sinaWeibo注销
        if (qqAuth != null)
            qqAuth.logout();// QQ注销
    }

    private void tencentLogin() {
        if (qqAuth != null)
            qqAuth.doAuthQQ();
    }

    private void sinaWeiBoLogin() {
        /**
         * 下面两个注释掉的代码，仅当sdk支持sso时有效，
         */
        // 单点方式登录
        mSsoHandler = new SsoHandler(LoginActivity.this, authInfo);
        mSsoHandler.authorize(new AuthDialogListener());
    }

    // sinaweibo登录的Listener
    private class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String uid = values.getString("uid");
            LoginActivity.accessToken = Oauth2AccessToken.parseAccessToken(values);
            saveLoginToken(uid, AccountInfo.THIRDTYPE.SINA_WEIBO);
            AccessTokenKeeper.keepAccessToken(LoginActivity.this, accessToken);//统一授权
            getSinaWeiBoUserInfo(uid);
        }

        @Override
        public void onWeiboException(WeiboException paramWeiboException) {
        }

        @Override
        public void onCancel() {
        }
    }

    // 新浪微博获取用户信息
    public void getSinaWeiBoUserInfo(String uid) {
        UsersAPI userAPI = new UsersAPI(this,ShareApi.SINA_CONSUMER_KEY,LoginActivity.accessToken);
        long luid = 0;
        try {
            luid = Long.parseLong(uid);
        } catch (Exception e) {
            // TODO: handle exception
        }

        userAPI.show(luid, new RequestListener() {

            @Override
            public void onComplete(String response) {
                try {
                    JSONObject json = new JSONObject(response);

                    String weiboId = json.getString("id");
                    String weiboName = json.getString("name");
                    String image_url = json.getString("profile_image_url");
                    AccountInfo.saveThirdLogin(AccountInfo.THIRDTYPE.SINA_WEIBO.toString(), weiboId, weiboName);

                    Message msg = new Message();
                    msg.what = LGOIN_SINA_SUCCESS;
                    handler.sendMessage(msg);
                    UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,LoginActivity.this);
                    third.setParams(json.getString("id"), weiboName, json.getString("profile_image_url"),null,UserLoginThird.LOGIN_THIRD_WEIBO);
                    mMainHttp.doRequest(third);
//                    http.loginWeibo(json.getString("id"), weiboName, json.getString("profile_image_url"),LoginActivity.this);
                    s_username = weiboName;
                    s_userHeadUrl = image_url;
                    s_userId = weiboId;
                    third_type = "WEIBO";

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }

//            @Override
//            public void onComplete4binary(ByteArrayOutputStream responseOS) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onIOException(IOException e) {
//            }
//
//            @Override
//            public void onError(WeiboException e) {
//            }

        });
    }

    /**
     * 注销sinaWeibo登录
     */
    public void logoutSina() {
        CookieSyncManager.createInstance(LoginActivity.this);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeAllCookie();
    }

    public void showToast(int id) {
        SouYueToast.makeText(LoginActivity.this, id, SouYueToast.LENGTH_SHORT).show();
    }


    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.USER_LOGIN_IN_REQUEST:
                HttpJsonResponse response = request.getResponse();
                loginSuccess(response);
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        if (pd != null && pd.isShowing())
            pd.dismiss();
        IHttpError error = request.getVolleyError();
        if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR){
            HttpJsonResponse resp = error.getJson();
            int code =error.getErrorCode();
            // 604 错误自己处理 不弹出服务器的body数据，并且，服务器的body 数据有误
            if(code!=604)
            {
                Toast.makeText(this, resp.getBodyString(), Toast.LENGTH_LONG).show();
            }
        }
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        if (pd != null && pd.isShowing())
//            pd.dismiss();
//        /*if (methodName.equals("dumpEditNick")) {
//            Intent editnickIntent = new Intent(LoginActivity.this, EditNickNameActivity.class);
//            editnickIntent.putExtra("username_nickname", s_username);
//            editnickIntent.putExtra("userpwd", s_userpwd);
//            editnickIntent.putExtra("third_type", third_type);
//            startActivity(editnickIntent);
//            LoginActivity.this.finish();
//            overridePendingTransition(R.anim.left_in, R.anim.left_out);
//        }*/
//
//    }

    @Override
    public void onBackPressed() {
        dismissProgress();
        super.onBackPressed();
    }

    @Override
    public void onBackPressClick(View view) {
        new SYInputMethodManager(this).hideSoftInput();
        super.onBackPressClick(view);
    }

    /**
     * 解析逗号分割的字符串
     *
     * @return
     */
    protected String[] parseCommaIds(String s) {
        if (s == null) {
            return null;
        }
        String[] ids = s.split(",");
        return ids;
    }

    /**
     * 取消等待框
     */
    protected void dismissProgress() {
        if (pd != null) {
            try {
                pd.dismiss();
            } catch (Exception e) {

            }
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            editEnd = username.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            username.removeTextChangedListener(mTextWatcher);

            if (editEnd > 0) {
                btn_login_username_clear.setVisibility(View.VISIBLE);
            } else {
                btn_login_username_clear.setVisibility(View.GONE);
            }
            // 恢复监听器  
            username.addTextChangedListener(mTextWatcher);
        }
    };

    @Override
    protected void onResume() {
        System.out.println("onResume");

        // TODO Auto-generated method stub
        super.onResume();
        User user = SYUserManager.getInstance().getUser();
        if (user != null && SYUserManager.USER_ADMIN.equals(user.userType())) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wxBroadCastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("onActivityResult");

        //add by trade
        loginHangye(requestCode, resultCode, data);

        /**
         * sso认证
         */
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    //add by trade

    /**
     * 行业中国用户信息取到后，为全局变量赋值. <br/>
     *
     * @author liudl
     * @date 2014-10-21 下午4:10:37
     */
    public void loginHangye(int requestCode, int resultCode, Intent data) {
        if (!ConfigApi.isSouyue() && resultCode == 24 && requestCode == 76 && data != null) {
            String uid = data.getStringExtra("uid");
            String nick = data.getStringExtra("nick");
            String logo = data.getStringExtra("logo");
            String syuid = data.getStringExtra("syuid");
            String type = data.getStringExtra("type");
            UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,LoginActivity.this);
            third.setParams(uid, nick, logo, syuid,UserLoginThird.LOGIN_THIRD_HANGYE);
            mMainHttp.doRequest(third);
//            http.loginHangYe(uid, nick, logo, syuid);
            this.uid = uid;
            this.syuid = syuid;
            this.s_username = nick;
            this.type = type;
            this.s_userHeadUrl = logo;
            this.s_userId = syuid;
            this.third_type = "HANGYEZHONGGUO";
            sysp.putBoolean(SYSharedPreferences.ISTHIRDTYPE, true);
        }
    }

//    @Override
//    public void loginCallBack() {
//        finish();
//    }

    private class WXBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                pd.setMessage(context.getResources().getString(R.string.loginActivity_pd_message));
                pd.show();
                WeiXinUserInfo weiXinUserInfo = (WeiXinUserInfo) bundle.getSerializable("weiXinUserInfo");
                UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,LoginActivity.this);
                third.setParams(weiXinUserInfo.getUnionid(), weiXinUserInfo.getNickname(), weiXinUserInfo.getHeadimgurl(),null,UserLoginThird.LOGIN_THIRD_WEIXIN);
                mMainHttp.doRequest(third);
//                new LoginUtils(LoginActivity.this, LoginActivity.this, null).loninWeiXin(weiXinUserInfo);
            }
        }
    }

    private void registerWXReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zhongsou.login.wxlogin");
        wxBroadCastReceiver = new WXBroadCastReceiver();
        registerReceiver(wxBroadCastReceiver, filter);
    }
}
