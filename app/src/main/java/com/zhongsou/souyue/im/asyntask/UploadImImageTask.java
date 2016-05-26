package com.zhongsou.souyue.im.asyntask;
import android.text.TextUtils;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.im.render.MessageManager;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.service.ZSAsyncTask;
import com.zhongsou.souyue.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Random;

/**
 * @author wanglong@zhongsou.com
 *
 */
public class UploadImImageTask extends ZSAsyncTask<Void, Void, String> implements IUpYunConfig {
	private long uid;
	private File file;
	private Object callbackHandler;
	private ChatMsgEntity m;
	private MessageManager messageManager;
	private String localPath;
	private boolean isVertical;
	private float mWidth;
	private float mHeight;
	public UploadImImageTask(Object callbackHandler, long uid, File file, ChatMsgEntity m, MessageManager messageManager, String localPath, boolean isVertical,float mWidth,float mHeight) {
		this.callbackHandler = callbackHandler;
		this.m = m;
		this.uid = uid;
		this.file = file;
		this.messageManager = messageManager;
		this.localPath = localPath;
		this.isVertical = isVertical;
		this.mWidth = mWidth;
		this.mHeight = mHeight;
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
			m.setType(15);
			String imageUrl = IUpYunConfig.HOST_IMAGE + url + "!android";
			m.setText(getJson(imageUrl, localPath,isVertical,mWidth,mHeight));
			m.setUrl(imageUrl);
			messageManager.sendImage(m);
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
	
	public String getJson(String url, String localPath,boolean isVertical,float mWidth,float mHeight) {
		JSONObject j = new JSONObject();
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		try {
			j.put("url", url);
			j.put("localPath", localPath);
			j.put("isVertical", isVertical);
			j.put("image-width", mWidth);
			j.put("image-height", mHeight);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return j.toString();
	}
}
