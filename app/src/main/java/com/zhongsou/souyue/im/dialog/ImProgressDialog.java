package com.zhongsou.souyue.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.zhongsou.souyue.R;

/**
 * 加载中dialog 使用方式同原生dialog
 * 
 * @author iamzl
 */
public class ImProgressDialog extends Dialog {
    public ImProgressDialog(Context context) {
        super(context);
    }

    public ImProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Create the custom dialog
         */
        public ImProgressDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ImProgressDialog dialog = new ImProgressDialog(context, R.style.im_progress_dialog);
            View layout = inflater.inflate(R.layout.im_progress_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }
    }
}
