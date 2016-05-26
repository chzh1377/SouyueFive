package com.zhongsou.souyue.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class SubscribeDialog extends ProgressDialog {
	private LinearLayout progressbar;
	private ImageView imageview;
	private TextView textview;

	public SubscribeDialog(Context context) {
		super(context);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe_loading);
		progressbar = (LinearLayout) findViewById(R.id.subcribe_loading_progress_bar);
		imageview = (ImageView) findViewById(R.id.subcribe_loading_imageview);
		textview = (TextView) findViewById(R.id.subcribe_loading_tip_txt);
	}

	// 订阅
	public void subscribe() {
		progressbar.setVisibility(View.GONE);
		imageview.setImageResource(R.drawable.subscribe);
		imageview.setVisibility(View.VISIBLE);
		textview.setText(R.string.subscribe_dialog);
		progressbar.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SubscribeDialog.this.dismiss();
			}
		}, 1000);
	}

	// 退订
	public void unsubscribe() {
		progressbar.setVisibility(View.GONE);
		imageview.setImageResource(R.drawable.unsubscribe);
		imageview.setVisibility(View.VISIBLE);
		textview.setText(R.string.unsubscribe_dialog);
		progressbar.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SubscribeDialog.this.dismiss();
			}
		}, 1000);

	}

	// 加载中
	public void progress() {
		progressbar.setVisibility(View.VISIBLE);
		imageview.setVisibility(View.GONE);
		textview.setText(R.string.subscribe_dialog_progress);
	}

	// 操作失败
	public void subscribefail() {
		progressbar.setVisibility(View.GONE);
		imageview.setImageResource(R.drawable.subscribe_fail);
		imageview.setVisibility(View.VISIBLE);
		textview.setText(R.string.subscribe_dialog_fail);
	}

}
