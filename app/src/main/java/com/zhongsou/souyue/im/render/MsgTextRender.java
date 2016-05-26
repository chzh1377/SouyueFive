package com.zhongsou.souyue.im.render;

/**
 * Created by zhangwenbin on 15/3/24.
 */

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.tuita.sdk.im.db.module.MessageHistory;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.dialog.ChatTextPopDialog;
import com.zhongsou.souyue.module.ChatMsgEntity;

import java.util.Timer;
import java.util.TimerTask;

public class MsgTextRender extends MsgItemRender {

    /**
     * 显示的文本
     */
    private TextView tvText;

    private int mCount;

    //抽取双击显示文字详情 下面统计时间的做法是不对的 造成两个bug
    private int mFirClick;//第一次点击
    private int mSecClick;//第二次点击

    public MsgTextRender(Context context,
                         BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);


    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        // TODO custom event
        mViewHolder.obtainView(mContentView,R.id.tv_chatcontent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    mCount++;
                    if (mCount == 1) {
                        mFirClick = (int) System.currentTimeMillis();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mCount = 0;
                            }
                        },500);
                    } else if (mCount == 2) {
                        mSecClick = (int) System.currentTimeMillis();
                        if (mSecClick - mFirClick < 500) {// 双击事件
                            // 双击事件
                            ChatTextPopDialog.Builder chatbuilder = new ChatTextPopDialog.Builder(
                                    mContext);
                            chatbuilder.setContent(mChatMsgEntity);
                            chatbuilder.create().show();
                            mCount = 0;
                            mFirClick = 0;
                            mSecClick = 0;
                        }
                    }
                }else{
                    if(cbCheck.isChecked()){
                        cbCheck.setChecked(false);
                        mChatMsgEntity.setEdit(false);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                    }else{
                        mChatMsgEntity.setEdit(true);
                        cbCheck.setChecked(true);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                    }
                }
            }
        });

        mViewHolder.obtainView(mContentView,R.id.tv_chatcontent).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(true,false);
                }
                return true;
            }
        });

    }

    @Override
    public void fitDatas(int position) {
        super.fitDatas(position);
        // TODO custom fit data

        //将控件放入缓存
        tvText = mViewHolder.obtainView(mContentView,
                R.id.tv_chatcontent);

        // 按照不同消息类型显示文本
        switch (mChatMsgEntity.getType()) {
            case IMessageConst.CONTENT_TYPE_TEXT: // 文本 0
                tvText.setText(MsgUtils.showText(mContext, mChatMsgEntity));
                if(!mChatAdapter.getIsEdit()) {
                    tvText.setMovementMethod(LinkMovementMethod.getInstance());
                }
//                }else{
//                    tvText.setMovementMethod(null);
//                }
                break;

//            case IMessageConst.CONTENT_TYPE_SENDCOIN: // 赠中搜币 11
//                String sendCoinContent = "";
//                try {
//                    JSONObject jsonObject = new JSONObject(mChatMsgEntity.getText()
//                            .toString());
//                    sendCoinContent = jsonObject.getString("text");
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//                tvText.setText(sendCoinContent);
//                break;
            case IMessageConst.CONTENT_TYPE_AT_FRIEND: // @好友 21
                String showText = MsgUtils.changeContentToName(mContext, mChatMsgEntity);
                mChatMsgEntity.setType(MessageHistory.CONTENT_TYPE_TEXT);
                mChatMsgEntity.setText(showText);
                tvText.setText(MsgUtils.showText(mContext, mChatMsgEntity));
                break;
            default:
                break;

        }

    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_text_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_text_right_view;
    }

}