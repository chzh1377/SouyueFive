package com.zhongsou.souyue.service.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.UpdateNewVersion;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadRunnable implements Runnable {

    protected static final int START = 1;
    private String downUrl;
    private NotificationManager mNotificationManager;
    private int id;
    private Notification notification;
    private DownloadInfo info;
    private Context context;
    private File cacheFile;
    private Handler handler;
    private int last = -1;
    private int curPercent = 0;
    private String titleText = "准备下载";
    private int fileLength;
    private long haveDown;

    public DownloadRunnable(Context context, DownloadInfo info) {
        this.mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        this.info = info;
        this.notification = info.notification;
        this.id = info.notifyId;
        this.context = context;
        downUrl = info.url;
        handler = new Handler() {

            @Override
            public void dispatchMessage(Message msg) {
                switch (msg.what) {
                    case START:
                        updateProgress(curPercent);
                        handler.sendEmptyMessageDelayed(START, 500);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void run() {
        InputStream in = null;
        FileOutputStream out = null;
        updateProgress(0);
        try {
            /*URL localURL = new URL(this.downUrl);
			HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURL
					.openConnection();
			localHttpURLConnection.setRequestMethod("GET");
			localHttpURLConnection.setConnectTimeout(10000);
			localHttpURLConnection.setReadTimeout(30000);
			localHttpURLConnection.connect();
			in = localHttpURLConnection.getInputStream();
			String path = localHttpURLConnection.getURL().getPath();
			titleText = path.substring(path.lastIndexOf('/')+1);
			out = new FileOutputStream(generateFilePath());
			
			fileLength = localHttpURLConnection.getContentLength();*/

            //httpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(this.downUrl);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                in = entity.getContent();
                fileLength = (int) entity.getContentLength();
                String apkName = this.downUrl.substring(this.downUrl.lastIndexOf('/') + 1);
                titleText = apkName;
                out = new FileOutputStream(UpdateNewVersion.getDownloadNewApkPath(context) + File.separator + apkName);


                byte[] buffer = new byte[1024];
                int len = 0;
                haveDown = 0;
                handler.sendEmptyMessage(START);
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                    haveDown += len;
                    curPercent = (int) ((haveDown * 100) / fileLength);
                }
                handler.removeMessages(START);
                updateProgress(100);
                if (info.listener != null) {
                    info.listener.onSuccess(info.getEvent(), info.url, info.getKeyword(), info.getSrpId());
                }
                mNotificationManager.notify(id, createSuccessNotifyCation(context));
            } else {
                mNotificationManager.notify(id, createFailedNotifyCation(context));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mNotificationManager.notify(id, createFailedNotifyCation(context));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            info.serviceTask.remove(downUrl);
            if (info.listener != null) {
                info.listener.onFinish(info.getEvent(), info.url, info.getKeyword(), info.getSrpId());
            }
        }

    }


    private void startInstall(Context context2) {
        Intent intent = new Intent();
        setIntentInstall(intent);
        context2.startActivity(intent);
    }

    /**
     * 返回文件存储路径
     *
     * @return
     * @throws IOException
     */
    private File generateFilePath() throws IOException {
        File dir = FileUtil.getCacheDirectory(context);
        String fileName = Md5Util.getMD5Str(downUrl.substring(downUrl.lastIndexOf("/") + 1));
        File file = new File(dir, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        this.cacheFile = file;
        return file;
    }

    private void updateProgress(int i) {
        if (i == last) {
            return;
        }
        RemoteViews views = notification.contentView;
        views.setTextViewText(R.id.down_progress_text, i + "%");
        views.setProgressBar(R.id.down_progress_bar, 100, i, false);
        views.setTextViewText(R.id.down_title, titleText);
        views.setTextViewText(R.id.down_description, "(" + getDataFormat((int) haveDown) + "/" + getDataFormat(fileLength) + "M)");

        mNotificationManager.notify(id, notification);
        last = i;
    }

    private String getDataFormat(int data) {
        double m = data / (1024 * 1024d);

        return (int) (m * 100) / 100f + "";
    }

    private Notification createSuccessNotifyCation(Context context) {
        Notification notification = new Notification(android.R.drawable.stat_sys_download, "下载完成", System.currentTimeMillis());
        final Intent intent = new Intent();
        setIntentInstall(intent);   //此方法是下载完成自动安装的问题 但会出现空指针  导致走到catch分支，显示下载失败通知栏  已经在方法内处理
        final PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, intent, 0);
        notification.setLatestEventInfo(context, "下载完成",
                "下载成功", contentIntent);
        notification.icon = android.R.drawable.stat_sys_download;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    private void setIntentInstall(Intent intent) {
        try{
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String type = "application/vnd.android.package-archive";
            intent.setDataAndType(Uri.fromFile(cacheFile), type);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private Notification createFailedNotifyCation(Context context) {
        Notification notification = new Notification(android.R.drawable.stat_sys_download, "下载失败", System.currentTimeMillis());
        notification.tickerText = "下载失败";
        final Intent intent = new Intent();
        final PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, intent, 0);
        notification.setLatestEventInfo(context, "下载失败",
                "下载失败", contentIntent);

        notification.icon = android.R.drawable.stat_sys_download;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return notification;
    }


}
