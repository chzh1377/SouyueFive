package com.zhongsou.souyue.net.sub;

import com.zhongsou.souyue.countUtils.ZSSdkUtil;
import com.zhongsou.souyue.module.RecommendTabSubListItem;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

/**
 * Created by zyw on 2015/12/21.
 * //批量删除 & 添加接口
 * 接口：http://61.135.210.239:8888/d3api2/subscribe/subscribe.recommend.my5.1.groovy
 * 功能不一样，传参有所差别,请注意。
 * 功能----订阅时
 * 参数：
 * token,
 * imei,
 * vc,
 * subscribeData
 * 对象----例如： [{category:srp,srpId:1111111111111111111,keyword:werewr,state:0},
 * {category:interest,srpId:22222222222222222,keyword:wewww,state:0},
 * {category:special,srpId:22222222222222222,keyword:wewww,state:0}]
 * 功能----删除订阅时
 * 参数：
 * token,
 * imei,
 * vc,
 * subscribeData
 * 对象----例如： [{category:srp,id:1111111111111111111,state:1},
 * {category:special,id:1111111111111111111,state:1},                                  {category:interest,srpId:22222222222222222,state:1}]
 * <p/>
 * <p/>
 * 返回值：
 * {
 * "head": {
 * "status": 200,
 * "hasMore": false
 * },
 * "body": {
 * "msg": "成功"
 * }}
 */
public class BatchSubAndDelRequest extends BaseUrlRequest {
    private String URL = HOST + "subscribe/subscribe.recommend.my5.1.groovy";

    public BatchSubAndDelRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return URL;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    public void setParams(HashSet<RecommendTabSubListItem> requestItem) {
        JSONArray array = new JSONArray();
        for (RecommendTabSubListItem item : requestItem) {
//            sb.append(item.toRequestString());
//            sb.append(",");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("category",item.getCategory());
                if(item.getAction() == 0){
                    //添加操作
                    jsonObject.put("keyword",item.getKeyword());
                    jsonObject.put("srpId",item.getSrpId());
                }else if(item.getAction() == 1){
                    //删除订阅
                    if(item.getCategory().equals("interest")){
                        jsonObject.put("srpId",item.getSrpId());
                    }else{
                        jsonObject.put("srpId",item.getSrpId());
                        jsonObject.put("id",item.getSubId());
                    }
                }
                jsonObject.put("state",item.getAction());
                array.put(jsonObject);
            }catch (Exception e){

            }


        }
//        sb.deleteCharAt(sb.length() - 1);
//        sb.append("]");
        addParams("subscribeData", array.toString());
        addParams("opSource", ZSSdkUtil.RECOMMEND_SUBSCRIBE);
    }

}
