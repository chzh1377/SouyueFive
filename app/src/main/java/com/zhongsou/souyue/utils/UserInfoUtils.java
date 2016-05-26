package com.zhongsou.souyue.utils;

import android.app.Activity;
import com.zhongsou.souyue.circle.model.TaskCenterInfo;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.view.TaskCenterToast;

public class UserInfoUtils {
    private static TaskCenterToast taskCenterToast = null;
    public static void jumpToFillUser(TaskCenterInfo taskCenterInfo){
        if(taskCenterInfo!=null&&taskCenterInfo.getGuide_isforced().equals("1")){
            IntentUtil.gotoWeb(UserInfoUtils.getActivity(), taskCenterInfo.getGuide_url(), "interactWeb");
        }else{
            taskCenterInfo.setCategory("user");
            taskCenterToast = new TaskCenterToast(getFirstActivity(), taskCenterInfo);
            if (taskCenterToast.isOpenPop()) {//如果当前有正在打开，则关闭当前，显示最新
              taskCenterToast.dissPopWindow();
            }
            taskCenterToast.showPopUpWindow();
                // 事件处理,更改pop状态
            taskCenterToast.setOpenPop(true);
            
        }
    }
    public static Activity getActivity(){
        Activity activity = null;
        if (ZhongSouActivityMgr.getInstance().acys != null && !ZhongSouActivityMgr.getInstance().acys.isEmpty()) {
            activity = ZhongSouActivityMgr.getInstance().acys.getLast();
        }
        return activity;
    }
    public static Activity getFirstActivity(){
        Activity activity = null;
        if (ZhongSouActivityMgr.getInstance().acys != null && !ZhongSouActivityMgr.getInstance().acys.isEmpty()) {
            activity = ZhongSouActivityMgr.getInstance().acys.getFirst();
        }
        return activity;
    }
    
    public static boolean getSpecialState(User user,boolean pushSpecialState){
        String user_cpmRecommend = null;
        if(user!=null){
            if(user.userType().equals(SYUserManager.USER_GUEST)){
                user_cpmRecommend=SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_GUEST_SPECIAL, "");
            }else{
                user_cpmRecommend=SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_SHOW_ADMIN_SPECIAL, "");
            }  
        }else{
            pushSpecialState=true;
            return pushSpecialState;
        }
        if(user_cpmRecommend==null){
            pushSpecialState=true;
            return pushSpecialState;
        }
        String[] cpmRecommends=user_cpmRecommend.split(",");
        String cpmRecommend = null;
        if(cpmRecommends!=null&&cpmRecommends.length>1){
            if(cpmRecommends[0].equals(user.userId()+"")){
                cpmRecommend=cpmRecommends[1];
            }else{
                pushSpecialState=true;
            }
        }
        if(cpmRecommend==null){
            pushSpecialState=true;
        }else{
              if(cpmRecommend.equals("1")){
                    pushSpecialState=true;
                }else{
                    pushSpecialState=false;
                }
        }
        return pushSpecialState;
    }
    
    public static void setSpecialState(boolean pushSpecialState,User user){
        String cpmRecommend;
        if(pushSpecialState){
            cpmRecommend="1";
        }else{
            cpmRecommend="0";
        }
        if(user.userType().equals(SYUserManager.USER_GUEST)){
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_GUEST_SPECIAL, user.userId()+","+cpmRecommend);
        }else{
            SYSharedPreferences.getInstance().putString(SYSharedPreferences.KEY_SHOW_ADMIN_SPECIAL, user.userId()+","+cpmRecommend);
        }
    }
}
