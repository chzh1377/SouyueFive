package com.zhongsou.souyue.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class SYProgressDialog extends ProgressDialog {

	private String toash;

	public SYProgressDialog(Context context) {
		super(context);
	}

	public SYProgressDialog(Context context, int theme) {
		super(context, R.style.Theme_Dialog_Alert);
	}
	public SYProgressDialog(Context context, int theme,String toash) {
		super(context, R.style.Theme_Dialog_Alert);
		this.toash = toash;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog);
		TextView t = (TextView) findViewById(R.id.message);
		t.setText(toash);
	}

	@Override
	public void cancel() {
		super.cancel();
	}
}
