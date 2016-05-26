package com.zhongsou.souyue.im.asyntask;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import com.tuita.sdk.im.db.module.Contact;
import com.zhongsou.souyue.service.ZSAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class MyPhoneContactsTask extends ZSAsyncTask<Void, Void, List<Contact>> {

	private static final String TAG = "MyPhoneContactsTask";
	
	private Context context;
	private Handler hanlder;
	private List<Contact> phoneContantList = new ArrayList<Contact>();
	
	public MyPhoneContactsTask(Context context,Handler handler) {
		this.context = context;
		this.hanlder = handler;
	}
	
	@Override
	protected List<Contact> doInBackground(Void... paramArrayOfParams) {
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(Phone.CONTENT_URI, new String[] { "display_name", "data1", "sort_key" }, null, null, "sort_key");
			cur.moveToFirst();
			while (cur.getCount() > cur.getPosition()) {

				String number = cur.getString(cur.getColumnIndex(Phone.NUMBER));
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
					number = number.substring(number.length() - 11, number.length());
				}

				String name = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
				Contact person = new Contact();
				person.setNick_name(name);
				person.setPhone(number);
				person.setLocal_order(cur.getString(cur.getColumnIndex(Phone.SORT_KEY_PRIMARY)));
				phoneContantList.add(person);
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

		return phoneContantList;
	}

	@Override
	protected void onPostExecute(List<Contact> result) {
		super.onPostExecute(result);
		Message msg = new Message();
		msg.obj = result;
		hanlder.sendMessage(msg);
	}


}
