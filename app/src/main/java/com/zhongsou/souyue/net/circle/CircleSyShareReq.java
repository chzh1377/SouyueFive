package com.zhongsou.souyue.net.circle;

//import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * Created by wangqiang on 15/12/15.
 * 新闻分享到兴趣圈
 */
public class CircleSyShareReq  extends BaseUrlRequest {
    public   String URL = HOST + "interest/sy.share.to.interest.groovy?vc="; //

    public CircleSyShareReq(int id, IVolleyResponse response) {
        super(id , response);
    }

    @Override
    public String getUrl() {
        return URL;
    }



    public void addParams(com.zhongsou.souyue.circle.model.ShareContent cont)
    {
        addParams("interest_ids", cont.getInterest_ids());
        addParams("title", cont.getTitle());
        addParams("brief", cont.getBrief());
        addParams("content", cont.getContent());
//        addParams("images", JSON.toJSONString(cont.getImages()));
        addParams("images", new Gson().toJson(cont.getImages()));
        addParams("srp_id", cont.getSrpId());
        addParams("srp_word", cont.getKeyword());
        addParams("text_type", cont.getTextType()+"");
        addParams("url", cont.getNewsUrl());
        String url = cont.getNewsUrl();
        if (StringUtils.isNotEmpty(url)) {
            try {
                String[] arr = url.split("&url=");
                if (arr.length > 1) {
                    url = arr[1];
                }
            } catch (Exception e) {
            }
        }
        addParams("url", url);
    }
}
