package com.zhongsou.souyue.utils;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HttpUtil {
	private static final String TAG = "HttpUtils";

	// android interactive with js, request server get json data
	public static String getUploadImageJsonByPost(String requestUrl, String img_url) {
    	String result = null;
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpPost httpPost = new HttpPost(requestUrl);
    	httpPost.setHeader("Accept", "application/json");

    	try {
    		List<NameValuePair> params = new ArrayList<NameValuePair>();
    		params.add(new BasicNameValuePair("img_url", img_url));
    		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
    		httpPost.setEntity(entity);
    		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1");
    		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
    		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			Log.d(TAG, "code="+httpResponse.getStatusLine().getStatusCode());
			if(httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			} else {
				Log.d(TAG, "request fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    	
    }
	
    public static String getImageUrlByParseJson(String jsonStr) {
    	String imageurl = null;
    	if (jsonStr != null) {
    		try {
    			JSONObject jsonObj = new JSONObject(jsonStr);
    			int head = jsonObj.getInt("head");
    			imageurl = jsonObj.getString("result");
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    	}
		return imageurl;
    }
}
