package com.zhongsou.souyue.net.circle;

import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;

/**
 * Created by zyw on 2015/12/12.
 * 保存帖子	interest/blog.save.groovy
 * Method : POST
 * params:
 * param.put("token", SYUserManager.getInstance().getToken());
 * param.put("mblog_id", mblog_id);
 * param.put("blog_id", blog_id);
 * param.put("interest_id", interest_id);
 * param.put("title", title);
 * param.put("content", content);
 * param.put("images", images);
 * param.put("user_ids", user_ids);
 * param.put("tag_id", tag_id);
 * param.put("posting_state", posting_state);
 */
public class CircleSaveBlogRequest extends BaseUrlRequest {

    public CircleSaveBlogRequest(int id, IVolleyResponse response) {
        super(id, response);
    }

    @Override
    public String getUrl() {
        return getSouyueHost() + "interest/blog.save.groovy?vc=" + DeviceInfo.getAppVersion();// 保存帖子;
    }

    @Override
    public int getMethod() {
        return REQUEST_METHOD_POST;
    }

    /**
     *
     * @param mblog_id
     * @param blog_id
     * @param interest_id
     * @param title
     * @param content
     * @param images
     * @param user_ids
     * @param tag_id
     * @param posting_state
     */
    public void setParams(long mblog_id, long blog_id,
                          long interest_id, String title, String content, String images,
                          String user_ids, String tag_id, int posting_state) {
        addParams("token", SYUserManager.getInstance().getToken()); // token
        addParams("mblog_id", mblog_id + ""); // 不知道为啥有俩blog_id
        addParams("blog_id", blog_id + "");
        addParams("interest_id", interest_id + ""); // 圈子id
        addParams("title", title); // 标题
        addParams("content", content); // 内容
        addParams("images", images); // 图片列表
        addParams("user_ids", user_ids); // 用户id
        addParams("tag_id", tag_id); // 标签id
        addParams("posting_state", posting_state + ""); // 发布状态
    }

    /**
     * 发请求
     * @param id
     * @param response
     * @param mblog_id
     * @param blog_id
     * @param interest_id
     * @param title
     * @param content
     * @param images
     * @param user_ids
     * @param tag_id
     * @param posting_state
     */
    public static void send(int id, IVolleyResponse response,long mblog_id, long blog_id,
                            long interest_id, String title, String content, String images,
                            String user_ids, String tag_id, int posting_state){
        CircleSaveBlogRequest request = new CircleSaveBlogRequest(id, response);
        request.setParams(mblog_id, blog_id, interest_id, title, content, images, user_ids, tag_id, posting_state);
        CMainHttp.getInstance().doRequest(request);
    }
}
