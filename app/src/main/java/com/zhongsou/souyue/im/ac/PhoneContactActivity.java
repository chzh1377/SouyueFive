package com.zhongsou.souyue.im.ac;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.PhoneContactAdapter;
import com.zhongsou.souyue.im.asyntask.CompareContactAsynTask;
import com.zhongsou.souyue.im.dialog.ImContactDialog;
import com.zhongsou.souyue.im.dialog.ImContactDialog.Builder.ImContactDialogInterface;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.dialog.ImDialog.Builder.ImDialogInterface;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.util.SMSUtils;
import com.zhongsou.souyue.im.view.AlphaSideBar;
import com.zhongsou.souyue.module.MobiContactEntity;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SYUserManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 手机联系人
 * 
 * @author wangchunyan@zhongsou.com
 * 
 */
public class PhoneContactActivity extends IMBaseActivity implements
		OnItemClickListener {
	private ListView listView;
	private PhoneContactAdapter adapter;
	private AlphaSideBar indexBar;
	private TextView txtOverlay;
	private WindowManager mWindowManager;
	private BroadcastReceiver br;
//	private ImProgressDialog dialog;
	private SMSUtils sMSUtils;
	private List<Contact> l = new ArrayList<Contact>();
	private List<Contact> lUpdate = new ArrayList<Contact>();
	private LoadPhoneContactsTask loadphonecontactstask;
	SYSharedPreferences sysp;
    private boolean isIMContactsUpload;

    private ConnectivityManager mConnectivityManager;   //联网用到
    private NetworkInfo mNetInfo;                       //联网用到
	private ContentResolver resolver;
	private SharedPreferences sPreferences;
	private LoadPhoneUpdateContactsTask loadphoneupdatecontactstask;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				MobiContactEntity mce = (MobiContactEntity) msg.obj;
				if (mce != null) {
					if (adapter != null) {
						adapter.setData(mce);
					}
				}
                dismissProgress();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		};
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.im_phonecontact);
		adapter = new PhoneContactAdapter(this);
		initView();
		sMSUtils = new SMSUtils(this, handler);
		listView.setAdapter(adapter);
		regeditBroadcast();
		loadphonecontactstask = new LoadPhoneContactsTask();
		loadphoneupdatecontactstask = new LoadPhoneUpdateContactsTask();
		sysp = SYSharedPreferences.getInstance();
		sPreferences = getSharedPreferences("contect",MODE_PRIVATE);
		resolver = getContentResolver();
        isIMContactsUpload = sysp
                .getBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, false);

		if (isIMContactsUpload) {
			flag = false;
			loadphonecontactstask.execute();// 来自广播
			loadphoneupdatecontactstask.execute();
		} else {
			flag = true;
			isFirstReadContact();
		}
        // 网络广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, mFilter);
	}

	private boolean flag;
	private void isFirstReadContact() {
		Map<Long, Integer> map = getContactVersion();
		Editor editor = sPreferences.edit();
		editor.putString("key", Arrays.toString(map.keySet().toArray()));
		editor.putString("value",
				Arrays.toString(map.values().toArray()));
		editor.commit();
		if (!isIMContactsUpload) {
			Dialog build = new AlertDialog.Builder(PhoneContactActivity.this)
					.setTitle(R.string.im_dialog_upload_contact_title)
					.setMessage(R.string.im_dialog_upload_contact_msgs)
					.setPositiveButton(R.string.im_dialog_ok,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									loadphonecontactstask.execute();
									sysp.putBoolean(
											SYSharedPreferences.ISFIRSTREADCONTACT,
											true);

								}
							})
					.setNegativeButton(R.string.im_dialog_cancel,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
			build.show();
		}
	}

	public class LoadPhoneContactsTask extends
			ZSAsyncTask<Void, Void, List<Contact>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<Contact> doInBackground(Void... paramArrayOfParams) {
			Cursor cur = null;
			try {
				cur = getContentResolver().query(Phone.CONTENT_URI,
						new String[] { "display_name", "data1", "sort_key" },
						null, null, "sort_key");
				cur.moveToFirst();
				while (cur.getCount() > cur.getPosition()) {

					String number = cur.getString(cur
							.getColumnIndex(Phone.NUMBER));
					if (number.length() < 11) {
						cur.moveToNext();
						continue;
					}
					if (number.startsWith("0")) {
						cur.moveToNext();
						continue;
					}
					number = number.replaceAll("\\D", "");
					if (number.length() > 11) {
						number = number.substring(number.length() - 11,
								number.length());
					}

					String name = cur.getString(cur
							.getColumnIndex(Phone.DISPLAY_NAME));
					Contact person = new Contact();
					person.setNick_name(name);
					person.setPhone(number);
					person.setLocal_order(cur.getString(cur
							.getColumnIndex(Phone.SORT_KEY_PRIMARY)));
					l.add(person);
					cur.moveToNext();
				}
			} catch (Exception e) {
				Log.i(TAG, " get phone contact fail" + e.getMessage());
			} finally {
				try {
					if (cur != null && !cur.isClosed())
						cur.close();
				}catch (Exception e){
					e.printStackTrace();
				}

			}

			return l;
		}

		@Override
		protected void onPostExecute(List<Contact> result) {
			super.onPostExecute(result);
			try {
				if (result != null && result.size() > 0) {
					if(flag){
					ImserviceHelp.getInstance().im_contacts_status(
							new Gson().toJson(result));
					}else{
						CompareContactAsynTask ccat = new CompareContactAsynTask(l,
								handler);
						ccat.execute(new ArrayList<Contact>());
					}
				} else {
					ImContactDialog.Builder build = new ImContactDialog.Builder(
							PhoneContactActivity.this);
					build.setPositiveButton(new ImContactDialogInterface() {

						@Override
						public void onClick(DialogInterface dialog, View v) {
							finish();
						}
					});
					build.create().show();
				}
			} catch (Exception e) {
			}
		}

	}

	public class LoadPhoneUpdateContactsTask extends
			ZSAsyncTask<Void, Void, List<Contact>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<Contact> doInBackground(Void... paramArrayOfParams) {
			Cursor cur = null;
			try {
				ArrayList<Long> idList = new ArrayList<Long>();// 接收变化的联系人id
				Map<Long, Integer> newMap = getContactVersion();
				Map<Long, Integer> oldMap = stringToMap(
						sPreferences.getString("key", ""),
						sPreferences.getString("value", ""));
				Set<Long> set = newMap.keySet();
				for (Long id : set) {
					if (oldMap.containsKey(id)) {// 存在
						if (oldMap.get(id) != newMap.get(id)) {// 老版本不等于新版本
							idList.add(id);
						}
					} else {// 不存在
						idList.add(id);
					}
				}
				if (idList.size() >= 0) {
					for (Long id : idList) {
						Cursor cursor = resolver.query(Phone.CONTENT_URI,
								new String[] { "display_name", "data1" },
								Phone.CONTACT_ID + "=" + id, null, null);
						cursor.moveToFirst();
						String number = cursor.getString(cursor
								.getColumnIndex(Phone.NUMBER));
						if (number.length() < 11) {
							cursor.moveToNext();
							continue;
						}
						if (number.startsWith("0")) {
							cursor.moveToNext();
							continue;
						}
						number = number.replaceAll("\\D", "");
						if (number.length() > 11) {
							number = number.substring(number.length() - 11,
									number.length());
						}
						String name = cursor.getString(cursor
								.getColumnIndex(Phone.DISPLAY_NAME));
						Contact person = new Contact();
						person.setNick_name(name);
						person.setPhone(number);
						lUpdate.add(person);

//						Editor editor = sPreferences.edit();
//						editor.putString("key",
//								Arrays.toString(newMap.keySet().toArray()));
//						editor.putString("value",
//								Arrays.toString(newMap.values().toArray()));
//						editor.commit();
					}
				}

			} catch (Exception e) {
				// Log.i(TAG, " get phone contact fail" + e.getMessage());
			} finally {
				try {
					if (cur != null && !cur.isClosed())
						cur.close();
				}catch (Exception e){
					e.printStackTrace();
				}

			}

			return lUpdate;
		}

		@Override
		protected void onPostExecute(List<Contact> result) {
			super.onPostExecute(result);
			try {
				if(result != null){
					ImserviceHelp.getInstance().im_contacts_status(
							new Gson().toJson(result));
				}
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 注册广播
	 */
	private void regeditBroadcast() {
		br = new PhoneBroadCastReceiver();
        IntentFilter uploadContactFilter = new IntentFilter();
        uploadContactFilter.addAction(BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR);
        uploadContactFilter.addAction(BroadcastUtil.ACTION_MOBILE_CONTACT);
		registerReceiver(br, uploadContactFilter);
	}

	private void unRegeditBroadcast() {
		if (br != null)
			unregisterReceiver(br);
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.section_list_view);
		listView.setOnItemClickListener(this);
		((TextView) findViewById(R.id.activity_bar_title))
				.setText(getString(R.string.im_phoncontact_title));
		setSlideBar();
        showProgress();
	}

	private void setSlideBar() {
		indexBar = (AlphaSideBar) findViewById(R.id.sideBar);
		indexBar.setListView(listView);
		txtOverlay = (TextView) LayoutInflater.from(this).inflate(
				R.layout.im_list_position, null);
		txtOverlay.setVisibility(View.INVISIBLE);
		indexBar.setTextView(txtOverlay);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(txtOverlay, lp);
	}

	@Override
	protected void onPause() {
        dismissProgress();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegeditBroadcast();
		if (txtOverlay != null) {
			WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.removeView(txtOverlay);
		}
		if (sMSUtils != null)
			sMSUtils.unListening(PhoneContactActivity.this);
        if (mNetReceiver != null)
            unregisterReceiver(mNetReceiver);
	}

	public void sendMessage(String phoneNo) {
		sMSUtils.sendSms(sysp.getString(SYSharedPreferences.UM_INVITE_SMS, ""),
				phoneNo);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pisotion,
			long arg3) {
		if (adapter.getItem(pisotion).getStatus() == Contact.STATUS_IS_SOUYUE_USER_NOT_FRIEND) {
			ImDialog.Builder builder = new ImDialog.Builder(
					PhoneContactActivity.this);
			builder.setEditMsg("我是"+SYUserManager.getInstance().getName());
			builder.setTitle(R.string.im_dialog_title);
			builder.setPositiveButton("确定",
					new OKClickListener(adapter.getItem(pisotion),
							PhoneContactActivity.this));
			builder.create().show();
		} else if (adapter.getItem(pisotion).getStatus() == Contact.STATUS_IS_NOT_SOUYUE_USER) {
			sendMessage(adapter.getInfolist().get(pisotion).getPhone());
		}

	}

	public class PhoneBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			if (intent.getAction().equals(BroadcastUtil.ACTION_MOBILE_CONTACT)) {
				String json = intent.getStringExtra("data");
				try {
					List<Contact> data = null;
					if (json != null && json.length() > 0) {
						data = new Gson().fromJson(json,
								new TypeToken<List<Contact>>() {
								}.getType());
					}
					CompareContactAsynTask ccat = new CompareContactAsynTask(l,
							handler);
					ccat.execute(data);
				} catch (Exception e) {
					e.printStackTrace();
				}

                Map<Long, Integer> newMap = getContactVersion();
                Editor editor = sPreferences.edit();
                editor.putString("key",
                        Arrays.toString(newMap.keySet().toArray()));
                editor.putString("value",
                        Arrays.toString(newMap.values().toArray()));
                editor.commit();
			}else if (intent.getAction().equals(BroadcastUtil.ACTION_MOBILE_CONTACT_UPLOAD_ERROR)){

            }
		}
	}

	public class OKClickListener implements ImDialogInterface {
		Contact item;
		WeakReference<Context> weakReference;

		public OKClickListener(Contact item, Context ctx) {
			this.item = item;
			weakReference = new WeakReference<Context>(ctx);
		}

		@Override
		public void onClick(DialogInterface dialog, View v) {
			if (item != null && weakReference != null
					&& weakReference.get() != null) {
				ImserviceHelp.getInstance().im_userOp(1, item.getChat_id(),
						item.getNick_name(), item.getAvatar(),
						v.getTag().toString());
				dialog.dismiss();
			}
		}

	}

	
	/**
	 * 获取联系人版本
	 */
	public Map<Long, Integer> getContactVersion() {
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		Cursor c = resolver
				.query(RawContacts.CONTENT_URI, new String[] {
						RawContacts.CONTACT_ID, RawContacts.VERSION }, null,
						null, null);
		try {
			while (c.moveToNext()) {
				long cid = c.getLong(c.getColumnIndex(RawContacts.CONTACT_ID));
				int ver = c.getInt(c.getColumnIndex(RawContacts.VERSION));
				map.put(cid, ver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (c != null && !c.isClosed()) {
					c.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}

	private Map<Long, Integer> stringToMap(String str, String str2) {
		Map<Long, Integer> map = new HashMap<Long, Integer>();
		String[] keys = str.replace("[", "").replace("]", "")
				.replaceAll(" ", "").split(",");
		String[] values = str2.replace("[", "").replace("]", "")
				.replaceAll(" ", "").split(",");
		for (int i = 0; i < values.length; i++) {
			map.put(Long.valueOf(keys[i]), Integer.valueOf(values[i]));
		}
		return map;
	}

    /**
     * 监听网络变化广播 做出相应的提示
     */
    private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                mNetInfo = mConnectivityManager.getActiveNetworkInfo();
                if (!(mNetInfo != null && mNetInfo.isAvailable()))
                    SouYueToast.makeText(MainApplication.getInstance(),"服务器忙，请稍后再试", Toast.LENGTH_LONG).show();
            }
        }
    };
}
