package com.zhongsou.souyue.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * Created by zhou on 2015/12/24.
 */
public class ImRequestDialog extends ProgressDialog {
    private LinearLayout progressbar;
    private ImageView imageview;
    private TextView textview;

    public ImRequestDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_loading);
        progressbar = (LinearLayout) findViewById(R.id.loading_progress_bar);
        imageview = (ImageView) findViewById(R.id.loading_imageview);
        textview = (TextView) findViewById(R.id.loading_tip_txt);
    }

    public void mDismissDialog(){
        if (isShowing()==true){
            dismiss();
        }
    }

    public void setTipText(String str) {
        textview.setText(str);
    }
}