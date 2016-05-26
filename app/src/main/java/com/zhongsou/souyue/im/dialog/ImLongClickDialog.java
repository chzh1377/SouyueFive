package com.zhongsou.souyue.im.dialog;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.tuita.sdk.im.db.module.MessageRecent;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.activity.MainActivity;
import com.zhongsou.souyue.im.ac.IMShareActivity;
import com.zhongsou.souyue.im.search.SearchUtils;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.ui.SouYueToast;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * 
 * @author wangchunyan@zhongsou.com
 * 聊天信息长按 （文本，名片，新闻分享）
 */
public class ImLongClickDialog extends Dialog {

    public ImLongClickDialog(Context context) {
        super(context);
    }
    public ImLongClickDialog(Context context, int theme) {
        super(context, theme);
    }
    @SuppressLint("NewApi")
    public static class Builder {
        private Context context;
//        private TextView chat_friend_name;
        private TextView im_chat_copy;
        private TextView im_chat_forword;
        private TextView im_chat_delete;
        private TextView imToTop; //置顶
        private View im_second_line;
//        private String selfName;
        private boolean isText;
        private boolean flag;//只显示删除按钮
        private boolean isTop;//显示置顶相关的菜单
        
        private ChatMsgEntity forwordcontent;
        private MessageRecent messageRecent;
        private String str;
        private UpdateListInterface updateListInterface;
        private UpdateToTopListInterface updateToTopListInterface;
        private View im_third_line;//删除上面的那条线
        private View imToTopLine; //置顶的线
        public void setForwordList(ChatMsgEntity forwordcontent,UpdateListInterface updateListInterface) {
            this.forwordcontent = forwordcontent;
            this.updateListInterface = updateListInterface;
        }
        public void setMessageRecentList(MessageRecent messageRecent,UpdateToTopListInterface updateToTopListInterface) {
            this.messageRecent = messageRecent;
            this.updateToTopListInterface = updateToTopListInterface;
        }
        public boolean isText() {
            return isText;
        }
        public void setText(boolean isText) {
            this.isText = isText;
        }
        public void setOnlyDelete(boolean flag){
            this.flag = flag;
        }

        public boolean isTop() {
            return isTop;
        }

        public void setTop(boolean isTop) {
            this.isTop = isTop;
        }

        public Builder(Context context) {
            this.context = context;
        }
        public Builder(Context context,String str) {
            this.context = context;
            this.str = str;
        }
//        public Builder setselfName(int selfName) {
//            this.selfName = (String) context.getText(selfName);
//            return this;
//        }
//        public Builder setselfName(String selfName) {
//            this.selfName = selfName;
//            return this;
//        }
        
        /**
         * Create the custom dialog
         */
        public ImLongClickDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ImLongClickDialog dialog = new ImLongClickDialog(context,R.style.im_dialog);
            View layout = inflater.inflate(R.layout.im_chat_longclick_pop_layout, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            setViewToDialog(layout);
            dialog.setCanceledOnTouchOutside(true);
            if (isText) {
                im_chat_copy.setVisibility(View.VISIBLE);
                im_second_line.setVisibility(View.VISIBLE);
            }else if(flag){
                im_chat_copy.setVisibility(View.GONE);
                im_second_line.setVisibility(View.GONE);
                im_chat_forword.setVisibility(View.GONE);
                im_third_line.setVisibility(View.GONE);
                im_chat_delete.setVisibility(View.VISIBLE);
            }else if(isTop){
                im_chat_copy.setVisibility(View.GONE);
                im_second_line.setVisibility(View.GONE);
                im_chat_forword.setVisibility(View.GONE);
                imToTopLine.setVisibility(View.VISIBLE);
                imToTop.setVisibility(View.VISIBLE);
                String by3 =messageRecent.getBy3();
                if(by3!=null && !by3.equals("0")){
                    imToTop.setText("取消置顶");
                }else {
                    imToTop.setText("置顶消息");
                }
            }else{
                im_chat_copy.setVisibility(View.GONE);
                im_second_line.setVisibility(View.GONE);
            }
            im_chat_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(forwordcontent!=null){
                       if(forwordcontent.getType() == MessageHistory.CONTENT_TYPE_AT_FRIEND) {
                           copy(str, context);
                       }else {
                           copy(forwordcontent.getText(), context);
                       }
                        SouYueToast.makeText(context, R.string.im_copy, SouYueToast.LENGTH_SHORT).show();
                   }
                   dialog.dismiss();
                }
            });
            im_chat_forword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(forwordcontent!=null){
//                        ContactsListActivity.startForwardAct((Activity) context, forwordcontent);
                        IMShareActivity.invoke((Activity) context, forwordcontent);
                    }
                    dialog.dismiss();
                }
            });

            imToTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(messageRecent!=null){
                        String by3 =messageRecent.getBy3();
                        if(by3!=null && !by3.equals("0")){
                            ImserviceHelp.getInstance().db_ToTopMessageRecent(messageRecent.getChat_id(),"0");
                        }else {
                            ImserviceHelp.getInstance().db_ToTopMessageRecent(messageRecent.getChat_id(),Long.toString(new Date().getTime()));
                        }
                        updateToTopListInterface.updateToTopList(messageRecent);
                    }
                    dialog.dismiss();
                }
            });

            im_chat_delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(isTop){
                        //消息页面的置顶删除
                        ImserviceHelp.getInstance().db_delMessageRecent(messageRecent.getChat_id());
                        ImserviceHelp.getInstance().db_clearMessageRecentBubble(messageRecent.getChat_id());
                        SearchUtils.deleteSession(MainActivity.SEARCH_PATH_MEMORY_DIR,messageRecent.getMyid(), (short)messageRecent.getChat_type(), messageRecent.getChat_id());
                        updateToTopListInterface.updateToTopList(messageRecent);
                    }else {
                        //此处做删除本地聊天记录操作。并且更新消息列表
                        updateListInterface.updateChatList(forwordcontent);
                        try {
                            SearchUtils.delMessage(MainActivity.SEARCH_PATH_MEMORY_DIR,forwordcontent.userId,
                                    (short)forwordcontent.getChatType(),
                                    forwordcontent.chatId,
                                    new Long(forwordcontent.getId()).intValue(),
                                    forwordcontent.getText());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    dialog.dismiss();
                }
            });

            return dialog;
        }

        private void setViewToDialog(View layout) {
            im_chat_copy=(TextView) layout.findViewById(R.id.im_chat_copy);
            im_chat_forword=(TextView) layout.findViewById(R.id.im_chat_forword);
            imToTop = (TextView) layout.findViewById(R.id.im_to_top);
            im_chat_delete = (TextView) layout.findViewById(R.id.im_chat_delete);
            im_second_line=(View) layout.findViewById(R.id.im_second_line);
            im_third_line = (View) layout.findViewById(R.id.im_third_line);
            imToTopLine = (View) layout.findViewById(R.id.im_to_top_line);
        }
        private void copy(String content, Context context){
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) context

            .getSystemService(Context.CLIPBOARD_SERVICE);

            cmb.setText(content.trim());

       }
    }

    public interface UpdateListInterface{
        public void updateChatList(ChatMsgEntity forwordcontent);
    }

    public interface UpdateToTopListInterface{
        public void updateToTopList(MessageRecent messageRecent);
    }

}
