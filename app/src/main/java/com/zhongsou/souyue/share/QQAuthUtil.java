package com.zhongsou.souyue.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhongsou.souyue.module.QQUserInfo;
import com.zhongsou.souyue.module.QQ_Oauth2AccessToken;
import com.zhongsou.souyue.platform.ShareApi;
import com.zhongsou.souyue.utils.QQAccessTokenKeeper;
import org.json.JSONObject;

public class QQAuthUtil {
	public void handleLoginData(Intent data) {
		Tencent.handleResultData(data,listener);
	}

	public interface QQAuthListener {
		public void onCallback(QQUserInfo info);
	}

	private Activity mContext;
	private QQAuthListener qlistener;
	private Tencent mTencent;

	public QQAuthUtil(Activity mContext, QQAuthListener qlistener) {
		this.mContext = mContext;
		this.qlistener = qlistener;
		mTencent = Tencent.createInstance(ShareApi.QQ_APP_ID, mContext.getApplicationContext());
	}

	IUiListener listener = new IUiListener() {
		@Override
		public void onComplete(Object values) {

			if (values != null) {
				JSONObject arg0 = (JSONObject) values;
				try {
					String accessToken = arg0.getString("access_token");
					String openid = arg0.getString("openid");
					String expires_in = arg0.getString("expires_in");
					long lExpires_in = System.currentTimeMillis() + Long.parseLong(expires_in) * 1000;

					QQ_Oauth2AccessToken token = new QQ_Oauth2AccessToken();
					token.setAccess_token(accessToken);
					token.setOpenid(openid);
					token.setExpires_in(lExpires_in);
					//升级新版qqsdk需要用到的
                    mTencent.setAccessToken(accessToken, expires_in);
                    mTencent.setOpenId(openid);
                    QQAccessTokenKeeper.writeAccessToken(mContext, token);
					ReadQQUserInfo(mContext, qlistener, openid);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(mContext, "授权失败", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(mContext, "授权失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onCancel() {

		}

		@Override
		public void onError(UiError arg0) {

		}

	};

	public void doAuthQQ() {
		try {
			Log.i("", "mTencent openId :" + mTencent.getOpenId());
			mTencent.login(mContext, "get_simple_userinfo", listener);
		} catch (Exception e) {
			if (qlistener != null)
				qlistener.onCallback(null);
		}
	}
	
	public void logout(){
		try {
			mTencent.logout(mContext.getApplicationContext());
			QQAccessTokenKeeper.clear(mContext);
		} catch (Exception e){
			
		}
	}

	private void ReadQQUserInfo(final Context context, final QQAuthListener callback, final String openid) {
		//升级新版qqsdk需要用到的
		UserInfo mInfo = new UserInfo(context, mTencent.getQQToken());
		mInfo.getUserInfo(new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				if (callback != null)
					callback.onCallback(null);
			}

			@Override
			public void onComplete(Object arg0) {
				try {
					QQUserInfo info = new Gson().fromJson(arg0.toString(), QQUserInfo.class);
					Log.i("", "mTencent openId :" + openid);
					info.setId(openid);
					if (callback != null)
						callback.onCallback(info);
				} catch (Exception e) {
					e.printStackTrace();
					if (callback != null)
						callback.onCallback(null);
				}
			}

			@Override
			public void onCancel() {

			}
		});

	}
}
