package com.zhongsou.souyue.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import com.umeng.analytics.MobclickAgent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.module.AppData;
import com.zhongsou.souyue.net.DeviceInfo;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.other.FirstGuideRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.net.volley.IVolleyResponse;

import java.util.List;

/**
 * Created by zyw on 2016/1/12.
 * 第一次启动的界面
 */
public class FirstInActivity001 extends Activity implements IVolleyResponse {

    private TextView tvTip;
    long mStartTime = 0;
    private static final long FIRST_WAIT_TIME = 2000;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobclickAgent.onError(this); //初始化友盟
        setContentView(R.layout.first_in_activity_layout);
        initView();
        mStartTime = SystemClock.uptimeMillis();
        initContext();
    }

    private void initView() {
        tvTip = (TextView) findViewById(R.id.loading_tip_txt);
    }

    private void initContext() {
        //如果已经有用户了，就订上吧
        uploadAppdata("");
    }


    @Override
    public void onHttpResponse(IRequest request) {
        HttpJsonResponse res = request.<HttpJsonResponse>getResponse();
        switch (request.getmId()) {
            case HttpCommon.SPLASH_FIRSTID_REQUESTID: // 第一次请求
                checkEnterHome();
                break;
        }
    }

    /**
     * 进入首页
     */
    private void entreHome() {
        //进去过之后，就不是第一次进去了
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void checkEnterHome(){
        long currentTime = SystemClock.uptimeMillis();
        if(currentTime  - mStartTime < FIRST_WAIT_TIME){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    entreHome();
                }
            },FIRST_WAIT_TIME - (currentTime  - mStartTime));
        }else{
            entreHome();
        }
    }
    /**
     * 这个方法用来上传一些私密的东西...
     */
    private void uploadAppdata(String token) {
        List<AppData> datas = DeviceInfo.getAppData(this);
        FirstGuideRequest.send(HttpCommon.SPLASH_FIRSTID_REQUESTID, this, token, datas);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        JPushInterface.onResume(this);  //JPush

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        JPushInterface.onPause(this);  //JPush
    }

    @Override
    public void onHttpError(IRequest request) {
        switch (request.getmId()) {
            case HttpCommon.SPLASH_FIRSTID_REQUESTID: // 第一次请求
                tvTip.setText("加载出错了...等会再来看看吧。");
                tvTip.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        overridePendingTransition(R.anim.anim_dispear, R.anim.anim_out_scale);
                        finish();
                    }
                }, 2000);
                break;
        }
    }

    @Override
    public void onHttpStart(IRequest request) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CMainHttp.getInstance().cancel(this);
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}