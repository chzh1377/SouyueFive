package com.zhongsou.souyue.net.detail;

import java.util.Date;
import java.util.HashMap;

import android.content.Context;

import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.WXShareBean;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.CVolleyRequest;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.service.download.Md5Util;
import com.zhongsou.souyue.share.ShareAppKeyUtils;
import com.zhongsou.souyue.utils.SYUserManager;
/**
 * 微信分享领红包 dialog data req
 * @author chz
 */
public class WXShareRequest extends BaseUrlRequest {
	
    //红包接口 ,领取红包
    public static final String WXSHARE_REQUEST_DEBUG = "http://jftest.zhongsou.com/";
    public static final String WXSHARE_REQUEST_PRE_ONLINE = "http://jfpre.zhongsou.com/";
    public static final String WXSHARE_REQUEST_ONLINE = "http://jf.zhongsou.com/";
    
    private Gson mGson; //gson.
    private HashMap<Integer, Boolean> mRunningTask; // 正在运行的实例，保证每次只有一个相同的方法请求,防止回调污染
    
	public WXShareRequest(int id, IVolleyResponse response) {
		super(id, response);
		mGson = new Gson();
		mRunningTask = new HashMap<Integer, Boolean>();
	}
	
	public void setParams() {
		if (!isRunning(HttpCommon.DETAIL_WX_SHARE_JF_ID)) {
			setRunning(HttpCommon.DETAIL_WX_SHARE_JF_ID);
			String date = Long.toString(new Date().getTime());
			String username = "";
			if (SYUserManager.getInstance().getUserType()
					.equals(SYUserManager.USER_ADMIN)) {
				username = SYUserManager.getInstance().getUserName();
			}
			addParams("username", username);
			addParams("time", date);
			addParams("sign", getSign(date, username));
		}
	}
	
	@Override
	public String getUrl() {
		return HOST;
	}

	public Object doParse(CVolleyRequest request, String response)
			throws Exception {
		setFinished(request.getmId());
		if (response == null) {
			throw new Exception("response is null");
		}
		Object result = null;
		switch (request.getmId()) {
		case HttpCommon.DETAIL_WX_SHARE_JF_ID: // 进来之后，获取图集列表和推荐列表
			result = parseShareBean(response);
			break;
		default:
			return response;
		}
		return result;
	}

	/**
	 * 解析微信红包接口
	 * 
	 * @param response
	 * @return
	 */
	private WXShareBean parseShareBean(String response) {
		return mGson.fromJson(response, WXShareBean.class);
	}
	
    /**
     * 判断任务的执行状态 ,防止返回值被污染。共需要三个方法
     * 共同作用。
     *
     * @param taskId
     * @returnW
     */
    public boolean isRunning(int taskId) {
        if (mRunningTask.containsKey(taskId))
            return mRunningTask.get(taskId);
        mRunningTask.put(taskId, false);
        return false;
    }

    /**
     * 如果当前的任务正在执行，就完成任务，否则就无法完成任务
     *
     * @param taskId
     * @return
     */
    public boolean setFinished(int taskId) {
        if (isRunning(taskId)) {
            mRunningTask.put(taskId, false);
            return true;
        }
        return false;
    }

    public void setRunning(int taskId) {
        mRunningTask.put(taskId, true);
    }

    //其他私有方法

    /**
     * 获取微信红包领取接口的 sign
     * sign加密规则
     * MD5（appkey+username+time）
     * appkey为积分平台分配的key：
     * f1f90d6f5f409f992ad7ade0c0ee3b06
     *
     * @return
     */
    public String getSign(String time,String username) {
        String key = ShareAppKeyUtils.JF_APP_KEY;
        return Md5Util.getMD5Str(key + username + time);
    }
    
    @Override
    public int getMethod() {
    	return REQUEST_METHOD_POST;
    }
    
    // 积分 目前只有一个环境
    @Override
	public String getSouyueHost() {
    	return getWXShareJFApi();
    }
    
    public String getWXShareJFApi() {
    	int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        String apiurl = "";
        switch (env) {
            //测试环境
            case SOUYUE_TEST:
                apiurl = WXSHARE_REQUEST_DEBUG;
                break;
            //预上线环境
            case SOUYUE_PRE_ONLINE:
                apiurl = WXSHARE_REQUEST_PRE_ONLINE;
                break;
            //搜索预上线
            case SOUYUE_PRE_ONLINE_FOR_SRP:
                apiurl = WXSHARE_REQUEST_DEBUG;
                break;
            //线上环境
            case SOUYUE_ONLINE:
                apiurl = WXSHARE_REQUEST_ONLINE;
                break;
            //开发环境
            case SOUYUE_DEVLOPER:
                apiurl = WXSHARE_REQUEST_DEBUG;
                break;
        }
        return apiurl + "redback/getUserInfo";
    }


}
