package com.zhongsou.souyue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.zhongsou.souyue.R;
import com.zhongsou.souyue.adapter.baselistadapter.HomeListManager;
import com.zhongsou.souyue.bases.BaseActivity;
import com.zhongsou.souyue.countUtils.UmengStatisticEvent;
import com.zhongsou.souyue.countUtils.UmengStatisticUtil;
import com.zhongsou.souyue.fragment.SouYueNewsFragment;
import com.zhongsou.souyue.utils.IntentUtil;

public class YaoWenActivity extends BaseActivity {
    private SouYueNewsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yao_wen);
        fragment = new SouYueNewsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.channel_fragment_container, fragment);
        ft.commit();
        UmengStatisticUtil.onEvent(this, UmengStatisticEvent.NEWS_ACTIVITY);  //Umeng
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IntentUtil.REQUEST_CODE_CHANNEL_MANNGER||requestCode == HomeListManager.SHARE_TO_SSO_REQUEST_CODE){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
