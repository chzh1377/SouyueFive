package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * @description:
 * @auther: qubian
 * @data: 2015/12/24.
 */

public class AddCommentUpReq extends BaseUrlRequest {

    public              String url              = HOST + "interest/interest.comment.dogoodpc.groovy";
    public static final int    DEVICE_COME_FROM = 3;// 来自搜悦客户端

    public AddCommentUpReq(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setParams(String _keyword, String _srpid, String _url, String _token, int operflag, int type, long comment_id, String main_title, String main_images, String main_decsription, String main_date, String main_source, long blog_user_id) {
        addParams("srpword", _keyword);
        addParams("srpid", _srpid);
        addParams("url", _url);
        addParams("token", _token);
        addParams("operflag", operflag + "");
        addParams("type", type + "");
        addParams("comment_id", comment_id + "");
        addParams("main_title", main_title);
        addParams("main_images", main_images);
        addParams("main_decsription", main_decsription);
        addParams("main_date", main_date);
        addParams("main_source", main_source);
        addParams("blog_user_id", blog_user_id + "");
    }

    public void setParams(String _keyword, String _srpid, String _url, String _token, int operflag, int type, long comment_id, String main_title, String main_images, String main_decsription, String main_date, String main_source, long blog_user_id, CommentsForCircleAndNews postsNew) {
        setParams(_keyword, _srpid, _url, _token, operflag, type, comment_id, main_title, main_images, main_decsription, main_date, main_source, blog_user_id);
        addKeyValueTag("CommentsForCircleAndNews", postsNew);
    }

    public void setParams(String _keyword, String _srpid, String _url, int type, long comment_id,
                          String main_title, String main_images, String main_decsription,
                          String main_date, String main_source, long blog_user_id) {
        addParams("srpword", _keyword);
        addParams("srpid", _srpid);
        addParams("url", _url);
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("operflag", DEVICE_COME_FROM + "");
        addParams("type", type + "");
        addParams("comment_id", comment_id + "");
        addParams("main_title", main_title);
        addParams("main_images", main_images);
        addParams("main_decsription", main_decsription);
        addParams("main_date", main_date);
        addParams("main_source", main_source);
        addParams("blog_user_id", blog_user_id + "");
    }
}
