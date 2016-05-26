package com.zhongsou.souyue.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utility;

/**
 * 个性签名
 * 
 * @author zhangliang01@zhongsou.com
 * 
 */
public class SignatureActivity extends RightSwipeActivity implements OnClickListener {
	private static final int EDIT_TEXT_MAX_LENGTH = 20;
	public static final String INTENT_HISTORY_SIGNATURE = "intent.history.signature";
	private TextView btn_submit;
	private TextView limit;
	private EditText et_signatrue;
	private Button btn_clear;
//	private Http http;
	private String historySignature;
	private String currentSignature;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signatrue);
//		http = new Http(this);
		user = SYUserManager.getInstance().getUser();
		initTitleBar();
		initView();
		initFromIntent(getIntent());
		editTextAddListener();
	}
	
	private void initFromIntent(Intent intent){
		historySignature = user.signature();
		doLimit();
		if (!TextUtils.isEmpty(historySignature) && et_signatrue != null){
			et_signatrue.setText(historySignature);
			et_signatrue.setSelection(historySignature.length());
		}
		limit.setText(String.format(getString(R.string.signatrue_limit), historySignature == null || historySignature.length() <=0 ? 0 :getStringLength(historySignature)));
	}

	/**
	 * 限制20 长度
	 */
	private void doLimit()
	{
		if(StringUtils.isNotEmpty(historySignature)&& getStringLength(historySignature)>EDIT_TEXT_MAX_LENGTH)
		{
			historySignature=historySignature.substring(0,EDIT_TEXT_MAX_LENGTH);
		}
	}
	private void initTitleBar() {
		((TextView) findViewById(R.id.activity_bar_title)).setText(R.string.signatrue_title);
		btn_submit = findView(R.id.btn_edit_signature_submit);
		btn_submit.setText(R.string.signatrue_btn_save);
		btn_submit.setOnClickListener(this);
	}
	
	private void initView(){
		et_signatrue = findView(R.id.et_signatrue);
		btn_clear = findView(R.id.btn_signature_clear);
		limit = findView(R.id.tv_text_limit);
		btn_clear.setOnClickListener(this);
		et_signatrue.setOnClickListener(this);
	}

	private void editTextAddListener() {
		et_signatrue.addTextChangedListener(new TextWatcher() {
			private CharSequence temp = "";
//			private int selectionStart;
//			private int selectionEnd;

			public void afterTextChanged(Editable s) {
//				try {
//					selectionStart = et_signatrue.getSelectionStart();
//					selectionEnd = et_signatrue.getSelectionEnd();
//					if (Utility.getStrLength(temp.toString()) > EDIT_TEXT_MAX_LENGTH) {
//						s.delete(selectionStart - 1, selectionEnd);
//						int tempSelection = selectionEnd;
////						et_signatrue.setText(s);
//						et_signatrue.setTextKeepState(s);
//						if (tempSelection > 0)
//							et_signatrue.setSelection(tempSelection);// 设置光标在最后
//					}
//
//				} catch (Exception ex) {
//
//				} finally {
				limit.setText(String.format(getString(R.string.signatrue_limit),getStringLength(temp.toString())));
//				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				temp = s;
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String mText = et_signatrue.getText().toString();
//				int len = mText.length();
				if (getStringLength(mText) < EDIT_TEXT_MAX_LENGTH) {
					limit.setTextColor(getResources().getColor(R.color.nick_tips_text_color));
				} else {
					limit.setTextColor(Color.RED);
				}
			}
		});
	}

	/**
	 *  是否需要替换中文为长度2的字节，英文为长度 1 的
	 *
	 * @param str
	 * @return
     */
	private static int  getStringLength(String str)
	{
		//替换中文为两个字节
//		str = str.replaceAll("[^\\x00-\\xff]", "**");
		int length = 0;
		if(StringUtils.isNotEmpty(str))
		{
			length=str.length();
		}
		return length;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_edit_signature_submit:
			doSave();
			break;
		case R.id.btn_signature_clear:
			if (et_signatrue != null && limit != null) {
				et_signatrue.setText("");
				limit.setText(R.string.edit_nick_empty);
			}
			break;
		default:
			break;
		}
	}
	
	private void doSave(){
		currentSignature = et_signatrue.getText().toString();
		doLimit();
		if (user != null && user.token() != null){
			UserRepairInfo info = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
			info.setParams(user.token(), null, null, null, currentSignature);
			mMainHttp.doRequest(info);
		}
//			http.updateProfile(user.token(), null, null, null, currentSignature);
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
				updateProfileSuccess();
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		IHttpError error = request.getVolleyError();
//		Toast.makeText(this,R.string.error_network,Toast.LENGTH_LONG).show();
		switch (request.getmId()){
			case HttpCommon.USER_REPIRE_USER_INFO_REQUEST:
				if (error.getErrorType() == IHttpError.TYPE_SERVER_ERROR){
					HttpJsonResponse respo = error.getJson();
					Toast.makeText(this, respo.getBodyString(), Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(this, R.string.tg_dialog_noconn, Toast.LENGTH_LONG).show();
				}
		}
	}

	//--------------------callback----------------------
	public void updateProfileSuccess() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_signatrue.getWindowToken(), 0);
		user.signature_$eq(currentSignature);
		SYUserManager.getInstance().setUser(user);
		this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

}
