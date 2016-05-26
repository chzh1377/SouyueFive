package com.zhongsou.souyue.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.gson.Gson;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.ui.CustomProgressDialog;


/**
 * Created by yinguanping on 15/1/27.
 * 第三方合作商通用登录工具类，登录返回后默认直接登录，每次添加第三方渠道商添加一个登录接口即可，登录成功统一调用loginSuccess方法。
 */
public class LoginUtils  {

    private Context context;
    protected SYSharedPreferences sysp = SYSharedPreferences.getInstance();
    private CustomProgressDialog pd;
//    private Http http = null;
    private LoginSuccessCallBack loginSuccessCallBack;
    private LoginSuccessGuest loginsuccessguest;

    /**
     * @param context
     * @param loginSuccessCallBack 登陆成功后的回调方法，如果不需要则传NULL即可。如果需要回调方法，则实现LoginSuccessCallBack接口即可
     */
    public LoginUtils(Context context, LoginSuccessCallBack loginSuccessCallBack, LoginSuccessGuest loginsuccessguest) {
        this.context = context;
        this.loginSuccessCallBack = loginSuccessCallBack;
        this.loginsuccessguest = loginsuccessguest;
        if (pd == null)
            pd = CustomProgressDialog.createDialog(context);
        pd.setCanceledOnTouchOutside(false);

//        http = new Http(this);
//        http.cachePolicy_$eq(AbstractAQuery.CACHE_POLICY_CACHE_FORCE);
    }


    /**
     * add by Yinguanping
     * 微信三方登录
     *
     * @param weiXinUserInfo
     */
//    public void loninWeiXin(WeiXinUserInfo weiXinUserInfo) {
//        pd.setMessage(context.getResources().getString(R.string.loginActivity_pd_message));
//        pd.show();

//        http.loninWeiXin(weiXinUserInfo.getUnionid(), weiXinUserInfo.getNickname(), weiXinUserInfo.getHeadimgurl(),context);
//    }

    /**
     * 登录成功返回接口，去掉了超A得返回后得系列操作。
     *
     * @param user
     */
    public void loginSuccess(User user) {
        loginSucc(user);
    }
    public void loginSuccess(HttpJsonResponse response) {
        User user = new Gson().fromJson(response.getBody(), User.class);
        loginSucc(user);
        if((StringUtils.isNotEmpty(response.getHead().get("activityUrl")))){
            String activeUrl = response.getHead().get("activityUrl").getAsString();
            IntentUtil.gotoWeb(context, activeUrl, "interactWeb");
        }
        
        if((StringUtils.isNotEmpty(response.getHead().get("guide_url")))){
            TaskCenterInfo taskCenterInfo=new TaskCenterInfo();
            taskCenterInfo.setGuide_isforced(response.getHead().get("guide_isforced").getAsString());
            taskCenterInfo.setGuide_url(response.getHead().get("guide_url").getAsString());
            taskCenterInfo.setGuide_msg(response.getHead().get("guide_msg").getAsString());
            UserInfoUtils.jumpToFillUser(taskCenterInfo);
        }
        if(StringUtils.isNotEmpty(response.getHead().get("cpmRecommend"))){
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_ADMIN_SPECIAL,
                user.userId()+","+response.getHead().get("cpmRecommend").getAsString());
        }
    }
    public void loginSucc(User user) {
        if (pd != null) {
            pd.dismiss();
        }
        sysp.putBoolean(SYSharedPreferences.KEY_USER_UPDATE, true);
        //add by trade mall
//        TradeBusinessApi.getInstance().mallLoginSuccess(user, s_userpwd);

//        logoutSns();// 第三方登录成功后，注销信息
        if (user != null) {
            user.userType_$eq("1");
//            if (!ConfigApi.isSouyue()) {
//                if (!StringUtils.isEmpty(uid)) {
//                    user.uid_$eq(uid);
//                }
//                if (!StringUtils.isEmpty(syuid)) {
//                    user.syuid_$eq(syuid);
//                }
//                token = user.token();
//                //加入行业中国
////                TradeBusinessApi.getInstance().joinHyzgBackGround(this, token, syuid);
//            }
            SYUserManager.getInstance().setUser(user);
//            ImserviceHelp.getInstance().im_logout();
//            if (from == SupplyDetailActivity.FROM_SALE_DETAIL) {
//                LoginActivity.this.finish();
//                overridePendingTransition(R.anim.left_in, R.anim.left_out);
//            } else {
            new SYInputMethodManager((Activity) context).hideSoftInput();
            Intent i = new Intent(ConstantsUtils.LINK);
            i.putExtra(com.tuita.sdk.Constants.TYPE, 40);
            context.sendBroadcast(i);
            //统计
            UpEventAgent.onLogin(context);
//                boolean onlyLogin = getIntent().getBooleanExtra(Only_Login, false);
//                long circleSetInterestId = getIntent().getLongExtra(CIRCLE_SET_INTEREST_ID, 0);

            //add by trade
//                TradeBusinessApi.getInstance().loginRedirect(this, showPager, onlyLogin, circleSetInterestId, shopAction, http);
//            }
            if (loginSuccessCallBack != null) {
                loginSuccessCallBack.loginCallBack();
            }
        }
    }
    
    public interface LoginSuccessCallBack {
        void loginCallBack();
    }

    public interface LoginSuccessGuest {
        void loginGuestCallBack();
    }

//    @Override
//    public void onHttpError(String methodName) {
//        if (pd != null) {
//            pd.dismiss();
//        }
//        if (loginsuccessguest != null) {
//            loginsuccessguest.loginGuestCallBack();
//        }
//    }
}
