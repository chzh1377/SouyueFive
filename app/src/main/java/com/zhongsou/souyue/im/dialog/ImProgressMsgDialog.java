package com.zhongsou.souyue.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * 加载中dialog 使用方式同原生dialog
 * 
 * @author iamzl
 */
public class ImProgressMsgDialog extends Dialog {
    public ImProgressMsgDialog(Context context) {
        super(context);
    }

    public ImProgressMsgDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String msg;
        private TextView tmsg;

        public Builder(Context context) {
            this.context = context;
        }
        
        public Builder setTextContent(String msg){
            this.msg = msg;
            return this;
        }
        
        public Builder setTextContent(int id){
            this.msg = (String) context.getText(id);
            return this;
        }


        /**
         * Create the custom dialog
         */
        public ImProgressMsgDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ImProgressMsgDialog dialog = new ImProgressMsgDialog(context, R.style.im_progress_dialog);
            View layout = inflater.inflate(R.layout.im_progress_msg_dialog, null);
            tmsg = (TextView) layout.findViewById(R.id.im_progress_dialog_msg);
            if (msg != null && tmsg != null) {
                tmsg.setText(msg);
            } else {
                tmsg.setText(context.getText(R.string.message_progress_def));
            }
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }
    }
}
