package com.zhongsou.souyue.service.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.srp.AdClickRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyLoadResponse;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.LogDebugUtil;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.UpdateNewVersion;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadService extends Service implements IVolleyResponse {

    private static final int POOL_SIZE = 5;
    public static final int MAX_PROGRESS = 100;
    ExecutorService mDownThreadExe;
    private NotificationManager mNotificationManager;
    private static HashSet<String> dowmTasks = new HashSet<String>();
    private int taskNum = 0;
//    private Http http;
    private int page;

    public static boolean IS_DOWNLOAD_BACKGROUND = false;   //版本更新apk  是否在后台下载

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        checkThreadPool();
//        http = new Http(this);
    }

    private void checkThreadPool() {
        if (mDownThreadExe == null || mDownThreadExe.isShutdown()) {
            mDownThreadExe = Executors.newFixedThreadPool(POOL_SIZE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
//	    	ADInfo adInfo = (ADInfo) intent.getSerializableExtra("adinfo");
            String url = intent.getStringExtra("url");
            String title = intent.getStringExtra("title");
            String event = intent.getStringExtra("event");
            String keyword = intent.getStringExtra("keyword");
            String srpId = intent.getStringExtra("srpId");
            page = intent.getIntExtra("page", 3);
            if (url != null) {
                if (title == null) {
                    title = "下载中";
                }
                if (!dowmTasks.contains(url)) {
                    dowmTasks.add(url);
                    taskNum++;
                    if (MainApplication.ACTION_DOWNLOAD_BACKGROUND.equals(intent.getAction())) {  //入口为MainApplication

                        final CMainHttp cMainHttp = new CMainHttp(this);
                        final String finalUrl = url;

                        final IVolleyLoadResponse iVolleyLoadResponse = new IVolleyLoadResponse() { //下载进度监听接口
                            @Override
                            public void onHttpProcess(long _totle_length, long _cur_length) {
                                //计算百分比
                                long percent = _cur_length / _totle_length * 100L;
                                String strPercent = percent + "%";
                                if (percent >= 100L) {    //下载完成
                                    IS_DOWNLOAD_BACKGROUND = false;  //后台下载完成
//                                    ToastUtil.show(DownloadService.this, "Update apk Download completed");

                                } else {  //下载未完成
                                    //TODO 可以实时更新进度
                                }
                            }
                        };

                        IVolleyResponse iVolleyResponse = new IVolleyResponse() {   //下载状态监听接口
                            @Override
                            public void onHttpResponse(IRequest _request) {

                            }

                            @Override
                            public void onHttpError(IRequest _request) {
                                IS_DOWNLOAD_BACKGROUND = false;  //后台下载失败
                                cMainHttp.doDownload(0,
                                        UpdateNewVersion.getDownloadNewApkPath(DownloadService.this),
                                        finalUrl,
                                        iVolleyLoadResponse,
                                        this);
                                IS_DOWNLOAD_BACKGROUND = true;  //后台下载开始
                            }

                            @Override
                            public void onHttpStart(IRequest _request) {

                            }
                        };
                        IS_DOWNLOAD_BACKGROUND = false;  //后台下载失败
                        LogDebugUtil.d("DownloadService","before : " + IS_DOWNLOAD_BACKGROUND);
                        cMainHttp.doDownload(0,
                                UpdateNewVersion.getDownloadNewApkPath(this),
                                url,
                                iVolleyLoadResponse,
                                iVolleyResponse);
                        IS_DOWNLOAD_BACKGROUND = true;  //后台下载开始
                        LogDebugUtil.d("DownloadService","after  : " + IS_DOWNLOAD_BACKGROUND);
//                        ToastUtil.show(this, "Download apk - Y" + SettingActivity.IS_DOWNLOAD_BACKGROUND);
                    } else {
//    				startDownload(event, url, createProgressNotifyCation(getApplicationContext(), title),title);
                        startDownload(event, url, createProgressNotifyCation(getApplicationContext(), title), title, keyword, srpId);
                    }
                }
            } else {
                checkComplete();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void checkComplete() {
        if (taskNum == 0) {
            stopSelf();
        }
    }

    private Notification createStartNotifyCation(Context context, String title) {
        Notification builder = new Notification(android.R.drawable.stat_sys_download, title, System.currentTimeMillis());
        builder.tickerText = title;
        final Intent intent = new Intent();
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.contentIntent = contentIntent;
        builder.icon = android.R.drawable.stat_sys_download;
        builder.flags |= Notification.FLAG_AUTO_CANCEL;
        builder.setLatestEventInfo(context, title, title, contentIntent);
        return builder;
    }

    private Notification createProgressNotifyCation(Context context, String title) {
        Notification notification = new Notification();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mobile_download_notification);
        views.setProgressBar(R.id.down_progress_bar, MAX_PROGRESS, 0, false);
        views.setTextViewText(R.id.down_title, title);
        views.setImageViewResource(R.id.appIcon, android.R.drawable.stat_sys_download);
        final Intent intent = new Intent();
        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.contentIntent = contentIntent;
        notification.icon = android.R.drawable.stat_sys_download;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.contentView = views;
        return notification;
    }

    /**
     * 开始下载
     * @param event 事件统计url
     * @param url   下载的url
     * @param pgNtf
     */
    /*private void startDownload(String event, String url, Notification pgNtf,String title) {
        int id = GenerateNotifyId.generateNotifyId(url);
		if(StringUtils.isEmpty(title)){
		    mNotificationManager.notify(id, createStartNotifyCation(getApplicationContext(), "开始下载"));
		}else{
		    mNotificationManager.notify(id, createStartNotifyCation(getApplicationContext(), title));
		}
		DownloadInfo info = new DownloadInfo(url, id, pgNtf);
		info.serviceTask = dowmTasks;
		info.event = event;
		info.listener = new OnDownloadListener() {

			@Override
			public void onFinish(String event, String url,String keyword,String srpId) {
				taskNum--;
				checkComplete();
			}

			@Override
			public void onSuccess(String event, String url,String keyword,String srpId) {
				taskNum++;
				submitEvent(event, "download", url);
			}
		};
		DownloadRunnable run = new DownloadRunnable(getApplicationContext(), info);
		mDownThreadExe.execute(run);
	}*/
    private void startDownload(String event, String url, Notification pgNtf, String title, String keyword, String srpId) {
        int id = GenerateNotifyId.generateNotifyId(url);
        if (StringUtils.isEmpty(title)) {
            mNotificationManager.notify(id, createStartNotifyCation(getApplicationContext(), "开始下载"));
        } else {
            mNotificationManager.notify(id, createStartNotifyCation(getApplicationContext(), title));
        }
        DownloadInfo info = new DownloadInfo(url, id, pgNtf);
        info.serviceTask = dowmTasks;
        info.setEvent(event);
        info.setKeyword(keyword);
        info.setSrpId(srpId);
        info.listener = new OnDownloadListener() {

            @Override
            public void onFinish(String event, String url, String keyword, String srpId) {
                taskNum--;
                checkComplete();
            }

            @Override
            public void onSuccess(String event, String url, String keyword, String srpId) {
                taskNum++;
                submitEvent(event, url, keyword, srpId);
            }
        };
        DownloadRunnable run = new DownloadRunnable(getApplicationContext(), info);
        mDownThreadExe.execute(run);
    }

    protected void submitEvent(String event, String url, String keyword, String srpId) {//参数url 未使用！！
        if (event == null || event.length() == 0) {
            return;
        }

        //广告点击统计  需要验证
        AdClickRequest request = new AdClickRequest(HttpCommon.SRP_AD_CLICK_REQUEST,this);
        request.addParams(keyword, srpId, page, "", event);
        CMainHttp.getInstance().doRequest(request);
//		doPost(event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownThreadExe.shutdown();
        mNotificationManager.cancel(1);
    }

    @Override
    public void onHttpResponse(IRequest request) {

    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    public interface OnDownloadListener {
        public void onFinish(String event, String url, String keyword, String srpId);

        public void onSuccess(String event, String url, String keyword, String srpId);
    }

    public static boolean doPost(String url/*,Map<String, String> params*/) {
        try {
            HttpPost post = new HttpPost(url);
//			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//			String value = null;
//			for (Map.Entry<String, String> e : params.entrySet()) {
//				value = e.getValue();
//				if (value != null) {
//					pairs.add(new BasicNameValuePair(e.getKey(), value.toString()));
//				}
//			}
//			post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            HttpResponse resp = new DefaultHttpClient().execute(post);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(resp.getEntity());
                HttpJsonResponse json = new HttpJsonResponse((JsonObject) new JsonParser().parse(result));
                return json.getCode() == 200;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}