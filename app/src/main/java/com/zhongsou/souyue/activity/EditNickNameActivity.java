package com.zhongsou.souyue.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserLogin;
import com.zhongsou.souyue.net.personal.UserLoginThird;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.TradeBusinessApi;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utility;

/**
 * 编辑昵称
 *
 * @author huanglb@zhongsou.com
 */
public class EditNickNameActivity extends RightSwipeActivity implements
        OnClickListener,TextWatcher {
    public static final String INTENT_USER = "EditNickNameActivity.USER";
	private static final int TEXT_MAX_LENGTH = 20;
    //add by trade
    protected SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    private EditText nickName;
    private Button nickClean;
    private Button nickSubmit;
    private ProgressDialog pd;
//    private CMainHttp mMainHttp;
    private String newNick;
    private String userId;
    private String userHeadUrl;

    private User user;
    
    //编辑昵称
    private CharSequence temp = "";
	private int selectionStart;
	private int selectionEnd;
	private TextView mLimitCount;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editnickname);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        nickName = findView(R.id.et_nickname);
        nickClean = findView(R.id.btn_nick_clear);
        nickSubmit = findView(R.id.btn_edit_nick_submit);
        mLimitCount = findView(R.id.tv_text_limit_count);
        nickName.addTextChangedListener(this);
        nickClean.setOnClickListener(this);
        nickSubmit.setOnClickListener(this);
//        mMainHttp  = CMainHttp.getInstance();
        initData();
    }

	private void initData() {
		Intent intent = getIntent();
        if (intent != null)
            user = (User) intent.getSerializableExtra(INTENT_USER);

        if (user != null && !StringUtils.isEmpty(user.name())) {
            nickName.setText(user.name());
            nickName.setSelection(user.name().length());
        }
        ((TextView) findViewById(R.id.activity_bar_title)).setText(getString(R.string.edit_nick));
        if(nickName != null && mLimitCount != null) {
        	mLimitCount.setText(String.format(getString(R.string.edit_nick_limit),nickName.length() <=0 ? 0 : nickName.getText().toString().length()));
        }
   }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_nick_clear:
                if (nickName != null && mLimitCount != null) {
                	nickName.setText("");
                	mLimitCount.setText(R.string.edit_nick_empty);
                }
                break;
            case R.id.btn_edit_nick_submit:
                if (!CMainHttp.getInstance().isNetworkAvailable(this)) {
                    showToast(R.string.networkerror);
                    return;
                }

//                http = new Http(EditNickNameActivity.this);
                if (Utility.validateNickName(nickName, EditNickNameActivity.this)) {
                    newNick = nickName.getText().toString().trim();
                    if (!StringUtils.isEmpty(getStringExtra("userpwd"))) {
                        pd.setMessage(this.getResources().getString(
                                R.string.again_logining));
                        // 编辑昵称后，重新登录
//                        http.login(getStringExtra("username_nickname"),
//                                getStringExtra("userpwd"), newNick,EditNickNameActivity.this);
                        UserLogin login = new UserLogin(HttpCommon.USER_LOGIN_IN_REQUEST,this);
                        login.setParams(getStringExtra("username_nickname"),
                                getStringExtra("userpwd"), newNick);
                        mMainHttp.doRequest(login);
                    } else if (!StringUtils.isEmpty(getStringExtra("third_type"))) {
                        if (getStringExtra("third_type").equals("RENREN")) {
                            UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,EditNickNameActivity.this);
                            third.setParams(userId, newNick, userHeadUrl,null,UserLoginThird.LOGIN_THIRD_RENREN);
                            mMainHttp.doRequest(third);
//                            http.loginRenRen(userId, newNick, userHeadUrl,EditNickNameActivity.this);
                        } else if (getStringExtra("third_type").equals("WEIBO")) {
                            //这里的登录回调还是login回调，所以id用登录的
                            UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,EditNickNameActivity.this);
                            third.setParams(userId, newNick, userHeadUrl,null,UserLoginThird.LOGIN_THIRD_WEIBO);
                            mMainHttp.doRequest(third);
//                            http.loginWeibo(userId, newNick, userHeadUrl,EditNickNameActivity.this);
                        } else if (getStringExtra("third_type").equals("QQ")) {
                            //这里的登录回调还是login回调，所以id用登录的
                            UserLoginThird third = new UserLoginThird(HttpCommon.USER_LOGIN_IN_REQUEST,EditNickNameActivity.this);
                            third.setParams(userId, newNick, userHeadUrl,null,UserLoginThird.LOGIN_THIRD_QQ);
                            mMainHttp.doRequest(third);
//                            http.loginQQ(userId, newNick, userHeadUrl,EditNickNameActivity.this);
                        }
                    } else {
                        // 编辑昵称后，修改昵称
                        pd.setMessage(this.getResources().getString(R.string.update_nicknameing));
                        if (user != null) {
                            UserRepairInfo info = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
                            info.setParams(user.token(), null, newNick, null, null);
                            mMainHttp.doRequest(info);
//                            http.updateProfile(user.token(), null, newNick, null, null);
                        }
                        Log.v("Huang", "编辑昵称后，修改昵称");
                    }
                    pd.show();
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onHttpResponse(IRequest request) {
        switch (request.getmId()){
            case HttpCommon.USER_LOGIN_IN_REQUEST:
                HttpJsonResponse response = request.getResponse();
                User user = new Gson().fromJson(response.getBody(), User.class);
                loginSuccess(user);
                break;
            case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
                updateProfileSuccess();
        }
    }

    // 登录成功回调
    public void loginSuccess(User user) {
        if (pd != null) {
            pd.dismiss();
        }
        if (user != null) {
            user.userType_$eq("1");
            SYUserManager.getInstance().setUser(user);
            Intent aintent = new Intent(this, CommonStringsApi.getHomeClass());

            //add by trade
            TradeBusinessApi.getInstance().mallLoginSuccess(user, null);

            this.setResult(RESULT_OK, aintent);
            EditNickNameActivity.this.finish();
//            aintent.setClass(this, MultipleActivity.class);
//            aintent.putExtra("fragmentType", MultipleActivity.IFRAGMENT);
            startActivity(aintent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        if (pd != null) {
            pd.dismiss();
        }
    }

    // 更改昵称成功回调
    public void updateProfileSuccess() {
        if (pd != null) {
            pd.dismiss();
        }
        showToast(R.string.update_nick_success);
        if (user != null) {
            user.name_$eq(newNick);
            //add by trade mall
            TradeBusinessApi.getInstance().mallEditNickNameSuccess(user);
        }
        SYUserManager.getInstance().setUser(user);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nickName.getWindowToken(), 0);
        EditNickNameActivity.this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

//    @Override
//    public void onHttpError(String methodName, AjaxStatus as) {
//        if (pd != null) {
//            pd.dismiss();
//        }
//    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        this.finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        if (!StringUtils.isEmpty(getStringExtra("userpwd"))) {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            super.onBackPressed();
        } else if (!StringUtils.isEmpty(getStringExtra("third_type"))) {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            super.onBackPressed();
        }
    }

    public void showToast(int resId) {
        SouYueToast.makeText(EditNickNameActivity.this,
                getResources().getString(resId), 0).show();
    }

    private String getStringExtra(String key) {
        Intent intent = getIntent();
        if (intent != null)
            return intent.getStringExtra(key);
        return "";
    }

    
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		temp = s;
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(nickName != null) {
			String mText = nickName.getText().toString();
//			int len = mText.length();
			if (Utility.getStrLength(mText) < TEXT_MAX_LENGTH) {
				mLimitCount.setTextColor(getResources().getColor(R.color.nick_tips_text_color));
			} else {
				mLimitCount.setTextColor(Color.RED);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(nickName != null) {
//			try {
//				selectionStart = nickName.getSelectionStart();
//				selectionEnd = nickName.getSelectionEnd();
//				if (Utility.getStrLength(temp.toString()) > TEXT_MAX_LENGTH) {
//					s.delete(selectionStart - 1, selectionEnd);
//					int tempSelection = selectionEnd;
//					nickName.setTextKeepState(s);
//					if (tempSelection > 0)
//						nickName.setSelection(tempSelection);// 设置光标在最后
//				}
//
//			} catch (Exception ex) {
//
//			} finally {
//			}
            mLimitCount.setText(String.format(getString(R.string.edit_nick_limit), Utility.getStrLength(temp.toString())));
        }
	}

}
