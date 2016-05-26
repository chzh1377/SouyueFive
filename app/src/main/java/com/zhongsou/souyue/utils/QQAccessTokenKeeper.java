/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.zhongsou.souyue.module.QQ_Oauth2AccessToken;

/**
 * 该类定义了微博授权时所需要的参数。
 */
public class QQAccessTokenKeeper {
	private static final String PREFERENCES_NAME = "com_qq_sdk_android";

	private static final String KEY_OPENID = "openid";
	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_EXPIRES_IN = "expires_in";

	/**
	 * 保存 Token 对象到 SharedPreferences。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * @param token
	 *            Token 对象
	 */
	public static void writeAccessToken(Context context, QQ_Oauth2AccessToken token) {
		if (null == context || null == token) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(KEY_OPENID, token.getOpenid());
		editor.putString(KEY_ACCESS_TOKEN, token.getAccess_token());
		editor.putLong(KEY_EXPIRES_IN, token.getExpires_in());
		editor.commit();
	}

	/**
	 * 从 SharedPreferences 读取 Token 信息。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 * 
	 * @return 返回 Token 对象
	 */
	public static QQ_Oauth2AccessToken readAccessToken(Context context) {
		if (null == context) {
			return null;
		}

		QQ_Oauth2AccessToken token = new QQ_Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setOpenid(pref.getString(KEY_OPENID, ""));
		token.setAccess_token(pref.getString(KEY_ACCESS_TOKEN, ""));
		token.setExpires_in(pref.getLong(KEY_EXPIRES_IN, 0));
		return token;
	}

	/**
	 * 清空 SharedPreferences 中 Token信息。
	 * 
	 * @param context
	 *            应用程序上下文环境
	 */
	public static void clear(Context context) {
		if (null == context) {
			return;
		}

		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
}
