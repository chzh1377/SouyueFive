/***
 Copyright (c) 2009
 Author: Stefan Klumpp <stefan.klumpp@gmail.com>
 Web: http://stefanklumpp.com

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.zhongsou.souyue.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelfCreateUploadHttp {
	private static String AGENT = "Android";
	
    public static HttpJsonResponse doPost(String url, Map<String, String> params) {
        if (!CMainHttp.getInstance().isNetworkAvailable(MainApplication.getInstance()))
            return null;
        try {
            HttpPost post = new HttpPost(url);
//			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//			String value = null;
//			
//			for (Map.Entry<String, String> e : params.entrySet()) {
//				value = e.getValue();
//				if (value != null) {
//					pairs.add(new BasicNameValuePair(e.getKey(), value.toString()));
//				}
//			}
            
            post.addHeader("User-Agent", AGENT);
            
            List<BasicNameValuePair> pairs = Utils.encryptToPostEntity(url, params, null);
            post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            HttpResponse resp = new DefaultHttpClient().execute(post);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(resp.getEntity());
                HttpJsonResponse json = new HttpJsonResponse((JsonObject) new JsonParser().parse(result));
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加用户原创内容 md5 //微件对应srpid column_name //栏目名 column_type //微件类型 title
     * //原创标题 content //原创内容 conpic //内容图片(若有多个图片空格隔开共同存储)
     */
    public static HttpJsonResponse addSelfCreate(String token, String keyword, String srpId, //
                                                 String md5, String column_name, String column_type, String title, //
                                                 String content, String conpic) {//
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("keyword", keyword);
        map.put("srpId", srpId);
        map.put("md5", md5);
        map.put("column_name", column_name);
        map.put("column_type", column_type);
        map.put("title", title);
        map.put("content", content);
        map.put("conpic", conpic);
        return doPost(UrlConfig.selfCreateAdd, map);
    }

    /**
     * 修改原创内容
     * token 用户登录token
     * keyword 关键词
     * srpId 关键词对应srpid
     * md5 微件对应srpid
     * column_name 栏目名
     *  column_type 微件类型
     *  title 原创标题
     *  content 原创内容
     *  conpic 内容图片(若有多个图片空格隔开共同存储)
     *  id 数据库id
     */
/*
    public static boolean updateSelfCreate(String token, String keyword, String srpId, //
			String md5, String column_name, String column_type, String title,//
			String content, String conpic, String id) {//
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("keyword", keyword);
		map.put("srpId", srpId);
		map.put("md5", md5);
		map.put("column_name", column_name);
		map.put("column_type", column_type);
		map.put("title", title);
		map.put("content", content);
		map.put("conpic", conpic);
		map.put("id", id);
		return doPost(UrlConfig.selfCreateUpdate, map);
	}
*/

}