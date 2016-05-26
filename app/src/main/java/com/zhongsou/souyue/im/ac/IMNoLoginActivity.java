package com.zhongsou.souyue.im.ac;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.utils.IntentUtil;

/**
 * Created by zoulu
 * on 14-8-28
 * Description:扫一扫未登录界面
 */
public class IMNoLoginActivity extends IMBaseActivity implements View.OnClickListener{
    private RelativeLayout re_cancle;
    private Button btn_my_login;
    private Button btn_my_reg;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.my_nologin);
        init();
    }

    private void init(){
        ((TextView)findViewById(R.id.tv_login_desc)).setText(String.format(CommonStringsApi.getStringResourceValue(R.string.my_login_desc_text), CommonStringsApi.APP_NAME_SHORT));
        re_cancle = (RelativeLayout) findViewById(R.id.re_cancle);
        re_cancle.setVisibility(View.VISIBLE);
        re_cancle.setOnClickListener(this);
        btn_my_login = (Button) findViewById(R.id.btn_my_login);
        btn_my_login.setOnClickListener(this);
        btn_my_reg = (Button) findViewById(R.id.btn_my_reg);
        btn_my_reg.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        IntentUtil.openMainActivity(this,3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.re_cancle:
                finish();
                break;
            case R.id.btn_my_login:
                IntentUtil.goLogin(this, true);
                break;
            case R.id.btn_my_reg:
                IntentUtil.toRegister(this);
                break;
            default:
                break;
        }
    }
}
