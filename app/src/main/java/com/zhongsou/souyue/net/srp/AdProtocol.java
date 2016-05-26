package com.zhongsou.souyue.net.srp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.net.volley.BaseUrlRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;
import com.zhongsou.souyue.utils.StringUtils;
import com.zhongsou.souyue.utils.Utils;

/**
 * 广告协议
 * @author chz
 *
 */
public class AdProtocol extends BaseUrlRequest {
	
    protected Map<String, String> headers;
    public static Random random = new Random();
	public static final String POST_HEADER_ACCEPT_SIGN = "Accept-Sign";
	public static final String POST_HEADER_REFERER = "Referer";
	public static final String AGENT = "User-Agent";
	
	public AdProtocol(int id,IVolleyResponse response) {
		super(id, response);
	}

	
	/**
     * 广告Post请求
     * @param params
     * @return
     */
    protected void adDoPost(Map<String, String> params,String source) {
    	String key = getSouyueADHost();//app_secrect（仅限预上线环境与线上环境修改为：!zs-ad@#ll!~@#，测试环境app_secrect不变，仍为1）
//        Map<String, Object> paramsEntity = new HashMap<String, Object>();
        String agent = "";
        try {
        	//设置请求头信息
        	Object object = params.get("app_name");
        	String appName = "";
        	if(object != null) {
        		appName = (String)object;
        	}
        	setPostHeader(POST_HEADER_ACCEPT_SIGN, generateAcceptSign(params, key));
        	setPostHeader(POST_HEADER_REFERER, generateReference(source,appName));
        	//body
//        	params.put(Constants.POST_ENTITY, new StringEntity(generateAdHttpBody(params), "UTF-8"));
        	addParams(POST_ENTITY, generateAdHttpBody(params));
            //User_Agent
            Object appid = params.get("app_id");
            if(appid != null) {
            	String idStr = (String) appid;
            	if(idStr.equals(ConfigApi.getSouyuePlatform())) {
            		agent = "souyue";
            	}else if(idStr.equals(ConfigApi.getSouyuePlatform())){
            		agent = "superapp";
            	}
            	setPostHeader(AGENT, agent);
            }
			setProcessStragegy(REQUEST_ADDPARAMS,false);
			setProcessStragegy(REQUEST_ALREADY_ENTITY,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Map<String, String> setPostHeader(String name,String value) {
    	if(headers == null) {
    		headers = new HashMap<String, String>();
    	}
    	headers.put(name, value);
        return headers;
    }
    
    /**
     * 生成广告请求的HTTP头的AcceptSign
     * 1.sign的生成规则为：$requestJson.$appSecrect后的md5数值
     * @return
     */
    private String generateAcceptSign(Map<String, String> requestJson,String appSecret) {
    	String reqJson = null;
    	String acceptSign = "";
    	
    	try {
    		if(requestJson != null) {
//    			reqJson = com.alibaba.fastjson.JSONObject.toJSON(requestJson).toString();
    			reqJson = new Gson().toJson(requestJson);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	
    	if(reqJson != null) {
    		String reqHeaderStr = reqJson + appSecret;
    		String reqHeaderMD5 = Utils.Md5(reqHeaderStr);
    		return reqHeaderMD5;
    	}
    	
    	return null;
    }

    private String generateAdHttpBody(Map<String, String> requestJson) {
    	
    	String reqJson = null;
    	
    	try {
    		if(requestJson != null) {
//    			reqJson = com.alibaba.fastjson.JSONObject.toJSON(requestJson).toString();
    			reqJson  = new Gson().toJson(requestJson);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	if(reqJson != null) {
    		
			String httpBody = Utils.encrypt(encode(reqJson));
	
	        int interferonLength = 8;
	        String httpBodyMd5 = Utils.Md5(httpBody);
	        int bodyLength	= httpBody.length();
	        		
	        int index = 1;
			do {
				int subIndex = (int) Math.pow(2, index);
				if (subIndex < bodyLength) {
					httpBody = httpBody.substring(0, subIndex) + httpBodyMd5.charAt(random.nextInt(31)) + httpBody.substring(subIndex);
				} else {
					break;
				}
				index++;
			} while (index <= interferonLength);
			
			index -= 1;
			
			httpBody = String.valueOf(index).length() + String.valueOf(index) + httpBody;
			
			httpBody = encode(httpBody);
			
			return httpBody;
    	}
    	return "";
    }
    /**
     * 生成Referer
     * @return
     */
	private String generateReference(String source, String appName) {
		if (StringUtils.isEmpty(source)) {
			String currSecond = String.valueOf(System.currentTimeMillis());
			return "custom://app=" + encode(appName) + "&localtion=1&dt="
					+ currSecond.substring(0, currSecond.length() - 3);
		}

		return encode(source);

	}

	/**
	 * 编码
	 * 
	 * @param str
	 * @return
	 */
	private String encode(Object str) {
		try {
			return (str == null || str.toString().trim() == "") ? ""
					: URLEncoder.encode(str.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str.toString();
	}


	@Override
	public String getUrl() {
		return HOST;
	}
	
	@Override
	public int getMethod() {
		return REQUEST_METHOD_POST;
	}
	
	@Override
	public Map<String, String> getRequestHeader() {
		return headers;
	}
	
	//4.2.2升级广告接口
	@Override
	public String getSouyueHost() {
		int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
		switch (env) {
		case SOUYUE_TEST:
			// 测试环境域名： 202.108.1.109 api.ad.zhongsou.com
			return "http://api.adtest.zhongsou.com/app/get.json";
		case SOUYUE_PRE_ONLINE:
			// 预上线环境域名： 61.135.210.44 api.ad.zhongsou.com
			return "http://api.ad.zhongsou.com/app/get.json";
		case SOUYUE_PRE_ONLINE_FOR_SRP:
			return "http://api.ad.zhongsou.com/app/get.json";
		case SOUYUE_ONLINE:
			return "http://api.ad.zhongsou.com/app/get.json";
		default:
			return "http://api.ad.zhongsou.com/app/get.json";
		}
	}
	
    /**
     * 广告秘钥
     *
     * @return String key = "!zs-ad@#ll!~@#";//app_secrect（仅限预上线环境与线上环境修改为：!zs-ad@#ll!~@#，测试环境app_secrect不变，仍为1）
     */
    public String getSouyueADHost() {
		int env = Integer.parseInt(CommonStringsApi.getStringResourceValue(R.string.souyue_interface_env));
        switch (env) {
            case SOUYUE_TEST:
                return "1";
            case SOUYUE_PRE_ONLINE:
            case SOUYUE_ONLINE:
                return "!zs-ad@#ll!~@#";
            default:
                return "!zs-ad@#ll!~@#";
        }
    }
}
