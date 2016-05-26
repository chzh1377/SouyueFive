package com.zhongsou.souyue.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.zhongsou.souyue.R;

/**
 * 用户信息弹出框
 * 受到布局限制，不可以随意修改布局文件
 * @author iamzl
 *
 */
public class ImAlertMenu {

    public interface OnAlertClick {
        void onClick(View whichButton);
    }

    private ImAlertMenu() {

    }

    public static Dialog showAlert(final Context context, final OnAlertClick alertDo, OnCancelListener cancelListener) {
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.im_alert_menu, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        int count = layout.getChildCount();
        for (int i = 0; i < count; i ++){
            layout.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDo.onClick(v);
                    dlg.dismiss();
                }
            });
        }
        Window w = dlg.getWindow();
        w.setWindowAnimations(R.style.im_alert_anim);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null) {
            dlg.setOnCancelListener(cancelListener);
        }
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }
}
