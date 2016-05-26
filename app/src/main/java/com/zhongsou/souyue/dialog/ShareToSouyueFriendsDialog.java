package com.zhongsou.souyue.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.ShareHelpActivity;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.module.SearchResultItem;
import com.zhongsou.souyue.module.SharePointInfo;
import com.zhongsou.souyue.net.personal.UserPushMsg;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.share.SharePvRequest;
import com.zhongsou.souyue.net.share.ShareResultRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ShareToSouyueFriendsDialog extends BaseActivity implements OnClickListener {
	private List<Button> btnSet = new ArrayList<Button>();
	private EditText et_share_reason;
	private TextView share_syfriend_content;
	private ImageButton share_syfriend_help;
	private String token;
	private String content;
	private SearchResultItem sri;
	private String CALLBACKSF="8";
	private String shareUrl;
//	private static final int SHAREPV_REQUESTID = 98444710;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFromIntent();
		setContentView(R.layout.share_souyue_friends_dialog);
		token = SYUserManager.getInstance().getToken();
		initView();
	}

	/**
	 * init intent
	 */
	private void initFromIntent() {
		Intent intent = getIntent();
		if (null != intent) {
			content = intent.getStringExtra("content");
			sri = (SearchResultItem) intent.getSerializableExtra("searchResultItem");
			shareUrl = intent.getStringExtra("shareUrl");
		}
	}

	/**
	 * init view
	 */
	private void initView() {
		share_syfriend_content = (TextView) findViewById(R.id.share_syfriend_content);
		share_syfriend_content.setText(null != content ? content : "");
		et_share_reason = (EditText) findViewById(R.id.et_share_reason);
		et_share_reason.addTextChangedListener(textWatcher);
		btnSet.add((Button) findViewById(R.id.btn_share_canel));
		btnSet.add((Button) findViewById(R.id.btn_share_send));
		for (Button btn : btnSet) {
			btn.setOnClickListener(this);
		}
		share_syfriend_help = (ImageButton) findViewById(R.id.share_syfriend_help);
		share_syfriend_help.setOnClickListener(this);
	}

	/**
	 * 文字输入框
	 */
	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.length() >= 50)
				Toast.makeText(ShareToSouyueFriendsDialog.this, getString(R.string.toast_push_max50), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.USER_PUSH_MSG_REQUEST:
				userPushMsgSuccess();
		}
	}

	@Override
	public void onHttpError(IRequest request) {
		IHttpError error = request.getVolleyError();
		if (error.getErrorCode() == 600){
			SouYueToast.makeText(this, error.getJson().getBodyString(), SouYueToast.LENGTH_SHORT).show();
		}
	}

	public void userPushMsgSuccess() {
		SouYueToast.makeText(this, getString(R.string.send_sucess), SouYueToast.LENGTH_SHORT).show();
		if(!StringUtils.isEmpty(shareUrl)){
         SharePointInfo info=new SharePointInfo();
         info.setUrl(shareUrl);
         info.setKeyWord(sri.keyword());
         info.setSrpId(sri.srpId());
         info.setPlatform(CALLBACKSF);
//         http.userSharePoint(info);
			ShareResultRequest.send(HttpCommon.SHARE_RESULT_REQUESTID,null,info);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share_send:
			String str = et_share_reason.getText().toString().trim();
			if (str != null && str.length() > 50) {
				Toast.makeText(this, getString(R.string.toast_push_max50), Toast.LENGTH_SHORT).show();
				return;
			}
			if (sri == null)
				return;
			UserPushMsg msg = new UserPushMsg(HttpCommon.USER_PUSH_MSG_REQUEST,this);
			msg.setParams(sri.keyword(), sri.srpId(), StringUtils.isNotEmpty(shareUrl)?shareUrl:sri.url(), StringUtils.shareTitle(sri.title(), sri.description()), sri.description(), str);
			mMainHttp.doRequest(msg);
//			http.userPushMsg(token, sri.keyword(), sri.srpId(), StringUtils.isNotEmpty(shareUrl)?shareUrl:sri.url(), StringUtils.shareTitle(sri.title(), sri.description()), sri.description(), str);
//			http.pv("souyue", sri.url());
			SharePvRequest.send("souyue",sri.url(),this);
            finish();
			break;
		case R.id.btn_share_canel:
			finish();
			break;
		case R.id.share_syfriend_help:
			startActivity(new Intent(ShareToSouyueFriendsDialog.this, ShareHelpActivity.class));
			break;
		}
	}

}
