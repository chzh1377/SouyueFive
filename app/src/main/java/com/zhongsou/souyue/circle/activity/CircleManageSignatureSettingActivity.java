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
 * 圈吧管理-编辑签名
 * @author chz
 *
 */
public class CircleManageSignatureSettingActivity extends BaseActivity implements OnClickListener {
//	private static final int CIRCLESETUSERINFO_REQUESTID = 6514;
	private EditText et_cricle_manage_edit_signature;
	private Button btn_cricle_manage_signature_submit;
	private Button btn_cricle_manage_signatrue_clear;
	private TextView tv_cricle_manage_words_count;
	private ProgressDialog pd;
	
	//参数
	private String signature ;
	private String newSignature;
	private long interest_id;
	private int oper_type;
	private String token;
	
//	private Http http;
//    private AQuery aq;
    
    private static final long MAX_COUNT = 20l;  
    public static final int RESULT_CODE_EDIT_SIGNATRUE_SUCC = 1;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cricle_manage_edit_signature);
		
		Intent extrIntent = getIntent();
		signature = extrIntent.getStringExtra("signature");
		interest_id = extrIntent.getLongExtra("interest_id", 0l);
		oper_type = extrIntent.getIntExtra("oper_type", 0);
		token = extrIntent.getStringExtra("token");
		
		initView();
		
	}
	
	private void initView() {
		
		pd = new ProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		
		et_cricle_manage_edit_signature = (EditText) findViewById(R.id.et_cricle_manage_edit_signature);
		btn_cricle_manage_signature_submit = (Button) findViewById(R.id.btn_cricle_manage_signature_submit);
		btn_cricle_manage_signatrue_clear = (Button) findViewById(R.id.btn_cricle_manage_signatrue_clear);
		tv_cricle_manage_words_count = (TextView) findViewById(R.id.tv_cricle_manage_words_count);
		
		btn_cricle_manage_signature_submit.setOnClickListener(this);
		btn_cricle_manage_signatrue_clear.setOnClickListener(this);
		
		et_cricle_manage_edit_signature.addTextChangedListener(mTextWatcher);
		
		if (!StringUtils.isEmpty(signature)) {
			et_cricle_manage_edit_signature.setText(signature);
			et_cricle_manage_edit_signature.setSelection(signature.length()); // 将光标移动最后一个字符后面
		}
		
		tv_cricle_manage_words_count.setText(String.format(getString(R.string.signatrue_limit),et_cricle_manage_edit_signature.length() <=0 ? 0 : Utility.getStrLength(et_cricle_manage_edit_signature.getText().toString())));

        ((TextView)findViewById(R.id.activity_bar_title)).setText(getString(R.string.cricle_manage_edit_signature));
       
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cricle_manage_signatrue_clear:
			if(et_cricle_manage_edit_signature != null &&  tv_cricle_manage_words_count != null) {
				et_cricle_manage_edit_signature.setText("");
				tv_cricle_manage_words_count.setText(R.string.edit_nick_empty);
			}
			break;
		case R.id.btn_cricle_manage_signature_submit:
			
//			http = new Http(CircleManageSignatureSettingActivity.this);
			
			if(!CMainHttp.getInstance().isNetworkAvailable(this)){
				showToast(R.string.cricle_manage_networkerror);
				return;
			}
			
			newSignature = et_cricle_manage_edit_signature.getText().toString().trim();
			pd.setMessage(this.getResources().getString(
					R.string.cricle_manage_update_signaturing));
			pd.show();
//			CircleSetUserInfoRequest circleSetUserInfo = new CircleSetUserInfoRequest(CIRCLESETUSERINFO_REQUESTID, this);
//			circleSetUserInfo.setParams(interest_id,oper_type,newSignature,token);
//			CMainHttp.getInstance().doRequest(circleSetUserInfo);
			CircleSetUserInfoRequest.send(HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_SIGN,this,interest_id,oper_type,newSignature,token);
//			http.updateCricleManageSignature(interest_id,oper_type,newSignature,token);
			break;
			default:
				break;
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		if (pd != null) {
			pd.dismiss();
		}
		switch (request.getmId()){
			case HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_SIGN:
				showToast(R.string.cricle_manage_update_signature_failed);
				break;
		}
	}



	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.CIRCLE_SETUSERINFO_REQUESTID_SIGN:
				updateCricleManageSignatureSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	public void updateCricleManageSignatureSuccess(HttpJsonResponse res){
		if (pd != null) {
			pd.dismiss();
		}
//		boolean isSucc = CircleManageActivity.isRequestSuccess(res, status);
//		  if(isSucc) {
			showToast(R.string.cricle_manage_update_signature_success);
			Intent data = new Intent();
			data.putExtra("NEW_SIGNATURE", newSignature);
			setResult(RESULT_CODE_EDIT_SIGNATRUE_SUCC, data);
			CircleManageSignatureSettingActivity.this.finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//		  }else{
//			  showToast(R.string.cricle_manage_update_signature_failed);
//		  }
	}
	
	public void showToast(int resId) {
		SouYueToast.makeText(CircleManageSignatureSettingActivity.this, getResources().getString(resId), 0).show();
	}
	
//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		if (pd != null) {
//			pd.dismiss();
//		}
//	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(et_cricle_manage_edit_signature != null) {
				String mText = et_cricle_manage_edit_signature.getText().toString();
//				int len = mText.length();
				if (Utility.getStrLength(mText) < MAX_COUNT) {
					tv_cricle_manage_words_count.setTextColor(getResources().getColor(R.color.nick_tips_text_color));
				} else {
					tv_cricle_manage_words_count.setTextColor(Color.RED);
				}
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			
		if(et_cricle_manage_edit_signature != null) {
			try {
				editStart = et_cricle_manage_edit_signature.getSelectionStart();
				editEnd  = et_cricle_manage_edit_signature.getSelectionEnd();
				if (Utility.getStrLength(temp.toString()) > MAX_COUNT) {
					s.delete(editStart - 1, editEnd);
					int tempSelection = editEnd;
					et_cricle_manage_edit_signature.setTextKeepState(s);
					if (tempSelection > 0)
						et_cricle_manage_edit_signature.setSelection(tempSelection);// 设置光标在最后
				}
	
			} catch (Exception ex) {
	
			} finally {
				tv_cricle_manage_words_count.setText(String.format(getString(R.string.signatrue_limit), Utility.getStrLength(temp.toString())));
			}
		}
		}
	};
	
	
	 /** 
     * 刷新剩余输入字数,最大值40个字符
     */  
    private void setLeftCount() {  
    	tv_cricle_manage_words_count.setText(getInputCount() + "/" + MAX_COUNT );  
    }  
    
    /** 
     * 获取用户输入的分享内容字数 
     *  
     * @return 
     */  
    private long getInputCount() {
    	
    	String wordStr = et_cricle_manage_edit_signature.getText().toString();
//    	return calculateLength(wordStr);
    	long wordCount = calculateLength(wordStr);
    	if(wordCount >= MAX_COUNT) {
    		tv_cricle_manage_words_count.setTextColor(getResources().getColor(R.color.cricle_manage_signature_count_text_color));
    		return wordCount;
    	}else {
    		tv_cricle_manage_words_count.setTextColor(getResources().getColor(R.color.cricle_manage_signature_counts_text_color));
    		return wordCount;
    	}
          
    } 
    
    /** 
     * 计算签名内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 
     *  
     * @param c 
     * @return 
     */  
    private long calculateLength(CharSequence c) {  
        double len = 0;  
        for (int i = 0; i < c.length(); i++) {  
            int tmp = (int) c.charAt(i);  
            if (tmp > 0 && tmp < 127) {  
                len += 0.5;  
            } else {  
                len++;  
            }  
        }  
        return Math.round(len);  
    }  
  
    public void onBackPressClick(View view) {
		this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

	@Override
	public void onHttpStart(IRequest request) {

	}
}
