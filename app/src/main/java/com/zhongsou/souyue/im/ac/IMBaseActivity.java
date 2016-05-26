package com.zhongsou.souyue.im.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Toast;
import com.tuita.sdk.BroadcastUtil;
import com.zhongsou.souyue.activity.WebSrcViewActivity;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.im.dialog.ImProgressMsgDialog;
import com.zhongsou.souyue.im.util.ImUtils;

import java.util.List;

/**
 * on 2014/11/7
 * Description:IM BaseActivity IM新写的activity需继承此activity
 */
public class IMBaseActivity extends RightSwipeActivity {
    private ImProgressMsgDialog progressDialog;
    public boolean isShowError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcast();
    }

    private void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtil.ACTION_ERROR_TIP);
        filter.addAction(BroadcastUtil.ACTION_SUCCESS_DIALOG);
        registerReceiver(timeoutReceiver, filter);

    }

    private void unregisterBroadcast(){
        unregisterReceiver(timeoutReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }

    /**
     * 显示对话框
     */
    public void showProgress() {
        progressDialog = new ImProgressMsgDialog.Builder(this).create();
        if (!isFinishing() || !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    /**
     * 显示对话框 不可以返回
     */
    public void showProgressNotBack() {
        progressDialog = new ImProgressMsgDialog.Builder(this).create();
        progressDialog.setCancelable(false);// 设置点击屏幕Dialog不消失
        if (!isFinishing() || !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 显示对话框
     */
    public void showProgress(String message) {
        progressDialog = new ImProgressMsgDialog.Builder(this).setTextContent(message).create();
        progressDialog.setCancelable(false);// 设置点击屏幕Dialog不消失
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    /**
     * 取消等待框
     */
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BroadcastReceiver timeoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             dismissProgress();
            if(intent.getAction().equals(BroadcastUtil.ACTION_ERROR_TIP)) {
                {
                    if (intent.getStringExtra("data") != null) {
                        ImUtils.showImError(intent.getStringExtra("data"), IMBaseActivity.this);
                        isShowError = true;
                    }
                }
            }

        }
    };

    @Override
    public void startActivity(Intent intent) {

        if (intent.toString().contains("mailto")) {
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            if (activities == null || activities.size() == 0) {
                Toast.makeText(this, "无可用邮箱", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if(intent.toString().contains("http")) {
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            if (activities != null || activities.size() >= 0) {
                Intent urlIntent = new Intent(getApplicationContext(), WebSrcViewActivity.class);
                urlIntent.putExtra(WebSrcViewActivity.PAGE_TYPE, "nopara");
                urlIntent.putExtra(WebSrcViewActivity.PAGE_URL, intent.getDataString());
                startActivity(urlIntent);
                return;
            }
        }
        super.startActivity(intent);
    }

}
