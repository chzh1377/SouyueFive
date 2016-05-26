package com.zhongsou.souyue.circle.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleMemberListGridAdapter;
import com.zhongsou.souyue.circle.model.CircleMemberItem;
import com.zhongsou.souyue.circle.ui.UIHelper;
import com.zhongsou.souyue.circle.ui.UIHelper.ChangeDateCallback;
import com.zhongsou.souyue.circle.util.Constant;
import com.zhongsou.souyue.circle.view.CircleMemberListPullToRefreshBase.OnLoadMoreListener;
import com.zhongsou.souyue.circle.view.CircleMemberListPullToRefreshGridView;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetMeberRoleRequest;
import com.zhongsou.souyue.net.circle.MemberListReq;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYInputMethodManager;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

public class CircleMemberListActivity extends BaseActivity implements OnClickListener{
//	private static final int GETMEBERROLE_REQUESTID = 6546465;
	private CircleMemberListPullToRefreshGridView mPullRefreshGridView;
	private GridView mGridView;
	private CircleMemberListGridAdapter mAdapter;
	private List<CircleMemberItem> mItems;
	private ImageButton btnBack;
	private int pno = 1;
	private int psize = 20;
	private EditText etName;
	private TextWatcher textWatcher;
	private ImageView btnSearch;
	private boolean isLoadAll;
	private ImageView investTv;
	private boolean isSearch;
	private TextView tvNoResult;
	private long interest_id;
    private String new_srpId;
	private SYInputMethodManager syInputMng;
	private String instrest_img;
	private String instest_name;
	private int type;     //圈子的类型   0--普通圈，1--私密圈
	private TextView tvAdmin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_member_list_pull_to_refresh_grid);
		type = getIntent().getIntExtra("type", -1);
		instrest_img = getIntent().getStringExtra("interest_logo");
        if(null == instrest_img){
        	instrest_img = "http://souyue-image.b0.upaiyun.com/user/0001/91733511.jpg";
        }
        instest_name = getIntent().getStringExtra("interest_name");
        if(null == instest_name){
        	instest_name = "圈子名称";
        }
		getView();
		textWatcher = new TextWatcher() {
			String before = null;
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				 isLoadAll = false;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				 before = s.toString();
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String after = s.toString();
                if (before != null && before.equals(after)) {// 没有改变
                    return;
                }
				if(s.toString().length() == 0 || s.toString().equals("")){
					etName.setHint("圈成员昵称");
				}
				pno = 1;
				isSearch = true;
				getMemberList();
			}
				
		};
        ((LinearLayout)findViewById(R.id.layout_all)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        ((RelativeLayout)findViewById(R.id.relayout_all)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        ((RelativeLayout)findViewById(R.id.layout_title)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
		etName.addTextChangedListener(textWatcher); 
		mPullRefreshGridView = (CircleMemberListPullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
        mPullRefreshGridView.setDisableScrollingWhileRefreshing(false);
		mGridView = mPullRefreshGridView.getRefreshableView();
		mAdapter = new CircleMemberListGridAdapter(this,mItems,type);
        mAdapter.setNew_srpId(new_srpId);
        mAdapter.setKeyWord(instest_name);
		mAdapter.setChangerListener(new ChangeDateCallback() {
			
			@Override
			public void changeDate() {
				isLoadAll = false;
				pno = 1;
				isSearch = true;
				mItems.clear();
				getMemberList();
			}
		});
		mGridView.setAdapter(mAdapter);
		mPullRefreshGridView.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				isSearch = false;
				getMemberList();
			}
		});
		isLoadAll = false;
		pno = 1;
		isSearch = true;
		getMemberList();
		getIsAdmin();
	}

	private void getIsAdmin() {
		if (IntentUtil.isLogin()) {
//			http.getMemberRole(SYUserManager.getInstance().getToken(),interest_id);
//			CircleGetMeberRoleRequest circleGetMeberRole = new CircleGetMeberRoleRequest(GETMEBERROLE_REQUESTID, this);
//			circleGetMeberRole.setParams();
//			CMainHttp.getInstance().doRequest(circleGetMeberRole);
			CircleGetMeberRoleRequest.send(HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID,this,SYUserManager.getInstance().getToken(), interest_id);
		}
	}

	@Override
	public void onHttpResponse(IRequest _request) {
		super.onHttpResponse(_request);
		switch (_request.getmId()) {
			case HttpCommon.CIRCLE_GETMEBERROLE_REQUESTID:
				getMemberRoleSuccess(_request.<HttpJsonResponse>getResponse());
				break;
			case HttpCommon.CIRLCE_MEMBERLIST_ID:
				getMemberListSuccess(_request.<HttpJsonResponse>getResponse());
				break;
		}
	}

	@Override
	public void onHttpError(IRequest _request) {
		switch (_request.getmId()) {
			case HttpCommon.CIRLCE_MEMBERLIST_ID:
				isLoadAll = false;
				UIHelper.ToastMessage(CircleMemberListActivity.this, "网络错误");
				mPullRefreshGridView.onLoadMoreComplete();
				break;
		}
	}


	public void getMemberRoleSuccess(HttpJsonResponse res) {
        int role = res.getBody().get("role").getAsInt();
        mAdapter.setRole(role);
        if (role == Constant.ROLE_ADMIN) {
            tvAdmin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
    }

    private void getView() {
		btnBack = (ImageButton) findViewById(R.id.goBack);
		btnSearch = (ImageView) findViewById(R.id.btnSearch);
		investTv = (ImageView) findViewById(R.id.invite_btn);
		tvNoResult = (TextView) findViewById(R.id.circle_noresult_tv);
		tvNoResult.setVisibility(View.GONE);
		tvAdmin = (TextView)findView(R.id.circle_admin_tv);
		tvAdmin.setVisibility(View.GONE);
		investTv.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		etName = (EditText) findViewById(R.id.circle_member_list_et);
		syInputMng = new SYInputMethodManager(this);
		mItems = new ArrayList<CircleMemberItem>();
		interest_id = getIntent().getLongExtra("interest_id", 1001l);
        new_srpId = getIntent().getStringExtra("new_srpId");
		isSearch = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	private void getMemberList() {
        if(isLoadAll && !isSearch) {
            mPullRefreshGridView.onLoadMoreComplete();
            UIHelper.ToastMessage(this, "已全部加载");
            return;
        }
//        http.getMemberList(interest_id,etName.getText().toString(), pno, psize);
		MemberListReq req = new MemberListReq(HttpCommon.CIRLCE_MEMBERLIST_ID,this);
		req.setParams(interest_id,etName.getText().toString(), pno, psize);
		CMainHttp.getInstance().doRequest(req);
    }



	public void getMemberListSuccess(HttpJsonResponse res){
        mPullRefreshGridView.onLoadMoreComplete();
		isLoadAll = false;
		 mGridView.setVisibility(View.VISIBLE);
		 int statusCode = res.getCode();
	      if(statusCode != 200){
	          return;
	      }
	      if(isSearch){
	    	  mItems.clear();
	    	  mAdapter.notifyDataSetChanged();
	    	  mGridView.removeAllViewsInLayout();
	      }
	      tvNoResult.setVisibility(View.GONE);
	      List<CircleMemberItem> mItemsTmp = new ArrayList<CircleMemberItem>();
	      mItemsTmp = new Gson().fromJson(res.getBodyArray(), new TypeToken<List<CircleMemberItem>>() {}.getType());
	      if((mItemsTmp == null ||mItemsTmp.size() == 0) && isSearch){
	    	  tvNoResult.setVisibility(View.VISIBLE);
	    	  tvNoResult.setText("无结果");
	    	  isLoadAll = false;
	    	  mGridView.setVisibility(View.GONE);
	    	  return;
	      }
	      if (mItemsTmp == null ||mItemsTmp.size()< 16) {
	            isLoadAll = true;
	        }
        if(!isLoadAll){
            pno +=1;
        }
	      mItems.addAll(mItemsTmp);
	      mAdapter.notifyDataSetChanged();
		}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.goBack:
			finish();
			break;
		case R.id.btnSearch:
//			isLoadAll= false;
//			pno = 1;
//			isSearch = true;
//			getMemberList();
//			syInputMng.hideSoftInput();
            etName.setText("");
			break;
		case R.id.invite_btn:    //邀请好友
			UIHelper.circleInviteFriend(this,interest_id,false,instrest_img,instest_name,null,type,false,"","");
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == UIHelper.RESULT_CODE_IMFRIEND){
            pno =1;
			getMemberList();
		}
	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus status) {
//		if(methodName.equals("getMemberList")){
//			isLoadAll = false;
//			UIHelper.ToastMessage(CircleMemberListActivity.this, "网络错误");
//            mPullRefreshGridView.onLoadMoreComplete();
//		}
//	}

    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(etName.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
