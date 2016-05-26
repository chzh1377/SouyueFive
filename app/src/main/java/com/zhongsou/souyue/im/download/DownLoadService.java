package com.zhongsou.souyue.im.download;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import com.zhongsou.souyue.im.module.PackageBean;
import com.zhongsou.souyue.im.util.Constants;
import com.zhongsou.souyue.im.util.Slog;
import com.zhongsou.souyue.im.util.ZipUtil;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.SYUserManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 
 * 下载service
 * 
 * 
 * 
 * @author wangqiang
 * 
 * 
 */

public class DownLoadService extends Service {

	private DownloaderThread downloader;
	private static final int INIT = 0;// 定义三种下载的状态：0初始化状态，1正在下载状态，2停止状态
	private static final int DOWNLOADING = 1;
	private static final int STOP = 2;
	private int fileSize;
	public PackageDao dao = null;

	// 消息处理
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				PackageBean pb = (PackageBean) msg.obj;
				int completeSize = msg.arg1;
				Slog.d("callback", "completeSize:------------" + completeSize);
				BroadcastUtil.sendUpdateBroadCast(DownLoadService.this, pb,
						completeSize);
				if (isFinishDown(pb)) { // 下载完成
					unZipAndDb(pb);
				}
				break;
				
			case -1:
				Toast.makeText(DownLoadService.this, "下载失败，貌似出问题了", Toast.LENGTH_LONG).show();
				BroadcastUtil.sendFailBroadCast(DownLoadService.this);
			default:
				break;
			}

		}
	};

	// 是否下载完成

	public boolean isFinishDown(PackageBean packageBean) {
		LoadInfo info = MemoryPackageDao.getInfos(packageBean.getPackageId());
		if (info == null) {
			return false;
		}
		int totalsize = info.getFileSize();
		int completeSize = info.getComplete();
		int result = (completeSize) / totalsize;
		return result == 1;
	}

	// 下载完成，解压并插入数据库

	public void unZipAndDb(PackageBean pb) {
		Slog.d("callback", "------------下载完成了" + pb.getPackageName());
		MemoryPackageDao.stopThread(pb.getPackageId());
		boolean b = false;
		try {
                        String filePath = getFilesDir()+Constants.PACKAGE_DOWNURL_ZIP + "/"
                                + pb.getFileName();
			File fileName = new File(filePath);
			FileInputStream is = new FileInputStream(fileName); // zip 输入流
			String unPackPath = getFilesDir()+Constants.PACKAGE_DOWNURL // 解压到地址
					+ File.separator + SYUserManager.getInstance().getUserId();
			Slog.d("callback1", "unpackpath----------" + unPackPath);

			File destinationFile = new File(unPackPath);
			b = ZipUtil.extractZip(DownLoadService.this, is, destinationFile);
			if (b) {
				dao.save(pb);// 插入数据库
				BroadcastUtil.sendAddBroadCast(DownLoadService.this, pb);// 广播通知
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new PackageDao(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int command = super.onStartCommand(intent, flags, startId);
		if (intent == null)
			return command;
		String action = intent.getStringExtra("flag");//
		PackageBean packageBean = (PackageBean) intent
				.getSerializableExtra("packagebean");
		if ("startDown".equals(action)) { // 开始下载
			startDownload(packageBean);
		}
		if ("stop".equals(action)) { // 停止下载
			stopThread(packageBean);
		}
		return command;

	}

	private void startDownload(PackageBean pb) {
		String downPath = getDownloadPath(pb);
		String savePath = getSavePath(pb);
		downloader = new DownloaderThread(downPath, savePath, pb, handler, this);
		ListState.state.put(pb.getPackageId(), INIT);
		// 调用方法开始下载
		downloader.download();
	}

	// 停止下载
	private void stopThread(PackageBean packageBean) {
		ListState.state.put(packageBean.getPackageId(), ListState.STOP);
	}

	// 下载地址
	private String getDownloadPath(PackageBean pb) {
		String getURL = null;
		String token = SYUserManager.getInstance().getToken();
		String id = "";
		String vc = DeviceInfo.getAppVersion();
		String packageId = pb.getPackageId();
		try {
			getURL = UrlConfig.getExpressionDownload + "?token="
					+ URLEncoder.encode(token, "utf-8") + "&id="
					+ URLEncoder.encode(packageId, "utf-8") + "&vc="
					+ URLEncoder.encode(vc, "utf-8");
			return getURL;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * zip保存地址
	 * 
	 * @param pb
	 * @return
	 */

	private String getSavePath(PackageBean pb) {
		String dirPath = getFilesDir()+Constants.PACKAGE_DOWNURL_ZIP;
		File dirFile = new File(dirPath);
		if (!dirFile.exists())
			dirFile.mkdirs();
		return dirFile.getAbsolutePath() + File.separator + pb.getFileName();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void print(String msg) {
		Slog.d("DownloadService", msg);
	}

}
