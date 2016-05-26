package com.zhongsou.souyue.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYSharedPreferences;
import com.zhongsou.souyue.utils.VersionUtils;

public class MySendingAlertDialog implements OnClickListener {

    Dialog dialog;
    Button sure;
    private Button look;
    private Button later;
    private Activity activity;
    private SYSharedPreferences sysp = SYSharedPreferences.getInstance();

    public MySendingAlertDialog(Activity activity) {
        this.activity = activity;
        if (sysp == null)
            sysp = SYSharedPreferences.getInstance();

        dialog = new Dialog(activity, R.style.sending_alert_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.sending_alert);
        look = (Button) dialog.findViewById(R.id.sending_alert_look);
//		look.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        later = (Button) dialog.findViewById(R.id.sending_alert_later);
        look.setOnClickListener(this);
        later.setOnClickListener(this);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH
                        || keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sending_alert_later:
                sysp.putInt(SYSharedPreferences.KEY_SENDALERTHIDE, VersionUtils.getVersionCode());
                dismiss();
                activity.finish();
                activity.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);
                break;
            case R.id.sending_alert_look:
                dismiss();
                IntentUtil.startRead_isCircleActivityWithAnim(activity, 1);
                activity.finish();
//                Intent i = new Intent();
//                i.setClass(activity, SelfCreateActivity.class);
//                activity.startActivity(i);
//                activity.finish();
//                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                break;

            default:
                break;
        }
    }

    public void show() {
    	if(dialog != null) {
    		dialog.show();
    	}
    }

    public void hide() {
    	if(dialog != null) {
    		dialog.hide();
    	}
    }

    public void dismiss() {
    	if(dialog != null) {
    		dialog.dismiss();
    	}
    }

}
