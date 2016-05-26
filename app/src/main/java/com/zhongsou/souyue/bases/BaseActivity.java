package com.zhongsou.souyue.bases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.SettingActivity;
import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.share.ShareByTencentQQ;
import com.zhongsou.souyue.share.ShareByTencentQQZone;
import com.zhongsou.souyue.ui.ProgressBarHelper;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.SmartBarUtils;
import com.zhongsou.souyue.utils.ZhongSouActivityMgr;

//import com.networkbench.agent.impl.NBSAppAgent;

public class BaseActivity extends FragmentActivity implements IVolleyResponse {
    protected Activity mContext;
    private static Activity mAct;
    protected static final String TAG = "souyue3.5";
    protected ProgressBarHelper pbHelp;
    protected SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    protected CMainHttp mMainHttp;

    public static Activity getCurrentActivity(){
        return mAct;
    }
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mMainHttp = CMainHttp.getInstance();
        if (meiZuHideSB() && SmartBarUtils.hasSmartBar()) {
            SmartBarUtils.hide(this, getWindow());
        }
        ZhongSouActivityMgr.getInstance().add(this);
        mContext = this;
        mAct = this;
    }

    protected boolean meiZuHideSB() {
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ZhongSouActivityMgr.getInstance().add(this);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
//	    ThreadPoolUtil.getInstance().execute(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				if (!ImserviceHelp.getInstance().appOnline()){
//					//上线
//					ImserviceHelp.getInstance().im_connect(DeviceInfo.getAppVersion());
//				}
//			}
//		});
        super.onResume();
        MobclickAgent.onResume(this);
        ZSSdkUtil.onPageStart(this, this.getClass().getSimpleName(), null);
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        ZSSdkUtil.onPageEnd(this, this.getClass().getSimpleName(), null);
        super.onPause();
    }

    @Override
    protected void onStop() {
        ZhongSouActivityMgr.getInstance().remove(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMainHttp.cancel(this);
        super.onDestroy();
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.systemwarning).setMessage("确定退出？")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                ZhongSouActivityMgr.getInstance().exit();
                                // System.exit(0);
                                // android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                return true;
            case R.id.setting:
                this.startActivity(new Intent(this, SettingActivity.class));
                this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    public void onBackPressClick(View view) {
        onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onHttpStart(IRequest request) {

    }

    @Override
    public void onHttpResponse(IRequest request) {

    }

    @Override
    public void onHttpError(IRequest request) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //qq分享,SharedByTencentQq
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE) {
            if (resultCode == com.tencent.connect.common.Constants.ACTIVITY_OK) {
                ShareByTencentQQ.getInstance().handleResultData(data);
            }
        }
        //qzone 分享
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE) {
            if (resultCode == com.tencent.connect.common.Constants.ACTIVITY_OK) {
                ShareByTencentQQZone.getInstance().handleResultData(data);
            }
        }
    }
}
