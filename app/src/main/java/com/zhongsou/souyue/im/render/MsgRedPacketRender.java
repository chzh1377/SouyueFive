package com.zhongsou.souyue.im.render;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.tuita.sdk.im.db.module.IConst;
import com.tuita.sdk.im.db.module.IMessageConst;
import com.zhongsou.souyue.R;
import com.zhongsou.souyue.im.dialog.IMRedPacketDialog;
import com.zhongsou.souyue.im.module.MsgContent;
import com.zhongsou.souyue.im.services.ImserviceHelp;
import com.zhongsou.souyue.module.ChatMsgEntity;
import com.zhongsou.souyue.module.User;
import com.zhongsou.souyue.net.UrlConfig;
import com.zhongsou.souyue.utils.IntentUtil;
import com.zhongsou.souyue.utils.SYUserManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhangwenbin zhangwb@zhongsou.com
 * @version version_code (e.g, V5.0.1)
 * @Copyright (c) 2016 zhongsou
 * @Description class 红包render
 * @date 16/1/6
 */
public class MsgRedPacketRender extends MsgItemRender {
    /**
     * 显示的文本祝福语
     */
    private TextView tvRedPacketContent;

    /**
     * 缓存解析的MsgContent
     */
    private SparseArray<MsgContent> mMsgContent = new SparseArray<MsgContent>();

    public static ChatMsgEntity mOpenRedPacket = null;


    public MsgRedPacketRender(Context context,
                              BaseTypeAdapter<ChatMsgEntity> adapter, int itemType) {
        super(context, adapter, itemType);

    }

    @Override
    public void fitEvents() {
        super.fitEvents();
        // TODO custom event
        mViewHolder.obtainView(mContentView, R.id.ll_red_packet_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChatAdapter.getIsEdit()) {

                    if (mChatMsgEntity.status == IMessageConst.STATE_RED_PACKET_READ) {     //如果红包打开过
                        MsgUtils.msgJump(mContext,obtainMsgContent(mChatMsgEntity));
                    } else {    //红包没有打开过
                        if (mChatMsgEntity.getChatType() == IConst.CHAT_TYPE_PRIVATE && !mChatMsgEntity.isComMsg()){
                            MsgUtils.msgJump(mContext,obtainMsgContent(mChatMsgEntity));
                        }else {
                            mOpenRedPacket = mChatMsgEntity;
                            String showName = "";
                            if (mChatMsgEntity.isComMsg()){
                                if (mChatMsgEntity.getChatType() == IConst.CHAT_TYPE_GROUP){
                                    showName = mChatMsgEntity.getNickname();
                                }else {
                                    showName = mMsgMananger.getFriendName();
                                }
                            }else{
                                showName = SYUserManager.getInstance().getUser().name();
                            }
                            IMRedPacketDialog.showDialog(mContext,
                                    obtainMsgContent(mChatMsgEntity).getText(),
                                    mChatMsgEntity.getIconUrl(),
                                    MsgUtils.createOpenRedUrl(obtainMsgContent(mChatMsgEntity).getUrl()),
                                    showName);
                        }
                    }

                } else {
                    if (cbCheck.isChecked()) {
                        cbCheck.setChecked(false);
                        mChatMsgEntity.setEdit(false);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox);
                    } else {
                        mChatMsgEntity.setEdit(true);
                        cbCheck.setChecked(true);
                        cbCheck.setBackgroundResource(R.drawable.im_chat_checkbox_selected);
                    }
                }
            }
        });

        mViewHolder.obtainView(mContentView, R.id.ll_red_packet_item).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mChatAdapter.getIsEdit()) {
                    showLCDialog(false, true);
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
        tvRedPacketContent = mViewHolder.obtainView(mContentView,
                R.id.tv_red_packet_content);
        tvRedPacketContent.setText(obtainMsgContent(mChatMsgEntity) != null ? obtainMsgContent(mChatMsgEntity).getText() : "");

    }

    @Override
    protected int getLeftLayoutId() {
        return R.layout.msg_red_packet_left_view;
    }

    @Override
    protected int getRightLayoutId() {
        return R.layout.msg_red_packet_right_view;
    }

    /**
     * 获取消息对象
     *
     * @param msgEntity
     * @return
     */
    private MsgContent obtainMsgContent(ChatMsgEntity msgEntity) {
        MsgContent msgContent = mMsgContent.get((int) msgEntity.getId());

        if (null == msgContent) {
            msgContent = parseJson(msgEntity);
            mMsgContent.put((int) msgEntity.getId(), msgContent);
        }

        return msgContent;
    }

    /**
     * 解析json得到MsgContent
     *
     * @param msgEntity
     * @return
     */
    private MsgContent parseJson(ChatMsgEntity msgEntity) {
        try {
            MsgContent msgContent = new Gson().fromJson(msgEntity.getText().toString(), MsgContent.class);
            return msgContent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 改变红包状态
     * @param chatMsgEntity
     */
    public static void changeRedPacketStutas(ChatMsgEntity chatMsgEntity){
        ImserviceHelp.getInstance().updateStatus(chatMsgEntity.getRetry(), chatMsgEntity.getType(), chatMsgEntity.chatId, IMessageConst.STATE_RED_PACKET_READ);
    }

}
