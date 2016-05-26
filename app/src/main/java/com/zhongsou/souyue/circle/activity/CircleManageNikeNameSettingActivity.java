package com.zhongsou.souyue.circle.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleSetUserInfoRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utility;
/**
 * 圈吧管理-修改昵称
 * @author Administrator
 *
 */
public class CircleManageNikeNameSettingActivity extends BaseActivity implements OnClickListener,TextWatcher{
	
	public static final int RESULT_CODE_EDIT_NIKENAME_SUCC = 2;
	private static final int CIRCLE_NICK_MAX_LENGTH = 20;
//	private static final int CIRCLESETUSERINFO_REQUESTID = 321;
	private EditText et_cricle_manage_edit_nikename;
	private Button btn_cricle_manage_nikename_clear;
	private Button btn_cricle_manage_nikename_submit;
	private ProgressDialog pd;
	
	//参数
	private String nikename ;
	private String newNickName;
	private long interest_id;
	private int oper_type;
	private String token;
	
    //编辑昵称
    private CharSequence temp = "";
	private int selectionStart;
	private int selectionEnd;
	private TextView mLimitCount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.cricle_manage_editnickname);
		
		Intent extrIntent = getIntent();
		nikename = extrIntent.getStringExtra("nickname");
		interest_id = extrIntent.getLongExtra("interest_id", 0l);
		oper_type = extrIntent.getIntExtra("oper_type", 0);
		token = extrIntent.getStringExtra("token");
		
		initView();
	}



	private void initView() {
		
		pd = new ProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		
		et_cricle_manage_edit_nikename = (EditText) findViewById(R.id.et_cricle_manage_edit_nikename);
		btn_cricle_manage_nikename_clear = (Button) findViewById(R.id.btn_cricle_manage_nikename_clear);
		btn_cricle_manage_nikename_submit = (Button) findViewById(R.id.btn_cricle_manage_nikename_submit);
		mLimitCount = findView(R.id.tv_circle_nick_limit_count);
		
		btn_cricle_manage_nikename_clear.setOnClickListener(this);
		btn_cricle_manage_nikename_submit.setOnClickListener(this);
		
		et_cricle_manage_edit_nikename.addTextChangedListener(this);
		
		if (!StringUtils.isEmpty(nikename)) {
			et_cricle_manage_edit_nikename.setText(nikename);
			et_cricle_manage_edit_nikename.setSelection(et_cricle_manage_edit_nikename.length());
		}
		mLimitCount.setText(String.format(getString(R.string.edit_nick_limit),et_cricle_manage_edit_nikename.length() <=0 ? 0 : Utility.getStrLength(et_cricle_manage_edit_nikename.getText().toString())));

        ((TextView)findViewById(R.id.activity_bar_title)).setText(getString(R.string.edit_nick));
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cricle_manage_nikename_clear:
			if(et_cricle_manage_edit_nikename != null && mLimitCount != null) {
				et_cricle_manage_edit_nikename.setText("");
				mLimitCount.setText(R.string.edit_nick_empty);
			}
			break;
		case R.id.btn_cricle_manage_nikename_submit:
			
			if(!CMainHttp.getInstance().isNetworkAvailable(this)){
				showToast( R.string.cricle_manage_networkerror);
				return;
			}
			if (Utility.validateNickName(et_cricle_manage_edit_nikename,CircleManageNikeNameSettingActivity.this)) {
				newNickName = et_cricle_manage_edit_nikename.getText().toString().trim();
				// 编辑昵称后，修改昵称
				pd.setMessage(this.getResources().getString(
						R.string.cricle_manage_update_nicknameing));
//				http.updateCricleManageNikename(interest_id,oper_type,newNickName,token);
//				CircleSetUserInfoRequest circleSetUserInfo = new CircleSetUserInfoRequest(HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_NICKNAME, this);
//				circleSetUserInfo.setParams(interest_id,oper_type,newNickName,token);
//				CMainHttp.getInstance().doRequest(circleSetUserInfo);
				CircleSetUserInfoRequest.send(HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_NICKNAME,this,interest_id,oper_type,newNickName,token);
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
			case HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_NICKNAME:
					updateCricleManageNikenameSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		if (pd != null) {
			pd.dismiss();
		}
		switch (request.getmId()){
			case HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_NICKNAME:
				showToast(R.string.cricle_manage_update_nick_failed);
				break;
		}
	}

	public void updateCricleManageNikenameSuccess(HttpJsonResponse res){
		if (pd != null) {
			pd.dismiss();
		}
//		boolean isSucc = CircleManageActivity.isRequestSuccess(res,null);
//		  if(isSucc) {
			showToast(R.string.cricle_manage_update_nick_success);
			Intent data = new Intent();
			data.putExtra("NEW_NIKENAME", newNickName);
			setResult(RESULT_CODE_EDIT_NIKENAME_SUCC, data);
			CircleManageNikeNameSettingActivity.this.finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//		  }else{
//			showToast(R.string.cricle_manage_update_nick_failed);
//		  }
	}
//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		if (pd != null) {
//			pd.dismiss();
//		}
//	}

	public void showToast(int resId) {
		SouYueToast.makeText(CircleManageNikeNameSettingActivity.this, getResources().getString(resId), 0).show();
	}
	
	/**
	 * 返回键
	 */
	public void onBackPressClick(View view) {
		this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}



	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		temp = s;
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(et_cricle_manage_edit_nikename != null) {
			String mText = et_cricle_manage_edit_nikename.getText().toString();
//			int len = mText.length();
			if (Utility.getStrLength(mText) < CIRCLE_NICK_MAX_LENGTH) {
				mLimitCount.setTextColor(getResources().getColor(R.color.nick_tips_text_color));
			} else {
				mLimitCount.setTextColor(Color.RED);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(et_cricle_manage_edit_nikename != null) {
			try {
				selectionStart = et_cricle_manage_edit_nikename.getSelectionStart();
				selectionEnd = et_cricle_manage_edit_nikename.getSelectionEnd();
				if (Utility.getStrLength(temp.toString()) > CIRCLE_NICK_MAX_LENGTH) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					et_cricle_manage_edit_nikename.setTextKeepState(s);
					if (tempSelection > 0)
						et_cricle_manage_edit_nikename.setSelection(tempSelection);// 设置光标在最后
				}
	
			} catch (Exception ex) {
	
			} finally {
				mLimitCount.setText(String.format(getString(R.string.edit_nick_limit), Utility.getStrLength(temp.toString())));
			}
		}
	}

}
