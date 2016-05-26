package com.zhongsou.souyue.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.zhongsou.souyue.net.personal.UserRegister;
import com.zhongsou.souyue.net.volley.*;
import com.zhongsou.souyue.ui.CustomProgressDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.UserInfoUtils;
import com.zhongsou.souyue.utils.Utility;

/**
 * 手机注册(登陆)-填写验证码，完成注册
 */
public class PhoneRegisterValidatorActivity extends RightSwipeActivity implements
        OnClickListener, OnFocusChangeListener {

//    private static final int HTTP_VALIDATE_CODE = 0x00100;
    private TextView user_ment, tv_login_send_msg_phone_number;
    private EditText nickname, pwd1, pwd2, et_login_validte_code, et_login_validte_nicheng;
    private Button complete;
    private CheckBox checkbox;
    private Boolean pwdEmpty, pwdLength;
    private CustomProgressDialog pd;
    private int nickname_length;
    private int pwd1_length;
//    private Http http;
    private String phoneNum;
    private Button btn_resend;
    private String codeStr;
    private boolean fromGame;
    private User user;

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

    class TimerCount extends CountDownTimer {

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

    private String sms_content = "";
    private SmsObserver smsObserver;

    public class SmsObserver extends ContentObserver {

        private ContentResolver mResolver;

        public SmsObserver(ContentResolver mResolver) {
            super(new Handler());
            this.mResolver = mResolver;
        }

        @Override
        public void onChange(boolean selfChange) {
            Cursor mCursor = mResolver.query(Uri.parse("content://sms/inbox"),
                    new String[]{"address", "body"}, null, null, "date desc");
            if (mCursor == null) {
                return;
            } else {
                int i = 0;
                while (mCursor.moveToNext() && i < 60) {
                    int bodyIndex = mCursor.getColumnIndex("body");
                    if (bodyIndex != -1) {
                        String msg = mCursor.getString(bodyIndex);
                        if (msg.indexOf("搜悦") > -1) {
                            Log.i("SMS", mCursor.getString(bodyIndex));
                            sms_content = msg;
                            return;
                        }
                        i++;
                    }
                }
            }

            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNum = getIntent().getStringExtra("phone");
        setContentView(R.layout.login_send_msg_validate_phone);
        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();
//        http = new Http(this);
        pd = CustomProgressDialog.createDialog(mContext);

        btn_resend = (Button) findViewById(R.id.btn_resend);
        btn_resend.setOnClickListener(this);
//        handler.sendEmptyMessageDelayed(60, 1000);
        TimerCount timerCount = new TimerCount(60 * 1000, 1000);
        timerCount.start();

        et_login_validte_code = findView(R.id.et_login_validte_code);
        complete = findView(R.id.btn_login_complete);
        tv_login_send_msg_phone_number = findView(R.id.tv_login_send_msg_phone_number);
        tv_login_send_msg_phone_number.setText("+86 " + phoneNum);

        complete.setOnClickListener(this);

        et_login_validte_code.setOnFocusChangeListener(this);
        ((TextView) findViewById(R.id.activity_bar_title)).setText(getResString(R.string.user_register_title_login));

        et_login_validte_nicheng = findView(R.id.et_login_validte_nicheng);

        smsObserver = new SmsObserver(getContentResolver());
        Uri smsUri = Uri.parse("content://sms");
        getContentResolver().registerContentObserver(smsUri, true, smsObserver);
    }

    @Override
    protected void onDestroy() {
        getContentResolver().unregisterContentObserver(smsObserver);
        mMainHttp.cancel(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_resend:
                //判断网络
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    showToast(R.string.user_login_networkerror);
                    return;
                }
                showDialog(getString(R.string.phonecode_sending));
//                http.validateCode(phoneNum, true);
                UserGetRegisterCode validate = new UserGetRegisterCode(HttpCommon.USER_GET_SECURITY_CODE_REQUEST,this);
                validate.setParams(phoneNum);
                validate.setTag(this);
                mMainHttp.doRequest(validate);
                btn_resend.setEnabled(false);
                btn_resend.setText(R.string.phonecode_resend);
                break;
            case R.id.btn_login_complete:
                getWidgetInfo();
                if (validateVCode()) {//验证验证码
                    return;
                } else {
                    //判断网络
                    if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                        showToast(R.string.user_login_networkerror);
                        return;
                    }
                    String nickName = et_login_validte_nicheng.getText().toString().trim();
                    if (Utility.validateNickName(et_login_validte_nicheng, PhoneRegisterValidatorActivity.this)) {
                        // 提交数据，进行注册
                        pd.setMessage(getResString(R.string.user_registering));
                        pd.setCanceledOnTouchOutside(false);
                        pd.show();
                        //手机注册
                        UserRegister register = new UserRegister(HttpCommon.USER_PHONE_REGISTER_REQUEST,this);
                        register.setParams(phoneNum, nickName, "", 1, codeStr, SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_CITY, "")
                                , SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_PROVINCE, ""), sms_content);
                        register.setTag(this);
                        mMainHttp.doRequest(register);
//                        http.register31Post(phoneNum, nickName, "", 1, codeStr, SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_CITY, "")
//                                , SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_PROVINCE, ""), sms_content);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                dismissDialog();
                TimerCount timerCount = new TimerCount(60 * 1000, 1000);
                timerCount.start();
                btn_resend.setEnabled(false);
                Toast.makeText(this, R.string.phonecode_receive, Toast.LENGTH_LONG).show();
                break;
            case HttpCommon.USER_PHONE_REGISTER_REQUEST:
                registerSuccess(request.<HttpJsonResponse>getResponse());
        }
    }

//    public void validateCodeSuccess(HttpJsonResponse res, AjaxStatus status) {
//        int statusCode = res.getCode();
//        dismissDialog();
//        if (statusCode == 200) {
//            handler.sendEmptyMessageDelayed(60, 100);
//            btn_resend.setEnabled(false);
//            Toast.makeText(this, R.string.phonecode_receive, Toast.LENGTH_LONG).show();
//        } else {
//            handler.sendEmptyMessageDelayed(0, 500);
//            Toast.makeText(this, res.getBodyString(), Toast.LENGTH_LONG).show();
//        }
//    }

    public void dismissDialog() {
        if (pd != null && pd.isShowing())
            pd.dismiss();
    }

    public void showDialog(String msg) {
        if (pd == null) {
            pd = CustomProgressDialog.createDialog(mContext);
        }
        pd.setCanceledOnTouchOutside(true);
        pd.setMessage(msg);
        if (!pd.isShowing())
            pd.show();
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


    private void registerUserSucc(User u){
        pd.dismiss();
        if (u != null) {
            user = u;
            u.userType_$eq("1");
            SYUserManager.getInstance().setUser(u);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
            new SYInputMethodManager(this).hideSoftInput();
            sysp.putBoolean(SYSharedPreferences.KEY_REGISTERSUCCESS, true);
            Intent i = new Intent(ConstantsUtils.LINK);
            i.putExtra(com.tuita.sdk.Constants.TYPE, 40);
            sendBroadcast(i);
            //判断是否赠送饮料
//            if (u.getGiveDrink() > 0)
//                showYouBaoDialog();
//            else
                regSuccess();

            //统计  注册成功
            UpEventAgent.onReg(this, "手机");
            UpEventAgent.onLogin(this);
        }
    }
    public void registerSuccess(HttpJsonResponse response) {
        User user = new Gson().fromJson(response.getBody(), User.class);
        registerUserSucc(user);
        if ((StringUtils.isNotEmpty(response.getHead().get("guide_url")))) {
            TaskCenterInfo taskCenterInfo = new TaskCenterInfo();
            taskCenterInfo.setGuide_isforced(response.getHead().get("guide_isforced").getAsString());
            taskCenterInfo.setGuide_url(response.getHead().get("guide_url").getAsString());
            taskCenterInfo.setGuide_msg(response.getHead().get("guide_msg").getAsString());
            UserInfoUtils.jumpToFillUser(taskCenterInfo);
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        dismissDialog();
        IHttpError error = request.getVolleyError();
        switch (request.getmId()){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR){
//                    handler.sendEmptyMessageDelayed(0, 500);
                    btn_resend.setEnabled(true);
                    btn_resend.setText(R.string.user_register_resend_phonecode_login);
                    HttpJsonResponse respo = error.getJson();
                    Toast.makeText(this, respo.getBodyString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, R.string.phonecode_senderror, Toast.LENGTH_LONG).show();
//                    handler.removeCallbacksAndMessages(null);
                    btn_resend.setEnabled(true);
                    btn_resend.setText(R.string.phonecode_resend);
                }
                break;
            case HttpCommon.USER_PHONE_REGISTER_REQUEST:
                if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR){
//                    handler.sendEmptyMessageDelayed(0, 500);
                    btn_resend.setEnabled(true);
                    btn_resend.setText(R.string.user_register_resend_phonecode_login);
                    HttpJsonResponse respo = error.getJson();
                    Toast.makeText(this, respo.getBodyString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, R.string.tg_dialog_noconn, Toast.LENGTH_LONG).show();
                }
        }
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        dismissDialog();
//        if ("validateCode".equals(methodName)) {
//            Toast.makeText(this, R.string.phonecode_senderror, Toast.LENGTH_LONG).show();
//            handler.removeCallbacksAndMessages(null);
//            btn_resend.setEnabled(true);
//            btn_resend.setText(R.string.phonecode_resend);
//        }
//    }

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
        if (!TextUtils.isEmpty(codeStr) && codeStr.matches("\\d{6}")) {//验证码为6位数字
            codeEmpty = false;
        } else {
            showToast(R.string.vcode_tip);
        }

        return codeEmpty;
    }


    public void showToast(int resId) {
        SouYueToast.makeText(this, getResources().getString(resId),
                0).show();
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


    private void regSuccess() {
        if (!fromGame) {
            IntentUtil.goHomeMine(this);
        } else {
            Intent mIntent = new Intent("subscribeState");
            sendBroadcast(mIntent);
            setResult(RESULT_OK);
        }
        finish();
    }
}
