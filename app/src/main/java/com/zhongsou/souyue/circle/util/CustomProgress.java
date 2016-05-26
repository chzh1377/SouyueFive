package com.zhongsou.souyue.circle.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * Created by wangqiang on 15/7/17.
 */
public class CustomProgress extends Dialog {

    public CustomProgress(Context context) {
        super(context);
    }

    public CustomProgress(Context context, int theme) {
        super(context, theme);
    }


    /**
     * 当窗口焦点改变时调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
//        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
//        // 获取ImageView上的动画背景
//        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
//        // 开始动画
//        spinner.start();
    }

    /**
     * 给Dialog设置提示信息
     *
     * @param message
     */
    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    /**
     * 弹出自定义ProgressDialog
     *
     * @param context        上下文
     * @param message        提示
     * @param cancelable     是否按返回键取消
     * @param cancelListener 按下返回键监听
     * @return
     */
    public CustomProgress(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        super(context, R.style.Custom_Progress);
        setTitle("");
        setContentView(R.layout.layout_loading_dialog);
        // 按返回键是否取消
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
        setCanceledOnTouchOutside(false);
        getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.2f;
        getWindow().setAttributes(lp);

        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        // 获取ImageView上的动画背景
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        // 开始动画
        spinner.start();
    }


}