package com.zhongsou.souyue.im.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tuita.sdk.BroadcastUtil;
import com.tuita.sdk.PushService;
import com.tuita.sdk.im.db.module.Contact;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.adapter.ChatAdapter;
import com.zhongsou.souyue.im.dialog.ImContactDialog;
import com.zhongsou.souyue.im.dialog.ImContactDialog.Builder.ImContactDialogInterface;
import com.zhongsou.souyue.im.dialog.ImProgressDialog;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.view.SwipeListView;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author wangchunyan@zhongsou.com //查询数据库的操作 初始化页面时查询一次，每收到广播查询一次
 */
public class ChatFragment extends Fragment {
	private SwipeListView swipelistview;
	private ChatAdapter chatAdapter;
	private Activity activity;
	private MessageRecentReceiver messageRecentReceiver;
	private ImserviceHelp service = ImserviceHelp.getInstance();
	private RelativeLayout network_state;
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;
	private List<MessageRecent> resultlist;
	private MessageRecentTask task;

	private List<Contact> l = new ArrayList<Contact>();
	private LoadPhoneContactsTask loadphonecontactstask;
	private LoadPhoneUpdateContactsTask loadphoneupdatecontactstask;
	private ImProgressDialog dialog;
	private boolean isFirst;
	private boolean isImFirst;
	SYSharedPreferences sysp;
	private SharedPreferences sPreferences;

	private ContentResolver resolver;
	private long newTime;
	private static final long TIME = 1000 * 60 * 60 * 24;
//	private static final long TIME = 1000 * 20;
	private BroadcastReceiver br;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.im_chat_fragment_layout,
				container, false);
		((TextView) root.findViewById(R.id.activity_bar_title))
				.setText(getString(R.string.im_chat_text));
		swipelistview = (SwipeListView) root.findViewById(R.id.delete_lv_list);
		network_state = (RelativeLayout) root.findViewById(R.id.network_state);
		network_state.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (android.os.Build.VERSION.SDK_INT > 10) {
					// 3.0以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
					startActivity(new Intent(
							android.provider.Settings.ACTION_SETTINGS));
					activity.overridePendingTransition(R.anim.left_in,
							R.anim.left_out);
				} else {
					startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					activity.overridePendingTransition(R.anim.left_in,
							R.anim.left_out);
				}
			};
		});
		if (getActivity() != null) {
			activity = getActivity();
		}
		chatAdapter = new ChatAdapter(activity, swipelistview,this);
		swipelistview.setAdapter(chatAdapter);
		swipelistview
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
//						MessageRecent item = chatAdapter.getItem(position);
//						if(item.getUser() != null){
////							startActivityWithAnim(TestChatActivity.class,
////									item);
//                            IMChatActivity.invoke(getActivity(), IConst.CHAT_TYPE_PRIVATE, item.getChat_id());
//						}else if(item.getGroup() != null){
////							startActivity2Group(GroupChatActivity.class,item);
//						}
					}
				});
		return root;
	}

	@Override
	public void onResume() {
		PushService.setMsgNum(MainApplication.getInstance());
		loadData();
		setReciever();
		super.onResume();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

//	private void startActivityWithAnim(Class<?> clazz,
//			MessageRecent messageRecent) {
//		Intent intent = new Intent();
//		intent.setClass(activity, clazz);
//		intent.putExtra(IMChatActivity.KEY_CONTACT, messageRecent.getUser());
//		intent.putExtra("BUBBLENUM", messageRecent.getBubble_num());
//		startActivity(intent);
//		activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
//	}
	
//	private void startActivity2Group(Class<?> clazz,
//			MessageRecent messageRecent) {
//		Intent intent = new Intent();
//		intent.setClass(activity, clazz);
//		intent.putExtra("group", messageRecent.getGroup());
//		intent.putExtra("BUBBLENUM", messageRecent.getBubble_num());
//		startActivity(intent);
//		activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
//	}

	public void loadData() {
		task = new MessageRecentTask();
		task.execute(service);
	}

	private class MessageRecentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadData();
		}

	}

	private void setReciever() {
		IntentFilter inf = new IntentFilter();
		inf.addAction(BroadcastUtil.ACTION_MSG_ADD);
		inf.addAction(BroadcastUtil.ACTION_CONTACT_AND_MSG);
		inf.addAction(BroadcastUtil.ACTION_CLRAR_MESSAGE_BUBBLE);
		inf.addAction(BroadcastUtil.ACTION_MSG_SEND_SUCCESS);
		messageRecentReceiver = new MessageRecentReceiver();
		activity.registerReceiver(messageRecentReceiver, inf);

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		activity.registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (messageRecentReceiver != null) {
			activity.unregisterReceiver(messageRecentReceiver);
		}
		if (mReceiver != null) {
			activity.unregisterReceiver(mReceiver);
		}
	}

	@Override
	public void onDestroy() {
		unRegeditBroadcast();
		super.onDestroy();
	}

	/**
	 * 监听网络变化广播 做出相应的提示
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				connectivityManager = (ConnectivityManager) getActivity()
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable())
					network_state.setVisibility(View.GONE);
				else
					network_state.setVisibility(View.VISIBLE);
			}
		}
	};

	class MessageRecentTask extends
			ZSAsyncTask<ImserviceHelp, Void, List<MessageRecent>> {
		@Override
		protected List<MessageRecent> doInBackground(ImserviceHelp... params) {
			try {
				return params[0].db_getMessageRecent();
			} catch (Exception ex) {

			}
			return null;
		}

		@Override
		protected void onPostExecute(List<MessageRecent> result) {
			if (chatAdapter != null && result != null)
				chatAdapter.setData(result);
			super.onPostExecute(result);
		}
	}

	public class LoadPhoneContactsTask extends
			ZSAsyncTask<Void, Void, List<Contact>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!dialog.isShowing()) {
				dialog.show();
			}
		}

		@Override
		protected List<Contact> doInBackground(Void... paramArrayOfParams) {
			Cursor cur = null;
			try {
				cur = getActivity().getContentResolver().query(
						Phone.CONTENT_URI,
						new String[] { "display_name", "data1" }, null, null,
						null);
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
					// person.setLocal_order(cur.getString(cur
					// .getColumnIndex(Phone.SORT_KEY_PRIMARY)));
					l.add(person);
					cur.moveToNext();
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

			return l;
		}

		@Override
		protected void onPostExecute(List<Contact> result) {
			super.onPostExecute(result);
			try {
				if (result != null && result.size() > 0) {
					ImserviceHelp.getInstance().im_contacts_upload(
							new Gson().toJson(result));
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
				} else {
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
					ImContactDialog.Builder build = new ImContactDialog.Builder(
							getActivity());
					build.setPositiveButton(new ImContactDialogInterface() {

						@Override
						public void onClick(DialogInterface dialog, View v) {
							// finish();
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
			if (!dialog.isShowing()) {
				dialog.show();
			}
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
						l.add(person);


						Editor editor = sPreferences.edit();
						editor.putLong("time", newTime);
						editor.putString("key", Arrays.toString(newMap.keySet().toArray()));
						editor.putString("value",
								Arrays.toString(newMap.values().toArray()));
						editor.commit();
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

			return l;
		}

		@Override
		protected void onPostExecute(List<Contact> result) {
			super.onPostExecute(result);
			try {
				if (result != null && result.size() > 0) {
					ImserviceHelp.getInstance().im_contacts_upload(
							new Gson().toJson(result));
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
				} else {
					if (dialog != null && dialog.isShowing())
						dialog.dismiss();
//					ImContactDialog.Builder build = new ImContactDialog.Builder(
//							getActivity());
//					build.setPositiveButton(new ImContactDialogInterface() {
//
//						@Override
//						public void onClick(DialogInterface dialog, View v) {
//							// finish();
//						}
//					});
//					build.create().show();
				}
			} catch (Exception e) {
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sysp = SYSharedPreferences.getInstance();
		regeditBroadcast();
		isFirst = sysp
				.getBoolean(SYSharedPreferences.ISFIRSTREADCONTACT, false);
		isImFirst = sysp
				.getBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, false);
		loadphonecontactstask = new LoadPhoneContactsTask();
		loadphoneupdatecontactstask = new LoadPhoneUpdateContactsTask();
		dialog = new ImProgressDialog.Builder(getActivity()).create();
		sPreferences = getActivity().getSharedPreferences("contect",
				getActivity().MODE_PRIVATE);
		resolver = getActivity().getContentResolver();
		newTime = System.currentTimeMillis();
		if (isFirst) {
			long oldTime = sPreferences.getLong("time", 0);
			if (newTime - oldTime >= TIME) {
				loadphoneupdatecontactstask.execute();
			}else{
				//24小时以内暂不做处理，直接跳转
			}
		} else {
			Map<Long, Integer> map = getContactVersion();
			Editor editor = sPreferences.edit();
			editor.putLong("time", newTime);
			editor.putString("key", Arrays.toString(map.keySet().toArray()));
			editor.putString("value",
					Arrays.toString(map.values().toArray()));
			editor.commit();
			isFirstReadContact();
		}

	}

	private void isFirstReadContact() {
		if (!isImFirst) {
		Builder buiDialog = new AlertDialog.Builder(getActivity());
		buiDialog.setTitle(R.string.im_dialog_upload_contact_title);
		buiDialog.setMessage(R.string.im_dialog_upload_contact_msgs);
		buiDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				loadphonecontactstask.execute();
				sysp.putBoolean(SYSharedPreferences.ISFIRSTREADCONTACT, true);
				sysp.putBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, true);
			}
		});

		buiDialog.setNegativeButton(R.string.im_dialog_cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				sysp.putBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, true);
			}
		});

		buiDialog.create();
		buiDialog.show();
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
	
	public class PhoneBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			sysp.putBoolean(SYSharedPreferences.ISFIRSTREADCONTACT, false);
			sysp.putBoolean(SYSharedPreferences.ISCHATFIRSTREADCONTACT, false);
		}
		
	}
	/**
	 * 注册广播
	 */
	private void regeditBroadcast() {
		br = new PhoneBroadCastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BroadcastUtil.ACTION_MOBILE_CONTACT);
		getActivity().registerReceiver(br, intentFilter);
	}

	private void unRegeditBroadcast() {
		if (br != null)
			getActivity().unregisterReceiver(br);
	}
}
