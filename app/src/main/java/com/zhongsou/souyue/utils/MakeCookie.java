package com.zhongsou.souyue.utils;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.common.utils.CommSharePreference;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.module.firstleader.UserGuideInfo;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.CMainHttp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * @author : zoulu
 * 2014年4月10日
 * 上午10:49:07 
 * 类说明 :生成cookie来访问web页
 */
public class MakeCookie {
    
    private static final String cookieStr=";Max-Age=3600*24;Domain=.%s;Path=/;expires=%s";
    private static final String cookieStrip=";Max-Age=3600*24;Domain=%s;Path=/;expires=%s";
    
    public static void synCookies(Context context, String url) {
        String regstrIp="https?://((\\d{1,3}\\.){3}\\d{1,3})(:\\d+)?/";
        Pattern patIp = Pattern.compile(regstrIp); 
        Matcher mat; 
        mat=patIp.matcher(url);
        String domain = null;
        if(mat.find()){
            domain= mat.group(1);
        }
        if(StringUtils.isEmpty(domain)||domain.length()==0){
            String regEx="^((\\w+://)([\\w\\d]+\\.)*([\\w\\d]+\\.[\\w\\d]+))(:\\d+)?/";
            Pattern pat = Pattern.compile(regEx);  
            mat= pat.matcher(url);
            if(mat.find()){
                domain=mat.group(4);
            }else{
                return;
            }
        }
        String wifi= CMainHttp.getInstance().isWifi(context)? "1" : "0";
        String hasPic = SYSharedPreferences.getInstance().getLoadWifi(context)?"0":"1";
        try {
            if(StringUtils.isNotEmpty(domain)){
                if((domain.startsWith("zhongsou.")||domain.contains("souyue.mobi"))){
                    CookieSyncManager.createInstance(context);
                    CookieManager cookieManager = CookieManager.getInstance();  
                    cookieManager.setAcceptCookie(true);  
                    cookieManager.removeAllCookie();//移除
                    Map<String,Object> cookieMap=getCookieMap(wifi,hasPic);//平均2ms
                    Iterator<String> iter = cookieMap.keySet().iterator();//这个遍历循环耗时30ms
                    while(iter.hasNext()){
                        String key=iter.next();
                        Object value =  cookieMap.get(key);
                        value = key+"="+value+ String.format(cookieStr,domain,expires());
                        cookieManager.setCookie(domain,value.toString());
                    }
                }else if(domain.contains("61.135.210.239")||
                        domain.contains("103.29.134.225")||domain.contains("103.29.134.224")){
                    CookieSyncManager.createInstance(context);
                    CookieManager cookieManager = CookieManager.getInstance();  
                    cookieManager.setAcceptCookie(true);  
//                    cookieManager.removeAllCookie();//移除
                    Map<String,Object> cookieMap=getCookieMap(wifi,hasPic);
                    Iterator<String> iter = cookieMap.keySet().iterator();
                    while(iter.hasNext()){
                        String key=iter.next();
                        Object value =  cookieMap.get(key);
                        value = key+"="+value+ String.format(cookieStrip,domain,expires());
                        cookieManager.setCookie(domain,value.toString());          
                    }
                    
                }
                CookieSyncManager.getInstance().sync();
            }
            
        } catch (Exception e) {
        }
    }
    public static String getDetailCookie(Context context){
        StringBuilder sb = new StringBuilder();
        String wifi= CMainHttp.getInstance().isWifi(context)? "1" : "0";
        String hasPic = SYSharedPreferences.getInstance().getLoadWifi(context)?"0":"1";
        sb.append("wifi=").append(wifi).append("; ").append("hasPic=").append(hasPic).append("; ");
        return sb.toString();
    }
    
    private static String expires(){
         //获取当前日期  
        Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat("E,dd MMM yyyy HH:mm:ss ",Locale.ENGLISH);
        String nowDate = sdf.format(date);  
        Calendar cal = Calendar.getInstance();  
        try {
            cal.setTime(sdf.parse(nowDate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        cal.add(Calendar.DAY_OF_YEAR, + 1);  
        String nextDate_1 = sdf.format(cal.getTime());  
        
        return nextDate_1 + "GMT";
    }
    
    private static boolean isLogin(){
        User user=SYUserManager.getInstance().getUser();
        return user != null && user.userType().equals(SYUserManager.USER_ADMIN);
    }
    
    private static Map<String, Object> getCookieMap(String wifi,String hasPic){
        Map<String, Object> cookieMap=new HashMap<String, Object>();
        User user = SYUserManager.getInstance().getUser();
        cookieMap.put("username", StringUtils.enCodeRUL(user.userName()));
        cookieMap.put("nick_name",StringUtils.enCodeRUL(user.name()));
        cookieMap.put("userphoto", StringUtils.enCodeRUL(user.image()));
        cookieMap.put("userid", user.userId());
        cookieMap.put("version", DeviceInfo.getAppVersion());
        cookieMap.put("vc", DeviceInfo.getAppVersion());
        try {
            cookieMap.put("fontsize",DeviceInfo.getSize().get("fontsize").toString());
        }catch (Exception e){

        }
        cookieMap.put("token", StringUtils.enCodeRUL(user.token()));
        if(isLogin()){
            cookieMap.put("anonymous",  1);
        }else{
          cookieMap.put("anonymous" , 0);
          cookieMap.put("nick_name",StringUtils.enCodeRUL("帐号登录"));
        }
        cookieMap.put("wifi", wifi);
        cookieMap.put("hasPic", hasPic);
        cookieMap.put("authkey",StringUtils.enCodeRUL(Encode.encode(user.userName())));
        cookieMap.put("lon", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LNG, ""));
        cookieMap.put("lat", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LAT, ""));
        cookieMap.put("screenResolution", DeviceInfo.getScreenSize());
        cookieMap.put("channel", StringUtils.enCodeRUL(DeviceInfo.getUmengChannel(MainApplication.getInstance())));
        //本地sp种存储的性别
        long uid = 0;
        try {
            uid = Long.decode(SYUserManager.getInstance().getUserId());
        } catch (Exception e) {

        }
        String localSex = CommSharePreference.getInstance().getValue(uid, UserGuideInfo.GENDER_KEY, "男");
        String localAge = CommSharePreference.getInstance().getValue(uid, UserGuideInfo.AGE_KEY, "");
        cookieMap.put("user_sex", StringUtils.enCodeRUL(localSex)); //要编码
        cookieMap.put("age", StringUtils.enCodeRUL(localAge)); //要编码
//        cookieMap.put("user_sex", "男".equals(localSex)?"0":1);
        return cookieMap;
    }
}
