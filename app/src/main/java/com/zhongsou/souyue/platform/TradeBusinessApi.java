/**
 * Project Name: souyue-platform-4.0.1
 * @version  4.0.1
 */
package com.zhongsou.souyue.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.LoginActivity;
import com.zhongsou.souyue.activity.LoginInputPhoneNumActivity;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.countUtils.UpEventAgent;
import com.zhongsou.souyue.fragment.MineFragment;
import com.zhongsou.souyue.module.User;

/** 
 * 搜悦与超级APP耦合部分调用类<br/> 
 * Company: 	ZhongSou.com<br/> 
 * Copyright: 	2003-2014 ZhongSou All right reserved<br/> 
 * @date     	2014-8-6 下午2:36:42
 * @author   	liudl
 */
public class TradeBusinessApi {
    private static TradeBusinessApi sInstance = null;
    
    public static TradeBusinessApi getInstance() {
        if (sInstance == null) {
            synchronized (TradeBusinessApi.class) {
                if (sInstance == null) {
                    sInstance = new TradeBusinessApi();
                }
            }
        }
        return sInstance;
    }
    
    /**  
     * 登陆成功后回调商城登陆 <br/>  
     *  
     * @author liudl
     * @date   2014-10-21 下午2:22:31
     * @param user
     * @param password  
     */
    public void mallLoginSuccess(User user,String password){
        if(ConfigApi.isSouyue()){
            return;
        }
    }
    
    /**  
     * 编辑昵称成功后回调商城编辑昵称 <br/>  
     *  
     * @author liudl
     * @date   2014-10-21 下午2:37:40
     * @param user  
     */
    public void mallEditNickNameSuccess(User user){
        if(ConfigApi.isSouyue()){
            return;
        }
    }

    /**  
     * 点击注册按钮跳转. <br/>  
     *  
     * @author liudl
     * @date   2014-10-21 下午4:27:24
     * @param context
     * @param fromGame  
     */
    public void registerRedirect(Context context, boolean fromGame){
            //统计  点击注册按钮
            UpEventAgent.onRegClick(context);
            Intent phoneIntent = new Intent(context,LoginInputPhoneNumActivity.class);
            phoneIntent.putExtra(LoginActivity.LOGIN_TYPE, LoginActivity.PHONEREG);
//            phoneIntent.putExtra(TigerGameActivity.LOGIN_TAG, fromGame);
            context.startActivity(phoneIntent);

    }
    
    /**  
     * 登陆成功后跳转. <br/>  
     *  
     * @author liudl
     * @date   2014-10-21 下午4:43:14
     * @param context
     * @param onlyLogin
     */
    public void loginRedirect(Activity context,boolean onlyLogin){
            if (!onlyLogin) {
                Intent intent = new Intent();
                intent.setClass(context, MainActivity.class);
                intent.putExtra("TAB_TAG_EXTRA", MineFragment.TAB_NAME);
                context.finish();
                context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                context.startActivity(intent);
            } else {
                Intent mIntent = new Intent("subscribeState");
                context.sendBroadcast(mIntent);
                context.setResult(context.RESULT_OK);
                context.finish();
            }

    }
    
    /**
     * 短信登陆成功后跳转实现. <br/>
     * 
     * @author liudl
     * @date 2014-10-22 上午9:40:48
     * @param context
     * @param onlyLogin
     */
    public void msgLoginSuccess(Activity context, boolean onlyLogin) {//其它参数都没用到所以去掉
//        public void msgLoginSuccess(Activity context, String showPager,
//                String shopAction, boolean onlyLogin, String token, String syuid) {
        if (ConfigApi.isSouyue()) {
            if (!onlyLogin) {
                Intent intent = new Intent();
                intent.setClass(context, MainActivity.class);
                intent.putExtra("TAB_TAG_EXTRA", MineFragment.TAB_NAME);
                context.startActivity(intent);
            } else {
                Intent mIntent = new Intent("subscribeState");
                context.sendBroadcast(mIntent);
                context.setResult(context.RESULT_OK);
            }
        }
        context.finish();
        context.overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

   /**
    * 判断该当前活动SRPID是否下载完毕
    *
    * @author liudl
    * @date   2015-02-11 下午5:56:24
    * @param srpId
    */
   public boolean isActivitySrpDownloadOver(String srpId){
       return false;
   }
}
  
	