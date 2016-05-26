package com.zhongsou.souyue.service.download;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.zhongsou.souyue.R;

public class DownloadAlert {
	private AlertDialog dialog;
	
    public DownloadAlert(Activity mContext, DialogInterface.OnClickListener listener) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    	builder.setIcon(android.R.drawable.ic_dialog_alert);
    	builder.setTitle(R.string.systemwarning);
    	builder.setMessage(R.string.ad_download_tips);
    	builder.setPositiveButton(R.string.alert_assent, listener);
    	builder.setNegativeButton(R.string.alert_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
    	dialog = builder.create();
    }

    public void show(){
    	dialog.show();
    }
}
