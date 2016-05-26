package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserGetRegisterCode;
import com.zhongsou.souyue.net.personal.UserLogin;
import com.zhongsou.souyue.net.volley.*;
import com.zhongsou.souyue.platform.TradeBusinessApi;
import com.zhongsou.souyue.ui.CustomProgressDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.UserInfoUtils;

/**
 * 短信登陆-填写验证码，完成登陆
 * 
 */
public class MsgLoginValidatorActivity extends RightSwipeActivity implements OnClickListener, OnFocusChangeListener {

    public static final String Only_Login = "Only_Login";
    private TextView tv_login_send_msg_phone_number;
    private EditText et_login_validte_code;
    private Button complete;
    private CustomProgressDialog pd;
//    private Http http;
    private String phoneNum;
    private Button btn_resend;
    private String codeStr;
    // add by trade
    private RelativeLayout user_nicheng_in_login;
    private TextView nicheng_warn;

//    Handler handler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            int time = msg.what;
//            if (time == 0) {
//                btn_resend.setEnabled(true);
//                btn_resend.setText(R.string.user_register_resend_phonecode_login);
//                handler.removeCallbacksAndMessages(null);
//                return false;
//            }
//            btn_resend.setEnabled(false);
//            btn_resend.setText(getString(R.string.phonecode_resend_time, time));
//            handler.sendEmptyMessageDelayed(time - 1, 1000);
//            return false;
//        }
//    });

    class TimerCount extends CountDownTimer{

        public TimerCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_resend.setEnabled(false);
            btn_resend.setText(getString(R.string.phonecode_resend_time, millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            btn_resend.setEnabled(true);
            btn_resend.setText(R.string.user_register_resend_phonecode_login);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNum = getIntent().getStringExtra("phone");
        setContentView(R.layout.login_send_msg_validate_phone);
        // add by trade
//        showPager = getIntent().getStringExtra(TAG);//没有用到所以去掉
//        shopAction = getIntent().getStringExtra("shopAction");

        if (sysp == null) sysp = SYSharedPreferences.getInstance();
//        http = new Http(this);
        pd = CustomProgressDialog.createDialog(mContext);

//        fromGame = getIntent().getBooleanExtra(TigerGameActivity.LOGIN_TAG, false);

        btn_resend = (Button) findViewById(R.id.btn_resend);
        btn_resend.setOnClickListener(this);
//        handler.sendEmptyMessageDelayed(60, 1000);

        TimerCount timerCount = new TimerCount(60 * 1000 , 1000);
        timerCount.start();

        et_login_validte_code = findView(R.id.et_login_validte_code);
        complete = findView(R.id.btn_login_complete);
        user_nicheng_in_login = (RelativeLayout) findViewById(R.id.user_nicheng_in_login);
        nicheng_warn = (TextView) findViewById(R.id.nicheng_warn);
        user_nicheng_in_login.setVisibility(View.GONE);
        nicheng_warn.setVisibility(View.GONE);
        tv_login_send_msg_phone_number = findView(R.id.tv_login_send_msg_phone_number);
        tv_login_send_msg_phone_number.setText("+86 " + phoneNum);
        // 用户协议
        // user_ment.setOnClickListener(this);
        complete.setOnClickListener(this);

        et_login_validte_code.setOnFocusChangeListener(this);
        ((TextView) findViewById(R.id.activity_bar_title)).setText(getResString(R.string.user_login_title_login));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resend:
                // 判断网络
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    showToast(R.string.user_login_networkerror);
                    return;
                }
                showDialog(getString(R.string.phonecode_sending));
                // ==添加短信登陆的验证码类型
//                http.validateCode(phoneNum, 6);
                UserGetRegisterCode getcode = new UserGetRegisterCode(HttpCommon.USER_GET_SECURITY_CODE_REQUEST,this);
                getcode.setParams(phoneNum, LoginInputPhoneNumActivity.EVENTTYPE_MSGLOGIN);
                mMainHttp.doRequest(getcode);
                btn_resend.setEnabled(false);
                btn_resend.setText(R.string.phonecode_resend);
                break;
            case R.id.btn_login_complete:
                getWidgetInfo();
                if (validateVCode()) {// 验证验证码
                    return;
                } else {
                    // 判断网络
                    if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                        showToast(R.string.user_login_networkerror);
                        return;
                    }
                    // 提交数据，登录
                    pd.setMessage(getResString(R.string.user_login_pd_login));
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    UserLogin login = new UserLogin(HttpCommon.USER_LOGIN_IN_REQUEST,this);
                    login.setParams(phoneNum, Integer.parseInt(codeStr));
                    mMainHttp.doRequest(login);
//                    http.login(phoneNum, Integer.parseInt(codeStr),MsgLoginValidatorActivity.this);
                }
                break;

            default:
                break;
        }
    }

    public void validateCodeSuccess(HttpJsonResponse res) {
        dismissDialog();
//        handler.sendEmptyMessageDelayed(60, 100);

        TimerCount timerCount = new TimerCount(60 * 1000 , 1000);
        timerCount.start();

        btn_resend.setEnabled(false);
        Toast.makeText(this, R.string.phonecode_receive, Toast.LENGTH_LONG).show();
    }

    public void dismissDialog() {
        if (pd != null && pd.isShowing()) pd.dismiss();
    }

    public void showDialog(String msg) {
        if (pd == null) {
            pd = CustomProgressDialog.createDialog(mContext);
        }
        pd.setCanceledOnTouchOutside(true);
        pd.setMessage(msg);
        if (!pd.isShowing()) pd.show();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        getWidgetInfo();

        switch (v.getId()) {
            case R.id.et_vCode:
                if (hasFocus == false) {
                    validateVCode();
                }
                break;
            default:
                break;
        }
    }

    private void getWidgetInfo() {
        codeStr = et_login_validte_code.getText().toString().trim();

    }



    @Override
    public void onBackPressed() {
        if (pd != null) {
            pd.dismiss();
        }
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        super.onBackPressed();
    }

    private boolean validateAllEdit() {
        String code = et_login_validte_code.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        return true;
    }

    private boolean validateVCode() {
        boolean codeEmpty = true;
        if (!TextUtils.isEmpty(codeStr) && codeStr.matches("\\d{6}")) {// 验证码为6位数字
            codeEmpty = false;
        } else {
            showToast(R.string.vcode_tip);
        }

        return codeEmpty;
    }

    public void showToast(int resId) {
        SouYueToast.makeText(this, getResources().getString(resId), 0).show();
    }

    // 获取资源String
    public String getResString(int id) {
        return this.getResources().getString(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = SYUserManager.getInstance().getUser();
        if (user != null && SYUserManager.USER_ADMIN.equals(user.userType()) && !(user.getGiveDrink() > 0)) {
            finish();
        }
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        int id = _request.getmId();
        switch (id){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                validateCodeSuccess(_request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.USER_LOGIN_IN_REQUEST:
                HttpJsonResponse response = _request.getResponse();
                loginSuccess(response);
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        dismissDialog();
        switch (request.getmId()){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                IHttpError error = request.getVolleyError();
                btn_resend.setEnabled(true);
                btn_resend.setText(R.string.user_register_resend_phonecode_login);
                int errortype = error.getErrorType();
                if (errortype == IHttpError.TYPE_SERVER_ERROR) {
                    HttpJsonResponse resp = error.getJson();
                    Toast.makeText(this, resp.getBodyString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, R.string.phonecode_senderror, Toast.LENGTH_LONG).show();
//                    handler.removeCallbacksAndMessages(null);
                    btn_resend.setEnabled(true);
                    btn_resend.setText(R.string.phonecode_resend);
                }

        }
    }

    public void loginSuccess(User user) {
        Msglogin(user);
    }
    private void Msglogin(User user){
        sysp.putBoolean(SYSharedPreferences.KEY_USER_UPDATE, true);
        if (pd != null && pd.isShowing()) pd.dismiss();
        // logoutSns();// 第三方登录成功后，注销信息
        if (user != null) {
            user.userType_$eq("1");
            // Log.i("Tuita",
            // "loginSuccess:" + user + ", hashcode:" + user.hashCode());
            SYUserManager.getInstance().setUser(user);
//            ImserviceHelp.getInstance().im_logout();
            new SYInputMethodManager(this).hideSoftInput();
            Intent i = new Intent(ConstantsUtils.LINK);
            i.putExtra(com.tuita.sdk.Constants.TYPE, 40);
            sendBroadcast(i);
            // 统计
            UpEventAgent.onLogin(this);
            boolean onlyLogin = getIntent().getBooleanExtra(Only_Login, false);
            TradeBusinessApi.getInstance().msgLoginSuccess(this, onlyLogin);
        }
    }
    public void loginSuccess(HttpJsonResponse response) {
        User user=new Gson().fromJson(response.getBody(), User.class);
        Msglogin(user);
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
        if (StringUtils.isNotEmpty(response.getHead().get("cpmRecommend"))) {
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_ADMIN_SPECIAL, user.userId() + "," + response.getHead().get("cpmRecommend").getAsString());
        }
    }
}
