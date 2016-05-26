package com.zhongsou.souyue.circle.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.circle.fragment.EssencePostFragment;

/**
 * @ClassName: EssencePostActivity 
 * @Description: 兴趣圈精华帖列表
 * @author gengsong@zhongsou.com
 * @date 2014年4月22日 下午2:11:26 
 * @version V3.8
 */
@SuppressWarnings("unused")
public class EssencePostActivity extends RightSwipeActivity implements OnClickListener {

	private EssencePostFragment mEssencePostFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_essencepost_layout);
		mEssencePostFragment = (EssencePostFragment)getSupportFragmentManager().findFragmentById(R.id.circle_essencepost);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		super.onBackPressClick(v);
	}
}