package com.zhongsou.souyue.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.util.MyDisplayImageOption;
import com.zhongsou.souyue.im.util.PhotoUtils;
import com.zhongsou.souyue.utils.StringUtils;

/**
 * 
 * @author wangchunyan@zhongsou.com
 *
 */
public class ImShareDialog extends Dialog {

    public ImShareDialog(Context context) {
        super(context);
    }
    public ImShareDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String imgHeader;
        private String content_text;
        private String desc_text;
        private String positiveButtonText;
        private String negativeButtonText;

        private Button ok, cancel;
        private TextView content;
        public EditText desc;
        private ImageView img;
        public interface ImShareDialogInterface {
            void onClick(DialogInterface dialog, View v);
        }
        private ImShareDialogInterface positiveButtonClickListener, negativeButtonClickListener;
        
        public Builder(Context context) {
            this.context = context;
        }
        public Builder setImgHeader(int imgHeader) {
            this.imgHeader = (String) context.getText(imgHeader);
            return this;
        }

        public Builder setImgHeader(String imgHeader) {
            this.imgHeader = imgHeader;
            return this;
        }
        
        public Builder setContent(int content_text) {
            this.content_text = (String) context.getText(content_text);
            return this;
        }

        public Builder setContent(String content_text) {
            this.content_text = content_text;
            return this;
        }
        
        public Builder setDesc(int desc_text) {
            this.desc_text = (String) context.getText(desc_text);
            return this;
        }

        public Builder setDesc(String desc_text) {
            this.desc_text = desc_text;
            return this;
        }
        
        public String getDesc_text() {
			return desc.getText().toString();
		}
        
        /**
         * Set the positive button resource and it"s listener
         * 
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, ImShareDialogInterface listener) {
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
        public Builder setPositiveButton(String positiveButtonText, ImShareDialogInterface listener) {
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
        public Builder setNegativeButton(int negativeButtonText, ImShareDialogInterface listener) {
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
        public Builder setNegativeButton(String negativeButtonText, ImShareDialogInterface listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public ImShareDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ImShareDialog dialog = new ImShareDialog(context, R.style.im_share_dialog);
            View layout = inflater.inflate(R.layout.im_share_dialog, null);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(dip2px(context,20),0,dip2px(context,20),0);
            dialog.setContentView(layout, params);
            setViewToDialog(layout);
            if (StringUtils.isNotEmpty(imgHeader)) {
               // aQuery.id(img).image(imgHeader, true, true, 0, R.drawable.im_dialog_header);
                PhotoUtils.showCard(PhotoUtils.UriType.HTTP,imgHeader,img, MyDisplayImageOption.getOptions(R.drawable.im_dialog_header));
            }else{
                img.setVisibility(View.GONE);
            }
            if (content_text != null) {
                content.setText(content_text.trim());
                content.setVisibility(View.VISIBLE);
            } else {
                content.setVisibility(View.INVISIBLE);
            }
            if (desc_text != null) {
                desc.setText(desc_text);
            } 
            
            if(!StringUtils.isEmpty(imgHeader)){
                img.setVisibility(View.VISIBLE);
            }else{
                img.setVisibility(View.GONE);
            }
            
            ok.setText(positiveButtonText != null ? positiveButtonText : context.getString(R.string.im_dialog_ok));
                ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (positiveButtonClickListener != null)
                            positiveButtonClickListener.onClick(dialog, v);
                        dialog.dismiss();
                    }
                });
            cancel.setText(negativeButtonText != null ? negativeButtonText : context.getString(R.string.im_dialog_cancel));
                cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (negativeButtonClickListener != null)
                            negativeButtonClickListener.onClick(dialog, v);
                        dialog.dismiss();
                    }
                });
            return dialog;
        }

        private void setViewToDialog(View layout) {
            img=(ImageView) layout.findViewById(R.id.im_dialog_header);
            content=(TextView) layout.findViewById(R.id.im_dialog_content);
            desc=(EditText) layout.findViewById(R.id.im_dialog_desc);
            ok = (Button) layout.findViewById(R.id.im_dialog_ok);
            cancel = (Button) layout.findViewById(R.id.im_dialog_cancel);
        }

        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        private int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }

}
