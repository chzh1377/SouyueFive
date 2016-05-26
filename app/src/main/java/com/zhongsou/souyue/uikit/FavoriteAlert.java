package com.zhongsou.souyue.uikit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.SYUserManager;

public class FavoriteAlert {
	private final Activity ctx;
	private AlertDialog dialog;
	private DialogInterface.OnClickListener nagativeListener;
	
    public FavoriteAlert(Activity activity, DialogInterface.OnClickListener nagativeListener) {
    	this.ctx = activity;
    	this.nagativeListener = nagativeListener;
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	builder.setIcon(android.R.drawable.ic_dialog_alert);
    	builder.setTitle(R.string.systemwarning);
    	builder.setMessage(String.format(CommonStringsApi.getStringResourceValue(R.string.favorite_tips),CommonStringsApi.APP_NAME_SHORT));
    	builder.setPositiveButton(R.string.loginActivity_login, 
    		new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(ctx, LoginActivity.class);
					intent.putExtra(LoginActivity.Only_Login, true);
					ctx.startActivityForResult(intent, 0);
					ctx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
					dialog.dismiss();
				}
				 
			});
    	
    	builder.setNegativeButton(R.string.dialog_continue, nagativeListener);
    	
    	dialog = builder.create();
    }

    public void show(){
    	User user = SYUserManager.getInstance().getUser();
    	if(user!=null && "1".equals(user.userType()) && !TextUtils.isEmpty(user.name())){//已登录
    		nagativeListener.onClick(dialog, 1);
    	}else{
    		dialog.show();
    	}
    }
}
