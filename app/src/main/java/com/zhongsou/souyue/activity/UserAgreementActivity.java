package com.zhongsou.souyue.activity;

import android.os.Bundle;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.platform.CommonStringsApi;
import com.zhongsou.souyue.platform.ConfigApi;

public class UserAgreementActivity extends RightSwipeActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agreement);
		String title = this.getResources().getString(R.string.userAgreementActivity_user_agreement);
        ((TextView)findViewById(R.id.activity_bar_title)).setText(title);
        if(!ConfigApi.isSouyue()){
        	 String content=this.getResources().getString(R.string.disclaimer_context);
             String tmpStr=content.replaceAll("搜悦", CommonStringsApi.LOCAL_APP);
              	  
             ((TextView)findViewById(R.id.about_content)).setText(tmpStr);
        }
		this.setCanRightSwipe(true);
	}
}