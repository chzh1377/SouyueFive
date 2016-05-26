package com.zhongsou.souyue.net.srp;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongsou.souyue.module.SpecialDialogData;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 专题推荐列表
 * @author chz
 *
 */
public class SpecialRecListRequest extends BaseUrlRequest {

	public SpecialRecListRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    public SpecialRecListRequest setParams(){
        addParams("token", SYUserManager.getInstance().getToken());
        return this;
    }
    
	@Override
	public String getUrl() {
		return HOST + "webdata/focus.recommend.groovy";
	}

    @Override
    public Object doParse(CVolleyRequest request, String response) throws Exception {
        HttpJsonResponse http = (HttpJsonResponse) super.doParse(request,response);
        if(http.getCode()==200){
            List<SpecialDialogData> specialData= new Gson().fromJson(http.getBodyArray(), new TypeToken<List<SpecialDialogData>>() {}.getType());
            return specialData;
        }
        return null;
    }
}
