package com.upyun.api;

import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;
import java.util.Random;
/**
 * @author wanglong@zhongsou.com
 *
 */
public class UploadImageTask extends ZSAsyncTask<Void, Void, String> implements IUpYunConfig {
	private long uid;
	private File file;
	private Object callbackHandler;
	public UploadImageTask(Object callbackHandler, long uid, File file) {
		this.callbackHandler = callbackHandler;
		this.uid = uid;
		this.file = file;
	}

	/**
	 * 增加直接调用方法
	 * @param callbackHandler
	 * @param uid
	 * @param file
     */
	public static void executeTask(Object callbackHandler, long uid, File file){
		UploadImageTask t = new UploadImageTask(callbackHandler, uid, file);
		t.execute();
	}

	public String getSaveKey() {
		StringBuffer bucket = new StringBuffer(uid + "");
		while (bucket.length() < 8) {
			bucket.insert(0, '0');
		}
		return bucket.insert(4, '/').insert(0, "/user/").append(randomTo4()).append(".jpg").toString();
	}
	@Override
	protected String doInBackground(Void... params) {
		try {
			String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_IMAGE);
			String signature = UpYunUtils.signature(policy + "&" + API_IMAGE_KEY);
			return Uploader.upload(policy, signature, UPDATE_HOST + BUCKET_IMAGE, file);
		} catch (UpYunException e) {
		}
		return null;
	}
	@Override
	protected void onPostExecute(String url) {
		if (StringUtils.isNotEmpty(url)) {
			Uploader.invokeMethod(callbackHandler, IUpYunConfig.HOST_IMAGE + url + "?r=" + System.currentTimeMillis());
		}
	}
	
	private String randomTo4() {
		String s = "";
		int intCount = 0;
		intCount = (new Random()).nextInt(9999);//
		if (intCount < 1000)
			intCount += 1000;
		s = intCount + "";
		return s;
	}
}
