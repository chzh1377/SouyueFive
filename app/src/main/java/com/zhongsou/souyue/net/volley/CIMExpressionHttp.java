package com.zhongsou.souyue.net.volley;

import android.content.Context;

/**
 * 表情相关Http
 */
public class CIMExpressionHttp extends AHttp {

	public static final int IM_EXPRESSIONLIST_METHOD = 0x001;
	public static final int IM_EXPRESSIONDETAIL_METHOD = 0x002;

	public CIMExpressionHttp(Context _context) {
		super(_context, CSuberListHttp.class.getName());
	}

//	public void getExpressionList(int id, String token, long minSortNo,
//			int pageSize, IVolleyResponse callback) {
//		CVolleyRequest request = new CVolleyRequest();
//		request.setmId(id);
//		request.addParams("token", token);
//		request.addParams("minSortNo", minSortNo + "");
//		request.addParams("pageSize", pageSize + "");
//		request.setUrl(UrlConfig.getExpressionListUrl);
//		request.setCallBack(callback);
//		mVolley.doRequest(request);
//	}
//
//	public void getExpressionDetail(int id, String token, String vc,
//			String packageId, IVolleyResponse callback) {
//		CVolleyRequest request = new CVolleyRequest();
//		request.setmId(id);
//		request.addParams("token", token);
//		request.addParams("vc", vc);
//		request.addParams("id", packageId);
//		request.setUrl(UrlConfig.getExpressionDetailUrl);
//		request.setCallBack(callback);
//		mVolley.doRequest(request);
//	}
}
