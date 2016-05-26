package com.zhongsou.souyue.im.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.PackageAdapter;
import com.zhongsou.souyue.im.download.HistoryDao;
import com.zhongsou.souyue.im.module.ExpressionBean;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.im.IMExpressionListRequest;
import com.zhongsou.souyue.net.volley.CIMExpressionHttp;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.ui.ProgressBarHelper.ProgressBarClickListener;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase.Mode;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.zhongsou.souyue.ui.pulltorefresh.PullToRefreshListView;
import com.zhongsou.souyue.utils.SYUserManager;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 表情包下载列表
 * 
 * @author wangqiang on 15/4/17
 * 
 */
public class IMExpressionListActivity extends IMBaseActivity implements
		OnClickListener, OnItemClickListener {

	private PullToRefreshListView mExpressionListView;
	private PackageAdapter mAdapter;
	private List<PackageBean> packages;
	private List<ExpressionBean> mEpressionBeans;
	private View mBack, mSetting;
	// private Http http;
	private CIMExpressionHttp mHttp;
	private String token = SYUserManager.getInstance().getToken();;
	private String vc;// 版本号
	private String userId;
	private UpdateBroadCastReceiver receiver;
	private HistoryDao historyDao;
	private TextView title;
	// private long minSortNo = 0;
	protected ProgressBarHelper progress;
	private View view;
	private int pageSize = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		view = View.inflate(this, R.layout.im_expressionlist_activity, null);
		setContentView(view);
		// historyDao = new HistoryDao(this);
		receiver = new UpdateBroadCastReceiver(this);
		receiver.registerAction("updateUI");
		receiver.registerAction(Constants.DELETE_ACTION);
		receiver.registerAction(Constants.FAIL_ACTION);
		mHttp = new CIMExpressionHttp(this);
		findViews();
		setListener();
		doInitProgressBar(view);
		loadData(0);

	}

	public void findViews() {
		mBack = this.findViewById(R.id.back);
		mSetting = this.findViewById(R.id.setting);
		mExpressionListView = (PullToRefreshListView) this
				.findViewById(R.id.lv_expression_list);
		//mExpressionListView.setMode(Mode.PULL_UP_TO_REFRESH);
		mExpressionListView.setMode(Mode.DISABLED);
		title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getText(R.string.expression_list_title));
	}

	public void setListener() {
		mBack.setOnClickListener(this);
		mSetting.setOnClickListener(this);
		mExpressionListView.setOnItemClickListener(this);
		mExpressionListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				Slog.d("callback", "加载更多");
				long minSortNo = mAdapter.getMinSortNo();
				loadData(minSortNo);

			}
		});
	}

	public void loadData(long minSortNo) {
//		mHttp.getExpressionList(CIMExpressionHttp.IM_EXPRESSIONLIST_METHOD,
//				token, minSortNo, pageSize, this);
		IMExpressionListRequest.send(CIMExpressionHttp.IM_EXPRESSIONLIST_METHOD,
				token, minSortNo, pageSize, this);
	}

	@Override
	public void onHttpResponse(IRequest _request) {
		// TODO Auto-generated method stub
		super.onHttpResponse(_request);
		HttpJsonResponse response = null;
		switch (_request.getmId()) {
		case CIMExpressionHttp.IM_EXPRESSIONLIST_METHOD:
			response = (HttpJsonResponse) _request.getResponse();
			Type type = new TypeToken<List<PackageBean>>() {
			}.getType();
			long pre = System.currentTimeMillis();
			List<PackageBean> beans = new Gson().fromJson(
					response.getBodyArray(), type);
			long end = System.currentTimeMillis()-pre;
			Slog.d("callback", "----------"+end);
			if (beans.size() <= 0) {
				showNoData();
			} else {
				goneLoad();
				setData(beans);
				mExpressionListView.onRefreshComplete();
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onHttpError(IRequest _request) {
		// TODO Auto-generated method stub
		super.onHttpError(_request);
		switch (_request.getmId()) {
		case CIMExpressionHttp.IM_EXPRESSIONLIST_METHOD:
			showNetError();
			break;
		}
	}

	private void showNetError() {
		if (progress != null)
			progress.showNetError();
	}

	private void showNoData() {
		if (progress != null)
			progress.showNoData();
	}

	private void goneLoad() {
		if (progress != null)
			progress.goneLoading();
	}

	public void getExpressionListSuccess(List<PackageBean> beans) {
		if (beans == null) {
			Toast.makeText(this, "加载失败", Toast.LENGTH_LONG).show();
			return;
		}
		// 下载完成后 update
		setData(beans);
		// historyDao.save(beans); // 持久化到db

	}

	public void setData(List<PackageBean> beans) {
		packages = beans;
		mAdapter = new PackageAdapter(this, packages, 0);
		mExpressionListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.putExtra("packageBean", packages.get(position-1));
		intent.setClass(this, ExpressionDetailActivity.class);
		startActivity(intent);

	}

	private void doInitProgressBar(View view) {
		progress = new ProgressBarHelper(this,
				view.findViewById(R.id.ll_data_loading));
		progress.setProgressBarClickListener(new ProgressBarClickListener() {
			@Override
			public void clickRefresh() {
				// doSetDataByNet();
				loadData(0);

			}
		});
		if (!CMainHttp.getInstance().isNetworkAvailable(mContext)) {
			showNetError();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		case R.id.setting:
			Intent intent = new Intent();
			intent.setClass(this, IMSettingActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	class UpdateBroadCastReceiver extends BroadcastReceiver {
		Context context;
		IntentFilter filter = new IntentFilter();

		public UpdateBroadCastReceiver(Context context) {
			this.context = context;
		}

		public void registerAction(String action) {
			filter.addAction(action);
			registerReceiver(this, filter);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (mAdapter == null)
				return;
			if ("updateUI".equals(action)) {
				mAdapter.notifyDataSetChanged();
			} else if (Constants.DELETE_ACTION.equals(action)) { // 卸载
				Slog.d("callback", "卸载成功!");
				PackageBean bean = (PackageBean) intent
						.getSerializableExtra("packagebean");
				mAdapter.updateList(bean);
			} else if (Constants.FAIL_ACTION.equals(action)) {
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	protected void onDestroy() {
                    mHttp.cancelAll();
		super.onDestroy();

		unregisterReceiver(receiver);



	};
}
