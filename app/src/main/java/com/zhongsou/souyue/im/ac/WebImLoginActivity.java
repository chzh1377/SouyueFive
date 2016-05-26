package com.zhongsou.souyue.im.ac;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.tuita.sdk.ContextUtil;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.ScaningActivity;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.im.dialog.ImDialog;
import com.zhongsou.souyue.im.net.CWebImLoginHttp;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.im.view.TipsDialog;
import com.zhongsou.souyue.net.volley.CMainHttp;
import com.zhongsou.souyue.net.volley.IHttpError;
import com.zhongsou.souyue.net.volley.IRequest;
import com.zhongsou.souyue.ui.SouYueToast;
import com.zhongsou.souyue.utils.IntentUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhou on 2015/9/7.
 */
public class WebImLoginActivity extends BaseActivity implements View.OnClickListener {
    private Button btnWebimLogin,btnWebimCancel;
    protected CWebImLoginHttp mVolleyHttp;
    public static final String EXTRA_UUID = "UUID";
    public static final String EXTRA_URL = "URL";
    private static final int WHAT_SUCCESS = 1;
    private TipsDialog dialog;

    public final int STATUS_CODE_0 = 0;
    public final int STATUS_CODE_1 = 1;
    public final int STATUS_CODE_2 = 2;
    public final int STATUS_CODE_3 = 3;
    public final int STATUS_CODE_4 = 4;
    public final int STATUS_CODE_5 = 5;
    public final int STATUS_CODE_6 = 6;
    public final int STATUS_CODE_7 = 7;
    public final int STATUS_CODE_8 = 8;
    public final int STATUS_CODE_100 = 100;

    private String uuid;
    private String url;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.webim_login_layout);
        handleIntent(getIntent());
        init();
        initEvent();
    }

    private void init(){
        btnWebimLogin = (Button)findViewById(R.id.btn_webim_login);
        btnWebimCancel = (Button)findViewById(R.id.btn_webim_cancel);
        mVolleyHttp = new CWebImLoginHttp(this);
        dialog = new TipsDialog(this);
    }

    private void initEvent(){
        btnWebimLogin.setOnClickListener(this);
        btnWebimCancel.setOnClickListener(this);
    }

    private void handleIntent(Intent intent){
        uuid = intent.getStringExtra(EXTRA_UUID);
        url = intent.getStringExtra(EXTRA_URL);
    }

    @Override
    public void onHttpResponse(IRequest _request) {
        dialog.dismiss();
        JSONObject obj = _request.getResponse();
        int status = 0;
        String msg;
        try {
            status = obj.getInt("status");
            msg = obj.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch(status){
            case STATUS_CODE_0:
                SouYueToast.makeText(this, getString(R.string.webim_login_success), SouYueToast.LENGTH_SHORT).show();
                finish();
                break;
            case STATUS_CODE_1://二维码过期
                ImDialog.Builder.ImDialogInterface imDialogInterface= new ImDialog.Builder.ImDialogInterface(){

                    @Override
                    public void onClick(DialogInterface dialog, View v) {
                        IntentUtil.startActivityWithAnim(WebImLoginActivity.this, "", ScaningActivity.class);
                        finish();
                    }
                };

                showDialog(getString(R.string.webim_qrcode_invalid),getString(R.string.webim_qrcode_rescan),getString(R.string.webim_qrcode_scan),imDialogInterface).show();
                break;
            case STATUS_CODE_2:
                SouYueToast.makeText(this, getString(R.string.webim_server_timeout), SouYueToast.LENGTH_LONG).show();break;
            case STATUS_CODE_3:
                SouYueToast.makeText(this,getString(R.string.webim_login_overtime), SouYueToast.LENGTH_SHORT).show();break;
            case STATUS_CODE_4:
            case STATUS_CODE_5:
            case STATUS_CODE_6:
            case STATUS_CODE_7:
            case STATUS_CODE_100:
                SouYueToast.makeText(this,getString(R.string.webim_login_failer),SouYueToast.LENGTH_SHORT).show()
                ;break;
            case STATUS_CODE_8:
                SouYueToast.makeText(this,getString(R.string.webim_login_unknown),SouYueToast.LENGTH_SHORT).show()
                ;break;
            default:
                SouYueToast.makeText(this,getString(R.string.webim_login_failer),SouYueToast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * 当请求错误时显示提示
     * @param title
     * @param message
     * @param buttonText
     * @param imDialogInterface
     * @return
     */
    private ImDialog showDialog(String title,String message,String buttonText,ImDialog.Builder.ImDialogInterface imDialogInterface){
        ImDialog.Builder builder=new ImDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton(buttonText,imDialogInterface).setNegativeButton("确定",null);
        ImDialog imDialog = builder.create();
        imDialog.setCancelable(false);
        return imDialog;
    }

    @Override
    public void onHttpError(IRequest _request) {
        super.onHttpError(_request);
        dialog.dismiss();
        IHttpError error = _request.getVolleyError();
        if(error.getErrorType()==2){
            SouYueToast.makeText(this,getString(R.string.webim_server_timeout),SouYueToast.LENGTH_SHORT).show();
        }else{
            SouYueToast.makeText(this,getString(R.string.webim_server_timeout),SouYueToast.LENGTH_SHORT).show();
        }
    }

    public static void invoke(Context context,String uuid,String url){
        Intent intent = new Intent(context,WebImLoginActivity.class);
        intent.putExtra(EXTRA_UUID,uuid);
        intent.putExtra(EXTRA_URL,url);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_webim_login:
                if(!CMainHttp.getInstance().isNetworkAvailable(this)){
                    SouYueToast.makeText(this,getString(R.string.webim_request_error),SouYueToast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray jsonArray = ImserviceHelp.getInstance().db_getMsgRecentList();
                Log.i(this.getClass().getName(), "--->" + jsonArray.toString());
                if(!TextUtils.isEmpty(url)){
                    mVolleyHttp.doWebImLogin(url, CWebImLoginHttp.HTTP_REQUEST_WEBLOGIN_MODULE, uuid, ContextUtil.getAppId(this), jsonArray.toString(), this);
                }else{
                    SouYueToast.makeText(this, getString(R.string.webim_refresh_rescan), SouYueToast.LENGTH_LONG).show();break;
                }
                dialog.show();
                dialog.setCancelable(false);
                dialog.initDialog(true, 0, null);
                ;break;
            case R.id.btn_webim_cancel:
                finish();
                break;
            default:;break;
        }
    }


    /**
     * 点击返回按钮
     * @param v
     */
    public void onGoBackClick(View v){
        finish();
    }

    @Override
    protected void onDestroy() {
        mVolleyHttp.cancelAll();
        super.onDestroy();
    }
}
