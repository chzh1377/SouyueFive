package com.upyun.api;

import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
/**
 * @author wanglong@zhongsou.com
 *
 */
public class UploadVoiceTask extends ZSAsyncTask<Void, Void, String> implements IUpYunConfig {
	private static SimpleDateFormat formatDate = new SimpleDateFormat("/yy/MM/dd/hh");
	private static Random r = new Random();
	private String token;
	private File file;
	private Object callbackHandler;
	public UploadVoiceTask(Object callbackHandler, String token, File file) {
		this.callbackHandler = callbackHandler;
		this.token = token;
		this.file = file;
	}
	public String getSaveKey() {
		return "/comment" + formatDate.format(new Date()) + token + (r.nextInt(8999) + 1000) + ".amr";
	}
	@Override
	protected String doInBackground(Void... params) {
		try {
			String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_VOICE);
			String signature = UpYunUtils.signature(policy + "&" + API_VOICE_KEY);
			return Uploader.upload(policy, signature, UPDATE_HOST + BUCKET_VOICE, file);
		} catch (UpYunException e) {
		}
		return null;
	}
	@Override
	protected void onPostExecute(String url) {
		if (StringUtils.isNotEmpty(url)) {
			Uploader.invokeMethod(callbackHandler, "uploadSuccess",IUpYunConfig.HOST_VOICE + url);
		} else {
		    Uploader.invokeMethod(callbackHandler, "uploadFaild","--uploadFaild--");
		}
	}
}
