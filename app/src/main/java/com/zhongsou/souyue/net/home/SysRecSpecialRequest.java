package com.zhongsou.souyue.net.home;

import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 系统推荐（是否展示系统推荐数据）
 * @author chz
 *
 */
public class SysRecSpecialRequest extends BaseUrlRequest {

	public SysRecSpecialRequest(int id, IVolleyResponse response) {
		super(id, response);
	}

    /**
     * 是否展示系统推荐数据
     */
    public void setParams(String state){
        addParams("token", SYUserManager.getInstance().getToken());
        addParams("state", state);
    }
	@Override
	public String getUrl() {
		return HOST + "user/recommendSpecialSwitch.groovy";
	}

	@Override
	public boolean isForceRefresh() {
		return true;
	}
}
