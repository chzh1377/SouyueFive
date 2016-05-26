package com.zhongsou.souyue.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhongsou.souyue.R;

public class AccountConflicDialog extends Dialog {

    private View.OnClickListener mClickListener;
    private TextView titleTv;
    private Button btn_logout;
    private Button btn_cancel;
    public AccountConflicDialog(Context context, View.OnClickListener cickListener) {
        super(context);
        mClickListener = cickListener;
    }

    public AccountConflicDialog(Context context, int theme, View.OnClickListener cickListener) {
        super(context, theme);
        mClickListener = cickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_dialog);

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AccountConflicDialog.this.dismiss();
            }
        });
        titleTv = (TextView) findViewById(R.id.title);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        if (mClickListener != null)
            findViewById(R.id.btn_logout).setOnClickListener(mClickListener);
    }
    public void setTitle(String titleString,String trueString ,String cancelString)
    {
        titleTv.setText(titleString);
        btn_logout.setText(trueString);
        btn_cancel.setText(cancelString);
    }
}
