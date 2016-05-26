package com.andruby.cbug;

import android.content.Context;
import android.text.format.DateFormat;

import android.util.Log;

import com.google.gson.Gson;
import com.zhongsou.souyue.filemanager.FileUtils;
import com.zhongsou.souyue.filemanager.SaveFile;
import com.zhongsou.souyue.filemanager.SaveType;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;

public class CbugUpload {
	private final static String LOG_TAG = CbugUpload.class.getSimpleName();
	private Gson gson = new Gson();
	private SaveFile saveFile = new SaveFile(null, SaveType.SAVE_CRASH_FILE_TYPE);
	private static CbugUpload cbugUpload;
	private static final String URL_PHONE_ONLINE = "http://103.29.134.210/Cbug/Api/phone_info";
	private static final String URL_PHONE_OFFLINE = "http://192.168.46.160:9090/index.php/Cbug/Api/phone_info";

	private static final String URL_BUG_ONLINE = "http://103.29.134.210/Cbug/Api/bug_info";
	private static final String URL_BUG_OFFLINE = "http://192.168.46.160:9090/index.php/Cbug/Api/bug_info";

	private CbugUpload() {

	}

	public static synchronized CbugUpload getInstance() {
		if (cbugUpload == null) {
			cbugUpload = new CbugUpload();
		}
		return cbugUpload;
	}

	public void uploadPhoneInfo(final Context context) {
		final PhoneInfo phoneInfo = PhoneUtils.getPhoneInfo(context);
		if(phoneInfo.androidSdk==null){ //如果androidSdk == null 则 证明以前上传过 phoneinfo  以后就不必上传了
			return;
		}
		final ArrayList<NameValuePair> nameValueArr = new ArrayList<NameValuePair>();
		nameValueArr.add(new BasicNameValuePair("json_data", gson.toJson(phoneInfo)));
		System.out.println(LOG_TAG + gson.toJson(phoneInfo));
		new Thread() {
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(URL_PHONE_ONLINE);//线上
//					HttpPost httpPost = new HttpPost("");//线上
					httpPost.setEntity(new UrlEncodedFormEntity(nameValueArr, HTTP.UTF_8));
					HttpResponse response = client.execute(httpPost);
					int responseCode = response.getStatusLine().getStatusCode();
					String result = EntityUtils.toString(response.getEntity());
					if (responseCode == HttpStatus.SC_OK) {
//						callBack.onSuccess(result);
						Log.e(LOG_TAG, "cBug上传成功uploadPhoneInfo回调" + String.valueOf(responseCode) + "   result=" + result);
						try {
							if (Integer.parseInt(result) > 0) {
								PhoneUtils.setShare(context);
								Log.i(LOG_TAG, "uploadPhoneInfo本地记录");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
//						callBack.onTaskError(String.valueOf(responseCode));
						Log.e(LOG_TAG, "cBug上传失败uploadPhoneInfo回调" + String.valueOf(responseCode) + "   result=" + result);
//						saveFile.saveCrashLog(gson.toJson(phoneInfo));
					}
				} catch (Exception e) {
					e.printStackTrace();
//					saveFile.saveCrashLog(gson.toJson(phoneInfo));
					Log.e(LOG_TAG, "uploadPhoneInfo出现异常");
				}
			}

			;
		}.start();
	}

//	public static void uploadBug() {
////		String dataStr = DataProcess.millisToStringDate(System.currentTimeMillis(), "yyyy-MM-dd");
//		String fileList[]= FileUtils.getFileNameList(SaveType.SAVE_CRASH_FILE_TYPE);
//		for(int i=0;i<fileList.length;i++){//遍历文件
//			if(fileList[i]!=null && fileList[i].contains(".crash")){//符合条件则上传，条件是以 .crash结尾
//				try {
//					InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(FileUtils.crashDir,fileList[i])));
//
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		final ArrayList<NameValuePair> nameValueArr = new ArrayList<NameValuePair>();
//		nameValueArr.add(new BasicNameValuePair("json_data", gson.toJson(bugInfo)));
//		System.out.println("cBug开始执行post上传bug 信息");
//		new Thread() {
//			public void run() {
//				try {
//					HttpClient client = new DefaultHttpClient();
//					HttpPost httpPost = new HttpPost("http://103.29.134.210/Cbug/Api/bug_info");
////					HttpPost httpPost = new HttpPost("http://192.168.31.200/cbug/cbug.php");
//					httpPost.setEntity(new UrlEncodedFormEntity(nameValueArr, HTTP.UTF_8));
//					HttpResponse response = client.execute(httpPost);
//					int responseCode = response.getStatusLine().getStatusCode();
//					String result = EntityUtils.toString(response.getEntity());
//					if (responseCode == HttpStatus.SC_OK) {
////						callBack.onSuccess(result);
//						Log.e("cBug1","cBug上传成功uploadBug回调"+String.valueOf(responseCode)+"   result="+result);
//						Log.e("cBug1","成功上传的bugInfo是=" + gson.toJson(bugInfo));
//						//上传成功，删除
//
//					} else {
////						callBack.onTaskError(String.valueOf(responseCode));
//						Log.e("cBug1","cBug上传失败uploadBug回调"+String.valueOf(responseCode)+"   result="+result);
//						Log.e("cBug1","成功保存的bugInfo是=" + gson.toJson(bugInfo));
////						saveFile.saveCrashLog(gson.toJson(bugInfo));
//					}
//				}catch (Exception e){
//					Log.e("cBug1", "出现IOException异常  bugInfo=" + gson.toJson(bugInfo));
////					saveFile.saveCrashLog(gson.toJson(bugInfo));
//					e.printStackTrace();
//				}
//			};
//		}.start();
//	}


	public static void uploadBug() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String fileList[] = FileUtils.getFileNameList(SaveType.SAVE_CRASH_FILE_TYPE);
				if (fileList != null) {
					for (int i = 0; i < fileList.length; i++) {//遍历文件
						StringBuffer buffer = new StringBuffer();
						if (fileList[i] != null && fileList[i].contains(".crash")) {//符合条件则上传，条件是以 .crash结尾
							Log.i("fileList", FileUtils.crashDir + fileList[i]);
							File crashFile = new File(FileUtils.crashDir, fileList[i]);
							if (crashFile.length() == 0) {
								crashFile.delete();
								continue;
							}
							InputStreamReader reader = null;
							try {
								reader = new InputStreamReader(new FileInputStream(crashFile));
								int length = 0;
								char str[] = new char[215];
								while ((length = reader.read(str)) != -1) {
									buffer.append(str, 0, length);
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								try {
									reader.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							String jsonData = new String(buffer);
							if(jsonData.getBytes().length>10240){
								jsonData = new String(jsonData.getBytes(),0,10240);
							}
							ArrayList<NameValuePair> nameValueArr = new ArrayList<NameValuePair>();
							nameValueArr.add(new BasicNameValuePair("json_data", jsonData));
							System.out.println(LOG_TAG + new String(buffer));
							try {
								HttpClient client = new DefaultHttpClient();
								HttpPost httpPost = new HttpPost(URL_BUG_ONLINE);
//							HttpPost httpPost = new HttpPost("http://192.168.46.160/cbug/Cbug/Api/bug_info");
								httpPost.setEntity(new UrlEncodedFormEntity(nameValueArr, HTTP.UTF_8));
								HttpResponse response = client.execute(httpPost);
								int responseCode = response.getStatusLine().getStatusCode();
								String result = EntityUtils.toString(response.getEntity());
								if (responseCode == HttpStatus.SC_OK) {
//						callBack.onSuccess(result);
									Log.i(LOG_TAG, "cBug上传成功uploadBug回调" + String.valueOf(responseCode) + "   result=" + result);
//								Log.i("LOG_TAG", "成功上传的bugInfo是=" + new String(buffer));
//								上传成功，删除
									if (crashFile.delete()) {
										Log.i("Delete", "删除成功");
									} else {
										Log.i("Delete", "删除失败");
									}
//								try {
//									Thread.sleep(10000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
								} else {
//						callBack.onTaskError(String.valueOf(responseCode));
									Log.e(LOG_TAG, "cBug上传失败uploadBug回调" + String.valueOf(responseCode) + "   result=" + result);
//								Log.e("LOG_TAG", "成功保存的bugInfo是=" + new String(buffer));
								}
							} catch (ClientProtocolException e) {
								e.printStackTrace();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}
				}
			}
		}).start();
	}

	public void saveBugInfo(BugInfo bugInfo) {
		Log.i(LOG_TAG, "执行保存bug方法");
		saveFile.saveCrashLog(gson.toJson(bugInfo));
	}


}
