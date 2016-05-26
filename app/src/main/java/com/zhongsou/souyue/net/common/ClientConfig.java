package com.zhongsou.souyue.net.common;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zhongsou.souyue.db.HomePageDBHelper;
import com.zhongsou.souyue.db.SuberDaoImp;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.utils.CacheUtils;
import com.zhongsou.souyue.utils.PushUtils;

/**
 * @description: 客户端配置
 * @auther: qubian
 * @data: 2016/1/14.
 */

public class ClientConfig {
    private int OPEN = 1;//打开，启动  极光 小米 默认打开
    private int CLOSE = 0;//关闭， 清理记录默认关闭
    private int jgPushOpen=OPEN;
    private int xmPushOpen=OPEN;
    private int cleanNewsList=CLOSE;
    private int cleanBrowserCache=CLOSE;

    public  ClientConfig(Context context,HttpJsonResponse response)
    {
        try
        {
            JsonObject obj = response.getBody();
            if(obj!=null)
            {
                jgPushOpen = getInt(obj,"jgPushOpen",OPEN);
                xmPushOpen = getInt(obj,"xmPushOpen",OPEN);
                cleanNewsList = getInt(obj,"cleanNewsList",CLOSE);
                cleanBrowserCache = getInt(obj,"cleanBrowserCache",CLOSE);
            }
//            Log.i("AAAA", toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            PushUtils.saveIsOpenJPush(context, jgPushOpen == OPEN);
            PushUtils.saveIsOpenXiaoMiPush(context, xmPushOpen == OPEN);
            if (cleanNewsList == OPEN) {
                CMainHttp.getInstance().clearCache();//清除所有http請求緩存
                new SuberDaoImp().clearAll();//清除所有訂閱球球本地數據庫緩存
                HomePageDBHelper.getInstance().deleteAll();//清除所有首頁增量列表數據庫緩存
            } else if (cleanBrowserCache == OPEN) {
                CacheUtils.clearWebViewCache();
            }
        }

    }

    private int getInt(JsonObject obj ,String string,int defaultValue)
    {
        JsonElement b = obj.get(string);
        int s = defaultValue;
        if (b != null)
        {
            s = b.getAsInt();
        }
        return s;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "jgPushOpen=" + jgPushOpen +
                ", xmPushOpen=" + xmPushOpen +
                ", cleanNewsList=" + cleanNewsList +
                ", cleanBrowserCache=" + cleanBrowserCache +
                '}';
    }
}
