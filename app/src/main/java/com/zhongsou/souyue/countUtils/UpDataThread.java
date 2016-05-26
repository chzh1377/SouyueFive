package com.zhongsou.souyue.countUtils;

import com.zhongsou.souyue.net.UrlConfig;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
/** 
 * Description: 函数过多导致android无法打包，删掉commons-httpclient-3.0.1.jar，使用android自带httpclient重构代码 
 * Company:     ZhongSou.com<br/> 
 * Copyright:   2003-2014 ZhongSou All right reserved<br/> 
 * @date        2014-10-22 下午3:35:37
 * @author      liudl
 */  
public class UpDataThread {
    public void send(final HttpCallBack callBack, final String jsonStr) {
        new Thread() {
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(UrlConfig.sendCountUrl());
                    ByteArrayEntity entity = new ByteArrayEntity(GZIPCompression.compress(jsonStr.getBytes())); 
                    httpPost.setEntity(entity);
                    httpPost.setHeader("Content-Type", "application/octet-stream");
                    HttpResponse response = client.execute(httpPost);
                    
                    int responseCode = response.getStatusLine().getStatusCode();
                    if (responseCode == HttpStatus.SC_OK) {
                        String result = EntityUtils.toString(response.getEntity());
                        callBack.onSuccess(result);
                    } else {
                        callBack.onTaskError(String.valueOf(responseCode));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
