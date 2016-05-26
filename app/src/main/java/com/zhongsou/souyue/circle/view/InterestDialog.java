package com.zhongsou.souyue.circle.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;

public class InterestDialog extends ProgressDialog{
    private LinearLayout progressbar;
    private ImageView imageview;
    private TextView  textview;
    public InterestDialog(Context context) {
        super(context);
        
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_loading);
        progressbar=(LinearLayout) findViewById(R.id.subcribe_loading_progress_bar);
        imageview=(ImageView) findViewById(R.id.subcribe_loading_imageview);
        textview=(TextView) findViewById(R.id.subcribe_loading_tip_txt);
        setCanceledOnTouchOutside(false);
    }
    //订阅
    public void subscribe(){
        progressbar.setVisibility(View.GONE);
        imageview.setImageResource(R.drawable.subscribe);
        imageview.setVisibility(View.VISIBLE);
        textview.setText("已订阅");
        progressbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(InterestDialog.this!=null&&InterestDialog.this.isShowing()) {
                    InterestDialog.this.dismiss();
                }
            }
        }, 1000);

    }
    
    public void subscribeRightNow(){
    	progressbar.setVisibility(View.GONE);
    	imageview.setImageResource(R.drawable.subscribe);
    	imageview.setVisibility(View.VISIBLE);
    	textview.setText("已订阅");
        InterestDialog.this.dismiss();
    }

    public void dimissRightNow(){
        InterestDialog.this.dismiss();
    }
    //退订
    public void unsubscribe(){
        progressbar.setVisibility(View.GONE);
        imageview.setImageResource(R.drawable.unsubscribe);
        imageview.setVisibility(View.VISIBLE);
        textview.setText("已退订");

        progressbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                InterestDialog.this.dismiss();
            }
        }, 1000);
    }
    //加载中
    public void progress(){
        progressbar.setVisibility(View.VISIBLE);
        imageview.setVisibility(View.GONE);
        textview.setText("操作中");
    }
    //操作失败
    public void subscribefail(){
        progressbar.setVisibility(View.GONE);
        imageview.setImageResource(R.drawable.subscribe_fail);
        imageview.setVisibility(View.VISIBLE);
        textview.setText(R.string.subscribe_dialog_fail);
        progressbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                InterestDialog.this.dismiss();
            }
        }, 1000);
    }

    public void subscribledAlread(){
        progressbar.setVisibility(View.GONE);
        imageview.setImageResource(R.drawable.subscribe_fail);
        imageview.setVisibility(View.VISIBLE);
        textview.setText("此栏目已订阅");
        progressbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                InterestDialog.this.dismiss();
            }
        }, 1000);
    }

    //操作失败
    public void subscribefailRightNow(){
        progressbar.setVisibility(View.GONE);
        imageview.setImageResource(R.drawable.subscribe_fail);
        imageview.setVisibility(View.VISIBLE);
        textview.setText(R.string.subscribe_dialog_fail);
        InterestDialog.this.dismiss();
    }

    //操作失败
    public void cannotExit(){
        progressbar.setVisibility(View.GONE);
        imageview.setImageResource(R.drawable.subscribe_fail);
        imageview.setVisibility(View.VISIBLE);
        textview.setText("圈主不可退出兴趣圈");
        progressbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                InterestDialog.this.dismiss();
            }
        }, 1000);
    }
    
}
