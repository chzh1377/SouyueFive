package com.zhongsou.souyue.im.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.PackageAdapter;
import com.zhongsou.souyue.im.download.PackageDao;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Constants;

import java.util.List;

/**
 * 表情管理
 * 
 * @author wangqiang on 15/4/19
 * 
 */
public class IMSettingActivity extends IMBaseActivity implements
		OnClickListener {

	private ListView mExpressionListView;
	private PackageAdapter mAdapter;
	private List<PackageBean> packages;
	private View mBack, mSetting;
	private TextView title;
	private IntentFilter mFilter;
	private LinearLayout default_show;

	// private Button downLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_expression_sestting);
		findViews();
		setListener();
		loadData();
		setData();
		showIfNull();
		mFilter = new IntentFilter(Constants.DELETE_ACTION);
		mFilter.addAction("updateUI");
		mFilter.addAction(Constants.FAIL_ACTION);
		registerReceiver(mUpdateRecevier, mFilter);
	}

	public void findViews() {
		mBack = this.findViewById(R.id.back);
		mSetting = this.findViewById(R.id.setting);
		mExpressionListView = (ListView) this
				.findViewById(R.id.lv_expression_list);
		title = (TextView) this.findViewById(R.id.title);
		this.findViewById(R.id.setting).setVisibility(View.GONE);
		default_show = (LinearLayout) findViewById(R.id.default_show);
	}

	public void setListener() {
		mBack.setOnClickListener(this);
		// downLoad.setOnClickListener(this);
	}

	PackageDao dao = new PackageDao(this);

	public void loadData() {
		title.setText("表情管理");
		packages = dao.queryDone(); // 下载过

	}

	public void setData() {
		mAdapter = new PackageAdapter(this, packages, 1);
		mExpressionListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;
		// case R.id.down_load:
		// Intent intent = new Intent();
		// intent.setClass(mContext, IMExpressionListActivity.class);
		// mContext.startActivity(intent);
		// finish();
		// ;break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mUpdateRecevier);
	}

	private BroadcastReceiver mUpdateRecevier = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Constants.DELETE_ACTION.equals(intent.getAction())) {
				// PackageBean bean = (PackageBean)
				// intent.getSerializableExtra("packagebean");
				mAdapter.notifyDataSetChanged();
			} else if ("updateUI".equals(intent.getAction())) {
				mAdapter.notifyDataSetChanged();
			}else if(Constants.FAIL_ACTION.equals(intent.getAction())){
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 当可显示的表情为空时，显示没表情界面
	 */
	private void showIfNull() {
		if (packages.size() < 1) {
			mExpressionListView.setVisibility(View.GONE);
			default_show.setVisibility(View.VISIBLE);
		} else {
			mExpressionListView.setVisibility(View.VISIBLE);
			default_show.setVisibility(View.GONE);
		}
	}
}
