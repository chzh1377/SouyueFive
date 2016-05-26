package com.zhongsou.souyue.net.volley;

import android.content.Context;
import com.google.gson.Gson;
import com.zhongsou.souyue.module.WXShareBean;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.service.download.Md5Util;
import com.zhongsou.souyue.share.ShareAppKeyUtils;
import com.zhongsou.souyue.utils.SYUserManager;

import java.util.Date;
import java.util.HashMap;

/**
 * create by zyw
 * me@raw.so
 * 微信红包dlg的http请求类
 */
public class CWXShareHttp extends AHttp {
//    private WXShareEnveDialog shareEnveDialog = null; // ->这个dlg
    private Context mContext;
    private Gson mGson; //gson.
    private HashMap<Integer, Boolean> mRunningTask; // 正在运行的实例，保证每次只有一个相同的方法请求,防止回调污染

    public static final int REQUEST_JF = 90001;

    public CWXShareHttp(Context _context) {
        super(_context, CWXShareHttp.class.getName());
        mContext = _context;
        mGson = new Gson();
        mRunningTask = new HashMap<Integer, Boolean>();
    }

    /**
     * 请求积分返回
     */
    public void doGetJF(int id, IVolleyResponse callback) {
        if (!isRunning(id)) {
            setRunning(id);
            String date = Long.toString(new Date().getTime());
            CVolleyRequest request = new CVolleyRequest();
            request.setmId(id);
            request.setmMethod(CVolleyRequest.REQUEST_METHOD_POST);
            String username = "";
            if(SYUserManager.getInstance().getUserType().equals(SYUserManager.USER_ADMIN)){
                username = SYUserManager.getInstance().getUserName();
            }
            request.addParams("username",username);
            request.addParams("time", date);
            request.addParams("sign", getSign(date,username));
            request.setParser(this);
            request.setCallBack(callback);
            request.setUrl(UrlConfig.getWXShareJFApi());
            mVolley.doRequest(request);
        }
    }


    /**
     * 在非ui线程中解析
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public Object doParse(CVolleyRequest request, String response) throws Exception {
        setFinished(request.getmId());
        if (response == null) {
            throw new Exception("response is null");
        }
        Object result = null;
        switch (request.getmId()) {
            case REQUEST_JF: // 进来之后，获取图集列表和推荐列表
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
}
