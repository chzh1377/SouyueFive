package com.zhongsou.souyue.im.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.render.MsgUtils;
import com.zhongsou.souyue.im.util.ImHtmlTagHandler;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.utils.StringUtils;
/**
 * 
 * @author wangchunyan@zhongsou.com
 * 点击聊天信息双击
 */

public class ChatTextPopDialog extends Dialog{
    public ChatTextPopDialog(Context context) {
        super(context);
        setOwnerActivity((Activity)context);
    }

    public ChatTextPopDialog(Context context, int theme) {
        super(context, theme);
        setOwnerActivity((Activity)context);
    }
    
    public static class Builder {
        private Context context;
        private ChatMsgEntity mChatMsgEntity;
        private TextView chat_pop_text;

        public Builder(Context context) {
            this.context = context;
        }
//        public Builder setContent(int content_text) {
//            this.content_text = (String) context.getText(content_text);
//            return this;
//        }
        public Builder setContent(ChatMsgEntity chatMsgEntity) {
            this.mChatMsgEntity = chatMsgEntity;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public ChatTextPopDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ChatTextPopDialog dialog = new ChatTextPopDialog(context, android.R.style.Animation_Dialog);
            View layout = inflater.inflate(R.layout.im_chat_text_pop_layout, null);
            LinearLayout im_chat_text_pop=(LinearLayout) layout.findViewById(R.id.im_chat_text_pop);
            im_chat_text_pop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            setViewToDialog(layout);
            if (!StringUtils.isEmpty(mChatMsgEntity.getText())) {
                chat_pop_text.setText(MsgUtils.showText(context, mChatMsgEntity));
                chat_pop_text.setMovementMethod(ScrollingMovementMethod.getInstance()); 
            }
            chat_pop_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View paramView) {
                    dialog.dismiss();
                }
            });
            return dialog;
        }

        private void setViewToDialog(View layout) {
            chat_pop_text=(TextView) layout.findViewById(R.id.chat_pop_text);
            
        }

        Html.ImageGetter imageGetter = new Html.ImageGetter()
        {
            @Override
            public Drawable getDrawable(String source)
            {
                int id = Integer.parseInt(source);
                Drawable d = context.getResources().getDrawable(id);
                d.setBounds(0, 0, d.getIntrinsicWidth() * 2 / 3, d.getIntrinsicWidth() * 2 / 3);
                return d;
            }
        };

    }

    public void showBottonDialog() {
        Window window = this.getWindow();
        window.setWindowAnimations(R.style.chat_pop_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        window.setBackgroundDrawableResource(R.color.white);
        this.onWindowAttributesChanged(wl);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.show();
    }
}
