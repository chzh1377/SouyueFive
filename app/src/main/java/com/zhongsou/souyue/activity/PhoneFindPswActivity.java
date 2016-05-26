package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.enterprise.api.SouyueAPIManager;
import com.zhongsou.souyue.module.SecurityInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserGetRegisterCode;
import com.zhongsou.souyue.net.personal.UserUpdatePwd;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.CustomProgressDialog;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.MobileUtils;

/**
 * 忘记密码
 * （填写验证码、设置新密码并完成）
 *  不再使用 已换成h5 - YanBin
 *
 */
public class PhoneFindPswActivity extends RightSwipeActivity implements OnClickListener {


    private EditText et_login_new_pwd,et_login_validte_code,verificodetext;
    private Button getverificode,settingsave;
    private CustomProgressDialog pd;
    private static final int PLENGTH=6;
    private static final String EXIST="1100";
    private String phoneNum;
    private boolean success=false;
    private String bindMobile;
    private boolean has_bind_phone;
    private int eventType; // eventType 2未登录找回密码，5已登录修改密码,7忘记密码

    private TextView tv_login_send_msg_phone_number;
    private boolean only_login;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int time = msg.what;
            if(time == 0){
                getverificode.setEnabled(true);
                getverificode.setText(getString(R.string.user_register_resend_phonecode_login));
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacksAndMessages(null);
                return true;
            }
            getverificode.setEnabled(false);
            getverificode.setText(getString(R.string.phonecode_resend_time, time));
            handler.sendEmptyMessageDelayed(time-1, 1000);
            return true;
        }
    }) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_msg_validate_set_password_phone);

//        http = new Http(this);
        only_login = getIntent().getBooleanExtra(LoginActivity.Only_Login, false);
        phoneNum = getIntent().getStringExtra("phone");
        initView();
        eventType = SouyueAPIManager.isLogin() ? 5 : 2;
    }

    private void initView() {
//        String title = SouyueAPIManager.isLogin() ? "登录密码" : "密码找回";
        ((TextView)findViewById(R.id.activity_bar_title)).setText("密码找回");
        et_login_validte_code = findView(R.id.et_login_validte_code);
        et_login_new_pwd=(EditText) findViewById(R.id.et_login_new_password);

        getverificode=(Button) findViewById(R.id.getverificode);
        getverificode.setOnClickListener(this);

        settingsave=(Button) findViewById(R.id.settingsave);
        settingsave.setOnClickListener(this);

        pd = CustomProgressDialog.createDialog(mContext);
        pd.setCanceledOnTouchOutside(false);

        tv_login_send_msg_phone_number = findView(R.id.tv_login_send_msg_phone_number);
        tv_login_send_msg_phone_number.setText("+86 " + phoneNum);

        handler.sendEmptyMessageDelayed(60, 100);
        //登录用户已绑定手机号，手机号不能修改
        /*bindMobile = getIntent().getStringExtra("phone");
        has_bind_phone = getIntent().getBooleanExtra("has_bind_phone", false);
        if(!has_bind_phone) {
            handler.sendEmptyMessageDelayed(60, 100);
        }*/
    }


    public void onBackPressClick(View view){
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getverificode:
                if(MobileUtils.isMobileNum(phoneNum)){
                    //判断网络
                    if(!CMainHttp.getInstance().isNetworkAvailable(this)){
                        showToast(R.string.user_login_networkerror);
                        return;
                    }
                    pd.setMessage("正在发送验证码");
                    pd.show();
//                    http.validateCode(phoneNum, eventType);  // 忘记密码，获取验证码，eventType值固定为2
                    UserGetRegisterCode getcode = new UserGetRegisterCode(HttpCommon.USER_GET_SECURITY_CODE_REQUEST,this);
                    getcode.setParams(phoneNum, eventType); // 忘记密码，获取验证码，eventType值固定为2
                    mMainHttp.doRequest(getcode);
                    getverificode.setEnabled(false);
                }else{
                    showDialog(getString(R.string.ent_phoneinfo));
                }
                break;
            case R.id.settingsave:
                getInfo();
                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                validateCodeSuccess(request.<HttpJsonResponse>getResponse());
                break;
            case HttpCommon.USER_UPDATE_PWD_REQUEST:
                updatePwdSuccess(request.<HttpJsonResponse>getResponse());
        }
    }

    public void validateCodeSuccess(HttpJsonResponse res){
        if(pd!=null){
            pd.dismiss();
        }
        handler.sendEmptyMessageDelayed(60, 100);
        showDialog(getString(R.string.ent_verificodetip_2));

    }

    private void saveInfo(SecurityInfo securityinfo){
        //判断网络
        if(!CMainHttp.getInstance().isNetworkAvailable(this)){
            showToast(R.string.user_login_networkerror);
            return;
        }
        pd.setMessage("正在保存，请稍候...");
        pd.show();
        UserUpdatePwd update = new UserUpdatePwd(HttpCommon.USER_UPDATE_PWD_REQUEST,this);
        update.setParams(securityinfo.getMobile(), securityinfo.getPassword(), securityinfo.getVerifyNum(), eventType);
        mMainHttp.doRequest(update);
//        http.updatePwd(securityinfo.getMobile(), securityinfo.getPassword(), securityinfo.getVerifyNum(), eventType);
    }


    public void updatePwdSuccess(HttpJsonResponse res){
        int statusCode = res.getCode();
        if(statusCode == 200){
            success=true;
            UIHelper.ToastMessage(this, "密码设置成功");
            try {
                Intent intent = new Intent(PhoneFindPswActivity.this,LoginActivity.class);
                intent.putExtra(LoginActivity.Only_Login, only_login);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                this.finish();
            } catch (Exception e) {}
        }else{

        }
    }

    @Override
    public void onHttpError(IRequest request) {
        if(pd!=null){
            pd.dismiss();
        }
        IHttpError error = request.getVolleyError();
        int type = error.getErrorType();
        switch (request.getmId()){
            case HttpCommon.USER_GET_SECURITY_CODE_REQUEST:
                if (type == IHttpError.TYPE_SERVER_ERROR){
                    showDialog(error.getmErrorMessage());
                    getverificode.setEnabled(true);
                    return;
                }
                break;
            case HttpCommon.USER_UPDATE_PWD_REQUEST:
                if (type == IHttpError.TYPE_SERVER_ERROR){
                    showDialog(error.getJson().getBodyString());
                    if(pd!=null){
                        pd.dismiss();
                    }
                    return;
                }

        }

        getverificode.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PhoneFindPswActivity.this, R.string.user_login_networkerror, Toast.LENGTH_SHORT).show();

                getverificode.setEnabled(true);
            }
        });
    }


    /**
     * 获取安全设置信息并且验证
     */
    private void getInfo(){
//        if(StringUtils.isNotEmpty(bindMobile)) {
//            phoneNum = bindMobile;
//        } else {
//            phoneNum=et_login_new_pwd.getText().toString().trim();
//        }
        String password= et_login_new_pwd.getText().toString();
        String verificode= et_login_validte_code.getText().toString().trim();

        if(validateVCode(verificode)){//验证验证码
            return;
        }else if(!validatePassword(password,null)){//验证密码
            return;
        }

        SecurityInfo securityinfo=new SecurityInfo();
        securityinfo.setMobile(phoneNum);
        securityinfo.setPassword(password);
        securityinfo.setVerifyNum(verificode);
        securityinfo.setName("");

      /*  if(StringUtils.isNotEmpty(bindMobile)) {
            saveInfo(securityinfo);
        } else */
        if(MobileUtils.isMobileNum(phoneNum)){
            saveInfo(securityinfo);
        } else {
            showDialog(getString(R.string.ent_phoneinfo));
        }

    }
    //验证密码
    private boolean validatePassword(String password,String rpassword){
        if(password==null||password.length()==0){
            showDialog(getString(R.string.user_password_no_empty_login));
            return false;
        }else if(password.length() < 6 ||password.length() > 14){
            showDialog(getString(R.string.user_password_len_error_login));
            return false;
        }
        return true;
    }
    //验证验证码
    private boolean validateVCode(String codeStr) {
        if (!TextUtils.isEmpty(codeStr)) {
            return false;
        } else {
            showDialog(getString(R.string.ent_vcode_tip));
        }

        return true;
    }
    //验证信息是否齐全
    private boolean validateAllEdit(String passwords,String rpasswords,String verificode) {

        if (TextUtils.isEmpty(passwords)|| TextUtils.isEmpty(verificode)) {
            return false;
        }
        return true;
    }
    private void showDialog(String message){
//        new AlertDialog.Builder(this)
//                .setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if(success){
//                    finish();
//                }
//            }
//
//        }).show();

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

    public void showToast(int resId) {
        SouYueToast.makeText(this, getResources().getString(resId),
                0).show();
    }
}
