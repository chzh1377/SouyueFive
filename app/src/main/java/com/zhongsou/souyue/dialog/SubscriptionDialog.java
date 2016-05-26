package com.zhongsou.souyue.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.module.Group;
import com.zhongsou.souyue.module.SubscribeBack;
import com.zhongsou.souyue.net.sub.GroupListRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDialog extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	private ListView groupLV;
	private List<DataInfo> data = new ArrayList<DataInfo>();
	private long currentId;
	private String groupName;
	private GroupAdapter gadapter;
	private List<Button> btnSet = new ArrayList<Button>();
//	private Http http;
	 private CMainHttp mainHttp;
	private String token;
	private String from;
	private String keyword;
	private String srpId;
	private RelativeLayout progress;
	boolean isCreate = false;
	private TextView sub_refresh_tv;
	private String str_loading, str_reload;
	private ImageView sub_refresh_iv;
	private Animation rotateAnim;
	private int fragmentPos;

//	public void groupListSuccess(List<Group> groups) {
//		progress.setVisibility(View.GONE);
//		sub_refresh_iv.clearAnimation();
//
//		for (Group group : groups) {
//			data.add(new DataInfo(group.name(), group.id(), false));
//		}
//		setDefaultSelect();
//		gadapter.notifyDataSetChanged();
//	}

	public void subscribeAddSrpSuccess(SubscribeBack subscribeBack) {
		SouYueToast.makeText(getApplicationContext(),
				R.string.subscribe__success, SouYueToast.LENGTH_SHORT).show();
		sysp.putBoolean(SYSharedPreferences.KEY_UPDATE, true);
		Intent mIntent = new Intent("changeText");
		mIntent.putExtra("changeText", 1); // 1订阅成功
		mIntent.putExtra("fragmentPos",fragmentPos);
		sendBroadcast(mIntent);
	}

//	@Override
//	public void onHttpError(String methodName, AjaxStatus as) {
//		if("groupList".equals(methodName)){//列表显示异常
//			sub_refresh_tv.setText(str_reload);
//			sub_refresh_iv.clearAnimation();
//			progress.setVisibility(View.VISIBLE);
//			return;
//		}
//
//		Intent mIntent = new Intent("changeText");
//		mIntent.putExtra("changeText", -1);// 不改变原来的状态
//		mIntent.putExtra("fragmentPos",fragmentPos);
//		// mIntent.putExtra("keyword", keyword);
//		sendBroadcast(mIntent);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (sysp == null)
			sysp = SYSharedPreferences.getInstance();
		token = SYUserManager.getInstance().getToken();
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		fragmentPos=	intent.getIntExtra("fragmentPos", 0);//新闻详情页用
		keyword = intent.getStringExtra("keyword");
		srpId = intent.getStringExtra("srpId");
		data = new ArrayList<DataInfo>();
		setContentView(R.layout.group_dialog_choose);
		
		rotateAnim = AnimationUtils.loadAnimation(this, R.anim.sub_refresh_rotateanim);
		str_loading = getResources().getString(R.string.loading_ing);
		str_reload = getResources().getString(R.string.sub_reload_fail);
		
		initView();
		addData();
	}

	private void initView() {
		progress = (RelativeLayout) findViewById(R.id.group_load_progress);
		progress.setOnClickListener(this);
		sub_refresh_tv = (TextView) findViewById(R.id.sub_refresh_tv);
		sub_refresh_iv = (ImageView) findViewById(R.id.sub_refresh_iv);
		
		btnSet.add((Button) findViewById(R.id.btn_choose_save));
		btnSet.add((Button) findViewById(R.id.btn_choose_canel));
		findViewById(R.id.btn_add_gruop).setOnClickListener(this);
		for (Button btn : btnSet) {
			btn.setOnClickListener(this);
		}
		groupLV = (ListView) findViewById(R.id.group_lv);
		gadapter = new GroupAdapter();
		groupLV.setAdapter(gadapter);
		groupLV.setOnItemClickListener(this);
	}

	private void addData() {
		progress.setVisibility(View.VISIBLE);
		sub_refresh_tv.setText(str_loading);
		sub_refresh_iv.startAnimation(rotateAnim);
//		http = new Http(this);
		//http.groupList(token, srpId);
		mainHttp = CMainHttp.getInstance();
		GroupListRequest group = new GroupListRequest(HttpCommon.SUB_GROUPLIST_REQUEST,this);
		group.setParams(token, srpId);
		mainHttp.doRequest(group);
	}

	private class DataInfo {
		public String text;
		public long id;
		public boolean imageStatue;

		public DataInfo(String text, long id, boolean imageStatue) {
			this.text = text;
			this.id = id;
			this.imageStatue = imageStatue;
		}
	}

	private class GroupAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(SubscriptionDialog.this)
						.inflate(R.layout.group_dialog_item, null);
				holder = new ViewHolder();
				holder.groupIV = (ImageView) convertView
						.findViewById(R.id.group_image);
				holder.groupName = (TextView) convertView
						.findViewById(R.id.group_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.groupIV
					.setBackgroundResource(data.get(position).imageStatue ? R.drawable.check
							: R.drawable.check_normal);
			holder.groupName.setText(data.get(position).text);
			return convertView;
		}

	}

	static class ViewHolder {
		TextView groupName;
		ImageView groupIV;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		for (DataInfo info : data) {
			info.imageStatue = false;
		}
		currentId = data.get(position).id;
		groupName = data.get(position).text;
		data.get(position).imageStatue = true;
		if (gadapter != null)
			gadapter.notifyDataSetChanged();
	}

	public void setDefaultSelect() {
		isCreate = sysp.getBoolean(SYSharedPreferences.KEY_ISCREATE, false);
		if (data.size() > 0) {
			if (isCreate) {
				data.get(data.size() - 1).imageStatue = true;
				groupName = data.get(data.size() - 1).text;
				currentId = data.get(data.size() - 1).id;
				groupLV.setSelection(gadapter.getCount());
				gadapter.notifyDataSetChanged();
				sysp.putBoolean(SYSharedPreferences.KEY_ISCREATE, false);
			} else {
				data.get(0).imageStatue = true;
				groupName = data.get(0).text;
				currentId = data.get(0).id;
			}
		}
	}

	/**
	 * srp页关键词订阅
	 * 
	 * @param keyword
	 * @param srpId
	 * @param deleteId
	 * @param groupName
	 */
	public void subKeyword(String keyword, String srpId, long deleteId,
			String groupName) {
		List<String> keywordAdd = new ArrayList<String>();
		List<String> srpIdAdd = new ArrayList<String>();
		List<Object> idDelete = new ArrayList<Object>();
		if (srpId != null) {
			srpIdAdd.add(srpId);
		}
		if (deleteId != -1) {
			idDelete.add(deleteId);
		}
		if (keyword != null && !keyword.trim().equals("")) {
			keywordAdd.add(keyword);
//			http.subscribeAddSrp(token, keywordAdd, srpIdAdd, idDelete,
//					groupName);
		} else {
			SouYueToast.makeText(getApplicationContext(),
					R.string.subscribe_fail, SouYueToast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_choose_save:
			if (groupName == null || "".equals(groupName) && currentId == -1) {
				if (data.size() > 0) {
					groupName = data.get(0).text;
					currentId = data.get(0).id;
				}
			}
			
			if (from != null && from.equals("popmenu")) {
				subKeyword(keyword, srpId, -1, groupName);
			} else {
			      Intent mIntent = new Intent("com.zhongsou.souyue.dialog");
		          mIntent.putExtra("currentId", currentId);
		          sendBroadcast(mIntent);
			}
			finish();
			break;
		case R.id.btn_choose_canel:
		    if(from != null && from.equals("SettingGroupActivtiy")){
		        Intent mIntent = new Intent("com.zhongsou.souyue.dialog");
                sendBroadcast(mIntent);
		    }else{
	            Intent mIntent = new Intent("changeText");
	            mIntent.putExtra("changeText", -1);
	            mIntent.putExtra("fragmentPos",fragmentPos);
	            sendBroadcast(mIntent);  
		    }

			finish();
			break;
		case R.id.group_load_progress:
			if(str_reload.equals(sub_refresh_tv.getText())){
				addData();
			}
			break;
		}
	}

	@Override
	public void onHttpResponse(IRequest request) {
		switch (request.getmId()){
			case HttpCommon.SUB_GROUPLIST_REQUEST:
				List<Group> groups = request.getResponse();
				progress.setVisibility(View.GONE);
				sub_refresh_iv.clearAnimation();
				for (Group group : groups) {
					data.add(new DataInfo(group.name(), group.id(), false));
				}
				setDefaultSelect();
				gadapter.notifyDataSetChanged();;
				break;
		}
	}

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()){
           case HttpCommon.SUB_GROUPLIST_REQUEST://列表显示异常
                sub_refresh_tv.setText(str_reload);
                sub_refresh_iv.clearAnimation();
                progress.setVisibility(View.VISIBLE);
            break;
        }
    }
}
