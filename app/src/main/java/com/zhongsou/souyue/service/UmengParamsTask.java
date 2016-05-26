package com.zhongsou.souyue.service;

import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.MainApplication;
import com.zhongsou.souyue.utils.SYSharedPreferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 获取友盟配置参数
 * 
 * @author iamzl
 * 
 */
public class UmengParamsTask extends ZSAsyncTask<String, Void, Map<String, String>> {
    private Map<String, String> result = new HashMap<String, String>();

    @Override
    protected Map<String, String> doInBackground(String... params) {
        if (params != null) {
            for (String key : params) {
                String value = MobclickAgent.getConfigParams(MainApplication.getInstance(), key);//得到在线参数的数值
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {//UI层处理方式
        Iterator<Map.Entry<String, String>> iter = result.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>)iter.next();
            SYSharedPreferences.getInstance().putString(entry.getKey(), entry.getValue());
        }
    }
}
