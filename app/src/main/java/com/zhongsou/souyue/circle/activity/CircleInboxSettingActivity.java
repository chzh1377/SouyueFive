package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import com.google.gson.JsonObject;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.circle.view.CheckableImageView;
import com.zhongsou.souyue.net.HttpJsonResponse;
import com.zhongsou.souyue.net.circle.CircleFreetrainRequest;
import com.zhongsou.souyue.net.circle.CircleSetfreetrialRequest;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.HttpCommon;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;

/**
 * Created by wlong on 14-4-29.
 */
public class CircleInboxSettingActivity extends BaseActivity implements View.OnClickListener{

    private CheckableImageView userCommend, sysCommend;
//    private Http http;
    private CMainHttp mCMainhttp;

    private Checkable updateingCheckable;

    private long interest_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_activity_inbox_setting);
        initView();
        interest_id = getIntent().getLongExtra("interest_id", 0l);
//        http = new Http(this);
        mCMainhttp = CMainHttp.getInstance();

    }

    private void initView(){
        userCommend = (CheckableImageView) findViewById(R.id.civ_circle_inbox_setting_user_commend);
        sysCommend = (CheckableImageView) findViewById(R.id.civ_circle_inbox_setting_sys_commend);
        userCommend.setOnClickListener(this);
        sysCommend.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        CircleFreetrainRequest request = new CircleFreetrainRequest(HttpCommon.CIRCLE_FREETRAIN_REQUEST,this);
        request.addParams(interest_id);
        mCMainhttp.doRequest(request);

        //http.getFreetrialInfo(interest_id);
    }

    public void onBackPressClick(View view) {
        this.finish();
    }

    public void setFreetrialSuccess(HttpJsonResponse res) {
        if (updateingCheckable != null && res != null) {
              JsonObject jsonObject = res.getBody();
              boolean result = jsonObject.get("result").getAsBoolean();
              if (result) {
                updateingCheckable.toggle();
              } else {
                  // 设置失败
                  SouYueToast.makeText(this, "设置失败", SouYueToast.LENGTH_SHORT).show();
              }

        }
        if (updateingCheckable != null) {
            ((View)updateingCheckable).setClickable(true);
            updateingCheckable = null;
        }

    }
    public void getFreetrialInfoSuccess(HttpJsonResponse res){
        if (res != null) {
            JsonObject jsonObject = res.getBody();
            int sys_freetrial = jsonObject.get("sys_freetrial").getAsInt();
            int user_freetrial = jsonObject.get("user_freetrial").getAsInt();

            sysCommend.setChecked(sys_freetrial == 1);
            userCommend.setChecked(user_freetrial == 1);
        }
    }

    @Override
    public void onClick(View v) {

        int status = 0;
        int oper_type = 0;
        Checkable checkable = null;
        if (v instanceof Checkable) {
            checkable = (Checkable)v;
            updateingCheckable = checkable;
            status = (!checkable.isChecked())? 1: 0;
        }
        switch (v.getId()) {
            case R.id.civ_circle_inbox_setting_user_commend:
                oper_type = 2;
                break;
            case R.id.civ_circle_inbox_setting_sys_commend:
                oper_type = 1;
                break;
        }

        if (oper_type != 0) {
            v.setClickable(false);
            //TODO 请求设置免审

            CircleSetfreetrialRequest req = new CircleSetfreetrialRequest(HttpCommon.CIRCLE_SETFREE_REQUEST,this);
            req.addParams(interest_id,oper_type,status);
            mCMainhttp.doRequest(req);

           // http.setFreetrial(interest_id, oper_type, status);

        } else {
            if (checkable != null)
                checkable.setChecked(!checkable.isChecked());
        }
    }


    @Override
    public void onHttpResponse(IRequest request) {

        super.onHttpResponse(request);
        HttpJsonResponse response = request.getResponse();
        switch (request.getmId()){
            case HttpCommon.CIRCLE_FREETRAIN_REQUEST:
                getFreetrialInfoSuccess(response);
                break;

            case HttpCommon.CIRCLE_SETFREE_REQUEST:
                setFreetrialSuccess(response);
                break;
        }
    }

    @Override
    public void onHttpError(IRequest request) {
        super.onHttpError(request);
        switch (request.getmId()){
            case HttpCommon.CIRCLE_SETFREE_REQUEST:
                SouYueToast.makeText(this, "设置失败", SouYueToast.LENGTH_SHORT).show();
                break;
        }
    }
}
