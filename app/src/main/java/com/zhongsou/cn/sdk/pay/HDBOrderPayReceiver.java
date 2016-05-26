package com.zhongsou.cn.sdk.pay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import org.json.JSONException;

public class HDBOrderPayReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.zhongsou.cn.pay")) {
			String info = intent.getStringExtra("info");
			try {
				org.json.JSONObject obj = new org.json.JSONObject(info);
				int code = obj.getInt("code");
				String msg = obj.getString("msg");
				Toast.makeText(context, msg, 0).show();
				/*switch (code) {
				case 9000:
					Toast.makeText(context, msg, 0).show();
					break;
				case 6001:
					Toast.makeText(context, msg, 0).show();
					break;
				case 6002:
					Toast.makeText(context,	msg, 0).show();
					break;
				default:
					break;
				}*/
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
