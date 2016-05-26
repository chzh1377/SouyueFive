package com.zhongsou.souyue.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongsou.souyue.DontObfuscateInterface;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.SouyueUpdateAdapter;
import com.zhongsou.souyue.module.UpdateBean;
import com.zhongsou.souyue.service.download.DownloadService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 新版本更新类
 *
 * @author zcz YanBin
 * @Copyright (c) 2015 zhongsou
 * @Description 升级流程
 */
public class UpdateNewVersion implements DontObfuscateInterface {

    public static final String TAG = "UpdateNewVersion";
    public Context context;
    public boolean isMust;
    public UpdateBean updateBean;
    private AlertDialog dialog;
    private String updateApkName;
    private String updateNewPath;

    public UpdateNewVersion(Context context, boolean isMust, UpdateBean updateBean) {
        this.context = context;
        this.isMust = isMust;
        this.updateBean = updateBean;
        this.updateApkName = getNewApkName(updateBean.getUrl());
        this.updateNewPath = getDownloadNewApkPath(MainApplication.getInstance());
    }

    private ArrayList<String> getDetails() {
        ArrayList<String> array = new ArrayList<String>();
        for (int i = 0; i < updateBean.getDesc().length; i++) {
            for (int j = 0; j < updateBean.getDesc()[i].getChanges().length; j++) {
                array.add(updateBean.getDesc()[i].getChanges()[j]);
            }
        }
        return array;
    }

    @SuppressLint("NewApi")
    public void update() {
        if (ZhongSouActivityMgr.getInstance().acys != null && !ZhongSouActivityMgr.getInstance().acys.isEmpty()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ZhongSouActivityMgr.getInstance().acys.getLast());
            final View view = LayoutInflater.from(ZhongSouActivityMgr.getInstance().acys.getLast()).inflate(R.layout.update_dialog, null);
            if (dialog == null) dialog = builder.create();
            // View处理代码
            final RelativeLayout button_contain = (RelativeLayout) view.findViewById(R.id.button_contain);
            TextView title = (TextView) view.findViewById(R.id.title);
            ListView detial = (ListView) view.findViewById(R.id.detial);

            detial.setAdapter(new SouyueUpdateAdapter(context, getDetails()));

            Button ok = (Button) view.findViewById(R.id.ok);
            Button cancel = (Button) view.findViewById(R.id.cancel);
            if (!isMust) {
                cancel.setText("下次再说");
            }
            final TextView updating = (TextView) view.findViewById(R.id.updating);
            title.setText(updateBean.getVersion() + "版本更新");
            ok.setOnClickListener(new OnClickListener() {// 确定下载按钮点击事件
                @Override
                public void onClick(View v) {


                    if (isHaveLocalApk()) {

                        File apkFile = new File(updateNewPath + File.separator + updateApkName);
                        // 通过Intent安装APK文件
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
                        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                                || state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                || !Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                                ) {
                            downloadBySouyue(); //使用搜悦自定义的下载
                        } else {
                            downloadBySystem(); //使用系统DownloadManager下载
                        }
                    }


                    // 修改显示更新中 后台下载前台显示更新中 .......................
                    button_contain.setVisibility(View.GONE);
                    updating.setVisibility(View.VISIBLE);
                    // 重新显示dialog
                    if (!dialog.isShowing()) dialog.show();
                    if (isMust) {
                        dialog.setCancelable(false);
                    } else {
                        dialog.setCancelable(true);
                    }
                    dialog.getWindow().setLayout(DeviceUtil.dip2px(context, 250), DeviceUtil.dip2px(context, 300));//第一个参数:宽；第二个参数：高
                    dialog.getWindow().setContentView(view);
                    if (isMust) { // 如果是强制更新，点击取消则退出应用
                        ZhongSouActivityMgr.getInstance().exit();
                    } else {// 如果不是强制更新，点击取消则对话框消失今日不再弹出 (设置share标识，存今日日期)
                        dialog.dismiss();
                    }
                }
            });
            cancel.setOnClickListener(new OnClickListener() {// 取消下载 （不更新）点击事件
                @Override
                public void onClick(View v) {
                    if (isMust) { // 如果是强制更新，点击取消则退出应用
                        ZhongSouActivityMgr.getInstance().exit();
                    } else {// 如果不是强制更新，点击取消则对话框消失今日不再弹出 (设置share标识，存今日日期)
                        dialog.dismiss();

                    }
                }
            });
            // 显示dialog
            if (!dialog.isShowing()) dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(DeviceUtil.dip2px(context, 250), DeviceUtil.dip2px(context, 300));//第一个参数:宽；第二个参数：高
            dialog.getWindow().setContentView(view);
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.UPDATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
    }

    /**
     * 调用搜悦中DownloadService进行下载
     */
    private void downloadBySouyue() {
        Intent intent = new Intent();
        intent.setClass(MainApplication.getInstance(), DownloadService.class);
        intent.putExtra("url", updateBean.getUrl());
        intent.putExtra("title", MainApplication.getInstance().getString(R.string.app_name));
        MainApplication.getInstance().startService(intent);
    }

    /**
     * 调用系统DownloadManager下载
     */
    private void downloadBySystem() {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(updateBean.getUrl());
        Request request = null;
        try {
            request = new Request(uri);
        } catch (Exception e) {
            LogDebugUtil.e(TAG, "update uri error!");
        }

        PackageManager manager = MainApplication.getInstance().getPackageManager();
        try {
            LogDebugUtil.d(TAG, manager.getApplicationInfo(MainApplication.getInstance().getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES).sourceDir);
        } catch (Exception e) {
            LogDebugUtil.d("UpdateNewVersion-Yan", "UpdateNewVersion Exception");
        }
//                        request.setDestinationUri()
        LogDebugUtil.d(TAG, MainApplication.getInstance().getExternalCacheDir().getAbsolutePath());    //test
//        updateNewPath = MainApplication.getInstance().getExternalCacheDir().getAbsolutePath();

        if (request != null) {
            request.setDestinationInExternalPublicDir("Download", updateApkName);//设置下载路径

//                        MainApplication.getInstance().getExternalCacheDir();
//                        request.allowScanningByMediaScanner();


            request.setTitle(MainApplication.getInstance().getString(R.string.app_name));
            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

            // 禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
            // 显示下载界面
            request.setVisibleInDownloadsUi(true);
                                  /*
                         * 设置下载后文件存放的位置,如果sdcard不可用，那么设置这个将报错，因此最好不设置如果sdcard可用，下载后的文件 在
                         * /mnt/sdcard/Android/data/packageName/files目录下面，如果sdcard不可用 ,
                         * 设置了下面这个将报错，不设置，下载后的文件在/cache这个 目录下面
                         */
            // request.setDestinationInExternalFilesDir(this, null,
            // "tar.apk");

            // 把id保存好，在接收者里面要用，最好保存在Preferences里面
            long id = downloadManager.enqueue(request);
            SYSharedPreferences.getInstance().putLong(SYSharedPreferences.UPDATE_ID, id);
        } else {  //request 为空
            ToastUtil.show(context, context.getResources().getString(R.string.update_failure));
        }
    }

    /**
     * 获得升级包存放路径
     *
     * @param context 上下文
     * @return 路径
     */
    public static String getDownloadNewApkPath(Context context) {
        String cachePath;

        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())    //外部存储是否挂载
                || !Environment.isExternalStorageRemovable())   //外部存储是否移除
                ) {
            cachePath = Environment.getExternalStoragePublicDirectory("Download").getPath();

        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 本地是否存在升级包
     *
     * @return 是否存在升级包
     */
    public boolean isHaveLocalApk() {
        boolean ret = false;
        File file = new File(updateNewPath + File.separator + updateApkName);
        if (file.exists()) {
//            updateBean.getVersion();    //new Version from server
//            com.zhongsou.souyue.net.DeviceInfo.getAppVersion();    //current app version

            PackageManager manager = MainApplication.getInstance().getPackageManager();
            LogDebugUtil.d(this.getClass().getSimpleName(), "updateDir  :  " + updateNewPath + File.separator + updateApkName);

            PackageInfo info = manager.getPackageArchiveInfo(updateNewPath + File.separator + updateApkName, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                String appName = manager.getApplicationLabel(appInfo).toString();
                String packageName = appInfo.packageName;  //得到安装包名称
                String version = info.versionName;       //得到版本信息
                LogDebugUtil.d(this.getClass().getSimpleName(), "appName:" + appName + "   packageName  " + packageName + "  version" + version);
                if (packageName.equals(MainApplication.getInstance().getPackageName())  //包名相同
                        && version.equals(updateBean.getVersion())) {       //是最新版本
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * 获得新版本apk的name
     *
     * @param url 下载地址
     * @return apk的name 例如：souyue.apk
     */
    private String getNewApkName(String url) {
        String ret = null;
        if (url != null) {
            ret = url.substring(url.lastIndexOf("/") + 1);
        }
        return ret;
    }

    public String getUpdateApkName() {
        return updateApkName;
    }

}
