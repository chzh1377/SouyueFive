package com.zhongsou.souyue.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import com.zhongsou.souyue.R;

/**
 * @author iamzl
 */
public class ImContactDialog extends Dialog {
    public ImContactDialog(Context context) {
        super(context);
    }

    public ImContactDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private Button ok;

        public interface ImContactDialogInterface {
            void onClick(DialogInterface dialog, View v);
        }

        private ImContactDialogInterface positiveButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }


        /**
         * Set the positive button text and it"s listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(ImContactDialogInterface listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public ImContactDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ImContactDialog dialog = new ImContactDialog(context, R.style.im_dialog);
            View layout = inflater.inflate(R.layout.im_contact_dialog, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            setViewToDialog(layout);

            ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                		dialog.dismiss();
                    if (positiveButtonClickListener != null) positiveButtonClickListener.onClick(dialog, v);
                }
            });
            return dialog;
        }

        private void setViewToDialog(View layout) {
            ok = (Button) layout.findViewById(R.id.im_contact_dialog_ok);
        }
    }
}
