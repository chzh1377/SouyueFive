package com.zhongsou.souyue.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.bases.RightSwipeActivity;
import com.zhongsou.souyue.fragment.FavoriteFragment;
/**
 * @ClassName: MyFavoriteActivity 
 * @Description: 我的收藏列表
 * @date 2014年6月25日 下午2:26:46 
 * @version 4.0
 */
public class MyFavoriteActivity extends RightSwipeActivity implements OnClickListener {

	private FavoriteFragment favoriteFrag;
    public static final String FAVORITE_ACTION = "favorite_refresh_action";//刷新收藏列表action
    
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.my_favorite);
		favoriteFrag = (FavoriteFragment)getSupportFragmentManager().findFragmentById(R.id.my_favorite);
		favoriteFrag.showBack();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(FAVORITE_ACTION);
		registerReceiver(favRefreshReceiver, filter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		super.onBackPressClick(v);
	}
	/**
	 * 接受收藏状态改变广播，刷新列表
	 */
	private BroadcastReceiver favRefreshReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (FAVORITE_ACTION.equals(action)) {
				if(null != favoriteFrag) {
					favoriteFrag.clickRefresh();
				}
			}
		}
	};
	
	protected void onDestroy() {
		try {
			if(null != favRefreshReceiver) {
				unregisterReceiver(favRefreshReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
}
