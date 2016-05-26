package com.zhongsou.souyue.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.personal.UserGetRegisterCode;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.ui.CustomProgressDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.TelephonyInfo;

/**
 * 用户注册、短信登陆、找回密码（公用）--
 * 输入手机号，获取手机验证码
 *
 */
public class LoginInputPhoneNumActivity extends RightSwipeActivity implements
        OnClickListener {
    /**
     *  1：默认注册验证码，2：未登录找回登录密码，3，设置或修改支付密码
     *  4:绑定手机号短信  5：登录修改密码 6.发短信登录验证码
     */
    public static final int EVENTTYPE_REG = 1;
    public static final int EVENTTYPE_FINDPSW = 2;
    public static final int EVENTTYPE_MSGLOGIN = 6;

    private Button btn_send,btn_login_phonenum_clear;
    //    private Http http;
    private CMainHttp mHttp;
    private EditText et_phone;
    private CustomProgressDialog pd;
    private String phoneNum;
    private boolean fromGame;
    private boolean only_login;

    private TextView tv_common_register_login;
    private String login_type;

    private TextView user_ment;
    private CheckBox cb_privacy;
    private TextView tv_link_service;


    private BroadcastReceiver  receiver = new BroadcastReceiver() {
        public void onReceive(android.content.Context context, Intent intent) {
            Intent mIntent = new Intent(LoginInputPhoneNumActivity.this,CommonStringsApi.getHomeClass());
            startActivity(mIntent);
            LoginInputPhoneNumActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        login_type = getIntent().getStringExtra(LoginActivity.LOGIN_TYPE);
//		fromGame = getIntent().getBooleanExtra(TigerGameActivity.LOGIN_TAG, false);
        only_login = getIntent().getBooleanExtra(LoginActivity.Only_Login, false);

        initView();

//        http = new Http(this);
        mHttp = CMainHttp.getInstance();
        //注册跳转广播
        IntentFilter filter = new IntentFilter("ACTION_LOGOUT_TO_HOME");
        registerReceiver(receiver, filter);
    }

    /**
     * 根据不同的type加载layout
     *
     */
    private void initView() {

        if(login_type !=null ){

            if(LoginActivity.FINDPSW.equals(login_type)) {  //找回密码，现在“忘记密码”点击事件已经跳转到H5 - 2015-12-23
                setContentView(R.layout.login_find_psw_phone);

                TextView title = (TextView)findViewById(R.id.activity_bar_title);
                title.setText(R.string.user_find_psw_title_login);

            }else if(LoginActivity.PHONEREG.equals(login_type)) {  //手机号注册
                setContentView(R.layout.login_register_phone);

                //新增普通登陆
                tv_common_register_login = findView(R.id.tv_common_register_login);
                tv_common_register_login.setOnClickListener(this);

                TextView title = (TextView)findViewById(R.id.activity_bar_title);
                title.setText(R.string.user_register_title_login);
                //用户协议
                cb_privacy = findView(R.id.cb_privacy_agree);
                cb_privacy.setOnClickListener(this);
                cb_privacy.setChecked(true);

                tv_link_service = findView(R.id.tv_link_service);
                ClickServicePrivacy(LoginInputPhoneNumActivity.this,tv_link_service);

            }else if(LoginActivity.MSGLOGIN.equals(login_type)){  //短信登陆
                setContentView(R.layout.login_msg_login_phone);

                TextView title = (TextView)findViewById(R.id.activity_bar_title);
                title.setText(R.string.user_login_title_login);
            }
        }
        //清除文本内容
        btn_login_phonenum_clear = (Button) findViewById(R.id.btn_login_phonenum_clear);
        btn_login_phonenum_clear.setOnClickListener(this);

        btn_send = (Button)findViewById(R.id.btn_send); //“获得验证码”和“下一步”按钮
        btn_send.setOnClickListener(this);

        et_phone = (EditText)findViewById(R.id.et_phone);   //手机号输入框
        et_phone.addTextChangedListener(mTextWatcher);

        String phoneNum = getIntent().getStringExtra("phone");
        if(TextUtils.isEmpty(phoneNum))
            phoneNum = TelephonyInfo.getNativePhoneNumber(this);
        if(!TextUtils.isEmpty(phoneNum)){
            et_phone.setText(TelephonyInfo.checkPhoneNum(phoneNum));
            btn_login_phonenum_clear.setVisibility(View.VISIBLE);
        }else {
            btn_login_phonenum_clear.setVisibility(View.GONE);
        }
    }


    public static void ClickServicePrivacy(final Activity context,TextView tv_link_service) {
        SpannableStringBuilder spans = new SpannableStringBuilder(tv_link_service.getText());
        spans.clearSpans();
        spans.setSpan(new ForegroundColorSpan(Color.parseColor("#5592c6")), 10, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ClickableSpan clickSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget)
            {
                widget.invalidate();
                Intent privacyIntent = new Intent(context,
                        UserAgreementActivity.class);
                context.startActivity(privacyIntent);
                context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };

        spans.setSpan(clickSpan, 10, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_link_service.setText(spans);
        //设置TextView可点击
        tv_link_service.setMovementMethod(LinkMovementMethod.getInstance());
        //防止点击出现背景颜色
        tv_link_service.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        phoneNum = et_phone.getText().toString();

//		int eventType = IntentUtil.isLogin() ? 5 : 2;

        switch (v.getId()) {
            case R.id.btn_send:

                if(LoginActivity.PHONEREG.equals(login_type)) { //如果是手机号注册
                    if (!cb_privacy.isChecked()) {//验证是否勾选协议
                        showToast(R.string.user_agreement_empty);
                        return;
                    }
                }

                if(TelephonyInfo.isMobileNum(phoneNum)){
                    //判断网络
                    if(!CMainHttp.getInstance().isNetworkAvailable(this)){
                        showToast(R.string.user_login_networkerror);
                        return;
                    }
                    showDialog(getString(R.string.phonecode_sending));
                    pd.show();

                    //==判断获取验证码类型
                    if(LoginActivity.FINDPSW.equals(login_type)) {
//					http.validateCode(phoneNum, EVENTTYPE_FINDPSW);
                        UserGetRegisterCode getcode = new UserGetRegisterCode(HttpCommon.USER_GET_SECURITY_CODE_REQUEST,this);
                        getcode.setParams(phoneNum, LoginInputPhoneNumActivity.EVENTTYPE_FINDPSW);
                        mHttp.doRequest(getcode);
                    }else if(LoginActivity.PHONEREG.equals(login_type)) {
//					http.validateCode(phoneNum, EVENTTYPE_REG);
                        UserGetRegisterCode getcode = new UserGetRegisterCode(HttpCommon.USER_GET_SECURITY_CODE_REQUEST,this);
                        getcode.setParams(phoneNum, LoginInputPhoneNumActivity.EVENTTYPE_REG);
                        mHttp.doRequest(getcode);
                    }else if(LoginActivity.MSGLOGIN.equals(login_type)){
//					http.validateCode(phoneNum, EVENTTYPE_MSGLOGIN);
                        UserGetRegisterCode getcode = new UserGetRegisterCode(HttpCommon.USER_GET_SECURITY_CODE_REQUEST,this);
                        getcode.setParams(phoneNum, LoginInputPhoneNumActivity.EVENTTYPE_MSGLOGIN);
                        mHttp.doRequest(getcode);
                    }

                    btn_send.setEnabled(false);
                }else{
                    Toast.makeText(LoginInputPhoneNumActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_common_register_login:
                IntentUtil.gotoWeb(this, UrlConfig.CommonRegister, "interactWeb");
//			Intent intent = new Intent(this, CommonRegisterActivity.class);
//			intent.putExtra(TigerGameActivity.LOGIN_TAG, fromGame);
//			intent.putExtra("phone", phoneNum);
//			startActivity(intent);
//			overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;
            case R.id.btn_login_phonenum_clear:
                et_phone.setText("");
                break;
            default:
                break;
        }

    }

    @Override
    public void onHttpResponse(IRequest _request) {
        switch (_request.getmId()){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                validateCodeSuccess(_request.<HttpJsonResponse>getResponse());
        }
    }

    public void validateCodeSuccess(HttpJsonResponse res){
        dismissDialog();
        Toast.makeText(this, R.string.phonecode_receive, Toast.LENGTH_LONG).show();
        Intent intent = null;
        if(login_type != null){

            if(LoginActivity.MSGLOGIN.equals(login_type)) {
                intent = new Intent(this, MsgLoginValidatorActivity.class);
            }else if(LoginActivity.FINDPSW.equals(login_type)){
                intent = new Intent(this, PhoneFindPswActivity.class);
            }else if(LoginActivity.PHONEREG.equals(login_type)){
                intent = new Intent(this, PhoneRegisterValidatorActivity.class);
            }

//				intent.putExtra(TigerGameActivity.LOGIN_TAG, fromGame);
            intent.putExtra(LoginActivity.Only_Login, only_login);
            intent.putExtra("phone", phoneNum);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            btn_send.postDelayed(new Runnable() {

                @Override
                public void run() {

                    btn_send.setEnabled(true);
                }
            }, 60*1000);
        }
    }

    @Override
    public void onHttpError(IRequest _request) {
        dismissDialog();
        btn_send.setEnabled(true);
        IHttpError error = _request.getVolleyError();
        switch (_request.getmId()){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR){
                    HttpJsonResponse resp = error.getJson();
                    Toast.makeText(LoginInputPhoneNumActivity.this,resp.getBodyString(), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, R.string.phonecode_senderror, Toast.LENGTH_LONG).show();
                }
        }
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus status) {
//        dismissDialog();
//        if("validateCode".equals(methodName)){
//            Toast.makeText(this, R.string.phonecode_senderror, Toast.LENGTH_LONG).show();
//        }
//        btn_send.setEnabled(true);
//    }

    public void dismissDialog(){
        if(pd!=null && pd.isShowing())
            pd.dismiss();
    }

    public void showDialog(String msg){
        if(pd ==null){
            pd = CustomProgressDialog.createDialog(mContext);
        }
        pd.setMessage(msg);
        if(!pd.isShowing())
            pd.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_send.setEnabled(true);
        User user = SYUserManager.getInstance().getUser();
        if (user != null && SYUserManager.USER_ADMIN.equals(user.userType())) {
            finish();
        }
    }

    @Override
    public void onBackPressClick(View view) {
        new SYInputMethodManager(this).hideSoftInput();
        super.onBackPressClick(view);
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

            editEnd  = et_phone.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            et_phone.removeTextChangedListener(mTextWatcher);

            if(editEnd  > 0) {
                btn_login_phonenum_clear.setVisibility(View.VISIBLE);
            }else {
                btn_login_phonenum_clear.setVisibility(View.GONE);
            }
            // 恢复监听器  
            et_phone.addTextChangedListener(mTextWatcher);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) {
            unregisterReceiver(receiver);
        }

    }
    public void showToast(int resId) {
        SouYueToast.makeText(this, getResources().getString(resId),
                0).show();
    }

}
