package com.zhongsou.souyue.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.tuita.sdk.PushService;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.db.SelfCreateHelper;
import com.zhongsou.souyue.db.UserHelper;
import com.zhongsou.souyue.fragment.MineFragment;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.AccountInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.common.UpdateClientId;
import com.zhongsou.souyue.net.other.UploadPushRegIDRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.share.ShareByWeibo;
import com.zs.zssdk.ZSclickAgent;

public class SYUserManager  {
    private static SYUserManager instance = null;
    private User user;
    public static final String USER_GUEST = "0";
    public static final String USER_ADMIN = "1";
    protected SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    private static class SetAliasHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                PushUtils.setPushAlias(MainApplication.getInstance());  //可能会出错
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private SYUserManager() {
    }

    public synchronized static SYUserManager getInstance() {
        if (instance == null) {
            instance = new SYUserManager();
        }
        return instance;
    }

    public String getImage() {
        if (user != null && !StringUtils.isEmpty(user.image())) {
            return user.image();
        } else if (getUser() != null) {
            return getUser().image();
        }

        return "";
    }

    public String getName() {
        if (user != null && !StringUtils.isEmpty(user.name()))
            return user.name();
        else if (getUser() != null)
            return getUser().name();
        return "";
    }

    public String getUserName() {
        if (user != null && !StringUtils.isEmpty(user.userName()))
            return user.userName();
        else if (getUser() != null) {
            return getUser().userName();
        }
        return null;
    }

    public boolean getFreeTrial() {
        if (user != null && !StringUtils.isEmpty(user.freeTrial()))
            return user.freeTrial();
        else if (getUser() != null) {
            return getUser().freeTrial();
        }
        return false;
    }

    public boolean getLoved() {
        if (user != null && !StringUtils.isEmpty(user.loved()))
            return user.loved();
        else if (getUser() != null) {
            return getUser().loved();
        }
        return false;
    }

    public String getToken() {
        if (user != null && !StringUtils.isEmpty(user.token())) {
            return user.token();
        } else if (getUser() != null) {
            return getUser().token();
        }

        return null;
    }

    public String getUserType() {
        if (user != null && !StringUtils.isEmpty(user.userType()))
            return user.userType();
        else if (getUser() != null) {
            return getUser().userType();
        }
        return null;
    }

    public String getUserId() {
        if (user != null && user.userId() != 0)
            return String.valueOf(user.userId());
        else if (getUser() != null) {
            return String.valueOf(getUser().userId());
        }
        return "0";
    }

    public User getUser() {
        if (user == null || StringUtils.isEmpty(user.token()))
            user = UserHelper.getInstance().getUserInfo();
        return user;
    }

    /**
     * 获得游客信息
     * @return
     */
    public User getGuestUser(){
        return UserHelper.getInstance().getGuestUserInfo();
    }

    /**
     * 获得游客ID
     * @return
     */
    public String getGuestId() {
        return String.valueOf(getGuestUser().userId());
    }

    public User getUserAdmin() {
        if (user == null || StringUtils.isEmpty(user.token()))
            user = UserHelper.getInstance().getAdminUserInfo();
        return user;
    }

    public User getDBUser() {
        return UserHelper.getInstance().getUserInfo();
    }

    public void setUser(User user) {
        Log.i("Tuita", "set user:" + user);
        Log.i("setUser", "------->set user:" + user);
        sysp.remove(SYSharedPreferences.SRPID);
        this.user = user;
        if (USER_ADMIN.equals(user.userType())) {
            //统计SDK获得userId
            try {

                Log.d("SYUserManager", "getUserId() : " + getUserId());
                ZSclickAgent.setUser(MainApplication.getInstance().getApplicationContext(), getUserId());   //ZSSDK 设置用户

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SYUserManager", "ZSSDK-setUser Exception");
            }
            PushService.setImUserIdentity(MainApplication.getInstance(), true);

            //向IM发送，推送的注册ID
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String pushRegID = PushUtils.getPushRegID(MainApplication.getInstance());
                    if (StringUtils.isNotEmpty(pushRegID)
                            && PushUtils.isUploadPushRegID()
                            && CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance())) {
                        int pushChannel = PushUtils.getPushChannel();
                        UploadPushRegIDRequest request = new UploadPushRegIDRequest(HttpCommon.UPLOAD_PUSH_REGID_REQUEST, MainApplication.getInstance());
                        request.setParams(pushRegID, pushChannel);
                        CMainHttp.getInstance().doRequest(request);
                    }
                }
            }, 1000);
        }

        if (user.userId() != 0) {
            SetAliasHandler handler = new SetAliasHandler();    //设置推送别名
            handler.sendEmptyMessageDelayed(0, 5000);
        }

        if (!StringUtils.isEmpty(user.token())) {
            UserHelper.getInstance().addUserInfo(user);
            Log.i("setUser", "222------->set user:" + user);
            //5.0新增启动服务操作原有启动操作不再适用搜悦
//            PushService.initPushService();
        }
        //将服务器的推送设备token和用户关联关系取消（传-1），防止给注销用户推送消息
        UpdateClientId updateClientid= new UpdateClientId(HttpCommon.UPDATECLIENT_REQUEST_ID, null);
        updateClientid.setParams("login");
        CMainHttp.getInstance().doRequest(updateClientid);
    }


//	public void updataUserImage(String image) {
//		UserHelper.getInstance().updataUserImage(image);
//		this.user = null;
//		getUser();
//	}
//	public void updateUserNick(String nick) {
//		UserHelper.getInstance().updateUserNick(nick);
//		this.user = null;
//		getUser();
//	}

    public boolean delUser(User user) {
        PushService.setImUserIdentity(MainApplication.getInstance(), false);
        SYSharedPreferences.getInstance().remove(user.userName()); //删除用户进入老虎机的记录（一个用户一个记录）
        long index = UserHelper.getInstance().deleteUser(user);
        SYSharedPreferences.getInstance().putBoolean(SYSharedPreferences.KEY_USER_UPDATE, true);
        //将服务器的推送设备token和用户关联关系取消（传-1），防止给注销用户推送消息
        if (index > 0) {

            try {
                ZSclickAgent.setUser(MainApplication.getInstance().getApplicationContext(), null);   //ZSSDK 设置用户
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SYUserManager", "ZSSDK-userExitSouYue Exception");
            }

            if (user.userId() != 0) {
                SetAliasHandler handler = new SetAliasHandler();    //设置推送别名
                handler.sendEmptyMessageDelayed(0, 5000);
            }

            SelfCreateHelper.getInstance().delAllSelfCreate();//删除当前用户库中原创内容
            this.user = null;
            UpdateClientId updateClientid= new UpdateClientId(HttpCommon.UPDATECLIENT_REQUEST_ID, null);
            updateClientid.setParams("add");
            CMainHttp.getInstance().doRequest(updateClientid);
            return true;
        } else
            return false;
    }

    public void delByUid0(User user) {
        UserHelper.getInstance().deleteUser(user);
    }

    /**
     * 退出搜悦的一些操作
     *
     * @param context
     */
    public static void userExitSouYue(Context context) {
        IntentUtil.chageDiscoverTabRed(context, -2);

        ZhongSouActivityMgr.getInstance().goHome();//退回到首页
        SYSharedPreferences.getInstance().remove(
                SYSharedPreferences.KEY_TASKCENTER_DISCOVERMSG);
        Intent intent = new Intent();
        intent.setAction(MineFragment.logoutAction);
        context.sendBroadcast(intent);
        User user = SYUserManager.getInstance().getUserAdmin();
        if (user != null) {
            SYUserManager.getInstance().delUser(user);
            String type = AccountInfo
                    .removeLoginToken();
            if (!TextUtils.isEmpty(type)) {
                switch (AccountInfo.THIRDTYPE
                        .valueOf(type)) {
                    case SINA_WEIBO:
                        ShareByWeibo.getInstance().unAuth2(
                                context);
                        break;
//                    case TECENT_WEIBO:
//                        ShareByTencentWeiboSSO
//                                .getInstance().clearOAuth2(
//                                context);
//                        break;
                    /*case RENREN:
                        ShareByRenren.getRenren(context)
                                .logout();
                        break;*/
                }
            }
            ThreadPoolUtil.getInstance().execute(
                    new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated
                            // method stub
//                            ImserviceHelp.getInstance()
//                                    .im_logout();
                            ImserviceHelp.getInstance().im_connect(
                                    DeviceInfo.getAppVersion());
                        }
                    });

            ImserviceHelp.getInstance().cancelNotify(-1);
        }
    }
}
