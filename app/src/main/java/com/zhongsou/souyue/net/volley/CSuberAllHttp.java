package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.zhongsou.souyue.net.UrlConfig;

import java.util.List;

/**订阅大全 网络接口
 * Created by wangqiang on 15/8/28.
 */
public class CSuberAllHttp extends AHttp {

    public static final int SUBER_ALL_INTEREST_ADD_ACTION = 0x0001;
    public static final int SUBER_ALL_INTEREST_DELETE_ACTION = 0x0002;
    public static final int SUBER_ALL_INTEREST_GROUP_ACTION = 0x0003;
    public static final int SUBER_ALL_INTEREST_CHILD_ACTION = 0x0004;

    public static final int SUBER_ALL_RSS_GROUP_ACTION = 0x0005;
    public static final int SUBER_ALL_RSS_CHILD_ACTION = 0x0006;
    public static final int SUBER_ALL_RSS_ADD_ACTION = 0x0007;
    public static final int SUBER_ALL_RSS_DELETE_ACTION = 0x0008;


    public CSuberAllHttp(Context _context) {
        super(_context, CSuberAllHttp.class.getName());
        // TODO Auto-generated constructor stub
    }

    ///////////////interst//////////////
    public void suberAllInterestGroup(int id,IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.SUBER_ALL_INTEREST_GROUP_URL);
        mVolley.doRequest(request);
    }

    public void suberAllInterestChild(int id, String token,String groupId, IVolleyResponse callback){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("group_id",groupId);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.SUBER_ALL_INTEREST_CHILD_URL);
        mVolley.doRequest(request);
    }

    public void suberAllInterestAdd(int id, String token, String vc,String interest_ids, String imei,
                                    IVolleyResponse callback,String opSource){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("vc", vc);
        request.addParams("imei", imei);
        request.addParams("interest_ids",interest_ids);
        request.addParams("opSource", opSource);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.SUBER_ALL_INTEREST_ADD_URL);
        mVolley.doRequest(request);
    }

    public void suberAllInterestDelete(int id, String token, String vc,String interest_ids, String imei,
                                       IVolleyResponse callback,String opSource){
        CVolleyRequest request = new CVolleyRequest();
        request.setmId(id);
        request.addParams("token", token);
        request.addParams("vc", vc);
        request.addParams("imei", imei);
        request.addParams("interest_id",interest_ids);
        request.addParams("opSource", opSource);
        request.setCallBack(callback);
        request.setUrl(UrlConfig.SUBER_ALL_INTEREST_DELETE_URL);
        mVolley.doRequest(request);
    }

    private String mkString(List list) {
        return list.toString().replaceAll("[\\[\\]\\s]", "");
    }





}
