package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.activity.WebSrcViewActivity;

public class VersionUtils {


//  public static String getSouYueVersion(Context context) {
//      String version = null;
//      try {
//          PackageManager manager = context.getPackageManager();
//          PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//          version = info.versionName;
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
//      return version;
//  }

    // public String getPackageName(Context context) {
    // String packageName = null;
    // try {
    // PackageManager manager = context.getPackageManager();
    // PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
    // packageName = info.packageName;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return packageName;
    // }

    /*public static String getVersionName() {
        String versionName = "";
        try {
            PackageManager manager = MainApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MainApplication.getInstance().getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {

        }
        return versionName;
    }*/

    public static int getVersionCode() {
        int versionCode = 0;
        try {
            PackageManager manager = MainApplication.getInstance().getPackageManager();
            PackageInfo info = manager.getPackageInfo(MainApplication.getInstance().getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (Exception e) {

        }
        return versionCode;
    }

    /**
     * 获得App版本
     *
     * @param context
     * @return
     */
//  public static String getAppVersion(Context context) {
//      String version = null;
//      try {
//          PackageManager manager = context.getPackageManager();
//          PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
//          version = info.versionName;
//      } catch (Exception e) {
//          e.printStackTrace();
//      }
//      return version;
//  }

    /**
     * 检测新版本（更新） 由友盟代码转换为自己的新版本检测 新逻辑 1，强制更新 每次打开软件都提示 点击取消或返回退出程序，点击更新则下载
     * 并在通知栏弹出通知 更新完成后直接进入安装界面 提示用户安装 2，非强制更新 每天打开检测，取消则进入首页面 点击更新通知栏弹出通知并进行下载
     * 新版本 更新完成后直接进入安装界面 提示用户安装
     *
     * @param context
     */
//  public void checkversion(boolean ifMain) {
//      this.ifMain = ifMain;
//      http.getUpdateInfo("souyue", "android", AppInfoUtils.getChannel(context), getAppVersion(context) + "");
//      // UmengUpdateAgent.update(context);
//      // UmengUpdateAgent.setUpdateAutoPopup(false);
//      // UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//      // @Override
//      // public void onUpdateReturned(int updateStatus, UpdateResponse
//      // updateInfo) {
//      // switch (updateStatus) {
//      // case 0: // has update
//      // UmengUpdateAgent.showUpdateDialog(context, updateInfo);
//      // break;
//      // case 1: // has no update
//      // SouYueToast.makeText(context,
//      // context.getString(R.string.registerActivity_has_no_update)+VersionUtils.getSouYueVersion(context),
//      // SouYueToast.LENGTH_SHORT).show();
//      // break;
//      // case 2: // none wifi
//      // SouYueToast.makeText(context, R.string.registerActivity_none_wifi,
//      // SouYueToast.LENGTH_SHORT).show();
//      // break;
//      // case 3: // time out
//      // SouYueToast.makeText(context, R.string.registerActivity_time_out,
//      // SouYueToast.LENGTH_SHORT).show();
//      // break;
//      // }
//      // }
//      //
//      // });
//      // UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {
//      // @Override
//      // public void OnDownloadStart() {
//      //
//      // }
//      // @Override
//      // public void OnDownloadUpdate(int progress) {
//      //
//      // }
//      // @Override
//      // public void OnDownloadEnd(int result, String file) {
//      // if (result == 0) {
//      // SouYueToast.makeText(MainApplication.getInstance(),
//      // R.string.registerActivity_update_error,
//      // SouYueToast.LENGTH_SHORT).show();
//      // }
//      // }
//      // });
//  }
    public static int checkPackage(final Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return WebSrcViewActivity.UNINSTALL;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return WebSrcViewActivity.INSTALL;
        } catch (NameNotFoundException e)

        {
            return WebSrcViewActivity.UNINSTALL;
        }

    }

//  public void getUpdateInfoDetail(HttpJsonResponse res, AjaxStatus status) {
//      // res =
//      // {"head":{"status":200,"hasMore":false},"body":{"version":"4.0.3","desc":[],"minVersion":"4.0.3","disable":[]}}
//      Gson gson = new Gson();
//      UpdateBean updateBean = gson.fromJson(res.getBody(), UpdateBean.class);
//      // 判断是否更新和是否强制更新，然后用以下语句更新 UpdateNewVersion.checkIfUpDate(context);
//      if (!updateBean.getVersion().equals(getAppVersion(context)) && isVersionBig(updateBean.getVersion(), getAppVersion(context))) {// 先判不等，再比大小，都满足则更新
//          // 更新 1,强制更新。2,非强制更新
//          boolean isMustUpdate = false;
//          if (isVersionBig(updateBean.getMinVersion(), getAppVersion(context))) {
//              isMustUpdate = true;
//          }
//          for (int i = 0; i < updateBean.getDisable().length; i++) {
//              if (updateBean.getDisable()[i].equals(getAppVersion(context))) {
//                  isMustUpdate = true;
//              }
//          }
//          new UpdateNewVersion(context, isMustUpdate, updateBean).upDate();
//      } else {
//          // 不更新
//          if(ifMain){//如果是从首页检测版本更新则不显示toast
//          }else{
//              Toast.makeText(context, "已经是最新版本，无需更新", Toast.LENGTH_LONG).show();
//          }
//      }
//
//  }

    /**
     * 版本号比较，需要先在外面判断两个字符串类型参数是否完全相等，如果相等则版本号相等无需进行再次比较
     *
     * @param first  当前版本
     * @param second 比较版本
     * @return 比较版本大于当前版本，返回true
     */
    public static boolean isVersionBig(String first, String second) {
        String strFirst[] = first.split("\\.");
        String strSecond[] = second.split("\\.");
        int length = 0;
        if (strFirst.length < strSecond.length) {
            length = strFirst.length;
        } else {
            length = strSecond.length;
        }

        for (int i = 0; i < length; i++) {
            if (Integer.valueOf(strFirst[i]) == Integer.valueOf(strSecond[i])) { // 当前数值相等，
                if (i == length - 1) {
                    return strFirst.length > strSecond.length;// 最后一项 假如是这样的版本号
                    // （4.1.2
                    // 4.1.2.3）则谁长返回谁；
                }
            } else if (Integer.valueOf(strFirst[i]) > Integer.valueOf(strSecond[i])) {// 当前版本大于比较版本
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
