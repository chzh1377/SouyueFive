package com.zhongsou.souyue.circle.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.adapter.FirstLeadAdapter;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.view.HideView;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.module.GuideRecommendSRP;
import com.zhongsou.souyue.module.GuideRecommendSRPList;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.common.GuestToken;
import com.zhongsou.souyue.net.sub.GuideRecSpecialListRequest;
import com.zhongsou.souyue.net.sub.GuideRecSrpListRequest;
import com.zhongsou.souyue.net.sub.GuideSubSrpRequest;
import com.zhongsou.souyue.net.volley.CGuideHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.lib.DiscrollvableLayout;
import com.zhongsou.souyue.ui.lib.DiscrollvablePathLayout;
import com.zhongsou.souyue.utils.NetWorkUtils;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class FirstLeaderActivity extends BaseActivity implements View.OnClickListener,OnItemClickListener{

    public final static String ABOUT = "about";
    private static final int HTTP_GET_GETTOKEN = 110;
    private static final int HTTP_GET_SUBSCRIBE_SRP = 112;
    public static final int HTTP_GET_GET_GUIDE_RECOMMEND_SRP = 113;
    public static final int HTTP_GET_GET_GUIDE_RECOMMEND_SPECIAL = 114;
    
    public static final int SUBSCRIBED = 1;	//已订阅
    public static final int NO_SUBSCRIBE = 0;//未订阅
    private ImageView mGuideSplashArrow;
    private ObjectAnimator jumpAnimation;
    private DiscrollvablePathLayout mPathLayout;
//	private CGuideHttp http;
	private ImageLoader imageLoader;
	private TextView mGuideSkipe;
	private Button mGuideEnterHome;
	private List<DiscrollvableLayout> mLayouts ;
	private List<GuideRecommendSRP> srpList;
	private LinearLayout mGuideLoadingFaieldLayout;
	private ProgressBar mGuideLoadingProgress;
	private TextView mGuideRetry;
	private TextView mGuideLoadingSkipe;
	private RelativeLayout mGuideLoadingLayout;
	private String mAppList;
	private FirstLeadAdapter mFirstLeadAdapter;
	private GridView mRecommendList;
	private List<GuideRecommendSRP> mSelectedList = new ArrayList<GuideRecommendSRP>();
	private boolean mSpecialRequestFlag = false;//专题推荐请求成功失败标示
	private boolean mSRPRequestFlag = false;//srp列表请求失败标示
	private GuestToken guest;
	private static DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheOnDisk(true).resetViewBeforeLoading(true).cacheInMemory(true)
			.showImageOnLoading(R.drawable.default_big)
			.showImageOnFail(R.drawable.default_big)
			.showImageForEmptyUri(R.drawable.default_big)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(10))
			.build();
	
	private CMainHttp mainHttp;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_main);
        //System.out.println(DeviceInfo.getScreenSize());
        //告诉HideView可以绘制提示
        SYSharedPreferences.getInstance().putBoolean(HideView.NEED_SHOW, true);
        initView();
		initData();
    }

	

	private void initView() {
		mGuideLoadingLayout = (RelativeLayout) findViewById(R.id.rl_guide_loading_layout);
		mGuideLoadingSkipe = (TextView) findViewById(R.id.tv_guide_loading_skipe);
		mGuideRetry = (TextView) findViewById(R.id.tv_guide_retry);
		mGuideLoadingProgress = (ProgressBar) findViewById(R.id.pb_guide_loading);
		mGuideLoadingFaieldLayout = (LinearLayout) findViewById(R.id.rl_guide_loading_faield_layout);
		mGuideSplashArrow = (ImageView) findViewById(R.id.iv_guide_splash_arrow);
		mPathLayout = (DiscrollvablePathLayout) findViewById(R.id.layout_path) ;
		mGuideSkipe = (TextView) findViewById(R.id.tv_guide_skipe);
		mGuideEnterHome = (Button) findViewById(R.id.btn_guide_enter);
		mRecommendList = (GridView) findViewById(R.id.gv_guide_recommend_list);
		
		mGuideSkipe.setOnClickListener(this);
		mGuideEnterHome.setOnClickListener(this);
		mGuideRetry.setOnClickListener(this);
		mGuideLoadingSkipe.setOnClickListener(this);
		mGuideLoadingLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	private void initData() {
		mAppList = DeviceInfo.getAppList(FirstLeaderActivity.this);
//		http = new CGuideHttp(this);
		mainHttp = CMainHttp.getInstance();
		mFirstLeadAdapter = new FirstLeadAdapter(this);
		mRecommendList.setAdapter(mFirstLeadAdapter);
		mRecommendList.setOnItemClickListener(this);
		imageLoader = ImageLoader.getInstance();
		launchRequest();
	}

	/**
	 * 发起请求引导页数据
	 */
	private void launchRequest() {
		User user = SYUserManager.getInstance().getUser();
		if (user == null) {
			getToken();
		} else {
			if (user.userId() == 0) {
				SYUserManager.getInstance().delUser(user);
				user = null;
				getToken();
			} else {
				reqGuideRecommandSPR();//请求推荐列表
			}
		}
	}
	
	private void getToken() {
		if(NetWorkUtils.isNetworkAvailable()) {
			guest=new GuestToken(HttpCommon.SELFCREATELIST_REQUEST_ID,FirstLeaderActivity.this);
			guest.setParams(FirstLeaderActivity.this);
			mMainHttp.doRequest(guest);
		}else {
			ToastUtil.show(this, R.string.neterror);
			showFailure();
		}
	}
	/**
	 * 请求数据
	 */
	private void reqGuideRecommandSPR() {
		if(NetWorkUtils.isNetworkAvailable()) {
			showProgressBar();
			if(!mSRPRequestFlag) {
				GuideRecSrpListRequest srpListRequest = new GuideRecSrpListRequest(HttpCommon.SUB_GUIDE_RECOMMEND_SRP_LIST_REQUEST, this);
				srpListRequest.setParams();
				mainHttp.doRequest(srpListRequest);
//				http.getGuideRecommendSRP(HTTP_GET_GET_GUIDE_RECOMMEND_SRP, SYUserManager.getInstance().getToken(), this);
			}
			if(!mSpecialRequestFlag) {
				GuideRecSpecialListRequest specListReq = new GuideRecSpecialListRequest(HttpCommon.SUB_GUIDE_RECOMMEND_SPECIAL_LIST_REQUEST, this);
				specListReq.setParams(SYUserManager.getInstance().getToken(),mAppList);
				mainHttp.doRequest(specListReq);
//				http.getGuideRecommendSpecial(HTTP_GET_GET_GUIDE_RECOMMEND_SPECIAL, SYUserManager.getInstance().getToken(),mAppList, this);
			}
		}else {
			ToastUtil.show(this, R.string.neterror);
			showFailure();
		}
	}
	
	/**
	 * 添加指示箭头
	 * @param target
	 */
	private void addSplashArrow(View target) {
		if(target != null) {
			jumpAnimation = ObjectAnimator.ofFloat(target, "translationY", 0f, -45f,-45f,0);
	        jumpAnimation.setRepeatCount(ValueAnimator.INFINITE);
	        jumpAnimation.setRepeatMode(ValueAnimator.RESTART);
	        jumpAnimation.setDuration(2000);
	        jumpAnimation.start();
		}
	}
	
	@Override
	public void onHttpResponse(IRequest _request) {
		
		HttpJsonResponse res = (HttpJsonResponse) _request.getResponse();
		
		int id = _request.getmId();
		switch (id) {
		case HttpCommon.SELFCREATELIST_REQUEST_ID:
			tokenSuccess(res);
			break;
		case HttpCommon.SUB_GUIDE_RECOMMEND_SRP_REQUEST:
			subscribeSrpSuccess(res);
			break;
		case HttpCommon.SUB_GUIDE_RECOMMEND_SRP_LIST_REQUEST:
			getGuideRecommendSRPSuccess(res);
			break;
		case HttpCommon.SUB_GUIDE_RECOMMEND_SPECIAL_LIST_REQUEST:
			getGuideRecommendSpecialSuccess(res);
			break;
		}
	}
	
	/**
	 * 获取推荐SRP列表
	 * @param res
	 */
	private void getGuideRecommendSRPSuccess(HttpJsonResponse res) {
		mSRPRequestFlag = true;
		GuideRecommendSRPList list = new GuideRecommendSRPList(res);
		if(null != list) {
			List<GuideRecommendSRP> srpList = list.list();
			
			//将默认选中订阅的加入临时列表
			for (GuideRecommendSRP guideRecommendSRP : srpList) {
				int status = guideRecommendSRP.getStatus();
				if(status == FirstLeaderActivity.SUBSCRIBED) {
					if(!mSelectedList.contains(guideRecommendSRP)) {
						mSelectedList.add(guideRecommendSRP);
					}
				}
			}
			mFirstLeadAdapter.addDatas(srpList);
		}
		
		if(mSpecialRequestFlag && mSRPRequestFlag) {
			hideProgressBar();
		}
	}
	/**
	 * 获取推荐专题列表
	 * @param res
	 */
	public void getGuideRecommendSpecialSuccess(HttpJsonResponse res) {
		mSpecialRequestFlag = true;
		GuideRecommendSRPList list = new GuideRecommendSRPList(res);
		mLayouts = mPathLayout.getLayouts();
		
		if (list != null) {
			srpList = list.list();
			
			for (int i = 1; i < mLayouts.size(); i++) {
				if(i > srpList.size()) {
					mLayouts.get(i).setVisibility(View.GONE);
				}
			}
			for (int i = 0; i < srpList.size(); i++) {
				if(i > mLayouts.size()){
					break;
				}
				
				GuideRecommendSRP srp = srpList.get(i);
				srp.setIndex(i);
				DiscrollvableLayout layout = mLayouts.get(i + 1);
				
				if((i == srpList.size() - 1) && srpList.size() != mLayouts.size()-1 ) {
					layout.findViewById(R.id.iv_guide_item_time_line).setVisibility(View.GONE);
				}
				
				View view = layout.getChildAt(0);
				if (view != null && view instanceof RelativeLayout) {
					
					ViewHolder holder = new ViewHolder();
					
					holder.mGuideItemDate = (TextView) view
							.findViewById(R.id.tv_guide_item_date);
					holder.mGuideItemTitle = (TextView) view
							.findViewById(R.id.tv_guide_item_title);
					holder.mGuideItemPic = (ImageView) view
							.findViewById(R.id.iv_guide_item_pic);
					
					holder.mGuideItemDate.setText(formatDate(srp.getDate()));
					holder.mGuideItemTitle.setText(srp.getTitle());
					
					imageLoader.displayImage(srp.getPic(), holder.mGuideItemPic,
							options);
					
					layout.setTag(holder);
				}
			}
		}
		//数据加载成功，隐藏进度条
		if(mSpecialRequestFlag && mSRPRequestFlag) {
			hideProgressBar();
		}
	}
	
	/**
	 * 点击订阅SRP本地临时存储
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		GuideRecommendSRP srp = (GuideRecommendSRP) parent.getItemAtPosition(position);
		if(null != srp) {
			int state = srp.getStatus();
			if(state == NO_SUBSCRIBE){
				if(!mSelectedList.contains(srp)) {
					mSelectedList.add(srp);
				}
				srp.setStatus(SUBSCRIBED);
			}else {
				if(mSelectedList.contains(srp)) {
					mSelectedList.remove(srp);
				}
				srp.setStatus(NO_SUBSCRIBE);
			}
			mFirstLeadAdapter.notifyDataSetChanged();
		}
		UmengStatisticUtil.onEvent(this, UmengStatisticEvent.SUBSCRIBE_ADD_GUIDE_CLICK);
	}
	
	@Override
	public void onClick(View v) {
		
		int action = v.getId();
		
		switch (action) {
		case R.id.tv_guide_loading_skipe:
		case R.id.tv_guide_skipe:
		case R.id.btn_guide_enter:
			enterHome();
			break;
		case R.id.tv_guide_retry:
			launchRequest();
			break;
		}
	}
	
	/**
	 * 进入首页
	 */
	private void enterHome() {
		mGuideSkipe.setEnabled(false);
		mGuideEnterHome.setEnabled(false);
		if(null != mSelectedList && mSelectedList.size() > 0){
			GuideSubSrpRequest subSrpRequest = new GuideSubSrpRequest(HttpCommon.SUB_GUIDE_RECOMMEND_SRP_REQUEST, this);
			subSrpRequest.setParams(SYUserManager.getInstance().getToken(), mSelectedList);
			mainHttp.doRequest(subSrpRequest);
//			http.subscribeSrp(HTTP_GET_SUBSCRIBE_SRP, SYUserManager
//					.getInstance().getToken(), mSelectedList, this);
		}else {
			gotoMain();//数据没有改变有网或无网都走这
		}
	}

	private void gotoMain() {
		Intent intent = new Intent();
		intent.setClass(FirstLeaderActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	public void subscribeSrpSuccess(HttpJsonResponse res) {
		gotoMain();
	}
	 
    public void tokenSuccess(HttpJsonResponse res) {
    	User u = new Gson().fromJson(res.getBody(), User.class);
        u.userType_$eq(SYUserManager.USER_GUEST);
        SYUserManager.getInstance().setUser(u);
        //token获取成功后，把应用程序列表传给服务器
        if (StringUtils.isNotEmpty(res.getHead().get("cpmRecommend"))&&StringUtils.isNotEmpty(res.getHead().get("cpmRecommend").getAsString())) {
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_GUEST_SPECIAL, u.userId() + "," + res.getHead().get("cpmRecommend").getAsString());
        }
        reqGuideRecommandSPR();
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(jumpAnimation != null) {
			jumpAnimation.cancel();
		}
	}

	private void showFailure() {
		mGuideLoadingProgress.setVisibility(View.GONE);
		mGuideLoadingFaieldLayout.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressBar(){
		addSplashArrow(mGuideSplashArrow);
		mGuideLoadingLayout.setVisibility(View.GONE);
	}
	
	private void showProgressBar() {
		mGuideLoadingLayout.setVisibility(View.VISIBLE);
		mGuideLoadingFaieldLayout.setVisibility(View.GONE);
		mGuideLoadingProgress.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onHttpError(IRequest _request) {
		switch (_request.getmId()) {
		case HttpCommon.SUB_GUIDE_RECOMMEND_SRP_REQUEST:
			gotoMain();
			break;
		case HttpCommon.SUB_GUIDE_RECOMMEND_SRP_LIST_REQUEST:
			mSRPRequestFlag = false;
			showFailure();
			break;
		case HttpCommon.SUB_GUIDE_RECOMMEND_SPECIAL_LIST_REQUEST:
			mSpecialRequestFlag = false;
			showFailure();
			break;
		default:
			showFailure();
			break;
		}
		
	}
	
	public static String formatDate(String date) {
		if(!StringUtils.isEmpty(date)){
			String [] arr = date.split("-");
			if(arr.length > 2) {
				for(int i=1;i<arr.length;i++) {
					if(arr[i].length() > 1 && arr[i].startsWith("0")) {
						arr[i] = arr[i].substring(arr[i].length() - 1);
					}
				}
				return date =  arr[1]+"月" + arr[2] + "日";
			}
		}
		return "";
	}
	
	static class ViewHolder{
		TextView mGuideItemDate;
		TextView mGuideItemTitle;
		ImageView mGuideItemPic;
		TextView mGuideItemSubscribeIcon;
	}
	
}