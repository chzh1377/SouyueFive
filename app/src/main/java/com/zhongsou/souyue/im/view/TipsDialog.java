package com.zhongsou.souyue.im.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * Created by zhou on 2015/9/10.
 */
public class TipsDialog extends ProgressDialog {
    private LinearLayout progressbar;
    private ImageView imageview;
    private TextView textview;

    public TipsDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_loading);
        progressbar = (LinearLayout) findViewById(R.id.loading_progress_bar);
        imageview = (ImageView) findViewById(R.id.loading_imageview);
        textview = (TextView) findViewById(R.id.loading_tip_txt);
    }

    /**
     * dialog
     * @param isShowProgress
     * @param imageId
     * @param text
     */
    public void initDialog(boolean isShowProgress,int imageId,String text){
        if(isShowProgress){
            progressbar.setVisibility(View.VISIBLE);
        }else{
            progressbar.setVisibility(View.GONE);
        }

        if(imageId==0){
            imageview.setVisibility(View.GONE);
        }else{
            imageview.setImageResource(imageId);
        }

        if(TextUtils.isEmpty(text)){
            textview.setVisibility(View.GONE);
        }else{
            textview.setText(text);
        }
    }

}
