package com.zhongsou.souyue.platform;
/**
 * Project Name: souyue_platform_4.0.1
 * @version  4.0.1
 */

import com.zhongsou.souyue.MainApplication;
/** 
 * Description: 配置项先从客户端Preference读取,如果为空从config.xml中取<br/> 
 * Company:     ZhongSou.com<br/> 
 * Copyright:   2003-2014 ZhongSou All right reserved<br/> 
 * @date        2014-7-30 下午2:10:31
 * @author      liudl
 */
public class CommonConfig {
    /**  
     * getResourceId:获取资源ID<br/>  
     *  
     * @author liudl
     * @date   2014-7-31 上午11:33:42
     * @param key
     * @return  
     */
    private static int getResourceId(String key, String type){
        return  MainApplication.getInstance().getResources().getIdentifier(key, type, MainApplication.getInstance().getPackageName());
    }
    
//    protected static int getStringResourceId(String key){
//        return getResourceId(key, "string");
//    }
    
    protected static int getLayoutResourceId(String key){
        return getResourceId(key, "layout");
    }
    
    protected static int getDrawableResourceId(String key){
        return getResourceId(key, "drawable");
    }
    
    protected static String getStringResourceValue(int id){
        return MainApplication.getInstance().getResources().getString(id);
    }
    
    protected static String getResourceName(int id){
        return MainApplication.getInstance().getResources().getResourceEntryName(id);
    }
//
//    /**
//     * getStringValue:获取字符串类型配置项 <br/>
//     *
//     * @author liudl
//     * @date   2014-7-31 上午9:11:44
//     * @param  key
//     * @return
//     */
//    protected static String getStringValue(String key){
//        return ConfigPreferences.getInstance().getString(key,getStringResourceValue(getStringResourceId(key)));
//    }
//
//    protected static void setStringValue(String key,String value){
//        ConfigPreferences.getInstance().setString(key, value);
//    }
//
//    /**
//     * getBooleanValue:获取布尔类型配置项. <br/>
//     *
//     * @author liudl
//     * @date   2014-7-31 上午9:13:30
//     * @param  key
//     * @return
//     */
//    protected static Boolean getBooleanValue(String key){
//        return ConfigPreferences.getInstance().getBoolean(key, Boolean.valueOf(getStringResourceValue(getStringResourceId(key))));
//    }
//
//
//    protected static void setBooleanValue(String key,boolean value){
//        ConfigPreferences.getInstance().setBoolean(key, value);
//    }
}
  
	