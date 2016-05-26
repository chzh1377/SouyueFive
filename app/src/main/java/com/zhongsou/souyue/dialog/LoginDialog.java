package com.zhongsou.souyue.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import com.zhongsou.souyue.R;

/**
 * 使用方式同原生dialog
 * 
 * @author iamzl
 */
public class LoginDialog extends Dialog {
    public LoginDialog(Context context) {
        super(context);
    }

    public LoginDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String msg;

        private String positiveButtonText, negativeButtonText;

        private Button ok, cancel;
        private TextView tmsg;

        public interface LoginDialogInterface {
            void onClick(DialogInterface dialog, View v);
        }

        private LoginDialogInterface positiveButtonClickListener, negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message for resource
         * 
         * @param msg
         * @return
         */
        public Builder setMessage(int msg) {
            this.msg = (String) context.getText(msg);
            return this;
        }

        /**
         * Set the Dialog message for string
         * 
         * @param msg
         * @return
         */
        public Builder setMessage(String msg) {
            this.msg = msg;
            return this;
        }


        /**
         * Set the positive button resource and it"s listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, LoginDialogInterface listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it"s listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, LoginDialogInterface listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it"s listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText, LoginDialogInterface listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it"s listener
         * 
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText, LoginDialogInterface listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public LoginDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LoginDialog dialog = new LoginDialog(context, R.style.im_dialog);
            View layout = inflater.inflate(R.layout.login_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            setViewToDialog(layout);
            if (msg != null) {
                tmsg.setText(msg);
                tmsg.setVisibility(View.VISIBLE);
            } else {
                tmsg.setVisibility(View.INVISIBLE);
            }
            ok.setText(positiveButtonText != null ? positiveButtonText : context.getString(R.string.login_dialog_goreg));
            ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (positiveButtonClickListener != null) positiveButtonClickListener.onClick(dialog, v);
                    dialog.dismiss();
                }
            });
            cancel.setText(negativeButtonText != null ? negativeButtonText : context.getString(R.string.login_dialog_notip));
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (negativeButtonClickListener != null) negativeButtonClickListener.onClick(dialog, v);
                    dialog.dismiss();
                }
            });
            return dialog;
        }

        private void setViewToDialog(View layout) {
            ok = (Button) layout.findViewById(R.id.login_dialog_ok);
            cancel = (Button) layout.findViewById(R.id.login_dialog_cancel);
            tmsg = (TextView) layout.findViewById(R.id.login_dialog_msg);
        }
    }
}
