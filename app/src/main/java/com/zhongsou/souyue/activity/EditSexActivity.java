package com.zhongsou.souyue.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.personal.UserRepairInfo;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYUserManager;

public class EditSexActivity extends RightSwipeActivity implements
		OnClickListener {
	public static final String INTENT_USER = "EditSexActivity.USER";
	public static final int USER_FEMALE = 1;
	public static final int USER_MALE = 0;
	
	private ProgressDialog pd;
//	private Http http;
	private String newNick;
	
	private ImageView ivMale;
	private ImageView ivFemale;
	private int sex;
	private RelativeLayout rlMySexMaleLayout,rlMySexFemaleLayout;
	
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info_edit_sex);
		pd = new ProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		ivMale = findView(R.id.iv_my_sex_male);
		ivFemale = findView(R.id.iv_my_sex_female);
		
		rlMySexMaleLayout = findView(R.id.rl_my_sex_layout_male);
		rlMySexFemaleLayout = findView(R.id.rl_my_sex_layout_female);
		rlMySexMaleLayout.setOnClickListener(this);
		rlMySexFemaleLayout.setOnClickListener(this);
		
//		http = new Http(EditSexActivity.this);
		Intent intent = getIntent();
		if (intent != null)
			user = (User) intent.getSerializableExtra(INTENT_USER);

		setSexIcon(user);
		
        ((TextView)findViewById(R.id.activity_bar_title)).setText(getString(R.string.my_info_sex_edit));
	}

	private void setSexIcon(User user) {
		if (user!=null) {
			sex = user.getSex();
		}
		switch (sex) {
		case USER_MALE:
			ivMale.setBackgroundResource(R.drawable.my_info_sex_selected_icon);
			ivFemale.setBackgroundResource(R.drawable.my_info_sex_normal_icon);
			break;
		case USER_FEMALE:
			ivMale.setBackgroundResource(R.drawable.my_info_sex_normal_icon);
			ivFemale.setBackgroundResource(R.drawable.my_info_sex_selected_icon);
			break;
		default:
			ivMale.setBackgroundResource(R.drawable.my_info_sex_normal_icon);
			ivFemale.setBackgroundResource(R.drawable.my_info_sex_normal_icon);
			break;
		}
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_my_sex_layout_male:
			updateSex(USER_MALE);
			break;
		case R.id.rl_my_sex_layout_female:
			updateSex(USER_FEMALE);
			break;
		default:
			break;
		}
	}

	private void updateSex(int sex) {
		if(!CMainHttp.getInstance().isNetworkAvailable(this)){
			showToast(R.string.networkerror);
			return;
		}
		
		pd.setMessage(this.getResources().getString(R.string.my_info_sex_editing));
		if (user != null){
			this.sex = sex;
		}
        UserRepairInfo repaire = new UserRepairInfo(HttpCommon.USER_REPIRE_USER_INFO_REQUEST,this);
        repaire.setParams(String.valueOf(this.sex));
        mMainHttp.doRequest(repaire);
//		Map<String,String> params = new HashMap<String,String>();
//		params.put("sex",String.valueOf(this.sex));
//		http.updateUserInfo(user.token(), params);
		pd.show();
	}

    @Override
    public void onHttpResponse(IRequest request) {
        updateUserInfoSuccess();
    }

    // 更改昵称成功回调
	public void updateUserInfoSuccess() {
		if (pd != null) {
			pd.dismiss();
		}
		
		if (user != null){
			user.setSex(sex);
			SYUserManager.getInstance().setUser(user);
			setSexIcon(user);
			showToast(R.string.my_info_sex_edit_suc);
		}
		
		EditSexActivity.this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

    @Override
    public void onHttpError(IRequest request) {
        if (pd != null) {
            pd.dismiss();
        }
        this.sex = user.getSex();
        showToast(R.string.my_info_sex_edit_faield);
    }

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}

	public void showToast(int resId) {
		SouYueToast.makeText(EditSexActivity.this,
				getResources().getString(resId), 0).show();
	}
	
	private String getStringExtra(String key){
		Intent intent = getIntent();
		if (intent != null)
			return intent.getStringExtra(key);
		return "";
	}

}
