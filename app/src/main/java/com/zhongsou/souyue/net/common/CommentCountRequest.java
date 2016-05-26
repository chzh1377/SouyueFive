package com.zhongsou.souyue.net.common;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zyw on 2016/1/22.
 * 获取评论数，点赞数等
 * URL = "interest/comment.count.groovy"
 * 参数
 * ("url", url)
 * ("operflag",opaflag)
 * ("token", SYUserManager.getInstance().getToken())
 * 返回
 * mUpCount = Utils.getJsonValue(obj, "upCount", 0);
 * mDownCount = Utils.getJsonValue(obj, "downCount", 0);
 * mCommentCount = Utils.getJsonValue(obj, "commentsCount", 0);
 * mHasUp = Utils.getJsonValue(obj, "hasUp", false);
 * mHasDown = Utils.getJsonValue(obj, "hasDown", false);
 * mHasFavorited = Utils.getJsonValue(obj, "hasFavorited", false);
 * mZsbCount = Utils.getJsonValue(obj, "zsbCount", 0);
 */
public class CommentCountRequest extends BaseUrlRequest {

    private static final String MYURL = "interest/comment.count.groovy";

    public CommentCountRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + MYURL;
    }

    public void setParams(String url, String opaflag) {
        addParams("url", url);
        addParams("operflag", opaflag);
        addParams("token", SYUserManager.getInstance().getToken());
    }

    /**
     * 请求获取评论数，点赞数等
     *
     * @param id
     * @param url
     * @param opaflag
     * @param callback
     */
    public static void send(int id, String url, String opaflag, IVolleyResponse callback) {
        CommentCountRequest request = new CommentCountRequest(id, callback);
        request.setParams(url, opaflag);
        CMainHttp.getInstance().doRequest(request);
    }
}
