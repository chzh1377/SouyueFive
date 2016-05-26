package com.zhongsou.souyue.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.upyun.api.IUpYunConfig;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunException;
import com.upyun.api.utils.UpYunUtils;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.db.SelfCreateHelper;
import com.zhongsou.souyue.fragment.MySharesFragment;
import com.zhongsou.souyue.module.SelfCreateItem;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.SYUserManager;
import com.zhongsou.souyue.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelfCreateTask {
	private SelfCreateHelper sch = SelfCreateHelper.getInstance();
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyMM/ddhh");
	private static Random r = new Random();
	private String uid;

	public void setUid(String uid) {
		this.uid = uid;
	}

	private static ExecutorService SINGLE_TASK_EXECUTOR;

	static {
		SINGLE_TASK_EXECUTOR = (ExecutorService) Executors.newSingleThreadExecutor();
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent i = new Intent();
			i.putExtra("ismodify", true);
			i.setAction(ConstantsUtils.INTENTFILTER_ACTION_REFRESH_SELFCREATE_LV);
			MainApplication.getInstance().sendBroadcast(i);
		}
	};
	
	private Handler postHandler = new Handler(){
		public void handleMessage(Message msg) {
			
		};
	};

	private SelfCreateTask() {
	}

	private static SelfCreateTask single = null;

	public synchronized static SelfCreateTask getInstance() {
		if (single == null) {
			single = new SelfCreateTask();
		}
		return single;
	}

	/**
	 * 发送原创内容
	 * @param sci
	 */
	public void sendRequest(SelfCreateItem sci) {
		UploadTask utk = new UploadTask();
		utk.executeOnExecutor(SINGLE_TASK_EXECUTOR, sci);
	}
	
	/**
	 * 获取原创失败内容
	 * @param context
	 * @param str
	 */
	public void loadFailData(Context context, String str){
		SelfCreateLoadSendFailTask scst = new SelfCreateLoadSendFailTask(context);
		scst.executeOnExecutor(SINGLE_TASK_EXECUTOR, str);
	}
	
	/**
	 * 超级分享相关
	 * @author zhongs
	 */
	public void chaojifenxiang(MySharesFragment mySharesFragment, String param_1, String param_2, String param_3) {
		SelfCreateLoadSendFailTask scst = new SelfCreateLoadSendFailTask(mySharesFragment);
		scst.executeOnExecutor(SINGLE_TASK_EXECUTOR, param_1, param_2, param_3);
	}
	
	public void save2draftBox(Context context, SelfCreateItem sci){
		Save2BoxTask task = new Save2BoxTask(context);
		task.executeOnExecutor(SINGLE_TASK_EXECUTOR, sci);
	}
	
	class Save2BoxTask extends ZSAsyncTask<SelfCreateItem, Void, String> {
		private Object callback;
		public Save2BoxTask(Object callback){
			this.callback = callback;
		}
		
		@Override
		protected String doInBackground(SelfCreateItem... params) {
			SelfCreateItem sci = params[0];
			if (sci != null){
				return SelfCreateHelper.getInstance().addSelfCreateItem(sci);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {
				// selfCreateListToDBSuccess
				Method[] methods = callback.getClass().getMethods();
				for (Method m : methods) {
					if ("save2BoxSuccess".equals(m.getName())) {
						m.invoke(callback, result);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	class UploadTask extends ZSAsyncTask<SelfCreateItem, Void, HttpJsonResponse> {
		UploadToYun uty;
		private SelfCreateItem reqObj = null;
        HttpJsonResponse jobj;

		@Override
		protected void onPreExecute() {
			uty = new UploadToYun();
			super.onPreExecute();
		}

		@Override
		protected HttpJsonResponse doInBackground(SelfCreateItem... params) {
			reqObj = params[0];
			if (reqObj != null) {
				uploadPics(uty);// 如果有图片，先将图片上传up云，并拿到图片地址
				return send();// 一切准备就绪 发送原创到中搜服务器
			}
			return null;
		}

		@Override
		protected void onPostExecute(HttpJsonResponse result) {
            if(result!=null&&result.getCode()!=200){
				SouYueToast.makeText(MainApplication.getInstance(), result.getBodyString(), 0).show();
				SendUtils.sendFail(false);
			}else{
				handler.sendEmptyMessage(0);
				SendUtils.sendFail(true);
            }
			super.onPostExecute(result);
		}
		

		/**
		 * 上传图片到up云
		 * 
		 * @param uty
		 */
		private void uploadPics(UploadToYun uty) {
			Log.i("sendSelfCreate", "send create uploadPics ");
			for (int i = 0; i < reqObj.conpics().size(); i++) {
				String dir = reqObj.conpics().get(i);
				File f = new File(dir);
				if (null == f || !f.canRead()) {
					continue;
				}
				String url = null;
				if (!(dir.toLowerCase().contains("http:")))
					url = uty.upload(f);
				if (!StringUtils.isEmpty(url)) {
					url = IUpYunConfig.HOST_IMAGE + url + "!android";
					reqObj.conpics().set(i, url);
				} else
					break;
			}
		}

		/**
		 * 如果图片上传失败1张，就算全部失败
		 */
		private HttpJsonResponse send() {
			Log.i("sendSelfCreate", "send create send ");
			if (reqObj != null) {
				boolean b = true;
				for (String str : reqObj.conpics()) {
					if (!str.toLowerCase().contains("http:")) {
						b = false;
						break;
					}
				}
				if (b) {
                    return send2SyService();
				} else {
                    fail();
					return null;
				}
			}
			return null;
		}

		/**
		 * 发送到搜悦服务器
		 * 
		 * @return
		 */
		private HttpJsonResponse send2SyService() {

			StringBuffer conpic = new StringBuffer();
			if (reqObj.conpics().size() != 0 && reqObj.conpics() != null) {
				for (String str : reqObj.conpics()) {
					conpic.append(str + " ");
				}
			}

			if (TextUtils.isEmpty(reqObj.id())) {
				return add(conpic);
			}
			return null;

		}

		/**
		 * 上传原创内容
		 * 
		 * @param conpic
		 */
		private HttpJsonResponse add(StringBuffer conpic) {
			Log.i("sendSelfCreate", "send create add");
            HttpJsonResponse addSuccess = SelfCreateUploadHttp.addSelfCreate(SYUserManager.getInstance().getToken(), reqObj.keyword(), reqObj.srpId(), reqObj.md5(), reqObj.column_name(),
					String.valueOf(reqObj.column_type()), reqObj.title(), reqObj.content(), conpic.toString().trim());

			return addSuccess;
		}

		/**
		 * 发送失败
		 */
		private boolean fail() {
			Log.i("sendSelfCreate", "send create send fail");
			reqObj.status_$eq(ConstantsUtils.STATUS_SEND_FAIL);
			sch.updateSelfCreateItem(reqObj);
			return false;
		}
	}

	class SelfCreateLoadSendFailTask extends ZSAsyncTask<String, Void, List<SelfCreateItem>> {
		private Object callback;

		public SelfCreateLoadSendFailTask(Object callback) {
			this.callback = callback;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected List<SelfCreateItem> doInBackground(String... params) {
			if (params.length == 1) {
				return SelfCreateHelper.getInstance().getAllSelfCreateItem(params[0]);
			} else if (params.length == 3) {
				return SelfCreateHelper.getInstance().getAllSelfCreateItem(params[0], params[1], params[2]);
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<SelfCreateItem> result) {
			try {
				// selfCreateListToDBSuccess
				Method[] methods = callback.getClass().getMethods();
				for (Method m : methods) {
					if ("selfCreateListToDBSuccess".equals(m.getName())) {
						m.invoke(callback, result);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class UploadToYun implements IUpYunConfig {
		@Override
		public String getSaveKey() {
			return "/selfcreate" + formatDate.format(new Date()) + "/" + uid + "/" + uid + (r.nextInt(8999) + 1000) + ".jpg";
		}

		public String upload(File file) {
			try {
				String policy = UpYunUtils.makePolicy(getSaveKey(), Uploader.getExpiration(), BUCKET_IMAGE);
				String signature = UpYunUtils.signature(policy + "&" + API_IMAGE_KEY);
				return Uploader.upload(policy, signature, UPDATE_HOST + BUCKET_IMAGE, file);
			} catch (UpYunException e) {
			}
			return null;
		}
	}

	public void showToast(int resId) {
		SouYueToast.makeText(MainApplication.getInstance(), MainApplication.getInstance().getResources().getString(resId), 0).show();
	}
}
