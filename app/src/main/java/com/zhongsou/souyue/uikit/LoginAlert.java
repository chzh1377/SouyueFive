package com.zhongsou.souyue.uikit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.text.TextUtils;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.ConstantsUtils;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;

public class LoginAlert {
	private final Activity ctx;
	private AlertDialog dialog;
	private int msg=R.string.login_tips;//默认提示
	private DialogInterface.OnClickListener nagativeListener;
	private int flg = 0; // 0精华区提示，1搜悦网友提示
	//add by trade
	private String mMessage="";  //字符型提示信息
	//add by trade
	public LoginAlert(Activity activity,DialogInterface.OnClickListener nagativeListener, int msg, int flg,String showPager,String action) {
        this.ctx = activity;
        this.nagativeListener = nagativeListener;
        this.msg = msg;
        this.flg = flg;
        create(nagativeListener,showPager,action);
    }

	public LoginAlert(Activity activity,DialogInterface.OnClickListener nagativeListener, int msg, int flg) {
		this.ctx = activity;
		this.nagativeListener = nagativeListener;
		this.msg = msg;
		this.flg = flg;
		create(nagativeListener);
	}
	public LoginAlert(Activity activity,DialogInterface.OnClickListener nagativeListener, String msg, int flg) {
		this.ctx = activity;
		this.nagativeListener = nagativeListener;
		this.mMessage = msg;
		this.flg = flg;
		create(nagativeListener);
	}
    public LoginAlert(Activity activity, DialogInterface.OnClickListener nagativeListener) {
    	this.ctx = activity;
    	this.nagativeListener = nagativeListener;
    	create(nagativeListener);
    }
	private void create(DialogInterface.OnClickListener nagativeListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	builder.setIcon(android.R.drawable.ic_dialog_alert);
    	builder.setTitle(R.string.systemwarning);
    	if(mMessage.equals(""))
    	{
    		if (ConfigApi.isSouyue()) {
				builder.setMessage(msg);
			} else {
				builder.setMessage(String.format(CommonStringsApi
						.getStringResourceValue(R.string.trade_login_tips),
						CommonStringsApi.APP_NAME_SHORT));
			}
    	}
    	else {
			builder.setMessage(mMessage);
		}
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
    	if(flg == 0)
    		builder.setNegativeButton(R.string.dialog_continue, nagativeListener);
    	else  
    		builder.setNegativeButton(R.string.dialog_cancel, null);
    	dialog = builder.create();
	}
    
    public void setOnDismissListener(OnDismissListener listener){
    	dialog.setOnDismissListener(listener);
    }

    public void show(){
    	User user = SYUserManager.getInstance().getUser();
    	if(user!=null && "1".equals(user.userType()) && !TextUtils.isEmpty(user.name())){//已登录
    		if (nagativeListener != null)
    			nagativeListener.onClick(dialog, 1);
    	}else{
    		dialog.show();
    	}
    }
    
    // add by trade
    private void create(DialogInterface.OnClickListener nagativeListener,final String showPager,final String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.systemwarning);
        builder.setMessage(String.format(MainApplication.getInstance().getResources().getString(msg), ConstantsUtils.APP_NAME));
        builder.setPositiveButton(R.string.loginActivity_login, 
            new DialogInterface.OnClickListener() {
            
                @Override
                public void onClick(DialogInterface dialog, int which) {
                     IntentUtil.gotoLoginWithAction(ctx,showPager,action);
//                  Intent intent = new Intent();
//                  intent.setClass(ctx, LoginActivity.class);
//                  intent.putExtra(LoginActivity.Only_Login, true);
//                  ctx.startActivityForResult(intent, 0);
//                  ctx.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    dialog.dismiss();
                }
                
            });
        if(flg == 0)
            builder.setNegativeButton(R.string.dialog_continue, nagativeListener);
        else  
            builder.setNegativeButton(R.string.dialog_cancel, null);
        dialog = builder.create();
    }
}
