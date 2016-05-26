package com.zhongsou.souyue.circle.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.adapter.CircleCheckRecordAdapter;
import com.zhongsou.souyue.circle.model.CircleCheckRecord;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleGetMemberApplyListRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

/** 
 * @author : zoulu
 * 2014年7月17日
 * 上午11:45:30 
 * 类说明 :查看圈子申请记录 
 */
@SuppressLint("InflateParams")
public class CircleCheckRecordActivity extends BaseActivity implements OnItemClickListener{
//	private static final int CIRCLEGETMEBERAPPLYLIST_REQUESTID = 56465; // 获取成员列表
	private ListView listView ;
	private ImageButton btn_cricle_edit;
	private ImageButton btn_cricle_option;
	private TextView activity_bar_title;
	private int pno = 1;
	private int psize = 10;
	private List<CircleCheckRecord> checkRecords = new ArrayList<CircleCheckRecord>();
	private CircleCheckRecordAdapter adapter;
	private ProgressBarHelper pbHelper;
	private View footerView;
	private boolean hasMore;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.circle_check_record);
		initUI();
		initData();
	}
	
	private void initUI(){
//		http = new Http(this);
		footerView = getLayoutInflater().inflate(R.layout.ent_refresh_footer,null);
		listView = (ListView) findViewById(R.id.check_list);
		listView.addFooterView(footerView);
		footerView.setVisibility(View.GONE);
		listView.setOnScrollListener(new ScrollListener());
		listView.setOnItemClickListener(this);
		btn_cricle_edit = (ImageButton) findViewById(R.id.btn_cricle_edit);
		btn_cricle_option = (ImageButton) findViewById(R.id.btn_cricle_option);
		btn_cricle_edit.setVisibility(View.INVISIBLE);
		btn_cricle_option.setVisibility(View.GONE);
		activity_bar_title = (TextView) findViewById(R.id.activity_bar_title);
		activity_bar_title.setText("我的申请记录");
		pbHelper = new ProgressBarHelper(this, findViewById(R.id.ll_data_loading));
	}
	
	private void initData(){
		adapter = new CircleCheckRecordAdapter(this);
		listView.setAdapter(adapter);
		CircleGetMemberApplyListRequest.send(HttpCommon.CIRCLE_GETMEMBERAPPLYLIST_REQUESTID,this,SYUserManager.getInstance().getToken(), pno, psize);
//		http.getMemberApplyList(SYUserManager.getInstance().getToken(), pno, psize);
	}
	
	public void getMemberApplyListSuccess(HttpJsonResponse response){
		pbHelper.goneLoading();
		checkRecords = new Gson().fromJson(response.getBodyArray(), new TypeToken<ArrayList<CircleCheckRecord>>() {
		}.getType());
		hasMore = response.getHeadBoolean("hasMore");
		adapter.addMoreList(checkRecords);
        if(checkRecords.size() < psize){
            listView.removeFooterView(footerView);
        }
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.CIRCLE_GETMEMBERAPPLYLIST_REQUESTID:
				getMemberApplyListSuccess(request.<HttpJsonResponse>getResponse());
				break;
		}

	}

	@Override
	public void onHttpError(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.CIRCLE_GETMEMBERAPPLYLIST_REQUESTID:
				pbHelper.showNetError();
				break;
		}
	}

	private void loadMore(){
        if(hasMore){
			CircleGetMemberApplyListRequest.send(HttpCommon.CIRCLE_GETMEMBERAPPLYLIST_REQUESTID,this,SYUserManager.getInstance().getToken(), ++pno, psize);
//			http.getMemberApplyList(SYUserManager.getInstance().getToken(), ++pno, psize);
		}else{
			if(footerView != null)
				listView.removeFooterView(footerView);
		}
	}
	
	private class ScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				// 判断滚动到底部
				if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
					footerView.setVisibility(View.VISIBLE);
					loadMore();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (adapter.getItem(arg2).getAudit_status()) {
		case 1:// 1:待审核
			IntentUtil.gotoSecretCricleCard(this, adapter.getItem(arg2).getInterest_id());
			break;
		case 2://2：已拒绝
			IntentUtil.gotoSecretCricleCard(this, adapter.getItem(arg2).getInterest_id());
			break;
		default:
			break;
		}
	}
}
