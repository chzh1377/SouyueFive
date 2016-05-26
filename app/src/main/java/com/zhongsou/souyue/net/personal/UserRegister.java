package com.zhongsou.souyue.net.personal;

import android.content.Context;
import android.os.Environment;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.DesUtil;
import com.zhongsou.souyue.utils.DeviceUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.TelephonyInfo;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 手机号注册接口
 * Created by lvqiang on 15/12/13.
 */
public class UserRegister extends BaseUrlRequest {
    private String URL = HOST + "user/register3.1.groovy";
    public UserRegister(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl(){
        return URL;
    }

    public void setParams(String account, String nick, String password, int type,
                          String validate , String city,String province,String sms_content){
        addParams("account", account);
        addParams("nick", nick);
        addParams("imei", DeviceUtil.getDeviceId(MainApplication.getInstance()));
        addParams("mac", com.tuita.sdk.DeviceUtil.getMacAddr(MainApplication.getInstance()));
        addParams("imsi", com.tuita.sdk.DeviceUtil.getSIMNum(MainApplication.getInstance()));
        addParams("uuid", com.tuita.sdk.DeviceUtil.getUUID(MainApplication.getInstance()));
        addParams("appName", com.tuita.sdk.ContextUtil.getAppId(MainApplication.getInstance()));//服务器生成clientId
        addParams("password", password);
        addParams("channel", DeviceInfo.getUmengChannel(MainApplication.getInstance()));
        addParams("deviceInfo", DeviceUtil.getDeviceInfo(MainApplication.getInstance()));
        addParams("valiNo", readValiNoFile());
        if(type == 0){
            addParams("type", "mail");
        }else if(type == 1){
            addParams("type", "mobi");
            addParams("validate", validate);
        }else if(type == 2) {
            addParams("type", "common");
        }
        addParams("city", city);
        addParams("province", province);
        addParams("lat", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LAT, ""));
        addParams("log", SYSharedPreferences.getInstance().getString(SYSharedPreferences.KEY_LNG, ""));

        addParams("modelType", DeviceUtil.getDeviceModel());
        addParams("systemVc", DeviceInfo.osVersion);
        addParams("state", TelephonyInfo.getSimState(MainApplication.getInstance())+"");
        addParams("_d", getDeviceInfo(sms_content));
    }

    // 设备信息和短信的组合
    private String getDeviceInfo(String sms_content) {
        String psw = "s1o2u3y4";
        JSONObject result = new JSONObject();
        JSONObject json = DeviceUtil.getDeviceIds(MainApplication.getInstance());
        try {
            result.put("device", json);
            result.put("sms", sms_content);
//            String str = encode(result.toString());
            String str = result.toString();

            String encryptResultStr = DesUtil.encryptDES(str, psw);
            return encryptResultStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readValiNoFile(){
        RandomAccessFile f;
        byte[] bytes=null;
        try {
            f = new RandomAccessFile(valiNoFile(), "r");
            bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(bytes==null){
            return "";
        }
        return new String(bytes);

    }

    private static File valiNoFile() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        File file;
        File fileValiNo;
        if (sdCardExist) {
            File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
            String path = sdDir.getAbsolutePath() + VALINO_PATH;
            file = new File(path);
            if(!file.exists())
                file.mkdirs();
            fileValiNo = new File(path + FILE_VALINO);
            if(!fileValiNo.exists()){
                try {
                    fileValiNo.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fileValiNo;
        } else {
            file = getCachevaliNoDir(MainApplication.getInstance());
            fileValiNo= new File(file.getAbsolutePath() + FILE_VALINO);
            if(!fileValiNo.exists()){
                try {
                    fileValiNo.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return file;
        }
    }

    public static File getCachevaliNoDir(Context context){

        if(cacheDir == null){
            cacheDir = new File(context.getCacheDir(),FILE_VALINO);
            cacheDir.mkdirs();
        }

        return cacheDir;

    }
    private static File cacheDir;
    public static void setCacheDir(File dir){
        cacheDir = dir;
        if(cacheDir != null){
            cacheDir.mkdirs();
        }
    }
    private static final String VALINO_PATH = "/souyue/login/temp/valino/";
    private static final String FILE_VALINO="valiNo";
}
