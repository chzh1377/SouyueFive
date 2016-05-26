package com.zhongsou.souyue.enterprise.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.content.ShareContent;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.List;

public class SouyueAPIManager {

    private static ShareContent content;
    private static SouyueAPIManager manager;
//    static Http http;

    public static  SouyueAPIManager getInstance(){
        if(manager == null){
            manager = new SouyueAPIManager();
        }
        return manager;
    }
    
    public SouyueAPIManager(){
//        http = new Http(this);
    }
    /**
     * 跳转登录页面
     * 
     * @param context
     * @param isCallBack 登录成功后是否回到原页面,true 返回;反之
     */
    public static void goLogin(Context context, boolean isCallBack) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("Only_Login", isCallBack);
        context.startActivity(intent);
    }
    
    /**
     * 跳转登录页面
     * 
     * @param context
     * @param code  登录成功后是否回到原页面,true 返回;反之
     */
    public static void goLoginForResult(Activity context, int code) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("Only_Login", true);
        context.startActivityForResult(intent, code);
    }

    /**
     * 获取用户信息
     * 
     * @return 若未登录则返回null, 反之，返回当前User信息对象
     */
    public static User getUserInfo() {
        if (!SYUserManager.USER_ADMIN.equals(SYUserManager.getInstance().getUserType())) {
            return null;
        }
        return SYUserManager.getInstance().getUser();
    }
    
    /**
     * 未登录  返回游客token  登录后返回 用户token
     * @return
     */
    public static String getToken(){
        return SYUserManager.getInstance().getToken();
    }

    /**
     * 判断是否已经登录
     * 
     * @return
     */
    public static boolean isLogin() {
        return SYUserManager.USER_ADMIN.equals(SYUserManager.getInstance().getUserType());
    }

    private OnSubscribeResult subResult;
    private OnCheckSubState checkSubResult;
    private OnDeleteSubEntWord deleteEntWord;


    public interface OnSubscribeResult {
        public void onSubscribeSuccess(List<String> id, HttpJsonResponse response);

        public void onSubscribeError();
    }

    public interface OnCheckSubState {
        /**
         * 已订阅 bid>0;反之
         * @param bid
         */
        public void onCheckSuccess(Long bid);
        public void onCheckError();
    }

    public interface OnDeleteSubEntWord{
        public void onDeleteSuccess();
        public void onDeleteError();
    }

//    @Override
//    public void onHttpError(String methodName) {
//        if("subscribeAddEntWord".equals(methodName)){
//            subResult.onSubscribeError();
//        } else if("subscribeCheck".equals(methodName)){
//            checkSubResult.onCheckError();
//        } else if("subscribeDelete".equals(methodName)){
//            deleteEntWord.onDeleteError();
//        }
//    }
}
