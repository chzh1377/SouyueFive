package com.zhongsou.souyue.receiver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.io.File;

public class CompleteReceiver extends BroadcastReceiver {

	private DownloadManager downloadManager;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			Toast.makeText(context, "下载成功", Toast.LENGTH_LONG).show();
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			// 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
			if (id == SYSharedPreferences.getInstance().getLong(SYSharedPreferences.UPDATE_ID, -1)) {
				Query query = new Query();
				query.setFilterById(id);
				downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
				Cursor cursor = downloadManager.query(query);
				int columnCount = cursor.getColumnCount();
				String path = null; // TODO
				// 这里把所有的列都打印一下，有什么需求，就怎么处理,文件的本地路径就是path
				while (cursor.moveToNext()) {
					for (int j = 0; j < columnCount; j++) {
						String columnName = cursor.getColumnName(j);
						String string = cursor.getString(j);
						if (columnName.equals("local_filename")) {
							path = string;
							break;
						}
						if (string != null) {
							System.out.println("---->" + columnName + ": " + string);
						} else {
							System.out.println("---->" + columnName + ": null");
						}
					}
				}
//				SYSharedPreferences.getInstance().putString(SYSharedPreferences.UPDATE_FILE, path);
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(install);
				
//				System.out.println("----------->" + path);

				cursor.close();
				// 如果sdcard不可用时下载下来的文件，那么这里将是一个内容提供者的路径，这里打印出来，有什么需求就怎么样处理
				// if (path.startsWith("content:")) {
				// cursor = context.getContentResolver().query(Uri.parse(path),
				// null, null, null, null);
				// columnCount = cursor.getColumnCount();
				// while (cursor.moveToNext()) {
				// for (int j = 0; j < columnCount; j++) {
				// String columnName = cursor.getColumnName(j);
				// String string = cursor.getString(j);
				// if (string != null) {
				// System.out.println(columnName + ": " + string);
				// } else {
				// System.out.println(columnName + ": null");
				// }
				// }
				// }
				// cursor.close();
				// }

			}
		} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
		}
	}
}
