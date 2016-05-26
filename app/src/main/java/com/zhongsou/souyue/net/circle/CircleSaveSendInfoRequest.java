package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zyw on 2015/12/12.
 * 圈吧信息发布微件
 * Method : POST
 * params :
 * param.put("token", SYUserManager.getInstance().getToken());
 * param.put("title", title);
 * param.put("content", content);
 * param.put("uuid", uuid);
 * param.put("syssign", "sy");	//搜悦客户端标示
 * param.put("username", username);
 * param.put("nickname", nickname);
 * param.put("userid", userid);
 * param.put("srpid", srpid);
 * param.put("keyword", keyword);
 * param.put("conpic", conpic);
 */
public class CircleSaveSendInfoRequest extends BaseUrlRequest {

    private static final String URL = getsaveSendInfo(); //圈吧信息发布微件地址

    private static String getsaveSendInfo() {
        int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
                return "http://202.108.1.109/webapi/savearticle";//"http://hems3.zhongsou.com/webapi/savearticle"
            case SOUYUE_PRE_ONLINE:
                return "http://202.108.1.114/webapi/savearticle";
            case SOUYUE_ONLINE:
                return "http://edit.zhongsou.com/webapi/savearticle";
            default:
                return "http://edit.zhongsou.com/webapi/savearticle";
        }
    }

    public CircleSaveSendInfoRequest(int id, IVolleyResponse response) {
        super(id,response);
    }


    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return URL;
    }

    public void addParams(String title, String content, String uuid ,
                          String username, String nickname, String userid, String srpid, String keyword, String conpic) {
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("title", title);
        addParams("content", content);
        addParams("uuid", uuid);
        addParams("syssign", "sy");	//搜悦客户端标示
        addParams("username", username);
        addParams("nickname", nickname);
        addParams("userid", userid);
        addParams("srpid", srpid);
        addParams("keyword", keyword);
        addParams("conpic", conpic);
    }
    public static void send(int id,IVolleyResponse resp,String title, String content, String uuid ,
                            String username, String nickname, String userid, String srpid, String keyword, String conpic){
        CircleSaveSendInfoRequest circleSaveSendInfo = new CircleSaveSendInfoRequest(id, resp);
        circleSaveSendInfo.addParams(title, content, uuid, username,nickname, userid, srpid, keyword, conpic);
        CMainHttp.getInstance().doRequest(circleSaveSendInfo);
    }
}
