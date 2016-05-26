package com.zhongsou.souyue.net.share;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

/**
 * Created by zyw on 2015/12/13.
 * 分享帖子到精华帖	interest/blog.share.groovy
 * Method : GET
 * params:
 * param.put("blog_id", blog_id);
 * param.put("token", token);
 * param.put("interest_ids", interest_ids);
 */
public class SharePostToDigistRequest extends BaseUrlRequest {
//    public static final String URL = HOST
//            + "interest/blog.share.groovy";// 分享帖子到精华帖

    public SharePostToDigistRequest(int id, IVolleyResponse response) {
        super(id,response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost()  + "interest/blog.share.groovy";// 分享帖子到精华帖
    }

    public void setParams(long blog_id, String token, String interest_ids) {
        addParams("blog_id", blog_id + "");
        addParams("token", token);
        addParams("interest_ids", interest_ids);
    }

    /**
     * 简写
     * @param id
     * @param resp
     * @param blog_id
     * @param token
     * @param interest_ids
     */
    public static void send(int id, IVolleyResponse resp, long blog_id, String token, String interest_ids) {
        SharePostToDigistRequest sharePostToDigist = new SharePostToDigistRequest(id, resp);
        sharePostToDigist.setParams(blog_id, token, interest_ids);
        CMainHttp.getInstance().doRequest(sharePostToDigist);
    }
}
