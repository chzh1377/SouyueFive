package com.zhongsou.souyue.circle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.model.InviteNoticeItem;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CirclePrivateReq;
import com.zhongsou.souyue.net.volley.CSouyueHttpError;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.Utility;
import com.zhongsou.souyue.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author liuyh 私密圈邀请好友填写备注及留言
 */
public class InviteNoticeActivity extends BaseActivity implements
		OnClickListener {
	private EditText etLiuYan;
	private Button btnConfirm;
	private Button btnCancle;
	private List<InviteNoticeItem> mItems;
//	private Http http;
	private boolean isAdmin;
	private long interest_id;
	private LinearLayout ll_layout;
//	private AQuery aQuery;
//
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.circle_invite_notice);
		initView();
	}

	private void initView() {
		mItems = new ArrayList<InviteNoticeItem>();
		mItems = (ArrayList<InviteNoticeItem>) getIntent().getSerializableExtra("InviteNoticeItem");
		isAdmin = getIntent().getBooleanExtra("isAdmin", false);
		interest_id = getIntent().getLongExtra("interest_id", -1);
		if (isAdmin) {
			((TextView) findViewById(R.id.activity_bar_title)).setText("备注昵称");
		} else {
			((TextView) findViewById(R.id.activity_bar_title)).setText("填写邀请信息");
		}

		ll_layout = (LinearLayout) findView(R.id.llll);
		btnConfirm = (Button) findViewById(R.id.btn_invite_confirm);
		btnCancle = (Button) findViewById(R.id.btn_invite_cancle);
		etLiuYan = (EditText) findViewById(R.id.et_liuyan);
		etLiuYan.setVisibility(View.GONE);
		if (!isAdmin) {
			etLiuYan.setVisibility(View.VISIBLE);
		} else {
			etLiuYan.setVisibility(View.GONE);
		}

		btnConfirm.setOnClickListener(this);
		btnCancle.setOnClickListener(this);

//		http = new Http(this);
//		aQuery = new AQuery(this);

		for (int i = 0; i < mItems.size(); i++) {
			View layout_item = getLayoutInflater().inflate(R.layout.circle_invite_notice_item, null);
			TextView name = (TextView) layout_item.findViewById(R.id.invite_notice_tv_name);
			EditText beiZhu = (EditText) layout_item.findViewById(R.id.invite_notice_et_beizhu);
			ImageView img = (ImageView) layout_item.findViewById(R.id.invite_notice_image);
			name.setText(mItems.get(i).getApplicant_nickname());
			if (!TextUtils.isEmpty(mItems.get(i).getApplicant_pic())) {
				//aQuery.id(img).image(  Utils.getImageUrl(mItems.get(i).getApplicant_pic()), true, true, 0,R.drawable.im_friendsicon);
                                  PhotoUtils.showCard(PhotoUtils.UriType.HTTP,  Utils.getImageUrl(mItems.get(i).getApplicant_pic()),img, MyDisplayImageOption.getOptions(R.drawable.im_friendsicon));
			}
			
			class MyTextWatcher implements TextWatcher {
	        	 private int pos;
	        	 public MyTextWatcher(int position) {
	        		 this.pos = position;
				}
	             @Override
	             public void onTextChanged(CharSequence s, int start,
	                     int before, int count) {
	             }

	             @Override
	             public void beforeTextChanged(CharSequence s, int start,
	                     int count, int after) {
	             }

	             @Override
	             public void afterTextChanged(Editable s) {
	                 if (s != null && !"".equals(s.toString())) {
	                     mItems.get(pos).setBeizhu(s.toString());
	                 }else{
	                 	mItems.get(pos).setBeizhu("");
	                 }
	             }
	         }
			beiZhu.addTextChangedListener(new MyTextWatcher(i));
			ll_layout.addView(layout_item);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnConfirm) {
			int lenth = etLiuYan.getText().toString().length();
			if (lenth != 0) {
				if (Utility.getStrLength(etLiuYan.getText().toString().trim()) > 140) {
					UIHelper.ToastMessage(InviteNoticeActivity.this,
							"留言不能超过140个字符(70个汉字)");
					return;
				}
			}
			sendInvite();
		}
		if (v == btnCancle) {
			InviteNoticeActivity.this.finish();
			setResult(UIHelper.RESULT_CODE_INVITE_FRIEND_CANCLE);
		}
	}

	private void sendInvite() {
		JSONArray friends = new JSONArray();
		for (int i = 0; i < mItems.size(); i++) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("user_id", mItems.get(i).getUser_id());
				if (mItems.get(i).getBeizhu().length() != 0) {
					if (Utility.getStrLength(mItems.get(i).getBeizhu()) < 4
							|| Utility.getStrLength(mItems.get(i).getBeizhu()) > 20) {
						UIHelper.ToastMessage(InviteNoticeActivity.this, mItems
								.get(i).getApplicant_nickname() + "的备注昵称长度不符");
						return;
					}
				}
				obj.put("nickname", mItems.get(i).getBeizhu());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			friends.put(obj);
		}

              CirclePrivateReq req = new CirclePrivateReq(HttpCommon.CIRCLE_INTERESTPRI_REQUEST,this);
              req.addParams(etLiuYan.getText().toString(), friends, interest_id, ZSSdkUtil.INVITE_SUBSCRIBE_GROUP);
              mMainHttp.doRequest(req);

//		http.inviteFriend(SYUserManager.getInstance().getToken(), etLiuYan
//				.getText().toString(), friends, interest_id);
	}

	public void inviteFriendSuccess(HttpJsonResponse res) {
		SouYueToast.makeText(InviteNoticeActivity.this, "发送成功!", 0).show();
		for(int i=0;i<mItems.size();i++){
			//搜悦统计   邀请好友加入私密圈成功
			UpEventAgent.onGroupJoin(InviteNoticeActivity.this, interest_id+"."+"", "");
		}
		Intent intent = new Intent();
		intent.putExtra("message", etLiuYan.getText().toString());
		intent.putExtra("user_ids", res.getBody().get("user_ids").toString());
		setResult(UIHelper.RESULT_CODE_INVITE_FRIEND_OK, intent);
		InviteNoticeActivity.this.finish();
	}

    @Override
    public void onHttpResponse(IRequest request) {
        super.onHttpResponse(request);
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.CIRCLE_INTERESTPRI_REQUEST:
                inviteFriendSuccess(response);
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()){
            case HttpCommon.CIRCLE_INTERESTPRI_REQUEST:
                IHttpError error= request.getVolleyError();
                if(error.getErrorType()== IHttpError.TYPE_SERVER_ERROR){
                    int statusCode = error.getErrorCode();
                    if (statusCode != 200) {
                        UIHelper.ToastMessage(this, "邀请失败");
                        return;
                    }
                }
                break;
        }
    }
}
