package com.zhongsou.souyue.net.detail;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 设置评论为热门评论
 * @author chz
 *
 */
public class CommentSetHotRequest extends BaseUrlRequest {

	public CommentSetHotRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public void setParams(String _url,long comment_id,int status,int operflag){
        addParams("comment_id", comment_id + "");
        addParams("status", status + "");
        addParams("url", _url);
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("operflag", operflag + "");
    }
    
	@Override
	public String getUrl() {
		return HOST + "interest/comment.sethot.groovy";//把某条评论设置为热门评论
	}

}
