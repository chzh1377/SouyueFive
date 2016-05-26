package com.zhongsou.souyue.net.detail;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.circle.model.CommentsForCircleAndNews;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 5.0获取评论列表
 * @author chz
 *
 */
public class NewCommentListRequest extends BaseUrlRequest {
    public static final int DETAIL_COMMENT_LIST_COMMENT=0;
    public static final int DETAIL_COMMENT_LIST_HOT=1;
    
	public NewCommentListRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

	public void setParams(String url, int operflag, long last_sort_num, String srpid, String srpword, int type){
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("url", url);
        addParams("operflag", operflag+"");
        addParams("psize", 10 + "");
        addParams("last_sort_num", last_sort_num+"");
        addParams("srpword", srpword);
        addParams("srpid", srpid);
		/**
		 * type的数据类型在 video 中是没有用的 直接 传 1
		 */
        addParams("type", type+"");
        addParams("appname","souyue");
    }
	
	@Override
	public String getUrl() {
		return HOST + "interest/comment5.0.list.groovy";//5.0评论列表
	}

	@Override
	public Object doParse(CVolleyRequest request, String response)
			throws Exception {
		HttpJsonResponse res = (HttpJsonResponse) super.doParse(request,
				response);
		Object result = null;
		switch (request.getmId()) {
		case HttpCommon.DETAIL_COMMENT_NEW_LIST_ID:
			result = parseCommentList(res);
			break;
		default:
			return super.doParse(request, response);
		}
		return result;
	}

	private Object parseCommentList(HttpJsonResponse res) {

		List<CommentsForCircleAndNews> listLatest = new Gson().fromJson(res
				.getBody().getAsJsonArray("list"),
				new TypeToken<List<CommentsForCircleAndNews>>() {
				}.getType());
		List<CommentsForCircleAndNews> listHot = new Gson().fromJson(res
				.getBody().getAsJsonArray("hotlist"),
				new TypeToken<List<CommentsForCircleAndNews>>() {
				}.getType());
		List<Object> result = new ArrayList<Object>();
		result.add(DETAIL_COMMENT_LIST_COMMENT, listLatest);
		result.add(DETAIL_COMMENT_LIST_HOT, listHot);
		return result;

	}
}
